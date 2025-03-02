package com.deathmotion.antihealthindicator.cache.entities;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.spoofers.impl.InvisibilitySpoofer;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.world.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerEntity extends CachedEntity {
    private boolean isInvisible;

    private Location location;
    private List<EntityData> entityDataList;

    public void processMetaData(EntityData metaData, AHIPlayer player) {
        if (metaData.getIndex() != 0) return;
        byte value = (byte) metaData.getValue();
        boolean invisible = (value & 0x20) != 0;

//        if (!isInvisible && invisible) {
//            player.spoofManager.getGenericSpoofer(InvisibilitySpoofer.class).setInvisible(getEntityId());
//        } else if (isInvisible && !invisible) {
//            player.spoofManager.getGenericSpoofer(InvisibilitySpoofer.class).setVisible(getEntityId());
//        }

        if (isInvisible && !invisible) {
            player.spoofManager.getGenericSpoofer(InvisibilitySpoofer.class).setInvisible(this);
        } else if (!isInvisible && invisible) {
            player.spoofManager.getGenericSpoofer(InvisibilitySpoofer.class).setVisible(this);
        }

        isInvisible = invisible;
    }
}
