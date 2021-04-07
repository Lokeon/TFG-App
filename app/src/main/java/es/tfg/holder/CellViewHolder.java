package es.tfg.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import es.tfg.R;
import es.tfg.model.Cell;

public class CellViewHolder extends AbstractViewHolder {
    @NonNull
    private final TextView cell_texview;
    @NonNull
    private final LinearLayout cell_container;

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
        cell_container = itemView.findViewById(R.id.cell_container);
        cell_texview = itemView.findViewById(R.id.cell_data);
    }

    public void setCell(@Nullable Cell cell) {
        cell_texview.setText(String.valueOf(cell.getData()));

        cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_texview.requestLayout();
    }
}
