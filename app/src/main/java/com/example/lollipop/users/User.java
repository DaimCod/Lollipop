package com.example.lollipop.users;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.lollipop.Utils.Constants;

import java.util.ArrayList;

public class User implements Parcelable {
    private String firebaseRef, name, surname, age, password, mail;
    private ArrayList<String> favorites, read, readPos;

    public User(String firebaseRef, String name, String surname, String age, String mail, String password,
                ArrayList<String> booksFav, ArrayList<String> booksRead, ArrayList<String> readPos) {
        this.firebaseRef = firebaseRef;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.mail = mail;
        this.password = password;
        this.favorites = booksFav;
        this.read = booksRead;
        this.readPos = readPos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAge() {
        return age;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() { return password; }

    public String getFirebaseRef() {
        return firebaseRef;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public ArrayList<String> getBooksRead() {
        return read;
    }

    public ArrayList<String> getReadPos() { return readPos; }

    public void setFavorites(ArrayList<String> fav) {
        favorites = fav;
    }

    public void setBooksRead(ArrayList<String> read) { this.read = read; }

    public void setReadPos(ArrayList<String> readPos) { this.readPos = readPos; }

    public ArrayList<String> userToArrayList()
    {
        ArrayList<String> toSend = new ArrayList<>();
        toSend.add(firebaseRef);
        toSend.add(name);
        toSend.add(surname);
        toSend.add(age);
        toSend.add(mail);
        toSend.add(password);
        toSend.add(helpBuilder(favorites));
        toSend.add(helpBuilder(read));
        toSend.add(helpBuilder(readPos));
        return toSend;
    }

    private String helpBuilder(ArrayList<String>toBuild)
    {
        String prefix = "";
        StringBuilder fav = new StringBuilder();

        for(String book : toBuild)
        {
            fav.append(prefix);
            prefix = Constants.FAVORITES_SPLITTER;
            fav.append(book);
        }
        return fav.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firebaseRef);
        dest.writeString(this.name);
        dest.writeString(this.surname);
        dest.writeString(this.age);
        dest.writeString(this.password);
        dest.writeString(this.mail);
        dest.writeStringList(this.favorites);
        dest.writeStringList(this.read);
        dest.writeStringList(this.readPos);
    }

    public void readFromParcel(Parcel source) {
        this.firebaseRef = source.readString();
        this.name = source.readString();
        this.surname = source.readString();
        this.age = source.readString();
        this.password = source.readString();
        this.mail = source.readString();
        this.favorites = source.createStringArrayList();
        this.read = source.createStringArrayList();
        this.readPos = source.createStringArrayList();
    }

    protected User(Parcel in) {
        this.firebaseRef = in.readString();
        this.name = in.readString();
        this.surname = in.readString();
        this.age = in.readString();
        this.password = in.readString();
        this.mail = in.readString();
        this.favorites = in.createStringArrayList();
        this.read = in.createStringArrayList();
        this.readPos = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void editSavedPos(ArrayList<String> readPos, ArrayList<String> readBooks, String nameOnServer,
                             int trackPos, int maxDuration)
    {
        if(readPos.get(0).equalsIgnoreCase("false"))
        {
            readPos.set(0, "true");
        }

        for(int i = 0; i < readBooks.size(); ++i)
        {
            if(readBooks.get(i).equalsIgnoreCase(nameOnServer))
            {
                readPos.set(i, trackPos + Constants.SPLITTER_DURATION + maxDuration);
                break;
            }
        }
        this.setReadPos(readPos);
    }




}
