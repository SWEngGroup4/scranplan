package com.group4sweng.scranplan.Exceptions;

/** Thrown when there is an issue uploading a picture from the client to the Firebase server. Can occur due to any of the following...
 *  - Input image is too large in size.
 *  - Image is of the wrong file type. IE not in SupportedFormats.
 *  - Failed to upload to Firebase for other reasons.
 */
public class ImageException extends Exception {
    public ImageException(String error){ super(error); }
}
