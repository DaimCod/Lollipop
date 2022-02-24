package com.example.lollipop.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RepositoryFetchBook extends Thread
{
    private final String toGet;
    private JSONArray jsonArray;

    public RepositoryFetchBook(String spacer, String toGet)
    {
        this.toGet = editToSearch(spacer, toGet);
    }

    @Override
    public void run()
    {
        try
        {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + toGet);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder data = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null)
            {
                data.append(line);
            }

            if(data.length() > 0)
            {
                JSONObject jsonObject = new JSONObject(data.toString());
                jsonArray = jsonObject.getJSONArray("items");
            }
        } catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }

    private String editToSearch(String spacer, String toGet)
    {
        return toGet.replace(" " + spacer + " ", "+").replaceAll(" ", "+");
    }

    public JSONArray getJsonArray()
    {
        return jsonArray;
    }
}
