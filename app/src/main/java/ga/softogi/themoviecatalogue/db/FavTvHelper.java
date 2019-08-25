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

public class FavTvHelper {
    private static final String DATABASE_TV = FavDatabaseContract.TABLE_TV;
    private static FavTvDatabaseHelper favTvDatabaseHelper;

    private static FavTvHelper INSTANCE;

    private static SQLiteDatabase database;

    private FavTvHelper(Context context) {
        favTvDatabaseHelper = new FavTvDatabaseHelper(context);
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
                contentItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TITLE)));
                contentItem.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.OVERVIEW)));
                contentItem.setRelease(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RELEASE)));
                contentItem.setRating(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RATING)));
                contentItem.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.POSTER_PATH)));
                contentItem.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.BACKDROP_PATH)));
                contentItem.setType(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TYPE_TV)));

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
        values.put(FavDatabaseContract.TableColumns.TITLE, contentItem.getTitle());
        values.put(FavDatabaseContract.TableColumns.OVERVIEW, contentItem.getOverview());
        values.put(FavDatabaseContract.TableColumns.RELEASE, contentItem.getRelease());
        values.put(FavDatabaseContract.TableColumns.RATING, contentItem.getRating());
        values.put(FavDatabaseContract.TableColumns.POSTER_PATH, contentItem.getPosterPath());
        values.put(FavDatabaseContract.TableColumns.BACKDROP_PATH, contentItem.getBackdropPath());
        values.put(FavDatabaseContract.TYPE_TV, contentItem.getType());
        return database.insert(DATABASE_TV, null, values);
    }

    public int deleteFromTvFav(int id) {
        return database.delete(DATABASE_TV, _ID + " = '" + id + "'", null);
    }

    public boolean isTvExists(String searchItem) {
        String[] projection = {
                _ID, FavDatabaseContract.TableColumns.TITLE, FavDatabaseContract.TableColumns.OVERVIEW, FavDatabaseContract.TableColumns.RELEASE, FavDatabaseContract.TableColumns.RATING, FavDatabaseContract.TableColumns.POSTER_PATH, FavDatabaseContract.TableColumns.BACKDROP_PATH, FavDatabaseContract.TYPE_TV
        };
        String selection = FavDatabaseContract.TableColumns.TITLE + " =?";
        String[] selectionArgs = {searchItem};
        String limit = "1";

        Cursor cursor = database.query(DATABASE_TV, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}
