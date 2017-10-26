package Uno;

import Deck.Cards.Card;
import Deck.Cards.Symbols;

import java.util.LinkedList;
/*Borrowed this from Josefs code, only made a class of it instead of one big.*/
public class Hand {
    private LinkedList<Card> cards = new LinkedList<Card>();
    public void printHand() {
        System.out.print("Your Current Hand: ");
        for(int i=0; i<cards.size(); i++) {
            System.out.print(printCard(cards.get(i)));
        }
        System.out.print("\n");
    }
    private String printCard(Card card) {
        return "\t"+card.getColor() + " " + card.getSymbol() + " " + Symbols.RESET;
    }

    public Card pickCard(int i){
        return cards.remove(i);
    }
    public void addCardToHand (Card card){
        cards.add(card);
    }
    public Card getCard(int i){return cards.get(i);}

    public LinkedList<Card> getHand(){
        return cards;
    }
    public int getSize(){
        return cards.size();
    }
}
