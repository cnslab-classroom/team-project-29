package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private final List<Friend> friendsList;
    private final OnFriendClickListener onFriendClickListener;

    public FriendsAdapter(List<Friend> friendsList, OnFriendClickListener onFriendClickListener) {
        this.friendsList = friendsList;
        this.onFriendClickListener = onFriendClickListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendsList.get(position);
        holder.textViewName.setText(friend.getName());
        holder.itemView.setOnClickListener(v -> onFriendClickListener.onFriendClick(friend));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }
}
