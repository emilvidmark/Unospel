package Uno;

public class main {


    public static void main (String argv[]) {
        int numberOfHumanPlayers = 1;    // Change how many players on the server you want. Now you + 3 bots.
        int numberOfBotPlayers = 2;
        Game g = new Game();
        g.setupNewGame(numberOfHumanPlayers, numberOfBotPlayers);
    }
}
