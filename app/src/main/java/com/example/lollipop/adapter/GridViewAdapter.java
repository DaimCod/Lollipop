package com.example.lollipop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.lollipop.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;;

public class GridViewAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> book_name;
    private  ArrayList<String>book_cover;

    LayoutInflater layoutInflater;

    public GridViewAdapter(Context c, ArrayList<String> ti, ArrayList<String> cov)
    {
        context = c;
        book_name = ti;
        book_cover = cov;
    }

    @Override
    public int getCount()
    {
        return book_name.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(layoutInflater == null)
        {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView imageView = convertView.findViewById(R.id.grid_image);
        TextView textView = convertView.findViewById(R.id.grid_text);

        Picasso.get()
                .load(book_cover.get(position))
                .resize(150, 250)
                .into(imageView);

        textView.setText(book_name.get(position));

        return convertView;
    }


}
