package com.restall.studylink.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.restall.studylink.R;
import com.restall.studylink.data.group.GroupEntity;
import com.restall.studylink.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private TextView tvGroupName, tvGroupDesc, tvInviteCode;
    private RecyclerView rvMembers;
    private FirebaseManager firebaseManager;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupDesc = findViewById(R.id.tv_group_desc);
        tvInviteCode = findViewById(R.id.tv_invite_code);
        rvMembers = findViewById(R.id.rv_members);
        rvMembers.setLayoutManager(new LinearLayoutManager(this));

        groupId = getIntent().getStringExtra("group_id");
        firebaseManager = new FirebaseManager();

        loadGroupInfo();
    }

    private void loadGroupInfo() {
        firebaseManager.getGroupById(groupId, new FirebaseManager.GroupCallback() {
            @Override
            public void onSuccess(String s) {}
            @Override
            public void onSuccessGroup(GroupEntity group) {
                if (group != null) {
                    tvGroupName.setText(group.getName());
                    tvGroupDesc.setText(group.getDescription() != null ? group.getDescription() : "");
                    tvInviteCode.setText("Код приглашения: " + group.getInviteCode());
                    loadMembers(group.getMemberIds());
                } else {
                    Toast.makeText(GroupDetailActivity.this, "Группа не найдена", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(GroupDetailActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadMembers(List<String> memberIds) {
        // Для простоты выводим только uid, можно расширить до имён через getUserProfile
        List<String> members = new ArrayList<>(memberIds);
        MembersAdapter adapter = new MembersAdapter(members);
        rvMembers.setAdapter(adapter);
    }

    class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
        private List<String> members;
        MembersAdapter(List<String> members) { this.members = members; }
        @NonNull
        @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(members.get(position));
        }
        @Override public int getItemCount() { return members.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) { super(itemView); textView = itemView.findViewById(android.R.id.text1); }
        }
    }
}