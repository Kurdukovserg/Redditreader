package com.example.redditreader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    ListView listView;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        contactList = new ArrayList<>();
        new GetRedditData().execute();
    }

    private class GetRedditData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://www.reddit.com/r/popular/top.json";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject data = jsonObj.getJSONObject("data");
                    JSONArray contacts = data.getJSONArray("children");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject d = contacts.getJSONObject(i);
                        JSONObject c = d.getJSONObject("data");
                        String name = c.getString("author");
                        long date = c.getLong("created_utc");
                        date *= 1000;
                        long timePass = System.currentTimeMillis() - date;
                        Integer hours = (int) timePass/(1000*60*60);
                        String thumbnail = c.getString("thumbnail");
                        if(thumbnail.equals("default")||thumbnail.equals("self")){
                            Integer thumb = R.drawable.default_img;
                            thumbnail = thumb.toString();
                        }
                        String imgName = "img" + i;
                        Drawable dr = sh.LoadImageFromWebOperations(thumbnail, imgName);
                        Integer resID = getResources().getIdentifier(imgName, "drawable", getPackageName());
                        String title = c.getString("title");
                        String comments = c.getString("num_comments");
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("name", name);
                        contact.put("date", hours.toString());
                        contact.put("comments", comments);
                        contact.put("title", title);
                        contact.put("thumbnail", resID.toString());
                        contact.put("dr", imgName);
                        Log.e(TAG, "contact map: " + imgName + " " + resID);
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "name","date", "title", "thumbnail"},
                    new int[]{R.id.author, R.id.date_of_creation, R.id.thread, R.id.thumbnail});
            listView.setAdapter(adapter);
        }
    }
}