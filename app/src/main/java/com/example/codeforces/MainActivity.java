package com.example.codeforces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView ratingText;
    private TextView rankText;
    private TextView handleText;
    private TextView nameText;
    private TextView lastNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleText = findViewById(R.id.handleText);
        ratingText = findViewById(R.id.ratingText);
        rankText = findViewById(R.id.rankText);
        nameText = findViewById(R.id.nameText);
        lastNameText = findViewById(R.id.lastNameText);

        ratingText.setText("");
        rankText.setText("");
        nameText.setText("");
        lastNameText.setText("");

        Button button = findViewById(R.id.button);

        button.setOnClickListener(view -> getUserInfo(handleText.getText()));

    }

    private void getUserInfo(CharSequence handle) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.execute(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://codeforces.com/api/user.info?handles=" + handle);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String s = in.readLine();;
                while (s != null) {
                    json.append(s);
                    s = in.readLine();
                }

                UserInfo userInfo = parseJSON(json.toString());

                Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
                handler.post(() -> {
                    handleText.setText(userInfo.getHandle());
                    ratingText.setText(Integer.toString(userInfo.getRating()));
                    rankText.setText(userInfo.getRank());
                    lastNameText.setText(userInfo.getLastName());
                    nameText.setText(userInfo.getName());
                });
            } catch (IOException | JSONException e) {
                // TODO: здесь надо какой-то диалог показывать, что не получислось получить данные
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }

    private UserInfo parseJSON(String jsonStr) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonStr);

        JSONArray result = jsonObj.getJSONArray("result");
        JSONObject handleInfo = (JSONObject) result.get(0);

        String handle = handleInfo.getString("handle");
        int rating = handleInfo.getInt("rating");
        String rank = handleInfo.getString("rank");
        String firstName = handleInfo.getString("firstName");
        String lastName = handleInfo.getString("lastName");
        return new UserInfo(handle, rating, rank, firstName, lastName);
    }

    private static class UserInfo {
        private String handle;
        private int rating;
        private String rank;
        private String name;
        private String lastName;

        public UserInfo(String handle, int rating, String rank, String name, String lastName) {
            this.handle = handle;
            this.rating = rating;
            this.rank = rank;
            this.name = name;
            this.lastName = lastName;
        }

        public String getHandle() {
            return handle;
        }

        public int getRating() {
            return rating;
        }

        public String getRank() {
            return rank;
        }

        public String getName() {
            return name;
        }

        public String getLastName() {
            return lastName;
        }


    }
}