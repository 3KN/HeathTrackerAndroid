package tracker.health.buzzapps.ua.heathtracker.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tracker.health.buzzapps.ua.heathtracker.Model.User;
import tracker.health.buzzapps.ua.heathtracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FatigueFragment extends Fragment {


    DatabaseReference mDataBase;
    public FatigueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_fatigue, container, false);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        FatigueAdapter adapter = new FatigueAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }


    public class FatigueAdapter extends RecyclerView.Adapter<FatigueAdapter.ViewHolder>{


        private static final String USERS = "users";
        private static final String DATA = "data";
        private static final String FATIGUE = "fatigue";
        private static final String FATIGUE_VALUE = "fatigueValue";
        HashMap<String,String> hashMap;
        HashMap<String,User> users;
        ArrayList<String> arrayOfId;
        FatigueAdapter(){
            users = new HashMap<>();
            arrayOfId = new ArrayList<>();
            hashMap = new HashMap<>();
            fillUsers();
        }

        private void fillUsers() {
            mDataBase.child(USERS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postsnapshot:dataSnapshot.getChildren()) {
                        User user = postsnapshot.getValue(User.class);
                        users.put(postsnapshot.getKey(),user);
                        arrayOfId.add(postsnapshot.getKey());
                    }
                    fillhashMap();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void fillhashMap() {


            for (Map.Entry<String,User> entry:users.entrySet()) {
                mDataBase.child(DATA).child(FATIGUE).child(entry.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap:dataSnapshot.getChildren()) {
                            if(snap.getValue()!=null) {
                                hashMap.put(dataSnapshot.getKey(), snap.getValue().toString());
                            }
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String id = arrayOfId.get(position);
            User currentUser = users.get(id);
            String fatigue = hashMap.get(id);
            Glide.with(getActivity()).load(currentUser.getPhotoUrl()).into(holder.image);
            holder.name.setText(currentUser.getName());
            if(fatigue!=null) {
                if (Integer.parseInt(fatigue) > 70) {
                    holder.fatigue.setTextColor(Color.MAGENTA);
                } else if (Integer.parseInt(fatigue) > 50) {
                    holder.fatigue.setTextColor(Color.RED);
                } else if (Integer.parseInt(fatigue) > 30) {
                    holder.fatigue.setTextColor(Color.YELLOW);
                } else {
                    holder.fatigue.setTextColor(Color.GREEN);
                }
            }
            holder.fatigue.setText(fatigue+" %");
        }

        @Override
        public int getItemCount() {
            if(hashMap==null){
                return 0;
            }else{
                return hashMap.size();
            }
        }

        public class   ViewHolder extends RecyclerView.ViewHolder {
            TextView fatigue;
            TextView name;
            ImageView image;
            public ViewHolder(View itemView) {
                super(itemView);
                fatigue = (TextView)itemView.findViewById(R.id.fatigue_text);
                name = (TextView)itemView.findViewById(R.id.nameText);
                image = (ImageView)itemView.findViewById(R.id.imageView);
            }
        }
    }
}
