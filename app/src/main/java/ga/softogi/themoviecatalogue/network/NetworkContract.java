package ga.softogi.themoviecatalogue.network;

import ga.softogi.themoviecatalogue.BuildConfig;

public class NetworkContract {
    public static final String IMG_URL = BuildConfig.IMG_URL;
    public static final String VERSION = "/3";
    public static final String MOVIE = "/movie";
    static final String BASE_URL = BuildConfig.BASE_URL;
    static final String API_KEY = BuildConfig.API_KEY;
    static final String TV = "/tv";
    static final String LANG_EN = "en-US";

    static final String MOVIE_PATH = VERSION + MOVIE;
    static final String TV_PATH = VERSION + TV;
}
