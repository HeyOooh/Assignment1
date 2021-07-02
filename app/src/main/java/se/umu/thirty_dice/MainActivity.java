package se.umu.thirty_dice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // An instance of the GameLogic that handles most of the logic of the game
    private final GameLogic gameLogic = new GameLogic();
    private final String logcatTag = "MainActivity";

    private TextView infoText;
    private TextView prevRounds;
    private TextView rounds;

    private String[] dropdownArray;
    ArrayAdapter<String> gameKindArray;
    String roundsString = "";

    private static final String EXTRA_GAMEROUNDS_ARRAYLIST= "se.umu.thirty_dice.gamerounds_arraylist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to the dice
        ImageButton btnDice0 = findViewById(R.id.dice0);
        ImageButton btnDice1 = findViewById(R.id.dice1);
        ImageButton btnDice2 = findViewById(R.id.dice2);
        ImageButton btnDice3 = findViewById(R.id.dice3);
        ImageButton btnDice4 = findViewById(R.id.dice4);
        ImageButton btnDice5 = findViewById(R.id.dice5);

        // Set the Dice listeners
        ImageButton[] diceArray = {btnDice0, btnDice1, btnDice2, btnDice3, btnDice4, btnDice5};
        setDiceListeners(diceArray);

        // Reference to the TextView
        infoText = (TextView) findViewById(R.id.textViewInfo);
        prevRounds = (TextView) findViewById(R.id.prev_rounds);
        rounds = (TextView) findViewById(R.id.rounds);

        // Reference to the buttons/dropdown
        Spinner round_choice_spinner = findViewById(R.id.spinner);
        Button btnSaveDice = findViewById(R.id.save_paired_dice);
        Button btnSubmitRound = findViewById(R.id.submit_round);
        Button btnThrow = findViewById(R.id.throw_dice);


        // Setup the dropdown/spinner
        dropdownArray = getResources().getStringArray(R.array.round_choice);
        gameKindArray = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dropdownArray);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        round_choice_spinner.setAdapter(gameKindArray);

        // Set the listener to the buttons and dropdown menu
        setButtonsAndDrowdownListener(round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, diceArray);
    }

    /**
     * Setting the listeners for the buttons and the drowdown
     *
     * @param round_choice_spinner
     * @param btnSaveDice
     * @param btnSubmitRound
     * @param btnThrow
     */
    private void setButtonsAndDrowdownListener(Spinner round_choice_spinner, Button btnSaveDice, Button btnSubmitRound, Button btnThrow, ImageButton[] imageButtonDiceArray) {

        // Dropdown listener, setting currentRoundChoice in gameLogic
        round_choice_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (gameLogic.isGameStarted()) {

                    btnSaveDice.setEnabled(true);
                    btnSubmitRound.setEnabled(true);
                    btnThrow.setEnabled(false);
                    round_choice_spinner.setEnabled(false);

                    gameLogic.setCurrentRoundChoice(parent.getItemAtPosition(position).toString());
                    gameLogic.resetIsDiceSaved();

                    infoText.setText(getResources().getString(R.string.info_pair_dice));
                    roundsString += dropdownArray[position] + " ";
                    rounds.setText(roundsString);

                    updateDiceGUI(gameLogic.getDiceArray(), imageButtonDiceArray);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(logcatTag, "round_choice_spinner:" + " nothing selected");
            }
        });

        // Button save dice listener
        btnSaveDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameLogic.calcPoints()) {
                    Toast.makeText(MainActivity.this, "Invalid dice choice for this round", Toast.LENGTH_SHORT).show();
                }
                updateDiceGUI(gameLogic.getDiceArray(), imageButtonDiceArray);
            }
        });

        // Button submit round
        btnSubmitRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnThrow.setEnabled(true);
                btnSaveDice.setEnabled(false);
                btnSubmitRound.setEnabled(false);
                round_choice_spinner.setEnabled(true);

                gameLogic.resetThrowCounter();
                gameLogic.resetIsDiceSaved();
                gameLogic.resetIsDiceLocked();
                updateDiceGUI(gameLogic.getDiceArray(), imageButtonDiceArray);

                // If it is the last round (10) all the results from each round is send with an intent
                // to the result activity and is shown
                if(gameLogic.getGameRoundCounter() == 10) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putParcelableArrayListExtra (EXTRA_GAMEROUNDS_ARRAYLIST, gameLogic.getGameRounds());
                    startActivity(intent);
                }
            }
        });

        // Button throw listener
        btnThrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLogic.setGameStarted(true);
                infoText.setText(getResources().getString(R.string.info_save_dice));
                Dice[] diceArray = gameLogic.throwAllDice();
                updateDiceGUI(diceArray, imageButtonDiceArray);
                if (gameLogic.increaseThrowCounter() == 3) {
                    infoText.setText(getResources().getString(R.string.info_choose_dropdown));
                    btnThrow.setEnabled(false);
                }
            }
        });
    }

    /**
     * Helper method to update the Dice images
     *
     * @param diceArray
     * @param imageButtonDiceArray
     */
    private void updateDiceGUI(Dice[] diceArray, ImageButton[] imageButtonDiceArray) {
        for (int i = 0; i < diceArray.length; i++) {
            imageButtonDiceArray[i].setImageResource(diceArray[i].getImageResource());
        }
    }

    /**
     * Setting listeners to all the dice
     *
     * @param imageButtonDiceArray
     */
    private void setDiceListeners(ImageButton[] imageButtonDiceArray) {

        for (int i = 0; i < imageButtonDiceArray.length; i++) {
            int finalI = i;
            imageButtonDiceArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!gameLogic.isGameStarted()) return; // If the game is not started, do nothing when a Dice is pressed...

                    Dice currentDice = gameLogic.getCurrentDice(finalI);
                    if(currentDice.isLocked()) return;
                    currentDice.toggleSaved();
                    setDiceImageResource(currentDice, imageButtonDiceArray[finalI]);
                }
            });
        }
    }

    /**
     * Sets the dice that is pressed to gray
     *
     * @param d
     * @param imageButton
     */
    private void setDiceImageResource(Dice d, ImageButton imageButton) {
        imageButton.setImageResource(d.getImageResource());
    }
}
