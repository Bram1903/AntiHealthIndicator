package com.deathmotion.antihealthindicator.data;


import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
public class WolfData {
    @Nullable
    UUID ownerUniqueId;

    boolean isTamed;
}