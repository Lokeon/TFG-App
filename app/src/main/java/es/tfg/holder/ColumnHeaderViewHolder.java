package es.tfg.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;

import es.tfg.R;
import es.tfg.model.ColumnHeader;


public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private static final String LOG_TAG = ColumnHeaderViewHolder.class.getSimpleName();

    @NonNull
    private final LinearLayout column_header_container;
    @NonNull
    private final TextView column_header_textview;
    @Nullable
    private final ITableView tableView;
    @NonNull
    private final View.OnClickListener mSortButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getSortState() == SortState.ASCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            } else if (getSortState() == SortState.DESCENDING) {
                tableView.sortColumn(getAdapterPosition(), SortState.ASCENDING);
            } else {
                // Default one
                tableView.sortColumn(getAdapterPosition(), SortState.DESCENDING);
            }

        }
    };

    public ColumnHeaderViewHolder(@NonNull View itemView, @Nullable ITableView tableView) {
        super(itemView);
        this.tableView = tableView;
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        column_header_container = itemView.findViewById(R.id.column_header_container);

    }


    public void setColumnHeader(@Nullable ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(columnHeader.getData()));

        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        column_header_textview.requestLayout();
    }
}
