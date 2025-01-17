package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private TextInputLayout usernameLayout, emailLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameLayout = findViewById(R.id.usernameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        MaterialButton registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        EditText usernameEditText = usernameLayout.findViewById(R.id.editTextUsername);
        EditText emailEditText = emailLayout.findViewById(R.id.editTextEmail);
        EditText passwordEditText = passwordLayout.findViewById(R.id.editTextPassword);

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameLayout.setError("사용자 이름을 입력해주세요");
            return;
        }
        if (email.isEmpty()) {
            emailLayout.setError("이메일을 입력해주세요");
            return;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("비밀번호를 입력해주세요");
            return;
        }
        if (password.length() < 6) {
            passwordLayout.setError("비밀번호는 6자리 이상이어야 합니다");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserData(username, email);
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "오류 발생";
                        Toast.makeText(this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String username, String email) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("created_at", System.currentTimeMillis());

        firestore.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> Log.d("RegisterActivity", "사용자 정보 저장 성공"))
                .addOnFailureListener(e -> Log.e("RegisterActivity", "사용자 정보 저장 실패", e));
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
