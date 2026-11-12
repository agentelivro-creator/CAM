package com.cameracinema.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.OutputStream;

@CapacitorPlugin(name = "GallerySaver")
public class GallerySaverPlugin extends Plugin {
    private Uri uri;
    private OutputStream os;

    @PluginMethod
    public void begin(PluginCall call) {
        try {
            ContentValues v = new ContentValues();
            v.put(MediaStore.Video.Media.DISPLAY_NAME, call.getString("name", "video.mp4"));
            v.put(MediaStore.Video.Media.MIME_TYPE, call.getString("mime", "video/mp4"));
            v.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Camera Cinema");
            v.put(MediaStore.Video.Media.IS_PENDING, 1);
            ContentResolver r = getContext().getContentResolver();
            uri = r.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, v);
            os = r.openOutputStream(uri);
            call.resolve();
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }

    @PluginMethod
    public void write(PluginCall call) {
        try {
            byte[] b = Base64.decode(call.getString("data"), Base64.DEFAULT);
            os.write(b);
            call.resolve();
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }

    @PluginMethod
    public void finish(PluginCall call) {
        try {
            os.flush();
            os.close();
            ContentValues v = new ContentValues();
            v.put(MediaStore.Video.Media.IS_PENDING, 0);
            getContext().getContentResolver().update(uri, v, null, null);
            call.resolve();
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }
}package com.cameracinema.app;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(GallerySaverPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
