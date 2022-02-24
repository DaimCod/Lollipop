package com.example.lollipop.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.users.User;
import com.example.lollipop.viewmodels.ViewModelUser;

public class PreferencesFragmentSettings extends PreferenceFragmentCompat
{
    private EditTextPreference nameText;
    private EditTextPreference surnameText;
    private EditTextPreference gitLink;
    private ViewModelUser viewModelUser;
    private User user;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        getActivity().findViewById(R.id.button_settings).setVisibility(View.GONE);

        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        user = viewModelUser.getUser();

        Log.d(Constants.LOG_TEXT, viewModelUser.getUser().getName());

        nameText = findPreference("name_key");
        assert nameText != null;
        nameText.setSummary(user.getName());

        surnameText = findPreference("surname_key");
        assert surnameText != null;
        surnameText.setSummary(user.getSurname());

        gitLink = findPreference("link_key");
        gitLink.setText("https://github.com/AK-JasonTodd/Lollipop");

        nameText.setOnPreferenceChangeListener((preference, newValue) ->
        {
            if(!newValue.toString().equalsIgnoreCase(user.getName()))
            {
                user.setName(newValue.toString());
                dataChanged("name");
            }
            return false;
        });

        surnameText.setOnPreferenceChangeListener((preference, newValue) ->
        {
            if(!newValue.toString().equalsIgnoreCase(user.getSurname()))
            {
              user.setSurname(newValue.toString());
              dataChanged("surname");
            }
            return false;
        });

    }

    private void dataChanged(String type)
    {
        final Observer<User> observeUser = userCurrent ->
        {
            user = userCurrent;

            if(type.equalsIgnoreCase("surname"))
            {
                surnameText.setSummary(user.getSurname());
            }
            else
            {
                nameText.setSummary(user.getName());
            }
        };
        viewModelUser.updateUserData(user);
        viewModelUser.getUserMutable().observe(getViewLifecycleOwner(), observeUser);
    }
}
