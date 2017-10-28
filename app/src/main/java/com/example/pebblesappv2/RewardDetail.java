package com.example.pebblesappv2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;

public class RewardDetail extends BaseACA {

    private TextView rwdTitle = null;
    private TextView want_day_txt = null;
    private TextView desc_txt = null;
    private TextView currency_txt = null;
    private TextView diamond_txt = null;
    private int rwdId = 0;
    private ActionBar actionbar = null;
    private File imgFile = null;
    private ImageView rwd_hero = null;
    private FloatingActionButton fab = null;
    private AlertDialog addform;
    private ImageButton choosePhoto = null;
    private Button editBtn = null;
    private Button delBtn = null;
    private Bitmap bit_cover_photo = null;
    private Rewards rwd = null;
    private static final int SELECT_PICTURE = 10;
    private boolean photo_changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        // Get Intent
        Intent intent = getIntent();
        rwdId = intent.getIntExtra(getMyString(R.string.intent_extra_rewardId), 0);
        rwd = Rewards.getRewardById(realm, rwdId);
        base_reward_id = rwd.getId();

        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(rwd.getReward_name());

        // Set Hero Image
        rwd_hero = (ImageView) findViewById(R.id.rwd_hero);
        String filename = Rewards.cover_prefix + rwd.getId();
        imgFile = new File(getFilesDir() + File.separator + Rewards.cover_dir + File.separator, filename);
        if(imgFile.exists() && rwd.getPhoto_path()!=null){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            rwd_hero.setImageBitmap(myBitmap);
        }

        // Edit Button
        editBtn = (Button) findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addform.show();
                fillEditDialog(addform); // populate the Dialog with existing values
            }
        });
        delBtn = (Button) findViewById(R.id.del_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RewardDetail.this)
                        .setTitle("Delete Operation")
                        .setMessage("Do you really want to delete this reward?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Rewards.deleteReward(realm, rwdId);
                                dialog.cancel();
                                finish();
                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }}).show();
            }
        });

        // Set fab icon & btns visibility
        fab = (FloatingActionButton) findViewById(R.id.rwd_fab);
        switch (rwd.getStatus()) {
            case Rewards.pending:
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pending));
                break;
            case Rewards.targeted:
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.target));
                editBtn.setVisibility(View.GONE);
                delBtn.setVisibility(View.GONE);
                break;
            case Rewards.redeemable:
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.moneybag));
                editBtn.setVisibility(View.GONE);
                delBtn.setVisibility(View.GONE);
                break;
            case Rewards.redeemed:
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.openbox));
                editBtn.setVisibility(View.GONE);
                delBtn.setVisibility(View.GONE);
                break;
        }

        // Set up more content
        want_day_txt = (TextView) findViewById(R.id.want_day_txt);
        desc_txt = (TextView) findViewById(R.id.description_txt);
        currency_txt = (TextView) findViewById(R.id.magcurrency_txt);
        diamond_txt = (TextView) findViewById(R.id.diamond_txt);
        TextView gotit_txt = (TextView) findViewById(R.id.getday_txt);

        want_day_txt.setText(rwd.getWant_day().toString());
        desc_txt.setText(rwd.getReward_desc());
        currency_txt.setText(rwd.getMag_gold()+"");
        diamond_txt.setText(rwd.getMag_diamond()+"");
        if (rwd.getStatus() == Rewards.redeemed) {
            gotit_txt.setText(rwd.getGet_day().toString());
        } else {
            LinearLayout getday_header = (LinearLayout) findViewById(R.id.get_day_header);
            getday_header.setVisibility(View.GONE);
        }

        // Set up Edit Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogLayout = inflater.inflate(R.layout.reward_add_form, null);
        builder.setView(dialogLayout)
                // Add action buttons
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText eT_rwd_name = (EditText) addform.findViewById(R.id.eT_rewardname);
                        EditText eT_rwd_desc = (EditText) addform.findViewById(R.id.eT_rewarddesc);
                        EditText eT_rwd_gold = (EditText) addform.findViewById(R.id.eT_gold);
                        EditText eT_rwd_diamond = (EditText) addform.findViewById(R.id.eT_diamond);
                        ArrayList<String> str_arr = new ArrayList<>();
                        ArrayList<Integer> int_arr = new ArrayList<>();
                        str_arr.add(eT_rwd_name.getText().toString());
                        str_arr.add(eT_rwd_desc.getText().toString());
                        int_arr.add(Integer.parseInt(eT_rwd_gold.getText().toString()));
                        int_arr.add(Integer.parseInt(eT_rwd_diamond.getText().toString()));
                        Rewards.editReward(realm, str_arr, int_arr, rwd.getId());
                        if (photo_changed) {
                            new CopyBitmapSyncRealm(new UpdateRewardUI() {
                                @Override
                                public void updateRewardCover() {
                                    if(imgFile.exists()){
                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        rwd_hero.setImageBitmap(myBitmap);
                                    }
                                }
                            }).execute(bit_cover_photo);
                            photo_changed = false;
                        }
                        dialog.cancel();
                        updateRewardDetails();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        addform = builder.create();

        // Set onClick listener for choose_photo ImageButton
        choosePhoto = (ImageButton) dialogLayout.findViewById(R.id.choose_photo);
        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    private void updateRewardDetails() {
        realm = Realm.getDefaultInstance();
        rwd = Rewards.getRewardById(realm, rwdId);
        this.getSupportActionBar().setTitle(rwd.getReward_name()); // this fails by unknown reasons...
        desc_txt.setText(rwd.getReward_desc());
        currency_txt.setText(rwd.getMag_gold()+"");
        diamond_txt.setText(rwd.getMag_diamond()+"");
    }

    private void fillEditDialog(AlertDialog dlg) {
        EditText eT_rwd_name = (EditText) dlg.findViewById(R.id.eT_rewardname);
        EditText eT_rwd_desc = (EditText) dlg.findViewById(R.id.eT_rewarddesc);
        EditText eT_rwd_gold = (EditText) dlg.findViewById(R.id.eT_gold);
        EditText eT_rwd_diamond = (EditText) dlg.findViewById(R.id.eT_diamond);
        choosePhoto = (ImageButton) dlg.findViewById(R.id.choose_photo);

        eT_rwd_name.setText(rwd.getReward_name());
        eT_rwd_desc.setText(rwd.getReward_desc());
        eT_rwd_gold.setText(rwd.getMag_gold()+"");
        eT_rwd_diamond.setText(rwd.getMag_diamond()+"");
        String filename = Rewards.cover_prefix + rwd.getId();
        File imgFile = new File(getFilesDir() + File.separator + Rewards.cover_dir + File.separator, filename);
        if(imgFile.exists() && rwd.getPhoto_path()!=null){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), myBitmap);
            choosePhoto.setBackground(bitmapDrawable);
            choosePhoto.setImageDrawable(null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(selectedImageUri);
                    bit_cover_photo = BitmapFactory.decodeStream(is);
                    bit_cover_photo = squareCrop(bit_cover_photo);
                    if (choosePhoto != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bit_cover_photo);
                        choosePhoto.setBackground(bitmapDrawable);
                    }
                    is.close();
                    photo_changed = true;
                } catch (Exception e) {
                    Log.d("CUMON FILE", e.toString());
                }
            }
        }
    }
}
