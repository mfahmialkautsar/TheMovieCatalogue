package ga.softogi.thefavoritemovie.entity;

import android.database.Cursor;

import static android.provider.BaseColumns._ID;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RATING;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.RELEASE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TITLE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.TableColumns.TYPE;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.getColumnInt;
import static ga.softogi.thefavoritemovie.db.DatabaseContract.getColumnString;

public class ContentItem {
    public static final String TYPE_MOVIE = "type_movie";
    public static final String TYPE_TV = "type_tv";

    private int id;
    private String title;
    private String overview;
    private String release;
    private String rating;
    private String posterPath;
    private String backdropPath;
    private String type;

    public ContentItem(int id, String title, String overview, String release, String rating, String posterPath, String backdropPath, String type) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.release = release;
        this.rating = rating;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.type = type;
    }

    public ContentItem(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, TITLE);
        this.overview = getColumnString(cursor, OVERVIEW);
        this.release = getColumnString(cursor, RELEASE);
        this.rating = getColumnString(cursor, RATING);
        this.posterPath = getColumnString(cursor, POSTER_PATH);
        this.backdropPath = getColumnString(cursor, BACKDROP_PATH);
        this.type = getColumnString(cursor, TYPE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
