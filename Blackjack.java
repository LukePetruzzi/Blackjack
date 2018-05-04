import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class Blackjack {

    private class Hand {

    }

    private class Player {

        private String name;
        private int money;
        private ArrayList<Hand> hands;

        private Player(String name, int money) {
            this.name = name;
            this.money = money;
        }

    }

    // instantiate a new game with this
    public Blackjack() {

    }

    public void playRound() {

    }

    // make a game
    public static void main(String[] args) {
        System.out.println("HELLO?");

        Scanner scanner = new Scanner(System.in);

        // prompt asking how many players at the table
        System.out.println("How many players at the table?");
        int numPlayers = scanner.nextInt();

        // for each player in numPlayers, ask for name, make new Player, and add to
        // arraylist of Player objects
        ArrayList playerList = new ArrayList<Player>();
        for (int i = 0; i < numPlayers; i++) {
            // ask for name, and make player
        }

        // make a new blackjack game with all players

        // while any players have money still, play round

        scanner.close();
    }
}