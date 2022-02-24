package com.example.lollipop.fragments;

import static android.content.Context.MODE_PRIVATE;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.FilterListeners;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.adapter.AutoCompleteAdapter;
import com.example.lollipop.adapter.GridViewAdapter;
import com.example.lollipop.books.Book;
import com.example.lollipop.itemList.ItemList;
import com.example.lollipop.viewmodels.ViewModelBooks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FragmentSearch extends Fragment implements View.OnClickListener, FilterListeners
{
    private AutoCompleteTextView toSearch;
    private ImageView searchImage;
    private ViewModelBooks viewModelBooks;
    private GridView gridView;
    private ProgressBar load;
    private GridViewAdapter gridViewSearchAdapter;
    private TextView noBooks;
    private Set<String> searchHistorySet;
    private AutoCompleteAdapter adapterSearch;
    private String stringToSearch;
    private int counterLoad = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        getActivity().findViewById(R.id.button_settings).setVisibility(View.GONE);
        //SHOW NAVIGATION BAR
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        viewModelBooks = new ViewModelProvider(requireActivity()).get(ViewModelBooks.class);
        gridView = view.findViewById(R.id.grid_book_search);
        load = view.findViewById(R.id.progress_bar_search);

        toSearch = view.findViewById(R.id.fragment_search_title);

        ImageView buttonSearch = view.findViewById(R.id.image_button_search);
        noBooks = view.findViewById(R.id.noBooksFound);
        searchImage = view.findViewById(R.id.searchImage);
        searchHistorySet = new HashSet<>();

        readInformation();

        buttonSearch.setOnClickListener(this);
        setItemClickGrid();

        //RELOAD OLD INFO
        if(viewModelBooks.getSearchedBook() != null)
        {
            toSearch.setText(viewModelBooks.getSearchedBook());

            if(viewModelBooks.getBooksSearch().size() > 0)
            {
                Log.d(Constants.LOG_TEXT, "onCreateView: reload");
                gridViewSearchAdapter = new GridViewAdapter(getContext(), viewModelBooks.getBooksNamesSearch(), viewModelBooks.getBooksCoverSearch());
                gridView.setAdapter(gridViewSearchAdapter);
                gridView.setVisibility(View.VISIBLE);
                noBooks.setVisibility(View.GONE);
                searchImage.setVisibility(View.GONE);
            }
            else
            {
                gridView.setVisibility(View.INVISIBLE);
                noBooks.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.INVISIBLE);
            }
        }

        final Observer<ArrayList<Book>> observerBook = books ->
        {
            counterLoad++;
            if(books.size() == 0 && counterLoad > 1)
            {
                load.setVisibility(View.GONE);
                gridView.setVisibility(View.INVISIBLE);
                noBooks.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.INVISIBLE);
            }
            else if(counterLoad > 1)
            {
                hideKeyboard();
                gridViewSearchAdapter = new GridViewAdapter(getContext(), viewModelBooks.getBooksNamesSearch(), viewModelBooks.getBooksCoverSearch());
                gridView.setAdapter(gridViewSearchAdapter);
                noBooks.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                searchImage.setVisibility(View.INVISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(() -> load.setVisibility(View.GONE), 3000);

                searchHistorySet.add(stringToSearch);
                saveInformation();
            }
        };
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModelBooks.observeBooksSearch().observe(getViewLifecycleOwner(), observerBook);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        stringToSearch = toSearch.getText().toString().trim();

        if(!stringToSearch.isEmpty() && InternetConnection.isConnected(requireActivity()))
        {
            viewModelBooks.setSearched(stringToSearch);
            load.setVisibility(View.VISIBLE);
            ArrayList<String> help = new ArrayList<>();
            help.add(stringToSearch);

            viewModelBooks.setBooksSearch(getString(R.string.author_spacer), help);
        }
    }

    private void saveInformation()
    {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                Constants.SHARED_PREF_SEARCH_HISTORY_FILE_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putStringSet(Constants.SHARED_PREF_SEARCH_HISTORY_KEY, searchHistorySet);
        editor.apply();
        //EDITING
        adapterSearch = new AutoCompleteAdapter(getContext(), setToArray(searchHistorySet));
        adapterSearch.setFilterListeners(this);
        setItemClickList();
        toSearch.setAdapter(adapterSearch);
    }


    private void readInformation()
    {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                Constants.SHARED_PREF_SEARCH_HISTORY_FILE_NAME, MODE_PRIVATE);

        if(sharedPref.contains(Constants.SHARED_PREF_SEARCH_HISTORY_KEY))
        {
            searchHistorySet.clear();
            searchHistorySet = sharedPref.getStringSet(Constants.SHARED_PREF_SEARCH_HISTORY_KEY, null);

            adapterSearch = new AutoCompleteAdapter(getContext(), setToArray(searchHistorySet));
            adapterSearch.setFilterListeners(this);
            setItemClickList();
            toSearch.setAdapter(adapterSearch);
        }
    }

    private ArrayList<ItemList> setToArray(Set<String>toFlip)
    {
        ArrayList<ItemList>help = new ArrayList<>();
        for(String el : toFlip)
        {
           help.add(new ItemList(el, R.drawable.ic_delete));
        }
        return help;
    }

    @Override
    public void filteringFinished(int filteredItemsCount)
    {
        if(filteredItemsCount < Constants.MAX_ELEMENTS_SEARCH_LIST)
        {
            toSearch.setDropDownHeight((filteredItemsCount + 1) * 100);
        }
        else
        {
            toSearch.setDropDownHeight((Constants.MAX_ELEMENTS_SEARCH_LIST + 1) * 100);
        }
    }

    public void hideKeyboard()
    {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }


    private void setItemClickGrid()
    {
        gridView.setOnItemClickListener((parent, view, position, id) ->
        {
            if(viewModelBooks.getBooksSearch() != null)
            {
                viewModelBooks.setSelected(viewModelBooks.getBooksSearch().get(position));
                //CREATE OBJECT CURRENT FRAGMENT
                Navigation.findNavController(view).navigate(R.id.fragment_book);
            }
        });
    }

    private void setItemClickList()
    {
        adapterSearch.setListener((text, what) ->
        {
            if(what)
            {
                searchHistorySet.remove(text);
                toSearch.setDropDownHeight(toSearch.getDropDownHeight() - 100);
                toSearch.dismissDropDown();
                if(toSearch.getDropDownHeight() > 100)
                {
                    toSearch.showDropDown();
                }
                saveInformation();
            }
            else
            {
                toSearch.setText(text);
            }
            return null;
        });
    }
}
