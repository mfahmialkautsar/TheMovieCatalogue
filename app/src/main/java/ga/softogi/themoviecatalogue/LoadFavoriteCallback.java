package ga.softogi.themoviecatalogue;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

public interface LoadFavoriteCallback {
    void preExecute();

    void postExecute(ArrayList<ContentItem> items);
}
