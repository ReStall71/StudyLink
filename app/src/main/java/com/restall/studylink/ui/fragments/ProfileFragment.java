package com.restall.studylink.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.restall.studylink.R;
import com.restall.studylink.databinding.FragmentProfileBinding;
import com.restall.studylink.ui.activities.LoginActivity;
import com.restall.studylink.utils.FirebaseManager;


public class ProfileFragment extends Fragment implements FabActionProvider{

    private FragmentProfileBinding binding;
    private FirebaseManager firebaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        firebaseManager = new FirebaseManager();
        super.onViewCreated(view, savedInstanceState);
        binding.logoutButton.setOnClickListener(v -> {
            firebaseManager.logout();
            requireActivity().finish();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        binding.deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить аккаунт?")
                    .setMessage("Это действие нельзя отменить!")
                    .setPositiveButton("Удалить", (dialog, which) -> {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            user.delete()
//                                    .addOnCompleteListener(task -> {
//                                        if (task.isSuccessful()) mAuth.signOut();
//                                    });
//                        }
                        firebaseManager.delete_account();
                        requireActivity().finish();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void setupFabAction(FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_person_edit_48dp);
        fab.setContentDescription(getText(R.string.profile_fab));
        fab.setOnClickListener(v -> {
//            showAddDialog();
        });
    }
}