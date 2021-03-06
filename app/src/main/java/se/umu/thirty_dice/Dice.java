package se.umu.thirty_dice;

import java.util.Random;

/**
 * Class to represent a Dice object
 */
public class Dice {

    private int dots;
    private boolean isSaved;

    private String color = "white";
    private int imageResource = R.drawable.white1;

    public Dice() {
        isSaved = false;
        dots = 1;
    }

    /**
     * @return If the user have clicked a dice to save, keep the current number of dots,
     * else generate a random value from 0-6
     */
    public int diceThrow() {
        Random rand = new Random();
        if (isSaved) return dots;
        dots = rand.nextInt(6) + 1;
        return dots;
    }

    public int getDots() {
        return dots;
    }

    /**
     * Toggle the dice to either grey (saved) or white (not saved)
     */
    public void toggleSaved() {
        isSaved = !isSaved;
        this.color = isSaved ? "grey" : "white";
    }

    public void setIsSaved(boolean b) {
        isSaved = b;
    }

    public boolean getIsSaved() {
        return isSaved;
    }
}