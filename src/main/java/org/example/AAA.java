package org.example;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;


import java.util.List;

@NullMarked
@Builder
@Getter
@Setter
public class AAA {

    private String v1;
//    private @Nullable String v2;
    private List<String> v3;
    private List<@org.jetbrains.annotations.Nullable String> v4;

//    private @NotNull String v1;
    private @org.jetbrains.annotations.Nullable String v2;
//    private @NotNull List<@NotNull String> v3;
//    private @NotNull List<@org.jetbrains.annotations.Nullable String> v4;
}
