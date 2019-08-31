package ga.softogi.themoviecatalogue.reminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
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
import ga.softogi.themoviecatalogue.activity.DetailContentActivity;
import ga.softogi.themoviecatalogue.activity.MainActivity;
import ga.softogi.themoviecatalogue.entity.ContentItem;

import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;

public class ReleasedFilmsService extends JobService {
    private final ArrayList<ContentItem> listContent = new ArrayList<>();
    private int notifId = 11;

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

        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.API_KEY + "&primary_release_date.gte=" + today + "&primary_release_date.lte=" + today;

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
                        int vote_count = movieItem.getInt("vote_count");
                        String poster = movieItem.getString("poster_path");
                        String backdrop_path = movieItem.getString("backdrop_path");
                        String type = ContentItem.TYPE_MOVIE;

                        ContentItem contentData = new ContentItem(id, title, overview, release, rating, vote_count, poster, backdrop_path, type);
//                        if (movieItem.getString("release_date").equals(today)) {
                        listContent.add(contentData);
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
                    Object extra = listContent.get(0);

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

        Intent intent = new Intent(context, DetailContentActivity.class);
        Uri uri = null;
        Uri uriMovie = Uri.parse(CONTENT_URI_MOVIE + "/" + extraId);
//        Uri uriTv = Uri.parse(CONTENT_URI_TV + "/" + extraId);
//        if (Objects.equals(mData.get(position).getType(), ContentItem.TYPE_MOVIE)) {
        uri = uriMovie;
//        } else if (Objects.equals(mData.get(position).getType(), ContentItem.TYPE_TV)) {
//            uri = uriTv;
//        }
        intent.setData(uri);
        intent.putExtra(DetailContentActivity.EXTRA_CONTENT, (Parcelable) extras);
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
