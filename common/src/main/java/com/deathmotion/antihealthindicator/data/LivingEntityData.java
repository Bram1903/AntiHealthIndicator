package com.deathmotion.antihealthindicator.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LivingEntityData {
    public EntityType entityType;

    // Wolf specific data
    public boolean isTamed;
    public UUID ownerUUID;

    public boolean isOwnerPresent() {
        return ownerUUID != null;
    }

    public UUID getOwnerUUID() {
        if (!isOwnerPresent()) {
            throw new IllegalStateException("Owner UUID not present");
        }
        return ownerUUID;
    }
}