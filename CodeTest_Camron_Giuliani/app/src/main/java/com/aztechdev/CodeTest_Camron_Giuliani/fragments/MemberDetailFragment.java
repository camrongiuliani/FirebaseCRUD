package com.aztechdev.CodeTest_Camron_Giuliani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aztechdev.CodeTest_Camron_Giuliani.R;
import com.aztechdev.CodeTest_Camron_Giuliani.classes.Member;


public class MemberDetailFragment extends DialogFragment implements View.OnClickListener {

    @SuppressWarnings("WeakerAccess")
    public static final String EXTRA_MEMBER = "EXTRA_MEMBER";

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhoneNumber;
    private EditText etBirthDate;
    private EditText etZipCode;
    private String uid;
    private Member mMember;

    private UpdateMemberListener mUpdateMemberListener;


    public MemberDetailFragment() {
        // Required empty public constructor
    }


    public static MemberDetailFragment newInstance(Member member, String mUid) {
        MemberDetailFragment frag = new MemberDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MEMBER, member);
        args.putString("EXTRA_UID", mUid);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_fragment_detail, container, false);
        Button mAddButton = (Button) view.findViewById(R.id.btnUpdateMember);
        Button mRemoveButton = (Button) view.findViewById(R.id.btnRemoveMember);

        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        etBirthDate = (EditText) view.findViewById(R.id.etBirthDate);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etZipCode = (EditText) view.findViewById(R.id.etZipCode);

        mAddButton.setOnClickListener(this);
        mRemoveButton.setOnClickListener(this);

        mMember = (Member) getArguments().getSerializable(EXTRA_MEMBER);

        assert mMember != null;
        etFirstName.setText(mMember.getFirstName());
        etLastName.setText(mMember.getLastName());
        etPhoneNumber.setText(mMember.getPhoneNumber());
        etBirthDate.setText(mMember.getBirthDate());
        etZipCode.setText(String.valueOf(mMember.getZipCode()));
        this.uid = mMember.getUserId();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        assert window != null;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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


        mUpdateMemberListener.UpdateMember(new Member(
                this.uid,
                etFirstName.getText().toString().trim(),
                etLastName.getText().toString().trim(),
                Long.valueOf(etZipCode.getText().toString().trim()),
                etBirthDate.getText().toString().trim(),
                etPhoneNumber.getText().toString().trim())
        );
        getDialog().dismiss();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateMemberListener) {
            mUpdateMemberListener = (UpdateMemberListener) context;
        } else {
            throw new IllegalArgumentException("Containing context does not implement the required methods.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdateMember)
        generateMember();

        if (v.getId() == R.id.btnRemoveMember) {
            mUpdateMemberListener.RemoveMember(mMember);
            getDialog().dismiss();
        }
    }

    public interface UpdateMemberListener {
        void RemoveMember(Member member);
        void UpdateMember(Member member);
    }

}
