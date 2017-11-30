package com.ssmirnov.myzeroapps;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    public static final String EXTRA_MESSAGE = "com.example.myzeroapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        String message = pref.getString(getString(R.string.saved_message), "");
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText(message);

    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.saved_message), message);
        editor.commit();

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    public void saveToFile(View view) {
        EditText fileNameField = (EditText) findViewById(R.id.editText);
        String fileName = fileNameField.getText().toString();
        EditText editText = (EditText) findViewById(R.id.textToFile);
        String fileContent = editText.getText().toString();

        if(fileName != null && !fileName.isEmpty()){

            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(fileContent.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void saveToDatabase(){

        FeedReaderDbHelper dbhlp = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbhlp.getWritableDatabase();
        ContentValues cvls = new ContentValues();

        cvls.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,"title");
        cvls.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,"subtitle");

        db.insert(FeedReaderContract.FeedEntry.TABLE_NAME,null,cvls);

    }

    public void readFromDataBase(){

        FeedReaderDbHelper dbhlp = new FeedReaderDbHelper(this);
        SQLiteDatabase db = dbhlp.getReadableDatabase();

        String[] projection = {
           FeedReaderContract.FeedEntry._ID,
           FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
           FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,
        };

        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArg = {"My Title"};

        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor curs = db.query(

                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArg,
                null,
                null,
                sortOrder
        );

        List<Long> itemIds = new ArrayList();
        while(curs.moveToNext()){
            Long itemId = curs.getLong(curs.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            itemIds.add(itemId);
        }
        curs.close();

    }
}
