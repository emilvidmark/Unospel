package Deck.Cards;

import static Deck.Cards.Symbols.RESET;

public class SpecialCard implements Card {
    private String color;
    private String symbol;

    public SpecialCard(String color, String symbol){
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

    public void setColor(String color){
        this.color = color;
    }
}
