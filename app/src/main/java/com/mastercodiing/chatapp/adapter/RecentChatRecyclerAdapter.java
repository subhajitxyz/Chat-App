package com.mastercodiing.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mastercodiing.chatapp.ChatActivity;
import com.mastercodiing.chatapp.R;
import com.mastercodiing.chatapp.model.ChatroomModel;
import com.mastercodiing.chatapp.model.UserModel;
import com.mastercodiing.chatapp.utils.AndroidUtil;
import com.mastercodiing.chatapp.utils.FirebaseUtil;

import java.util.List;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel,
        RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {

        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        UserModel otherUserModel=task.getResult().toObject(UserModel.class);

                        FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if (t.isSuccessful()){
                                        Uri uri=t.getResult();
                                        AndroidUtil.setProfilePic(context,uri,holder.profilePic);
                                    }
                                });

                        boolean lastMessageSentByMe=model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());


                        //this code by me

                        holder.usernameText.setText(otherUserModel.getUsername());




                        if(lastMessageSentByMe){
                            holder.lastMessageText.setText("You : "+model.getLastMessage());
                        }else{
                            holder.lastMessageText.setText(model.getLastMessage());
                        }


                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v->{
                            //navigation to chat activity
                            Intent intent=new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }

                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.user_name_text);
           lastMessageTime=itemView.findViewById(R.id.last_message_time_text);
           lastMessageText=itemView.findViewById(R.id.last_message_text);
            profilePic=itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}

