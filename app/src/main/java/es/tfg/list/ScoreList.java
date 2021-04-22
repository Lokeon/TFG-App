package es.tfg.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.evrencoskun.tableview.TableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.adapter.TableViewAdapter;
import es.tfg.game.GameUser;
import es.tfg.game.Petition;
import es.tfg.listener.TableViewListener;
import es.tfg.model.Cell;
import es.tfg.model.TableViewModel;
import es.tfg.recommendation.Recommendation;
import es.tfg.registration.SignIn;
import es.tfg.user.Profile;
import es.tfg.user.UserActivity;


public class ScoreList extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView userText;
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id;
    private CircleImageView circleImageView;
    private TableView tableView;
    private List<List<Cell>> list;


    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }

    }

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to Logout ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(activity, SignIn.class));
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        TextView textView = (TextView) findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.scoreList);
        userText = (TextView) findViewById(R.id.top_toolbar_username);
        circleImageView = (CircleImageView) findViewById(R.id.avatar_img_user);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_user);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id = bundle.getString("id").replace("\"", "");
        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id);
        list = new ArrayList<>();

        tableView = (TableView) findViewById(R.id.tableviews);

        new GetUsername().execute(new UserInfo(token, id));
        new GetRates().execute(new UserInfo(token, id));
    }

    public void goHome(View view) {
        startActivity(new Intent(ScoreList.this, UserActivity.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goProfile(View view) {
        startActivity(new Intent(ScoreList.this, Profile.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goGame(View view) {
        startActivity(new Intent(ScoreList.this, GameUser.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goList(View view) {
        startActivity(new Intent(ScoreList.this, ScoreList.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goPetition(View view) {
        startActivity(new Intent(ScoreList.this, Petition.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goRecommendation(View view) {
        startActivity(new Intent(ScoreList.this, Recommendation.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public String formatDate(String date) {
        String dateFormatted = null;

        try {
            String[] dateN = date.split("T");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateD = dateFormat.parse(dateN[0]);

            dateFormat.applyPattern("MM-dd-yyyy");
            dateFormatted = dateFormat.format(dateD);

        } catch (Exception e) {
        }

        return dateFormatted;
    }

    public void openMenu(View view) {
        openDrawer(drawerLayout);
    }

    public void closeMenu(View view) {
        closeDrawer(drawerLayout);
    }

    public void goLogout(View view) {
        logout(this);
    }

    private static class UserInfo {
        String id;
        String token;

        UserInfo(String token, String id) {
            this.id = id;
            this.token = token;
        }
    }

    class GetUsername extends AsyncTask<UserInfo, Void, String> {

        @Override
        protected String doInBackground(UserInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_username));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                text = new Scanner(inputStream).useDelimiter("\\A").next();

            } catch (Exception e) {
                return e.toString();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            if (results != null) {
                try {
                    JSONObject jsonobject = new JSONObject(results);

                    userText.setText(jsonobject.getString("username"));
                    byte[] decodedString = Base64.decode(jsonobject.getString("avatar"), Base64.DEFAULT);
                    circleImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GetRates extends AsyncTask<UserInfo, Integer, String> {
        private ProgressBar progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = (ProgressBar) findViewById(R.id.progress_bar);
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(UserInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_rated));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                text = new Scanner(inputStream).useDelimiter("\\A").next();

            } catch (Exception e) {
                return e.toString();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            progress.setVisibility(View.INVISIBLE);

            if (results != null) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        List<Cell> listC = new ArrayList<>();

                        listC.add(new Cell(String.valueOf(i), jsonobject.getString("nameGame")));
                        listC.add(new Cell(String.valueOf(i), jsonobject.getString("score")));
                        listC.add(new Cell(String.valueOf(i), formatDate(jsonobject.getString("date"))));

                        list.add(listC);
                    }

                    TableViewModel tableViewModel = new TableViewModel();

                    TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);
                    tableView.setAdapter(tableViewAdapter);
                    tableView.setTableViewListener(new TableViewListener(tableView, list, token));

                    tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel.getRowHeaderList(list.size()), list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


