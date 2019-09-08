package ga.softogi.themoviecatalogue.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import ga.softogi.themoviecatalogue.BuildConfig;
import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.activity.DetailActivity;
//import ga.softogi.themoviecatalogue.activity.DetailFavActivity;
import ga.softogi.themoviecatalogue.activity.MainActivity;
import ga.softogi.themoviecatalogue.entity.MovieData;
import ga.softogi.themoviecatalogue.network.NetworkContract;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.GENRE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;
import static ga.softogi.themoviecatalogue.entity.MovieData.TYPE_MOVIE;

public class ReleasedFilmsService extends JobService {
    private final ArrayList<MovieData> listMovie = new ArrayList<>();
    private int notifId = 11;
    private Uri uri;
    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private Cursor cursor;
    private MovieData movieDataUri;

    @Override
    public boolean onStartJob(JobParameters params) {
        getReleased(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void getReleased(final JobParameters job) {
        AsyncHttpClient client = new AsyncHttpClient();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String today = dateFormat.format(date);

        String url = BuildConfig.BASE_URL + NetworkContract.VERSION + "/discover" + NetworkContract.MOVIE + "?api_key=" + BuildConfig.API_KEY + "&primary_release_date.gte=" + today + "&primary_release_date.lte=" + today;

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject movieItem = list.getJSONObject(i);

                        int id = movieItem.getInt("id");
                        String title = movieItem.getString("title");
//                        if (lang.equals("en-US")) {
//                            title = movieItem.getString("title");
//                        } else if (lang.equals("id-ID")) {
//                            title = movieItem.getString("original_title");
//                        }
                        String overview = movieItem.getString("overview");
                        String release = movieItem.getString("release_date");
                        double rating = movieItem.getDouble("vote_average");
//                        int vote_count = movieItem.getInt("vote_count");
                        String poster = movieItem.getString("poster_path");
                        String backdrop_path = movieItem.getString("backdrop_path");
                        String type = TYPE_MOVIE;

//                        uri = Uri.parse(CONTENT_URI_MOVIE + "/" + id);
//                        projection = new String[]{
//                                _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
//                        };
//                        selection = _ID + " =?";
//                        selectionArgs = new String[]{String.valueOf(id)};
//                        cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
//
//                        if (uri != null) {
//                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null) {
//                                if (cursor.moveToFirst()){
//                                    movieDataUri = new MovieData(cursor);
//                                }
//                                cursor.close();
//                            }
//                        }

                        MovieData movieData;
                        MovieData newMovie = new MovieData(id, title, overview, release, rating, poster, backdrop_path, type);
//                        if (cursor != null && cursor.getCount() > 0) {
//                            title = movieDataUri.getTitle();
//                            overview = movieDataUri.getOverview();
//                            release = movieDataUri.getReleaseDate();
//                            rating = movieDataUri.getVoteAverage();
//                            poster = movieDataUri.getPosterPath();
//                            backdrop_path = movieDataUri.getBackdropPath();
//                            String genre = movieDataUri.getGenre();
//                            String runtime = movieDataUri.getRuntime();
//                            MovieData savedMovieData = new MovieData(id, title, overview, release, rating, poster, backdrop_path, runtime, genre, type);
//                            cursor.close();
//                            movieData = savedMovieData;
//                        } else {
                            movieData = newMovie;
//                        }
//                        if (movieItem.getString("release_date").equals(today)) {
                        listMovie.add(movieData);
//                        }
                    }
                    JSONObject oneMovie = list.getJSONObject(0);
                    int id = oneMovie.getInt("id");
                    String title = oneMovie.getString("title");
//                    String overview = oneMovie.getString("overview");
//                    String release = oneMovie.getString("release_date");
//                    String rating = oneMovie.getString("vote_average");
//                    String poster_path = oneMovie.getString("poster_path");
//                    String backdrop_path = oneMovie.getString("backdrop_path");
                    Object extra = listMovie.get(0);

                    String itemCount = String.valueOf(list.length());
                    if (list.length() > 0) {
                        showNotification(getApplicationContext(), itemCount
                                + (itemCount.length() > 1 ? getString(R.string.release_plural) : getString(R.string.release_singular))
                                + getString(R.string.one_released)
                                + title
                                + "!", extra, id);
                    } else {
                        showNotificationNull(getApplicationContext(), getString(R.string.no_movie_released));
                    }
                    jobFinished(job, false);
                } catch (Exception e) {
                    Log.d("Exception Movie", e.getMessage());
                    jobFinished(job, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure Movie", error.getMessage());
                jobFinished(job, true);
            }
        });
    }

    private void showNotification(Context context, String message, Object extras, int extraId) {
        String CHANNEL_ID = "Channel_2";
        String CHANNEL_NAME = "Release Reminder";
        String title = getString(R.string.released);

        Intent intent = new Intent(context, DetailActivity.class);

        uri = Uri.parse(CONTENT_URI_MOVIE + "/" + extraId);
//        projection = new String[]{
//                _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
//        };
//        selection = _ID + " =?";
//        selectionArgs = new String[]{String.valueOf(extraId)};
//        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        Intent theIntent;
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.close();
//            theIntent = favIntent;
//        } else {
            theIntent = intent;
//        }

        theIntent.putExtra(DetailActivity.EXTRA_MOVIE, (Parcelable) extras);
        theIntent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmReceiver.ID_RELEASED, theIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_movie_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(AlarmReceiver.ID_RELEASED, notification);
        }
    }

    private void showNotificationNull(Context context, String message) {
        String CHANNEL_ID = "Channel_2";
        String CHANNEL_NAME = "Released Reminder";
        String title = getString(R.string.release_reminder);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmReceiver.ID_RELEASED, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_movie_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});

            builder.setChannelId(CHANNEL_ID);

            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(AlarmReceiver.ID_RELEASED, notification);
        }
    }
}
