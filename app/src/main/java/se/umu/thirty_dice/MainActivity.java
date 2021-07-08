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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.Console;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // An instance of the GameLogic that handles most of the logic of the game
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
        infoText = (TextView) findViewById(R.id.textViewInfo);
        prevRounds = (TextView) findViewById(R.id.prev_rounds);
        rounds = (TextView) findViewById(R.id.rounds);

        // Reference to the buttons/dropdown
        Spinner round_choice_spinner = findViewById(R.id.spinner);
        Button btnSaveDice = findViewById(R.id.save_paired_dice);
        Button btnSubmitRound = findViewById(R.id.submit_round);
        Button btnThrow = findViewById(R.id.throw_dice);
        Button btnChooseDice = findViewById(R.id.choose_dice);

        // Setup the dropdown/spinner
        dropdownArray = getResources().getStringArray(R.array.round_choice);
        gameKindArray = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dropdownArray);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        round_choice_spinner.setAdapter(gameKindArray);

        // Set the listener to the buttons and dropdown menu
        setButtonsAndDropdownListener(round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, diceArray, btnChooseDice);

        if (mGameLogicViewModel.isGameStarted()) {
            rounds.setText(mGameLogicViewModel.getRoundString());
            updateDiceGUI(mGameLogicViewModel.getDiceArray(), diceArray);

            String currentState = mGameLogicViewModel.getCurrentState();
            Log.d(logcatTag, mGameLogicViewModel.getCurrentState());
            updateButtonsGUI(currentState, round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, btnChooseDice);
        }

        Log.d(logcatTag, "gameroundcounter" + mGameLogicViewModel.getGameRoundCounter() + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rounds.setText(mGameLogicViewModel.getRoundString());
    }

    /**
     * Setting the listeners for the buttons and the drowdown
     *
     * @param round_choice_spinner
     * @param btnSaveDice
     * @param btnSubmitRound
     * @param btnThrow
     */
    private void setButtonsAndDropdownListener(Spinner round_choice_spinner, Button btnSaveDice, Button btnSubmitRound, Button btnThrow, ImageButton[] imageButtonDiceArray, Button btnChooseDice) {
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
            }
        });


        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------

        // Button SAVE PAIRED DICE
        btnSaveDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGameLogicViewModel.calcPoints()) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.invalid_dice_choice), Toast.LENGTH_SHORT).show();
                }
                updateDiceGUI(mGameLogicViewModel.getDiceArray(), imageButtonDiceArray);
            }
        });

        // Button SUBMIT ROUND
        btnSubmitRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGameLogicViewModel.setCurrentState("inRoundThrowsLeft");
                updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, btnChooseDice);

                mGameLogicViewModel.resetThrowCounter();
                mGameLogicViewModel.resetIsDiceSaved();
                mGameLogicViewModel.resetIsDiceLocked();
                String roundString = mGameLogicViewModel.addToRoundString(dropdownArray[mGameLogicViewModel.getCurrentRound()] + " ");
                rounds.setText(roundString);

                updateDiceGUI(mGameLogicViewModel.getDiceArray(), imageButtonDiceArray);

                if (mGameLogicViewModel.isGameOver()) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_GAMEROUNDS_ARRAYLIST, mGameLogicViewModel.getGameRounds());
                    startActivity(intent);
                }
            }
        });

        // Button CHOOSE DICE
        btnChooseDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mGameLogicViewModel.isGameStarted()) return;


                mGameLogicViewModel.setCurrentRoundChoice(currentRoundChoice[0]);

                mGameLogicViewModel.setCurrentState("pairDice");
                updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, btnChooseDice);

            }
        });

        // ---------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Button THROW
        btnThrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameLogicViewModel.setGameStarted(true);

                mGameLogicViewModel.setCurrentState("inRoundThrowsLeft");
                updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, btnChooseDice);
                Dice[] diceArray = mGameLogicViewModel.throwAllDice();
                updateDiceGUI(diceArray, imageButtonDiceArray);

                if (mGameLogicViewModel.isLastThrow()) {
                    mGameLogicViewModel.setCurrentState("inRoundNoThrowsLeft");
                    updateButtonsGUI(mGameLogicViewModel.getCurrentState(), round_choice_spinner, btnSaveDice, btnSubmitRound, btnThrow, btnChooseDice);
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
                    if (!mGameLogicViewModel.isGameStarted())
                        return; // If the game is not started, do nothing when a Dice is pressed...

                    Dice currentDice = mGameLogicViewModel.getCurrentDice(finalI);
                    if (currentDice.isLocked()) return;
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
        if (d.isLocked()) {
            switch (d.getDots()) {
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
                    Log.d("fail", "Switch case failed, dice.dots should be 1-6");
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
                    Log.d("fail", "Switch case failed, dice.dots should be 1-6");
                    return -1;
            }
        }
    }

    /**
     * Update the buttons and the textfields depending on the currentState
     *
     * @param currentState
     */
    private void updateButtonsGUI(String currentState, Spinner round_choice_spinner, Button btnSaveDice, Button btnSubmitRound, Button btnThrow, Button btnChooseDice) {
        switch (currentState) {

            case "inRoundThrowsLeft":
                Log.d(logcatTag, "case: inRoundThrowsLeft");
                infoText.setText(getResources().getString(R.string.info_save_dice));
                btnSaveDice.setEnabled(false);
                btnSubmitRound.setEnabled(false);
                btnThrow.setEnabled(true);
                round_choice_spinner.setEnabled(true);
                break;

            case "inRoundNoThrowsLeft":
                infoText.setText(getResources().getString(R.string.info_choose_dropdown));
                btnThrow.setEnabled(false);
                break;

            case "pairDice":
                btnSaveDice.setEnabled(true);
                btnSubmitRound.setEnabled(true);
                btnThrow.setEnabled(false);
                round_choice_spinner.setEnabled(false);
                infoText.setText(getResources().getString(R.string.info_pair_dice));
                break;

            default:
                Log.d("error", getResources().getString(R.string.state_not_allowed));
        }
    }
}