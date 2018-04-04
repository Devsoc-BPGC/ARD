package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AboutMacAdapter;

public class AboutMacActivity extends BaseActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mac);
        RecyclerView rv = findViewById(R.id.rv_about_mac);
        toolbar = findViewById(R.id.toolbar_activity_about_mac);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AboutMacAdapter(this));
    }
}
