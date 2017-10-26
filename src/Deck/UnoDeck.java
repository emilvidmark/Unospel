package Deck;
import Deck.Cards.Card;
import Deck.Cards.CardFactory;
import Uno.GameSettings;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static Deck.Cards.Symbols.RESET;
import static Deck.Cards.Symbols.*;

// Creates Deck.UnoDeck
public class UnoDeck implements Deck{
    private CardFactory factory = new CardFactory();
    private LinkedList<Card> deck = new LinkedList<Card>();
    private GameSettings ruleBook;
    private Random rnd;

    public UnoDeck(GameSettings ruleBook){
        this.ruleBook = ruleBook;
        setUpDeck(ruleBook);
    }
    public UnoDeck(){

    }
    @Override
    public Card drawCard() {
        return deck.remove();
    }
    @Override
    public void add(Card card) {
        deck.add(card);
    }
    @Override
    public int getSize() {
        return deck.size();
    }

    @Override
    public Card getLastCard() {
        return deck.get(getSize()-1);
    }

    @Override
    /*prints the deck.*/
    public void printDeck() {
        for(int i = 0; i<deck.size(); i++){
            System.out.print("\t"+deck.get(i).getColor() + "[" + deck.get(i).getSymbol() + "]" + RESET);
        }
        System.out.print("\n");
    }
    /*Returns card at certain position*/
    @Override
    public Card getCardAtPosition(int i) {
        return deck.get(i);
    }
    /*Inserts card into the deck.*/
    private void insertNewCards(LinkedList<Card> cards){
        int n = cards.size();
        for(int i = 0; i < n; i++){
            add(cards.remove());
        }
    }
    /*Inserts all the cards into the deck.*/
    public void setUpDeck(GameSettings ruleBook){

        //Insertion of number cards
        insertNewCards((factory.createColoredCards(ruleBook.numberOfZeros, ZERO)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfOnes, ONE)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfTwos, TWO)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfThrees, THREE)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfFours, FOUR)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfFives,  FIVE)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfSixes,  SIX)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfSevens, SEVEN)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfEights,  EIGHT)));
        insertNewCards((factory.createColoredCards(ruleBook.numberOfNines,  NINE)));

        //Insertion of colored special cards

        insertNewCards(factory.createColoredCards(ruleBook.numberOfSkip, SKIP));
        insertNewCards(factory.createColoredCards(ruleBook.numberOfChangeDirection, CHANGEDIRECTION));
        insertNewCards(factory.createColoredCards(ruleBook.numberOfPlusTwo, PLUSTWO));


       //Insertion of special cards

        insertNewCards(factory.createSpecialCards(ruleBook.numberOfPlusFour, PLUSFOUR));
        insertNewCards(factory.createSpecialCards(ruleBook.numberOfChangeColor, CHANGECOLOR));

        //Shuffels the deck, borrowed this from your code Josef.

        rnd = ThreadLocalRandom.current();
        for(int i=deck.size()-1; i>0; i--) {
            int index = rnd.nextInt(i + 1);
            Card a = deck.get(index);
            deck.set(index, deck.get(i));
            deck.set(i, a); // SWAP
        }
    }
    /*Shuffles the deck*/
    public void shuffleDeck(){
        rnd = ThreadLocalRandom.current();
        for(int i=deck.size()-1; i>0; i--) {
            int index = rnd.nextInt(i + 1);
            Card a = deck.get(index);
            deck.set(index, deck.get(i));
            deck.set(i, a); // SWAP
        }
    }
    public void reset(){
        deck = new LinkedList<Card>();
    }
}
