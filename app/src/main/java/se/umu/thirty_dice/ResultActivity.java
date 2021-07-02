package se.umu.thirty_dice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ArrayList<GameRound> gameRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // The arraylist with all the gameRounds info (round, score and totalScore) to be displayed in this Activity
        this.gameRounds = getIntent().getParcelableArrayListExtra("se.umu.thirty_dice.gamerounds_arraylist");

        // Getting references to the Views needed
        TextView resultText = (TextView) findViewById(R.id.results);
        TextView totalScoreText = (TextView) findViewById(R.id.totalScore);

        // Creating and setting the score for each round, and the total score for all the rounds
        resultText.setText(getResultString(gameRounds));
        totalScoreText.setText("Total score: " + getTotalScore(gameRounds));
    }

    /**
     * Helper method to calculate the total score of all the rounds
     * @param gameRounds
     * @return
     */
    private String getTotalScore(ArrayList<GameRound> gameRounds) {
        int totalScore = 0;
        for (GameRound current: gameRounds) {
            totalScore += current.getTotalScore();
        }

        return totalScore + "";
    }

    /**
     * To create the result String to be displayed, eg,
     * Round | Score
     * Low   | 5
     * 9     | 18
     * @param gameRounds
     * @return
     */
    private String getResultString(ArrayList<GameRound> gameRounds) {
        String format = "%1$-5s|%2$5s\n";
        String results = "";
        for (GameRound current: gameRounds) {
            String currGameRoundFormatted = String.format(format, current.getGameRound(), current.getTotalScore() + "");
            // String currTotalScoreFormatted = String.format("% d", current.getTotalScore());
            results += currGameRoundFormatted;
        }

        return results;
    }
}