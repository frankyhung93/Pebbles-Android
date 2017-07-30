package com.example.pebblesappv2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoutinesActivity extends BaseACA {
    public ArrayList<RoutineItem> routine_data = new ArrayList<>();
    public PebblesTDLSource rt_source;
    public RoutinesGridAdapter rt_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routines);

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetch grid data
        routine_data.clear();
        rt_source = new PebblesTDLSource(this);
        rt_source.open();
        routine_data = rt_source.getRoutines();

        GridView gridview = (GridView) findViewById(R.id.routines_gridview);
        rt_adapter = new RoutinesGridAdapter(this, routine_data);
        gridview.setAdapter(rt_adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("DEBUG", "THIS ICON ID = "+routine_data.get(i).getRtId());
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                AlertDialog.Builder builder = new AlertDialog.Builder(RoutinesActivity.this);
                builder.setMessage("Delete this Routine Item?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                long itemId = rt_adapter.getItemId(position);
                                rt_source.DeleteRoutineItem(itemId);
                                routine_data.remove(position);
                                rt_adapter.notifyDataSetChanged();
                                Log.d("DEBUG", "ITEM "+itemId+" is DELETED.");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(RoutinesActivity.this, "Deletion cancelled...", Toast.LENGTH_SHORT).show();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();

                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        rt_source.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.routines_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_routine) {
            startActivityForResult(
                    new Intent(RoutinesActivity.this, RoutinesAddActivity.class),
                    0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.d("DEBUG", data.getLongExtra("icon_id_input",-1) +" - "+data.getStringExtra("icon_name_input"));
                RoutineItem new_rtItem = rt_source.CreateRoutineItem(data.getLongExtra("icon_id_input", -1),
                                                                    data.getStringExtra("icon_name_input"),
                                                                    data.getLongExtra("icon_bg_color", -1),
                                                                    data.getLongExtra("icon_tx_color", -1));
                if (new_rtItem != null) {
                    routine_data.add(new_rtItem);
                    rt_adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
