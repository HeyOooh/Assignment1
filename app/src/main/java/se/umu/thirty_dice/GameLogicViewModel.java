package se.umu.thirty_dice;

import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

import android.widget.Toast;

import androidx.lifecycle.ViewModel;

/**
 * Class to handle most of the logic of the game
 */
public class GameLogicViewModel extends ViewModel {

    private final String logcatTag = "GameLogic";

    // Array to keep track of all the Dice
    private Dice[] diceArray = {new Dice(), new Dice(), new Dice(), new Dice(), new Dice(), new Dice()};
    // ArrayList to hold all the gameRound, to be used in for the results
    ArrayList<GameRound> gameRounds = new ArrayList<>();

    private int throwCounter;
    private boolean isGameStarted = false;
    private String roundsString = "";
    private int currentRound;
    private String currentState = "beforeStart";
    private final int MAX_ROUNDS = 10;
    private final int MAX_THROWS = 3;

    // Getters and setters
    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * @return If the game is over or not, used to show the resultActivity after the tenth round
     */
    public boolean isGameOver() {
        return this.gameRounds.size() >= this.MAX_ROUNDS;
    }

    public void setCurrentRound(int current) {
        this.currentRound = current;
    }

    public String getRoundString() {
        return this.roundsString;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    public ArrayList<GameRound> getGameRounds() {
        return this.gameRounds;
    }

    /**
     * Setting the current round chosen by adding it to an array, when the user chose from the dropdown menu
     *
     * @param currentRoundChoice
     */
    public void setCurrentRoundChoice(String currentRoundChoice) {
        this.gameRounds.add(new GameRound(currentRoundChoice));
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

    public Dice[] getDiceArray() {
        return this.diceArray;
    }

    public Dice getCurrentDice(int current) {
        return this.diceArray[current];
    }


    public String addToRoundString(String currentRound) {
        this.roundsString += currentRound;
        return this.roundsString;
    }

    /**
     * Adds a gameRound to the gameRounds ArrayList and increments the gameRoundCounter
     *
     * @param gameRound
     */
    public void addGameRound(GameRound gameRound) {
        this.gameRounds.add(gameRound);
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

        // The case "Low" is not similar to the rest of the choices so it is handled directly in the switch case
        switch (currentGameRound.getGameRound()) {
            case "Low":
                // If one or more of the dice have a value of 4, 5 or 6
                for (Dice d : diceArray) {
                    if (d.getIsSaved() && d.getDots() > 3) {
                        return false;
                    }
                }

                // If the dice chosen is all valid (1, 2 or 3)
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

    /*
    /**
     * Helper method to calculate the score, given the chosen round, for the rounds 4-12
     *
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

    int targetSum = 1337;
    // --------------------------------------Från github-------------------------------------------------
    public int sumOfDice(ArrayList<Integer> dice) {
        int sum = 0;
        for (int d : dice)
            sum += d;
        return sum;
    }

    // Denna ska man kalla på först sen löser sig resten
    public int score(ArrayList<Integer> dice) {
        return score(dice, 0);
    }

    public int score(ArrayList<Integer> dice, int bestPrev) {
        int hi = bestPrev;
        for (int n = 1; n < (1 << dice.size()); n++) {
            ArrayList<Integer> subset = new ArrayList<>();
            ArrayList<Integer> remaining = new ArrayList<>();
            for (int i = 0; i < dice.size(); i++) {
                if ((n & (1 << i)) != 0)
                    subset.add(dice.get(i));
                else
                    remaining.add(dice.get(i));
            }
            if (sumOfDice(subset) == targetSum) {
                int s = score(remaining, bestPrev + targetSum);
                if (s > hi)
                    hi = s;
            }
        }
        return hi;
    }

    /**
     * Increments the throwcounter and checks if it is the last (3) throw
     *
     * @return
     */
    public boolean isLastThrow() {
        return ++this.throwCounter == MAX_THROWS;
    }
}