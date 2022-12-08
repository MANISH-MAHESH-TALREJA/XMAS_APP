package com.vpapps.interfaces;

import com.vpapps.item.ItemMessage;
import com.vpapps.item.ItemWallpaper;

import java.util.ArrayList;

public interface MessageListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemMessage> arrayListMessage);
}