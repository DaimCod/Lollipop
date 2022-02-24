package com.example.lollipop.repository;

import com.example.lollipop.Utils.Callback;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import java.util.ArrayList;
import java.util.Objects;

public class RepositoryRecoverUserData
{
    private ArrayList<String> user;

    public RepositoryRecoverUserData()
    {
        user = new ArrayList<>();
    }

    public void recoverUser(Callback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task ->
        {
            if(task.getResult() != null)
            {
                for(DataSnapshot t : task.getResult().getChildren())
                {
                    if(Objects.requireNonNull(t.getKey()).equals("favorites")
                            || Objects.requireNonNull(t.getKey()).equals("booksRead")
                            || t.getKey().equals("readPos"))
                    {
                        StringBuilder favToString = new StringBuilder();

                        GenericTypeIndicator<ArrayList<String>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                        ArrayList<String> fav = t.getValue(genericTypeIndicator);

                        String prefix = "";
                        for(String book : fav)
                        {
                            favToString.append(prefix);
                            prefix = Constants.FAVORITES_SPLITTER;
                            favToString.append(book);
                        }
                        user.add(favToString.toString());
                    }
                    else if((t.getKey().equals("firebaseRef")))
                    {
                        user.add(task.getResult().getKey());
                    }
                    else
                    {
                        user.add(t.getValue().toString());
                    }
                }
                callback.onCallBack(user); ////Sent: age, booksRead, fav, firebaseRef, mail, name, password, readPos, surname, fav (with splitter <-!->)
            }
            else
            {
                callback.onError("Null user");
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }


    public void updateBooks(ArrayList<String> favorites, String currentUser, String what, Callback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUser)
                .child(what).setValue(favorites).addOnCompleteListener(task ->
                {
                    if(task.isSuccessful())
                    {
                        callback.onCallBack(favorites);
                    }
                }).addOnFailureListener(e ->
        {
            callback.onError(e.toString());
        });
    }


    public void updateUser(User userCurrent, Callback callback)
    {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userCurrent.getFirebaseRef())
                .setValue(userCurrent).addOnCompleteListener(task ->
        {
            if(task.isSuccessful())
            {
                callback.onCallBack(userCurrent.userToArrayList());
            }
        }).addOnFailureListener(e ->
        {
            callback.onError(e.toString());
        });
    }
}
