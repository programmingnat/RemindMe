package com.imaginat.remindme.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.imaginat.remindme.data.ITaskItem;
import com.imaginat.remindme.data.ReminderList;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by nat on 8/8/16.
 */
public class RemindMeSQLHelper extends SQLiteOpenHelper {

    private static final String TAG = RemindMeSQLHelper.class.getSimpleName();
    private static final int VERSION=1;
    private static final String DATABASE_NAME="remindMe.db";
    private static RemindMeSQLHelper mInstance;

    private RemindMeSQLHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    public static RemindMeSQLHelper getInstance(Context c){
        if(mInstance==null){
            mInstance = new RemindMeSQLHelper(c);
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //list of lists
        db.execSQL(DBSchema.lists_table.createCommand);
        //create the lists of tasks
        db.execSQL(DBSchema.reminders_table.createCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {


    }


    ////////////////LIST OF LISTS/////////////
    Observable<List<ReminderList>>getAllLists(Callable<List<ReminderList>> func){
        return makeObservable(func);
    }
    ///////////////TASKS FROM TABLES//////////////
    Observable<List<ITaskItem>>getAllTasks(ListsLocalDataSource.GetTasks_Callable func){
        return makeObservable((Callable)func);
    }


    ///HELPER METHOD////////////////////////////////////////////////////////
    private static <T> Observable<T> makeObservable(final Callable<T> func){
        return Observable.create(
                new Observable.OnSubscribe<T>(){

                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        try{
                            subscriber.onNext(func.call());
                        }catch(Exception ex){
                            subscriber.onError(ex);
                        }
                    }
                }
        );
    }
}
