package Server;

import Deck.Cards.Card;
import Deck.Cards.UnoCard;
import Deck.UnoDeck;
import Uno.GameStates;
import Uno.Hand;
import Uno.RuleBook;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import Deck.*;
import Deck.Cards.*;

import java.util.Arrays;
import java.util.Random;

public class HumanClient {
    private DataOutputStream outToServer;
    BufferedReader inFromServer;
    BufferedReader br;
    private Hand hand;
    private Deck playedCards;
    private int CURRENTSTATE;
    private RuleBook ruleBook = new RuleBook();
    private Random rnd = new Random();
    private boolean uno;
    public HumanClient() {
        hand = new Hand();
        playedCards = new UnoDeck();
        try {
            uno = false;
            Socket clientSocket = new Socket("localhost", 2048);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes("false\n");
            reactToInput(readFromServer()); //Eighter draws cards or places card on playedCards
            while(true){
                reactToInput(readFromServer());
            }

        }catch (Exception e){}

    }
    /*Reads from the server and splits the received string into an array.*/
    private String[] readFromServer(){
        try{
            return inFromServer.readLine().split(";");
        }catch(Exception e){ }
        return null;
    }

    private void drawCards(String input){
        String[] cards = input.split(":");
        for(int i = 0; i<cards.length; i+=2){
            hand.addCardToHand(new UnoCard(cards[i], cards[i+1]));
        }
    }

    private Card getCard(String cardString){
        String[] cardElements = cardString.split(":");
        Card card = new UnoCard(cardElements[0], cardElements[1]);
        return card;
    }
    /*Reacts to the input from the server*/

    private void reactToInput(String[] input) {
        int action = Integer.parseInt(input[0]);
        switch (action) {
            case GameStates.DRAW: {
                uno = false;
                drawCards(input[1]);
                break;
            }
            case GameStates.CARD: {                 //Places card at the top of the played deck.
                playedCards.add(getCard(input[1]));
                break;
            }
            case GameStates.YOURTURN: {
                if (ruleBook.handIsPlayable(hand.getHand(), playedCards.getLastCard())) {       //If hand is playable the user plays.
                    Card[] cardToPlay = humanPlay();
                    playedCards.add(cardToPlay[cardToPlay.length-1]);
                    try {
                        String cardString = createCardString(cardToPlay);
                        outToServer.writeBytes(cardString);
                        break;
                    }catch (Exception e){}
                }
                else {
                    try {
                        outToServer.writeBytes(GameStates.DRAW + "; "+"; \n");              //If user don't has cards he draws 1 card.
                        break;
                    } catch (Exception e) {}
                    break;
                }
                break;
            }
            case GameStates.WIN:{
                System.out.println(input[1]);
                break;            }

            case GameStates.GAMEINFO:{  //Prints out message from server
                System.out.print(input[1]);
                System.out.println(playedCards.getLastCard().printCard());
                break;
            }
            case GameStates.UNO:{
                System.out.println(input[1] + " called uno");
                break;
            }
        }

    }
    /*The human play*/
    private Card[] humanPlay() {
        Card[] cardsToPlay;

        hand.printHand();
        System.out.print("                     ");
        for(int i= 0; i<hand.getSize();i++){
            System.out.print("["+i+"]" + "     ");
        }
        System.out.println("\n");
        cardsToPlay = getUserCardChoises();
        return cardsToPlay;
    }

    public static void main(String argv[]) {

        HumanClient humanClient = new HumanClient();
    }
    /*Does the user has uno*/
    public String getUno(){
        if(uno){return "true";}
        else{return "false";}
    }


    /*This methods runs when user should pick cards.*/
    private Card[] getUserCardChoises(){
        while(true) {
            int[] userChoise = readUserInput();
            Card[] cards = new Card[userChoise.length];
            for (int i = 0; i < userChoise.length; i++) {
                cards[i] = hand.getCard(userChoise[i]);
            }
            if (cardsIsPlayable(cards)) {
                Card[] pickedCards = new Card[userChoise.length];
                for (int i = 0; i < userChoise.length; i++) {
                    cards[i] = hand.getCard(userChoise[i]);
                }
                removeCardsFromHand(userChoise);
                return cards;
            }
            System.out.println("Sorry, you can't play those cards.");
        }

}
    /*Removes the cards from hand*/
    private void removeCardsFromHand(int[] userChoise){
        Arrays.sort(userChoise);
        for(int i = userChoise.length-1; i >= 0; i--){
            hand.pickCard(userChoise[i]);
        }
    }

    /*Waits for user to insert input, also validates if it is the right type of input.*/
    private int[] readUserInput(){

        while(true){
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                String[] input = br.readLine().split(",");
                int numberOfLoops = input.length;
                if(input[input.length-1].equals("uno") && hand.getSize() == 2){
                    uno = true;
                    numberOfLoops--;
                }
                if(input[input.length-1].equals("uno") && hand.getSize() > 2){
                    System.out.println("Saw what you did there, don't try to cheat. Your cards will be played.");
                    numberOfLoops--;
                }
                int[] pickedNumbers = new int[numberOfLoops];
                for(int i = 0; i < numberOfLoops; i++) {
                        if (Integer.parseInt(input[i]) >= hand.getSize() || Integer.parseInt(input[i]) < 0) {
                            throw new Exception("Bad number");
                        }
                        pickedNumbers[i] = Integer.parseInt(input[i]);
                }
                return  pickedNumbers;
            }catch (Exception e){
                System.out.println("Sorry, you must enter valid characters.");
            }
        }
    }

    /*Checks if cards is playable*/
    private boolean cardsIsPlayable(Card[] cards) {
        if (ruleBook.isValidMove(cards[0], playedCards.getLastCard())) {
            if(ruleBook.isWildCard(cards[0])){
                cards = pickColor(cards);
            }
            if (cards.length > 1) {
                for (int i = 0; i < cards.length - 1; i++) {
                    if (ruleBook.isValidMultiMove(cards[i + 1], cards[i])) {
                        continue;
                    } else {
                        return false;
                    }

                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

    /*Creates a string with with cards that should be sent to the server.*/
    private String createCardString(Card[] cards){
        String cardString = GameStates.CARD + ";";
        cardString += cards[0].getColor() + ":" + cards[0].getSymbol();
        for(int i = 1; i<cards.length; i++){
            cardString+= ":"+cards[i].getColor()+":"+cards[i].getSymbol();
        }
        cardString+= ";" + getUno() +"; \n";
        return cardString;
    }


    /*If a wild card is choosen the user needs to pick a color.*/
    private Card[] pickColor(Card[] cards){
        System.out.println("Pick a color: " + Symbols.GREEN +" 0 " + Symbols.BLUE+" 1 " + Symbols.YELLOW + " 2 " + Symbols.RED + " 3 " + Symbols.RESET);
        while(true){
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                int input = Integer.parseInt(br.readLine());
                if ((input > 3) || (input < 0)) {
                        throw new Exception("Bad number");
                    }else{
                    switch(input){
                        case 0:{
                            cards[0].setColor(Symbols.GREEN);
                            return cards;
                        }
                        case 1:{
                            cards[0].setColor(Symbols.BLUE);
                            return cards;
                        }
                        case 2:{
                            cards[0].setColor(Symbols.YELLOW);
                            return cards;
                        }
                        case 3:{
                            cards[0].setColor(Symbols.RED);
                            return cards;
                        }
                    }
                }
            }catch (Exception e){
                System.out.println("Sorry, you must enter valid characters.");
            }
        }
    }
}
