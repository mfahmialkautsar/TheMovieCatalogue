package ga.softogi.themoviecatalogue.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MainModel<T> {
    @SerializedName("page")
    private int page;
    @SerializedName("results")
    private ArrayList<T> results;

    MainModel() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<T> getResults() {
        return results;
    }

    public void setResults(ArrayList<T> results) {
        this.results = results;
    }
}
