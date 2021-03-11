package es.tfg.registration;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import es.tfg.MainActivity;
import es.tfg.R;

public class SignUp extends AppCompatActivity {
    private EditText txtUser;
    private EditText txtEmail;
    private EditText txtpassword;
    private Button btn_accept;
    // Check that always fields are filled before send
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String user = txtUser.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String pass = txtpassword.getText().toString().trim();

            btn_accept.setEnabled(!user.isEmpty() && !email.isEmpty() && !pass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.sign_up);
        top_toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        top_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });


        txtUser = (EditText) findViewById(R.id.TxtUser);
        txtEmail = (EditText) findViewById(R.id.TxtEmail);
        txtpassword = (EditText) findViewById(R.id.TxtPassword);
        btn_accept = (Button) findViewById(R.id.BtnSignUp);

        txtUser.addTextChangedListener(textWatcher);
        txtEmail.addTextChangedListener(textWatcher);
        txtpassword.addTextChangedListener(textWatcher);


        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostUsers().execute();
            }
        });

    }

    class PostUsers extends AsyncTask<Void, Void, String> {

        private String user;
        private String email;
        private String pass;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user = txtUser.getText().toString();
            email = txtEmail.getText().toString();
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
            HttpURLConnection urlConnection = null;

            try {
                JSONObject dataToSend = new JSONObject();

                dataToSend.put("username", user);
                dataToSend.put("email", email);
                dataToSend.put("password", pass);

                URL url = new URL(getResources().getString(R.string.ip_register));
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
                    text = urlConnection.getResponseCode() + ":" + getResources().getString(R.string.added_user);
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
                Toast.makeText(SignUp.this, error[1], Toast.LENGTH_SHORT).show();
                txtUser.setText("");
                txtEmail.setText("");
                txtpassword.setText("");
                startActivity(new Intent(SignUp.this, MainActivity.class));
            } else {
                Toast.makeText(SignUp.this, error[1], Toast.LENGTH_LONG).show();
            }

        }
    }

}
