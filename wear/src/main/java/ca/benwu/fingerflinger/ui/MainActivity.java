package ca.benwu.fingerflinger.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ca.benwu.fingerflinger.R;

public class MainActivity extends Activity {

    private TextView mTextView;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = (TextView) findViewById(R.id.gameTitle);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf"));

        mTextView = (TextView) findViewById(R.id.startButton);
        mTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf"));
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);

        startActivity(intent);
    }
}
