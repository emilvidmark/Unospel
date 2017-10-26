package Uno;

import Deck.Cards.Card;
import Deck.*;
import Deck.Cards.Symbols;

import java.util.LinkedList;


public class RuleBook {
    private GameSettings rules = new GameSettings();

    public boolean handIsPlayable(LinkedList<Card> hand, Card lastCard){
        for(int i = 0; i < hand.size(); i++){
            Card card = hand.get(i);
            if(isSameColor(card, lastCard) || isSameNumber(card, lastCard) || isWildCard(card)){
                return true;
            }
        }
        return false;
    }
    //Kontrollerar om drag är giltigt
    public boolean isValidMove(Card card, Card lastCard){
        if(isSameNumber(card, lastCard) || isSameColor(card, lastCard) || isWildCard(card)){
            return true;
        }
        return false;
    }

    public GameSettings getRules(){
        return rules;
    }

    //Kontrollerar hur många kort nästa spelare i ordningen ska dra.
    public int checkNumberOfCardsToDraw(Deck playedCards){
        int numberOfCards = 0;
        int index = playedCards.getSize()-1;
        while(true){
            if(playedCards.getCardAtPosition(index).getSymbol().equals(Symbols.PLUSFOUR) || playedCards.getCardAtPosition(index).getSymbol().equals(Symbols.PLUSTWO)) {
            numberOfCards += nummberOfCardsToPickUp(playedCards.getCardAtPosition(index));
            index--;
            }else{
                break;
            }

        }
        return numberOfCards;
    }
    // Kontrollerar om första kortet är spelbart
    public boolean isValidStartCard(Card card){
        String cardSymbol = card.getSymbol();
        if(cardSymbol == Symbols.CHANGEDIRECTION || cardSymbol == Symbols.SKIP ||cardSymbol == Symbols.PLUSTWO || cardSymbol == Symbols.CHANGECOLOR ||cardSymbol == Symbols.PLUSFOUR){
            return false;
        }return true;
    }

    // Regler för om man spelar flera kort på en gång
    public boolean isValidMultiMove(Card card, Card lastCard){
        if(isSameColor(card, lastCard) && isSameNumber(card, lastCard)){
            return true;
        }
        if (isSameNumber(card, lastCard) && isWildCard(card) && isWildCard(lastCard)){
            return true;
        }
        if(isWildCard(card)&&isWildCard(lastCard)){
            return true;
        }
        if(isWildCard(lastCard) && isSameColor(card, lastCard)){
            return true;
        }
        if(isSameNumber(card, lastCard)){
            return true;
        }
        return false;
    }

    public boolean isSameColor(Card card, Card earlyCard){
        if(card.getColor().equals(earlyCard.getColor())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isSameNumber(Card card, Card earlyCard){
        if(card.getSymbol().equals(earlyCard.getSymbol())){
            return true;
        }else{
            return false;
        }
    }

    public int nummberOfCardsToPickUp(Card card){
        if(card.getSymbol().equals(Symbols.PLUSTWO)){
            return 2;
        }
        if(card.getSymbol().equals(Symbols.PLUSFOUR)){
            return 4;
        }
        return 0;
    }

    public boolean ColoredSpecialCard(Card card, Card earlyCard){ //Checks if card is has the same card symbol or same color.
        if (card.getColor().equals(earlyCard.getColor()) || card.getSymbol().equals(earlyCard.getSymbol())){
            return true;
        }
        return false;
    }
    public boolean isWildCard(Card card){
        if(card.getSymbol().equals(Symbols.PLUSFOUR) || card.getSymbol().equals(Symbols.CHANGECOLOR)){
            return true;
        }
        return false;
    }

    public boolean cardIsChangeDirection(Card card){
        if(card.getSymbol().equals(Symbols.CHANGEDIRECTION)){return true;}
        return false;
    }
    public boolean cardIsSkip(Card card){
        if(card.getSymbol().equals(Symbols.SKIP)){return true;}
        return false;
    }

    public int checkNextPlayer(int i, boolean clockwise, LinkedList<Player> players){
        if(i == players.size()-1 && clockwise){
            i= -1;
        }
        if(i == 0 && clockwise==false){
            i = players.size();
        }
        if(clockwise){
            i++;
        }else{
            i--;
        }
        return i;
    }

}
