package com.group4sweng.scranplan.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.group4sweng.scranplan.SupportedFormats;

import java.util.ArrayList;

public class ImageHelpers {

    /** Reference: https://gist.github.com/VassilisPallas/b88fb701c55cdace0c420356ee7c1464 **/
    public static long getSize(Context context, Uri uri) {
        String fileSize = null;
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            assert cursor != null;
            cursor.close();
        }
        assert fileSize != null;
        return Long.parseLong(fileSize);
    }

    public static String getExtension(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mimeMap = MimeTypeMap.getSingleton();
        return  mimeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    public static boolean isImageFormatSupported(Context context, Uri uri){
        String extension = ImageHelpers.getExtension(context, uri);

        for(SupportedFormats.ImageFormats format : SupportedFormats.ImageFormats.values()){
            if(extension.equals(format.toString())){
                return true;
            }
        }
        return false;
    }

    public static String getPrintableSupportedFormats(){
        ArrayList<String> supportedFormats = new ArrayList<>();

        for(SupportedFormats.ImageFormats format : SupportedFormats.ImageFormats.values()){
            supportedFormats.add(format.toString());
        }

        String printableFormats = "";
        for(String pf: supportedFormats){
            printableFormats = pf.concat(" " + printableFormats + " ");
        }
        return printableFormats;
    }

}
