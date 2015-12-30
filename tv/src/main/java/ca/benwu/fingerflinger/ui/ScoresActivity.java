package ca.benwu.fingerflinger.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.data.ScoresDbHelper;

import static ca.benwu.fingerflinger.data.ScoresContract.ScoresColumns;

/**
 * Created by Ben Wu on 12/29/2015.
 */
public class ScoresActivity extends Activity {

    private Cursor mNormalScores;
    private Cursor mFastScores;
    private Cursor mTimeAttackScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scores);

        ScoresDbHelper dbHelper = new ScoresDbHelper(this);

        mNormalScores = dbHelper.query(ScoresColumns.FOR_GAME_MODE, new String[] {ScoresColumns.GAME_MODE_NORMAL});
        mFastScores = dbHelper.query(ScoresColumns.FOR_GAME_MODE, new String[] {ScoresColumns.GAME_MODE_FAST});
        mTimeAttackScores = dbHelper.query(ScoresColumns.FOR_GAME_MODE, new String[] {ScoresColumns.GAME_MODE_TIME});

        setupList((ListView) findViewById(R.id.scoresList), mNormalScores);
        setupList((ListView) findViewById(R.id.scoresList2), mFastScores);
        setupList((ListView) findViewById(R.id.scoresList3), mTimeAttackScores);
    }

    private void setupList(ListView listView, Cursor scores) {
        listView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        String[] fromColumns = new String[]{ScoresColumns.COLUMN_POINTS,
                ScoresColumns.COLUMN_ANIMATION_MODE, ScoresColumns.COLUMN_DATE};
        int[] toViews = new int[]{R.id.scoreItemPoints, R.id.scoreItemAnimMode, R.id.scoreItemTime};
        SimpleCursorAdapter scoreAdapter = new SimpleCursorAdapter(this, R.layout.item_score, scores, fromColumns, toViews, 0);
        listView.setAdapter(scoreAdapter);
    }
}
