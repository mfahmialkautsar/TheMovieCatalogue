package ga.softogi.themoviecatalogue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class FavMovieDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_MOVIE_CATALOGUE = "CREATE TABLE " + FavDatabaseContract.TABLE_MOVIE
            + " (" + _ID + " INTEGER PRIMARY KEY NOT NULL, "
            + FavDatabaseContract.TableColumns.TITLE + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.OVERVIEW + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.RELEASE + " TEXT NOT NULL, "
//            + FavDatabaseContract.TableColumns.GENRE + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.RATING + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.VOTE_COUNT + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.POSTER_PATH + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.BACKDROP_PATH + " TEXT NOT NULL, "
            + FavDatabaseContract.TableColumns.TYPE + " TEXT NOT NULL"
            + ");";
    private static String DATABASE_NAME = "dbMovieCatalogue";

    FavMovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_MOVIE_CATALOGUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavDatabaseContract.TABLE_MOVIE);
        onCreate(db);
    }
}
