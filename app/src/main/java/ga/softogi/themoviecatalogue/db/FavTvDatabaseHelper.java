package ga.softogi.themoviecatalogue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class FavTvDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_TV_CATALOGUE = "CREATE TABLE " + FavDatabaseContract.TABLE_TV
            + " (" + _ID + " INTEGER PRIMARY KEY, "
            + FavDatabaseContract.TableColumns.TITLE + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.OVERVIEW + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.RELEASE + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.RATING + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.POSTER_PATH + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.BACKDROP_PATH + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.TYPE_TV + " TEXT NOT NULL"
            + ");";
    private static String DATABASE_NAME = "dbTvCatalogue";

    FavTvDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_TV_CATALOGUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavDatabaseContract.TABLE_TV);
        onCreate(db);
    }
}
