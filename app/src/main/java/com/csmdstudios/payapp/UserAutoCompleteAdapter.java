package com.csmdstudios.payapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by wayne on 25/6/16.
 */
public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private static final String TAG = "UserAutoCompleteAdapter";
    private Context mContext;
    private ArrayList<User> resultList = new ArrayList<>();

    public UserAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.image_dropdown, parent, false);
        }
        User user = (User) getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(user.getName());
        if (user.getPic_url() != null) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            Glide.with(mContext).load(user.getPic_url()).into(imageView);
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ArrayList<User> users = findUsers(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = users;
                    filterResults.count = users.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (ArrayList<User>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

    }

    public static ArrayList<User> findUsers(Context context, String nameOfUser) {
        final ArrayList<User> users = new ArrayList<>();
        users.add(new User("Rohan", "affaa@aag.faf"));
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");
        mRef.orderByChild("name")
                .startAt(nameOfUser)
                .limitToFirst(MAX_RESULTS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                users.add(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        return users;
    }

    public static ArrayList<String> findUsers2(Context context, String nameOfUser) {
        final ArrayList<String> users = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users");
        mRef.orderByChild("name")
                .startAt(nameOfUser)
                .limitToFirst(MAX_RESULTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        return users;
    }
}
