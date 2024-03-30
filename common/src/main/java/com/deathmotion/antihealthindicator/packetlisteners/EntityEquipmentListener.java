package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;

import java.util.Collections;
import java.util.List;

public class EntityEquipmentListener<P, S> extends PacketListenerAbstract {
    private final AHIPlatform<P, S> platform;

    private final boolean useDamageableInterface;
    private final boolean bypassPermissionEnabled;
    private final boolean spoofStackAmount;
    private final boolean spoofDurability;
    private final boolean spoofEnchantments;

    /**
     * The enchantment list to spoof the item with
     */
    private final List<Enchantment> enchantmentList = Collections.singletonList(Enchantment.builder()
            .type(EnchantmentTypes.BLOCK_FORTUNE)
            .level(3)
            .build());

    public EntityEquipmentListener(AHIPlatform<P, S> platform) {
        this.platform = platform;

        this.useDamageableInterface = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
        this.bypassPermissionEnabled = platform.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED);
        this.spoofStackAmount = platform.getConfigurationOption(ConfigOption.STACK_AMOUNT_ENABLED);
        this.spoofDurability = platform.getConfigurationOption(ConfigOption.DURABILITY_ENABLED);
        this.spoofEnchantments = platform.getConfigurationOption(ConfigOption.ENCHANTMENTS_ENABLED);
    }

    /**
     * Called when a packet is sent to the player
     *
     * @param event the packet sends event
     */
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            Player player = (Player) event.getPlayer();

            if (bypassPermissionEnabled) {
                if (this.platform.hasPermission(?, "AntiHealthIndicator.Bypass"))
            }

            List<Equipment> equipmentList = packet.getEquipment();
            if (equipmentList.isEmpty()) {
                return;
            }

            equipmentList.forEach(equipment -> handleEquipment(equipment, packet.getClientVersion()));

            packet.setEquipment(equipmentList);
            event.markForReEncode(true);
        }
    }

    /**
     * Handles the modification of an equipment item as per configurations.
     * If the spoofStackAmount is enabled and the item amount exceeds 1, the item amount is set to 1.
     * If the spoofDurability is enabled and the item is damageable,
     * the damage value on the item is set to 0 for non-legacy items
     * and the legacy data on the item is set to 0 for legacy items.
     * If the spoofEnchantments are enabled and the item is enchanted,
     * the enchantments on the item are set to enchantmentList.
     *
     * @param equipment     a single piece of equipment
     * @param clientVersion the player's client version
     */
    private void handleEquipment(Equipment equipment, ClientVersion clientVersion) {
        ItemStack itemStack = equipment.getItem();
        if (itemStack == null) return;

        if (spoofStackAmount && itemStack.getAmount() > 1) {
            itemStack.setAmount(1);
            equipment.setItem(itemStack);
        }

        if (spoofDurability && itemStack.isDamageableItem()) {
            if (useDamageableInterface) {
                itemStack.setDamageValue(0);
            } else {
                itemStack.setLegacyData((short) 0);
            }
            equipment.setItem(itemStack);
        }

        if (spoofEnchantments && itemStack.isEnchanted(clientVersion)) {
            itemStack.setEnchantments(enchantmentList, clientVersion);
            equipment.setItem(itemStack);
        }
    }
}