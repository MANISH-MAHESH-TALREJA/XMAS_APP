package com.vpapps.interfaces;

import com.vpapps.item.ItemWallpaper;

import java.util.ArrayList;

public interface WallpaperListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWallpaper);
}