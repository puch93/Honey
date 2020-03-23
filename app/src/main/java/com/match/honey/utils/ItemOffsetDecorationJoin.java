package com.match.honey.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class ItemOffsetDecorationJoin extends RecyclerView.ItemDecoration {
    AppCompatActivity act;
    int value1, value2, value3;

    public ItemOffsetDecorationJoin(AppCompatActivity act, int value1, int value2, int value3) {
        this.act = act;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);


        if (position % 3 == 0) {
            outRect.right = value2;
        } else if(position % 3 == 1) {
            outRect.left = value1;
            outRect.right = value1;
        } else if(position % 3 == 2) {
            outRect.left = value2;
        }

        if (position > 2) {
            outRect.top = value3;
        }
    }
}