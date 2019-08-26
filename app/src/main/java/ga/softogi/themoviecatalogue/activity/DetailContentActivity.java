package ga.softogi.themoviecatalogue.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.db.FavMovieHelper;
import ga.softogi.themoviecatalogue.db.FavTvHelper;
import ga.softogi.themoviecatalogue.entity.ContentItem;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE_TV;

public class DetailContentActivity extends AppCompatActivity {
    public static final String EXTRA_CONTENT = "extra_movie";
    public static final String EXTRA_TV = "extra_tv";
    public static final int NO_INTERNET = R.string.no_internet;
    private MaterialFavoriteButton favoriteButton;
    private ProgressBar progressBar;

    private ContentItem content;
    int position;
//    private FavMovieHelper favMovieHelper;
//    private FavTvHelper favTvHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_content);

        ImageView ivPoster = findViewById(R.id.iv_poster);
        ImageView ivBackdrop = findViewById(R.id.iv_backdrop);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvRelease = findViewById(R.id.tv_release);
        TextView tvRating = findViewById(R.id.tv_rating);

        content = getIntent().getParcelableExtra(EXTRA_CONTENT);

//        if (content != null) {
//            position = getIntent().getIntExtra("extra_position", 0);
//        } else {
//            content = new ContentItem();
//        }

//        Uri uri = getIntent().getData();
//
//        if (uri != null) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//
//            if (cursor != null) {
//                if (cursor.moveToFirst()) content = new ContentItem(cursor);
//                cursor.close();
//            }
//        }
        int id = content.getId();
        String title = content.getTitle();
        String overview = content.getOverview();
        String rating = content.getRating();
        String type = content.getType();
        String poster_path = content.getPosterPath();
        String backdrop_path = content.getBackdropPath();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(type);
        }

        tvTitle.setText(title);
        tvOverview.setText(overview);

        String theOverview;
        if (TextUtils.isEmpty(overview)) {
            theOverview = getString(R.string.overview_not_found);
        } else {
            theOverview = overview;
        }
        tvOverview.setText(theOverview);

        String theRating;
        if (Objects.equals(rating, "0")) {
            theRating = getString(R.string.no_rating);
        } else {
            theRating = rating;
        }
        tvOverview.setText(theOverview);
        tvRating.setText(String.format(" %s", theRating));

        String release = content.getRelease();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String theDate = getString(R.string.date);

        try {
            Date date = dateFormat.parse(release);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat(theDate);
            String releaseYear = newDateFormat.format(date);
            tvRelease.setText(releaseYear);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + poster_path)
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivPoster);
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original/" + backdrop_path)
                .placeholder(this.getResources().getDrawable(R.drawable.ic_loading_24dp))
                .error(this.getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                .into(ivBackdrop);

        progressBar = findViewById(R.id.progress_bar);
        showLoading(true);

        ContentValues values = new ContentValues();

        favoriteButton = findViewById(R.id.favorite);
        if (type != null) {
            if (type.equals(ContentItem.TYPE_MOVIE)) {
                values.put(_ID, id);
                values.put(TITLE, title);
                values.put(OVERVIEW, overview);
                values.put(RELEASE, release);
                values.put(RATING, rating);
                values.put(POSTER_PATH, poster_path);
                values.put(BACKDROP_PATH, backdrop_path);
                values.put(TYPE_MOVIE, type);
//                favMovieHelper = FavMovieHelper.getInstance(getApplicationContext());
//                favMovieHelper.openMovie();
                favMovie(id, title, values);
            } else if (type.equals(ContentItem.TYPE_TV)) {
                values.put(_ID, id);
                values.put(TITLE, title);
                values.put(OVERVIEW, overview);
                values.put(RELEASE, release);
                values.put(RATING, rating);
                values.put(POSTER_PATH, poster_path);
                values.put(BACKDROP_PATH, backdrop_path);
                values.put(TYPE_TV, type);
//                favTvHelper = FavTvHelper.getInstance(getApplicationContext());
//                favTvHelper.openTv();
                favTv(id, title, values);
            }
        }

        if (ivPoster != null && ivBackdrop != null && tvRelease != null) {
            showLoading(false);
        }

        isNetworkAvailable();
    }

    private void favMovie(final int id, final String title, final ContentValues values) {
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, RATING, POSTER_PATH, BACKDROP_PATH, TYPE_MOVIE
        };
        String selection = TITLE + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = getContentResolver().query(getIntent().getData(), projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                favoriteButton.setFavorite(true);
                favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
    //                        favMovieHelper.beginTransaction();
    //                        favMovieHelper.insertTransaction(content);
    //                        favMovieHelper.setTransactionSuccess();
                            showSnackbarMessage(title + getString(R.string.add_fav_success));
    //                        favMovieHelper.endTransaction();
                        } else {
                            getContentResolver().delete(getIntent().getData(), String.valueOf(id), null);
    //                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
    //                        if (result > 0) {
                                showSnackbarMessage(title + getString(R.string.del_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
    //                        }
                        }
                    }
                });
            } else {
                favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            getContentResolver().insert(CONTENT_URI_MOVIE, values);
    //                        favMovieHelper.beginTransaction();
    //                        favMovieHelper.insertTransaction(content);
    //                        favMovieHelper.setTransactionSuccess();
                            showSnackbarMessage(title + getString(R.string.add_fav_success));
    //                        favMovieHelper.endTransaction();
                        } else {
                            getContentResolver().delete(getIntent().getData(), String.valueOf(id), null);
    //                        long result = favMovieHelper.deleteFromMovieFav(content.getId());
    //                        if (result > 0) {
                                showSnackbarMessage(title + getString(R.string.del_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
    //                        }
                        }
                    }
                });
            }
            cursor.close();
        }
    }

    private void favTv(int id, final String title, final ContentValues values) {
        String[] projection = {
                _ID, TITLE, OVERVIEW, RELEASE, RATING, POSTER_PATH, BACKDROP_PATH, TYPE_MOVIE
        };
        String selection = TITLE + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = getContentResolver().query(getIntent().getData(), projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                favoriteButton.setFavorite(true);
                favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            getContentResolver().insert(CONTENT_URI_TV, values);
    //                        long result = favTvHelper.addToTvFav(content);
    //                        if (result > 0) {
    //                            content.setId((int) result);
                                showSnackbarMessage(title + getString(R.string.add_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.add_fav_failed));
    //                        }
                        } else {
                            getContentResolver().delete(getIntent().getData(), null, null);
    //                        long result = favTvHelper.deleteFromTvFav(content.getId());
    //                        if (result > 0) {
                                showSnackbarMessage(title + getString(R.string.del_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
    //                        }
                        }
                    }
                });
            } else {
                favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        if (favorite) {
                            getContentResolver().insert(CONTENT_URI_TV, values);
    //                        long result = favTvHelper.addToTvFav(content);
    //                        if (result > 0) {
    //                            content.setId((int) result);
                                showSnackbarMessage(title + getString(R.string.add_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.add_fav_failed));
    //                        }
                        } else {
                            getContentResolver().delete(getIntent().getData(), null, null);
    //                        long result = favTvHelper.deleteFromTvFav(content.getId());
    //                        if (result > 0) {
                                showSnackbarMessage(title + getString(R.string.del_fav_success));
    //                        } else {
    //                            showSnackbarMessage(title + getString(R.string.del_fav_failed));
    //                        }
                        }
                    }
                });
            }
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(findViewById(R.id.detail_layout), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoading(boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(this, NO_INTERNET, Toast.LENGTH_LONG).show();
        }
        if (networkInfo != null) {
            networkInfo.isConnected();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        content = getIntent().getParcelableExtra(EXTRA_CONTENT);
//        String type = content.getType();
//        if (type != null) {
//            if (type.equals(ContentItem.TYPE_MOVIE)) {
////                favMovieHelper.closeMovie();
//            } else if (type.equals(ContentItem.TYPE_TV)) {
////                favTvHelper.closeTv();
//            }
//        }
    }
}
