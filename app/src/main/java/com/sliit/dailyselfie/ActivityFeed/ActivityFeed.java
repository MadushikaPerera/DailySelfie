package com.sliit.dailyselfie.ActivityFeed;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.roughike.bottombar.OnSizeDeterminedListener;
import com.sliit.dailyselfie.Community.SharePost;
import com.sliit.dailyselfie.R;

import java.util.ArrayList;

public class ActivityFeed extends AppCompatActivity {

    BottomBar mBottomBar;
    ArrayList<SharePost> SharedPosts = new ArrayList<>();
    RecyclerView RV;
    AdapterAC adapterAC;
    Firebase fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);

        fire=new Firebase("https://dailyselfie.firebaseio.com/sharedPost");

        RV= (RecyclerView)findViewById(R.id.recycler1);
        RV.setLayoutManager(new LinearLayoutManager(this));


        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noNavBarGoodness();



        mBottomBar.setItemsFromMenu(R.menu.bottomba_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                if (menuItemId == R.id.nav_home) {

                    Toast.makeText(ActivityFeed.this, "Timeline", Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.nav_fav) {
                    Toast.makeText(ActivityFeed.this, "Take Snap", Toast.LENGTH_SHORT).show();

                } else if (menuItemId == R.id.nav_gallery) {
                    Toast.makeText(ActivityFeed.this, "Favorites", Toast.LENGTH_SHORT).show();

                }

                mBottomBar.mapColorForTab(0, ContextCompat.getColor(ActivityFeed.this, R.color.bottomPrimary));
                mBottomBar.mapColorForTab(1, ContextCompat.getColor(ActivityFeed.this, R.color.bottomPrimary));
                mBottomBar.mapColorForTab(2, ContextCompat.getColor(ActivityFeed.this, R.color.bottomPrimary));


            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        fire =new Firebase("https://dailyselfie.firebaseio.com/sharedpost");
    }

    public void RetriveData(){
           fire.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              GetDataUpdates(dataSnapshot);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              GetDataUpdates(dataSnapshot);
          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {

          }
      });
    }

    public void GetDataUpdates(DataSnapshot dataSnapshot ){
         SharedPosts.clear();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            SharePost sp = new SharePost();
            sp.setPostSharer(ds.getValue(SharePost.class).getPostSharer());
            sp.setPostType(ds.getValue(SharePost.class).getPostType());
            sp.setPostDescription(ds.getValue(SharePost.class).getPostDescription());
            sp.setPostImage(ds.getValue(SharePost.class).getPostImage());

            SharedPosts.add(sp);

        }

         if(SharedPosts.size()>0){
                adapterAC = new AdapterAC(ActivityFeed.this,SharedPosts);
                RV.setAdapter(adapterAC);

        }else{
             Toast.makeText(ActivityFeed.this, "No data available", Toast.LENGTH_SHORT).show();
         }

    }

}
