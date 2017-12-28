package com.example.pebblesappv2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/10/22.
 */

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private ArrayList<Rewards> rewards;
    private Context context;

    public RewardsAdapter(ArrayList<Rewards> rewards, Context context) {
        this.rewards = rewards;
        this.context = context;
    }

    @Override
    public RewardsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reward_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RewardsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.reward_name.setText(rewards.get(i).getReward_name());
        Drawable status_drawable = null;
        switch (rewards.get(i).getStatus()) {
            case Rewards.pending:
                status_drawable = context.getDrawable(R.drawable.pending);
                break;
            case Rewards.targeted:
                status_drawable = context.getDrawable(R.drawable.target);
                break;
            case Rewards.redeemable:
                status_drawable = context.getDrawable(R.drawable.moneybag);
                break;
            case Rewards.redeemed:
                status_drawable = context.getDrawable(R.drawable.openbox);
                break;
        }
        viewHolder.reward_status.setImageDrawable(status_drawable);
//        runEnterAnimation(viewHolder.itemView, i);
    }

    private void runEnterAnimation(View view, int order) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        view.setTranslationX(-size.x);

        ObjectAnimator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0);
        transAnim.setInterpolator(new DecelerateInterpolator(2.5f));
        transAnim.setDuration(700);
        transAnim.setStartDelay(order*100);
        transAnim.start();
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView reward_name;
        private ImageView reward_status;
        public ViewHolder(View view) {
            super(view);

            reward_name = (TextView)view.findViewById(R.id.reward_name);
            reward_status = (ImageView)view.findViewById(R.id.reward_status);
        }
    }
}
