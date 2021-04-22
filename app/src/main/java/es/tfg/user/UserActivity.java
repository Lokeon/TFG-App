package es.tfg.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.game.GameUser;
import es.tfg.game.GameViewUser;
import es.tfg.game.Petition;
import es.tfg.list.ScoreList;
import es.tfg.recommendation.Recommendation;
import es.tfg.registration.SignIn;


public class UserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView userText;
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
        setContentView(R.layout.activity_user);

        NotificationChannel channel = new NotificationChannel("notif_channel", "notif_channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        FirebaseMessaging.getInstance().subscribeToTopic("game");

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        TextView textView = (TextView) findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.home);
        userText = (TextView) findViewById(R.id.top_toolbar_username);
        circleImageView = (CircleImageView) findViewById(R.id.avatar_img_user);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_user);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id = bundle.getString("id").replace("\"", "");
        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id);


        new GetUsername().execute(new UserInfo(token, id));
        new GetBestGames().execute(new UserInfo(token, id));
    }

    public void goHome(View view) {
        startActivity(new Intent(UserActivity.this, UserActivity.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goProfile(View view) {
        startActivity(new Intent(UserActivity.this, Profile.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goGame(View view) {
        startActivity(new Intent(UserActivity.this, GameUser.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goList(View view) {
        startActivity(new Intent(UserActivity.this, ScoreList.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goRecommendation(View view) {
        startActivity(new Intent(UserActivity.this, Recommendation.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goPetition(View view) {
        startActivity(new Intent(UserActivity.this, Petition.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
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

    class GetBestGames extends AsyncTask<UserInfo, Void, String> {
        private View mostVoted;
        private View mostAvg;
        private ImageView mostVotedImg;
        private TextView mostVotedTitle;
        private TextView mostVotedName;
        private ImageView mostAvgImg;
        private TextView mostAvgTitle;
        private TextView mostAvgName;
        private ProgressBar progressBar;
        private String voted;
        private String voted_name;
        private String avg;
        private String avg_name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mostVoted = (View) findViewById(R.id.mostVoted);
            mostVotedImg = (ImageView) mostVoted.findViewById(R.id.imageGame);
            mostVotedName = (TextView) mostVoted.findViewById(R.id.nameGame);
            mostVotedTitle = (TextView) mostVoted.findViewById(R.id.Txttitle);
            mostVoted.setVisibility(View.INVISIBLE);

            mostAvg = (View) findViewById(R.id.mostAvg);
            mostAvgImg = (ImageView) mostAvg.findViewById(R.id.imageGame);
            mostAvgName = (TextView) mostAvg.findViewById(R.id.nameGame);
            mostAvgTitle = (TextView) mostAvg.findViewById(R.id.Txttitle);
            mostAvg.setVisibility(View.INVISIBLE);

            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(UserInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_bestGames));

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

                    progressBar.setVisibility(View.GONE);

                    mostVoted.setVisibility(View.VISIBLE);
                    mostVotedName.setText(jsonobject.getString("mostVoted"));
                    voted_name = jsonobject.getString("mostVoted");
                    voted = jsonobject.getString("idMostVoted");
                    byte[] decodedString = Base64.decode(jsonobject.getString("mostVotedImg"), Base64.DEFAULT);
                    mostVotedImg.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    mostVotedTitle.setText(R.string.most_game_voted);

                    mostAvg.setVisibility(View.VISIBLE);
                    mostAvgName.setText(jsonobject.getString("mostAvg"));
                    avg_name = jsonobject.getString("mostAvg");
                    avg = jsonobject.getString("idMostAvg");
                    byte[] decodedString2 = Base64.decode(jsonobject.getString("mostAvgImg"), Base64.DEFAULT);
                    mostAvgImg.setImageBitmap(BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length));
                    mostAvgTitle.setText(R.string.most_game_avg);

                    mostVoted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bundleSend.putString("name_game", voted_name);
                            bundleSend.putString("id_game", voted);
                            startActivity(new Intent(UserActivity.this, GameViewUser.class).putExtras(bundleSend));
                        }
                    });

                    mostAvg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bundleSend.putString("name_game", avg_name);
                            bundleSend.putString("id_game", avg);
                            startActivity(new Intent(UserActivity.this, GameViewUser.class).putExtras(bundleSend));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


