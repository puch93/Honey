package com.match.honey.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.match.honey.termFrag.TermSub01;
import com.match.honey.termFrag.TermSub02;
import com.match.honey.termFrag.TermSub03;


public class TermsPageAdapter extends FragmentStatePagerAdapter{

    public static final int PAGE_CNT = 3;

    public TermsPageAdapter(FragmentManager fm){super(fm);}

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return TermSub01.newInstance();
            case 1:
                return TermSub02.newInstance();
            case 2:
                return TermSub03.newInstance();
            default:
                return null;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment createdFragment = (Fragment)super.instantiateItem(container,position);
        return createdFragment;
    }

    @Override
    public int getCount() {
        return PAGE_CNT;
    }
}
