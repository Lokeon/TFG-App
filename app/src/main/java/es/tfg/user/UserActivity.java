package es.tfg.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import es.tfg.R;
import es.tfg.registration.SignIn;


public class UserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView userText;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        bundle = getIntent().getExtras();

        TextView textView = (TextView) findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.home);
        userText = (TextView) findViewById(R.id.top_toolbar_username);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_user);

    }

    public void goHome(View view) {
        startActivity(new Intent(UserActivity.this, UserActivity.class));
    }

    public void openMenu(View view) {
        openDrawer(drawerLayout);
        new GetUsername().execute(bundle.getString("id").replace("\"", ""));
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeMenu(View view) {
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }

    }

    public void clickHome(View view) {
        recreate();
    }

    public void goLogout(View view) {
        logout(this);
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

    class GetUsername extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_username) + "/" + strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
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

            System.out.println("AAAAAAAAAAAAAAAAA" + results);

            if (results != null) {
                JSONArray jsonArray;
                try {
                    jsonArray = new JSONArray(results);
                    JSONObject jsonobject = jsonArray.getJSONObject(0);

                    userText.setText(jsonobject.getString("username"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


