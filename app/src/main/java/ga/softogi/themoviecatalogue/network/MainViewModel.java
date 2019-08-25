package ga.softogi.themoviecatalogue.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import ga.softogi.themoviecatalogue.BuildConfig;
import ga.softogi.themoviecatalogue.entity.ContentItem;

public class MainViewModel extends ViewModel {
    private static final String MOVIE_ERROR_MESSAGE = "Movie ERROR! Refresh!";
    private static final String TV_ERROR_MESSAGE = "TV ERROR! Refresh!";
    private final ArrayList<ContentItem> listContent = new ArrayList<>();
    private MutableLiveData<ArrayList<ContentItem>> listData = new MutableLiveData<>();
    private AsyncHttpClient client = new AsyncHttpClient();

    public void setMovie(final String lang, String movieTitle) {
        String url;
        String urlMovie = "https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.API_KEY + "&language=" + lang;
        String urlMovieSearch = "https://api.themoviedb.org/3/search/movie?api_key=" + BuildConfig.API_KEY + "&language=" + lang + "&query=" + movieTitle;

        if (TextUtils.isEmpty(movieTitle)) {
            url = urlMovie;
        } else {
            url = urlMovieSearch;
        }

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
                        if (lang.equals("en-US")) {
                            title = movieItem.getString("title");
                        } else if (lang.equals("id-ID")) {
                            title = movieItem.getString("original_title");
                        }
                        String overview = movieItem.getString("overview");
                        String release = movieItem.getString("release_date");
                        String rating = movieItem.getString("vote_average");
                        String poster = movieItem.getString("poster_path");
                        String backdrop = movieItem.getString("backdrop_path");
                        String type = ContentItem.TYPE_MOVIE;

                        ContentItem contentData = new ContentItem(id, title, overview, release, rating, poster, backdrop, type);
                        listContent.add(contentData);
                    }
                    listData.postValue(listContent);
                } catch (Exception e) {
                    Log.d("Exception Movie", e.getMessage());
                    ContentItem content = new ContentItem();
                    content.setTitle(e.getMessage());
                    content.setOverview(MOVIE_ERROR_MESSAGE);
                    content.setRelease("error");
                    listContent.add(content);
                    listData.postValue(listContent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure Movie", error.getMessage());
                ContentItem content = new ContentItem();
                content.setTitle(error.getMessage());
                content.setOverview(MOVIE_ERROR_MESSAGE);
                content.setRelease("error");
                listContent.add(content);
                listData.postValue(listContent);
            }
        });
    }

    public void setTv(final String lang, String tvTitle) {
        String url;
        String urlTv = "https://api.themoviedb.org/3/discover/tv?api_key=" + BuildConfig.API_KEY + "&language=" + lang;
        String urlTvSearch = "https://api.themoviedb.org/3/search/tv?api_key=" + BuildConfig.API_KEY + "&language=" + lang + "&query=" + tvTitle;

        if (TextUtils.isEmpty(tvTitle)) {
            url = urlTv;
        } else {
            url = urlTvSearch;
        }

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject tvItem = list.getJSONObject(i);

                        int id = tvItem.getInt("id");
                        String title = tvItem.getString("name");
                        if (lang.equals("en-US")) {
                            title = tvItem.getString("name");
                        } else if (lang.equals("id-ID")) {
                            title = tvItem.getString("original_name");
                        }
                        String overview = tvItem.getString("overview");
                        String firstAir = tvItem.getString("first_air_date");
                        String rating = tvItem.getString("vote_average");
                        String poster = tvItem.getString("poster_path");
                        String backdrop = tvItem.getString("backdrop_path");
                        String type = ContentItem.TYPE_TV;

                        ContentItem contentData = new ContentItem(id, title, overview, firstAir, rating, poster, backdrop, type);
                        listContent.add(contentData);
                    }
                    listData.postValue(listContent);
                } catch (Exception e) {
                    Log.d("Exception Tv", e.getMessage());
                    ContentItem content = new ContentItem();
                    content.setTitle(e.getMessage());
                    content.setOverview(TV_ERROR_MESSAGE);
                    content.setRelease("error");
                    listContent.add(content);
                    listData.postValue(listContent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure Tv", error.getMessage());
                ContentItem content = new ContentItem();
                content.setTitle(error.getMessage());
                content.setOverview(TV_ERROR_MESSAGE);
                content.setRelease("error");
                listContent.add(content);
                listData.postValue(listContent);
            }
        });
    }

    public LiveData<ArrayList<ContentItem>> getContent() {
        return listData;
    }
}
