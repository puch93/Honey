package com.match.honey.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.match.honey.R;


public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
    AppCompatActivity act;

    public ItemOffsetDecoration(AppCompatActivity act) {
        this.act = act;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

//        outRect.right = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
//        outRect.left = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
        outRect.bottom = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);

        if (position == 0) {
            outRect.top = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
        }
    }
}