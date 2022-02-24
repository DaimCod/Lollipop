package com.example.lollipop.repository;

import com.example.lollipop.Utils.Callback;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;

public class RepositoryFetchUrlBook
{
    private ArrayList<String> bookUrl;

    public RepositoryFetchUrlBook()
    {
       bookUrl = new ArrayList<>();
    }

    public void getBookUrl(String spacer, String book, Callback firebaseCallback)
    {
        StringBuilder titleC = new StringBuilder();
        titleC.append(book
                .replace(" " + spacer + " ", "-")
                .replaceAll(" ", "_"));
        titleC.append(".mp3");

        FirebaseStorage.getInstance()
                .getReference().child("books/" + titleC.toString())
                .getDownloadUrl().addOnSuccessListener(uri ->
        {
            bookUrl.add(uri.toString());
            firebaseCallback.onCallBack(bookUrl);
        }).addOnFailureListener(e ->
        {
            firebaseCallback.onError(e.toString());
        });
    }
}
