package es.tfg.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import es.tfg.R;
import es.tfg.model.Cell;

public class TableViewListener implements ITableViewListener {
    @NonNull
    private final Context mContext;
    @NonNull
    private final String token;
    @NonNull
    private List<List<Cell>> list;

    public TableViewListener(@NonNull TableView tableView, List<List<Cell>> list, String token) {
        this.mContext = tableView.getContext();
        this.list = list;
        this.token = token;
    }

    private AlertDialog deleteConfirmation(final String nameGame) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                .setTitle("Delete Score")
                .setMessage("Are you sure you want delete this score?")
                .setIcon(R.drawable.ic_baseline_delete)
                .setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        new DeleteRate().execute(new RateInfo(token, nameGame));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        if (column == 0) {
            showToast(String.valueOf(list.get(row).get(0).getData()));
        }

    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, final int column, int row) {

    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        AlertDialog diaBox = deleteConfirmation(String.valueOf(list.get(row).get(0).getData()));
        diaBox.show();
    }

    private void showToast(String p_strMessage) {
        Toast.makeText(mContext, p_strMessage, Toast.LENGTH_SHORT).show();
    }


    private static class RateInfo {
        String nameGame;
        String token;

        public RateInfo(String token, String nameGame) {
            this.nameGame = nameGame;
            this.token = token;
        }
    }

    class DeleteRate extends AsyncTask<RateInfo, Void, String> {

        @Override
        protected String doInBackground(RateInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(mContext.getResources().getString(R.string.ip_rate) + "/" + strings[0].nameGame);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    text = "Rate Deleted!";
                } else {
                    text = "Rate not Deleted!";
                }

            } catch (Exception e) {
                return e.toString();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = ((Activity) mContext).getIntent();
            ((Activity) mContext).overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, 0);
            mContext.startActivity(intent);
            showToast(s);
        }
    }

}