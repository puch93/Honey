package com.match.marryme.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class ItemOffsetDecorationJoin extends RecyclerView.ItemDecoration {
    Activity act;
    int value1, value2, value3;

    public ItemOffsetDecorationJoin(Activity act, int value1, int value2, int value3) {
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