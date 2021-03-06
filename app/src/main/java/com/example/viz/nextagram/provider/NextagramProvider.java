package com.example.viz.nextagram.provider;

import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class NextagramProvider extends ContentProvider {
    private static final String TAG = NextagramProvider.class.getSimpleName();
    private SQLiteDatabase database;
    private final String TABLE_NAME = "Articles";
    private Context context;
    private static final int ARTICLE_LIST = 1;
    private static final int ARTICLE_ID = 2;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(NextagramContract.AUTHORITY, "Articles", ARTICLE_LIST);
        URI_MATCHER.addURI(NextagramContract.AUTHORITY, "Articles/#", ARTICLE_ID);
    }

    public NextagramProvider() {
    }

    private void sqLiteInitialize() {
        database = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        database.setLocale(Locale.getDefault());
        database.setVersion(1);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(_id integer primary key autoincrement," + "Title text not null," + "Writer text not null,"
                + "Id text not null," + "Content text not null," + "WriteDate text not null," + "ImgName text UNIQUE not null);";

        database.execSQL(sql);
    }

    private boolean isTableExist() {
        return true;
    }

    @Override
    public boolean onCreate() {
        this.context = getContext();
        sqLiteInitialize();
        createTable();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case ARTICLE_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = NextagramContract.Articles.SORT_ORDER_DEFAULT;
                }
                break;
            case ARTICLE_ID:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = NextagramContract.Articles.SORT_ORDER_DEFAULT;
                }
                if (selection == null) {
                    selection = "_ID = ?";
                    selectionArgs = new String[] { uri.getLastPathSegment() };
                }
                break;
            default:
                throw new IllegalArgumentException("[Query]Insertion을 지원하지 않는 URI입니다: " + uri);
        }

        Cursor cursor = database.query(TABLE_NAME, NextagramContract.Articles.PROJECTION_ALL, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) != ARTICLE_LIST) {
            throw new IllegalArgumentException("[Insert]Insertion을 지원하지 않는 URI입니다: " + uri);
        }

        // ARTICLE_LIST에 대한 URI 요청 시,
        if (URI_MATCHER.match(uri) == ARTICLE_LIST) {
            // database에 insert 후 해당 ID를 리턴 받음.
            if(isDBLatest(values)) return null;
            if(isAlreadyExist(values)) return null;

            long id = database.insert("Articles", null, values);

            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);

            return itemUri;
        }

        return null;
    }

    private boolean isAlreadyExist(ContentValues values) {
        String sql = "SELECT * FROM Articles WHERE _id = " + values.getAsString("_id");
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor.getCount() == 1) {
            return true;
        }

        return false;
    }

    private boolean isDBLatest(ContentValues values) {
        String sql = "SELECT * FROM Articles";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() == Integer.parseInt(values.get("_id").toString())) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
