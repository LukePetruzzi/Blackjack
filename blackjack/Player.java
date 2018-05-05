
package blackjack;

import java.util.ArrayList;


public class Player {

    private String name;
    private int money;
    private ArrayList<Hand> hands;
    private ArrayList<Integer> currentBets;

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
        this.hands = new ArrayList<Hand>();
        this.currentBets = new ArrayList<Integer>();
    }

    public String getName() {
        return this.name;
    }

    public int getMoney() {
        return this.money;
    }

    public ArrayList<Integer> getCurrentBets() {
        return this.currentBets;
    }

    public int getCurrentBet(int currentHandIndex) {
        return this.currentBets.get(currentHandIndex);
    }

    public void setCurrentBets(ArrayList<Integer> bets) {
        this.currentBets = bets;
    }

    public void setCurrentBet(int bet, int currentHandIndex) {
        this.currentBets.set(currentHandIndex, bet);
    }

    public void addCurrentBet(int bet) {
        this.currentBets.add(bet);
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<Hand> getHands() {
        return this.hands;
    }

    public void setHands(ArrayList<Hand> hands) {
        this.hands = hands;
    }

    public void addHand(Hand hand) {
        this.hands.add(hand);
    }
}