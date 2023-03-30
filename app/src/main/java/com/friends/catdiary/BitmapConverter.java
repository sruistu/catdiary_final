package com.friends.catdiary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class BitmapConverter {

    //비트맵 에서 스트링
    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] arr = stream.toByteArray();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(arr);
        } else {
            return "";
        }
    }

    //스트링에서 비트맵
    public Bitmap stringToBitmap(String encodedString) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] encodeByte = Base64.getDecoder().decode(encodedString);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } else {
            return null;
        }
    }
}
