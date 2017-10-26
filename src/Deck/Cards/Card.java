package Deck.Cards;

import Deck.Deck;

public interface Card {
    public String getColor();
    public String getSymbol();
    public String printCard();
    public void setColor(String color);
}
