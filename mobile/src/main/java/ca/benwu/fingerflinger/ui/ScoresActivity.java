package ca.benwu.fingerflinger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.data.ScoresDbHelper;
import ca.benwu.fingerflinger.utils.Logutils;

/**
 * Created by Ben Wu on 12/19/2015.
 */
public class ScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        getFragmentManager().beginTransaction().replace(R.id.scoresContainer, new ScoresFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionClear) {
            int deleted = new ScoresDbHelper(this).clearAll();
            Logutils.d("ScoresActivity", deleted + " rows deleted");
            Intent intent = new Intent(this, ScoresActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.activity_zoom_in_slide_left, R.anim.activity_zoom_out_slide_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
