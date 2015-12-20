package ca.benwu.fingerflinger.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ca.benwu.fingerflinger.data.ScoresContract.ScoresColumns;

/**
 * Created by Ben Wu on 12/19/2015.
 */
public class ScoresDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FlingerScores.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScoresColumns.TABLE_NAME + " (" +
                    ScoresColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScoresColumns.COLUMN_POINTS + " INTEGER," +
                    ScoresColumns.COLUMN_GAME_MODE + " TEXT," +
                    ScoresColumns.COLUMN_ANIMATION_MODE + " TEXT," +
                    ScoresColumns.COLUMN_PLATFORM + " TEXT," +
                    ScoresColumns.COLUMN_DATE + " TEXT" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScoresColumns.TABLE_NAME;

    public ScoresDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insert(int points, String gameMode,
                       String animationMode, String platform, String date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScoresColumns.COLUMN_POINTS, points);
        values.put(ScoresColumns.COLUMN_GAME_MODE, gameMode);
        values.put(ScoresColumns.COLUMN_ANIMATION_MODE, animationMode);
        values.put(ScoresColumns.COLUMN_PLATFORM, platform);
        values.put(ScoresColumns.COLUMN_DATE, date);
        return db.insert(ScoresColumns.TABLE_NAME, "null", values);
    }

    public int clearAll() {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(ScoresColumns.TABLE_NAME, null, null);
    }

    public Cursor query(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = ScoresColumns.PROJ;

        Cursor data = db.query(ScoresColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null,
                ScoresColumns.COLUMN_POINTS + " DESC");

        return data;
}
}
