package com.example.viz.nextagram.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.viz.nextagram.R;
import com.example.viz.nextagram.db.ArticleDTO;
import com.example.viz.nextagram.db.ProviderDao;
import com.example.viz.nextagram.network.Proxy;

import java.util.ArrayList;

/**
 * Created by chayongbin on 15. 5. 8..
 */
public class HomeController {

    private SharedPreferences sharedPreferences;
    private Context context;
    private Proxy proxy;
    private ProviderDao dao;

    public HomeController(Context context) {
        this.context = context;
        this.proxy = new Proxy(context);
        this.dao = new ProviderDao(context);

    }

    public void initSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(context.getResources().
                getString(R.string.pref_name), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.server_ip),
                context.getResources().getString(R.string.server_value));
        editor.putString(context.getResources().getString(R.string.pref_article_number), 0 + "");
        editor.commit();
    }

    public void refreshData() {
        new Thread() {
            @Override
            public void run() {
                // network 작업을 하는 Proxy는 반드시 Thread 내부에서 실행되어야 한다.
                ArrayList<ArticleDTO> articleList = proxy.getArticleDTO();
                dao.insertData(articleList);
            }
        }.start();
    }

    public void startSyncDataService() {
        Intent intentSync = new Intent("com.example.viz.nextagram.service.SyncDataService");
        context.startService(intentSync);
    }
}
