package org.example;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class ManualPerson {
    @NotNull
    private String id;

    @Nullable
    private Integer age;

    @Nullable
    private List<@Nullable String> names;


    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public Integer getAge() {
        return age;
    }

    public void setNames(@Nullable List<@Nullable String> names) {
        this.names = names;
    }

    @Nullable
    public List<@Nullable String> getNames() {
        return names;
    }
}
