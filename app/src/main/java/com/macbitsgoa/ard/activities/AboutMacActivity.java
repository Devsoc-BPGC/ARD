package com.macbitsgoa.ard.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.adapters.AboutMacAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutMacActivity extends BaseActivity {

    @BindView(R.id.rv_activity_about_mac)
    RecyclerView rv;

    @BindView(R.id.toolbar_activity_about_mac)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mac);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AboutMacAdapter(this));
    }
}
