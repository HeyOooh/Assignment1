package se.umu.thirty_dice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * One game round to hold the number of the current game round (i.e: "low" or "4" or "12")
 * and the score for the current game round.
 */
public class GameRound implements Parcelable {

    private String gameRound;
    private int totalScore;

    /**
     * Constructor to initialize a GameRound object with the gameRound and the score for the current round
     * @param gameRound
     */
    public GameRound(String gameRound) {
        this.gameRound = gameRound;
    }

    protected GameRound(Parcel in) {
        gameRound = in.readString();
        totalScore = in.readInt();
    }

    public static final Creator<GameRound> CREATOR = new Creator<GameRound>() {
        @Override
        public GameRound createFromParcel(Parcel in) {
            return new GameRound(in);
        }

        @Override
        public GameRound[] newArray(int size) {
            return new GameRound[size];
        }
    };

    public String getGameRound() {
        return gameRound;
    }

    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Updates the totalScore variable
     * @param score
     */
    public void updateTotalScore(int score) {
        this.totalScore += score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameRound);
        dest.writeInt(totalScore);
    }

    @Override
    public String toString() {
        return this.gameRound + ": " + this.totalScore;
    }
}
