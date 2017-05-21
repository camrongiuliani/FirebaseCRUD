package com.aztechdev.CodeTest_Camron_Giuliani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aztechdev.CodeTest_Camron_Giuliani.R;
import com.aztechdev.CodeTest_Camron_Giuliani.classes.Member;


public class MemberMgmtFragment extends Fragment implements View.OnClickListener {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhoneNumber;
    private EditText etBirthDate;
    private EditText etZipCode;

    private AddMemberListener mAddMemberListener;


    public MemberMgmtFragment() {
        // Required empty public constructor
    }


    public static MemberMgmtFragment newInstance() {
        return new MemberMgmtFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member_mgmt, container, false);
        Button mAddButton = (Button) view.findViewById(R.id.btnAddMember);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        etBirthDate = (EditText) view.findViewById(R.id.etBirthDate);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etZipCode = (EditText) view.findViewById(R.id.etZipCode);
        mAddButton.setOnClickListener(this);

        return view;
    }

    private void generateMember() {
        if (etFirstName.getText().toString().trim().equals("") ||
                etLastName.getText().toString().trim().equals("")||
                etZipCode.getText().toString().trim().equals("") ||
                etBirthDate.getText().toString().trim().equals("") ||
                etPhoneNumber.getText().toString().trim().equals("")){

            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }

        mAddMemberListener.AddMember(new Member(
                null,
                etFirstName.getText().toString().trim(),
                etLastName.getText().toString().trim(),
                Long.valueOf(etZipCode.getText().toString().trim()),
                etBirthDate.getText().toString().trim(),
                etPhoneNumber.getText().toString().trim())
        );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddMemberListener) {
            mAddMemberListener = (AddMemberListener) context;
        } else {
            throw new IllegalArgumentException("Containing context does not implement the required methods.");
        }
    }

    @Override
    public void onClick(View v) {
        generateMember();
    }

    public interface AddMemberListener {
        void AddMember(Member member);
    }

}
