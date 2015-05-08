package com.example.viz.nextagram.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.viz.nextagram.R;
import com.example.viz.nextagram.controller.HomeController;
import com.example.viz.nextagram.db.ArticleDTO;
import com.example.viz.nextagram.db.ProviderDao;
import com.example.viz.nextagram.provider.NextagramContract;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;


public class HomeView extends Activity implements AdapterView.OnItemClickListener, OnClickListener {

    private final String TAG = HomeView.class.getSimpleName();
    private ArrayList<ArticleDTO> articleDTOList;

    // HomeController
    HomeController homeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Home Controller 에게 인스턴스를 할당한다.
        homeController = new HomeController(getApplicationContext());
        homeController.initSharedPreferences();
        homeController.startSyncDataService();

        setListView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        homeController.refreshData();
    }


    private void setListView() {
        // CustomAdapter 적용
        ListView listView = (ListView) findViewById(R.id.customlist_listview);

        Cursor mCursor = getContentResolver().query(
                NextagramContract.Articles.CONTENT_URI,
                NextagramContract.Articles.PROJECTION_ALL, null, null,
                NextagramContract.Articles._ID + " asc"
        );
        Log.i(TAG, mCursor.toString());
        HomeViewAdapter customAdapter = new HomeViewAdapter(this, mCursor, R.layout.custom_list_row);

        listView.setAdapter(customAdapter);
        // listView에 ClickListener 설정
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button1:
                Intent intentWrite = new Intent(".WritingArticle");
                startActivity(intentWrite);
                break;
            case R.id.button2:
                homeController.refreshData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 수정 전 코드
        // Intent intentView = new Intent(view.getContext(), ArticleView.class);
        Intent intent = new Intent(".ArticleView");
        intent.putExtra("ArticleNumber", articleDTOList.get(position).getArticleNumber()+"");
        startActivity(intent);
    }
}

