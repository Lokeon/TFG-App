package es.tfg.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import es.tfg.R;
import es.tfg.game.GameUser;
import es.tfg.game.Petition;
import es.tfg.list.ScoreList;
import es.tfg.recommendation.Recommendation;
import es.tfg.registration.SignIn;

public class Profile extends AppCompatActivity {
    int SELECT_AVATAR = 200;
    private DrawerLayout drawerLayout;
    private TextView userText;
    private TextView userNText;
    private TextView emailText;
    private TextView dateText;
    private TextView cPassword;
    private Button button;
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

    @Nullable
    public static String getPathUri(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;

        // Create file path inside app's data dir
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
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
        button = findViewById(R.id.BtnAvatar);
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

    public void goGame(View view) {
        startActivity(new Intent(Profile.this, GameUser.class).putExtras(bundleSend));
    }

    public void goChangePassword(View view) {
        startActivity(new Intent(Profile.this, ChangePassword.class).putExtras(bundleSend));
    }

    public void goList(View view) {
        startActivity(new Intent(Profile.this, ScoreList.class).putExtras(bundleSend));
    }

    public void goRecommendation(View view) {
        startActivity(new Intent(Profile.this, Recommendation.class).putExtras(bundleSend));
    }

    public void goPetition(View view) {
        startActivity(new Intent(Profile.this, Petition.class).putExtras(bundleSend));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_AVATAR) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    new PatchAvatar().execute(new UserInfoAvatar(token, id, getPathUri(getBaseContext(), data.getData())));
                }
            }
        }
    }

    public void goAvatar(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Avatar"), SELECT_AVATAR);
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

    private static class UserInfoAvatar {
        String id;
        String token;
        String avatar;

        UserInfoAvatar(String token, String id, String avatar) {
            this.id = id;
            this.token = token;
            this.avatar = avatar;
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

    class PatchAvatar extends AsyncTask<UserInfoAvatar, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected String doInBackground(UserInfoAvatar... strings) {
            String text;
            HttpURLConnection urlConnection = null;
            DataOutputStream dos = null;
            String fileName = strings[0].avatar;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            try {
                FileInputStream fileInputStream = new FileInputStream(fileName);

                URL url = new URL(getResources().getString(R.string.ip_cavatar));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(30000);
                urlConnection.setConnectTimeout(30000);
                urlConnection.setRequestMethod("PATCH");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("auth-token", strings[0].token);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dos = new DataOutputStream(urlConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"avatar\";filename=\"" + "avatar.jpg" + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                fileInputStream.close();
                dos.flush();
                dos.close();

                if (urlConnection.getResponseCode() == 200) {
                    text = urlConnection.getResponseCode() + ":" + getResources().getString(R.string.chavatar);
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
                Toast.makeText(Profile.this, error[1], Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this, UserActivity.class).putExtras(bundleSend));
            } else {
                Toast.makeText(Profile.this, error[1], Toast.LENGTH_LONG).show();
            }


        }
    }
}