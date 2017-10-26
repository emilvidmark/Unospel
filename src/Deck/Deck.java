package Deck;

import Deck.Cards.Card;
import Uno.GameSettings;

public interface Deck {
    public Card drawCard();
    public void add(Card card);
    public int getSize();
    public Card getLastCard();
    public void printDeck(); //Developing purposes.
    public Card getCardAtPosition(int i);
    public void shuffleDeck();
    public void reset();
    public void setUpDeck(GameSettings ruleBook);
}
