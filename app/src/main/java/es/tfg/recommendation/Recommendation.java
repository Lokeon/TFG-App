package es.tfg.recommendation;

import android.app.Activity;
import android.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.game.GameUser;
import es.tfg.game.GameViewUser;
import es.tfg.game.Petition;
import es.tfg.list.ScoreList;
import es.tfg.registration.SignIn;
import es.tfg.user.Profile;
import es.tfg.user.UserActivity;


public class Recommendation extends AppCompatActivity {

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
        setContentView(R.layout.activity_recommendation);


        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        TextView textView = (TextView) findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.recommendation);
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
        new GetRecommendation().execute(new UserInfo(token, id));
    }

    public void goHome(View view) {
        startActivity(new Intent(Recommendation.this, UserActivity.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goProfile(View view) {
        startActivity(new Intent(Recommendation.this, Profile.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goGame(View view) {
        startActivity(new Intent(Recommendation.this, GameUser.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goList(View view) {
        startActivity(new Intent(Recommendation.this, ScoreList.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goRecommendation(View view) {
        startActivity(new Intent(Recommendation.this, Recommendation.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goPetition(View view) {
        startActivity(new Intent(Recommendation.this, Petition.class).putExtras(bundleSend).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    protected String getErrorFromServer(InputStream error) throws IOException {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(error))) {
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
        }

        return builder.toString();
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

    class GetRecommendation extends AsyncTask<UserInfo, Void, String> {
        private View game1;
        private View game2;
        private View game3;
        private ImageView image1;
        private TextView name1;
        private ImageView image2;
        private TextView name2;
        private ImageView image3;
        private TextView name3;
        private ProgressBar progressBar;
        private String id1;
        private String gname1;
        private String id2;
        private String gname2;
        private String id3;
        private String gname3;
        private TextView TxtReco;
        private HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            game1 = (View) findViewById(R.id.game1);
            image1 = (ImageView) game1.findViewById(R.id.imageGame);
            name1 = (TextView) game1.findViewById(R.id.nameGame);
            game1.setVisibility(View.INVISIBLE);

            game2 = (View) findViewById(R.id.game2);
            image2 = (ImageView) game2.findViewById(R.id.imageGame);
            name2 = (TextView) game2.findViewById(R.id.nameGame);
            game2.setVisibility(View.INVISIBLE);

            game3 = (View) findViewById(R.id.game3);
            image3 = (ImageView) game3.findViewById(R.id.imageGame);
            name3 = (TextView) game3.findViewById(R.id.nameGame);
            game3.setVisibility(View.INVISIBLE);

            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            TxtReco = (TextView) findViewById(R.id.TxtReco);

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(UserInfo... strings) {
            String text;

            try {
                URL url = new URL(getResources().getString(R.string.ip_recommendation));

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
                    if (urlConnection.getResponseCode() == 402) {
                        progressBar.setVisibility(View.GONE);
                        TxtReco.setVisibility(View.VISIBLE);
                        TxtReco.setText(R.string.recomen);
                    } else {
                        JSONObject jsonobject = new JSONObject(results);

                        progressBar.setVisibility(View.GONE);
                        TxtReco.setVisibility(View.GONE);

                        game1.setVisibility(View.VISIBLE);
                        name1.setText(jsonobject.getString("nameGame1"));
                        gname1 = jsonobject.getString("nameGame1");
                        id1 = jsonobject.getString("idGame1");
                        byte[] decodedString = Base64.decode(jsonobject.getString("image1"), Base64.DEFAULT);
                        image1.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));

                        game2.setVisibility(View.VISIBLE);
                        name2.setText(jsonobject.getString("nameGame2"));
                        gname2 = jsonobject.getString("nameGame2");
                        id2 = jsonobject.getString("idGame2");
                        byte[] decodedString2 = Base64.decode(jsonobject.getString("image2"), Base64.DEFAULT);
                        image2.setImageBitmap(BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length));


                        game3.setVisibility(View.VISIBLE);
                        name3.setText(jsonobject.getString("nameGame3"));
                        gname3 = jsonobject.getString("nameGame3");
                        id3 = jsonobject.getString("idGame3");
                        byte[] decodedString3 = Base64.decode(jsonobject.getString("image3"), Base64.DEFAULT);
                        image3.setImageBitmap(BitmapFactory.decodeByteArray(decodedString3, 0, decodedString3.length));

                        image1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bundleSend.putString("name_game", gname1);
                                bundleSend.putString("id_game", id1);
                                startActivity(new Intent(Recommendation.this, GameViewUser.class).putExtras(bundleSend));
                            }
                        });

                        image2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bundleSend.putString("name_game", gname2);
                                bundleSend.putString("id_game", id2);
                                startActivity(new Intent(Recommendation.this, GameViewUser.class).putExtras(bundleSend));
                            }
                        });

                        image3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bundleSend.putString("name_game", gname3);
                                bundleSend.putString("id_game", id3);
                                startActivity(new Intent(Recommendation.this, GameViewUser.class).putExtras(bundleSend));
                            }
                        });
                    }


                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


