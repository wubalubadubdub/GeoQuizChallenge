package com.example.rokabr.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.example.rokabr.geoquiz.answer_is_true"; // key for the extra that
    // will be passed in the Intent.putExtra(String name, <T> value) method.
    private static final String EXTRA_ANSWER_SHOWN =
            "com.example.rokabr.geoquiz.answer_shown";



    private boolean mAnswerIsTrue;
    private boolean mAnswerWasShown; // used for storing the value in case user rotates device
    private TextView mAnswerTextView;
    private TextView mApiTextView;
    private Button mShowAnswerButton;

    // this method is called by a parent activity, the one that wants to spawn this activity.
    // QuizActivity will include whether the current question is true or false in this call.
    // this will get called when the user clicks the cheat button, regardless of whether they
    // then choose to click the Show Answer button or not.
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);



        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        // retrieves the value of true/false for the current question from the Intent
        // that QuizActivity sends when user clicks the Cheat! button

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mApiTextView = (TextView) findViewById(R.id.api_text_view);
        mApiTextView.setText("API level " + Integer.toString(Build.VERSION.SDK_INT));
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the answer to the user
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }

                mAnswerWasShown = true; // used for storing the value in case user rotates device

                setAnswerShownResult(true); // creates an intent for the parent activity
                // to receive and puts an extra containing the value "true" on the Intent
                // since the user has clicked the Show Answer button

                // if we're running on API 21 or higher, perform the animation
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy,
                            radius, 0); // x and y values of center, start, and ending radius
                    // create a listener which allows you to know when the animation is complete
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // show the answer and hide the button after animation over
                            mAnswerTextView.setVisibility(View.VISIBLE);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();

                } else {  // otherwise just jump to setting the visibility without an animation
                    mAnswerTextView.setVisibility(View.VISIBLE);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        // this method needs to come after the variable declarations above to avoid a null pointer
        // exception being thrown
        if (savedInstanceState != null) {
            mAnswerWasShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN);
            setAnswerShownResult(mAnswerWasShown);

            mAnswerIsTrue = savedInstanceState.getBoolean(EXTRA_ANSWER_IS_TRUE);
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(EXTRA_ANSWER_SHOWN, getAnswerShownResult());
        savedInstanceState.putBoolean(EXTRA_ANSWER_IS_TRUE, mAnswerIsTrue);

    }

    // this method lets QuizActivity know if the answer was shown or not.
    // becomes true if user clicks the Show Answer button
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    // this method will be called by QuizActivity. it unpacks the "data" from the
    // Intent that was created in the method above to get a true/false value for whether the user
    // cheated or not.
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    // used for storing the value in case user rotates device
    private boolean getAnswerShownResult() {
        return mAnswerWasShown;
    }


}
