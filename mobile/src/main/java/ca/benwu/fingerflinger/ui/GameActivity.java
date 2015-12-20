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
import static ca.benwu.fingerflinger.data.ScoresContract.ScoresColumns;

import ca.benwu.fingerflinger.data.ScoresDbHelper;
import ca.benwu.fingerflinger.utils.DateUtils;
import ca.benwu.fingerflinger.utils.Logutils;

/**
 * Created by Ben Wu on 12/12/2015.
 */
public class GameActivity extends AppCompatActivity { // TODO: sounds, fragment animation

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
    private Animation mFadeInWacky;

    private Animation mSlideOutTop;
    private Animation mSlideOutBottom;
    private Animation mSlideOutRight;
    private Animation mSlideOutLeft;
    private Animation mFadeOut;
    private Animation mFadeOutWacky;

    private int mScoreCount = 0;
    private int mErrorCount = 0;
    private TextView mScoreBox;

    private View mPauseButton;

    private int mTimeLimit = 1000;
    private CountDownTimer mTimer;
    private int mTimeAttackLimit = 60000;
    private CountDownTimer mTimeAttackTimer;

    private boolean mNoLives = false;
    private boolean mTimeAttack = false;
    private boolean mFastMode = false;
    private boolean mWackyAnim = false;
    private boolean mEasyAnim = false;
    private boolean mNoAnim = false;

    private boolean mGameEnded = false;
    private boolean mGamePaused = true;
    private boolean mGameStarted = false;
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
        mFadeInWacky = AnimationUtils.loadAnimation(this, R.anim.fade_in_normal);

