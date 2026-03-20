package com.restall.studylink.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private ChatRepository repository;
    private LiveData<List<Chat>> allChats

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatRepository(application);
        allChats = repository.getAllChats();
    }

    public LiveData<List<Chat>> getAllChats() {
        return allChats;
    }

}
