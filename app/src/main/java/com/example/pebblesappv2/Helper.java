package com.example.pebblesappv2;

/**
 * Created by ChunFaiHung on 2017/12/26.
 */

public class Helper {

    public int emoji_cyclone = 0x1F300;

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
