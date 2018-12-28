package cz.uhk.fim.runhk.service;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ElevationService {
    public AsyncResponse delegate = null;
    final double[] elevation = {0};

    @SuppressLint("StaticFieldLeak")
    public double getElevation(final double latitude, final double longitude) {

        new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... voids) {
                URL url = null;

                try {
                    url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" +
                            String.valueOf(latitude) + "," +
                            String.valueOf(longitude) +
                            "&key=AIzaSyDS6vXNVTJFOUkJTcVhHfsEhuFOwmtkNxk");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(30 * 1000);
                urlConnection.setRequestProperty("Accept", "application/json");

                // Set request type
                try {
                    urlConnection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                urlConnection.setDoOutput(false);
                urlConnection.setDoInput(true);

                try {
                    // Check for errors
                    int code = urlConnection.getResponseCode();
                    if (code != HttpsURLConnection.HTTP_OK)
                        throw new IOException("HTTP error " + urlConnection.getResponseCode());

                    // Get response
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null)
                        json.append(line);

                    // Decode result
                    JSONObject jroot = new JSONObject(json.toString());
                    String status = jroot.getString("status");
                    if ("OK".equals(status)) {
                        JSONArray results = jroot.getJSONArray("results");
                        if (results.length() > 0) {
                            elevation[0] = results.getJSONObject(0).getDouble("elevation");
                            System.out.println("Elevation " + elevation[0]);
                        } else
                            throw new IOException("JSON no results");
                    } else
                        throw new IOException("JSON status " + status);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return elevation[0];
            }

            @Override
            protected void onPostExecute(Double aDouble) {
                System.out.println("post execute");
                delegate.processFinish(aDouble);
            }
        }.execute();

        return elevation[0];
    }

}
