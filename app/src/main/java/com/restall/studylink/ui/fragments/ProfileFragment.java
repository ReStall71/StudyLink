package com.restall.studylink.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.restall.studylink.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
        });
        binding.deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить аккаунт?")
                    .setMessage("Это действие нельзя отменить!")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) mAuth.signOut();
                                    });
                        }
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
}