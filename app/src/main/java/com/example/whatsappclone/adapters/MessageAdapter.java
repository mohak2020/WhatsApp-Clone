package com.example.whatsappclone.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Chat;
import com.example.whatsappclone.model.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> mMessageList;


    public MessageAdapter(ArrayList<Message> messages) {
        this.mMessageList = messages;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {

        viewHolder.messageView.setText(mMessageList.get(i).getMessageText());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView messageView;
        FrameLayout mLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageView = itemView.findViewById(R.id.message_view);


        }
    }
}
