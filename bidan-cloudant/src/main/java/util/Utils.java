/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package util;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.widget.ImageView;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.upperCase;


/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {};




    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

//    public static String humanize(String value) {
//        return capitalize(replace(getValue(value), "_", " "));
//    }
//
//    public static String replaceAndHumanize(String value, String oldCharacter, String newCharacter) {
//        return humanize(replace(getValue(value), oldCharacter, newCharacter));
//    }
//
//    public static String replaceAndHumanizeWithInitCapText(String value, String oldCharacter, String newCharacter) {
//        return humanize(WordUtils.capitalize(replace(getValue(value), oldCharacter, newCharacter)));
//    }
//
//    public static String humanizeAndDoUPPERCASE(String value) {
//        return upperCase(humanize(getValue(value)));
//    }
//
//    public static String getValue(String value) {
//        return isBlank(value)? "" : value;
//    }


    public static void setImagetoHolderFromUri(Activity activity, String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        if (externalFile.exists()) {
            Uri external = Uri.fromFile(externalFile);
            view.setImageURI(external);
        }

    }

}
