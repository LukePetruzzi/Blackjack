package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class Blackjack {

    private final int STARTING_MONEY = 1000;
    private final int NUM_DECKS = 6;
    private final double SHUFFLE_RATIO = 0.25;

    private Scanner scanner;
    private List<Player> players;
    private List<Card> decks;
    private Dealer dealer;

    public Blackjack() {

    }

    public Blackjack(String[] playerNames, Scanner scanner) {

        this.scanner = scanner;

        // create a dealer
        this.dealer = new Dealer();

        // make the players
        players = new ArrayList<Player>();
        for (int i = 0; i < playerNames.length; i++) {
            Player player = new Player(playerNames[i], STARTING_MONEY);
            players.add(player);
        }

        // create a shuffled collection of cards
        this.decks = createNewDecks();
    }

    // main controller for a round of blackjack, from deal to payout
    public void playRound() {
        List<Player> players = getPlayers();

        // get each player to place a bet or pass on the round
        boolean somebodyBet = this.placeBets();

        // don't play the round if nobody bet anything
        if (!somebodyBet) {
            System.out.println("\nI'll be here if anyone wants to play...");
            return;
        }

        // the dealer deals
        this.deal();

        // each player plays their turn
        for (int i = 0; i < players.size(); i++) {
            this.playTurn(players.get(i));
        }

        // dealer plays his hand
        this.dealerPlays();

        // payout the players
        this.payout();

        // clear the players' hands and bets
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHands(new ArrayList<Hand>());
            players.get(i).setCurrentBets(new ArrayList<Integer>());
        }
    }

    public void getResults() {
        long TIME_TO_WAIT = 1000L;
        System.out.format(
                "\n\n------------------------------------\n|                                  |\n|              RESULTS             |\n|                                  |\n------------------------------------\n\n");
        for (int i = 0; i < this.players.size(); i++) {
            System.out.format("%s:\n", players.get(i).getName());
            System.out.format("\tMoney at start: $%d\n", STARTING_MONEY);
            System.out.format("\tMoney in pocket: $%d\n", players.get(i).getMoney());
            int gainloss = players.get(i).getMoney() - STARTING_MONEY;
            if (gainloss < 0) {
                if (STARTING_MONEY - Math.abs(gainloss) == 0) {
                    System.out.format("\t-$%d on the day   (You Suck!)\n", Math.abs(gainloss));

                } else {
                    System.out.format("\t-$%d on the day\n", Math.abs(gainloss));
                }
            } else if (gainloss > 0) {
                System.out.format("\t+$%d on the day\n", gainloss);
            } else {
                System.out.format("\tYou broke even\n");
            }
            this.waitForMillis(TIME_TO_WAIT);
        }
        this.waitForMillis(TIME_TO_WAIT);
    }

    public boolean allPlayersAreBroke() {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getMoney() != 0) {
                return false;
            }
        }
        return true;
    }

    public void resetEligiblePlayers() {
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getMoney() > 0) {
                this.players.get(i).setIsPlaying(true);
            }
        }
    }

    // ********************* GETTERS AND SETTERS *******************************
    public List<Player> getPlayers() {
        List<Player> validPlayers = new ArrayList<Player>();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.isPlaying()) {
                validPlayers.add(player);
            }
        }
        return validPlayers;
    }

    public Scanner getScanner() {
        return this.scanner;
    }
    // ********************* END GETTERS AND SETTERS ***************************

    // ********************* PRIVATE HELPER METHODS ****************************

    // create a new decks
    private List<Card> createNewDecks() {
        List<Card> newDecks = new ArrayList<Card>();
        // make the decks
        for (int i = 0; i < NUM_DECKS; i++) {

            // use ranks + 1
            for (int suit = 0; suit < 4; suit++) {
                // use suit + 1
                for (int rank = 0; rank < 13; rank++) {
                    newDecks.add(new Card(rank + 1, suit + 1));
                }
            }
        }
        // shuffle the new decks
        Collections.shuffle(newDecks);
        return newDecks;
    }

    private boolean placeBets() {
        boolean somebodyBet = false;
        List<Player> players = getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.format("\n\n%s, what is your bet?\n", player.getName());
            System.out.format("MONEY REMAINING: $%d\n$", player.getMoney());

            String betStr = this.sanitizeInt(this.scanner.next());
            while (betStr.equals("")) {
                System.out.println("\nYou can't wager with that!");
                System.out.format("So what is your bet, in moneys?\n$", player.getName());
                betStr = this.sanitizeInt(this.scanner.next());
            }
            int bet = Integer.parseInt(betStr);

            if (bet <= 0) {
                // player doesn't play
                System.out.println("Have fun on the sideline, LOSER!");
                player.setIsPlaying(false);
            } else {
                player.setIsPlaying(true);
                // check if player has enough money to make the bet
                if (bet > player.getMoney()) {
                    bet = player.getMoney();
                    System.out.format("You don't have enough money to bet that much, so you're going all-in with $%d\n",
                            bet);
                }

                player.addCurrentBet(bet);
                player.setMoney(player.getMoney() - bet);
                somebodyBet = true;
            }
        }
        return somebodyBet;
    }

    private void deal() {
        List<Player> players = getPlayers();

        System.out.format(
                "\n\n-------------------------------------\n|                                   |\n|            DEALER DEALS           |\n|                                   |\n-------------------------------------\n\n");
        // create new decks if the current one is below 1/4 number of cards
        if (this.decks.size() <= ((double) (NUM_DECKS * 52)) * SHUFFLE_RATIO) {
            this.decks = createNewDecks();
        }

        // deal an initial hand for the dealer and for each of the players
        for (int i = 0; i < players.size() + 1; i++) {
            Hand hand = new Hand();
            hand.addCard(this.decks.remove(this.decks.size() - 1));
            hand.addCard(this.decks.remove(this.decks.size() - 1));

            // add dealer's hand
            if (i == players.size()) {
                this.dealer.setHand(hand);
                continue;
            }

            Player player = players.get(i);
            player.addHand(hand);
        }
    }

    private void playTurn(Player player) {
        if (!player.isPlaying()) return;
        System.out.format("\n--------------- %s's Turn ---------------\n", player.getName());

        Hand dealerHand = dealer.getHand();
        Card dUpCard = dealerHand.getCards().get(1);
        // player only has one hand, so play it
        int currentHandIndex = 0;
        boolean lastMoveStand = false;

        while (true) {
            // the player has played their hands, the turn is over
            if (currentHandIndex == player.getHands().size()) {
                break;
            }
            // the player has split their cards, and is on another hand. Deal them a card
            if (currentHandIndex != 0) {
                this.hit(player, currentHandIndex);
            }
            lastMoveStand = false;

            System.out.format("\nDealer is showing: 🂠 %s%s%s\n\n", Card.suitToString(dUpCard.getSuit()), 
                    Card.rankToString(dUpCard.getRank()), Card.suitToString(dUpCard.getSuit()));

            Hand currentHand = player.getHands().get(currentHandIndex);

            // build the string to print, showing all the players hands
            StringBuilder handsString = new StringBuilder();
            for (int i = 0; i < player.getHands().size(); i++) {
                if (player.getHands().size() == 1) {
                    handsString.append(String.format("%s's hand: ", player.getName()));
                    if (currentHand.isBlackjack()) {
                        handsString.append(String.format("($%d) BLACKJACK %s BLACKJACK",
                                player.getCurrentBet(currentHandIndex), currentHand.toString()));
                    } else {
                        handsString.append(String.format("($%d) %s", player.getCurrentBet(currentHandIndex),
                                currentHand.toString()));
                    }
                } else {
                    Hand hand = player.getHands().get(i);
                    if (i == 0) {
                        handsString.append(String.format("%s's hands: ", player.getName()));
                    } else {
                        handsString.append(", ");
                    }

                    if (i == currentHandIndex) {
                        handsString.append(String.format("($%d) [ %s ]", player.getCurrentBet(i), hand.toString()));
                    } else {
                        handsString.append(String.format("($%d) %s", player.getCurrentBet(i), hand.toString()));
                    }
                }
            }
            handsString.append(String.format("\nMoney: $%d", player.getMoney()));
            handsString.append("\n\n");
            System.out.print(handsString.toString());

            String move = this.getValidMove(player, currentHandIndex);

            if (move.equals("s")) {
                // advance the hand index
                currentHandIndex += 1;
                lastMoveStand = true;
            } else if (move.equals("h")) {
                currentHand = this.hit(player, currentHandIndex);
                // check for a bust
                if (currentHand.getValue() > 21) {
                    System.out.format("\n($%d) !BUST! %s !BUST!\n\n", player.getCurrentBet(currentHandIndex),
                            currentHand.toString());
                    currentHandIndex += 1;
                }
            } else if (move.equals("d")) {
                currentHand = this.doubleDown(player, currentHandIndex);
                // check for a bust
                if (currentHand.getValue() > 21) {
                    System.out.format("\n($%d) !BUST! %s !BUST!\n\n", player.getCurrentBet(currentHandIndex),
                            currentHand.toString());
                } else {
                    System.out.format("\nResult: ($%d) %s\n\n", player.getCurrentBet(currentHandIndex),
                            currentHand.toString());
                }
                // double down only gets one card
                currentHandIndex += 1;
            } else if (move.equals("l")) {
                this.split(player, currentHandIndex);
            }
        }
        if (!lastMoveStand) {
            System.out.println("Press [ENTER] to continue");
            this.scanner.nextLine();
            this.scanner.nextLine();
        }
    }

    // checks if the hand can be split into two hands
    private boolean canSplit(Hand hand) {
        if (hand.getCards().size() > 2) {
            return false;
        }
        return hand.getCards().get(0).getRank() == hand.getCards().get(1).getRank();
    }

    private String getValidMove(Player player, int currentHandIndex) {
        Hand hand = player.getHands().get(currentHandIndex);
        System.out.format("%s, what would you like to do?\n", player.getName());

        // build possible moves
        StringBuilder possMoves = new StringBuilder();
        possMoves.append("Stand [S]");
        // check if the player has blackjack
        if (!hand.isBlackjack() && hand.getValue() != 21) {
            possMoves.append(", Hit [H]");

            // check if the player has the money to double down or split
            if (player.getCurrentBet(currentHandIndex) <= player.getMoney()) {
                possMoves.append(", Double Down [D]");

                // check if the hand can be split
                if (this.canSplit(hand)) {
                    possMoves.append(", Split [L]");
                }
            }
        }
        String moves = possMoves.toString();
        System.out.format("%s\n\n", possMoves);

        while (true) {
            String input = this.scanner.next().toLowerCase();

            if ((input.equals("s") && moves.contains("[S]")) || (input.equals("h") && moves.contains("[H]"))
                    || (input.equals("d") && moves.contains("[D]")) || (input.equals("l") && moves.contains("[L]"))) {
                return input;
            }

            System.out.println("Don't make get someone to escort you out, enter a valid move.");
        }
    }

    private Hand hit(Player player, int currentHandIndex) {
        ArrayList<Hand> hands = player.getHands();
        Hand hand = hands.get(currentHandIndex);
        hand.addCard(this.decks.remove(this.decks.size() - 1));
        hands.set(currentHandIndex, hand);
        player.setHands(hands);
        return hand;
    }

    private Hand doubleDown(Player player, int currentHandIndex) {
        int currentBet = player.getCurrentBet(currentHandIndex);
        player.setMoney(player.getMoney() - currentBet);
        player.setCurrentBet(currentBet * 2, currentHandIndex);
        return this.hit(player, currentHandIndex);
    }

    private void split(Player player, int currentHandIndex) {
        ArrayList<Hand> hands = player.getHands();
        Hand hand = hands.get(currentHandIndex);
        Hand newHand1 = new Hand();
        newHand1.addCard(hand.getCards().get(0));
        Hand newHand2 = new Hand();
        newHand2.addCard(hand.getCards().get(1));
        hands.set(currentHandIndex, newHand1);
        hands.add(currentHandIndex + 1, newHand2);

        // add an equal bet for the new hand
        player.addCurrentBet(player.getCurrentBet(currentHandIndex));

        // deal the left hand a card
        this.hit(player, currentHandIndex);
    }

    private void dealerPlays() {
        long TIME_TO_WAIT = 1300L;
        System.out.format(
                "\n\n-------------------------------------\n|                                   |\n|            DEALER PLAYS           |\n|                                   |\n-------------------------------------\n\n");

        Hand dealerHand = this.dealer.getHand();
        Card dUpCard = dealerHand.getCards().get(1);

        System.out.format("\n🂠 %s%s%s\n\n", Card.suitToString(dUpCard.getSuit()),
                Card.rankToString(dUpCard.getRank()), Card.suitToString(dUpCard.getSuit()));
        this.waitForMillis(TIME_TO_WAIT);
        // keep dealing until 17 or bust
        while (true) {

            if (dealerHand.getValue() >= 17 && dealerHand.getValue() <= 21) {

                System.out.format("%s\n\n", dealerHand.toString());
                this.waitForMillis(TIME_TO_WAIT);
                System.out.format("Dealer stands at %d.\n\n", dealerHand.getValue());
                break;
            } else if (dealerHand.getValue() > 21) {
                System.out.format("!BUST! %s !BUST!\n\n", dealerHand.toString());
                break;
            } else {
                System.out.format("%s\n\n", dealerHand.toString());
            }

            dealerHand.addCard(this.decks.remove(this.decks.size() - 1));
            this.dealer.setHand(dealerHand);

            this.waitForMillis(TIME_TO_WAIT);
        }
        System.out.println("Press [ENTER] to continue to payout");
        this.scanner.nextLine();
        this.scanner.nextLine();
    }

    private void waitForMillis(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Interrupted: " + e);
        }
    }

    private void payout() {
        List<Player> players = getPlayers();
        long TIME_TO_WAIT = 1000L;
        System.out.format(
                "\n\n-------------------------------------\n|                                   |\n|               PAYOUT              |\n|                                   |\n-------------------------------------\n\n");
        Hand dealerHand = this.dealer.getHand();
        int dealerVal = dealerHand.getValue();
        if (dealerVal > 21) {
            dealerVal = -1;
            System.out.format("Dealer's hand: !BUST! %s !BUST!\n\n", dealerHand.toString());
        } else {
            System.out.format("Dealer's hand: %s\n\n", dealerHand.toString());
        }
        this.waitForMillis(TIME_TO_WAIT);

        // pay the players or show the outcome
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            int currentMoney = player.getMoney();
            int moneyBefore = currentMoney;
            int betSum = 0;
            for (int j = 0; j < player.getCurrentBets().size(); j++) {
                moneyBefore += player.getCurrentBet(j);
                betSum += player.getCurrentBet(j);
            }

            System.out.format("%s:\n", player.getName());
            System.out.format("\tMoney before round: $%d\n", moneyBefore);
            if (player.getHands().size() > 1) {
                System.out.format("\tMoney on hands: $%d\n", betSum);
            } else {
                System.out.format("\tMoney on hand: $%d\n", betSum);
            }

            // payout each of the player's hands
            for (int j = 0; j < player.getHands().size(); j++) {
                Hand hand = player.getHands().get(j);
                int playerVal = hand.getValue();
                int currentBet = player.getCurrentBet(j);

                System.out.format("\t\t");
                if (playerVal <= 21) {
                    // blackjack pays 3:2
                    if (hand.isBlackjack() && player.getHands().size() == 1) {
                        if (playerVal == dealerVal) {
                            // blackjack push
                            currentMoney += currentBet;
                            System.out.format("BLACKJACK PUSH: %s  +$%d\n", hand.toString(), currentBet);
                        } else {
                            // blackjack!
                            currentMoney += (int) Math.ceil(currentBet + (currentBet * 1.5));
                            System.out.format("BLACKJACK %s BLACKJACK  +$%d\n", hand.toString(),
                                    (int) Math.ceil(currentBet + currentBet * 1.5));
                        }
                    } else if (playerVal > dealerVal) {
                        // player win
                        currentMoney += currentBet * 2;
                        System.out.format("WIN: %s  +$%d\n", hand.toString(), currentBet * 2);
                    } else if (playerVal == dealerVal) {
                        // push
                        currentMoney += currentBet;
                        System.out.format("PUSH: %s  +$%d\n", hand.toString(), currentBet);
                    } else if (playerVal < dealerVal) {
                        // loss
                        System.out.format("LOSS: %s  -$%d\n", hand.toString(), currentBet);
                    }
                } else {
                    // player bust
                    System.out.format("BUST: %s  -$%d\n", hand.toString(), currentBet);
                }

            }
            player.setMoney(currentMoney);
            System.out.format("\tMoney after round: $%d\n", currentMoney);
            this.waitForMillis(TIME_TO_WAIT);
        }
    }

    // ********************* END PRIVATE HELPER METHODS ************************

    public String sanitizeInt(String input) {
        input = input.replaceAll("[^0123456789]", "");
        if (input.length() > 9) {
            input = input.substring(0, 8);
        }
        return input;
    }

    // make a game
    public static void main(String[] args) {
        new Blackjack().run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        int STARTING_MONEY = 1000;

        System.out.format(
                "\nWelcome to Blackjack!\nThe House plays with 6 decks, and stands on all 17s.\nBlackjack pays 3:2, Ace and 10-value pair after a split is a non-Blackjack 21.\nNo Double Down on Blackjack, Unlimited Splits, No Insurance, No Surrenders.\nThis is a massive table with 10 seats, invite your friends.\nHave fun!\n");

        // prompt asking how many players at the table
        System.out.println("\nHow many players at the table?");

        String numPlayersStr = scanner.next().replaceAll("[^0123456789]", "");
        while (numPlayersStr.equals("")) {
            System.out.println("\nThat's not an amount!");
            System.out.format("So how many are you?\n");
            numPlayersStr = scanner.next().replaceAll("[^0123456789]", "");
        }
        if (numPlayersStr.length() > 9) {
            numPlayersStr = numPlayersStr.substring(0, 8);
        }
        int numPlayers = Integer.parseInt(numPlayersStr);
        if (numPlayers > 10) {
            System.out.println("This table only seats 10, everyone else will have to sit and watch.\n");
            numPlayers = 10;
        } else if (numPlayers <= 0) {
            System.out.println("Come back when you exist!\n");
            scanner.close();
            return;
        }

        // get each players name
        String[] playerNames = new String[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            // ask for name, and make player
            System.out.format("\nPlayer %d, what is your name?\n", i + 1);
            String name = scanner.next();
            playerNames[i] = name;
        }

        // String[] playerNames = { "Luke", "Jason" };

        // make a new blackjack game with all players
        Blackjack blackjack = new Blackjack(playerNames, scanner);

        boolean hasPlayedRound = false;
        // while any players have money still, play round
        while (true) {
            // reset the players that didn't bet last round to "playing"
            blackjack.resetEligiblePlayers();

            // remove players that have no money
            for (Iterator<Player> iterator = blackjack.getPlayers().iterator(); iterator.hasNext();) {
                Player player = iterator.next();

                if (player.getMoney() == 0) {
                    // removes the current player
                    System.out.format("\n%s, looks like you just ran out of cash. Get yo broke booty outta here!\n\n",
                            player.getName());
                    player.setIsPlaying(false);
                }
            }

            if (blackjack.allPlayersAreBroke()) {
                System.out.println("\nThat's it, you're all broke. House wins again!");
                break;
            }

            if (!hasPlayedRound) {
                System.out.format("\n\nWould you like to play a round?\n[Y / N]\n");
                String response = scanner.next().replaceAll("[^YyNn]", "");
                while (response.equals("")) {
                    System.out.println("\nWhat was that? I couldn't hear you.");
                    System.out.format("Are you going to play?\n[Y / N]\n");
                    response = scanner.next().replaceAll("[^YyNn]", "");
                }
                response = response.substring(0, 1);
                if (!response.toLowerCase().equals("y")) {
                    System.out.println("\nThanks for wasting my time!");
                    break;
                }
            } else {
                System.out.format("\n\nWould you like to play another round?\n[Y / N]\n");
                String response = scanner.next().replaceAll("[^YyNn]", "");
                while (response.equals("")) {
                    System.out.println("\nWhat was that? I couldn't hear you.");
                    System.out.format("Are you playing again?\n[Y / N]\n");
                    response = scanner.next().replaceAll("[^YyNn]", "");
                }
                if (!response.toLowerCase().equals("y")) {

                    // results on the day
                    blackjack.getResults();

                    System.out.println("\nSee ya next time!");
                    break;
                }
            }

            blackjack.playRound();
            hasPlayedRound = true;
        }

        blackjack.getScanner().close();
    }
}