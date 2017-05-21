package com.aztechdev.CodeTest_Camron_Giuliani.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.aztechdev.CodeTest_Camron_Giuliani.R;


public class NoConnectionFragment extends DialogFragment {


    public NoConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 350);
    }

    public static NoConnectionFragment newInstance() {
        return new NoConnectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_no_connection, container, false);
    }

}
