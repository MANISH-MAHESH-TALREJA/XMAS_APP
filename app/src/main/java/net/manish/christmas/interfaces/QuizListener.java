package com.vpapps.interfaces;

import com.vpapps.item.ItemQuiz;
import com.vpapps.item.ItemWallpaper;

import java.util.ArrayList;

public interface QuizListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuiz> arrayListQuiz);
}