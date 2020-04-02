package com.group4sweng.scranplan;

/** Supported extension formats for different media types to be uploaded and stored within Firebase **/
public interface SupportedFormats {
    enum AudioFormats{
        mp3,
        wav
    }

    enum ImageFormats {
        jpeg,
        jpg,
        tif,
        bmp,
        gif,
        png;
    }
}
