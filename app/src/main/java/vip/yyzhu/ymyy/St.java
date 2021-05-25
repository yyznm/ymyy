package vip.yyzhu.ymyy;

import android.os.Environment;

public class St {
    public static final String  voicePackage = "/Android/data/vip.yyzhu.ymyy/语音包";

    public static String getVoicePackage() {
        return voicePackage;
    }
    public static String getVoicePackageRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + voicePackage;
    }
    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


}
