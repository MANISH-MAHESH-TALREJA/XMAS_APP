package com.vpapps.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import net.manish.christmas.R;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemRingtone;
import com.vpapps.util.Constant;
import com.vpapps.util.Methods;
import com.wortise.ads.AdError;
import com.wortise.ads.natives.GoogleNativeAd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class AdapterRingtone extends RecyclerView.Adapter<AdapterRingtone.MyViewHolder> {

    private final Methods methods;
    private final Context context;
    private final ArrayList<ItemRingtone> arrayList;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private Boolean isFirst = true;
    private int playpos = 0;
    private ImageView roundedImageView;
    private CircularProgressBar progressBar;
    private BottomSheetDialog dialog_setas;
    private final ProgressDialog progressDialog;

    private Boolean isAdLoaded = false;
    private final List<NativeAd> mNativeAdsAdmob = new ArrayList<>();
    List<NativeAdDetails> nativeAdsStartApp = new ArrayList<>();

    public AdapterRingtone(Context context, ArrayList<ItemRingtone> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        methods = new Methods(context, interAdListener);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.downloading));
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_name;
        ImageView imageView_playpause;
        ImageView imageView_more;
        CircularProgressBar progressBar;
        RelativeLayout rl_native_ad;

        MyViewHolder(View view) {
            super(view);
            textView_name = view.findViewById(R.id.tv_ring_name);
            imageView_playpause = view.findViewById(R.id.iv_ring_play);
            imageView_more = view.findViewById(R.id.iv_ring_more);
            progressBar = view.findViewById(R.id.pb_ringtone);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ringtone, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final ItemRingtone itemRingtone = arrayList.get(position);

        holder.textView_name.setText(itemRingtone.getName());

        holder.imageView_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextThemeWrapper ctw;
                if (methods.isDarkMode()) {
                    ctw = new ContextThemeWrapper(context, R.style.PopupMenuDark);
                } else {
                    ctw = new ContextThemeWrapper(context, R.style.PopupMenuLight);
                }

                PopupMenu popupMenu = new PopupMenu(ctw, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.item_popup_save) {
                            if (methods.checkPer()) {
                                methods.showInterAd(holder.getAdapterPosition(), ctw.getString(R.string.download));
                            }
                        } else if (itemId == R.id.item_popup_share) {
                            if (methods.checkPer()) {
                                methods.showInterAd(holder.getAdapterPosition(), ctw.getString(R.string.share));
                            }
                        } else if (itemId == R.id.item_popup_setas) {
                            if (methods.checkPer()) {
                                methods.showInterAd(holder.getAdapterPosition(), ctw.getString(R.string.set_as));
                            }
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        holder.imageView_playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirst) {
                    if (!arrayList.get(playpos).getId().equals(itemRingtone.getId())) {
                        progressBar.setVisibility(View.GONE);
                        roundedImageView.setVisibility(View.VISIBLE);

                        roundedImageView.setImageResource(R.drawable.play);
                        roundedImageView = holder.imageView_playpause;
                        progressBar = holder.progressBar;

                        progressBar.setVisibility(View.VISIBLE);
                        roundedImageView.setVisibility(View.GONE);

                        playpos = holder.getAdapterPosition();
                        playRingTone(holder.getAdapterPosition());
                    } else {
                        playpause();
                    }
                } else {
                    roundedImageView = holder.imageView_playpause;
                    progressBar = holder.progressBar;

                    progressBar.setVisibility(View.VISIBLE);
                    roundedImageView.setVisibility(View.GONE);

                    isFirst = false;
                    playpos = holder.getAdapterPosition();
                    playRingTone(holder.getAdapterPosition());
                }
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();

                roundedImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                roundedImageView.setImageResource(R.drawable.pause);
            }
        });

        if (Constant.isNativeAd && isAdLoaded && (position != arrayList.size() - 1) && (position + 1) % Constant.nativeAdShow == 0) {
            try {
                if (holder.rl_native_ad.getChildCount() == 0) {
                    switch (Constant.nativeAdType) {
                        case Constant.AD_TYPE_ADMOB:
                        case Constant.AD_TYPE_FACEBOOK:
                            if (mNativeAdsAdmob.size() >= 1) {

                                int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);

                                NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                                holder.rl_native_ad.removeAllViews();
                                holder.rl_native_ad.addView(adView);

                                holder.rl_native_ad.setVisibility(View.VISIBLE);
                            }
                            break;
                        case Constant.AD_TYPE_WORTISE:
                            GoogleNativeAd googleNativeAd = new GoogleNativeAd(
                                    context, Constant.nativeAdID, new GoogleNativeAd.Listener() {
                                @Override
                                public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                                    NativeAdView adView = (NativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_wortise, null);
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    holder.rl_native_ad.removeAllViews();
                                    holder.rl_native_ad.addView(adView);

                                    holder.rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.wortise.ads.AdError adError) {

                                }

                                @Override
                                public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {

                                }

                                @Override
                                public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {

                                }
                            });
                            googleNativeAd.load();
                            break;
                        case Constant.AD_TYPE_STARTAPP:
                            int i = new Random().nextInt(nativeAdsStartApp.size() - 1);

                            RelativeLayout nativeAdView = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_startapp, null);
                            populateStartAppNativeAdView(nativeAdsStartApp.get(i), nativeAdView);

                            holder.rl_native_ad.removeAllViews();
                            holder.rl_native_ad.addView(nativeAdView);
                            holder.rl_native_ad.setVisibility(View.VISIBLE);
                            break;
                        case Constant.AD_TYPE_APPLOVIN:
                            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.nativeAdID, context);
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                                    nativeAdView.setPadding(0, 0, 0, 10);
                                    nativeAdView.setBackgroundColor(Color.WHITE);
                                    holder.rl_native_ad.removeAllViews();
                                    holder.rl_native_ad.addView(nativeAdView);
                                    holder.rl_native_ad.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                                }

                                @Override
                                public void onNativeAdClicked(final MaxAd ad) {
                                }
                            });

                            nativeAdLoader.loadAd();
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadPlay extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Uri myUri = Uri.parse(arrayList.get(Integer.parseInt(strings[0])).getUrl());
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, myUri); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void populateStartAppNativeAdView(NativeAdDetails nativeAdDetails, RelativeLayout nativeAdView) {
        ImageView icon = nativeAdView.findViewById(R.id.icon);
        TextView title = nativeAdView.findViewById(R.id.title);
        TextView description = nativeAdView.findViewById(R.id.description);
        Button button = nativeAdView.findViewById(R.id.button);

        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
        title.setText(nativeAdDetails.getTitle());
        description.setText(nativeAdDetails.getDescription());
        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
    }

    public void addAds(NativeAd nativeAd) {
        mNativeAdsAdmob.add(nativeAd);
        isAdLoaded = true;
    }

    public void addNativeAds(ArrayList<NativeAdDetails> nativeAdDetails) {
        nativeAdsStartApp.addAll(nativeAdDetails);
        isAdLoaded = true;
    }

    public void setNativeAds(boolean isLoaded) {
        isAdLoaded = isLoaded;
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playRingTone(int pos) {
        new LoadPlay().execute(String.valueOf(pos));
    }

    private void playpause() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            roundedImageView.setImageResource(R.drawable.pause);
        } else {
            mediaPlayer.pause();
            roundedImageView.setImageResource(R.drawable.play);
        }
    }

    private void showBottomSheetDialog(final int pos) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_ringtone_setas, null);

        dialog_setas = new BottomSheetDialog(context);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        LinearLayout ll_set_ring = dialog_setas.findViewById(R.id.ll_set_ring);
        LinearLayout ll_set_noti = dialog_setas.findViewById(R.id.ll_set_noti);
        LinearLayout ll_set_alarm = dialog_setas.findViewById(R.id.ll_set_alarm);

        ll_set_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, context.getString(R.string.set_as_ring));
            }
        });

        ll_set_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, context.getString(R.string.set_as_noti));
            }
        });

        ll_set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, context.getString(R.string.set_as_alarm));
            }
        });
    }

    private void setRingtone(final int pos, final String type) {
        boolean settingsCanWrite = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            settingsCanWrite = Settings.System.canWrite(context);

            if (!settingsCanWrite) {
                // If do not have write settings permission then open the Can modify system settings panel.
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                context.startActivity(intent);
            } else {
                loadRingTone(pos, type);
            }
        } else {
            loadRingTone(pos, type);
        }
    }

    private void loadRingTone(final int pos, final String type) {
        new LoadDownloadRing(type, pos).execute();
    }

    class LoadDownloadRing extends AsyncTask<String, String, String> {

        int pos;
        String type;

        LoadDownloadRing(String type, int pos) {
            this.type = type;
            this.pos = pos;

            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                int count;
                String ringtone_url = arrayList.get(pos).getUrl();
                String fileName = arrayList.get(pos).getName() + ".mp3";
                URL url = new URL(ringtone_url);

                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    String filePath = Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name);

                    if (!new File(Environment.getExternalStorageDirectory() + File.separator + filePath, fileName).exists()) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
                        values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

                        values.put(MediaStore.Audio.Media.TITLE, fileName);
                        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);

                        values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());

                        values.put(MediaStore.Audio.Media.RELATIVE_PATH, filePath);

                        values.put(MediaStore.Audio.Media.IS_PENDING, true);

                        Uri uri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                        Constant.uri_set = uri;
                        if (uri != null) {
                            try {
                                URLConnection connection = url.openConnection();
                                connection.setRequestProperty("Accept-Encoding", "identity");
                                connection.connect();
                                int lenghtOfFile = connection.getContentLength();

                                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                                InputStream input = new BufferedInputStream(new URL(ringtone_url).openStream(), 8192);

                                byte[] data = new byte[1024];

                                long total = 0;

                                while ((count = input.read(data)) != -1) {
                                    total += count;
                                    progressDialog.setProgress((int) ((total * 100) / lenghtOfFile));
                                    outputStream.write(data, 0, count);
                                }

                                outputStream.close();

                                values.put(MediaStore.Audio.Media.IS_PENDING, false);
                                context.getContentResolver().update(uri, values, null, null);

                                return "1";
                            } catch (Exception e) {
                                return "0";
                            }
                        } else {
                            return "0";
                        }
                    } else {
                        return "1";
                    }

                } else {
                    try {
                        File root = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name));
                        if (!new File(root, fileName).exists()) {
                            if (!root.exists()) {
                                root.mkdirs();
                            }

                            URLConnection connection = url.openConnection();
                            connection.setRequestProperty("Accept-Encoding", "identity");
                            connection.connect();
                            int lenghtOfFile = connection.getContentLength();

                            InputStream input = new BufferedInputStream(url.openStream(), 8192);
                            OutputStream output = new FileOutputStream(new File(root, fileName));

                            byte[] data = new byte[1024];

                            long total = 0;

                            while ((count = input.read(data)) != -1) {
                                total += count;
                                progressDialog.setProgress((int) ((total * 100) / lenghtOfFile));
                                output.write(data, 0, count);
                            }

                            output.flush();
                            output.close();
                            input.close();
                        }
                        return "1";
                    } catch (Exception e) {
                        return "0";
                    }
                }
            } catch (Exception e) {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                if (context.getString(R.string.set_as_ring).equals(type)) {
                    setAsRingtone(arrayList.get(pos));
                } else if (context.getString(R.string.set_as_noti).equals(type)) {
                    setNoti(arrayList.get(pos));
                } else if (context.getString(R.string.set_as_alarm).equals(type)) {
                    setAlarm(arrayList.get(pos));
                } else if (context.getString(R.string.download).equals(type)) {
                    methods.showToast(context.getString(R.string.ringtone_saved));
                }
            } else {
                methods.showToast(context.getString(R.string.try_again));
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }

    }

    private void setAsRingtone(ItemRingtone itemRingtone) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name) + File.separator + itemRingtone.getName() + ".mp3";

        File ringtoneFile = new File(filePath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, itemRingtone.getName() + ".mp3");
            values.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

            Uri newUri = context.getContentResolver()
                    .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = context.getContentResolver().openOutputStream(newUri)) {
                int size = (int) ringtoneFile.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(ringtoneFile));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    os.write(bytes);
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        } else {

            MediaMetadataRetriever md = new MediaMetadataRetriever();
            md.setDataSource(ringtoneFile.getAbsolutePath());
            String title = ringtoneFile.getName();
            String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            ContentValues content = new ContentValues();
            content.put(MediaStore.MediaColumns.DATA, filePath);
            content.put(MediaStore.MediaColumns.TITLE, title);
            content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            content.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            content.put(MediaStore.Audio.Media.ARTIST, artist);
            content.put(MediaStore.Audio.Media.DURATION, duration);
            content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            content.put(MediaStore.Audio.Media.IS_ALARM, true);
            content.put(MediaStore.Audio.Media.IS_MUSIC, true);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
            Uri newUri = context.getContentResolver().insert(uri, content);

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
        }
        methods.showToast(context.getString(R.string.ringtone_set));
    }

    private void setAlarm(ItemRingtone itemRingtone) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name) + File.separator + itemRingtone.getName() + ".mp3";

        File ringtoneFile = new File(filePath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, itemRingtone.getName() + ".mp3");
            values.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);

            Uri newUri = context.getContentResolver()
                    .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = context.getContentResolver().openOutputStream(newUri)) {
                int size = (int) ringtoneFile.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(ringtoneFile));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    os.write(bytes);
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
        } else {

            MediaMetadataRetriever md = new MediaMetadataRetriever();
            md.setDataSource(ringtoneFile.getAbsolutePath());
            String title = ringtoneFile.getName();
            String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            ContentValues content = new ContentValues();
            content.put(MediaStore.MediaColumns.DATA, filePath);
            content.put(MediaStore.MediaColumns.TITLE, title);
            content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            content.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            content.put(MediaStore.Audio.Media.ARTIST, artist);
            content.put(MediaStore.Audio.Media.DURATION, duration);
            content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            content.put(MediaStore.Audio.Media.IS_ALARM, true);
            content.put(MediaStore.Audio.Media.IS_MUSIC, true);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
            Uri newUri = context.getContentResolver().insert(uri, content);

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);
        }
        methods.showToast(context.getString(R.string.alarm_set));
    }

    private void setNoti(ItemRingtone itemRingtone) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_RINGTONES + File.separator + context.getString(R.string.app_name) + File.separator + itemRingtone.getName() + ".mp3";

        File ringtoneFile = new File(filePath);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, itemRingtone.getName() + ".mp3");
            values.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);

            Uri newUri = context.getContentResolver()
                    .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            try (OutputStream os = context.getContentResolver().openOutputStream(newUri)) {
                int size = (int) ringtoneFile.length();
                byte[] bytes = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(ringtoneFile));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
                    os.write(bytes);
                    os.close();
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
        } else {
            MediaMetadataRetriever md = new MediaMetadataRetriever();
            md.setDataSource(ringtoneFile.getAbsolutePath());
            String title = ringtoneFile.getName();
            String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            ContentValues content = new ContentValues();
            content.put(MediaStore.MediaColumns.DATA, filePath);
            content.put(MediaStore.MediaColumns.TITLE, title);
            content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
            content.put(MediaStore.MediaColumns.MIME_TYPE, methods.getMIMEType(filePath));
            content.put(MediaStore.Audio.Media.ARTIST, artist);
            content.put(MediaStore.Audio.Media.DURATION, duration);
            content.put(MediaStore.Audio.Media.IS_RINGTONE, false);
            content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            content.put(MediaStore.Audio.Media.IS_ALARM, false);
            content.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
            Uri newUri = context.getContentResolver().insert(uri, content);

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);
        }
        methods.showToast(context.getString(R.string.noti_set));
    }

