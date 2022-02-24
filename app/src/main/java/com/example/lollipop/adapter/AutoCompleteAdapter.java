package com.example.lollipop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.lollipop.R;
import com.example.lollipop.Utils.FilterListeners;
import com.example.lollipop.itemList.ItemList;
import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<ItemList>
{
    private IOnItemListener listener = null;
    private List<ItemList> tempItems;
    private List<ItemList> suggestions;
    private FilterListeners filterListeners;

    public AutoCompleteAdapter(@NonNull Context context, @NonNull List<ItemList>items)
    {
        super(context, 0, items);

        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {

        if(convertView == null)
        {
           convertView = LayoutInflater.from(getContext()).inflate(
                   R.layout.custom_item_list, parent, false);
        }
        TextView textName = convertView.findViewById(R.id.field);
        ImageView imageView = convertView.findViewById(R.id.deleteItem);

        ItemList itemList = getItem(position);

        if(itemList != null)
        {
            textName.setText(itemList.getSearched());
            textName.setOnClickListener(v -> listener.onClick(textName.getText().toString(), false));

            imageView.setImageResource(itemList.getIdImage());
            imageView.setOnClickListener(v -> listener.onClick(textName.getText().toString(), true));
        }
        return convertView;
    }

    public void setListener(IOnItemListener listener)
    {
        this.listener = listener;
    }

    public interface IOnItemListener
    {
        View.OnClickListener onClick(String text, boolean what);
    }

    public void setFilterListeners(FilterListeners filterFinishedListener)
    {
        filterListeners = filterFinishedListener;
    }

    @Override
    public Filter getFilter()
    {
        return nameFilter;
    }

    Filter nameFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            if(constraint != null)
            {
                suggestions.clear();

                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ItemList el : tempItems)
                {
                    if (el.getSearched() != null && el.getSearched().toLowerCase().contains(filterPattern))
                    {
                        suggestions.add(el);
                    }
                }
            }
            else
            {
                suggestions.addAll(tempItems);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = suggestions;
            filterResults.count = suggestions.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            if(filterListeners != null && results != null)
                filterListeners.filteringFinished(results.count);

            if(results != null && results.count > 0)
            {
                clear();
                addAll((List) results.values);
                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue)
        {
            return ((ItemList) resultValue).getSearched();
        }
    };
}
