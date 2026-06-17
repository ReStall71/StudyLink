package com.restall.studylink.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.restall.studylink.R;
import com.restall.studylink.data.group.GroupEntity;
import com.restall.studylink.data.group.GroupRepository;
import com.restall.studylink.databinding.DialogCreateGroupBinding;
import com.restall.studylink.databinding.FragmentGroupBinding;
import com.restall.studylink.ui.activities.GroupDetailActivity;
import com.restall.studylink.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GroupFragment extends Fragment implements FabActionProvider {

    FragmentGroupBinding binding;
    private FirebaseManager firebaseManager;
    private GroupRepository groupRepository;
    private GroupsAdapter adapter;
    private List<GroupEntity> groups = new ArrayList<>();
    private String currentUid;

    public GroupFragment() {
        // Required empty public constructor
    }
    public static GroupFragment newInstance(String param1, String param2) {
        return new GroupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseManager = new FirebaseManager();

        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user == null) {
            requireActivity().finish();
            return;
        }
        currentUid = user.getUid();

        groupRepository = new GroupRepository(requireContext());

        binding.createGroup.setOnClickListener(v -> showCreateGroupDialog());

        binding.deleteGroup.setOnClickListener(v -> showDeleteGroupDialog());

        binding.joinGroup.setOnClickListener(v -> showJoinGroupDialog());

        adapter = new GroupsAdapter(groups, group -> {
            Intent intent = new Intent(getContext(), GroupDetailActivity.class);
            intent.putExtra("group_id", group.getGroupId());
            startActivity(intent);
        });
        loadGroups();
    }

    @Override
    public void setupFabAction(FloatingActionButton fab) {
        fab.setImageResource(R.drawable.ic_group_add_48dp);
        fab.setContentDescription(getText(R.string.groups_fab));
        fab.setOnClickListener(v -> {});
    }

    private void loadGroups() {
        groupRepository.syncUserGroups(currentUid, new GroupRepository.SyncCallback() {
            @Override
            public void onSyncComplete() {
                // После синхронизации загружаем из Room (упростим: напрямую из FirebaseManager)
                firebaseManager.getUserGroups(currentUid, new FirebaseManager.GroupsListCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<GroupEntity> groupList) {
                        groups.clear();
                        groups.addAll(groupList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Ошибка синхронизации: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Создать группу");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_group, null);
        EditText etName = view.findViewById(R.id.group_name);
        EditText etDesc = view.findViewById(R.id.group_desc);
        builder.setView(view);
        builder.setPositiveButton("Создать", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Введите название", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseManager.createGroup(name, desc, currentUid, new FirebaseManager.GroupCallback() {
                @Override
                public void onSuccess(String groupId) {
                    Toast.makeText(getContext(), "Группа создана", Toast.LENGTH_SHORT).show();
                    loadGroups();
                }
                @Override
                public void onSuccessGroup(GroupEntity group) {}
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showJoinGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Вступить по коду");
        EditText etCode = new EditText(getContext());
        etCode.setHint("Код приглашения");
        builder.setView(etCode);
        builder.setPositiveButton("Вступить", (dialog, which) -> {
            String code = etCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(getContext(), "Введите код", Toast.LENGTH_SHORT).show();
                return;
            }
            firebaseManager.joinGroupByCode(code, currentUid, new FirebaseManager.GroupCallback() {
                @Override
                public void onSuccess(String groupId) {
                    Toast.makeText(getContext(), "Вы вступили в группу", Toast.LENGTH_SHORT).show();
                    loadGroups();
                }
                @Override
                public void onSuccessGroup(GroupEntity group) {}
                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showDeleteGroupDialog() {

    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    interface OnGroupClickListener {
        void onGroupClick(GroupEntity group);
    }

    // Адаптер для RecyclerView
    class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
        private List<GroupEntity> groups;
        private final OnGroupClickListener listener;


        GroupsAdapter(List<GroupEntity> groups, OnGroupClickListener listener) {
            this.groups = groups;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GroupEntity group = groups.get(position);
            holder.textView.setText(group.getName());
            holder.itemView.setOnClickListener(v -> listener.onGroupClick(group));
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }

}