package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View PrivateChatsView ;
    private RecyclerView chatsList ;
    private DatabaseReference ChatsRef ,UsersRef;
    private FirebaseAuth mAuth ;
    private String currentUserID;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       PrivateChatsView =  inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        chatsList = PrivateChatsView.findViewById(R.id.chats_list);//recycler view
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));




    return PrivateChatsView;



    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatsRef,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String[] userDate = {" "};
                        final String[] userTime = { " " };
                        final String[] userState = { " " };

                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};
                        holder.userOnlineStatusImg.setVisibility(View.GONE);
                        holder.userStatus.setVisibility(View.GONE);
                        holder.userOnlineStatusImg.setVisibility(View.GONE);

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.hasChild("image"))
                                    {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);

                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();
                                    if(dataSnapshot.hasChild("userState"))
                                    {
                                         userDate[0] = dataSnapshot.child("userState").child("date").getValue().toString();
                                         userTime[0] = dataSnapshot.child("userState").child("time").getValue().toString();
                                         userState[0] = dataSnapshot.child("userState").child("state").getValue().toString();
                                        if(userState[0].equals("online"))
                                        {
                                            holder.userOnlineStatusImg.setVisibility(View.VISIBLE);
                                            holder.userStatus.setVisibility(View.GONE);
                                            holder.userOnlineStatusImg.setVisibility(View.VISIBLE);
                                        }
                                        else
                                        {
                                            holder.userStatus.setVisibility(View.VISIBLE);
                                            holder.userOnlineStatusImg.setVisibility(View.INVISIBLE);

                                        }

                                    }
                                    String dateTime = "Last Seen:"+userTime[0]+"\n"+userDate[0];

                                    holder.userName.setText(retName);
                                    holder.userStatus.setText(dateTime);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id",usersIDs);
                                            chatIntent.putExtra("visit_user_name",retName);
                                            chatIntent.putExtra("visit_image", retImage[0]);
                                            chatIntent.putExtra("userState",userState[0]);
                                            chatIntent.putExtra("userDate",userDate[0]);
                                            chatIntent.putExtra("userTime",userTime[0]);

                                            startActivity(chatIntent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        return new ChatsViewHolder(view);

                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();


    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage ;
        TextView userStatus ,userName ;
        ImageView  userOnlineStatusImg;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus  = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.users_profile_name);
            userOnlineStatusImg = itemView.findViewById(R.id.user_online_status);

        }



    }


}
