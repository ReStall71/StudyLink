package com.restall.studylink.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.restall.studylink.R;
import com.restall.studylink.databinding.ActivityMainBinding;
import com.restall.studylink.ui.fragments.ChatFragment;
import com.restall.studylink.ui.fragments.GroupFragment;
import com.restall.studylink.ui.fragments.HomeFragment;
import com.restall.studylink.ui.fragments.ProfileFragment;
import com.restall.studylink.ui.fragments.TaskFragment;
import com.restall.studylink.utils.FirebaseManager;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseManager firebaseManager;

//    Hashtable<String, List<Integer>> fabValues = new Hashtable<>(Map.of(
//            // Название: фрагмент, fab (иконка, название, обработчик)
//            R.string.home_bottom_nav, List.of(R.drawable.ic_home_48dp, R.string.home_fab),
//            R.string.tasks_bottom_nav, List.of(R.drawable.ic_add_alert_48dp, R.string.tasks_fab),
//            R.string.chats_bottom_nav, List.of(R.drawable.ic)
//    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = new FirebaseManager();

        if (firebaseManager.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if (savedInstanceState == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                changeFragment(new HomeFragment());
                changeFabIcon(R.drawable.ic_home_48dp);
                changeFabTitle(R.string.home_fab);
            }
        }

        setupBottomNavigation();

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

    private void changeFragment(Fragment newFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, newFragment)
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