package es.tfg.registration;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import es.tfg.R;
import es.tfg.game.Game;
import es.tfg.user.UserActivity;

public class SignIn extends AppCompatActivity {

    private EditText txtUser;
    private EditText txtpassword;
    private TextView textSignup;
    private Button btn_accept;
    // Check that always fields are filled before send
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String user = txtUser.getText().toString().trim();
            String pass = txtpassword.getText().toString().trim();

            btn_accept.setEnabled(!user.isEmpty() && !pass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.sign_in);


        txtUser = (EditText) findViewById(R.id.TxtUser);
        txtpassword = (EditText) findViewById(R.id.TxtPassword);
        textSignup = (TextView) findViewById(R.id.TxtSignUp);
        btn_accept = (Button) findViewById(R.id.BtnSignIn);

        txtUser.addTextChangedListener(textWatcher);
        txtpassword.addTextChangedListener(textWatcher);
        textSignup.setPaintFlags(textSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostUsers().execute();
            }
        });

    }

    public void goSignUp(View view) {
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

    public void goSignIn(View view) {
        startActivity(new Intent(SignIn.this, SignIn.class));
    }

    public void goGame(View view) {
        startActivity(new Intent(SignIn.this, Game.class));
    }


    class PostUsers extends AsyncTask<Void, Void, String> {

        private String user;
        private String email;
        private String pass;
        private HttpURLConnection urlConnection = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user = txtUser.getText().toString();
            pass = txtpassword.getText().toString();
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
        protected String doInBackground(Void... strings) {
            String text;
            BufferedWriter bufferedWriter;
            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("username", user);
                dataToSend.put("password", pass);

                URL url = new URL(getResources().getString(R.string.ip_login));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                if (urlConnection.getResponseCode() == 200) {
                    text = urlConnection.getResponseCode() + ":" + getResources().getString(R.string.login);

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
            StringBuilder result = new StringBuilder();
            String[] info = new String[2];
            Bundle bundle = new Bundle();

            //Take Id from server response
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                info = result.toString().split(" ");

            } catch (IOException e) {
                e.printStackTrace();
            }

            bundle.putString("token", info[0]);
            bundle.putString("id", info[1]);

            if (Integer.parseInt(error[0]) == 200) {
                Toast.makeText(SignIn.this, error[1], Toast.LENGTH_SHORT).show();
                txtUser.setText("");
                txtpassword.setText("");
                startActivity(new Intent(SignIn.this, UserActivity.class).putExtras(bundle));
            } else {
                Toast.makeText(SignIn.this, error[1], Toast.LENGTH_LONG).show();
            }

        }
    }

}
