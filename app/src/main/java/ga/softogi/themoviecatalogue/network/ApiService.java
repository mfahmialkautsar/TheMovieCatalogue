package ga.softogi.themoviecatalogue.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ga.softogi.themoviecatalogue.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private ApiInterface apiInterface;

    public ApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder())
                .baseUrl(NetworkContract.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    private OkHttpClient builder() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
        okHttpClient.connectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(20, TimeUnit.SECONDS);
        okHttpClient.readTimeout(60, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(interceptor());
        }

        okHttpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url()
                        .newBuilder()
                        .addQueryParameter("api_key", NetworkContract.API_KEY)
                        .addQueryParameter("language", NetworkContract.LANG_EN)
                        .build();

                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        return okHttpClient.build();
    }

    private static HttpLoggingInterceptor interceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

//    public void getDiscoverMovies(int page, Callback callback) {
//        apiInterface.discoverMovies(page).enqueue(callback);
//    }
//
//    public void getMovieDetail(int movieId, Callback callback) {
//        apiInterface.movieDetail(movieId).enqueue(callback);
//    }
//
//    public void getSearchMovie(int page, String keyword, Callback callback) {
//        apiInterface.searchMovie(page, keyword).enqueue(callback);
//    }
//
//    public void getDiscoverTvs(int page, Callback callback) {
//        apiInterface.discoverTvs(page).enqueue(callback);
//    }
//
//    public void getTvDetail(int tvId, Callback callback) {
//        apiInterface.tvDetail(tvId).enqueue(callback);
//    }
//
//    public void getSearchTv(int page, String keyword, Callback callback) {
//        apiInterface.searchTv(page, keyword).enqueue(callback);
//    }
}
