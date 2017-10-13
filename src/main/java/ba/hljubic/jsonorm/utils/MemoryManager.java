package ba.hljubic.jsonorm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ba.hljubic.jsonorm.JsonOrm;

public class MemoryManager {

    private SharedPreferences sharedPreferences;

    public MemoryManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES_NAME, 0);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }


    public static String read(InputStream is) {
        String json = Constants.EMPTY_STRING;

        try {
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public static String readAsset(String filename) {
        try {
            return read(JsonOrm.get().getContext().getAssets().open(filename));
        } catch (IOException e) {
            Log.e(Constants.TAG, "Can't read assets file: " + filename);

            e.printStackTrace();
        }

        return Constants.EMPTY_ARRAY;
    }

    public static String readFile(String filename) {
        try {
            return read(new FileInputStream(getFile(filename)));
        } catch (IOException e) {
            Log.e(Constants.TAG, "Can't read file: " + filename);

            e.printStackTrace();
        }

        return Constants.EMPTY_ARRAY;
    }

    public static File getDirectory() {
        File directory = new File(JsonOrm.get().getDirectory());

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(Constants.TAG, "Can't create files directory. Returning empty array.");
            }
        }

        return directory;
    }

    public static File getFile(String filename) throws IOException {
        File file = new File(getDirectory().getPath()
                + File.separator + filename);

        if (file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public static void writeFile(String filename, String data) {
        try {
            FileOutputStream writer = new FileOutputStream(getFile(filename).getPath());
            writer.write(data.getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
