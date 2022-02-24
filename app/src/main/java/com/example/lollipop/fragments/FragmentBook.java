package com.example.lollipop.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.books.Book;
import com.example.lollipop.users.User;
import com.example.lollipop.viewmodels.ViewModelBookPlayer;
import com.example.lollipop.viewmodels.ViewModelBooks;
import com.example.lollipop.viewmodels.ViewModelUser;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class FragmentBook extends Fragment implements View.OnClickListener
{
    private TextView bookTitle;
    private ViewModelUser viewModelUser;
    private Book book;
    private ImageView fav;
    private User currentUser;
    private ViewModelBooks viewModelBooks;
    private ViewModelBookPlayer viewModelBookPlayer;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_book, container, false);

        getActivity().findViewById(R.id.button_settings).setVisibility(View.GONE);

        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        viewModelBookPlayer = new ViewModelProvider(requireActivity()).get(ViewModelBookPlayer.class);
        viewModelBooks = new ViewModelProvider(requireActivity()).get(ViewModelBooks.class);
        book = viewModelBooks.getSelected();
        currentUser = viewModelUser.getUser();

        //HIDE NAVIGATION BAR
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        if(book != null)
        {
            fav = view.findViewById(R.id.favorite);
            preSetImage();
            fav.setOnClickListener(this);

            bookTitle = view.findViewById(R.id.book_title);
            ImageView cover = view.findViewById(R.id.cover);
            TextView bookYear = view.findViewById(R.id.year_from_book);
            TextView bookCategories = view.findViewById(R.id.genre_from_book);
            TextView bookAuthors = view.findViewById(R.id.authors_from_book);
            TextView bookDescription = view.findViewById(R.id.description_from_book);

            Button startListening = view.findViewById(R.id.button_start_listening);

            bookTitle.setText(book.getTitle());
            Picasso.get().load(book.getUrl().toString())
                    .resize(125, 175)
                    .into(cover);
            bookYear.setText(book.getYear());
            bookCategories.setText(listToArray(book.getCategories()));
            bookAuthors.setText(listToArray(book.getAuthors()));
            bookDescription.setText(book.getDescription());

            startListening.setOnClickListener(this);
        }

       return view;
    }

    private String listToArray(List<String> arr)
    {
        StringBuilder help = new StringBuilder();
        int position = 0;

        for(String s : arr)
        {
            if(position == arr.size() -1)
            {
                help.append(s);
            }
            else
            {
               help.append(s).append("; ");
            }
        }
        return help.toString();
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.button_start_listening)
        {
            addNewBook("read");
            Log.d("X", "Playing: " + book.getNameOnServer());
            Navigation.findNavController(v).navigate(R.id.fragment_audio_player);
        }
        else if(v.getId() == R.id.favorite)
        {
            if(getDrawableId((ImageView) v) == R.drawable.im_favorite)
            {
                removeFromFavorite();
            }
            else
            {
                addNewBook("liked");
            }
        }
    }

    private void addNewBook(String type)
    {
        if(InternetConnection.isConnected(requireActivity()))
        {
            if(type.equals("liked"))
            {
               view.findViewById(R.id.layout_book).setClickable(false);

                ArrayList<String> userBooks = currentUser.getFavorites();
                viewModelBooks.deleteBookNamesUserLiked();

                if(userBooks.get(0).equals("false"))
                {
                    userBooks.set(0, "true");
                }
                userBooks.add(book.getNameOnServer());
                setBooks(userBooks, "favorites");
                preSetImage();
            }
            else if(type.equals("read"))
            {
                if(viewModelBookPlayer.getPlayerGeneral() != null
                        && !viewModelBookPlayer.getCurrentTrack().equalsIgnoreCase(viewModelBooks.getSelected().getNameOnServer()))
                {
                    currentUser.editSavedPos(viewModelUser.getUser().getReadPos(), viewModelUser.getUser().getBooksRead(),
                            viewModelBookPlayer.getCurrentTrack(),
                            viewModelBookPlayer.getBookPlayer().getCurrentPosition(),
                            viewModelBookPlayer.getBookPlayer().getDuration());

                    final Observer<User> userObserver = user ->
                    {
                        currentUser = user;
                        updateUser();
                    };

                    viewModelUser.updateUserData(currentUser);
                    viewModelUser.getUserMutable().observe(this, userObserver);
                }
                else
                {
                    updateUser();
                }
            }
        }
        else
        {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    private void removeFromFavorite()
    {
        if(InternetConnection.isConnected(requireActivity()))
        {
            view.findViewById(R.id.layout_book).setClickable(false);
            ArrayList<String> favoritesBooks = currentUser.getFavorites();

            for(String name : favoritesBooks)
            {
                if(name.equalsIgnoreCase(book.getNameOnServer()))
                {
                    favoritesBooks.remove(name);
                    break;
                }
            }

            if(favoritesBooks.size() == 1)
            {
                favoritesBooks.set(0, "false");
            }
            viewModelBooks.deleteBookNamesUserLiked();
            setBooks(favoritesBooks, "favorites");
        }
        else
        {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    public void preSetImage()
    {
        for(String name : viewModelUser.getUser().getFavorites())
        {
            if(name.equalsIgnoreCase(book.getNameOnServer()))
            {
                fav.setImageResource(R.drawable.im_favorite);
                fav.setTag(R.drawable.im_favorite);
                return;
            }
        }
        fav.setImageResource(R.drawable.im_not_favorite);
        fav.setTag(R.drawable.im_not_favorite);
    }

    private int getDrawableId(ImageView iv)
    {
        return (Integer) iv.getTag();
    }


    private void setBooks(ArrayList<String> favorites, String type)
    {
        final Observer<User> userObserver = user ->
        {
            currentUser = user;
            view.findViewById(R.id.layout_book).setClickable(true);

            if(type.equals("favorites"))
            {
                preSetImage();
            }
        };

        viewModelUser.updateBooks(favorites, currentUser.getFirebaseRef(), type);
        viewModelUser.getUserMutable().observe(this, userObserver);
    }

    private void updateUser()
    {
        if(!viewModelUser.getUser().getBooksRead().contains(viewModelBooks.getSelected().getNameOnServer()))
        {
            view.findViewById(R.id.layout_book).setClickable(false);

            ArrayList<String> userBooks = currentUser.getBooksRead();
            ArrayList<String> readPos = currentUser.getReadPos();

            viewModelBooks.deleteBookNamesUserRead();

            if(userBooks.get(0).equals("false"))
            {
                userBooks.set(0, "true");
            }

            if(readPos.get(0).equals("false"))
            {
                readPos.set(0, "true");
            }

            userBooks.add(book.getNameOnServer());
            readPos.add("0" + Constants.SPLITTER_DURATION + "0");

            setBooks(userBooks, "booksRead");
            setBooks(readPos, "readPos");
        }
    }
}