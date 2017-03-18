package com.example.promahmoud.task;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.promahmoud.task.R;

public class MainActivity extends Activity {

	ArrayList<Actors> actorsList;

	ActorAdapter adapter;
    ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actorsList = new ArrayList<Actors>();
		new JSONAsyncTask().execute("https://api.github.com/users/Square/repos");

		listview = (ListView)findViewById(R.id.list);
		adapter = new ActorAdapter(getApplicationContext(), R.layout.row, actorsList);

		listview.setAdapter(adapter);

        OnLongClick();

	}
    public void  OnLongClick(){
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder show_url = new AlertDialog.Builder(MainActivity.this);
                show_url.setMessage("Chooser To go To owner or page github")
                        .setCancelable(false)
                        .setPositiveButton("Owner Link", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Repo Link", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = show_url.create();
                alert.setTitle("Go To");
                alert.show();


                // Toast.makeText(MainActivity.this , "Happy" , Toast.LENGTH_LONG).show();
                return true;
            }
        });

    }

	class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Loading, please wait");
			dialog.setTitle("Connecting server");
			dialog.show();
			dialog.setCancelable(false);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			try {

				//------------------>>
				HttpGet httppost = new HttpGet(urls[0]);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(httppost);

				// StatusLine stat = response.getStatusLine();
				int status = response.getStatusLine().getStatusCode();

				if (status == 200) {
					HttpEntity entity = response.getEntity();
					String data = EntityUtils.toString(entity);



					JSONObject jsono = new JSONObject(data);
					JSONArray jarray = jsono.getJSONArray("");

					for (int i = 0; i < jarray.length(); i++) {

						JSONObject object = jarray.getJSONObject(i);

						Actors actor = new Actors();

						actor.setName(object.getString("name"));
						/*///*actor.setDescription(object.getString("description"));
						actor.setDob(object.getString("dob"));
						actor.setCountry(object.getString("country"));
						actor.setHeight(object.getString("height"));
						actor.setSpouse(object.getString("spouse"));
						actor.setChildren(object.getString("children"));*/
						actor.setImage(object.getString("avatar_url"));
						actorsList.add(actor);

					}
					return true;
				}

				//------------------>>

			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			dialog.cancel();
			adapter.notifyDataSetChanged();
			if(result == false)
				Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

		}
	}



}
