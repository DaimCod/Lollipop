package com.example.lollipop.books;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable
{
    //IMPLEMENTARE DI PASSARE A PROSSIMO OGGETO SE UNO DEI PARAMETRI E' VUOTO
    private String title = "";
    private List<String> authors = new ArrayList<>(0);
    private String description = "";
    private URL url;
    private List<String> categories = new ArrayList<>(0);
    private String year = "";
    private String nameOnServer;

    //NB: SINTASSI LIBRI IN FIREBASE (RISPETTARLA): NOME_DEL_LIBRO-NOME_DELL_AUTORE
    
    public Book(@NonNull JSONArray jsonArray, String nameOnServer)
    {
        int size = jsonArray.length(), position = 0;
        String u = "";

        try
        {
            //IF WE NEED BOOKS ONLY IN ENGLISH ADD A CLAUSE IN THE WHILE
            while ((title.isEmpty() || authors.size() == 0 || description.isEmpty() || u.isEmpty()
                        || categories.size() == 0) && position < size)
            {
                JSONObject book = jsonArray.getJSONObject(position);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                title = getData(volumeInfo, title, "title");

                description = getData(volumeInfo, description, "description");

                year = getData(volumeInfo, year, "publishedDate");

                fillArray(volumeInfo, "categories", categories);

                fillArray(volumeInfo, "authors", authors);

                if(u.isEmpty() && volumeInfo.has("imageLinks"))
                {
                    u = volumeInfo.getJSONObject("imageLinks")
                            .getString("thumbnail")
                            .replace("http", "https");
                }
                position++;
            }
            url = new URL(u);
            this.nameOnServer = nameOnServer;
        }
        catch (JSONException | MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public String getYear()
    {
        return year;
    }

    public String getTitle()
    {
        return title;
    }

    public List<String> getAuthors()
    {
        return authors;
    }

    public String getDescription()
    {
        return description;
    }

    public URL getUrl()
    {
        return url;
    }

    public List<String> getCategories()
    {
        return categories;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                ", description='" + description + '\'' +
                ", url=" + url +
                ", categories=" + categories +
                ", year='" + year + '\'' +
                ", nameOnServer='" + nameOnServer + '\'' +
                '}';
    }

    public String getNameOnServer()
   {
        return nameOnServer;
   }


    private String getData(JSONObject jsonObject, String field, String toSearch)
    {
       if(field.isEmpty())
       {
           if(jsonObject.has(toSearch))
           {
               try
               {
                   return jsonObject.getString(toSearch);
               }
               catch (JSONException e)
               {
                  return "";
               }
           }
           return "";
       }
       else
       {
           return field;
       }
    }

    private void fillArray(JSONObject jsonObject, String toSearch, List<String> arr)
    {
        if(arr.size() == 0 && jsonObject.has(toSearch))
        {
            try
            {
                JSONArray cat = jsonObject.getJSONArray(toSearch);

                for(int i = 0; i < cat.length(); i++)
                {
                    arr.add(cat.getString(i));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeStringList(this.authors);
        dest.writeString(this.description);
        dest.writeSerializable(this.url);
        dest.writeStringList(this.categories);
        dest.writeString(this.year);
        dest.writeString(this.nameOnServer);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.authors = source.createStringArrayList();
        this.description = source.readString();
        this.url = (URL) source.readSerializable();
        this.categories = source.createStringArrayList();
        this.year = source.readString();
        this.nameOnServer = source.readString();
    }

    protected Book(Parcel in) {
        this.title = in.readString();
        this.authors = in.createStringArrayList();
        this.description = in.readString();
        this.url = (URL) in.readSerializable();
        this.categories = in.createStringArrayList();
        this.year = in.readString();
        this.nameOnServer = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
