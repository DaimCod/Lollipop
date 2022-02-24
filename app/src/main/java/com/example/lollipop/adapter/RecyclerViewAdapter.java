package com.example.lollipop.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    //CONTROLLO A NULL SE LIKED

    private ArrayList<String> bookNames, bookCover, bookDuration;
    private Context mContext;
    private OnItemClickListener listener;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> bookNames, ArrayList<String> bookCover, ArrayList<String> duration,
                               OnItemClickListener listener)
    {
        this.bookNames = bookNames;
        this.bookCover = bookCover;
        this.bookDuration = duration;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Picasso.get()
                .load(bookCover.get(position))
                /*.resize(150, 250)*/
                .into(holder.bookImage);

        holder.bookText.setText(bookNames.get(position));
        holder.bind(position, listener);

        if(bookDuration != null)
        {
            String [] help = bookDuration.get(position + 1).split(Constants.SPLITTER_DURATION);

            if(help[1].equalsIgnoreCase("0"))
            {
                holder.bookProgress.setMax(100);
            }
            else
            {
                holder.bookProgress.setMax(Integer.parseInt(help[1]));

                if(Integer.parseInt(help[1]) <= Integer.parseInt(help[0]) + Constants.ROUNDED_UP)
                {
                    Drawable progressColor = holder.bookProgress.getProgressDrawable().mutate();
                    progressColor.setColorFilter(
                            ContextCompat.getColor(mContext, R.color.DarkGreen),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.bookProgress.setProgressDrawable(progressColor);
                }
            }
            holder.bookProgress.setProgress(Integer.parseInt(help[0]));
            holder.bookProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        return bookCover.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView bookImage;
        TextView bookText;
        ProgressBar bookProgress;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            bookImage = itemView.findViewById(R.id.imageRecycler);
            bookText = itemView.findViewById(R.id.bookRecycler);
            bookProgress = itemView.findViewById(R.id.position);
        }

        public void bind(final int item, final OnItemClickListener listener)
        {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
