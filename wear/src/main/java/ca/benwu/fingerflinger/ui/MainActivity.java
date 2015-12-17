package ca.benwu.fingerflinger.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ca.benwu.fingerflinger.R;

public class MainActivity extends Activity {

    private TextView mTextView;
    private TextView mTitleFront;
    private TextView mTitleEnd;
    private View mMainArrow;

    private Animation mSlideUpExit;
    private Animation mSlideUpEnter;
    private Animation mRotateCcw;
    private Animation mRotateCw;
    private Animation mArrowPause;
    private Animation mFrontPause;
    private Animation mEndPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideUpExit = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_top);
        mSlideUpEnter = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);

        mRotateCcw = AnimationUtils.loadAnimation(this, R.anim.rotate_ccw);
        mRotateCw = AnimationUtils.loadAnimation(this, R.anim.rotate_cw);

        mArrowPause = AnimationUtils.loadAnimation(this, R.anim.pause);
        mArrowPause.setDuration(970);
        mFrontPause = AnimationUtils.loadAnimation(this, R.anim.pause);
        mFrontPause.setDuration(950);
        mEndPause = AnimationUtils.loadAnimation(this, R.anim.pause);
        mEndPause.setDuration(800);

        mTitleFront = (TextView) findViewById(R.id.gameTitleFront);
        mTitleFront.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf"));

        mTitleEnd = (TextView) findViewById(R.id.gameTitleEnd);
        mTitleEnd.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf"));

        mTextView = (TextView) findViewById(R.id.startButton);
        mTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf"));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });

        mMainArrow = findViewById(R.id.mainArrow);

        setAnimationListeners();

        mTitleFront.startAnimation(mRotateCcw);
        mTitleEnd.startAnimation(mRotateCw);
        mMainArrow.startAnimation(mSlideUpExit);
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void setAnimationListeners() {
        mSlideUpExit.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mMainArrow.startAnimation(mSlideUpEnter);
            }
        });
        mSlideUpEnter.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mMainArrow.startAnimation(mArrowPause);
            }
        });
        mArrowPause.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mMainArrow.startAnimation(mSlideUpExit);
            }
        });

        mRotateCcw.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mTitleFront.startAnimation(mFrontPause);
            }
        });
        mFrontPause.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mTitleFront.startAnimation(mRotateCcw);
            }
        });

        mRotateCw.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mTitleEnd.startAnimation(mEndPause);
            }
        });
        mEndPause.setAnimationListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mTitleEnd.startAnimation(mRotateCw);
            }
        });
    }

    // to avoid too much code repeat in setAnimationListeners
    private class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
