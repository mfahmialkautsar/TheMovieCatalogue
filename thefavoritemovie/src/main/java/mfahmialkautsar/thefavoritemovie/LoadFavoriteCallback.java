package mfahmialkautsar.thefavoritemovie;

import android.database.Cursor;

public interface LoadFavoriteCallback {
    void postExecute(Cursor items);
}
