package blackjack;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

public class Hand {

    private List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<Card>();
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public int getValue() {
        int value = 0;
        int numAces = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int cardVal = card.getRank();
            // faces equal 10
            if (cardVal >= 10) {
                cardVal = 10;
            }

            // skip aces, handle separately
            if (cardVal == 1) {
                numAces += 1;
                continue;
            }
            value += cardVal;
        }

        // handle aces
        if (numAces > 0) {
            int valLeft = 21 - value;
            
            if (valLeft - 11 >= numAces - 1) {
                value += (11 + numAces - 1);
            } else {
                value += numAces;
            }
        }

        return value;
    }

    public boolean isBlackjack() {
        if (this.cards.size() == 2 && getValue() == 21) {
            return true;
        }
        return false;
    }

    public boolean containsAce() {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getRank() == 1) {
                return true;
            }
        }
        return false;
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < this.cards.size(); i++) {
            Card card = this.cards.get(i);
            str.append(Card.suitToString(card.getSuit()));
            str.append(Card.rankToString(card.getRank()));
            str.append(Card.suitToString(card.getSuit()));
            if (i != this.cards.size() - 1) {
                str.append(" ");
            }
        }

        return str.toString();
    }
}