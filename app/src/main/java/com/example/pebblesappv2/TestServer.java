package com.example.pebblesappv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TestServer extends BaseACA {
    Realm realm;
    Button getJson;
    String staticIp = "192.168.1.130";
    String protocol = "http://";
    JSONArray jDlRsArray;
    JSONArray jTagRsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_server);

        // Setup realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();

//        getJson = (Button) findViewById(R.id.getJson);
//        getJson.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                jDlRsArray = getJsonResult("dbsqlite_return_downloads");
//                jTagRsArray = getJsonResult("dbsqlite_return_tags");
//
//                createModel_downloads(jDlRsArray);
//                createModel_tags(jTagRsArray);
//            }
//        });

    }

    public void createModel_downloads(JSONArray jsonArray) {
        // First delete previous records
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(YTDownloads.class);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                final String video_title = oneObject.getString("video_title");
                final String video_id = oneObject.getString("video_id");
                final int id = oneObject.getInt("id");
                final String[] tags_string = oneObject.getString("video_tags").split(",");
                final Integer[] tags = new Integer[tags_string.length];
                for (int j = 0; j < tags_string.length; j++) {
                    tags[j] = Integer.parseInt(tags_string[j]);
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        YTDownloads dl = realm.createObject(YTDownloads.class); // Create a new object
                        dl.setId(id);
                        dl.setVideo_id(video_id);
                        dl.setVideo_title(video_title);
                        RealmQuery<YTTags> query = realm.where(YTTags.class).in("id", tags);
                        RealmResults<YTTags> rs = query.findAll();
                        for (int k = 0; k < rs.size(); k++) {
//                            System.out.println(rs.get(k).getTag_name());
                            dl.getVideo_tags().add(rs.get(k));
                        }
                    }
                });

            } catch (JSONException e) {
                Log.d("EXCEPTION", e.getMessage());
            }
        }
    }

    public void createModel_tags(JSONArray jsonArray) {
        // First delete previous records
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(YTTags.class);
            }
        });

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                final String tag_name = oneObject.getString("tag_name");
                final int tag_order = oneObject.getInt("tag_order");
                final int id = oneObject.getInt("id");

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        YTTags tag = realm.createObject(YTTags.class); // Create a new object
                        tag.setId(id);
                        tag.setTag_name(tag_name);
                        tag.setTag_order(tag_order);
                    }
                });

            } catch (JSONException e) {
                Log.d("EXCEPTION", e.getMessage());
            }
        }
    }

    public JSONArray getJsonResult(String action) {
        //Some url endpoint that you may have
        String domain = protocol + staticIp;
        String myUrl = domain + "/" + "ytdownloader/" + action;
        String result;
        JSONArray jArray;

        HttpGetRequest getRequest = new HttpGetRequest();

        try {
            result = getRequest.execute(myUrl).get();

            // Create JSON Object from result string
            jArray = new JSONArray(result);
            return jArray;

        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage());
            return null;
        }
    }
}
