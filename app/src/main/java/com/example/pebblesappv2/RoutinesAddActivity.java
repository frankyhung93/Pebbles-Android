package com.example.pebblesappv2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

/**
 * Created by ChunFaiHung on 2017/2/23.
 */

public class RoutinesAddActivity extends AppCompatActivity {
    private ImageButton add_routine_icon_butt;
    private Integer chosen_icon_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_routine);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_routine_toolbar);
        setSupportActionBar(toolbar);

        // Init actions (Dialog building, gridview building and adapter setting)
        GridView dialogGridView = (GridView) LayoutInflater.from(this).inflate(R.layout.dialog_routines_grid, null);
//        GridView dialogGridView = new GridView(this);
        final RoutinesDialogGridAdapter dialogGridAdapter = new RoutinesDialogGridAdapter(this);
        dialogGridView.setAdapter(dialogGridAdapter);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogGridView);
        builder.setTitle("Choose Routine Icon");
        final Dialog iconDialog = builder.create();

        // On Chosen the Icon, set image and set chosen Id
        dialogGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                add_routine_icon_butt.setImageResource((int)dialogGridAdapter.getItemId(position));
                chosen_icon_id = (int)dialogGridAdapter.getItemId(position);
                iconDialog.dismiss();
            }
        });

        add_routine_icon_butt = (ImageButton) findViewById(R.id.add_routine_icon_button);
        add_routine_icon_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIconDialog(iconDialog);
            }
        });

        Button add_routine_ok_butt = (Button) findViewById(R.id.add_routine_ok_button);
        Button add_routine_cancel_butt = (Button) findViewById(R.id.add_routine_cancel_button);
        add_routine_ok_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }
        });
        add_routine_cancel_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void sendResult() {
        Intent i = new Intent();
//        i.putExtra("dateInput", dateInput);
        setResult(RESULT_OK, i);
        finish();
    }

    private void showIconDialog(Dialog iconDialog) {
        iconDialog.getWindow().setLayout(900, 800); //Controlling width and height.
        iconDialog.show();
    }

}
