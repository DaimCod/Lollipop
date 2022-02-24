package com.example.lollipop.repository;

import com.example.lollipop.Utils.Callback;
import com.example.lollipop.Utils.Constants;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RepositoryFetchBooksNames
{
    private ArrayList<String> booksNames;

    public RepositoryFetchBooksNames()
    {
       booksNames = new ArrayList<>();
    }

    public void readData(String spacer, ArrayList<String> search, String type, Callback firebaseCallback)
    {
        StorageReference st = FirebaseStorage.getInstance().getReference().child("books");
        st.listAll().addOnSuccessListener(listResult ->
        {
            if(type != null && type.equalsIgnoreCase(Constants.FROM_USER_DATABASE))
            {
               booksNames.addAll(search);
               booksNames.remove(0);
            }
            else
            {
                for(StorageReference item : listResult.getItems())
                {
                    String normalized = item.getName().replace(".mp3", "")
                            .replace("-", " " + spacer + " ")
                            .replaceAll("_", " ");

                    if(compareSearch(search, normalized, spacer))
                    {
                        booksNames.add(normalized);
                    }
                }
            }
            firebaseCallback.onCallBack(booksNames);
        }).addOnFailureListener(e -> firebaseCallback.onError(e.getMessage()));
    }

    private boolean compareSearch(ArrayList<String> toSearch, String bookTitleComplete, String spacer)
    {
        if(toSearch == null)
        {
            return true;
        }
        else if(toSearch.get(0).equalsIgnoreCase(spacer.trim()))
        {
            return false;
        }
        else
        {
            for(String bookUser : toSearch)
            {
                if(bookUser.equalsIgnoreCase(bookTitleComplete) ||
                        Pattern.compile(Pattern.quote(bookUser), Pattern.CASE_INSENSITIVE).matcher(bookTitleComplete).find())
                {
                    return true;
                }
            }
            return false;
        }
    }

}
