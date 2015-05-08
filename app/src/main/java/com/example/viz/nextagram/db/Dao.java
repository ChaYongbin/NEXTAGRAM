//package com.example.viz.nextagram;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
//* Created by viz on 2015. 3. 16..
//*/
//public class Dao {
//    private Context context;
//    private SQLiteDatabase database;
//    private SharedPreferences pref;
//
//    public Dao(Context context) {
//        this.context = context;
//
//        database = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
//
//        try {
//            String sql = "CREATE TABLE IF NOT EXISTS Articles (ID integer primary key autoincrement," +
//                    "ArticleNumber integer UNIQUE not null," +
//                    "Title text not null," +
//                    "WriterName text not null," +
//                    "WriterID text not null," +
//                    "Content text not null," +
//                    "WriteDate text not null," +
//                    "ImgName text not null);";
//            database.execSQL(sql);
//        } catch (Exception e) {
//            Log.e("test", "CREATE TABLE FAILED! - " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public void insertJsonData(String jsonData) {
//        int articleNumber;
//        String title;
//        String writer;
//        String id;
//        String content;
//        String writeDate;
//        String imgName;
//
//        FileDownloader fileDownloader = new FileDownloader(context);
//
//        try {
//            JSONArray jArr = new JSONArray(jsonData);
//            Log.e("length is : ", String.valueOf(jArr.length()));
//
//            for (int i = 0; i < jArr.length(); i++) {
//                JSONObject jObj = jArr.getJSONObject(i);
//
//                articleNumber = jObj.getInt("ArticleNumber");
//                title = jObj.getString("Title");
//                writer = jObj.getString("Writer");
//                id = jObj.getString("Id");
//                content = jObj.getString("Content");
//                writeDate = jObj.getString("WriteDate");
//                imgName = jObj.getString("ImgName");
//
//                if (i == jArr.length() - 1) {
//                    String prefName = context.getResources().getString(R.string.pref_name);
//                    pref = context.getSharedPreferences(prefName,
//                            context.MODE_PRIVATE);
//                    String prefArticleNumberKey = context.getResources()
//                            .getString(R.string.pref_article_number);
//
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putString(prefArticleNumberKey, "" + articleNumber);
//                    editor.commit();
//                }
//
//                Log.i("test", "ArticleNumber: " + articleNumber + " Title: " + title);
//
//                String sql = "INSERT OR REPLACE INTO Articles(ArticleNumber, Title, WriterName, WriterID,Content, WriteDate, ImgName)" + " VALUES(" + articleNumber + ",'" +
//                        title + "','" + writer + "','" + id + "','" + content + "','" + writeDate + "','" + imgName + "');";
//
//                try {
//                    database.execSQL(sql);
//                    Log.i("insertJSonData: ", "success");
//                } catch (Exception e) {
//                    Log.i("insertJSonData: ", "fail");
//                    e.printStackTrace();
//                }
//                fileDownloader.downFile("http://192.168.56.1:5010" + "/image/" + imgName, imgName); // 확장자 써줘야 하는거 아닌가?
//            }
//
//        } catch (JSONException e) {
//            Log.e("test", "JSON ERROR! - " + e);
//            e.printStackTrace();
//        }
//    }
//
//    public ArrayList<ArticleDTO> getArticleList() {
//
//        ArrayList<ArticleDTO> articleDTOList = new ArrayList<ArticleDTO>();
//
//        int articleNumber;
//        String title;
//        String writer;
//        String id;
//        String content;
//        String writeDate;
//        String imgName;
//
//        String sql = "SELECT * FROM Articles;";
//        Cursor cursor = database.rawQuery(sql, null);
//
//        while (cursor.moveToNext()) {
//            articleNumber = cursor.getInt(1);
//            title = cursor.getString(2);
//            writer = cursor.getString(3);
//            id = cursor.getString(4);
//            content = cursor.getString(5);
//            writeDate = cursor.getString(6);
//            imgName = cursor.getString(7);
//
//            ArticleDTO articleDTO = new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName);
//            articleDTOList.add(articleDTO);
//        }
//
//        cursor.close();
//
//        return articleDTOList;
//    }
//
//    public ArticleDTO getArticleByArticleNumber(int articleNumber) {
//        ArticleDTO articleDTO = null;
//
//        String title;
//        String writer;
//        String id;
//        String content;
//        String writeDate;
//        String imgName;
//
//        String sql = "SELECT * FROM Articles WHERE ArticleNumber = " + articleNumber + ";";
//        Cursor cursor = database.rawQuery(sql, null);
//
//        cursor.moveToNext();
//        articleNumber = cursor.getInt(1);
//        title = cursor.getString(2);
//        writer = cursor.getString(3);
//        id = cursor.getString(4);
//        content = cursor.getString(5);
//        writeDate = cursor.getString(6);
//        imgName = cursor.getString(7);
//
//        articleDTO = new ArticleDTO(articleNumber, title, writer, id, content, writeDate, imgName);
//
//        cursor.close();
//        return articleDTO;
//    }
//}
