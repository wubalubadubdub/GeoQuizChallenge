package com.example.rokabr.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// Controller Class

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private int mCheatIndex = 0;
    private boolean mIsCheater = false;
    private static final String USER_CHEAT_STATUS = "com.example.rokabr.geoquiz.user_cheat_status";
    private static final String USER_CHEAT_INDEX = "com.example.rokabr.geoquiz.user_cheat_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                // if the user has hit next until they are back at the original question, and
                // they were a cheater before, they are still a cheater. we're checking to see
                // whether mCheatIndex (index when the user cheated) and mCurrentIndex are the same.

                updateQuestion();
            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Start CheatActivity

                Intent i = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex]
                        .isAnswerTrue());
                startActivityForResult(i, REQUEST_CODE_CHEAT);
                // the above starts the CheatActivity and passes over a request code that tells
                // the parent activity which child activity is sending data back
            }
        });

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mIsCheater = savedInstanceState.getBoolean(USER_CHEAT_STATUS);
            mCheatIndex = savedInstanceState.getInt(USER_CHEAT_INDEX);
        }


        updateQuestion(); // this is so the Question gets displayed initially, before
        // user has clicked any buttons
    }

    // this method is called by the OS when the user presses the Back button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) { // if user clicked the Back button b4 Show Answer
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }

            mIsCheater = CheatActivity.wasAnswerShown(data);
        }

        if (mIsCheater) {
            mCheatIndex = mCurrentIndex;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex); // check for this value in OnCreate()
        savedInstanceState.putBoolean(USER_CHEAT_STATUS, mIsCheater);
        savedInstanceState.putInt(USER_CHEAT_INDEX, mCheatIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateQuestion() {
        // getTextResId() call is needed because the resource ID changes for
        // each question, i.e. it's different for question_oceans vs. question_mideast
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue(); // returns T or F

        int messageResId; // used by the Toast to determine what message to display

        if (mIsCheater) {
            if (mCheatIndex == mCurrentIndex) { // same question they last got an answer to. Cheater!
                messageResId = R.string.judgment_toast;
            }
            else {
                mIsCheater = false; // reset cheater status since user has now answered a
                // different question
                if (userPressedTrue == answerIsTrue) { // user answered correctly
                    messageResId = R.string.correct_toast;
                } else { // user answered incorrectly
                    messageResId = R.string.incorrect_toast;
                }

            }

        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }

        }


        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


}
