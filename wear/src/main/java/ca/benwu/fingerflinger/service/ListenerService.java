package ca.benwu.fingerflinger.service;

import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import ca.benwu.fingerflinger.ui.MainActivity;
import ca.benwu.fingerflinger.utils.Logutils;

/**
 * Created by Ben Wu on 12/21/2015.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG ="ListenerService";

    private static final String PATH_START_ON_WATCH = "/gameOnWatch";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String messagePath = messageEvent.getPath();

        Logutils.d(TAG, "Message: " + messagePath);

        if(messagePath.startsWith(PATH_START_ON_WATCH)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
