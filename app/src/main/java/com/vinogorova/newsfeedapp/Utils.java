package com.vinogorova.newsfeedapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class Utils {

    private static final String LOG_TAG = "Utils";

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    //Keys for extracting data from JSON
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_PUBLICATION_DATE = "webPublicationDate";
    private static final String KEY_SECTION_NAME = "sectionName";
    private static final String KEY_URL = "webUrl";
    private static final String KEY_REFERENCES = "references";
    private static final String KEY_ID = "id";


    /**
     * Handle all stages of receiving List of articles from JSON
     * @param requestUrl
     * @return articleList
     */
    public static List<Article> fetchNewsData(String requestUrl){

        String jsonResponse = null;

        URL request = createUrl(requestUrl);
        try {
           jsonResponse = makeHttpRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Article> articleList = extractArticles(jsonResponse);
        return articleList;
    }

    /**
     * This method creates URL from the given String
     * @param urlString
     * @return url
     */
    private static URL createUrl (String urlString){

        URL url = null;

        if (urlString == null){
            Log.e(LOG_TAG, "urlString value is null");
            return null;
        }
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * This method opens Http Connection and receives InputStream from it
     * @param urlRequest
     * @return
     * @throws IOException
     */
    private static String makeHttpRequest(URL urlRequest) throws IOException{

        String jsonResponse = "";
        if (urlRequest == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;


        try {
            urlConnection = (HttpURLConnection) urlRequest.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = receiveNewsData(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Receive data from given InputStream
     * and return String with JSON data
     * @param inputStream
     * @return jsonString
     * @throws IOException
     */
    private static String receiveNewsData(InputStream inputStream) throws IOException {
        if (inputStream == null){
            Log.e(LOG_TAG, "InputStream value is null");
            return null;
        }
        StringBuilder jsonString = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String line = reader.readLine();

        while (line != null){
            jsonString.append(line);
            line = reader.readLine();
        }
        return jsonString.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Article> extractArticles (String jsonString){

        if (TextUtils.isEmpty(jsonString)){
            Log.e(LOG_TAG, "JSON string is empty");
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(jsonString);
            JSONObject responseObject = object.getJSONObject("response");
            JSONArray array = responseObject.getJSONArray("results");
            for (int i = 0; i < array.length(); i++){
                JSONObject currentObject = array.getJSONObject(i);
                String title = currentObject.getString(KEY_TITLE);
                String date = currentObject.getString(KEY_PUBLICATION_DATE).substring(0, 10);
                String section = currentObject.getString(KEY_SECTION_NAME);
                String url = currentObject.getString(KEY_URL);
                JSONArray references = currentObject.getJSONArray(KEY_REFERENCES);
                String author = null;
                if (references.length() > 0){
                    JSONObject authorObject = references.getJSONObject(0);
                    String[] authorInfo = authorObject.getString(KEY_ID).split("/");
                    author = authorInfo[1];
                    Log.e(LOG_TAG, "Author name " + author);
                }


                articles.add(new Article(title, date, section, author, url));

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error with reading JSON data from String", e);
        }
        return articles;
    }
}
