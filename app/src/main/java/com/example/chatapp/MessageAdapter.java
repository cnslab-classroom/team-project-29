package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());//메세지 텍스트

        // 타임스탬프를 날짜와 시간 형식으로 변환
        String formattedTime = formatTimestamp(message.getTimestamp());
        holder.timestampTextView.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    // 타임스탬프 포맷 함수
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textMessage);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}