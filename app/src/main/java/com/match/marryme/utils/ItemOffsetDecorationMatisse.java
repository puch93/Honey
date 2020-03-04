package com.match.marryme.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class ItemOffsetDecorationMatisse extends RecyclerView.ItemDecoration {
    Activity act;
    int value1;

    public ItemOffsetDecorationMatisse(Activity act, int value1) {
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