package com.example.viz.nextagram;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.security.Provider;
import java.util.ArrayList;


public class HomeView extends Activity implements AdapterView.OnItemClickListener, OnClickListener {

    private final String TAG = HomeView.class.getSimpleName();
    private static AsyncHttpClient client = new AsyncHttpClient();
    private Button writeButton;
    private Button refreshButton;
    private ArrayList<ArticleDTO> articleDTOList;
    // SharedPreferences
    private SharedPreferences pref;
    private ProviderDao dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(getResources().getString(R.string.pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(getResources().getString(R.string.server_ip),
                getResources().getString(R.string.server_value));

        writeButton = (Button) findViewById(R.id.button1);
        refreshButton = (Button) findViewById(R.id.button2);
        writeButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        Intent intentSync = new Intent("com.example.viz.nextagram.SyncDataService");
        startService(intentSync);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 서버에서 데이터를 가져와서 db에 넣는 부분
        refreshData();
        // db로부터 게시판 글을 가져와서 리스트에 넣는 부분
//        listView(dao.getArticleList()); // refreshData()에서 서버에서 json과 이미지 파일 가져오는 작업이 비동기기 때문에 이게 먼저 실행되기 때문에 처음 화면에 화면에 아무것도 안 보임
    }

    private void refreshData() {
        client.get("http://192.168.56.1:5010/loadData/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String jsonData = new String(bytes);
                Log.i("getJSonData", "success: " + jsonData);
                ProviderDao dao = new ProviderDao(getApplicationContext());
                dao.insertJsonData(jsonData);
                listView(dao.getArticleList());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("getJSonData: ", "fail: " + throwable.getMessage());
            }
        });

    }

    private void listView(ArrayList<ArticleDTO> arrayList) {
        ListView listView = (ListView) findViewById(R.id.customlist_listview);
        dao = new ProviderDao(getApplicationContext());
        articleDTOList = dao.getArticleList();

        Cursor mCursor = getContentResolver().query(
                NextagramContract.Articles.CONTENT_URI,
                NextagramContract.Articles.PROJECTION_ALL, null, null,
                NextagramContract.Articles._ID + " asc"
        );
        Log.i(TAG, mCursor.toString());
        HomeViewAdapter customAdapter = new HomeViewAdapter(this, mCursor, R.layout.custom_list_row);

        listView.setAdapter(customAdapter);
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
                refreshData();
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

