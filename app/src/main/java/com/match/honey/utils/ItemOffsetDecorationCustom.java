package com.match.honey.utils;

import android.app.Activity;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


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