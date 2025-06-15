package org.example;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManualPerson {
    @NotNull
    private String id;

    @Nullable
    private Integer age;


    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public Integer getAge() {
        return age;
    }
}
