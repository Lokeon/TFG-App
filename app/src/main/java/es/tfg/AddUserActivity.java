package es.tfg;

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

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddUserActivity extends AppCompatActivity {
    private EditText txtUser;
    private EditText txtEmail;
    private Button btn_accept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.add_user);


        txtUser = (EditText) findViewById(R.id.TxtUser);
        txtEmail = (EditText) findViewById(R.id.TxtEmail);
        btn_accept = (Button) findViewById(R.id.BtnAddUser);

        txtUser.addTextChangedListener(textWatcher);
        txtEmail.addTextChangedListener(textWatcher);


        Button btn_home = (Button) findViewById(R.id.button_home);
        Button btn_date = (Button) findViewById(R.id.button_date);


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddUserActivity.this, MainActivity.class));
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddUserActivity.this, AddUserActivity.class));
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostUsers().execute();
            }
        });

    }

    // Para mirar que los inputs esten filled
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String user = txtUser.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();

            btn_accept.setEnabled(!user.isEmpty() && !email.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    class PostUsers extends AsyncTask<Void, Void, String> {

        private String user;
        private String email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user = txtUser.getText().toString();
            email = txtEmail.getText().toString();
        }

        @Override
        protected String doInBackground(Void... strings) {
            String text = null;
            BufferedWriter bufferedWriter = null;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("user", user);
                dataToSend.put("email", email);

                URL url = new URL(getResources().getString(R.string.ip_user));
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
                    text = getResources().getString(R.string.post_success);
                } else {
                    text = getResources().getString(R.string.post_fail);
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
            Toast.makeText(AddUserActivity.this, R.string.added_user, Toast.LENGTH_SHORT).show();
            txtUser.setText("");
            txtEmail.setText("");
        }
    }

}
