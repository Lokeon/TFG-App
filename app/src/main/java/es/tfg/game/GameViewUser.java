package es.tfg.game;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import es.tfg.R;
import es.tfg.list.ScoreList;
import es.tfg.user.Profile;
import es.tfg.user.UserActivity;


public class GameViewUser extends AppCompatActivity {
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id_user;
    private String id_game;
    private String name_game;
    private TextView nameGame;
    private TextView descriptionGame;
    private ImageView imageGame;
    private TextView averageGame;
    private TextView ratedGame;
    private TextView genre;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view_user);

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar_user);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle("");
        top_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id_user = bundle.getString("id").replace("\"", "");
        id_game = bundle.getString("id_game").replace("\"", "");
        name_game = bundle.getString("name_game").replace("\"", "");
        nameGame = (TextView) findViewById(R.id.name);
        descriptionGame = (TextView) findViewById(R.id.description);
        imageGame = (ImageView) findViewById(R.id.image);
        genre = (TextView) findViewById(R.id.genre);
        averageGame = (TextView) findViewById(R.id.avg);
        ratedGame = (TextView) findViewById(R.id.rated);

        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id_user);


        top_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameViewUser.this, GameUser.class).putExtras(bundleSend));
            }
        });

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        ratingBar = (RatingBar) findViewById(R.id.rating);
        new GetGame().execute(new GameInfo(token, id_game));
        new GetRate().execute(new RateInfo(token, id_user, id_game));

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    new PostRate().execute(new RateInfo(token, id_user, id_game, name_game, rating));
                }
            }
        });
    }

    public void goHome(View view) {
        startActivity(new Intent(GameViewUser.this, UserActivity.class).putExtras(bundleSend));
    }

    public void goProfile(View view) {
        startActivity(new Intent(GameViewUser.this, Profile.class).putExtras(bundleSend));
    }

    public void goGame(View view) {
        startActivity(new Intent(GameViewUser.this, GameUser.class).putExtras(bundleSend));
    }

    public void goList(View view) {
        startActivity(new Intent(GameViewUser.this, ScoreList.class).putExtras(bundleSend));
    }

    private static class GameInfo {
        String id;
        String token;

        GameInfo(String token, String id) {
            this.id = id;
            this.token = token;
        }
    }

    private static class RateInfo {
        String id_user;
        String id_game;
        String name_game;
        String token;
        Float score;

        RateInfo(String token, String id_user, String id_game, String name_game, Float score) {
            this.id_user = id_user;
            this.id_game = id_game;
            this.name_game = name_game;
            this.token = token;
            this.score = score;
        }

        RateInfo(String token, String id_user, String id_game) {
            this.id_user = id_user;
            this.id_game = id_game;
            this.token = token;
        }
    }

    class GetGame extends AsyncTask<GameInfo, Void, String> {

        @Override
        protected String doInBackground(GameInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_uogames) + "/" + strings[0].id);

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

                    nameGame.setText(jsonobject.getString("name"));
                    descriptionGame.setText(jsonobject.getString("description"));
                    genre.setText(jsonobject.getString("genre"));
                    byte[] decodedString = Base64.decode(jsonobject.getString("image"), Base64.DEFAULT);
                    imageGame.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    averageGame.setText(jsonobject.getString("avg"));
                    ratedGame.setText(jsonobject.getString("rates"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GetRate extends AsyncTask<RateInfo, Void, String> {

        @Override
        protected String doInBackground(RateInfo... strings) {
            String text;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(getResources().getString(R.string.ip_rated) + "/" + strings[0].id_user + "/" + strings[0].id_game);

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
                    ratingBar.setRating(Float.parseFloat(jsonobject.getString("score")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ratingBar.setRating(0);
            }
        }
    }

    class PostRate extends AsyncTask<RateInfo, Void, String> {
        @Override
        protected String doInBackground(RateInfo... strings) {
            String text = null;
            BufferedWriter bufferedWriter;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("score", strings[0].score);
                dataToSend.put("idUser", strings[0].id_user);
                dataToSend.put("idGame", strings[0].id_game);
                dataToSend.put("nameGame", strings[0].name_game);

                URL url = new URL(getResources().getString(R.string.ip_rate));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(30000);
                urlConnection.setConnectTimeout(30000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                text = String.valueOf(urlConnection.getResponseCode());
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

            if (Integer.parseInt(results) == 201) {
                Toast.makeText(GameViewUser.this, "Score Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            } else {
                Toast.makeText(GameViewUser.this, "Score Submited!", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        }
    }

}


