package com.deathmotion.antihealthindicator.cache.entities;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerEntity extends CachedEntity {
    private boolean isInvisible;

    public void processMetaData(EntityData metaData, AHIPlayer player) {
        if (metaData.getIndex() != 0) return;
        byte value = (byte) metaData.getValue();
        isInvisible = (value & 0x20) != 0;
    }
}
