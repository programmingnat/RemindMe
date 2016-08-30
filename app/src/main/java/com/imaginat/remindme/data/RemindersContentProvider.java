package com.imaginat.remindme.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.imaginat.remindme.data.source.local.ListsLocalDataSource;

/**
 * Created by nat on 8/27/16.
 */
public class RemindersContentProvider extends ContentProvider {

    private static final String AUTHORITY =
            "com.imaginat.remindme.provider.RemindersContentProvider";
    private static final String LISTS_NAMES_TABLE = "lists_of_names";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+LISTS_NAMES_TABLE);

    public static final int ALL_LISTS_NAMES=1;
    public static final int IND_LIST_NAME=2;

    private ListsLocalDataSource mListsLocalDataSource;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sURIMatcher.addURI(AUTHORITY,LISTS_NAMES_TABLE,ALL_LISTS_NAMES);
        sURIMatcher.addURI(AUTHORITY,LISTS_NAMES_TABLE+"/#",IND_LIST_NAME);
    }

    @Override
    public boolean onCreate() {
        mListsLocalDataSource=ListsLocalDataSource.getInstance(getContext());

        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        String id=null;
        switch(uriType){
            case ALL_LISTS_NAMES:
                id=mListsLocalDataSource.createNewList(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(LISTS_NAMES_TABLE+"/"+id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
