package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import Deck.*;
import Deck.Cards.Card;
import Deck.Cards.Symbols;
import Deck.Cards.UnoCard;
import Uno.*;

public class Server {
    private Deck deck;
    private Deck playedCards = new UnoDeck();
    private LinkedList<Player> players;
    private boolean skip;
    private RuleBook ruleBook;
    private int numberOfClients;
    private boolean playerIsPlaying;
    int cardsToDraw = 0;
    private Card lastcard;
    private int index = 0;
    private boolean win = false;
    public boolean clockwise;
    public Server(Deck deck, int numberOfHumanClients, int numberOfBotClients, RuleBook ruleBook) throws IOException {
        this.skip = false;
        this.deck = deck;
        this.clockwise = true;
        this.ruleBook = ruleBook;
        players = new LinkedList<Player>();
        numberOfClients = numberOfBotClients + numberOfHumanClients;
        setupServer(numberOfClients);
        dealCardsToPlayers();


    }
    /*Sets up the server, adds players, creates sockets etc.*/
    private void setupServer(int numberOfOnlineClients) throws IOException {
        try {
            ServerSocket aSocket = new ServerSocket(2048);
            for(int onlineClient=0; onlineClient<numberOfOnlineClients; onlineClient++) {
                Socket connectionSocket = aSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                boolean isBot = Boolean.parseBoolean(inFromClient.readLine());

                Player newPlayer = new UnoPlayer(onlineClient, isBot, connectionSocket, inFromClient, outToClient);
                players.add(newPlayer);
                System.out.println("Connected to " + (isBot?"Bot":"Player") + " ID: " + (onlineClient));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*Starts the server and the game. */
    public void startGame(){
        while (true) {
            lastcard = deck.drawCard();
            if (ruleBook.isValidStartCard(lastcard)) {
                playedCards.add(lastcard);
                System.out.println("Starting card is: " + lastcard.printCard());
                messageStartingCard(lastcard);
                break;
            } else {
                deck.add(lastcard);
                deck.shuffleDeck();
            }
        }

        /*The game starts here.*/
        while(!win){
            Player current = players.get(index);
            if(cardsToDraw > 0){players.get(index).setUno(false);}
            playerDrawCard(cardsToDraw, current); // The server draw cards and message them to the client.
            if(!skip){
            try {
                current.getOutToClient().writeBytes(GameStates.YOURTURN+"\n"); // Tells the client it is his or her turn
            } catch (IOException e) {
                e.printStackTrace();
            }

            playerIsPlaying = true;
            while(playerIsPlaying){
                String[] input = readFromServer(current);
                reactToInput(input, current);
                }
            }else{
                skip = false;
            }
            index = ruleBook.checkNextPlayer(index, clockwise ,players);


        }
    }



    /*Base of the server. This method reacts to the input, depending on the inputs it reacts certain ways.*/
    private void reactToInput(String[] input, Player player){
        int state = Integer.parseInt(input[0]);
        switch (state){
            case GameStates.CARD:{
                Card[] placedCards = getPlayedCards(input[1], player);
                Card lastCardPlayed = placeCards(placedCards);

                if(ruleBook.cardIsSkip(lastCardPlayed)){ skip = true;}
                if(ruleBook.cardIsChangeDirection(lastCardPlayed)){clockwise = !clockwise;}


                cardsToDraw = ruleBook.checkNumberOfCardsToDraw(playedCards);
                tellClients(GameStates.CARD + ";" + lastCardPlayed.getColor() + ":" + lastCardPlayed.getSymbol() +";" +" "+";"+ "\n");

                if(player.hasUno()){
                    win = true;
                   tellClients(GameStates.WIN+";"+(player.isBot()?"Bot ":"Player ")+player.getPlayerId()+" WINS!!!;"+"\n");
                }
                playerIsPlaying = false;
                if(input[2].equals("true")){
                    player.setUno(true);
                    tellClients(GameStates.UNO + ";" + (player.isBot()?"Bot ":"Player ")+player.getPlayerId()+" "+"\n");
                }else{
                    player.setUno(false);
                }

                tellClients(GameStates.GAMEINFO+";"+(player.isBot()?"Bot ":"Player ")+player.getPlayerId()+" "+"\n");
                break;
            }

            case GameStates.DRAW:{
                playerDrawCard(1, player);
                player.setUno(false);
                try {
                    player.getOutToClient().writeBytes(GameStates.YOURTURN+"\n");
                } catch (IOException e) {}

            }

            case GameStates.WIN:{

            }
        }
    }
    /*Places cards on the played deck.*/
    private Card placeCards(Card[] placedCards){
        for(int i = 0; i < placedCards.length; i++){
            playedCards.add(placedCards[i]);
        }

        System.out.println(playedCards.getLastCard().printCard());
        return playedCards.getLastCard();
    }

    /*Creates a new card*/
    private Card getCard(String color, String symbol){
        Card card = new UnoCard(color, symbol);
        return card;
    }
    /*Returns the cards the player played*/
    private Card[] getPlayedCards(String cards, Player current){
        String[] cardElements = cards.split(":");
        Card[] card = new Card[cardElements.length/2];
        int n = 0;
        for(int i = 0; i<((cardElements.length/2)+1); i+=2){
            card[n] = getCard(cardElements[i], cardElements[i+1]);
            n++;
        }
        return card;
    }

    /*Deals out card to players in the beginning*/
    private void dealCardsToPlayers(){
        for (int n = 0; n < players.size(); n++) {
            Card[] cards = new Card[ruleBook.getRules().numberOfCardsOnHand];
            for(int i = 0; i<ruleBook.getRules().numberOfCardsOnHand; i++) {
                cards[i] = deck.drawCard();
                players.get(n).addCard();

            }
            try {
                players.get(n).getOutToClient().writeBytes(createCardString(cards));
            } catch (IOException e) {
            }
        }
    }

    /*Draw cards to player and gives it to him or her.*/
    private void playerDrawCard(int numberOfCardsToDraw, Player current){
        Card[] cards = new Card[numberOfCardsToDraw];
        for(int i = 0; i < numberOfCardsToDraw; i++){
            if(deck.getSize() == 0){
                deck.setUpDeck(ruleBook.getRules());
            }
            cards[i] = deck.drawCard();
            current.addCard();
        }
        try {
            if(cards.length > 0 ){
                current.getOutToClient().writeBytes(createCardString(cards));
                cardsToDraw = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*Creates a string correct string with card or cards that can be sent out to clients.*/
    private String createCardString(Card[] cards){
        String cardString = GameStates.DRAW + ";";
        cardString += cards[0].getColor() + ":" + cards[0].getSymbol();
        for(int i = 1; i<cards.length; i++){
            cardString+= ":"+cards[i].getColor()+":"+cards[i].getSymbol();
        }
        cardString+="\n";
        return cardString;
    }

    /*Takes in a message and sends it out to all the client.*/
    private void tellClients(String message){
        try {
            for(int i = 0; i<players.size();i++)
            players.get(i).getOutToClient().writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*Is played in the beginning, the first card is place on the table and informs which card it is to the players.*/
    private void messageStartingCard(Card card){
        for(int i=0; i<players.size(); i++) {
            if(players.get(i).isOnline()) {
                try {
                    players.get(i).getOutToClient().writeBytes(GameStates.CARD + ";" + card.getColor() + ":" + card.getSymbol() +";" + "Starting card is: "+";"+ "\n"); //make sure all clients know the starting card
                } catch(Exception e) {}
            }
        }
        tellClients(GameStates.GAMEINFO+";"+"Starting Card is; "+" "+"\n");
    }


    /*Reads the input form player.*/
    private String[] readFromServer(Player current){
        try{
            return current.getInFromClient().readLine().split(";");
        }catch(Exception e){ }
        return null;
}

}

