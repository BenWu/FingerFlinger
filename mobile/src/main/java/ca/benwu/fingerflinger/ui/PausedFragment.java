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
public class PausedFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logutils.d("PausedFragment", "OnCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pause_menu, container, false);

        TextView quitButt = ((TextView) view.findViewById(R.id.quitButton));
        quitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).openQuitConfirmation();
            }
        });

        TextView resumeButt = ((TextView) view.findViewById(R.id.resumeButton));
        resumeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).unpause();
            }
        });

        return view;
    }
}
