package Uno;

import Deck.Cards.Card;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class UnoPlayer implements Player {
    public int playerID;
    private boolean isBot;
    private boolean online = true;
    public Socket connection;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private Hand hand;
    private int numberOfCards;
    private boolean uno = false;

    public UnoPlayer(int playerID, boolean isBot, Socket socketConnection, BufferedReader inFromClient, DataOutputStream outToClient){
        this.isBot = isBot;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.playerID = playerID;
        this.numberOfCards = 0;

    }

    public void addCard(){numberOfCards++;}

    @Override
    public int getPlayerId() {
        return playerID;
    }

    public void removeCard(){numberOfCards--;}
    @Override
    public Hand getHand(){
        return hand;
    }

    @Override
    public void addCardToHand(Card card) {
        hand.addCardToHand(card);
        this.uno = false;
    }

    @Override
    public boolean isOnline() {
        return online;
    }

    public BufferedReader getInFromClient() {
        return inFromClient;
    }

    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    @Override
    public boolean hasUno() {
        return uno;
    }

    @Override
    public void setUno(Boolean uno) {
        this.uno = uno;
    }

    @Override
    public Boolean isBot() {
        return isBot;
    }

    public void callUno(){
        this.uno = true;
    }


}

