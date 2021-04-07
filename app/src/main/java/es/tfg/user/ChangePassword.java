package es.tfg.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

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
import es.tfg.registration.SignIn;

public class ChangePassword extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private EditText txtpassword;
    private EditText txtcpassword;
    private Button btn_accept;
    private Bundle bundle;
    private Bundle bundleSend;
    private String token;
    private String id;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pass = txtpassword.getText().toString().trim();
            String cpass = txtcpassword.getText().toString().trim();

            btn_accept.setEnabled(!pass.isEmpty() && !cpass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar_user);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.changePassword);
        top_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        bundle = getIntent().getExtras();
        token = bundle.getString("token").replace("\"", "");
        id = bundle.getString("id").replace("\"", "");

        bundleSend = new Bundle();
        bundleSend.putString("token", token);
        bundleSend.putString("id", id);


        top_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar_user);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        txtpassword = findViewById(R.id.TxtPassword);
        txtcpassword = findViewById(R.id.TxtCPassword);
        btn_accept = findViewById(R.id.BtnCPassword);
        drawerLayout = findViewById(R.id.drawer_cpassword);

        txtpassword.addTextChangedListener(textWatcher);
        txtcpassword.addTextChangedListener(textWatcher);

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtpassword.getText().toString().equals(txtcpassword.getText().toString())) {
                    Toast.makeText(ChangePassword.this, R.string.passDontMatch, Toast.LENGTH_LONG).show();
                } else {
                    new PatchPassword().execute(new UserInfo(token, id));
                }
            }
        });

    }

    private static class UserInfo {
        String id;
        String token;

        UserInfo(String token, String id) {
            this.id = id;
            this.token = token;
        }
    }

    class PatchPassword extends AsyncTask<UserInfo, Void, String> {
        private String pass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
        protected String doInBackground(UserInfo... strings) {
            String text;
            BufferedWriter bufferedWriter;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("password", pass);

                URL url = new URL(getResources().getString(R.string.ip_cpassword));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(30000);
                urlConnection.setConnectTimeout(30000);
                urlConnection.setRequestMethod("PATCH");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.connect();

                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                if (urlConnection.getResponseCode() == 200) {
                    text = urlConnection.getResponseCode() + ":" + getResources().getString(R.string.passchanged);
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
                Toast.makeText(ChangePassword.this, error[1], Toast.LENGTH_SHORT).show();
                txtpassword.setText("");
                txtcpassword.setText("");
                startActivity(new Intent(ChangePassword.this, SignIn.class));
            } else {
                Toast.makeText(ChangePassword.this, error[1], Toast.LENGTH_LONG).show();
            }

        }
    }
}