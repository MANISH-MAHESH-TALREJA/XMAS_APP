package com.vpapps.util;

import android.net.Uri;

import net.manish.christmas.BuildConfig;
import com.vpapps.item.ItemAbout;
import com.vpapps.item.ItemMessage;
import com.vpapps.item.ItemWallpaper;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    private static final long serialVersionUID = 1L;

    //server url
    public static String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String SERVER_IMAGE = BuildConfig.SERVER_URL + "images/";

    public static final String METHOD_ABOUT = "get_app_details";
    public static final String METHOD_WALLPAPER = "get_wallpaper";
    public static final String METHOD_MOST_VIEWED_WALLPAPER = "get_wallpaper_view";
    public static final String METHOD_RINGTONE = "get_ringtone";
    public static final String METHOD_QUIZ = "get_quiz";
    public static final String METHOD_QUOTES = "get_sms";

    public static final String METHOD_SINGLE_WALL = "get_single_wallpaper";
    public static final String METHOD_SINGLE_RINGTONE = "get_single_ringtone";
    public static final String METHOD_SINGLE_QUIZ = "get_single_quiz";
    public static final String METHOD_SINGLE_QUOTES = "get_single_sms";

    public static final String TAG_ROOT = "CHRISTMAS_APP";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "msg";

    public static final String TAG_WALL_NAME = "wall_name";
    public static final String TAG_WALL_IMAGE_BIG= "image_b";
    public static final String TAG_WALL_IMAGE_SMALL= "image_s";

    public static final String LATESTMSG_ID = "id";
    public static final String LATESTMSG_URL = "sms";

    public static final String QUIZ_ID = "id";
    public static final String QUIZ_QUES = "quiz_title";
    public static final String QUIZ_A = "option1";
    public static final String QUIZ_B = "option2";
    public static final String QUIZ_C = "option3";
    public static final String QUIZ_D = "option4";
    public static final String QUIZ_ANS = "quiz_ans";

    public static final String TAG_ID = "id";
    public static final String TAG_RING_NAME = "ringtone_name";
    public static final String TAG_RING_URL = "ringtone_link";
    public static final String TAG_RING_DOWNLOAD = "download";
    public static final String TAG_TAGS = "tags";

    public static final String DARK_MODE_ON = "on";
    public static final String DARK_MODE_OFF = "off";
    public static final String DARK_MODE_SYSTEM = "system";

    public static final String AD_TYPE_ADMOB = "admob";
    public static final String AD_TYPE_FACEBOOK = "facebook";
    public static final String AD_TYPE_STARTAPP = "startapp";
    public static final String AD_TYPE_APPLOVIN = "applovins";
    public static final String AD_TYPE_WORTISE = "wortise";

    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;
    public static String DEVICE_ID;

    public static Uri uri_set;
    public static ArrayList<ItemWallpaper> arrayList_wallpaper = new ArrayList<>();
    public static ArrayList<ItemMessage> arrayList_message = new ArrayList<>();

    public static ItemAbout itemAbout;

    public static Boolean isBannerAd = true, isInterstitialAd = true, isNativeAd = true, showUpdateDialog = false, appUpdateCancel = false;
    public static String publisherAdID = "", startapp_id = "", bannerAdType = "admob", interstitialAdType = "admob",
            nativeAdType = "admob", bannerAdID = "", interstitialAdID = "", nativeAdID = "", packageName = "",
            appVersion= "", appUpdateMsg = "", appUpdateURL = "";

    public static int adCount = 1, nativeAdShow = 10, interstitialAdShow = 5;

    public static boolean isQuizEnabled = true;
    public static boolean isWallpaperEnabled = true;
    public static boolean isRingToneEnabled = true;
    public static boolean isMessageEnabled = true;
}