//    private void loadRingtoneAPI(int pos, String type) {
//        if (methods.isNetworkAvailable()) {
//            LoadRingtone loadRingtone = new LoadRingtone(new RingtoneListener() {
//
//                ProgressDialog pDialog;
//
//                @Override
//                public void onStart() {
//                    pDialog = new ProgressDialog(context);
//                    pDialog.setMessage(context.getString(R.string.loading));
//                    pDialog.setCancelable(false);
//                    pDialog.show();
//                }
//
//                @Override
//                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRingtone> arrayListRingtone) {
//                    pDialog.dismiss();
//                    if (success.equals("1")) {
//                        if (!verifyStatus.equals("-1")) {
//
//                        }
//                    }
//                }
//            }, methods.getAPIRequest(Constant.METHOD_SINGLE_RINGTONE, 0, "", arrayList.get(pos).getId(), ""));
//            loadRingtone.execute();
//        }
//    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            if (type.equals(context.getString(R.string.download))) {
                if (methods.isNetworkAvailable()) {
                    loadRingTone(position, type);
//                    methods.saveImage(arrayList.get(position).getUrl(), context.getString(R.string.download), context.getString(R.string.ringtone), arrayList.get(position).getName());
                } else {
                    methods.showToast(context.getString(R.string.net_not_conn));
                }
            } else if (type.equals(context.getString(R.string.share))) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.christmas_ringtone) + arrayList.get(position).getName() + " - " + context.getString(R.string.download_this_ring) + "\n\n " + context.getString(R.string.app_name) + " - https://play.google.com/store/apps/details?id=" + context.getPackageName());
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share)));
//                methods.saveImage(arrayList.get(position).getUrl(), context.getString(R.string.share), context.getString(R.string.ringtone), arrayList.get(position).getName());
            } else {
                if (methods.isNetworkAvailable()) {
                    showBottomSheetDialog(position);
                } else {
                    methods.showToast(context.getString(R.string.net_not_conn));
                }
            }
        }
    };
}