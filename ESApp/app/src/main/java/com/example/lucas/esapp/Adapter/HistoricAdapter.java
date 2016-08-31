package com.example.lucas.esapp.Adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.esapp.Data.MySQLiteOpenHelper;
import com.example.lucas.esapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistoricAdapter extends BaseAdapter implements Filterable {

    private MySQLiteOpenHelper bd;
    private final String user;
    private ArrayList<String> data;
    private String[] typeAheadData;
    private LayoutInflater inflater;
    private boolean queryChange = false;

    public HistoricAdapter(Activity activity, String user, MySQLiteOpenHelper bd) {
        inflater = LayoutInflater.from(activity);
        this.bd = bd;
        data = new ArrayList<>();
        this.user = user;
        typeAheadData = bd.recuperarbuscas(user);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<String> searchData = new ArrayList<>();
                if (!TextUtils.isEmpty(constraint)) {
                    // Retrieve the autocomplete results.
                    int i = 0;
                    int numeroDeBuscas = 5;
                    for (String str : typeAheadData) {
                        if (str.toLowerCase().startsWith(constraint.toString().toLowerCase()) && i < numeroDeBuscas) {
                            searchData.add(str);
                            i++;
                        }
                    }

                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                } else {
                    searchData.add("Xerox");
                    searchData.add("Lanchonete");
                    searchData.add("Bloco");
                    searchData.add("Coordenação");

                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null && queryChange) {
                    data = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
                queryChange = true;
            }
        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_historic, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        String currentListData = (String) getItem(position);
        mViewHolder.textView.setText(currentListData);
        Log.d("testeein", currentListData);
        boolean test1 = currentListData.equals("Xerox") || currentListData.equals("Lanchonete");
        boolean test2 = currentListData.equals("Bloco") || currentListData.equals("Coordenação");

        if(test1 || test2)
            mViewHolder.icon.setImageResource(R.drawable.ic_room_black_18dp);
        else
            mViewHolder.icon.setImageResource(R.drawable.ic_action);

        return convertView;
    }

    public void zerarLista () {
        queryChange = false;
        data.clear();
        notifyDataSetChanged();
    }

    public void update () {
        typeAheadData = bd.recuperarbuscas(user);
        notifyDataSetChanged();
    }



    private class MyViewHolder {
        private TextView textView;
        private ImageView icon;

        public MyViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.text1);
            icon = (ImageView) convertView.findViewById(R.id.icon);
        }
    }

}
