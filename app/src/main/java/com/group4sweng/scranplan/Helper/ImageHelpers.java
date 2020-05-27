package com.group4sweng.scranplan.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.group4sweng.scranplan.SupportedFormats;
import com.group4sweng.scranplan.UserInfo.FilterType;

import java.util.ArrayList;

/** Static image helper functions.
 *  Author: JButler, (Credits & references given to external authors)
 *  (c) CoDev 2020 **/
public class ImageHelpers implements FilterType {

    /** Returns the size of an image file. Uses Androids content resolver to query using only a uri input
     *  and a cursor to search through the associated database for the 'Size' column.
     *  @return - Size of image file in bytes.
     *
     *  Author: VassilisPallas
     *  Reference: https://gist.github.com/VassilisPallas/b88fb701c55cdace0c420356ee7c1464
     *  Fair use statement: https://github.com/SWEngGroup4/scranplan/issues/59**/
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

    /** Gets the file extension from a singleton MIME map that grabs the associated image uri extension type from the given context
     * @return - Associated extension name. IE 'jpeg', 'gif' **/
    public static String getExtension(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mimeMap = MimeTypeMap.getSingleton();
        return  mimeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    /** Check if our image format is supported by checking against or list of supported extensions within the 'ImageFormats' enum
     * @return - Boolean value for if the image format is supported.
     **/
    public static boolean isImageFormatSupported(Context context, Uri uri){
        String extension = ImageHelpers.getExtension(context, uri);

        System.out.println("FORMAT IS: " + extension);

        for(SupportedFormats.ImageFormats format : SupportedFormats.ImageFormats.values()){ //  Cycles through each format to check.
            if(extension.equals(format.toString())){
                return true;
            }
        }
        return false;
    }

    /** Return a list of the supported formats from the enum 'ImageFormats' in a way that can be used to print to screen **/
    public static String getPrintableSupportedFormats(){
        ArrayList<String> supportedFormats = new ArrayList<>();

        //  Cycle through each format and add to the arraylist.
        for(SupportedFormats.ImageFormats format : SupportedFormats.ImageFormats.values()){
            supportedFormats.add(format.toString());
        }

        String printableFormats = "";

        //  Concatenates each new format to the end of 'printableFormats'.
        for(String pf: supportedFormats){
            printableFormats = pf.concat(" " + printableFormats + " ");
        }
        return printableFormats;
    }

    public static ArrayList<String> getFilterIconsHoverMessage(filterType type){

        ArrayList<String> message = new ArrayList<>();
        
        switch(type){
            case ALLERGENS:
                message.add("Contains Eggs");
                message.add("Contains Lactose");
                message.add("Contains Nuts");
                message.add("Contains Shellfish");
                message.add("Contains Soy");
                message.add("Contains Gluten");
                break;
            case DIETARY:
                message.add("Suitable for Pescatarian's");
                message.add("Suitable for Vegans");
                message.add("Suitable for Vegetarians");
                break;
        }
        return message;
    }

}
