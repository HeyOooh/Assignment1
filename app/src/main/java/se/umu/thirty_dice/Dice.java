package se.umu.thirty_dice;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Random;

/**
 * Class to represent a Dice object
 */
public class Dice {

    private int dots;
    private boolean isSaved;



    private boolean isLocked;
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
        isLocked = false;
        Random rand = new Random();
        if (isSaved) return dots;
        dots = rand.nextInt(6) + 1;
        return dots;
    }

    public int getDots() {
        return dots;
    }

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

    public String getColor() {
        return color;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    /**
     * Keeps track of the images for the dice
     * @return
     */
    public int getImageResource() {
        if(this.isLocked) {
            switch (this.dots) {
                case 1:
                    return R.drawable.red1;
                case 2:
                    return R.drawable.red2;
                case 3:
                    return R.drawable.red3;
                case 4:
                    return R.drawable.red4;
                case 5:
                    return R.drawable.red5;
                case 6:
                    return R.drawable.red6;
                default:
                    Log.d("fail", "Switch case failed, dice.dots should be 1-6");
                    return -1;
            }
        }
        if (!this.isSaved) {
            switch (this.dots) {
                case 1:
                    return R.drawable.white1;
                case 2:
                    return R.drawable.white2;
                case 3:
                    return R.drawable.white3;
                case 4:
                    return R.drawable.white4;
                case 5:
                    return R.drawable.white5;
                case 6:
                    return R.drawable.white6;
                default:
                    Log.d("fail", "Switch case failed, dice.dots should be 1-6");
                    return -1;
            }
        } else {
            switch (this.dots) {
                case 1:
                    return R.drawable.grey1;
                case 2:
                    return R.drawable.grey2;
                case 3:
                    return R.drawable.grey3;
                case 4:
                    return R.drawable.grey4;
                case 5:
                    return R.drawable.grey5;
                case 6:
                    return R.drawable.grey6;
                default:
                    Log.d("fail", "Switch case failed, dice.dots should be 1-6");
                    return -1;
            }
        }
    }
}
