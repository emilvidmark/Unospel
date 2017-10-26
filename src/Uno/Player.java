package Uno;

import Deck.Cards.Card;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.LinkedList;

public interface Player {
    public Hand getHand();
    public void addCardToHand(Card card);
    public boolean isOnline();
    public BufferedReader getInFromClient();
    public DataOutputStream getOutToClient();
    public boolean hasUno();
    public void setUno(Boolean uno);
    public Boolean isBot();
    public void removeCard();
    public void addCard();
    public int getPlayerId();
}
