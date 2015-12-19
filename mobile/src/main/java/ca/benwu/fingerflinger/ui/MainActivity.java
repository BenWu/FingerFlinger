package ca.benwu.fingerflinger.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.utils.AnimationQueuePlayer;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper mArrowFlipper;

    private TextView mFrontTitle;
    private TextView mEndTitle;

    private Animation mFrontTitleSlideRight;
    private Animation mFrontTitleSlideLeft;
    private Animation mEndTitleSlideRight;
    private Animation mEndTitleSlideLeft;

    // to animation listener constructor
    private final int FRONT_RIGHT = 1;
    private final int FRONT_LEFT = 2;
    private final int END_RIGHT = 3;
    private final int END_LEFT = 4;

    private final int ANIMATION_DURATION = 1000; // in ms

    private int[] mInAnims = new int[] {R.anim.fade_in_wacky, R.anim.slide_in_from_bottom,
            R.anim.slide_in_from_top, R.anim.slide_in_from_left, R.anim.slide_in_from_right};
    private int[] mOutAnims = new int[] {R.anim.fade_out_wacky, R.anim.slide_out_to_left,
            R.anim.slide_out_to_right, R.anim.slide_out_to_bottom, R.anim.slide_out_to_top};

    private CountDownTimer mCountDown = new CountDownTimer(ANIMATION_DURATION, ANIMATION_DURATION) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if(mArrowFlipper != null) {
                mArrowFlipper.setInAnimation(getApplicationContext(),
                        mInAnims[(int) (Math.random() * mInAnims.length)]);
                mArrowFlipper.setOutAnimation(getApplicationContext(),
                        mOutAnims[(int) (Math.random() * mOutAnims.length)]);
            }
            mCountDown.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mFrontTitle = (TextView) findViewById(R.id.mainTitle1);
        mEndTitle = (TextView) findViewById(R.id.mainTitle2);

        mFrontTitle.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/Quicksand-Bold.otf"));
        mEndTitle.setTypeface(Typeface.createFromAsset(getAssets(),
                "fonts/Quicksand-Regular.otf"));

        mArrowFlipper = (ViewFlipper) findViewById(R.id.mainActivityAnimation);
        mArrowFlipper.setFlipInterval(ANIMATION_DURATION);
        mArrowFlipper.startFlipping();

        mCountDown.start();

        TextView startButton = ((TextView) findViewById(R.id.mainStartButton));
        startButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf"));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startGameIntent = new Intent(getApplicationContext(), GameActivity.class);
                Bundle args = ActivityOptions
                        .makeCustomAnimation(getApplicationContext(), R.anim.activity_zoom_in_slide_left, R.anim.activity_zoom_out_slide_left)
                        .toBundle();
                startActivity(startGameIntent, args);
            }
        });

        new AnimationQueuePlayer(mFrontTitle, true,
                AnimationUtils.loadAnimation(this, R.anim.front_title_slide_right),
                AnimationUtils.loadAnimation(this, R.anim.front_title_slide_left)).playQueue();

        new AnimationQueuePlayer(mEndTitle, true,
                AnimationUtils.loadAnimation(this, R.anim.end_title_slide_right),
                AnimationUtils.loadAnimation(this, R.anim.end_title_slide_left)).playQueue();
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
}
