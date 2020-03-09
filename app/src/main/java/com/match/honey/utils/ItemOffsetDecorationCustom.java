package com.match.honey.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.match.honey.R;


public class ItemOffsetDecorationCustom extends RecyclerView.ItemDecoration {
    Activity act;
    int value;

    public ItemOffsetDecorationCustom(Activity act, int value) {
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