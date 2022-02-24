package com.example.lollipop.Utils;

import java.util.ArrayList;

public interface Callback
{
    void onCallBack(ArrayList<String> names);

    void onError(String error);
}
