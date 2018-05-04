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

    private Scanner scanner;
    private List<Player> players;
    private List<Card> decks;

    public Blackjack() {

    }

    public Blackjack(String[] playerNames, Scanner scanner) {

        this.scanner = scanner;

        // make the players
        players = new ArrayList<Player>();
        for (int i = 0; i < playerNames.length; i++) {
            Player player = new Player(playerNames[i], STARTING_MONEY);
            players.add(player);
        }

        this.decks = new ArrayList<Card>();
        // make the decks
        for (int i = 0; i < NUM_DECKS; i++) {

            // use ranks + 1
            for (int suit = 0; suit < 4; suit++) {
                // use suit + 1
                for (int rank = 0; rank < 13; rank++) {
                    decks.add(new Card(rank + 1, suit + 1));
                }
            }
        }
        // shuffle the decks
        this.shuffle();

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

    private void shuffle() {
        Collections.shuffle(this.decks);
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

    }
    // ********************* END PRIVATE HELPER METHODS ************************

    // make a game
    public static void main(String[] args) {
        new Blackjack().run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        int STARTING_MONEY = 1000;

        System.out.format("\nWelcome to Blackjack!\nPlease input money amounts as plain ints, and all other things as strings. Have fun!\n\n\n");

        // // prompt asking how many players at the table
        // System.out.println("How many players at the table?");
        // int numPlayers = scanner.nextInt();

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

            System.out.format("Would you like to play another round?\n[Y] / [N]\n");
            String response = scanner.next();
            if (!response.toLowerCase().equals("y")) {
                System.out.println("See ya next time!");
                break;
            }

            blackjack.playRound();
        }

        blackjack.getScanner().close();
    }
}