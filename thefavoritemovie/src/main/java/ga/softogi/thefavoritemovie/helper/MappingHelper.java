package ga.softogi.thefavoritemovie.helper;

import android.database.Cursor;

import java.util.ArrayList;

import ga.softogi.thefavoritemovie.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.GENRE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RATING;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RELEASE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TITLE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TYPE;

public class MappingHelper {

    public static ArrayList<ContentItem> mapCursorToArrayList(Cursor movieCursor) {
        ArrayList<ContentItem> movieList = new ArrayList<>();

        while (movieCursor.moveToNext()) {
            int id = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(_ID));
            String title = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TITLE));
            String overview = movieCursor.getString(movieCursor.getColumnIndexOrThrow(OVERVIEW));
            String release = movieCursor.getString(movieCursor.getColumnIndexOrThrow(RELEASE));
            double rating = movieCursor.getDouble(movieCursor.getColumnIndexOrThrow(RATING));
            String poster = movieCursor.getString(movieCursor.getColumnIndexOrThrow(POSTER_PATH));
            String backdrop = movieCursor.getString(movieCursor.getColumnIndexOrThrow(BACKDROP_PATH));
            String runtime = movieCursor.getString(movieCursor.getColumnIndexOrThrow(RUNTIME));
            String genre = movieCursor.getString(movieCursor.getColumnIndexOrThrow(GENRE));
            String type = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TYPE));
            movieList.add(new ContentItem(id, title, overview, release, rating, poster, backdrop, runtime, genre, type));
        }
        return movieList;
    }
}
