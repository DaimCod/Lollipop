package com.example.lollipop.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.lollipop.Utils.Callback;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.repository.RepositoryRecoverUserData;
import com.example.lollipop.users.User;
import java.util.ArrayList;
import java.util.Arrays;

public class ViewModelUser extends AndroidViewModel
{
    private MutableLiveData<User> user;
    private RepositoryRecoverUserData userData;

    public ViewModelUser(@NonNull Application application)
    {
        super(application);
        user = new MutableLiveData<>();
        userData = new RepositoryRecoverUserData();
    }

    public void findCurrentUserData()
    {
        userData.recoverUser(new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> u)
            {
                //received: age, booksRead, fav, firebaseRef, mail, name, password, readPos, surname (with splitter <-!->)
                //will be : firebaseRef, name, surname, age, mail, password, booksFav, booksRead, readPos)
                User temp = new User(u.get(3), u.get(5), u.get(8), u.get(0), u.get(4), u.get(6),
                        stringToArrayList(u.get(2)), stringToArrayList(u.get(1)), stringToArrayList(u.get(7)));
                user.setValue(temp);
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, "onError: " + error);
            }
        });
    }

    public void updateUserData(User userCurr)
    {
        userData.updateUser(userCurr, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> u)
            {
               User temp = new User(u.get(0), u.get(1), u.get(2), u.get(3), u.get(4), u.get(5),
                       stringToArrayList(u.get(6)), stringToArrayList(u.get(7)), stringToArrayList(u.get(8)));
               user.setValue(temp);
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, "onError: " + error);
            }
        });
    }

    public void updateBooks(ArrayList<String> favorites, String currentUser, String what)
    {
        userData.updateBooks(favorites, currentUser, what, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> books)
            {
                User help = user.getValue();
                if(what.equals("favorites"))
                {
                    help.setFavorites(books);
                }
                else if(what.equals("readPos"))
                {
                    help.setReadPos(books);
                }
                else
                {
                    help.setBooksRead(books);
                }
                user.setValue(help);
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, error);
            }
        });
    }


    public User getUser()
    {
        return user.getValue();
    }

    public MutableLiveData<User> getUserMutable()
    {
        return user;
    }

    private ArrayList<String> stringToArrayList(String toParse)
    {
        String [] split = toParse.split(Constants.FAVORITES_SPLITTER);
        return new ArrayList<>(Arrays.asList(split));
    }
}
