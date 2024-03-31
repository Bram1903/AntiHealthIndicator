package com.deathmotion.antihealthindicator.data;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EntityDataStore {
    public EntityType entityType;

    public boolean isTamed;
    public UUID ownerUUID;
}