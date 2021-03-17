package es.tfg.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.registration.SignIn;

public class Profile extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private TextView userText;
    private TextView userNText;
    private TextView emailText;
    private TextView dateText;
    private TextView cPassword;
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id;
    private CircleImageView circleImageView;

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
        setContentView(R.layout.activity_profile);

        Toolbar bottom_toolbar = findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        TextView textView = findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.profile);
        userText = findViewById(R.id.top_toolbar_username);
        userNText = findViewById(R.id.TxtUser);
        emailText = findViewById(R.id.TxtEmail);
        dateText = findViewById(R.id.TxtDate);
        cPassword = findViewById(R.id.TxtCPassword);
        drawerLayout = findViewById(R.id.drawer_profile);
        circleImageView = findViewById(R.id.avatar_img_user);

        cPassword.setPaintFlags(cPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id = bundle.getString("id").replace("\"", "");

        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id);

        new GetUser().execute(new UserInfo(token, id));
    }

    public void goHome(View view) {
        startActivity(new Intent(Profile.this, UserActivity.class).putExtras(bundleSend));
    }

    public void goProfile(View view) {
        startActivity(new Intent(Profile.this, Profile.class).putExtras(bundleSend));
    }

    public void goChangePassword(View view) {
        startActivity(new Intent(Profile.this, ChangePassword.class).putExtras(bundleSend));
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

    private static class UserInfo {
        String id;
        String token;

        UserInfo(String token, String id) {
            this.id = id;
            this.token = token;
        }
    }

    class GetUser extends AsyncTask<UserInfo, Void, String> {

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
                    userNText.setText(jsonobject.getString("username"));
                    emailText.setText(jsonobject.getString("email"));
                    dateText.setText(formatDate(jsonobject.getString("date")));
                    byte[] decodedString = Base64.decode(jsonobject.getString("avatar"), Base64.DEFAULT);
                    circleImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}