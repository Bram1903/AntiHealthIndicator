package com.deathmotion.antihealthindicator.packetlisteners;

import com.deathmotion.antihealthindicator.AntiHealthIndicator;
import com.deathmotion.antihealthindicator.enums.ConfigOption;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
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
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class EntityEquipmentListener extends PacketListenerAbstract {
    private final ConfigManager configManager;

    private final boolean useDamageableInterface;

    /**
     * The enchantment list to spoof the item with
     */
    private final List<Enchantment> enchantmentList = Collections.singletonList(Enchantment.builder()
            .type(EnchantmentTypes.BLOCK_FORTUNE)
            .level(3)
            .build());

    public EntityEquipmentListener(AntiHealthIndicator plugin) {
        this.configManager = plugin.getConfigManager();
        this.useDamageableInterface = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
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

            if (configManager.getConfigurationOption(ConfigOption.ALLOW_BYPASS_ENABLED)) {
                if (player.hasPermission("AntiHealthIndicator.Bypass")) return;
            }

            List<Equipment> equipmentList = packet.getEquipment();
            if (equipmentList.isEmpty()) {
                return;
            }

            if (configManager.getConfigurationOption(ConfigOption.STACK_AMOUNT_ENABLED))
                spoofItemStackAmount(equipmentList);

            if (configManager.getConfigurationOption(ConfigOption.DURABILITY_ENABLED))
                spoofItemStackDurability(equipmentList);

            if (configManager.getConfigurationOption(ConfigOption.ENCHANTMENTS_ENABLED))
                spoofEnchantment(packet.getClientVersion(), equipmentList);

            packet.setEquipment(equipmentList);
            event.markForReEncode(true);
        }
    }

    /**
     * Spoof the amount of the item to 1
     *
     * @param equipmentList the list of equipment
     */
    private void spoofItemStackAmount(List<Equipment> equipmentList) {
        equipmentList.forEach(equipment -> {
            ItemStack itemStack = equipment.getItem();

            if (itemStack.getAmount() > 1) {
                itemStack.setAmount(1);
                equipment.setItem(itemStack);
            }
        });
    }

    /**
     * Spoof the durability items to appear as if they are new
     *
     * @param equipmentList the list of equipment
     */
    private void spoofItemStackDurability(List<Equipment> equipmentList) {
        equipmentList.forEach(equipment -> {
            ItemStack itemStack = equipment.getItem();

            if (itemStack.isDamageableItem()) {
                if (useDamageableInterface) {
                    itemStack.setDamageValue(0);
                } else {
                    itemStack.setLegacyData((short) 0);
                }

                equipment.setItem(itemStack);
            }
        });
    }

    /**
     * Spoof the enchantment level of the item to Fortune III (Xbox 360 Reference)
     *
     * @param clientVersion the player client version
     * @param equipmentList the list of equipment
     */
    private void spoofEnchantment(ClientVersion clientVersion, List<Equipment> equipmentList) {
        equipmentList.forEach(equipment -> {
            ItemStack itemStack = equipment.getItem();

            if (itemStack.isEnchanted(clientVersion)) {
                itemStack.setEnchantments(enchantmentList, clientVersion);
                equipment.setItem(itemStack);
            }
        });
    }
}