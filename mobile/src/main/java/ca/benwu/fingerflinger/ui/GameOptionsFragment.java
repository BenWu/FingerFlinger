package ca.benwu.fingerflinger.ui;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.benwu.fingerflinger.R;

/**
 * Created by Ben Wu on 12/19/2015.
 */
public class GameOptionsFragment extends Fragment {

    private static final String KEY_TYPE = "OPTION_TYPE";

    private GameActivity mActivity;

    // isGameMode: true means selecting game mode, false means selecting animation mode
    public static GameOptionsFragment create(GameActivity activity, boolean isGameMode) {
        GameOptionsFragment fragment = new GameOptionsFragment();
        fragment.setActivity(activity);

        Bundle args = new Bundle();
        args.putBoolean(KEY_TYPE, isGameMode);
        fragment.setArguments(args);

        return fragment;
    }

    public void setActivity(GameActivity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean isGameMode = getArguments().getBoolean(KEY_TYPE);

        View view = inflater.inflate(R.layout.fragment_game_options, container, false);

        TextView title = (TextView) view.findViewById(R.id.optionsTitle);
        TextView button1 = (TextView) view.findViewById(R.id.optionButton1);
        TextView button2 = (TextView) view.findViewById(R.id.optionButton2);
        TextView button3 = (TextView) view.findViewById(R.id.optionButton3);
        TextView button4 = (TextView) view.findViewById(R.id.optionButton4);

        title.setText(isGameMode ? R.string.game_mode_options : R.string.anim_mode_options);
        button1.setText(isGameMode ? R.string.mode_normal : R.string.mode_normal_anim);
        button2.setText(isGameMode ? R.string.mode_fast : R.string.mode_wacky);
        button3.setText(isGameMode ? R.string.mode_time_attack : R.string.mode_easy);
        button4.setText(isGameMode ? R.string.mode_infinite : R.string.mode_none);

        title.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/Quicksand-Bold.otf"));
        button1.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/Quicksand-Regular.otf"));
        button2.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/Quicksand-Regular.otf"));
        button3.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/Quicksand-Regular.otf"));
        button4.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), "fonts/Quicksand-Regular.otf"));

        if(isGameMode) {
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.openAnimOptions();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setFastMode();
                    mActivity.openAnimOptions();
                }
            });
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setTimeAttack();
                    mActivity.openAnimOptions();
                }
            });
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setNoLives();
                    mActivity.openAnimOptions();
                }
            });
        } else {
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.closeAnimOptions();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setWacky();
                    mActivity.closeAnimOptions();
                }
            });
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setEasy();
                    mActivity.closeAnimOptions();
                }
            });
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.setNoAnim();
                    mActivity.closeAnimOptions();
                }
            });
        }
        return view;
    }
}
