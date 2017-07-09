package com.example.pebblesappv2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class RoutinesAddActivity extends AppCompatActivity {
    private ImageButton add_routine_icon_butt;
    private ImageView add_routine_color_butt;
    private ImageView add_routine_txcolor_butt;
    private int currentBackgroundColor = 0xffffffff;
    private Integer chosen_icon_id;

    @SuppressWarnings("InflateParams")
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
                add_routine_icon_butt.setScaleType(ImageView.ScaleType.FIT_XY);
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

        add_routine_color_butt = (ImageView) findViewById(R.id.add_routine_color_button);
        add_routine_color_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(RoutinesAddActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentBackgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Log.d("DEBUG", Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                changeBackgroundColor(selectedColor, "bg");
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        add_routine_txcolor_butt = (ImageView) findViewById(R.id.add_routine_txcolor_button);
        add_routine_txcolor_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(RoutinesAddActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentBackgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Log.d("DEBUG", Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                changeBackgroundColor(selectedColor, "tx");
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

    }

    public void sendResult() {
        TextView add_routine_icon_name = (TextView) findViewById(R.id.add_routine_name_input);
        add_routine_color_butt = (ImageView) findViewById(R.id.add_routine_color_button);
        add_routine_txcolor_butt = (ImageView) findViewById(R.id.add_routine_txcolor_button);
        ColorDrawable color_bg = (ColorDrawable) add_routine_color_butt.getBackground();
        ColorDrawable color_tx = (ColorDrawable) add_routine_txcolor_butt.getBackground();

        Intent i = new Intent();
//        i.putExtra("dateInput", dateInput);
        i.putExtra("icon_id_input", (long)chosen_icon_id);
        i.putExtra("icon_name_input", add_routine_icon_name.getText().toString());
        i.putExtra("icon_bg_color", (long)color_bg.getColor());
        i.putExtra("icon_tx_color", (long)color_tx.getColor());
        setResult(RESULT_OK, i);
        finish();
    }

    private void showIconDialog(Dialog iconDialog) {
        iconDialog.show();
        iconDialog.getWindow().setLayout(1100, 1000); //Controlling width and height.
    }

    private void changeBackgroundColor(int selectedColor, String type) {
        switch (type) {
            case "bg":
                add_routine_color_butt.setBackgroundColor(selectedColor);
                break;
            case "tx":
                add_routine_txcolor_butt.setBackgroundColor(selectedColor);
                break;
            default:
                break;
        }
    }
}
