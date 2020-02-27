package com.group4sweng.scranplan.Firebase;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class FirebaseDownload {
    public FirebaseDownload(Context context, String fileNameAndExtension, String destinationDirectory, String URL){
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri URI = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(URI);

        request.setNotificationVisibility((DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED));
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileNameAndExtension);

        downloadManager.enqueue(request);
    }

}
