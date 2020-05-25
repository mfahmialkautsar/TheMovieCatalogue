package mfahmialkautsar.themoviecatalogue;

import android.database.Cursor;

import java.util.ArrayList;

import mfahmialkautsar.themoviecatalogue.db.FavDatabaseContract;
import mfahmialkautsar.themoviecatalogue.entity.MovieData;
import mfahmialkautsar.themoviecatalogue.entity.TvData;

import static android.provider.BaseColumns._ID;

public class MappingHelper {

    public static ArrayList<MovieData> mapMovieCursorToArrayList(Cursor cursor) {
        ArrayList<MovieData> contentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.OVERVIEW));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RELEASE));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RATING));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.POSTER_PATH));
            String backdrop = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.BACKDROP_PATH));
            String runtime = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RUNTIME));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.GENRE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TYPE));
            contentList.add(new MovieData(id, title, overview, release, rating, poster, backdrop, runtime, genre, type));
        }
        return contentList;
    }

    public static ArrayList<TvData> mapTvCursorToArrayList(Cursor cursor) {
        ArrayList<TvData> contentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.OVERVIEW));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RELEASE));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RATING));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.POSTER_PATH));
            String backdrop = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.BACKDROP_PATH));
            String runtime = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.RUNTIME));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.GENRE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(FavDatabaseContract.TableColumns.TYPE));
            contentList.add(new TvData(id, title, overview, release, rating, poster, backdrop, runtime, genre, type));
        }
        return contentList;
    }
}
