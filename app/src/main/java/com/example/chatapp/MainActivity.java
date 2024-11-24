package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputLayout emailLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextInputLayout 초기화
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        // Firebase 인증 초기화
        auth = FirebaseAuth.getInstance();

        // 회원가입 버튼
        MaterialButton registerButton = findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼
        MaterialButton loginButton = findViewById(R.id.button);
        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        // TextInputLayout 안의 EditText에서 텍스트 가져오기
        EditText emailEditText = emailLayout.findViewById(R.id.EmailAddress);
        EditText passwordEditText = passwordLayout.findViewById(R.id.Password);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 입력값 유효성 검사
        if (email.isEmpty()) {
            emailLayout.setError("이메일을 입력해주세요");
            return;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("비밀번호를 입력해주세요");
            return;
        }

        // Firebase 로그인
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공 시
                        auth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        // ChatActivity로 이동
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // 로그인 실패 시
                        Toast.makeText(MainActivity.this,
                                "로그인 실패: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}