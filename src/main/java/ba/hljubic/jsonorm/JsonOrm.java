package ba.hljubic.jsonorm;

import android.content.Context;

import java.io.File;

import ba.hljubic.jsonorm.utils.Constants;
import ba.hljubic.jsonorm.utils.MemoryManager;

/**
 * Created by hrvoje on 12.10.2017..
 */

public class JsonOrm {

    private static JsonOrm sInstance;

    private Context context;
    private MemoryManager memoryManager;

    private String directory;

    public static void with(Context context) {
        if (sInstance == null) {
            sInstance = new JsonOrm();

            sInstance.context = context;
            sInstance.directory = context.getFilesDir().getPath()
                    + File.separator
                    + Constants.FILES_DIRECTORY;

            sInstance.memoryManager = new MemoryManager(context);
        }
    }

    public static JsonOrm get() {
        return sInstance;
    }

    public Context getContext() {
        return context;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public String getDirectory() {
        return directory;
    }
}
