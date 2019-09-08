package ga.softogi.themoviecatalogue.entity;

import com.google.gson.annotations.SerializedName;

public class MovieTrailer extends MainModel<MovieTrailerData> {

    @SerializedName("id")
    private int id;

    public MovieTrailer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
