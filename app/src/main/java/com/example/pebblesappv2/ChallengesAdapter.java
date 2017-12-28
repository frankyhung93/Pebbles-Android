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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ChunFaiHung on 2017/10/28.
 */

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> {

    private ArrayList<Challenges> challenges;
    private Context context;

    public ChallengesAdapter(ArrayList<Challenges> challenges, Context context) {
        this.challenges = challenges;
        this.context = context;
    }

    @Override
    public ChallengesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.challenge_card, viewGroup, false);
        return new ChallengesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengesAdapter.ViewHolder viewHolder, int i) {

        viewHolder.challenge_name.setText(challenges.get(i).getCha_name());
        viewHolder.challenge_endsin.setText("Ends in: " + challenges.get(i).findChallengeEndsIn());
        Drawable status_drawable = null;
        switch (challenges.get(i).getStatus()) {
            case Challenges.pending:
                status_drawable = context.getDrawable(R.drawable.pending);
                break;
            case Challenges.in_progress:
                status_drawable = context.getDrawable(R.drawable.progress);
                break;
            case Challenges.completed:
                status_drawable = context.getDrawable(R.drawable.completed);
                break;
            case Challenges.failed:
                status_drawable = context.getDrawable(R.drawable.fail);
                break;
        }
        viewHolder.challenge_status.setImageDrawable(status_drawable);
        if (challenges.get(i).getIs_recurrent() == 1) {
            Helper helper = new Helper();
            viewHolder.challenge_name.append(" "+helper.getEmojiByUnicode(helper.emoji_cyclone));
        }

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
        return challenges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView challenge_name;
        private TextView challenge_endsin;
        private ImageView challenge_status;
        public ViewHolder(View view) {
            super(view);

            challenge_name = (TextView)view.findViewById(R.id.challenge_name);
            challenge_endsin = (TextView)view.findViewById(R.id.endsin);
            challenge_status = (ImageView)view.findViewById(R.id.challenge_status);
        }
    }
}
