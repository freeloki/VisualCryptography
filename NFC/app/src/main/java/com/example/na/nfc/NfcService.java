package com.example.na.nfc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import java.io.File;

/**
 * Created by Yavuz on 24.06.2016.
 */
public class NfcService {

    private NfcAdapter mNfcAdapter;
    boolean mAndroidBeamAvailable = false;
    private Activity masterActivity;

    public NfcService(Activity masterActivity) {

        //appContext = mContext;
        this.masterActivity = masterActivity;

    }


    public boolean isNFCSupported() {

        if (!masterActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            /*
             * Disable NFC features here.
             * exit from app.
             * NFC-related features
             */
            return false;

            // Android Beam file transfer isn't supported
        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // If Android Beam isn't available, don't continue.
            mAndroidBeamAvailable = false;
            return false;
            // Android Beam file transfer is available, continue
        } else {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(masterActivity);
            return true;
        }
    }

    public void sendFile(View view, int share) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(masterActivity);

        // Check whether NFC is enabled on device
        if (!mNfcAdapter.isEnabled()) {
            // NFC is disabled, show the settings UI
            // to enable NFC
            masterActivity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if (!mNfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            masterActivity.startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        } else {
            // NFC and Android Beam both are enabled

            // File to be transferred
            // For the sake of this tutorial I've placed an image
            // named 'wallpaper.png' in the 'Pictures' directory
            String fileName = null;
            if (share == 1) {
                fileName = "share1.png";
            } else if (share == 2) {
                fileName = "share2.png";
            }


            // Retrieve the path to the user's public pictures directory
            File fileDirectory = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);

            // Create a new file using the specified directory and name
            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            mNfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, masterActivity);
        }
    }
}
