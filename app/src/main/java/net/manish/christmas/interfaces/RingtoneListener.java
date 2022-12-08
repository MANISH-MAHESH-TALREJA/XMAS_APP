package com.vpapps.interfaces;

import com.vpapps.item.ItemRingtone;

import java.util.ArrayList;

public interface RingtoneListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRingtone> arrayListRingtone);
}