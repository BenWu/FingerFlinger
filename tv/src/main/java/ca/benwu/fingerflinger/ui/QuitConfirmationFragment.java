package ca.benwu.fingerflinger.ui;

import android.app.Fragment;
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
public class QuitConfirmationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logutils.d("QuitConfirmationFragment", "OnCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quit_confirmation, container, false);

        TextView yesButt = ((TextView) view.findViewById(R.id.yesButton));
        yesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in_wacky, R.anim.fade_out_wacky);
            }
        });

        TextView noButt = ((TextView) view.findViewById(R.id.noButton));
        noButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).removeDialogFragment();
            }
        });

        return view;
    }

}
