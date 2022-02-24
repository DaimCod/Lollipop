package com.example.lollipop.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.fragment.app.FragmentActivity;

public class InternetConnection
{
    public static boolean isConnected(FragmentActivity fragmentActivity)
    {
        ConnectivityManager cM = (ConnectivityManager) fragmentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo nI = cM.getActiveNetworkInfo();
        return nI != null && nI.isConnectedOrConnecting();
    }


}
