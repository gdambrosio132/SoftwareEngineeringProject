package com.example.calendarapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.calendarapp.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class personAdapter extends FirebaseRecyclerAdapter<
        User, personAdapter.personsViewholder> {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String senderUserID, receiverUserID;
    private String current_state = "not_friends";


    public personAdapter(
            @NonNull FirebaseRecyclerOptions<User> options)
    {
        super(options);
    }

    // Function to bind the view in Card view(here
    // "person.xml") iwth data in
    // model class(here "person.class")
    @Override
    protected void
    onBindViewHolder(@NonNull final personsViewholder holder,
                     int position, @NonNull final User model)
    {

        // Add username from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.username.setText(model.getUsername());
        holder.status.setText(model.getStatus());

        // Set visibility to add friend
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        // get ID of sender and receiver of friend requests
        senderUserID = mAuth.getCurrentUser().getUid();

        if(model.getFriends().containsKey(senderUserID))
            current_state = "is_friends";
        else
            current_state = "not_friends";


        if (senderUserID.equals(model.getId()) || current_state.equals("is_friends")){
            holder.add_friend_button.setVisibility(View.GONE);
        }

        else{
            holder.add_friend_button.setVisibility(View.VISIBLE);
            holder.add_friend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    receiverUserID = model.getId();
                    userRef.child(senderUserID).child("friends").child(receiverUserID).setValue("true")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    userRef.child(receiverUserID).child("friends").child(senderUserID).setValue("true");
                                    current_state = "is_friends";
                                    holder.add_friend_button.setVisibility(View.GONE);
                                }
                            });
                }
            });
        }

    }



    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public personsViewholder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person, parent, false);
        return new personAdapter.personsViewholder(view);
    }

    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class personsViewholder
            extends RecyclerView.ViewHolder {
        TextView username, status;
        Button add_friend_button;
        //ImageView imageView;
        public personsViewholder(@NonNull View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            status = itemView.findViewById(R.id.status);
            add_friend_button = itemView.findViewById(R.id.send_friend_request);
        }
    }
}