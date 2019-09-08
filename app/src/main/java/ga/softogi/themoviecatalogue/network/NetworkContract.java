package ga.softogi.themoviecatalogue.network;

import ga.softogi.themoviecatalogue.BuildConfig;

public class NetworkContract {
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String IMG_URL = BuildConfig.IMG_URL;
    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String VERSION = "/3";
    public static final String MOVIE = "/movie";
    public static final String TV = "/tv";
    public static final String LANG_EN = "en-US";

    public static final String MOVIE_PATH = VERSION + MOVIE;
    public static final String TV_PATH = VERSION + TV;
}
