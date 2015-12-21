package ca.benwu.fingerflinger.service;

import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

import ca.benwu.fingerflinger.data.ScoresDbHelper;
import ca.benwu.fingerflinger.utils.DateUtils;
import ca.benwu.fingerflinger.utils.Logutils;
import static ca.benwu.fingerflinger.data.ScoresContract.ScoresColumns;

/**
 * Created by Ben Wu on 12/20/2015.
 */
public class WearableService extends WearableListenerService {

    private static final String TAG = "WearableService";

    public static final String PATH_RESULTS = "/WEAR_RESULTS";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API).build();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                return;
            }
        }
        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Logutils.d(TAG, "Data path: " + path);

            if(path.equals(PATH_RESULTS)) {
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                int score = dataMap.getInt("score");

                new ScoresDbHelper(this).insert(score, ScoresColumns.GAME_MODE_NORMAL,
                        ScoresColumns.ANIM_MODE_EASY, ScoresColumns.PLATFORM_WEARABLE,
                        DateUtils.getDateStringFromMilliseconds(System.currentTimeMillis(), DateUtils.DATE_FORMAT));

                Logutils.d(TAG, "Score: " + score);
            }
        }
    }
}
