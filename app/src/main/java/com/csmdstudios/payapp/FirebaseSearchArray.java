package com.csmdstudios.payapp;

/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
class FirebaseSearchArray implements ChildEventListener {

    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved }
        void onChanged(EventType type, int index, int oldIndex);
    }

    private Query mEmailQuery, mNameQuery;
    private OnChangedListener mListener;
    private ArrayList<DataSnapshot> mSnapshots;
    private FirebaseUser mUser;
    private ProgressBar mLoadingIndicator;
    private int index;


    public FirebaseSearchArray(Activity activity, Query emailRef, Query nameRef) {
        index = 0;
        mEmailQuery = emailRef;
        mNameQuery = nameRef;
        mSnapshots = new ArrayList<DataSnapshot>();
        mLoadingIndicator = (ProgressBar) activity.findViewById(R.id.pb_loading_indicator);
        mEmailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mEmailQuery.addChildEventListener(FirebaseSearchArray.this);
                    mNameQuery.addChildEventListener(FirebaseSearchArray.this);
                } else {
                    cleanup();
                    if (mLoadingIndicator != null)
                        mLoadingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    public void cleanup() {
        mEmailQuery.removeEventListener(this);
        mNameQuery.removeEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();

    }
    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    // Start of ChildEventListener methods
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        if(!contains(mSnapshots, snapshot.getKey()) && !mUser.getUid().equals(snapshot.getKey())) {
            if (mLoadingIndicator != null) {
                mLoadingIndicator.setVisibility(View.GONE);
            }
            mSnapshots.add(index, snapshot);
            index++;
            notifyChangedListeners(OnChangedListener.EventType.Added, index);
        }
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Changed, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.Removed, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Moved, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError firebaseError) {
        Log.d(this.getClass().toString(), firebaseError.getMessage());
    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    public static boolean contains(ArrayList<DataSnapshot> aList, String key) {
        for (DataSnapshot dataSnapshot : aList) {
            if (dataSnapshot.getKey().equals(key)) return true;
        }
        return false;
    }
}