package com.example.whatsappclone.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.whatsappclone.MessageActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Chat;
import com.example.whatsappclone.model.User;

import java.util.ArrayList;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private static final String TAG = "ChatAdapter";

    ArrayList<Chat> mChatList;

    public ChatAdapter(ArrayList<Chat> chats) {

        this.mChatList = chats;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.userNameView.setText(mChatList.get(i).getChatId());
        Log.d(TAG, "onBindViewHolder: "+mChatList.get(i).getChatId() );

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID",mChatList.get(viewHolder.getAdapterPosition()).getChatId());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userNameView;
        FrameLayout mLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.chat_user_name_view);
            mLayout = itemView.findViewById(R.id.chat_layout);
        }
    }
}
