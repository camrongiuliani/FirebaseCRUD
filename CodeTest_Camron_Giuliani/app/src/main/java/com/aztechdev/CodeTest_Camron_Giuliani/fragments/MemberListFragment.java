package com.aztechdev.CodeTest_Camron_Giuliani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aztechdev.CodeTest_Camron_Giuliani.MainActivity;
import com.aztechdev.CodeTest_Camron_Giuliani.R;
import com.aztechdev.CodeTest_Camron_Giuliani.classes.Member;

import java.util.ArrayList;


public class MemberListFragment extends ListFragment {

    private static final String TAG = "MemberListFragment";

    private ListItemClickListener listItemClickListener;

    public MemberListFragment() {
        //Empty Constructor
    }

    public static MemberListFragment newInstance(ArrayList<Member> arrayList){
        //Handle passed in arguments.
        MemberListFragment frag = new MemberListFragment();
        Log.d(TAG, "newInstance: " + arrayList.size());
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.EXTRA_ARRAY, arrayList);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        //Setup the interface
        super.onAttach(context);
        if (context instanceof ListItemClickListener) {
            listItemClickListener = (ListItemClickListener) context;
        } else {
            throw new IllegalArgumentException("Containing context does not implement the required methods.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_list, container, false);



        ArrayList<Member> arrayList = new ArrayList<>();

        //Conditional/Loop combination to ensure the contents of the stored object is of type ArrayList<Person>.
        ArrayList<?> result = (ArrayList<?>) getArguments().getSerializable(MainActivity.EXTRA_ARRAY);
        if (result != null) {
            for (Object object : result) {
                if (object instanceof Member) {
                    //Add the Person to our arrayList.
                    arrayList.add((Member) object);
                }
            }
        }



        //Assign a memberListAdapter to the listView.
        MemberListAdapter memberListAdapter = new MemberListAdapter(getActivity(), arrayList);
        ListView mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(memberListAdapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        listItemClickListener.onListItemClick(position);
    }

    public interface ListItemClickListener {
        void onListItemClick(Integer position);
    }

    private class MemberListAdapter extends BaseAdapter {

        private static final int ID_CONSTANT = 0x010000000;
        private final Context mContext;
        private final ArrayList<Member> arrayList;

        public MemberListAdapter(Context mContext, ArrayList<Member> arrayList) {
            this.mContext = mContext;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ID_CONSTANT + position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_member_list_item, parent, false);

                // Create a ViewHolder and store references to the children views
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.tvName);
                holder.phone = (TextView) convertView.findViewById(R.id.tvPhoneNumber);
                holder.dob = (TextView) convertView.findViewById(R.id.tvBirthDate);

                // Set the tag
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back.
                holder = (ViewHolder) convertView.getTag();
            }

            //Set the text for the ListView items.

            Member member = arrayList.get(position);

            Log.d(TAG, "getView: " + arrayList.get(position).getFirstName());
            holder.title.setText(member.getFirstName() + " " + member.getLastName());
            holder.phone.setText(member.getPhoneNumber());
            holder.dob.setText(member.getBirthDate());

            return convertView;
        }
    }

    private class ViewHolder {
        TextView title;
        TextView phone;
        TextView dob;
    }

}
