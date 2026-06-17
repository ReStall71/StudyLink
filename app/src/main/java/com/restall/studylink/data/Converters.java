package com.restall.studylink.data;

import androidx.room.TypeConverter;
import com.restall.studylink.data.task.TaskStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromTaskStatus(TaskStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static TaskStatus toTaskStatus(String name) {
        return name == null ? null : TaskStatus.valueOf(name);
    }

    private static final String SEPARATOR = "∶∶"; // редкий юникод-разделитель

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(SEPARATOR);
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> toList(String data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(data.split(SEPARATOR)));
    }
}