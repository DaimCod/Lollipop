package com.example.lollipop.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.lollipop.Utils.Callback;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.books.Book;
import com.example.lollipop.repository.RepositoryFetchBook;
import com.example.lollipop.repository.RepositoryFetchBooksNames;
import com.example.lollipop.repository.RepositoryFetchUrlBook;

import java.util.ArrayList;

public class ViewModelBooks extends AndroidViewModel
{
    private MutableLiveData<ArrayList<String>> booksNamesHome, booksNamesUserLiked, booksNamesUserRead, booksNamesSearch;
    private MutableLiveData<ArrayList<Book>> booksHome, booksUserLiked, booksUserRead, booksSearch;
    private MutableLiveData<ArrayList<String>> booksCoverHome, booksCoverUserLiked, booksCoverUserRead, booksCoverSearch;
    private MutableLiveData<Book> selected;
    private MutableLiveData<String> selectedUrl, searchedBook;
    private RepositoryFetchBooksNames fetchBooksNamesUser, fetchBooksNamesHome, fetchBooksNamesSearch;
    private RepositoryFetchBook fetchBook;
    private RepositoryFetchUrlBook fetchUrlBook;
    private MutableLiveData<ArrayList<String>> booksReadPos;

    public ViewModelBooks(@NonNull Application application)
    {
        super(application);
        booksReadPos = new MutableLiveData<>(new ArrayList<>());
        booksHome = new MutableLiveData<>(new ArrayList<>());
        booksCoverHome = new MutableLiveData<>(new ArrayList<>());
        booksNamesHome = new MutableLiveData<>(new ArrayList<>());
        booksUserLiked = new MutableLiveData<>(new ArrayList<>());
        booksCoverUserLiked = new MutableLiveData<>(new ArrayList<>());
        booksNamesUserLiked = new MutableLiveData<>(new ArrayList<>());
        booksUserRead = new MutableLiveData<>(new ArrayList<>());
        booksCoverUserRead = new MutableLiveData<>(new ArrayList<>());
        booksNamesUserRead = new MutableLiveData<>(new ArrayList<>());
        booksSearch = new MutableLiveData<>(new ArrayList<>());
        booksNamesSearch = new MutableLiveData<>(new ArrayList<>());
        booksCoverSearch = new MutableLiveData<>(new ArrayList<>());
        fetchBook = null;
        selected = new MutableLiveData<>(null);
        selectedUrl = new MutableLiveData<>();
        searchedBook = new MutableLiveData<>();
        fetchUrlBook = null;
        fetchBooksNamesUser = null;
        fetchBooksNamesHome = null;
        fetchBooksNamesSearch = null;
    }


