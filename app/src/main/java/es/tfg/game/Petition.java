package es.tfg.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.list.ScoreList;
import es.tfg.registration.SignIn;
import es.tfg.user.Profile;
import es.tfg.user.UserActivity;


public class Petition extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView userText;
    private TextView gameText;
    private Button btn_petition;
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id;
    private CircleImageView circleImageView;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String game = gameText.getText().toString().trim();

            btn_petition.setEnabled(!game.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


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
        setContentView(R.layout.activity_petition);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        TextView textView = (TextView) findViewById(R.id.title_toolbar_user);
        textView.setText(R.string.game_petition);
        userText = (TextView) findViewById(R.id.top_toolbar_username);
        gameText = (TextView) findViewById(R.id.TxtPetition);
        btn_petition = (Button) findViewById(R.id.BtnPetition);
        circleImageView = (CircleImageView) findViewById(R.id.avatar_img_user);

        gameText.addTextChangedListener(textWatcher);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_user);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id = bundle.getString("id").replace("\"", "");
        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id);

        new GetUsername().execute(new UserInfo(token, id));

        btn_petition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostPetition().execute(new UserInfo(token, id));
            }
        });
    }

    public void goHome(View view) {
        startActivity(new Intent(Petition.this, UserActivity.class).putExtras(bundleSend));
    }

    public void goProfile(View view) {
        startActivity(new Intent(Petition.this, Profile.class).putExtras(bundleSend));
    }

    public void goGame(View view) {
        startActivity(new Intent(Petition.this, GameUser.class).putExtras(bundleSend));
    }

    public void goList(View view) {
        startActivity(new Intent(Petition.this, ScoreList.class).putExtras(bundleSend));
    }

    public void goPetition(View view) {
        startActivity(new Intent(Petition.this, Petition.class).putExtras(bundleSend));
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

    class PostPetition extends AsyncTask<UserInfo, Void, String> {

        private String game;
        private HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            game = gameText.getText().toString();
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

        @Override
        protected String doInBackground(UserInfo... strings) {
            String text;
            BufferedWriter bufferedWriter;
            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("nameGame", game);

                URL url = new URL(getResources().getString(R.string.ip_petition));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                if (urlConnection.getResponseCode() == 200) {
                    text = urlConnection.getResponseCode() + ":" + getResources().getString(R.string.petitionSend);

                } else {
                    text = urlConnection.getResponseCode() + ":" + getErrorFromServer(urlConnection.getErrorStream());
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
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            String[] error = results.split(":");

            if (Integer.parseInt(error[0]) == 200) {
                Toast.makeText(Petition.this, error[1], Toast.LENGTH_SHORT).show();
                gameText.setText("");
                startActivity(new Intent(Petition.this, Petition.class).putExtras(bundleSend));
            } else {
                Toast.makeText(Petition.this, error[1], Toast.LENGTH_LONG).show();
            }

        }
    }
}


