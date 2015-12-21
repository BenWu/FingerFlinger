package ca.benwu.fingerflinger.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ca.benwu.fingerflinger.R;
import static ca.benwu.fingerflinger.data.ScoresContract.ScoresColumns;
import ca.benwu.fingerflinger.data.ScoresDbHelper;
import ca.benwu.fingerflinger.view.SlidingTabLayout;

/**
 * Created by Ben Wu on 12/20/2015.
 */
public class ScoresFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;

    private ViewPager mViewPager;

    private String[] mTabTitles = {"Normal", "Fast", "Time Attack", "On Watch"};
    private String[] mModeColumns = {ScoresColumns.GAME_MODE_NORMAL,
            ScoresColumns.GAME_MODE_FAST, ScoresColumns.GAME_MODE_TIME};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scores, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.scoresPager);
        mViewPager.setAdapter(new ScoresPagerAdapter());

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.scoreSelectorTabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    class ScoresPagerAdapter extends PagerAdapter {

        int mPosition;

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.score_page, container, false);

            Cursor c;

            if(position == 3) {
                c = new ScoresDbHelper(getActivity())
                        .query(ScoresColumns.FOR_GAME_MODE_AND_PLATFORM,
                                new String[]{mModeColumns[0], ScoresColumns.PLATFORM_WEARABLE});
            } else {
                // lots of problems with running query in background thread, query should be fast anyway
                c = new ScoresDbHelper(getActivity())
                        .query(ScoresColumns.FOR_GAME_MODE_AND_PLATFORM,
                                new String[]{mModeColumns[position], ScoresColumns.PLATFORM_MOBILE});
            }

            if(c.getCount() == 0) {
                TextView noScoreText = (TextView) view.findViewById(R.id.noScoresText);
                noScoreText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf"));
                noScoreText.setVisibility(View.VISIBLE);
            } else {
                ListView scoreList = (ListView) view.findViewById(R.id.scoresList);
                scoreList.setVisibility(View.VISIBLE);

                String[] fromColumns = new String[]{ScoresColumns.COLUMN_POINTS,
                        ScoresColumns.COLUMN_ANIMATION_MODE, ScoresColumns.COLUMN_DATE};

                int[] toViews = new int[]{R.id.scoreItemPoints, R.id.scoreItemAnimMode, R.id.scoreItemTime};

                SimpleCursorAdapter scoreAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_score, c, fromColumns, toViews, 0);

                scoreList.setAdapter(scoreAdapter);
            }

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private class GetScoresTask extends AsyncTask<SimpleCursorAdapter, Void, Cursor> {
            SimpleCursorAdapter mScores;

            @Override
            protected Cursor doInBackground(SimpleCursorAdapter... params) {
                mScores = params[0];

                Cursor c = new ScoresDbHelper(getActivity())
                        .query(ScoresColumns.FOR_GAME_MODE_AND_PLATFORM,
                                new String[] {mModeColumns[mPosition], "Mobile"});

                return c;
            }

            @Override
            protected void onPostExecute(Cursor results) {
                String[] fromColumns = new String[] {ScoresColumns.COLUMN_POINTS,
                                ScoresColumns.COLUMN_ANIMATION_MODE, ScoresColumns.COLUMN_DATE};

                int[] toViews = new int[] {R.id.scoreItemPoints, R.id.scoreItemAnimMode, R.id.scoreItemTime};

                mScores.changeCursorAndColumns(results, fromColumns, toViews);
            }
        }
    }
}
