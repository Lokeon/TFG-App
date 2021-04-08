package es.tfg.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.sort.ISortableModel;

public class Cell implements ISortableModel {
    @Nullable
    private Object mData;
    @NonNull
    private String mId;

    public Cell(@NonNull String id, @Nullable Object data) {
        this.mId = id;
        this.mData = data;
    }

    @Nullable
    public Object getData() {
        return mData;
    }

    @NonNull
    @Override
    public String getId() {
        return mId;
    }

    @Nullable
    @Override
    public Object getContent() {
        return mData;
    }
}