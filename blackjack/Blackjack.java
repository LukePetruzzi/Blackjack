package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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

        // for (int i = 0; i < decks.size(); i++) {
        // Card card = decks.get(i);
        // System.out.format("Card %d is: %s of %s\n", i,
        // Card.rankToString(card.getRank()),
        // Card.suitToString(card.getSuit()));
        // }
    }

    // main controller for a round of blackjack, from deal to payout
    public void playRound() {

        // get each player to place a bet or pass on the round
        boolean somebodyBet = this.placeBets();

        // don't play the round if nobody bet anything
        if (!somebodyBet) {
            System.out.println("I'll be here if anyone wants to play...");
            return;
        }

        // the dealer deals
        this.deal();

        // each player plays their turn
        for (int i = 0; i < players.size(); i++) {
            this.playTurn(players.get(i));
        }

    }

    // ********************* GETTERS AND SETTERS *******************************
    public List<Player> getPlayers() {
        return this.players;
    }

    public Scanner getScanner() {
        return this.scanner;
    }
    // ********************* END GETTERS AND SETTERS ***************************

    // ********************* PRIVATE HELPER METHODS ****************************

    // ensure there are players that are still in the game
    private boolean playersStillHaveMoney(List<Player> players) {
        return players.size() > 0;
    }

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
        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);
            System.out.format("\n\n%s, what is your bet?\n", player.getName());
            System.out.format("MONEY REMAINING: $%d\n", player.getMoney());
            int bet = scanner.nextInt();
            if (bet <= 0) {
                // player doesn't play
                System.out.println("Have fun on the sideline, LOSER!");
            } else {
                // check if player has enough money to make the bet
                if (bet > player.getMoney()) {
                    bet = player.getMoney();
                    System.out.format("You don't have enough money to bet that much, so you're going all-in with $%d\n",
                            bet);
                }

                player.setCurrentBet(bet);
                somebodyBet = true;
            }
        }
        return somebodyBet;
    }

    private void deal() {
        System.out.format("\n\n-------------------------------------\n|                                   |\n|            DEALER DEALS           |\n|                                   |\n-------------------------------------\n\n");
        // create new decks if the current one is below 1/4 number of cards
        if (this.decks.size() <= ((double) (NUM_DECKS * 52)) * SHUFFLE_RATIO) {
            this.decks = createNewDecks();
        }

        // deal an initial hand for the dealer and for each of the players
        for (int i = 0; i < this.players.size() + 1; i++) {
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
        System.out.format("\n--------------- %s's Turn ---------------\n", player.getName());

        Hand dealerHand = dealer.getHand();
        Card dUpCard = dealerHand.getCards().get(1);
        // player only has one hand, so play it
        int currentHand = 0;
        while (true) {
            System.out.format("\nDealer is showing: 🂠 %s%s%s\n\n", Card.suitToString(dUpCard.getSuit()), 
                    Card.rankToString(dUpCard.getRank()), Card.suitToString(dUpCard.getSuit()));

            Hand hand = player.getHands().get(currentHand);

            System.out.format("%s's hand: %s\n\n", player.getName(), hand.toString());

            String move = this.getValidMove(player, hand);

            if (move.equals("s")) {
                return;
            } else if (move == "h") {

            } else if (move == "d") {

            } else if (move == "l") {

            }
        }

    }

    // checks if the hand can be split into two hands
    private boolean canSplit(Hand hand) {
        if (hand.getCards().size() > 2) {
            return false;
        }
        return hand.getCards().get(0).getRank() == hand.getCards().get(1).getRank();
    }

    private String getValidMove(Player player, Hand hand) {

        System.out.format("%s, what would you like to do?\n", player.getName());

        // build possible moves
        StringBuilder possMoves = new StringBuilder();
        possMoves.append("Stand [S]");
        // check if the player has blackjack
        if (!hand.isBlackjack()) {
            possMoves.append(", Hit [H]");
            possMoves.append(", Double Down [D]");

            // check if the player can split
            if (this.canSplit(hand)) {
                possMoves.append(", Split [L]");
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

    // private void playHand(Player player, Hand hand) {

    // }

    // ********************* END PRIVATE HELPER METHODS ************************

    // make a game
    public static void main(String[] args) {
        new Blackjack().run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        int STARTING_MONEY = 1000;

        System.out.format(
                "\nWelcome to Blackjack!\nPlease input money and other obvious number amounts as plain ints, and all other things as strings. Consider the inputs sanitized!\nThe House plays with 6 decks, and stands on all 17s.\nBlackjack pays 3:2, and Ace and 10-value pair after a split counts as a non-Blackjack 21. No Double Down on Blackjack. Unlimited Splits.\nThis is a massive table with 10 seats, invite your friends.\nHave fun!\n\n\n");

        // // prompt asking how many players at the table
        // System.out.println("How many players at the table?");
        // int numPlayers = scanner.nextInt();
        // if (numPlayers > 10) {
        // System.out.println("This table only seats 10, everyone else will have to sit
        // and watch.\n");
        // }
        // if (numPlayers < 10) {
        // System.out.println("Come back when you exist!\n");
        // scanner.close();
        // return;
        // }

        // // get each players name
        // String[] playerNames = new String[numPlayers];
        // for (int i = 0; i < numPlayers; i++) {
        // // ask for name, and make player
        // System.out.format("Player %d, what is your name?\n", i + 1);
        // String name = scanner.next();
        // playerNames[i] = name;
        // }

        String[] playerNames = { "Luke", "Jason" };

        // make a new blackjack game with all players
        Blackjack blackjack = new Blackjack(playerNames, scanner);

        // while any players have money still, play round
        while (true) {

            // remove players that have no money
            for (Iterator<Player> iterator = blackjack.getPlayers().iterator(); iterator.hasNext();) {
                Player player = iterator.next();

                if (player.getMoney() == 0) {
                    // removes the current player
                    System.out.format("%s, get yo broke booty outta here!\n\n", player.getName());
                    iterator.remove();
                }
            }

            if (blackjack.getPlayers().size() == 0) {
                System.out.println("That's it, you're all broke. House wins again!");
                break;
            }

            System.out.format("Would you like to play another round?\n[Y / N]\n");
            String response = scanner.next();
            if (!response.toLowerCase().equals("y")) {

                // I should make a results printout here!

                System.out.println("See ya next time!");
                break;
            }

            blackjack.playRound();
        }

        blackjack.getScanner().close();
    }
}