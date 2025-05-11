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
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;

import java.util.Collections;
import java.util.List;

public final class EquipmentSpoofer extends Spoofer {

    public EquipmentSpoofer(AHIPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_EQUIPMENT) return;

        Settings.Items settings = configManager.getSettings().getItems();
        if (!settings.isEnabled()) return;

        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
        List<Equipment> items = packet.getEquipment();
        if (items.isEmpty()) return;

        items.forEach(eq -> applySpoof(eq.getItem(), eq.getSlot(), settings));
        packet.setEquipment(items);
        event.markForReEncode(true);
    }

    private void applySpoof(ItemStack item, EquipmentSlot slot, Settings.Items settings) {
        if (item.getType() == ItemTypes.AIR) return;

        if (settings.isStackAmount() && item.getAmount() > 1) {
            item.setAmount(1);
        }

        if (settings.isDurability() && item.isDamaged()) {
            if (!isBrokenElytra(item, slot, settings)) {
                item.setDamageValue(0);
            }
        }

        if (settings.isEnchantments() && item.isEnchanted()) {
            item.setEnchantments(getDefaultEnchants());
        }
    }

    private boolean isBrokenElytra(ItemStack item, EquipmentSlot slot, Settings.Items settings) {
        if (item.getType() != ItemTypes.ELYTRA) return false;
        boolean broken = item.getDamageValue() >= item.getMaxDamage() - 1;
        boolean allowedSlot = slot == EquipmentSlot.MAIN_HAND || slot == EquipmentSlot.OFF_HAND || slot == EquipmentSlot.HELMET;
        return settings.isBrokenElytra() && broken && allowedSlot;
    }

    private static List<Enchantment> getDefaultEnchants() {
        return LazyHolder.DEFAULT_ENCHANTS;
    }

    private static class LazyHolder {
        private static final List<Enchantment> DEFAULT_ENCHANTS = Collections.singletonList(
                Enchantment.builder()
                        .type(EnchantmentTypes.BLOCK_FORTUNE)
                        .level(3)
                        .build()
        );
    }
}
