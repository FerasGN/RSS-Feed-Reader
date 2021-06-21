package de.htwsaar.pib2021.rss_feed_reader.constants;

public class Constants {

    public final static String VIEW_CARDS = "cards";
    public final static String VIEW_TITLE_ONLY = "title-only";

    public final static String PERIOD_TODAY = "today";
    public final static String PERIOD_LAST_SEVEN_DAYS = "last-seven-days";
    public final static String PERIOD_LAST_THIRTY_DAYS = "last-thirty-days";
    public final static String PERIOD_ALL = "all";

    public final static String ORDER_BY_LATEST = "latest";
    public final static String ORDER_BY_CATEGORY = "category";
    public final static String ORDER_BY_ALL_CATEGORIES = "all-categories";
    public final static String ORDER_BY_UNREAD = "unread";
    public final static String ORDER_BY_CHANNEL = "channel";
    public final static String ORDER_BY_OLDEST = "oldest";

    public static final int PAGE_SIZE = 12;

    /**
     * User agent header to be used by the Jsoup connection
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

    /**
     * name of location header
     */
    public static final String LOCATION = "location";
}