    public void setSelectedUrl(String spacer, String nameOnServer)
    {
        fetchUrlBook = new RepositoryFetchUrlBook();
        fetchUrlBook.getBookUrl(spacer, nameOnServer, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> urls)
            {
                selectedUrl.setValue(urls.get(0));
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, error);
            }
        });
    }

    public void setBooksSearch(String spacer, ArrayList<String> search)
    {
        fetchBooksNamesSearch = new RepositoryFetchBooksNames();
        fetchBooksNamesSearch.readData(spacer, search, null, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> names)
            {
                setData(names, spacer, "search");
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, error);
            }
        });
    }

    public void setModelBooksHome(String spacer)
    {
        fetchBooksNamesHome = new RepositoryFetchBooksNames();
        fetchBooksNamesHome.readData(spacer, null, null, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> names)
            {
                setData(names, spacer, "home");
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, "onError: " + error);
            }
        });
    }

    //Aggiungo parametro per libri utente letti
    public void setModelBooksUser(String spacer, ArrayList<String> likedBooks, String type)
    {
        fetchBooksNamesUser = new RepositoryFetchBooksNames();
        fetchBooksNamesUser.readData(spacer, likedBooks, Constants.FROM_USER_DATABASE, new Callback()
        {
            @Override
            public void onCallBack(ArrayList<String> names)
            {
                setData(names, spacer, type);
            }

            @Override
            public void onError(String error)
            {
                Log.d(Constants.LOG_TEXT, "onError: " + error);
            }
        });
    }


    private void setData(ArrayList<String> names, String spacer, String what)
    {
        ArrayList<Book> book_help = new ArrayList<>(0);
        ArrayList<String> cover_help = new ArrayList<>(0);

        for(int i = 0; i < names.size(); i++)
        {
            book_help.add(recoverBooksData(names.get(i), spacer));
            int size = book_help.size() - 1;
            //CHANGE STATEMENT TO SHOW DIFFERENT BOOKS IN HOME
            if(book_help.get(size).getCategories().contains(Constants.HIDE_BOOKS))
            {
                book_help.remove(size);
                names.remove(size);
                i--;
            }
            else
            {
                cover_help.add(book_help.get(size).getUrl().toString());
            }
        }

        if(what.equals("home"))
        {
            booksCoverHome.setValue(cover_help);
            booksNamesHome.setValue(names);
            booksHome.setValue(book_help);
        }
        else if(what.equals("liked"))
        {
            booksCoverUserLiked.setValue(cover_help);
            booksNamesUserLiked.setValue(names);
            booksUserLiked.setValue(book_help);
        }
        else if(what.equals("read"))
        {
            booksCoverUserRead.setValue(cover_help);
            booksNamesUserRead.setValue(names);
            booksUserRead.setValue(book_help);
        }
        else
        {
            booksCoverSearch.setValue(cover_help);
            booksNamesSearch.setValue(names);
            booksSearch.setValue(book_help);
        }
    }


    public Book recoverBooksData(String toSearch, String spacer)
    {
        fetchBook = new RepositoryFetchBook(spacer, toSearch);
        fetchBook.start();
        try
        {
            fetchBook.join();
            return new Book(fetchBook.getJsonArray(), toSearch);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //GET METHODS HOME
    public ArrayList<String> getBooksNamesHome()
    {
        return booksNamesHome.getValue();
    }

    public ArrayList<String> getBooksCoversHome()
    {
        return booksCoverHome.getValue();
    }

    public ArrayList<Book> getBooksHome()
    {
        return booksHome.getValue();
    }

    public MutableLiveData<ArrayList<Book>> observeBooksHome()
    {
        return booksHome;
    }

    //GET METHODS USER READ
    public ArrayList<String> getBooksNamesUserRead()
    {
        return booksNamesUserRead.getValue();
    }

    public ArrayList<String> getBooksCoversUserRead()
    {
        return booksCoverUserRead.getValue();
    }

    public ArrayList<Book> getBooksUserRead()
    {
        return booksUserRead.getValue();
    }

    public MutableLiveData<ArrayList<Book>> observeBooksUserRead()
    {
        return booksUserRead;
    }

    //GET METHODS USER LIKED
    public ArrayList<String> getBooksNamesUserLiked() { return booksNamesUserLiked.getValue(); }

    public ArrayList<String> getBooksCoversUserLiked() { return booksCoverUserLiked.getValue(); }

    public ArrayList<Book> getBooksUserLiked() { return booksUserLiked.getValue(); }

    public MutableLiveData<ArrayList<Book>> observeBooksUserLiked() { return booksUserLiked; }

    //GET METHODS SEARCH
    public ArrayList<String> getBooksNamesSearch()
    {
        return booksNamesSearch.getValue();
    }

    public ArrayList<String> getBooksCoverSearch()
    {
        return booksCoverSearch.getValue();
    }

    public ArrayList<Book> getBooksSearch()
    {
        return booksSearch.getValue();
    }

    public MutableLiveData<ArrayList<Book>> observeBooksSearch()
    {
        return booksSearch;
    }

    public String getSearchedBook()
    {
        return searchedBook.getValue();
    }

    public void setSearched(String ser)
    {
        searchedBook.setValue(ser);
    }

    //GET METHODS URL
    public MutableLiveData<String> observeBookUrl()
    {
        return selectedUrl;
    }


    //DELETE METHODS USER
    public void deleteBookNamesUserLiked()
    {
        booksUserLiked = new MutableLiveData<>(new ArrayList<>());
        booksCoverUserLiked = new MutableLiveData<>(new ArrayList<>());
        booksNamesUserLiked = new MutableLiveData<>(new ArrayList<>());
    }

    public void deleteBookNamesUserRead()
    {
        booksUserRead = new MutableLiveData<>(new ArrayList<>());
        booksCoverUserRead = new MutableLiveData<>(new ArrayList<>());
        booksNamesUserRead = new MutableLiveData<>(new ArrayList<>());
        booksReadPos = new MutableLiveData<>(new ArrayList<>());
    }

    //SELECT METHODS
    public Book getSelected()
    {
        return selected.getValue();
    }

    public void setSelected(Book sel)
    {
        this.selected.setValue(sel);
    }


}
