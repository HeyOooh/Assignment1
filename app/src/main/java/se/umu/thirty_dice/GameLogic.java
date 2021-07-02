package se.umu.thirty_dice;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Class to handle most of the logic of the game
 */
public class GameLogic {

    private final String logcatTag = "GameLogic";


    private Dice[] diceArray = {new Dice(), new Dice(), new Dice(), new Dice(), new Dice(), new Dice()};
    private int throwCounter;
    private boolean isGameStarted = false;

    // ArrayList to hold all the gameRound, to be used in for the results
    ArrayList<GameRound> gameRounds = new ArrayList<>();

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    /**
     * Setting the current round chosen by adding it to an array, when the user chose from the dropdown menu
     *
     * @param currentRoundChoice
     */
    public void setCurrentRoundChoice(String currentRoundChoice) {
        this.gameRounds.add(new GameRound(currentRoundChoice));
    }

    public ArrayList<GameRound> getGameRounds() {
        return this.gameRounds;
    }

    /**
     * Adds a gameRound to the gameRounds ArrayList and increments the gameRoundCounter
     *
     * @param gameRound
     */
    public void addGameRound(GameRound gameRound) {
        this.gameRounds.add(gameRound);
    }

    public int getGameRoundCounter() {
        return this.gameRounds.size();
    }

    /**
     * The dice that is pressed os saved and will the boolean that maps to that dice in the array is set to true
     *
     * @param diceNumber
     * @return
     */
    public void setIsDiceSaved(int diceNumber) {
        diceArray[diceNumber].toggleSaved();
    }

    /**
     * Resets the isDiceSaved boolean array to all false, when a new round starts
     */
    public void resetIsDiceSaved() {
        for (Dice d : diceArray) d.setIsSaved(false);
    }

    /**
     * Resets the locked state in the for every Dice
     */
    public void resetIsDiceLocked() {
        for (Dice d : diceArray) d.setLocked(false);
    }

    public Dice[] getDiceArray() {
        return this.diceArray;
    }

    public Dice getCurrentDice(int current) {
        return this.diceArray[current];
    }

    /**
     * If a player presses the Throw button, all the dice in the diceArray are thrown
     */
    public Dice[] throwAllDice() {
        for (Dice d : diceArray) {
            d.diceThrow();
        }
        return diceArray;
    }

    /**
     * Increments the throwcounter with 1
     * @return
     */
    public int increaseThrowCounter() {
        return ++this.throwCounter;
    }

    /**
     * Resets the throwcounter to 0
     */
    public void resetThrowCounter() {
        this.throwCounter = 0;
    }

    /**
     * Method to calc the scores, if the chosen dice is not valid for the current round, a Toast is shown to indicate the choice is invalid
     */
    public boolean calcPoints() {
        GameRound currentGameRound = gameRounds.get(gameRounds.size() - 1);

        // The case "Low" is not similar to the rest of the choices so it is handled directly here
        switch (currentGameRound.getGameRound()) {
            case "Low":
                for (Dice d : diceArray) {
                    if (d.getIsSaved() && d.getDots() <= 3) {
                        currentGameRound.updateTotalScore(d.getDots());
                        d.setLocked(true);
                    }
                }
                return true;

                // Case 4-12 are all similar and are handled in the calculateScore method
            case "4":
                return calculateScore(4, currentGameRound);
            case "5":
                return calculateScore(5, currentGameRound);
            case "6":
                return calculateScore(6, currentGameRound);
            case "7":
                return calculateScore(7, currentGameRound);
            case "8":
                return calculateScore(8, currentGameRound);
            case "9":
                return calculateScore(9, currentGameRound);
            case "10":
                return calculateScore(10, currentGameRound);
            case "11":
                return calculateScore(11, currentGameRound);
            case "12":
                return calculateScore(12, currentGameRound);
            default:
                Log.d("success", "Switch case failed, dice.dots should be 1-6");
                return false;
        }
    }

    /**
     * Helper method to calculate the score, given the chosen round, for the rounds 4-12
     * @param chosenRound
     * @param currentGameRound
     * @return
     */
    private boolean calculateScore(int chosenRound, GameRound currentGameRound) {
        int totalDots = 0;
        for (Dice d : diceArray) {
            if (d.getIsSaved()) {
                totalDots += d.getDots();
            }
        }

        if (totalDots == chosenRound) {
            currentGameRound.updateTotalScore(chosenRound);

            for (Dice d : diceArray) {
                if (d.getIsSaved()) {
                    d.setLocked(true);
                    d.setIsSaved(false);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}