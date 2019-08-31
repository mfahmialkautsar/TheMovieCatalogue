package ga.softogi.themoviecatalogue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.VOTE_COUNT;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;

public class FavTvHelper {
    private static final String DATABASE_TV = FavDatabaseContract.TABLE_TV;
    private static FavTvDatabaseHelper favTvDatabaseHelper;

    private static FavTvHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavTvHelper(Context context) {
        favTvDatabaseHelper = new FavTvDatabaseHelper(context);
    }

    public FavTvHelper() {
    }

    public static FavTvHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavTvHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void openTv() throws SQLException {
        database = favTvDatabaseHelper.getWritableDatabase();
    }

    public void closeTv() {
        favTvDatabaseHelper.close();

        if (database.isOpen()) {
            database.close();
        }
    }

    public ArrayList<ContentItem> getAllTv(String title) {
        ArrayList<ContentItem> arrayList = new ArrayList<>();
        Cursor cursor;
        Cursor cursorSearch = database.query(DATABASE_TV, null,
                "TITLE LIKE ?",
                new String[]{"%" + title + "%"},
                null,
                null,
                _ID + " ASC",
                null);

        Cursor justCursor = database.query(DATABASE_TV, null,
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
                contentItem.setRating(cursor.getDouble(cursor.getColumnIndexOrThrow(RATING)));
                contentItem.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(VOTE_COUNT)));
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

    public long addToTvFav(ContentItem contentItem) {
        ContentValues values = new ContentValues();
        values.put(_ID, contentItem.getId());
        values.put(TITLE, contentItem.getTitle());
        values.put(OVERVIEW, contentItem.getOverview());
        values.put(RELEASE, contentItem.getRelease());
        values.put(RATING, contentItem.getRating());
        values.put(VOTE_COUNT, contentItem.getVoteCount());
        values.put(POSTER_PATH, contentItem.getPosterPath());
        values.put(BACKDROP_PATH, contentItem.getBackdropPath());
        values.put(TYPE, contentItem.getType());
        return database.insert(DATABASE_TV, null, values);
    }

    public int deleteFromTvFav(int id) {
        return database.delete(DATABASE_TV, _ID + " = '" + id + "'", null);
    }

    public boolean isTvExists(String searchItem) {
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, RATING, VOTE_COUNT, POSTER_PATH, BACKDROP_PATH, TYPE
        };
        String selection = TITLE + " =?";
        String[] selectionArgs = {searchItem};
        String limit = "1";

        Cursor cursor = database.query(DATABASE_TV, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_TV, null
                , _ID + " = ?"
                , new String[]{id}
                , null
                , null
                , null
                , null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_TV
                , null
                , null
                , null
                , null
                , null
                , _ID + " ASC");
    }

    public Cursor queryByTitleProvider(String selection, String[] searchTitle) {
        return database.query(DATABASE_TV
                , null
                , selection
                , searchTitle
                , null
                , null
                , _ID + " ASC");
    }

    public long insertProvider(ContentValues values) {
        return database.insert(DATABASE_TV, null, values);
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_TV, _ID + " = ?", new String[]{id});
    }
}
