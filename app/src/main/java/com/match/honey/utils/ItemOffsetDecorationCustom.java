package com.match.honey.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class ItemOffsetDecorationCustom extends RecyclerView.ItemDecoration {
    AppCompatActivity act;
    int value;

    public ItemOffsetDecorationCustom(AppCompatActivity act, int value) {
        this.act = act;
        this.value = value;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        outRect.right = value;
        outRect.left = value;
        outRect.bottom = value;

        if (position == 0) {
            outRect.top = value;
        }
    }
}