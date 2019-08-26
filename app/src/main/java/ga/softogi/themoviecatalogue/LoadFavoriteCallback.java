package ga.softogi.themoviecatalogue;

import android.database.Cursor;

import java.util.ArrayList;

import ga.softogi.themoviecatalogue.entity.ContentItem;

public interface LoadFavoriteCallback {
    void preExecute();

    void postExecute(Cursor items);
}
