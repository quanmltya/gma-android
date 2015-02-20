package com.expidev.gcmapp.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.expidev.gcmapp.model.Location;

public abstract class LocationMapper<T extends Location> extends BaseMapper<T> {
    @Override
    protected void mapField(@NonNull final ContentValues values, @NonNull final String field, @NonNull final T obj) {
        switch (field) {
            case Contract.Church.COLUMN_LATITUDE:
                values.put(field, obj.getLatitude());
                break;
            case Contract.Church.COLUMN_LONGITUDE:
                values.put(field, obj.getLongitude());
                break;
            default:
                super.mapField(values, field, obj);
                break;
        }
    }

    @NonNull
    @Override
    public T toObject(@NonNull final Cursor c) {
        final T obj = super.toObject(c);
        obj.setLatitude(getDouble(c, Contract.Location.COLUMN_LATITUDE));
        obj.setLongitude(getDouble(c, Contract.Location.COLUMN_LONGITUDE));
        return obj;
    }
}
