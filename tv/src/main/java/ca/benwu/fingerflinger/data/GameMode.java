package ca.benwu.fingerflinger.data;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ca.benwu.fingerflinger.R;

/**
 * Created by Ben Wu on 12/28/2015.
 */
public class GameMode {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_FAST = 1;
    public static final int MODE_TIME_ATTACK = 2;
    public static final int MODE_INFINITE = 3;

    private static final String[] MODE_NAMES = {"Normal Mode", "Fast Mode", "Time Attack", "Infinite Lives"};

    private int mMode;
    private String mModeName;

    private Animation mAnim;
    private Animation mSecondAnim;

    public GameMode(Context context, int mode) {
        mMode = mode;

        switch(mode) {
            case MODE_NORMAL:
                mAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_out_to_right);
                mSecondAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_in_from_left);
                break;
            case MODE_FAST:
                mAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_out_to_right_fast);
                mSecondAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_in_from_left_fast);
                break;
            case MODE_TIME_ATTACK:
                mAnim = AnimationUtils.loadAnimation(context, R.anim.rotation);
                mSecondAnim = AnimationUtils.loadAnimation(context, R.anim.rotation);
                break;
            case MODE_INFINITE:
                mAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_out_to_right_slow);
                mSecondAnim = AnimationUtils.loadAnimation(context, R.anim.card_slide_in_from_left_slow);
                break;
        }

        mModeName = MODE_NAMES[mode];
    }

    public Animation getAnimation() {
        return mAnim;
    }

    public Animation getSecondAnimation() {
        return mSecondAnim;
    }

    public String getModeName() {
        return mModeName;
    }

    public int getMode() {
        return mMode;
    }
}
