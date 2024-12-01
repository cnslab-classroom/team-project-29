package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        firestore = FirebaseFirestore.getInstance();
        chatRoomId = getIntent().getStringExtra("chatRoomId");

        // 채팅방 ID가 null이거나 비어있는 경우 처리
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            Toast.makeText(this, "Invalid chat room", Toast.LENGTH_SHORT).show();
            finish();  // 유효하지 않은 채팅방이면 액티비티 종료
            return;
        }

        Log.d(TAG, "Checking if chat room exists: " + chatRoomId);

        // Firestore에서 채팅방이 존재하는지 확인
        firestore.collection("chatRooms").document(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "Chat room exists: " + chatRoomId);
                    // 채팅방이 존재하면 메시지 로드
                    loadMessages();
                } else {
                    Log.d(TAG, "Chat room does not exist: " + chatRoomId);
                    // 채팅방이 존재하지 않으면 에러 메시지 표시 후 종료
                    Toast.makeText(ChatActivity.this, "Invalid chat room", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.e(TAG, "Error checking chat room", task.getException());
                // Firestore 요청 실패 처리
                Toast.makeText(ChatActivity.this, "Error checking chat room", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 메시지 전송 버튼 클릭 이벤트 처리
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
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

                        // Firestore로부터 받은 메시지 업데이트
                        if (value != null) {
                            messageList.clear();  // 메시지 목록 초기화 (중복 방지)
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    Message message = dc.getDocument().toObject(Message.class);
                                    messageList.add(message);  // 새 메시지 추가
                                }
                            }
                            messageAdapter.notifyDataSetChanged();  // 어댑터에 데이터 변경 알리기
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
}
