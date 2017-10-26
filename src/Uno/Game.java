package Uno;
import Deck.Deck;
import Deck.UnoDeck;
import Server.Server;


import java.io.IOException;


public class Game {
    private RuleBook ruleBook;
    private Deck deck;
    private Server server;


    public void setupNewGame(int numberOfHumanPlayers, int numberOfBotPlayers){
        ruleBook = new RuleBook();
        deck = new UnoDeck(ruleBook.getRules());
        try {
            server = new Server(deck, numberOfHumanPlayers, numberOfBotPlayers, ruleBook);
            server.startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }







}

