package ca.benwu.fingerflinger.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.utils.Logutils;

/**
 * Created by Ben Wu on 12/12/2015.
 */
public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private final String TAG_PAUSE_FRAG = "PAUSE_FRAG";
    private final String TAG_QUIT_FRAG = "QUIT_FRAG";

    private int mFromX = 0;
    private int mToX = 0;
    private int mFromY = 0;
    private int mToY = 0;

    private ViewFlipper mGameFlipper;

    private ViewGroup.LayoutParams mArrowParams;

    private Animation mSlideInTop;
    private Animation mSlideInBottom;
    private Animation mSlideInRight;
    private Animation mSlideInLeft;
    private Animation mFadeIn;

    private Animation mSlideOutTop;
    private Animation mSlideOutBottom;
    private Animation mSlideOutRight;
    private Animation mSlideOutLeft;
    private Animation mFadeOut;

    private int mScoreCount = 0;
    private int mErrorCount = 0;
    private TextView mScoreBox;

    private View mPauseButton;

    private int mTimeLimit = 1000;
    private CountDownTimer mTimer;

    private boolean mGameEnded = false;
    private boolean mGamePaused = false;
    private boolean mInDialog = false;

    // constants corresponding to index of arrays
    private final int DOWN_ARROW = 0;
    private final int LEFT_ARROW = 1;
    private final int UP_ARROW = 2;
    private final int RIGHT_ARROW = 3;

    private int mCurrentDirection = UP_ARROW;

    private int[] mImageRes = new int[] {R.drawable.arrow_down_white, R.drawable.arrow_left_white,
            R.drawable.arrow_up_white, R.drawable.arrow_right_white};

    // animations corresponding to arrows in mImageRes
    private Animation[] mInAnims;
    private Animation[] mOutAnims;

    private ProgressBar mTimeLimitBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logutils.d(TAG, "OnCreate");

        setContentView(R.layout.activity_game);

        mGameFlipper = (ViewFlipper) findViewById(R.id.gameFlipper);
        mArrowParams = findViewById(R.id.templateArrow).getLayoutParams();

        mScoreBox = (TextView) findViewById(R.id.scoreCount);
        mScoreBox.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf"));
        mScoreBox.setText("0");

        mSlideInTop = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top);
        mSlideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
        mSlideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mSlideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        mFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_normal);

        mSlideOutTop = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top);
        mSlideOutBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
        mSlideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
        mSlideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_normal);

        mInAnims = new Animation[] {mSlideInTop, mSlideInRight, mSlideInBottom, mSlideInLeft};
        mOutAnims = new Animation[] {mSlideOutTop, mSlideOutRight, mSlideOutBottom, mSlideOutLeft};

        setAnimationDurations(150);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Logutils.d(TAG, "width: " + width + ", height: " + height);
        mTimeLimitBar = (ProgressBar) findViewById(R.id.timeLimitBar);
        ViewGroup.LayoutParams params = mTimeLimitBar.getLayoutParams();
        params.height = width;
        params.width = height+100;
        mTimeLimitBar.requestLayout();

        mPauseButton = findViewById(R.id.gamePauseButton);

        // TODO: game pauses with no pause menu if click release is out of the view
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mGamePaused && !mGameEnded) {
                    openPauseMenu();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        if(mGameEnded || mGamePaused) {
            return super.onTouchEvent(event);
        }

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Logutils.d(TAG,"Action was DOWN");
                mFromX = (int) event.getAxisValue(MotionEvent.AXIS_X);
                mFromY = (int) event.getAxisValue(MotionEvent.AXIS_Y);
                break;
            case (MotionEvent.ACTION_MOVE) :
                //Logutils.d(TAG,"Action was MOVE");
                break;
            case (MotionEvent.ACTION_UP) :
                Logutils.d(TAG, "Action was UP");
                mToX = (int) event.getAxisValue(MotionEvent.AXIS_X);
                mToY = (int) event.getAxisValue(MotionEvent.AXIS_Y);
                onTouchReleased();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onTouchReleased() {
        int deltaX = mToX - mFromX;
        int deltaY = mToY - mFromY;
        int delta = (int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        boolean correctMovement = false;

        Logutils.d(TAG,
                "Motion: from (" + mFromX + ", " + mFromY + ") " +
                        "to (" + mToX + ", " + mToY + "), " +
                        "Change: (" + deltaX + ", " + deltaY + ")");

        if(delta < 20) {
            Logutils.d(TAG, "No move");
            mGameFlipper.setInAnimation(mFadeIn);
            mGameFlipper.setOutAnimation(mFadeOut);
        } else if(Math.abs(deltaX) > Math.abs(deltaY)) {
            if(deltaX > 0) {
                Logutils.d(TAG, "Drag right");
                mGameFlipper.setInAnimation(mSlideInLeft);
                mGameFlipper.setOutAnimation(mSlideOutRight);
                correctMovement = mCurrentDirection == RIGHT_ARROW;
            } else {
                Logutils.d(TAG, "Drag left");
                mGameFlipper.setInAnimation(mSlideInRight);
                mGameFlipper.setOutAnimation(mSlideOutLeft);
                correctMovement = mCurrentDirection == LEFT_ARROW;
            }
        } else if(deltaY > 0) {
            Logutils.d(TAG, "Drag down");
                mGameFlipper.setInAnimation(mSlideInTop);
                mGameFlipper.setOutAnimation(mSlideOutBottom);
                correctMovement = mCurrentDirection == DOWN_ARROW;
        } else {
            Logutils.d(TAG, "Drag up");
            mGameFlipper.setInAnimation(mSlideInBottom);
            mGameFlipper.setOutAnimation(mSlideOutTop);
            correctMovement = mCurrentDirection == UP_ARROW;
        }
        if(correctMovement) {
            scoreUp();
        } else {
            errorUp();
        }
        if(!mGameEnded) {
            resetTimer();
            nextImage();
        }
    }

    private void resetTimer() {
        if(mTimer != null) {
            mTimer.cancel();
        }
        if(mTimeLimit > 10) {
            mTimeLimit--;
        }
        mTimer = new CountDownTimer(mTimeLimit, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLimitBar.setProgress((int) (mTimeLimit - millisUntilFinished) * 100 / mTimeLimit);
            }

            @Override
            public void onFinish() {
                errorUp();
                if(!mGameEnded) {
                    resetTimer();
                    nextImage();
                }
            }
        };
        mTimer.start();
    }

    private void scoreUp() {
        mScoreBox.setText(String.valueOf(++mScoreCount));
    }

    private void errorUp() {
        ImageView image;

        switch(mErrorCount++) {
            case 0:
                image = (ImageView) findViewById(R.id.redXOne);
                break;
            case 1:
                image = (ImageView) findViewById(R.id.redXTwo);
                break;
            case 2:
                image = (ImageView) findViewById(R.id.redXThree);
                endGame();
                break;
            default:
                return;
        }
        image.setImageDrawable(getResources().getDrawable(R.drawable.x_red));
    }

    private void endGame() {
        Fragment fragment = EndGameFragment.newInstance(mScoreCount);
        mTimer.cancel();
        mTimer = null;
        mGameEnded = true;
        getFragmentManager().beginTransaction().replace(R.id.inGameContainer, fragment).commit();
        mPauseButton.setVisibility(View.INVISIBLE);
    }

    private void nextImage() {
        mGameFlipper.addView(getRandomImage());
        mGameFlipper.showNext();
        mGameFlipper.removeViewAt(0);
    }

    private View getRandomImage() {
        ImageView image = new ImageView(this);
        int index = (int) (Math.random() * mImageRes.length);

        mCurrentDirection = index;

        image.setImageDrawable(getResources().getDrawable(mImageRes[index]));
        image.setLayoutParams(mArrowParams);

        mGameFlipper.setInAnimation(mInAnims[index]);

        return image;
    }

    private void setAnimationDurations(int millis) {
        mSlideInTop.setDuration(millis);
        mSlideInBottom.setDuration(millis);
        mSlideInRight.setDuration(millis);
        mSlideInLeft.setDuration(millis);
        mFadeIn.setDuration(millis);
        mSlideOutTop.setDuration(millis);
        mSlideOutBottom.setDuration(millis);
        mSlideOutRight.setDuration(millis);
        mSlideOutLeft.setDuration(millis);
        mFadeOut.setDuration(millis);
    }

    private void openPauseMenu() {
        if(mTimer != null) {
            mTimer.cancel();
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.inGameContainer, new PausedFragment(), TAG_PAUSE_FRAG).commit();
        mGamePaused = true;
        mPauseButton.setVisibility(View.INVISIBLE);
    }

    public void unpause() {
        getFragmentManager().beginTransaction()
                .remove(getFragmentManager().findFragmentByTag(TAG_PAUSE_FRAG)).commit();
        mGamePaused = false;
        nextImage();
        resetTimer();
        mPauseButton.setVisibility(View.VISIBLE);
    }

    public void openQuitConfirmation() {
        mInDialog = true;
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.inGameDialog, new QuitConfirmationFragment(), TAG_QUIT_FRAG).commit();
        fm.beginTransaction().remove(fm.findFragmentByTag(TAG_PAUSE_FRAG)).commit();
    }

    public void removeDialogFragment() {
        mInDialog = false;
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.inGameContainer, new PausedFragment(), TAG_PAUSE_FRAG).commit();
        fm.beginTransaction().remove(fm.findFragmentByTag(TAG_QUIT_FRAG)).commit();
    }

    @Override
    public void onBackPressed() {
        if(mGameEnded) {
            super.onBackPressed();
        } else if(mInDialog) {
            removeDialogFragment();
        } else if(mGamePaused) {
            unpause();
        } else {
            openPauseMenu();
        }
    }
}
