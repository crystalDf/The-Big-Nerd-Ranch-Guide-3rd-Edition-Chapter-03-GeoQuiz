package com.star.geoquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class QuizActivity extends AppCompatActivity {

    private static final int NUMBER_OF_QUESTION = 6;

    private static final String KEY_INDEX = "index";
    private static final String KEY_CORRECT = "correct";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_ALL_ANSWERED = "allAnswered";

    private Button mTrueButton;
    private Button mFalseButton;

    private TextView mQuestionTextView;

    private Button mNextButton;
    private Button mPrevButton;

    private ImageButton mPrevImageButton;
    private ImageButton mNextImageButton;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    private boolean[] mCorrect = new boolean[NUMBER_OF_QUESTION];
    private boolean[] mAnswered = new boolean[NUMBER_OF_QUESTION];
    private boolean mAllAnswered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mCorrect = savedInstanceState.getBooleanArray(KEY_CORRECT);
            mAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            mAllAnswered = savedInstanceState.getBoolean(KEY_ALL_ANSWERED);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(v -> getNextQuestion());

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(v -> checkAnswer(true));

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(v -> checkAnswer(false));

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(v -> getNextQuestion());

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(v -> getPrevQuestion());

        mNextImageButton = (ImageButton) findViewById(R.id.next_image_button);
        mNextImageButton.setOnClickListener(v -> getNextQuestion());

        mPrevImageButton = (ImageButton) findViewById(R.id.prev_image_button);
        mPrevImageButton.setOnClickListener(v -> getPrevQuestion());

        updateQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_CORRECT, mCorrect);
        outState.putBooleanArray(KEY_ANSWERED, mAnswered);
        outState.putBoolean(KEY_ALL_ANSWERED, mAllAnswered);
    }

    private void getNextQuestion() {
        if (mAllAnswered) {
            return;
        }

        while (true) {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            if (!mAnswered[mCurrentIndex]) {
                break;
            }
        }

        updateQuestion();
    }

    private void getPrevQuestion() {
        if (mAllAnswered) {
            return;
        }

        while (true) {
            mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
            if (!mAnswered[mCurrentIndex]) {
                break;
            }
        }

        updateQuestion();
    }

    private void updateQuestion() {
        int questionResId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionResId);

        mTrueButton.setEnabled(!mAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mAnswered[mCurrentIndex]);
    }

    private void checkAnswer(boolean userPressedTrue) {
        if (mAnswered[mCurrentIndex]) {
            return;
        }

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        mCorrect[mCurrentIndex] = (userPressedTrue == answerIsTrue);

        int messageResId = (userPressedTrue == answerIsTrue) ?
                R.string.correct_toast : R.string.incorrect_toast;

        Toast.makeText(QuizActivity.this, messageResId,
                Toast.LENGTH_SHORT).show();

        updateAnswered();

        if (mAllAnswered) {

            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mPrevImageButton.setEnabled(false);
            mNextImageButton.setEnabled(false);

            Toast.makeText(QuizActivity.this,
                    "Score: " + new DecimalFormat("######0.00").format(getScore()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAnswered() {
        mAnswered[mCurrentIndex] = true;

        mAllAnswered = true;

        for (boolean answered : mAnswered) {
            if (!answered) {
                mAllAnswered = false;
                break;
            }
        }
    }

    private double getScore() {
        int numberOfCorrect = 0;

        for (boolean correct : mCorrect) {
            if (correct) {
                numberOfCorrect++;
            }
        }

        Toast.makeText(QuizActivity.this,
                "Number of Correct Answer: " + numberOfCorrect,
                Toast.LENGTH_SHORT).show();

        return ((double) numberOfCorrect) / NUMBER_OF_QUESTION * 100;
    }
}