        mSlideOutTop = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top);
        mSlideOutBottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_bottom);
        mSlideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_right);
        mSlideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
        mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_normal);
        mFadeOutWacky = AnimationUtils.loadAnimation(this, R.anim.fade_out_wacky);

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
        mPauseButton.setVisibility(View.INVISIBLE);

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mGamePaused && !mGameEnded) {
                    openPauseMenu();
                }
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.inGameContainer, GameOptionsFragment.create(this, true)).commit();
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
        if(mTimeAttackTimer != null) {
            mTimeAttackTimer.cancel();
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
            return;
        } else if(Math.abs(deltaX) > Math.abs(deltaY)) {
            if(deltaX > 0) {
                Logutils.d(TAG, "Drag right");
                if(!mNoAnim && !mWackyAnim) {
                    mGameFlipper.setOutAnimation(mSlideOutRight);
                }
                correctMovement = mCurrentDirection == RIGHT_ARROW;
            } else {
                Logutils.d(TAG, "Drag left");
                if(!mNoAnim && !mWackyAnim) {
                    mGameFlipper.setOutAnimation(mSlideOutLeft);
                }
                correctMovement = mCurrentDirection == LEFT_ARROW;
            }
        } else if(deltaY > 0) {
            Logutils.d(TAG, "Drag down");
            if(!mNoAnim && !mWackyAnim) {
                mGameFlipper.setOutAnimation(mSlideOutBottom);
            }
                correctMovement = mCurrentDirection == DOWN_ARROW;
        } else {
            Logutils.d(TAG, "Drag up");
            if(!mNoAnim && !mWackyAnim) {
                mGameFlipper.setOutAnimation(mSlideOutTop);
            }
            correctMovement = mCurrentDirection == UP_ARROW;
        }

        if(mWackyAnim) {
            if(Math.random() * 5 > 1) {
                mGameFlipper.setOutAnimation(mOutAnims[(int) (Math.random() * mImageRes.length)]);
            } else {
                mGameFlipper.setOutAnimation(mFadeOutWacky);
            }
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

    public void openAnimOptions() {
        getFragmentManager().beginTransaction()
                .replace(R.id.inGameContainer, GameOptionsFragment.create(this, false), "ANIM_MODE").commit();
    }

    public void closeAnimOptions() {
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("ANIM_MODE")).commit();
        unpause();
        if(mTimeAttackTimer != null) {
            mTimeAttackTimer.start();
        }
        mGameStarted = true;
    }

    public void setFastMode() {
        mTimeLimit = 600;
        mFastMode = true;
    }

    public void setTimeAttack() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final ProgressBar bar = (ProgressBar) findViewById(R.id.timeAttackTimer);
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        params.height = width-120;
        params.width = height+100;
        bar.requestLayout();

        mTimeAttack = true;
        mTimeAttackTimer = new CountDownTimer(mTimeAttackLimit, mTimeAttackLimit/100) {
            @Override
            public void onTick(long millisUntilFinished) {
                bar.setProgress((int) (mTimeAttackLimit - millisUntilFinished) * 100 / mTimeAttackLimit);
            }

            @Override
            public void onFinish() {
                endGame();
            }
        };
    }

    public void setEasy() {
        mEasyAnim = true;
    }

    public void setNoAnim() {
        mNoAnim = true;
    }

    public void setWacky() {
        mWackyAnim = true;
    }

    public void setNoLives() {
        mNoLives = true;
    }

    public void setTimeLimit(int limit) {
        mTimeLimit = limit;
    }

    private void resetTimer() {
        if(mTimer != null) {
            mTimer.cancel();
        }
        if(mTimeLimit > 10) {
            mTimeLimit--;
        }
        mTimer = new CountDownTimer(mTimeLimit, mTimeLimit/100) {
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
        if(mNoLives) {
            return;
        }

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
        if(mTimeAttackTimer != null) {
            mTimeAttackTimer.cancel();
        }
        mTimer.cancel();
        mTimer = null;
        mGameEnded = true;
        getFragmentManager().beginTransaction().replace(R.id.inGameContainer, fragment).commit();
        mPauseButton.setVisibility(View.INVISIBLE);

        insertScore();
    }

    private void insertScore() {
        String gameMode;
        String animMode;
        String date = DateUtils.getDateStringFromMilliseconds(System.currentTimeMillis(), "dd/MM/yyyy");

        if(mNoLives) {
            return;
        } else if(mTimeAttack) {
            gameMode = ScoresColumns.GAME_MODE_TIME;
        } else if(mFastMode) {
            gameMode = ScoresColumns.GAME_MODE_FAST;
        } else {
            gameMode = ScoresColumns.GAME_MODE_NORMAL;
        }

        if(mEasyAnim) {
            animMode = ScoresColumns.ANIM_MODE_EASY;
        } else if(mWackyAnim) {
            animMode = ScoresColumns.ANIM_MODE_WACKY;
        } else if(mNoAnim) {
            animMode = ScoresColumns.ANIM_MODE_NONE;
        } else {
            animMode = ScoresColumns.ANIM_MODE_NORMAL;
        }

        new ScoresDbHelper(this).insert(mScoreCount, gameMode, animMode, ScoresColumns.PLATFORM_MOBILE, date);
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

        if(mEasyAnim) {
            mGameFlipper.setInAnimation(mInAnims[index]);
        } else if(mWackyAnim) {
            if(Math.random() * 5 > 1) {
                mGameFlipper.setInAnimation(mInAnims[(int) (Math.random() * mImageRes.length)]);
            } else {
                mGameFlipper.setInAnimation(mFadeInWacky);
            }
        } else if(!mNoAnim) {
            mGameFlipper.setInAnimation(mFadeIn);
        }

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
        getFragmentManager().beginTransaction().replace(R.id.inGameContainer, new PausedFragment(), TAG_PAUSE_FRAG).commit();
        mGamePaused = true;
        mPauseButton.setVisibility(View.INVISIBLE);
    }

    public void unpause() {
        Fragment frag = getFragmentManager().findFragmentByTag(TAG_PAUSE_FRAG);
        if(frag != null) {
            getFragmentManager().beginTransaction().remove(frag).commit();
        }
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
        if(!mGameStarted || mGameEnded) {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in_wacky, R.anim.fade_out_wacky);
        } else if(mInDialog) {
            removeDialogFragment();
        } else if(mGamePaused) {
            unpause();
        } else {
            openPauseMenu();
        }
    }
}
