package com.restall.studylink.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.restall.studylink.R;
import com.restall.studylink.databinding.ActivityMainBinding;
import com.restall.studylink.ui.fragments.ChatFragment;
import com.restall.studylink.ui.fragments.GroupFragment;
import com.restall.studylink.ui.fragments.HomeFragment;
import com.restall.studylink.ui.fragments.ProfileFragment;
import com.restall.studylink.ui.fragments.TaskFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };

        if (savedInstanceState == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                changeFragment(new HomeFragment());
                changeFabIcon(R.drawable.ic_home_48dp);
                changeFabTitle(R.string.home_fab);
            }
        }

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setupBottomNavigation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener);
        }
    }
    private void setupBottomNavigation() {
        binding.bottomNavigationBar.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == binding.bottomNavigationBar.getSelectedItemId()) {
                return false;
            }

            Fragment selectedFragment = null;
            int fabIcon = 0, fabTitle = 0;
            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
                fabIcon = R.drawable.ic_add_48dp;
                fabTitle = R.string.home_fab;
            } else if (itemId == R.id.tasks) {
                selectedFragment = new TaskFragment();
                fabIcon = R.drawable.ic_add_alert_48dp;
                fabTitle = R.string.tasks_fab;
            } else if (itemId == R.id.chats) {
                selectedFragment = new ChatFragment();
                fabIcon = R.drawable.ic_add_48dp;
                fabTitle = R.string.chats_fab;
            } else if (itemId == R.id.groups){
                selectedFragment = new GroupFragment();
                fabIcon = R.drawable.ic_group_add_48dp;
                fabTitle = R.string.groups_fab;
            }else if (itemId == R.id.profile) {
                selectedFragment = new ProfileFragment();
                fabIcon = R.drawable.ic_person_edit_48dp;
                fabTitle = R.string.profile_fab;
            }

            if (selectedFragment != null) {
                changeFragment(selectedFragment);
                changeFabIcon(fabIcon);
                changeFabTitle(fabTitle);
            }

            return true;
        });
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment)
                .commit();
    }

    private void changeFabIcon(int newIconResId) {
        FloatingActionButton fab = binding.floatingButton;
        if (fab.getTag() != null && fab.getTag().equals(newIconResId)) return;
        fab.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .alpha(0.7f)
                .setDuration(100)
                .withEndAction(() -> {
                    fab.setImageResource(newIconResId);
                    fab.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(150)
                            .setInterpolator(new OvershootInterpolator(0.8f))
                            .start();
                })
                .start();
    }

    private void changeFabTitle(int newActionTitleResId) {
        binding.floatingButton.setContentDescription(getText(newActionTitleResId));
    }

}