package com.match.honey.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class ItemOffsetDecorationMatisse extends RecyclerView.ItemDecoration {
    AppCompatActivity act;
    int value1;

    public ItemOffsetDecorationMatisse(AppCompatActivity act, int value1) {
        this.act = act;
        this.value1 = value1;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        if(position != 0) {
            outRect.left = value1;
        }
    }
}