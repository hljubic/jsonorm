package ba.hljubic.jsonorm;

import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ba.hljubic.jsonorm.annotations.InAssets;
import ba.hljubic.jsonorm.annotations.InFile;
import ba.hljubic.jsonorm.utils.Constants;
import ba.hljubic.jsonorm.utils.JsonWrapper;
import ba.hljubic.jsonorm.utils.MemoryManager;

/**
 * Created by hrvoje on 12.10.2017..
 */

public abstract class JsonTable<T> {

    protected String id;

    public String getId() {
        return id;
    }

    @CallSuper
    public void save() {
        List<T> list = (List<T>) JsonTable.listAll(this.getClass());

        if (this.id == null || this.id.isEmpty())
            this.id = String.valueOf(System.currentTimeMillis());

        list.add((T) this);

        JsonTable.saveAll(this.getClass(), list);
    }

    @CallSuper
    public void update() {
        List<T> list = (List<T>) JsonTable.listAll(this.getClass());

        int position = 0;

        for (T obj : list) {
            if (getId().equals(((JsonTable) obj).getId())) {
                list.remove(position);

                list.add(position, (T) this);

                break;
            }

            position++;
        }

        JsonTable.saveAll(this.getClass(), list);
    }

    @CallSuper
    public void delete() {
        if (getId() == null) {
            Log.e(Constants.TAG, "Your object must define getId() method properly.");
            return;
        }

        List<T> list = (List<T>) JsonTable.listAll(this.getClass());

        int position = 0;

        for (T obj : list) {
            if (this.getId().equals(((JsonTable) obj).getId())) {
                list.remove(position);
                break;
            }
            position++;
        }

        JsonTable.saveAll(this.getClass(), list);
    }

    @CallSuper
    public static <T> T findById(Class<T> type, String id) {
        List<T> list = JsonTable.listAll(type);

        for (T obj : list) {
            if (id.equals(((JsonTable) obj).getId())) {
                return obj;
            }
        }

        return null;
    }

    @CallSuper
    public static <T> List<T> listAll(Class<T> type) {
        String data;

        if (type.isAnnotationPresent(InAssets.class)) {
            InAssets inAssets = type.getAnnotation(InAssets.class);

            data = MemoryManager.readAsset(inAssets.value());
        } else if (type.isAnnotationPresent(InFile.class)) {
            InFile inFile = type.getAnnotation(InFile.class);

            data = MemoryManager.readFile(inFile.value());
        } else {
            data = getPreferences().getString(type.getSimpleName(), Constants.EMPTY_STRING);
        }

        List<T> list = new Gson().fromJson(data, new JsonWrapper<>(type));

        Log.d(Constants.TAG, "Method " + type.getSimpleName() + ".listAll(). Data size: " +
                "(" + (list == null ? " not exists " : list.size()) + ")");

        return list != null ? list : new ArrayList<T>();
    }

    public static <T> void saveAll(Class<?> type, List<T> list) {

        if (type.isAnnotationPresent(InFile.class)) {
            InFile inFile = type.getAnnotation(InFile.class);

            MemoryManager.writeFile(inFile.value(), new Gson().toJson(list));
        } else {
            getPreferences().edit().putString(type.getSimpleName(), new Gson().toJson(list)).apply();
        }
    }

    // If using this method outside of this class, make sure your objects have set manually their ID

    @CallSuper
    public static <T> void appendAll(Class<?> type, List<T> list) {
        List<T> data = (List<T>) listAll(type);

        data.addAll(list);

        saveAll(type, data);
    }

    @CallSuper
    public static <T> void deleteAll(Class<T> type) {
        JsonTable.saveAll(type, new ArrayList<>());
    }


    // Async methods

    @CallSuper
    public void saveAsync(final OnResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                save();

                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }


    @CallSuper
    public void updateAsync(final OnResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                update();

                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    @CallSuper
    public void deleteAsync(final OnResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                delete();

                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    @CallSuper
    public static <T> void findByIdAsync(final Class<T> type, final String id, final OnObjectResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {

                if (listener != null) {
                    listener.onComplete(findById(type, id));
                }
            }
        });
    }

    @CallSuper
    public static <T> void listAllAsync(final Class<T> type, final OnListResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                List<T> list = listAll(type);

                if (listener != null) {
                    listener.onComplete(list);
                }
            }
        });
    }

    @CallSuper
    public static <T> void saveAllAsync(final Class<?> type, final List<T> list, final OnResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                saveAll(type, list);

                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    @CallSuper
    public static <T> void appendAllAsync(final Class<?> type, final List<T> list, final OnResponseListener<T> listener) {
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                appendAll(type, list);

                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    @CallSuper
    public void saveAsync() {
        saveAsync(null);
    }

    @CallSuper
    public void updateAsync() {
        updateAsync(null);
    }

    @CallSuper
    public void deleteAsync() {
        deleteAsync(null);
    }

    @CallSuper
    public static <T> void appendAllAsync(final Class<?> type, final List<T> list) {
        appendAllAsync(type, list, null);
    }

    @CallSuper
    public static <T> void saveAllAsync(final Class<?> type, final List<T> list) {
        saveAllAsync(type, list, null);
    }

    public interface OnResponseListener<T> {
        void onComplete();
    }

    public interface OnObjectResponseListener<T> {
        void onComplete(T object);
    }

    public interface OnListResponseListener<T> {
        void onComplete(List<T> list);
    }

    private static SharedPreferences.Editor getEditor() {
        return JsonOrm.get().getMemoryManager().getEditor();
    }

    private static SharedPreferences getPreferences() {
        return JsonOrm.get().getMemoryManager().getSharedPreferences();
    }
}
