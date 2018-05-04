package blackjack;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Blackjack {

    private final int STARTING_MONEY = 1000;

    private Scanner scanner;
    private ArrayList<Player> players;

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

    }

    public void playRound() {

    }

    // ********************* GETTERS AND SETTERS *******************************
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public Scanner getScanner() {
        return this.scanner;
    }
    // ********************* END GETTERS AND SETTERS ***************************

    // ********************* PRIVATE HELPER METHODS ****************************

    // ensure there are players that are still in the game
    private boolean playersStillHaveMoney(ArrayList<Player> players) {
        return players.size() > 0;
    }
    // ********************* END PRIVATE HELPER METHODS ************************

    // make a game
    public static void main(String[] args) {
        new Blackjack().run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        int STARTING_MONEY = 1000;

        // prompt asking how many players at the table
        System.out.println("How many players at the table?");
        int numPlayers = scanner.nextInt();

        // get each players name
        String[] playerNames = new String[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            // ask for name, and make player
            System.out.format("Player %d, what is your name?\n", i + 1);
            String name = scanner.next();
            playerNames[i] = name;
        }

        // make a new blackjack game with all players
        Blackjack blackjack = new Blackjack(playerNames, scanner);

        // while any players have money still, play round
        while (playersStillHaveMoney(blackjack.getPlayers())) {

        }

        blackjack.getScanner().close();
    }
}