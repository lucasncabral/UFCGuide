package com.example.lucas.esapp.Fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.esapp.Adapter.SearchAdapter;
import com.example.lucas.esapp.Interface.RecycleViewOnClickListenerHack;
import com.example.lucas.esapp.Model.MarkedPlace;
import com.example.lucas.esapp.R;

import info.hoang8f.android.segmented.SegmentedGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lucas on 07/08/2016.
 */
public class ListResultSearchFragment extends DialogFragment implements RecycleViewOnClickListenerHack{
    private static List<MarkedPlace> resultSearch;
    private static NewMapaFragment map;
    private SearchAdapter searchAdapter;
    private RecyclerView recyclerView;
    private SegmentedGroup segmented;

    public static ListResultSearchFragment newInstance(List<MarkedPlace> listSearch, NewMapaFragment mapaFragment) {
        resultSearch = listSearch;
        map = mapaFragment;

        ListResultSearchFragment fragment = new ListResultSearchFragment();
        return fragment;
    }

    public ListResultSearchFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().setTitle("Result Search");

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_result_search, container);
        TextView textSearch = (TextView) view.findViewById(R.id.text_no_search_found);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        searchAdapter = new SearchAdapter(getActivity(), resultSearch);
        searchAdapter.setRecycleViewOnCLickListenerHack(this);
        recyclerView.setAdapter(searchAdapter);

        if(resultSearch.size() == 0){
            textSearch.setText(getString(R.string.no_result));
            recyclerView.setBackground(getResources().getDrawable(R.drawable.serverout1));
        } else {
            textSearch.setText(searchAdapter.getItemCount() + " results for this search");
            recyclerView.setBackground(null);
        }

        segmented = (SegmentedGroup) view.findViewById(R.id.segmented);
        RadioButton option1 = (RadioButton) view.findViewById(R.id.button21);
        RadioButton option2 = (RadioButton) view.findViewById(R.id.button22);
        RadioButton option3 = (RadioButton) view.findViewById(R.id.button23);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy(0);
                searchAdapter.update(resultSearch);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy(1);
                searchAdapter.update(resultSearch);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy(2);
                searchAdapter.update(resultSearch);
            }
        });

        return view;
    }

    private void orderBy(final int i) {
        Collections.sort(resultSearch, new Comparator<MarkedPlace>() {
            @Override
            public int compare(MarkedPlace marker1, MarkedPlace marker2)
            {
                switch (i) {
                    case 0:
                        return  marker1.getName().compareTo(marker2.getName());
                    case 1:
                        return  marker1.getDistance().compareTo(marker2.getDistance());
                    default:
                        return  marker1.getEvaluation().compareTo(marker2.getEvaluation()) * -1;
            }
            }
        });
    }

    @Override
    public void onClickListener(View v, int position) {
        map.selectInSearch(resultSearch.get(position));
        try {
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void OnLongClickListener(View v, int position) {

    }
}
