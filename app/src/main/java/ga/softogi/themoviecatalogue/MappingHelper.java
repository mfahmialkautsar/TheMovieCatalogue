package ga.softogi.themoviecatalogue;

import android.database.Cursor;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.MovieData;
import ga.softogi.themoviecatalogue.entity.TvData;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.GENRE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;

public class MappingHelper {

    public static ArrayList<MovieData> mapMovieCursorToArrayList(Cursor cursor) {
        ArrayList<MovieData> contentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(RELEASE));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(RATING));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = cursor.getString(cursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String runtime = cursor.getString(cursor.getColumnIndexOrThrow(RUNTIME));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(GENRE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
            contentList.add(new MovieData(id, title, overview, release, rating, poster, backdrop, runtime, genre, type));
        }
        return contentList;
    }

    public static ArrayList<TvData> mapTvCursorToArrayList(Cursor cursor) {
        ArrayList<TvData> contentList = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(OVERVIEW));
            String release = cursor.getString(cursor.getColumnIndexOrThrow(RELEASE));
            double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(RATING));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = cursor.getString(cursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String runtime = cursor.getString(cursor.getColumnIndexOrThrow(RUNTIME));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(GENRE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(TYPE));
            contentList.add(new TvData(id, title, overview, release, rating, poster, backdrop, runtime, genre, type));
        }
        return contentList;
    }
}
