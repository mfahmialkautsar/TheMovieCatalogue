package ga.softogi.themoviecatalogue;

import android.database.Cursor;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE_TV;

public class MappingHelper {

    public static ArrayList<ContentItem> mapMovieCursorToArrayList(Cursor movieCursor) {
        ArrayList<ContentItem> movieList = new ArrayList<>();

        while (movieCursor.moveToNext()) {
            int id = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(_ID));
            String title = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TITLE));
            String overview = movieCursor.getString(movieCursor.getColumnIndexOrThrow(OVERVIEW));
            String release = movieCursor.getString(movieCursor.getColumnIndexOrThrow(RELEASE));
            String rating = movieCursor.getString(movieCursor.getColumnIndexOrThrow(RATING));
            String poster = movieCursor.getString(movieCursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = movieCursor.getString(movieCursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String type = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TYPE_MOVIE));
            movieList.add(new ContentItem(id, title, overview, release, rating, poster, backdrop, type));
        }
        return movieList;
    }

    public static ArrayList<ContentItem> mapTvCursorToArrayList(Cursor tvCursor) {
        ArrayList<ContentItem> tvList = new ArrayList<>();

        while (tvCursor.moveToNext()) {
            int id = tvCursor.getInt(tvCursor.getColumnIndexOrThrow(_ID));
            String title = tvCursor.getString(tvCursor.getColumnIndexOrThrow(TITLE));
            String overview = tvCursor.getString(tvCursor.getColumnIndexOrThrow(OVERVIEW));
            String release = tvCursor.getString(tvCursor.getColumnIndexOrThrow(RELEASE));
            String rating = tvCursor.getString(tvCursor.getColumnIndexOrThrow(RATING));
            String poster = tvCursor.getString(tvCursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = tvCursor.getString(tvCursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String type = tvCursor.getString(tvCursor.getColumnIndexOrThrow(TYPE_TV));
            tvList.add(new ContentItem(id, title, overview, release, rating, poster, backdrop, type));
        }
        return tvList;
    }
}
