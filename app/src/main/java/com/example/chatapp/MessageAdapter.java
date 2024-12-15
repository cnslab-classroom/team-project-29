package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

        // 메시지 텍스트 설정
        holder.messageTextView.setText(message.getMessage());

        // 타임스탬프를 날짜와 시간 형식으로 변환
        String formattedTime = formatTimestamp(message.getTimestamp());
        holder.timestampTextView.setText(formattedTime);

        // 이미지 URL이 있을 경우 이미지 로드
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            holder.imageMessage.setVisibility(View.VISIBLE); // 이미지뷰 표시
            Glide.with(holder.itemView.getContext())
                    .load(message.getImageUrl())
                    .into(holder.imageMessage);
        } else {
            holder.imageMessage.setVisibility(View.GONE); // 이미지뷰 숨기기
        }
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
        ImageView imageMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textMessage);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            imageMessage = itemView.findViewById(R.id.imageMessage); // 이미지뷰 초기화
        }
    }
}
