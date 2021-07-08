package se.umu.thirty_dice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private GameLogicViewModel mGameLogicViewModel;
    private final String logcatTag = "MainActivity";

    private TextView infoText;
    private TextView prevRounds;
    private TextView rounds;

    private String[] dropdownArray;
    ArrayAdapter<String> gameKindArray;

    private static final String EXTRA_GAMEROUNDS_ARRAYLIST = "se.umu.thirty_dice.gamerounds_arraylist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Gets the ViewModel for this Activity
        mGameLogicViewModel = new ViewModelProvider(this).get(GameLogicViewModel.class);
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
        infoText = findViewById(R.id.textViewInfo);
        prevRounds = findViewById(R.id.prev_rounds);
        rounds = findViewById(R.id.rounds);

        // Reference to the buttons/dropdown
        Spinner round_choice_spinner = findViewById(R.id.spinner);
        Button btnThrow = findViewById(R.id.throw_dice);
        Button btnCalculateSubmit = findViewById(R.id.calculate_submit);

        // Setup the dropdown/spinner
        dropdownArray = getResources().getStringArray(R.array.round_choice);
        gameKindArray = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, dropdownArray);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        round_choice_spinner.setAdapter(gameKindArray);

        // Set the listener to the buttons and dropdown menu
        setButtonsAndDropdownListener(round_choice_spinner, btnThrow, diceArray, btnCalculateSubmit);

        if (mGameLogicViewModel.isGameStarted()) {
            rounds.setText(mGameLogicViewModel.getRoundString());
            updateDiceGUI(mGameLogicViewModel.getDiceArray(), diceArray);
            String currentState = mGameLogicViewModel.getCurrentState();
            updateButtonsGUI(currentState, round_choice_spinner, btnThrow, btnCalculateSubmit);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rounds.setText(mGameLogicViewModel.getRoundString());
    }

    /**
     * Set the listeners for the buttons and the spinner
     *
     * @param round_choice_spinner
     * @param btnThrow
     * @param imageButtonDiceArray
     * @param btnCalculateSubmit
     */
    private void setButtonsAndDropdownListener(Spinner round_choice_spinner, Button btnThrow, ImageButton[] imageButtonDiceArray, Button btnCalculateSubmit) {
        final String[] currentRoundChoice = {""};

        // Dropdown listener, setting currentRoundChoice in gameLogic
        round_choice_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mGameLogicViewModel.isGameStarted()) {
                    currentRoundChoice[0] = parent.getItemAtPosition(position).toString();
                    mGameLogicViewModel.resetIsDiceSaved();
                    mGameLogicViewModel.setCurrentRound(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not implemented, will never occur
            }
        });

        // Button THROW
        btnThrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameLogicViewModel.setGameStarted(true);

                mGameLogicViewModel.setCurrentState("inRoundThrowsLeft");
                updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnThrow, btnCalculateSubmit);
                Dice[] diceArray = mGameLogicViewModel.throwAllDice();
                updateDiceGUI(diceArray, imageButtonDiceArray);

                if (mGameLogicViewModel.isLastThrow()) {
                    mGameLogicViewModel.setCurrentState("inRoundNoThrowsLeft");
                    updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnThrow, btnCalculateSubmit);
                }
            }
        });


        // Button CALCULATE SCORE & SUBMIT
        btnCalculateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mGameLogicViewModel.isGameStarted()) return;

                // Set the current round (Low, 4-12) then calculate the score
                mGameLogicViewModel.setCurrentRoundChoice(currentRoundChoice[0]);
                mGameLogicViewModel.calcPoints();

                // If we have played 10 rounds, show the ResultsActivity with the result
                if (mGameLogicViewModel.isGameOver()) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_GAMEROUNDS_ARRAYLIST, mGameLogicViewModel.getGameRounds());
                    startActivity(intent);
                }

                mGameLogicViewModel.resetThrowCounter();
                mGameLogicViewModel.resetIsDiceSaved();

                // Update the GUI, based on current "state"
                mGameLogicViewModel.setCurrentState("inRoundThrowsLeft");
                String roundString = mGameLogicViewModel.addToRoundString(dropdownArray[mGameLogicViewModel.getCurrentRound()] + " ");
                rounds.setText(roundString);
                updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnThrow, btnCalculateSubmit);
                updateDiceGUI(mGameLogicViewModel.getDiceArray(), imageButtonDiceArray);
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
            imageButtonDiceArray[i].setImageResource(getImageResource(diceArray[i]));
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
                    // If the game is not started, do nothing when a Dice is pressed...
                    if (!mGameLogicViewModel.isGameStarted()) return;

                    Dice currentDice = mGameLogicViewModel.getCurrentDice(finalI);
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
        imageButton.setImageResource(getImageResource(d));
    }


    /**
     * Keeps track of the images for the dice
     *
     * @return
     */
    public int getImageResource(Dice d) {
        if (!d.getIsSaved()) {
            switch (d.getDots()) {
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
                    Log.d("fail", getResources().getString(R.string.get_dice_image_fail));
                    return -1;
            }
        } else {
            switch (d.getDots()) {
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
                    Log.d("fail", getResources().getString(R.string.get_dice_image_fail));
                    return -1;
            }
        }
    }

    /**
     * Update the buttons and the textfields depending on the currentState
     *
     * @param currentState
     */
    private void updateButtonsGUI(String currentState, Spinner round_choice_spinner, Button btnThrow, Button btnCalculateSubmit) {
        switch (currentState) {
            case "inRoundThrowsLeft":
                infoText.setText(getResources().getString(R.string.info_save_dice));
                btnThrow.setEnabled(true);
                round_choice_spinner.setEnabled(true);
                break;
            case "inRoundNoThrowsLeft":
                infoText.setText(getResources().getString(R.string.info_choose_dropdown));
                btnThrow.setEnabled(false);
                break;
            default:
                Log.d("error", getResources().getString(R.string.state_not_allowed));
        }
    }
}