package mfahmialkautsar.themoviecatalogue.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

import mfahmialkautsar.themoviecatalogue.entity.MovieData;

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

    public ArrayList<MovieData> getAllTv(String title) {
        ArrayList<MovieData> arrayList = new ArrayList<>();
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

        MovieData contentItem;
        if (cursor.getCount() > 0) {
            do {
                contentItem = new MovieData();
                contentItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                contentItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TITLE)));
                contentItem.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.BACKDROP_PATH)));
                contentItem.setType(cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TYPE)));

                arrayList.add(contentItem);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
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
