/*
 *  This file is part of AntiHealthIndicator - https://github.com/Bram1903/AntiHealthIndicator
 *  Copyright (C) 2025 Bram and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.antihealthindicator.spoofers.impl;

import com.deathmotion.antihealthindicator.data.AHIPlayer;
import com.deathmotion.antihealthindicator.data.Settings;
import com.deathmotion.antihealthindicator.spoofers.Spoofer;
import com.deathmotion.antihealthindicator.spoofers.type.PacketSpoofer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;

import java.util.Collections;
import java.util.List;

public class EquipmentSpoofer extends Spoofer implements PacketSpoofer {

    private final boolean useDamageableInterface;

    /**
     * The enchantment list to spoof the item with
     */
    private final List<Enchantment> enchantmentList = Collections.singletonList(Enchantment.builder()
            .type(EnchantmentTypes.BLOCK_FORTUNE)
            .level(3)
            .build());

    public EquipmentSpoofer(AHIPlayer player) {
        super(player);

        this.useDamageableInterface = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        final Settings settings = configManager.getSettings();
        if (!settings.getItems().isEnabled()) return;

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
        List<Equipment> equipmentList = packet.getEquipment();
        if (equipmentList.isEmpty()) {
            return;
        }

        equipmentList.forEach(equipment -> handleEquipment(equipment, packet.getClientVersion(), settings));

        packet.setEquipment(equipmentList);
        event.markForReEncode(true);
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
    private void handleEquipment(Equipment equipment, ClientVersion clientVersion, Settings settings) {
        ItemStack itemStack = equipment.getItem();
        if (itemStack == null) {
            return;
        }

        if (settings.getItems().isStackAmount() && itemStack.getAmount() > 1) {
            itemStack.setAmount(1);
            equipment.setItem(itemStack);
        }

        if (settings.getItems().isDurability() && itemStack.isDamageableItem() && itemStack.getDamageValue() > 0) {
            boolean isElytra = (itemStack.getType() == ItemTypes.ELYTRA);

            // Additional checks if the item is Elytra
            if (isElytra) {
                boolean isInAllowedElytraSlot = (equipment.getSlot() == EquipmentSlot.MAIN_HAND || equipment.getSlot() == EquipmentSlot.OFF_HAND || equipment.getSlot() == EquipmentSlot.HELMET);
                boolean isReallyBroken = (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1);

                // Prevent resetting Elytra durability unless allowed by settings
                if (!settings.getItems().isBrokenElytra() || !isInAllowedElytraSlot || !isReallyBroken) {
                    resetItemDamage(itemStack);
                    equipment.setItem(itemStack);
                }
            } else {
                // For non-Elytra items, reset damage unconditionally when durability is disabled
                resetItemDamage(itemStack);
                equipment.setItem(itemStack);
            }
        }

        if (settings.getItems().isEnchantments() && itemStack.isEnchanted(clientVersion)) {
            itemStack.setEnchantments(enchantmentList, clientVersion);
            equipment.setItem(itemStack);
        }
    }

    private void resetItemDamage(ItemStack itemStack) {
        if (useDamageableInterface) {
            itemStack.setDamageValue(0);
        } else {
            itemStack.setLegacyData((short) 0);
        }
    }
}
