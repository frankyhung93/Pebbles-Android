package com.example.pebblesappv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class TdlActivity extends AppCompatActivity {

//    final static int GET_RESULT_TEXT = 0;
    public ArrayList<ToDoItem> tdl_data = new ArrayList<ToDoItem>();
    public PebblesTDLSource tdl_source;
    public TdlArrayAdapter tdl_adapter;
    private int update_position;
    private long update_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdl);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tdl_data.clear();
        // Opening sql connection and helper
        tdl_source = new PebblesTDLSource(this);
        tdl_source.open();
        // Initializing List Data
        tdl_data = tdl_source.getTDL();

        // Initializing custom adapter
        tdl_adapter = new TdlArrayAdapter(this, tdl_data);

        ListView tdl_listView = (ListView) findViewById(R.id.tdl_listview);
//        tdl_listView.setEmptyView(findViewById(R.id.empty_view_tdl_text));

        // Set adapter for the listView
        tdl_listView.setAdapter(tdl_adapter);
        tdl_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToDoItem orgItem = tdl_data.get(position);
                update_position = position;
                update_id = orgItem.getId();
                Intent updateI = new Intent(TdlActivity.this, TdlUpdateActivity.class);
                updateI.putExtra("Org_Date", orgItem.getToDoDate());
                updateI.putExtra("Org_Time", orgItem.getToDoTime());
                updateI.putExtra("Org_Desc", orgItem.getToDoDesc());
//                updateI.putExtra("Org_Id", orgItem.getId());
                startActivityForResult(updateI,1);
            }
        });

        tdl_adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        tdl_source.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tdl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_td) {
            startActivityForResult(
                    new Intent(TdlActivity.this, TdlAddNewActivity.class),
                    0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle the result once the activity returns a result, display contact
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                ToDoItem newTDItem = tdl_source.CreateTDItem(data.getStringExtra("descInput"),
                                        data.getStringExtra("timeInput"),
                                        data.getStringExtra("dateInput"));
                tdl_data.add(newTDItem);
                tdl_adapter.closeAllItems();
                tdl_adapter.notifyDataSetChanged();
                Log.d("DEBUG", newTDItem.getToDoDesc());
            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ToDoItem updatedItem = tdl_source.UpdateTDItem(
                        data.getStringExtra("descInput"),
                        data.getStringExtra("timeInput"),
                        data.getStringExtra("dateInput"),
                        update_id);
                tdl_data.set(update_position, updatedItem);
                tdl_adapter.closeAllItems();
                tdl_adapter.notifyDataSetChanged();
                Log.d("DEBUG", updatedItem.getToDoDesc());
            }
        }
    }

}
