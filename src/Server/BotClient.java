package Server;

import Deck.Cards.Card;
import Deck.Cards.Symbols;
import Deck.Cards.UnoCard;
import Deck.Deck;
import Uno.Hand;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import Uno.GameStates;
import Deck.*;
import Uno.RuleBook;
import Uno.main;

public class BotClient {
    private DataOutputStream outToServer;
    BufferedReader inFromServer;
    private Hand hand;
    private Deck playedCards;
    private int CURRENTSTATE;
    private RuleBook ruleBook = new RuleBook();
    private Random rnd = new Random();
    private boolean uno;
    public BotClient() {
        hand = new Hand();
        playedCards = new UnoDeck();
        try {
            uno = false;
            Socket clientSocket = new Socket("localhost", 2048);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes("true\n");
            reactToInput(readFromServer()); //Eighter draws cards or places card on playedCards
            while(true){
                reactToInput(readFromServer());
            }

        }catch (Exception e){}

    }
    /*Reads string from server and splits it into an array.*/
    public String[] readFromServer(){
        try{
            return inFromServer.readLine().split(";");
        }catch(Exception e){ }
        return null;
    }
    /*Adds cards to hand.*/
    public void drawCards(String input){
        String[] cards = input.split(":");
        for(int i = 0; i<cards.length; i+=2){
            hand.addCardToHand(new UnoCard(cards[i], cards[i+1]));
        }
    }
    /*Creates and returns card*/
    public Card getCard(String cardString){
        String[] cardElements = cardString.split(":");
        Card card = new UnoCard(cardElements[0], cardElements[1]);
        return card;
    }
    /*Reacts to the input from the server*/
    public void reactToInput(String[] input) {
        int action = Integer.parseInt(input[0]);
        switch (action) {
            case GameStates.DRAW: {
                drawCards(input[1]);
                uno = false;
                break;
            }
            case GameStates.CARD: {         // Card played is placed on the top of the played deck.
                playedCards.add(getCard(input[1]));
                System.out.println("Played card is: " + playedCards.getLastCard().printCard()+"\n");
                break;
            }
            case GameStates.YOURTURN: {
                hand.printHand();
                if (ruleBook.handIsPlayable(hand.getHand(), playedCards.getLastCard())) {
                    Card cardToPlay = botPlay();
                    playedCards.add(cardToPlay);
                    try {
                            outToServer.writeBytes(GameStates.CARD + ";" + cardToPlay.getColor() + ":" + cardToPlay.getSymbol() + ";" + getUno() + ";\n");
                            break;
                    }catch (Exception e){}
                    }
                else {
                    try {
                        outToServer.writeBytes(GameStates.DRAW + "; " +"; \n");
                        break;
                    } catch (Exception e) {}
                    break;
                }
                break;
            }
            case GameStates.WIN:{
                System.out.println(input[1]);
                break;            }

            case GameStates.GAMEINFO:{
                break;
            }

            case GameStates.UNO:{
                System.out.println(input[1] + " called uno");
                break;
            }
        }

    }

    /*Bot intelligence*/
    public Card botPlay() {
        rnd = ThreadLocalRandom.current();
        Card cardToPlay = null;
        for (int i=0; i<hand.getSize(); i++) {
            if(ruleBook.isValidMove(hand.getCard(i), playedCards.getLastCard())) { //Same value, same color, or wildcard
                if(ruleBook.isWildCard(hand.getCard(i))) {
                    int color = rnd.nextInt(4);
                    hand.getCard(i).setColor(Symbols.BLUE);
                    switch(color){
                        case 0:{
                            hand.getCard(i).setColor(Symbols.GREEN);
                            break;
                        }
                        case 1:{
                            hand.getCard(i).setColor(Symbols.BLUE);
                            break;
                        }
                        case 2:{
                            hand.getCard(i).setColor(Symbols.YELLOW);
                            break;
                        }
                        case 3:{
                            hand.getCard(i).setColor(Symbols.RED);
                            break;
                        }
                    }
                }

                if(hand.getSize() == 2) {
                    uno = true;
                }
                return cardToPlay = hand.pickCard(i);
            }
        }
        return cardToPlay; // Detta kommer aldrig inträffa, kontrollen är redan gjord.
    }
    public static void main(String argv[]) {
        BotClient botClient = new BotClient();
    }

    /*Bot has uno.*/
    public String getUno(){
        if(uno){return "true";}
        else{return "false";}
    }
}
