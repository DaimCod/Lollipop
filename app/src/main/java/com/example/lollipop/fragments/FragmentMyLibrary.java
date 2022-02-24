package com.example.lollipop.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.adapter.RecyclerViewAdapter;
import com.example.lollipop.books.Book;
import com.example.lollipop.home.HomeActivity;
import com.example.lollipop.viewmodels.ViewModelBooks;
import com.example.lollipop.viewmodels.ViewModelUser;
import java.util.ArrayList;

public class FragmentMyLibrary extends Fragment
{
    private ViewModelBooks viewModelBooks;
    private RecyclerView recyclerViewLiked, recyclerViewRead;
    private RecyclerViewAdapter adapterLiked, adapterRead;
    private ProgressBar progressBarLiked, progressBarRead;
    private ViewModelUser viewModelUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_library, container, false);
        //SHOW NAVIGATION BAR
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        getActivity().findViewById(R.id.button_settings).setVisibility(View.GONE);

        viewModelBooks = new ViewModelProvider(requireActivity()).get(ViewModelBooks.class);
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);

        LinearLayoutManager layoutManagerLiked = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerRead = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerViewLiked = view.findViewById(R.id.recycler_view_liked);
        recyclerViewRead = view.findViewById(R.id.recycler_view_read);

        recyclerViewLiked.setLayoutManager(layoutManagerLiked);
        recyclerViewRead.setLayoutManager(layoutManagerRead);

        progressBarLiked = view.findViewById(R.id.progressBarLiked);
        progressBarRead = view.findViewById(R.id.progressBarRead);

        TextView noLiked = view.findViewById(R.id.noLikedBooks);
        TextView noRead = view.findViewById(R.id.noReadBooks);

        ((HomeActivity)getActivity()).updatePosTrack();

        if(viewModelBooks.getBooksUserLiked().size() > 0 && viewModelBooks.getBooksNamesUserLiked().size() > 0
                && viewModelBooks.getBooksCoversUserLiked().size() > 0)
        {
            Log.d("x", "RELOADED!");
            setItemClickLiked(view);
            recyclerViewLiked.setAdapter(adapterLiked);
            progressBarLiked.setVisibility(View.GONE);
            noLiked.setVisibility(View.GONE);
        }

        else
        {
            if (viewModelUser.getUser().getFavorites().size() > 1)
            {
                if (InternetConnection.isConnected(requireActivity()))
                {
                    noLiked.setVisibility(View.GONE);
                    final Observer<ArrayList<Book>> observerBooksLiked = books ->
                    {
                        adapterLiked = new RecyclerViewAdapter(getContext(),
                                viewModelBooks.getBooksNamesUserLiked(),
                                viewModelBooks.getBooksCoversUserLiked(),
                                null,
                                position -> {
                                    Log.d(Constants.LOG_TEXT, "onCreateView: " + position);
                                });
                        setItemClickLiked(view);
                        recyclerViewLiked.setAdapter(adapterLiked);
                        //IS THE ONLY WAY TO KEEP SPINNING THE PROGRESS BAR; LOAD DOESN'T WAIT FOR SET ADAPTER
                        new Handler(Looper.getMainLooper()).postDelayed(() -> progressBarLiked.setVisibility(View.GONE), 2000);
                    };

                    // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                    viewModelBooks.setModelBooksUser(getString(R.string.author_spacer),
                            viewModelUser.getUser().getFavorites(), "liked");
                    viewModelBooks.observeBooksUserLiked().observe(getViewLifecycleOwner(), observerBooksLiked);
                }
                else
                {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                progressBarLiked.setVisibility(View.GONE);
            }
        }

        if(viewModelUser.getUser().getBooksRead().size() > 1)
        {
            if(InternetConnection.isConnected(requireActivity()))
            {
                noRead.setVisibility(View.GONE);
                final Observer<ArrayList<Book>> observerBooksRead = books ->
                {
                    adapterRead = new RecyclerViewAdapter(getContext(),
                            viewModelBooks.getBooksNamesUserRead(),
                            viewModelBooks.getBooksCoversUserRead(),
                            viewModelUser.getUser().getReadPos(),
                            position -> {
                                Log.d(Constants.LOG_TEXT, "onCreateView: " + position);
                            });
                    setItemClickRead(view);
                    recyclerViewRead.setAdapter(adapterRead);
                    //IS THE ONLY WAY TO KEEP SPINNING THE PROGRESS BAR; LOAD DOESN'T WAIT FOR SET ADAPTER
                    new Handler(Looper.getMainLooper()).postDelayed(() -> progressBarRead.setVisibility(View.GONE), 2000);
                };

                // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
                viewModelBooks.setModelBooksUser(getString(R.string.author_spacer),
                        viewModelUser.getUser().getBooksRead(), "read");
                viewModelBooks.observeBooksUserRead().observe(getViewLifecycleOwner(), observerBooksRead);
            }
            else
            {
                Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            progressBarRead.setVisibility(View.GONE);
        }


        return view;
    }

    private void setItemClickLiked(View view)
    {
        adapterLiked = new RecyclerViewAdapter(getContext(),
                viewModelBooks.getBooksNamesUserLiked(),
                viewModelBooks.getBooksCoversUserLiked(),
                null,
                position ->
        {
            viewModelBooks.setSelected(viewModelBooks.getBooksUserLiked().get(position));
            //CREATE OBJECT CURRENT FRAGMENT
            Navigation.findNavController(view).navigate(R.id.fragment_book);
            Log.d(Constants.LOG_TEXT, String.valueOf(position));
        });

    }

    private void setItemClickRead(View view)
    {
        adapterRead = new RecyclerViewAdapter(getContext(),
                viewModelBooks.getBooksNamesUserRead(),
                viewModelBooks.getBooksCoversUserRead(),
                viewModelUser.getUser().getReadPos(),
                position ->
        {
            viewModelBooks.setSelected(viewModelBooks.getBooksUserRead().get(position));
            //CREATE OBJECT CURRENT FRAGMENT
            Navigation.findNavController(view).navigate(R.id.fragment_book);
            Log.d(Constants.LOG_TEXT, String.valueOf(position));
        });
    }
}