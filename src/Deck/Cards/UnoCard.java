package Deck.Cards;

import Deck.Cards.Card;
import Deck.Deck;

import static Deck.Cards.Symbols.RESET;

public class UnoCard implements Card {
    private String color;
    private String symbol;

    public UnoCard(String color, String symbol){
        this.color = color;
        this.symbol = symbol;
    }
    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String printCard() {
        return ("\t"+getColor() + "[" + getSymbol() + "]" + RESET);
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }
}
