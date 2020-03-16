package com.match.honey.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.match.honey.R;


public class ItemOffsetDecorationBlock extends RecyclerView.ItemDecoration {
    Activity act;

    public ItemOffsetDecorationBlock(Activity act) {
        this.act = act;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

//        outRect.right = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
//        outRect.left = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
        outRect.bottom = act.getResources().getDimensionPixelSize(R.dimen.dimen_8);
    }
}