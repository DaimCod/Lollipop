package com.example.lollipop.fragments;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.adapter.GridViewAdapter;
import com.example.lollipop.books.Book;
import com.example.lollipop.viewmodels.ViewModelBooks;
import java.util.ArrayList;

public class FragmentHome extends Fragment implements AdapterView.OnItemClickListener
{
    private ViewModelBooks viewModelBooks;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private ProgressBar load;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = view.findViewById(R.id.grid_book);
        load = view.findViewById(R.id.progressBarHome);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        getActivity().findViewById(R.id.button_settings).setVisibility(View.VISIBLE);
        view.setBackgroundColor(Color.WHITE);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        /*N.B.:
            CALLBACK, THE CODE WORKS; BUT IS TRICKY!
            WHEN WE RETRIEVE DATA FROM THE SERVER THE OPERATION IS ASYNCHRONOUS, AND THE APP NOT WAIT FOR THE RESPONSE
            SO IS NECESSARY TO IMPLEMENT AN INTERFACE WHEN THE DATA IS READY
         */

        //VIEW MODEL ALLOW SAVE DATA BETWEEN FRAGMENT; ROTATION
        viewModelBooks = new ViewModelProvider(requireActivity()).get(ViewModelBooks.class);

        if(viewModelBooks.getBooksHome().size() > 0 && viewModelBooks.getBooksNamesHome().size() > 0
                && viewModelBooks.getBooksCoversHome().size() > 0)
        {
            Log.d("x", "RELOADED!");

            gridViewAdapter = new GridViewAdapter(getContext(), viewModelBooks.getBooksNamesHome(), viewModelBooks.getBooksCoversHome());
            gridView.setAdapter(gridViewAdapter);
            load.setVisibility(View.GONE);
        }
        else
        {
            if(InternetConnection.isConnected(requireActivity()))
            {
                final Observer<ArrayList<Book>> observerBook = books ->
                {
                    gridViewAdapter = new GridViewAdapter(getContext(), viewModelBooks.getBooksNamesHome(), viewModelBooks.getBooksCoversHome());
                    gridView.setAdapter(gridViewAdapter);

                    //IS THE ONLY WAY TO KEEP SPINNING THE PROGRESS BAR; LOAD DOESN'T WAIT FOR SET ADAPTER
                    new Handler(Looper.getMainLooper()).postDelayed(() -> load.setVisibility(View.GONE), 3000);
                };

                // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                viewModelBooks.setModelBooksHome(getString(R.string.author_spacer));
                viewModelBooks.observeBooksHome().observe(getViewLifecycleOwner(), observerBook);
            }
            else
            {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        }
        gridView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(viewModelBooks.getBooksHome() != null)
        {
            viewModelBooks.setSelected(viewModelBooks.getBooksHome().get(position));
            //CREATE OBJECT CURRENT FRAGMENT
            Navigation.findNavController(view).navigate(R.id.fragment_book);
        }
    }
}