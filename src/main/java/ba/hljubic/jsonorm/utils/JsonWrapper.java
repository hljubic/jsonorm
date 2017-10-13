package ba.hljubic.jsonorm.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Helper class for deserializing json array into object list
 */
public class JsonWrapper<T> implements ParameterizedType {
    private Class<?> wrapped;

    public JsonWrapper(Class<T> wrapper) {
        this.wrapped = wrapper;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrapped};
    }

    @Override
    public Type getRawType() {
        return List.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}