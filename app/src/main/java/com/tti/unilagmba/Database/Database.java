package com.tti.unilagmba.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.tti.unilagmba.Model.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scarecrow on 4/13/2018.
 */

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "UslaDB.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Todo> getTodo(String todoDay)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"TodoDay", "TodoName", "TodoVenue", "TodoStart", "TodoEnd"};
        String sqlTable = "DailyTask";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, "TodoDay=?", new String[]{todoDay}, null, null, null);

        final List<Todo> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new Todo(
                        c.getString(c.getColumnIndex("TodoDay")),
                        c.getString(c.getColumnIndex("TodoName")),
                        c.getString(c.getColumnIndex("TodoVenue")),
                        c.getString(c.getColumnIndex("TodoStart")),
                        c.getString(c.getColumnIndex("TodoEnd"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToTodo(Todo todo){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO DailyTask(TodoDay, TodoName, TodoVenue, TodoStart, TodoEnd) VALUES('%s', '%s', '%s', '%s', '%s');",
                todo.getDay(),
                todo.getCourseCode(),
                todo.getVenue(),
                todo.getStart(),
                todo.getStop());
        db.execSQL(query);
    }

    public void cleanTodo(String todoDay){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM DailyTask WHERE TodoDay = '%s'", todoDay);
        db.execSQL(query);
    }
}