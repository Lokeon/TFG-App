package es.tfg.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TableViewModel {

    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();

        list.add(new ColumnHeader("Name"));
        list.add(new ColumnHeader("Score"));
        list.add(new ColumnHeader("Score Date"));

        return list;
    }

    @NonNull
    public List<RowHeader> getRowHeaderList(int size) {
        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new RowHeader(String.valueOf(i + 1)));
        }
        return list;
    }
}
