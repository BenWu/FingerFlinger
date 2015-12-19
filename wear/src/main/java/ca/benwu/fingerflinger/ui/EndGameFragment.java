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
import ca.benwu.fingerflinger.utils.Logutils;

/**
 * Created by Ben Wu on 12/13/2015.
 */
public class EndGameFragment extends Fragment {

    private static final String TAG = "EndGameFragment";

    public static final String KEY_SCORE = "KEY_SCORE";

    private int mScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logutils.d(TAG, "OnCreate");

        Bundle args = getArguments();

        if(args != null) {
           mScore = args.getInt(KEY_SCORE);
        }
    }

    public static EndGameFragment newInstance(int score) {
        Bundle args = new Bundle();
        args.putInt(KEY_SCORE, score);

        EndGameFragment fragment = new EndGameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_game_box, container, false);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");

        ((TextView) view.findViewById(R.id.gameOver)).setTypeface(fontBold);

        TextView okButt = ((TextView) view.findViewById(R.id.endGameButton));
        okButt.setTypeface(font);
        okButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView scoreTextView = ((TextView) view.findViewById(R.id.endGameScoreText));
        scoreTextView.setTypeface(font);
        String scoreText;

        if(mScore != 1) {
            scoreText = String.format(getResources().getString(R.string.score_text), mScore);
        } else {
            scoreText = getResources().getString(R.string.score_text_single);
        }
        scoreTextView.setText(scoreText);

        return view;
    }
}
