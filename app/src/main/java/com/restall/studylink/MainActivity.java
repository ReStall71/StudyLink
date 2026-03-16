package com.restall.studylink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.restall.studylink.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        binding.deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();

                    // Удалить из Realtime Database (аналогично setValue)
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users").child(uid)
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                // Удалить из Auth
                                user.delete()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                Log.w("TAG", "Ошибка удаления Auth", task.getException());
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> Log.w("TAG", "Ошибка DB", e));
                }
            }
        });

    }


}