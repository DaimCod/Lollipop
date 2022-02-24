package com.example.lollipop.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.adapter.RecyclerViewAdapter;
import com.example.lollipop.books.Book;
import com.example.lollipop.users.User;
import com.example.lollipop.viewmodels.ViewModelBookPlayer;
import com.example.lollipop.viewmodels.ViewModelBooks;
import com.example.lollipop.viewmodels.ViewModelUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Spliterator;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private FloatingActionButton floatingActionButton;
    private ViewModelUser viewModelUser;
    private NavController navController;
    private ViewModelBookPlayer viewModelBookPlayer;
    private ViewModelBooks viewModelBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //SETTING UP VIEW MODEl USER
        viewModelUser = new ViewModelProvider(this).get(ViewModelUser.class);

        //SETTING UP VIEW MODEL PLAYER
        viewModelBookPlayer = new ViewModelProvider(this).get(ViewModelBookPlayer.class);

        //SETTING UP VIEW MODEL BOOK
        viewModelBooks = new ViewModelProvider(this).get(ViewModelBooks.class);

        //SETTING UP NAVIGATION
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        //---------------------

        //SETTING UP TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        //------------------

        //SETTING UP APPBAR
        AppBarConfiguration appBarConfiguration = new
                AppBarConfiguration.Builder(
                R.id.fragment_home,
                R.id.fragment_search,
                R.id.fragment_my_library).build();
        //----------------

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //CHANGING COLOR TOOLBAR/BOTTOM NAV ONLY FOR TEST PURPOSE
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        bottomNavigationView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        //SETTING COMMON ACTION MENU
        bottomNavigationView.setOnItemSelectedListener(item ->
        {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            String subCF = currentFragment.toString().substring(8, 12);
            String oF = item.toString().replace(" ", "");

            if(!oF.contains(subCF))
            {
                updatePosTrack();
                if(item.getItemId() == R.id.fragment_home)
                {
                    navController.navigate(R.id.fragment_home);
                }
                else if(item.getItemId() == R.id.fragment_search)
                {
                    navController.navigate(R.id.fragment_search);
                }
                else
                {
                    navController.navigate(R.id.fragment_my_library);
                }
                return true;
            }
            return false;
        });

        //Linking thr floating button to the settings page
        floatingActionButton = findViewById(R.id.button_settings);

        getUser();
    }

    @Override
    public void onClick(View v)
    {
        navController.navigate(R.id.go_To_Settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //STOP AUDIO TRACK EXITING THE APP
    @Override
    public void onPause()
    {
        if(viewModelBookPlayer.getPlayerGeneral() != null)
        {
            if(viewModelBookPlayer.isBookPlaying())
            {
                viewModelBookPlayer.pauseBook();
            }
            updatePosTrack();
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        getUser();
        super.onResume();
    }


    private void getUser()
    {
        if (InternetConnection.isConnected(this)) {
            final Observer<User> userObserver = newName ->
                    floatingActionButton.setOnClickListener(this);

            viewModelUser.findCurrentUserData();
            viewModelUser.getUserMutable().observe(this, userObserver);
        } else {
            Toast.makeText(HomeActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }


    public void updatePosTrack()
    {
        if(viewModelBookPlayer.getPlayerGeneral() != null)
        {
            User current = viewModelUser.getUser();
            current.editSavedPos(viewModelUser.getUser().getReadPos(), viewModelUser.getUser().getBooksRead(),
                    viewModelBookPlayer.getCurrentTrack(),
                    viewModelBookPlayer.getBookPlayer().getCurrentPosition(),
                    viewModelBookPlayer.getBookPlayer().getDuration());

            viewModelUser.updateUserData(current);
        }
    }
}


