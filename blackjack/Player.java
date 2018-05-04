
package blackjack;

import java.util.ArrayList;


public class Player {

    private String name;
    private int money;
    private ArrayList<Hand> hands;
    private int currentBet;

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
    }

    public String getName() {
        return this.name;
    }

    public int getMoney() {
        return this.money;
    }

    public int getCurrentBet() {
        return this.currentBet;
    }

    public void setCurrentBet(int bet) {
        this.money -= bet;
        this.currentBet = bet;
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
}