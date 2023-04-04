//package com.example.lab5;
package com.example.lab5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TODO> elements;
    private MyListAdapter myAdapter;
    TODO todo;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        EditText editText = (EditText) findViewById(R.id.editText);
        Switch swUrgernt = findViewById(R.id.switch2);
        Button addButton = findViewById(R.id.myButton);

        elements = new ArrayList<>();

        loadDataFromDatabase();

        addButton.setOnClickListener(click -> {

            myAdapter.notifyDataSetChanged();

            todo = new TODO();

            String listItem = editText.getText().toString();
            todo.setTodoText(listItem);
            todo.setUrgent(swUrgernt.isChecked());

            //add to db & get new ID
            ContentValues newRowValues = new ContentValues();

            //Put String into the Items column
            newRowValues.put(MyOpener.COL_ITEMS, listItem);
            newRowValues.put(MyOpener.COL_URGENT, swUrgernt.isChecked());

            //insert into db
            long newID = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            //CHANGED
            //Create entry item
            //adding third parameter to constructor
            todo = new TODO(listItem, newID, swUrgernt.isChecked());
            elements.add(todo);

            editText.setText("");
            swUrgernt.setChecked(false);

            myAdapter.notifyDataSetChanged();

        });

        ListView myList = findViewById(R.id.myList);
        myList.setAdapter(myAdapter = new MyListAdapter());

        myList.setOnItemClickListener((parent, view, pos, id) -> {
//            elements.remove(pos);
//            myAdapter.notifyDataSetChanged();
        });

        myList.setOnItemLongClickListener((p, b, pos, id) -> {

            View newView = getLayoutInflater().inflate(R.layout.todo, null);
            TextView tView = newView.findViewById(R.id.textGoesHere);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            //            tView.setText(elements.get(pos).getTodoText());

            alertDialogBuilder.setTitle("Do you want to delete this?")
                    //What is the message:
                    .setMessage("The selected row is: " + pos +
                            "\n " + elements.get(pos).todoText)

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(elements.get(pos));
                        myAdapter.notifyDataSetChanged();
                    })

                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> {
                    })
                    .setView(newView)
                    //Show the dialog
                    .create().show();
            return true;
        });

    }

    private void loadDataFromDatabase() {
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

//        dbOpener.onCreate(db);

        //Step 3
        //Adding MyOpener.COL_URGENT
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String[] columns = {MyOpener.COL_ID, MyOpener.COL_ITEMS, MyOpener.COL_URGENT};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:

        int nameColIndex = results.getColumnIndex(MyOpener.COL_ITEMS);
        int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

        //Step 4
        //ADDED index for Urgent column
        int urgentColIndex = results.getColumnIndex(MyOpener.COL_URGENT);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String name = results.getString(nameColIndex);
            long id = results.getLong(idColIndex);

            //Step 5
            //get boolean value of Urgent column
            boolean urgent = (results.getInt(urgentColIndex) != 0);

            //Step 6
            //call TODO constructor with third parameter
            //add the new List Item to the array list:
            elements.add(new TODO(name, id, urgent));
        }

        //At this point, the List array has loaded every row from the cursor.
    }


    protected void updateContact(TODO c) {
        //Creating ContentValues object to represent a database row:
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MyOpener.COL_ITEMS, c.getTodoText());

        //now call the update function:
        db.update(MyOpener.TABLE_NAME, updatedValues, MyOpener.COL_ID + "= ?", new String[]{Long.toString(c.getId())});
    }

    protected void deleteContact(TODO c) {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[]{Long.toString(c.getId())});
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public TODO getItem(int position) {
            return elements.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View old, ViewGroup parent) {

            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            if (newView == null) {
                newView = inflater.inflate(R.layout.todo, parent, false);
            }

            //set what the text should be for this row:
            TextView tView = newView.findViewById(R.id.textGoesHere);
            tView.setText(getItem(position).todoText);

            if (getItem(position).isUrgent) {
                tView.setBackgroundColor(Color.RED);
                tView.setTextColor(Color.WHITE);
            } else {
                tView.setBackgroundColor(Color.WHITE);
                tView.setTextColor(Color.GRAY);
            }

            //return it to be put in the table
            return newView;
        }
    }

    class TODO {

        String todoText;
        boolean isUrgent;
        protected long id;

        public TODO() {

        }

        public TODO(String n, long i) {
            todoText = n;
            id = i;
        }

        //Step 7
        //ADDED constructor with third parameter
        public TODO(String n, long i, boolean urgent) {
            todoText = n;
            id = i;
            isUrgent = urgent;
        }


        public void update(String n) {
            todoText = n;
        }


        public String getTodoText() {
            return todoText;
        }

        public long getId() {
            return id;
        }

        public void setTodoText(String todoText) {
            this.todoText = todoText;
        }

        public boolean isUrgent() {
            return isUrgent;
        }

        public void setUrgent(boolean urgent) {
            isUrgent = urgent;
        }
    }

}

