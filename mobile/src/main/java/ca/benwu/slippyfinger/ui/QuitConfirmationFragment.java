package ca.benwu.slippyfinger.ui;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.benwu.slippyfinger.R;
import ca.benwu.slippyfinger.utils.Logutils;

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

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");

        ((TextView) view.findViewById(R.id.areYouSure)).setTypeface(fontBold);

        TextView yesButt = ((TextView) view.findViewById(R.id.yesButton));
        yesButt.setTypeface(font);
        yesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView noButt = ((TextView) view.findViewById(R.id.noButton));
        noButt.setTypeface(font);
        noButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameActivity) getActivity()).removeDialogFragment();
            }
        });

        return view;
    }

}
