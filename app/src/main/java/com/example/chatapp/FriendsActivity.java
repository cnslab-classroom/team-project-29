package com.example.chatapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFriends;
    private FriendsAdapter friendsAdapter;
    private List<Friend> friendsList;
    private FirebaseFirestore firestore;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerViewFriends = findViewById(R.id.recyclerViewFriends);
        emptyView = findViewById(R.id.emptyView);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));

        friendsList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendsList, this::onFriendClick);
        recyclerViewFriends.setAdapter(friendsAdapter);

        firestore = FirebaseFirestore.getInstance();
        loadFriends();
    }

    private void loadFriends() {
        firestore.collection("users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendsList.clear();
                        QuerySnapshot documents = task.getResult();
                        if (documents != null) {
                            for (DocumentSnapshot document : documents) {
                                Friend friend = document.toObject(Friend.class);
                                friendsList.add(friend);
                            }
                            friendsAdapter.notifyDataSetChanged();
                        }
                        toggleEmptyView();
                    } else {
                        Log.e("FriendsActivity", "친구 목록 로드 실패: ", task.getException());
                        emptyView.setText("친구 목록을 불러오지 못했습니다.");
                    }
                });
    }

    private void toggleEmptyView() {
        if (friendsList.isEmpty()) {
            recyclerViewFriends.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFriends.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void onFriendClick(Friend friend) {
        Log.d("FriendsActivity", "Friend clicked: " + friend.getName());
        // 추후 클릭 이벤트 처리 추가 가능
    }
}
