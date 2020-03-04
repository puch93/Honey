package com.match.marryme.adapters;

import android.view.View;

import com.match.marryme.listDatas.PopData2;

import java.util.ArrayList;

/**
 * Created by Cho on 2017-06-21.
 */
public interface RecyclerViewItemClick {
    public void onItemClick(int position, View v);

    public void onItemClick2(ArrayList<PopData2> list2);
}
