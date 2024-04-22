package com.deathmotion.antihealthindicator.data;

import com.deathmotion.antihealthindicator.util.MetadataIndex;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class WolfData extends LivingEntityData {
    private boolean isTamed;
    private UUID ownerUUID;

    public boolean isOwnerPresent() {
        return ownerUUID != null;
    }

    public UUID getOwnerUUID() {
        if (!isOwnerPresent()) {
            throw new IllegalStateException("Owner UUID not present");
        }
        return ownerUUID;
    }

    @Override
    public void processMetaData(EntityData metaData, User user) {
        int index = metaData.getIndex();

        if (index == MetadataIndex.TAMABLE_TAMED) {
            setTamed(((Byte) metaData.getValue() & 0x04) != 0);
        } else if (index == MetadataIndex.TAMABLE_OWNER) {
            Object value = metaData.getValue();

            UUID ownerUUID = value instanceof String
                    ? Optional.ofNullable((String) value)
                    .filter(user.getUUID().toString()::equals)
                    .map(UUID::fromString)
                    .orElse(null)
                    : ((Optional<UUID>) value)
                    .filter(user.getUUID()::equals)
                    .orElse(null);

            setOwnerUUID(ownerUUID);
        }
    }
}
