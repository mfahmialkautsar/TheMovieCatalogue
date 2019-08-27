package ga.softogi.themoviecatalogue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
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

    public FavMovieHelper() {
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

    public ArrayList<ContentItem> getAllMovies(String title) {
        ArrayList<ContentItem> arrayList = new ArrayList<>();
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

        ContentItem contentItem;
        if (cursor.getCount() > 0) {
            do {
                contentItem = new ContentItem();
                contentItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                contentItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                contentItem.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW)));
                contentItem.setRelease(cursor.getString(cursor.getColumnIndexOrThrow(RELEASE)));
                contentItem.setRating(cursor.getString(cursor.getColumnIndexOrThrow(RATING)));
                contentItem.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(POSTER_PATH)));
                contentItem.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(BACKDROP_PATH)));
                contentItem.setType(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)));

                arrayList.add(contentItem);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public int deleteFromMovieFav(int id) {
        return database.delete(DATABASE_MOVIE, _ID + " = '" + id + "'", null);
    }

    public void beginTransaction() {
        database.beginTransaction();
    }

    public void setTransactionSuccess() {
        database.setTransactionSuccessful();
    }

    public void endTransaction() {
        database.endTransaction();
    }

    public void insertTransaction(ContentItem item) {
        String sql = "INSERT INTO " + DATABASE_MOVIE
                + " (" + _ID + ", " + TITLE + ", " + OVERVIEW + ", " + RELEASE + ", "
                + RATING + ", " + POSTER_PATH + ", " + BACKDROP_PATH + ", " + TYPE
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindLong(1, (long) item.getId());
        statement.bindString(2, item.getTitle());
        statement.bindString(3, item.getOverview());
        statement.bindString(4, item.getRelease());
        statement.bindString(5, item.getRating());
        statement.bindString(6, item.getPosterPath());
        statement.bindString(7, item.getBackdropPath());
        statement.bindString(8, item.getType());
        statement.execute();
        statement.clearBindings();
    }

    public boolean isMovieExists(String searchItem) {
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
        };
        String selection = TITLE + " =?";
        String[] selectionArgs = {searchItem};
        String limit = "1";

        Cursor cursor = database.query(DATABASE_MOVIE, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
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
