package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private FirebaseFirestore firestore;
    private String chatRoomId;

    private static final String TAG = "ChatActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // UI 요소 초기화
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // 메시지 목록 초기화
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance();
        chatRoomId = "default_chat_room"; // 고정된 채팅방 ID 사용

        // 채팅방 메시지 로드
        loadMessages();

        // 메시지 전송 버튼 이벤트
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    private void initializeChatRoom() {
        firestore.collection("chatRooms").document(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document == null || !document.exists()) {
                    // 채팅방이 없으면 새로 생성
                    firestore.collection("chatRooms")
                            .document(chatRoomId)
                            .set(new ChatRoom("Default Chat Room"))
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Default chat room created"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to create chat room", e));
                }
            }
        });
    }
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;

        public SpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;  // 각 아이템의 아래쪽에 공간 추가
        }
    }


    // Firestore에서 메시지 로드
    private void loadMessages() {
        firestore.collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    Message message = dc.getDocument().toObject(Message.class);
                                    messageList.add(message); // 새로운 메시지만 추가
                                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                                    recyclerViewMessages.scrollToPosition(messageList.size() - 1); // 최신 메시지로 스크롤
                                }
                            }
                        }
                    }
                });
    }

    // 메시지 전송
    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Firebase에서 현재 사용자 ID 가져오기
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Message message = new Message(senderId, messageText, System.currentTimeMillis());

        // Firestore에 메시지 저장
        firestore.collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .add(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // 메시지 전송 성공
                        editTextMessage.setText("");  // 메시지 입력란 초기화
                    } else {
                        // 메시지 전송 실패
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessageWithImage(Uri imageUri, String messageText) {
        if (imageUri == null && messageText.trim().isEmpty()) {
            return; // 텍스트와 이미지 둘 다 비어있으면 메시지를 보내지 않음
        }

        // Firebase Storage에 이미지 업로드
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_images/" + System.currentTimeMillis() + ".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Firestore에 메시지 저장
                    String messageTextToStore = messageText.isEmpty() ? null : messageText;
                    sendMessageToFirestore(messageTextToStore, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    // Firestore에 메시지 저장
    private void sendMessageToFirestore(String messageText, String imageUrl) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        // Firestore에 저장할 메시지 객체 생성
        Message message = new Message(senderId, messageText, timestamp);

        // Firestore에 메시지 저장
        FirebaseFirestore.getInstance().collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .add(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
