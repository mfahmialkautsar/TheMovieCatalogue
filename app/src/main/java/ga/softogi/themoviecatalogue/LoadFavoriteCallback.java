package ga.softogi.themoviecatalogue;

import android.database.Cursor;

public interface LoadFavoriteCallback {
    void preExecute();

    void postExecute(Cursor items);
}
