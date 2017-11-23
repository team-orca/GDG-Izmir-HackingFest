package com.example.orca.howbeautyiam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Admin on 5.05.2016.
 */
public class Fragment4 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container==null)
            return null;
        return (LinearLayout) inflater.inflate(R.layout.fragment4_layout,container,false);
    }
}
