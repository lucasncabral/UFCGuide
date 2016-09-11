package com.example.lucas.esapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.lucas.esapp.Adapter.CommentsAdapter;
import com.example.lucas.esapp.Model.Comment;
import com.example.lucas.esapp.R;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {
    private List<Comment> comments = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentsAdapter cmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        cmAdapter = new CommentsAdapter(comments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cmAdapter);

        prepareCommentData();
    }

    private void prepareCommentData() {
        Comment movie = new Comment("beleza", "massa");
        comments.add(movie);

        movie = new Comment("b0m", "geras");
        comments.add(movie);

        movie = new Comment("melior", "almoco eh em ines");
        comments.add(movie);

        movie = new Comment("mzr", "boy");
        comments.add(movie);


       cmAdapter.notifyDataSetChanged();
    }
}