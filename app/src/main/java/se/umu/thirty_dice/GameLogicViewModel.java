package se.umu.thirty_dice;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

/**
 * Class to handle most of the logic of the game
 */
public class GameLogicViewModel extends ViewModel {

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
    private int targetSum;

    // ---------------- Getters and setters ------------------
    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setCurrentRound(int current) {
        this.currentRound = current;
    }

    /**
     * @return If the game is over or not, used to show the resultActivity after the tenth round
     */
    public boolean isGameOver() {
        return this.gameRounds.size() >= this.MAX_ROUNDS;
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

    public Dice[] getDiceArray() {
        return this.diceArray;
    }

    public Dice getCurrentDice(int current) {
        return this.diceArray[current];
    }
    // -------------------------------------------------------

    /**
     * Creates the string that is shown for the previous round, etc: 4 12 Low 9
     * @param currentRound
     * @return
     */
    public String addToRoundString(String currentRound) {
        this.roundsString += currentRound;
        return this.roundsString;
    }

    /**
     * Resets the isDiceSaved boolean array to all false, when a new round starts
     */
    public void resetIsDiceSaved() {
        for (Dice d : diceArray) d.setIsSaved(false);
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
     * Increments the throwcounter and checks if it is the last (3) throw
     *
     * @return
     */
    public boolean isLastThrow() {
        return ++this.throwCounter == MAX_THROWS;
    }

    /**
     * Method to calc the scores, if the chosen dice is not valid for the current round, a Toast is shown to indicate the choice is invalid
     */
    public void calcPoints() {
        GameRound currentGameRound = gameRounds.get(gameRounds.size() - 1);

        // Calculate the total point for the round "Low"
        if (currentGameRound.getGameRound().equals("Low")) {
            for (Dice d : diceArray) {
                if (d.getDots() <= 3) {
                    currentGameRound.updateTotalScore(d.getDots());
                }
            }
        } else {
            // Else if the current round is 4-12
            targetSum = Integer.parseInt(currentGameRound.getGameRound());

            // Create an ArrayList with the int values from the Dice array
            ArrayList<Integer> diceArrayListInt = new ArrayList<>();
            for (Dice d : diceArray) {
                diceArrayListInt.add(d.getDots());
            }

            // Update the score of the currentGameRound
            currentGameRound.updateTotalScore(score(diceArrayListInt));
        }
    }

    // ---------------------------------------------From Stack overflow, user wcochran ---------------------------------------------
    // https://stackoverflow.com/questions/62733378/how-to-calculate-the-sum-of-dice-whose-value-combined-amounts-to-an-input-number
    public int sumOfDice(ArrayList<Integer> dice) {
        int sum = 0;
        for (int d : dice)
            sum += d;
        return sum;
    }

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
    // -------------------------------------------------------------------------------------------------------------------------------
}