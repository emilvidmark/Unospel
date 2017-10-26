package Deck.Cards;

import java.util.LinkedList;

import static Deck.Cards.Symbols.*;

public class CardFactory {
    private Card createColoredCard(String color, String symbol){
        Card card = new UnoCard(color, symbol);
        return card;
    }

    private Card createSpecialCard(String color, String symbol){
        Card card = new SpecialCard(color, symbol);
        return card;
    }
    /*Create colored cards*/
    public LinkedList<Card> createColoredCards(int numberOfCards, String symbol){
        LinkedList<Card> cards = new LinkedList<Card>();
        for(int i = 0; i < numberOfCards; i++){
            cards.add(createColoredCard(GREEN, symbol));
            cards.add(createColoredCard(BLUE, symbol));
            cards.add(createColoredCard(YELLOW, symbol));
            cards.add(createColoredCard(RED, symbol));
        }
        return cards;
    }
    /*Creates special cards.*/
    public LinkedList<Card> createSpecialCards(int numberOfCards, String symbol){
        LinkedList<Card> cards = new LinkedList<Card>();
        for(int i = 0; i < numberOfCards; i++){
            cards.add(createSpecialCard(BLACK, symbol));
        }
        return  cards;
    }


}
