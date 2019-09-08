package ga.softogi.themoviecatalogue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.MovieData;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;

public class FavMovieHelper {
    private static final String DATABASE_MOVIE = FavDatabaseContract.TABLE_MOVIE;
    private static FavMovieDatabaseHelper favMovieDatabaseHelper;
    private static FavMovieHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavMovieHelper(Context context) {
        favMovieDatabaseHelper = new FavMovieDatabaseHelper(context);
    }

    public static FavMovieHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavMovieHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void openMovie() throws SQLException {
        database = favMovieDatabaseHelper.getWritableDatabase();
    }

    public void closeMovie() {
        favMovieDatabaseHelper.close();

        if (database.isOpen()) {
            database.close();
        }
    }

    public ArrayList<MovieData> getAllMovies(String title) {
        ArrayList<MovieData> arrayList = new ArrayList<>();
        Cursor cursor;
        Cursor cursorSearch = database.query(DATABASE_MOVIE, null,
                "TITLE LIKE ?",
                new String[]{"%" + title + "%"},
                null,
                null,
                _ID + " ASC",
                null);

        Cursor justCursor = database.query(DATABASE_MOVIE, null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null);

        if (TextUtils.isEmpty(title)) {
            cursor = justCursor;
        } else {
            cursor = cursorSearch;
        }
        cursor.moveToFirst();

        MovieData contentItem;
        if (cursor.getCount() > 0) {
            do {
                contentItem = new MovieData();
                contentItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                contentItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                contentItem.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(BACKDROP_PATH)));
                contentItem.setType(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));

                arrayList.add(contentItem);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_MOVIE, null
                , _ID + " = ?"
                , new String[]{id}
                , null
                , null
                , null
                , null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_MOVIE
                , null
                , null
                , null
                , null
                , null
                , _ID + " ASC");
    }

    public Cursor queryByTitleProvider(String selection, String[] searchTitle) {
        return database.query(DATABASE_MOVIE
                , null
                , selection
                , searchTitle
                , null
                , null
                , _ID + " ASC");
    }

    public long insertProvider(ContentValues values) {
        return database.insert(DATABASE_MOVIE, null, values);
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_MOVIE, _ID + " = ?", new String[]{id});
    }
}
