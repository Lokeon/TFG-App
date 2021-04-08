package es.tfg.listener;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.sort.SortState;

import es.tfg.R;
import es.tfg.holder.ColumnHeaderViewHolder;


public class ColumnHeaderLongPressPopup extends PopupMenu implements PopupMenu
        .OnMenuItemClickListener {
    // Menu Item constants
    private static final int ASCENDING = 1;
    private static final int DESCENDING = 2;

    @NonNull
    private final TableView mTableView;
    private final int mXPosition;

    public ColumnHeaderLongPressPopup(@NonNull ColumnHeaderViewHolder viewHolder, @NonNull TableView tableView) {
        super(viewHolder.itemView.getContext(), viewHolder.itemView);
        this.mTableView = tableView;
        this.mXPosition = viewHolder.getAdapterPosition();

        initialize();
    }

    private void initialize() {
        createMenuItem();
        changeMenuItemVisibility();

        this.setOnMenuItemClickListener(this);
    }

    private void createMenuItem() {
        Context context = mTableView.getContext();
        this.getMenu().add(Menu.NONE, ASCENDING, 0, context.getString(R.string.sort_ascending));
        this.getMenu().add(Menu.NONE, DESCENDING, 1, context.getString(R.string.sort_descending));

    }

    private void changeMenuItemVisibility() {
        // Determine which one shouldn't be visible
        SortState sortState = mTableView.getSortingStatus(mXPosition);
        if (sortState == SortState.UNSORTED) {
            // Show others
        } else if (sortState == SortState.DESCENDING) {
            // Hide DESCENDING menu item
            getMenu().getItem(1).setVisible(false);
        } else if (sortState == SortState.ASCENDING) {
            // Hide ASCENDING menu item
            getMenu().getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case ASCENDING:
                mTableView.sortColumn(mXPosition, SortState.ASCENDING);

                break;
            case DESCENDING:
                mTableView.sortColumn(mXPosition, SortState.DESCENDING);
                break;
        }
        return true;
    }

}