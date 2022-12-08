package com.vpapps.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.vpapps.interfaces.WallpaperListener;
import com.vpapps.item.ItemWallpaper;
import com.vpapps.util.Constant;
import com.vpapps.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadWallpapers extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private WallpaperListener wallpaperListener;
    private ArrayList<ItemWallpaper> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadWallpapers(WallpaperListener wallpaperListener, RequestBody requestBody) {
        this.wallpaperListener = wallpaperListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        wallpaperListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Constant.TAG_SUCCESS)) {
                    String id = objJson.getString(Constant.TAG_ID);
                    String name = objJson.getString(Constant.TAG_WALL_NAME);
                    String tag = objJson.getString(Constant.TAG_TAGS);
                    String imagebig = objJson.getString(Constant.TAG_WALL_IMAGE_BIG);
                    String imagesmall = objJson.getString(Constant.TAG_WALL_IMAGE_SMALL);

                    ItemWallpaper itemWallpaper = new ItemWallpaper(id, name, tag, imagebig, imagesmall);
                    arrayList.add(itemWallpaper);
                } else {
                    verifyStatus = objJson.getString(Constant.TAG_SUCCESS);
                    message = objJson.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        wallpaperListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}