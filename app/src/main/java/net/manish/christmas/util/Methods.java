package com.vpapps.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import net.manish.christmas.BuildConfig;
import net.manish.christmas.R;
import net.manish.christmas.SetAsWallpaperActivity;
import com.vpapps.interfaces.InterAdListener;
import com.wortise.ads.AdError;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.banner.BannerAd;
import com.wortise.ads.interstitial.InterstitialAd;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Methods {

    private Context context;
    private InterAdListener interAdListener;
    private boolean isClicked = false;

    private static final String ALGORITHM = "Blowfish";
    private static final String MODE = "Blowfish/CBC/PKCS5Padding";

    public Methods(Context context) {
        this.context = context;
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public void setStatusColor(Window window) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(R.color.statusbarcolor));
        }
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }

    public String getDarkMode() {
        SharedPref sharedPref = new SharedPref(context);
        return sharedPref.getDarkMode();
    }

    public boolean isAdmobFBAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_ADMOB) ||
                Constant.bannerAdType.equals(Constant.AD_TYPE_FACEBOOK) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_FACEBOOK) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_FACEBOOK);
    }

    public boolean isStartAppAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_STARTAPP) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_STARTAPP) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_STARTAPP);
    }

    public boolean isApplovinAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_APPLOVIN) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_APPLOVIN) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_APPLOVIN);
    }

    public boolean isWortiseAds() {
        return Constant.bannerAdType.equals(Constant.AD_TYPE_WORTISE) ||
                Constant.interstitialAdType.equals(Constant.AD_TYPE_WORTISE) ||
                Constant.nativeAdType.equals(Constant.AD_TYPE_WORTISE);
    }

    public void initializeAds() {
        if (isAdmobFBAds()) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }

        if (isStartAppAds()) {
            StartAppSDK.init(context, Constant.startapp_id, false);
            StartAppAd.disableSplash();
        }

        if (isWortiseAds()) {
            WortiseSdk.initialize(context, context.getString(R.string.wortise_app_id));
        }

        if (isApplovinAds()) {
            if (!AppLovinSdk.getInstance(context).isInitialized()) {
                AppLovinSdk.initializeSdk(context);
                AppLovinSdk.getInstance(context).setMediationProvider("max");
//                AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("bb6822d9-18de-41b0-994e-41d4245a4d63", "749d75a2-1ef2-4ff9-88a5-c50374843ac6"));
            }
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable() && Constant.isBannerAd) {
            switch (Constant.bannerAdType) {
                case Constant.AD_TYPE_ADMOB:
                case Constant.AD_TYPE_FACEBOOK:
                    if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                        showNonPersonalizedAds(linearLayout);
                    } else {
                        showPersonalizedAds(linearLayout);
                    }
                    break;
                case Constant.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(context);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    break;
                case Constant.AD_TYPE_APPLOVIN:
                    MaxAdView adView = new MaxAdView(Constant.bannerAdID, context);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    linearLayout.addView(adView);
                    adView.loadAd();
                    break;
                case Constant.AD_TYPE_WORTISE:
                    BannerAd mBannerAd = new BannerAd(context);
                    mBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                    mBannerAd.setAdUnitId(Constant.bannerAdID);
                    linearLayout.addView(mBannerAd);
                    mBannerAd.loadAd();
                    linearLayout.setGravity(Gravity.CENTER);
                    break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void showPersonalizedAds(LinearLayout linearLayout) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, new Bundle())
                .build();
        adView.setAdUnitId(Constant.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    @SuppressLint("MissingPermission")
    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.setAdUnitId(Constant.bannerAdID);
        adView.setAdSize(AdSize.BANNER);
        linearLayout.addView(adView);
        adView.loadAd(adRequest);
    }

    public void showInterAd(final int pos, final String type) {
        if (Constant.isInterstitialAd) {
            Constant.adCount = Constant.adCount + 1;
            if (Constant.adCount % Constant.interstitialAdShow == 0) {
                switch (Constant.interstitialAdType) {
                    case Constant.AD_TYPE_ADMOB:
                    case Constant.AD_TYPE_FACEBOOK:
                        final AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(context);
                        if (adManagerInterAdmob.getAd() != null) {
                            adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdDismissedFullScreenContent();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                    AdManagerInterAdmob.setAd(null);
                                    adManagerInterAdmob.createAd();
                                    interAdListener.onClick(pos, type);
                                    super.onAdFailedToShowFullScreenContent(adError);
                                }
                            });
                            adManagerInterAdmob.getAd().show((Activity) context);
                        } else {
                            AdManagerInterAdmob.setAd(null);
                            adManagerInterAdmob.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Constant.AD_TYPE_STARTAPP:
                        final AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(context);
                        if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                            adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                                @Override
                                public void adHidden(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void adDisplayed(Ad ad) {

                                }

                                @Override
                                public void adClicked(Ad ad) {

                                }

                                @Override
                                public void adNotDisplayed(Ad ad) {
                                    AdManagerInterStartApp.setAd(null);
                                    adManagerInterStartApp.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterStartApp.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Constant.AD_TYPE_APPLOVIN:
                        final AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(context);
                        if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                            adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {

                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {

                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {

                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    AdManagerInterApplovin.setAd(null);
                                    adManagerInterApplovin.createAd();
                                    interAdListener.onClick(pos, type);
                                }
                            });
                            adManagerInterApplovin.getAd().showAd();
                        } else {
                            AdManagerInterStartApp.setAd(null);
                            adManagerInterApplovin.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                    case Constant.AD_TYPE_WORTISE:
                        final AdManagerInterWortise adManagerInterWortise = new AdManagerInterWortise(context);
                        if (adManagerInterWortise.getAd() != null && adManagerInterWortise.getAd().isAvailable()) {
                            adManagerInterWortise.getAd().setListener(new InterstitialAd.Listener() {
                                @Override
                                public void onInterstitialClicked(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialDismissed(@NonNull InterstitialAd interstitialAd) {
                                    AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialFailed(@NonNull InterstitialAd interstitialAd, @NonNull AdError adError) {
                                    AdManagerInterWortise.setAd(null);
                                    adManagerInterWortise.createAd();
                                    interAdListener.onClick(pos, type);
                                }

                                @Override
                                public void onInterstitialLoaded(@NonNull InterstitialAd interstitialAd) {

                                }

                                @Override
                                public void onInterstitialShown(@NonNull InterstitialAd interstitialAd) {

                                }
                            });
                            adManagerInterWortise.getAd().showAd();
                        } else {
                            AdManagerInterWortise.setAd(null);
                            adManagerInterWortise.createAd();
                            interAdListener.onClick(pos, type);
                        }
                        break;
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public static String encrypt(String value) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
        Cipher cipher;
        byte[] values;
        try {
            cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            values = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(values, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String decrypt(String value) {
        byte[] values;
        Cipher cipher;
        try {
            values = Base64.decode(value, Base64.DEFAULT);
            SecretKeySpec secretKeySpec = new SecretKeySpec(BuildConfig.ENC_KEY.getBytes(), ALGORITHM);
            cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(BuildConfig.IV.getBytes()));
            return new String(cipher.doFinal(values));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public void saveImage(String img_url, String option, String postName) {
        new LoadShare(option, postName).execute(img_url, FilenameUtils.getName(img_url));
    }

    public class LoadShare extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        String option, filePath, postName;
        File file;

        LoadShare(String option, String postName) {
            this.option = option;
            this.postName = postName;
        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(context, android.app.AlertDialog.THEME_HOLO_LIGHT);
            if (option.equals(context.getString(R.string.download))) {
                pDialog.setMessage(context.getResources().getString(R.string.downloading));
            } else {
                pDialog.setMessage(context.getResources().getString(R.string.please_wait));
            }
            pDialog.setIndeterminate(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[1];
            try {
                if (!option.equalsIgnoreCase(context.getString(R.string.download))) {
                    filePath = context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator + name;
                } else {
                    filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator + File.separator + name;
                }
                file = new File(filePath);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;

                    if (strings[0].contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Accept", "*/*");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                    }

                    if (option.equalsIgnoreCase(context.getString(R.string.download))) {
                        boolean isSaved = false;
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        isSaved = saveImage(bitmap, name, option);

                        if (isSaved) {
                            return "1";
                        } else {
                            return "2";
                        }
                    } else {
                        if (file.createNewFile()) {
                            file.createNewFile();
                        }

                        FileOutputStream fileOutput = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        return "1";
                    }
                } else {
                    return "2";
                }
            } catch (IOException e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            pDialog.dismiss();

            if (option.equals(context.getString(R.string.download))) {
                if (s.equals("2")) {
                    showToast(context.getResources().getString(R.string.wallpaper_saved));
                } else {
                    showToast(context.getResources().getString(R.string.wallpaper_saved));
                }
            } else if (option.equals(context.getString(R.string.set_as_wallpaper))) {
                Constant.uri_set = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);
                Intent intent = new Intent(context, SetAsWallpaperActivity.class);
                context.startActivity(intent);
            } else if (option.equals(context.getString(R.string.share))) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName().concat(".fileprovider"), file);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.get_more_wall) + "\n" + context.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
            }

            super.onPostExecute(s);
        }
    }

    private boolean saveImage(Bitmap bitmap, String fileName, String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && type.equalsIgnoreCase(context.getString(R.string.download))) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.app_name));
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();

                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            File directory;

            if (!type.equals(context.getString(R.string.download))) {
                directory = new File(context.getExternalCacheDir().getAbsoluteFile().getAbsolutePath() + File.separator);
            } else {
                directory = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + context.getString(R.string.app_name) + File.separator);
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);

            try {
                OutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public String getMIMEType(String url) {
        String mType = null;
        String mExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension);
        }
        return mType;
    }

    public Boolean checkPer() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            if ((ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ((Activity) context).requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 22);
                return false;
            } else {
                return true;
            }
        } else {
            if ((ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Activity) context).requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 22);
                    return false;
                }
            }
            return true;
        }
    }

    public void showUpdateAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(context.getString(R.string.update));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = Constant.appUpdateURL;
                if (url.equals("")) {
                    url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);

                ((Activity) context).finish();
            }
        });
        if (Constant.appUpdateCancel) {
            alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        } else {
            alertDialog.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).finish();
                }
            });
        }
        alertDialog.show();
    }


    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public RequestBody getAPIRequest(String method, int page, String deviceID, String itemID, String searchText) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_SINGLE_WALL:
                jsObj.addProperty("wall_id", itemID);
                break;

            case Constant.METHOD_SINGLE_RINGTONE:
                jsObj.addProperty("ring_id", itemID);
                break;

            case Constant.METHOD_SINGLE_QUIZ:
                jsObj.addProperty("quiz_id", itemID);
                break;

            case Constant.METHOD_SINGLE_QUOTES:
                jsObj.addProperty("sms_id", itemID);
                break;
        }

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", API.toBase64(jsObj.toString()))
                .build();
    }
}