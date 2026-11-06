package com.deathmotion.antihealthindicator.packets;

import com.deathmotion.antihealthindicator.AHIPlatform;
import com.deathmotion.antihealthindicator.cache.entities.CachedEntity;
import com.deathmotion.antihealthindicator.managers.ConfigManager;
import com.deathmotion.antihealthindicator.managers.PlayerDataManager;
import com.deathmotion.antihealthindicator.models.AHIPlayer;
import com.deathmotion.antihealthindicator.models.Settings;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.injector.ChannelInjector;
import com.github.retrooper.packetevents.manager.server.ServerManager;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.enchantment.Enchantment;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.github.retrooper.packetevents.util.EventCreationUtil;
import com.github.retrooper.packetevents.util.LogManager;
import com.github.retrooper.packetevents.util.PacketEventsImplHelper;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.impl.netty.NettyManagerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Sends real packets through the real PacketEvents send path, then checks what would reach the client.
 */
class SkipUnchangedPacketsTest {

    private static final ClientVersion CLIENT = ClientVersion.V_1_21_5;
    private static final ServerVersion SERVER = ServerVersion.V_1_21_5;
    private static final int SELF = 7;
    private static final int OTHER = 42;

    // HealthUtil keeps the first ConfigManager it sees in a static field, so all tests share this one.
    private static final ConfigManager<?> CONFIG = mock(ConfigManager.class);

    private final Object channel = new Object();
    private Settings settings;
    private EventManager eventManager;
    private User user;
    private AHIPlayer player;

    @BeforeEach
    void setUp() throws Exception {
        settings = new Settings();
        settings.getEntityData().setRandomizeHealth(false);
        doReturn(settings).when(CONFIG).getSettings();

        eventManager = new EventManager();
        ServerManager serverManager = mock(ServerManager.class);
        when(serverManager.getVersion()).thenReturn(SERVER);

        PacketEventsAPI<?> api = mock(PacketEventsAPI.class);
        when(api.getSettings()).thenReturn(new PacketEventsSettings().reEncodeByDefault(true));
        when(api.getEventManager()).thenReturn(eventManager);
        when(api.getServerManager()).thenReturn(serverManager);
        when(api.getNettyManager()).thenReturn(new NettyManagerImpl());
        when(api.getInjector()).thenReturn(mock(ChannelInjector.class));
        when(api.getLogManager()).thenReturn(mock(LogManager.class));
        PacketEvents.setAPI(api);

        AHIPlatform<Object> platform = platformMock();
        setInstance(platform);

        user = new User(channel, ConnectionState.PLAY, CLIENT, new UserProfile(UUID.randomUUID(), "viewer"));
        user.setEntityId(SELF);
        player = new AHIPlayer(user);
        player.entityCache.addLivingEntity(OTHER, new CachedEntity(EntityTypes.PLAYER));

        PlayerDataManager<Object> playerData = playerDataMock();
        when(playerData.getPlayer(any())).thenReturn(player);
        when(platform.getPlayerDataManager()).thenReturn(playerData);

        eventManager.registerListener(new SpoofManagerPacketListener<>(platform));
    }

    @SuppressWarnings("unchecked")
    private static AHIPlatform<Object> platformMock() {
        AHIPlatform<Object> platform = mock(AHIPlatform.class);
        when(platform.getConfigManager()).thenReturn((ConfigManager<Object>) CONFIG);
        when(platform.isProxy()).thenReturn(false);
        return platform;
    }

    @SuppressWarnings("unchecked")
    private static PlayerDataManager<Object> playerDataMock() {
        return mock(PlayerDataManager.class);
    }

    private static void setInstance(AHIPlatform<?> platform) throws Exception {
        Field field = AHIPlatform.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, platform);
    }

    // Equipment

    @Test
    void equipmentWithNothingToHideKeepsOriginalBytes() throws Exception {
        assertOriginalBytesKept(equipment(OTHER, named(ItemTypes.DIAMOND_SWORD), ItemStack.EMPTY));
    }

    @Test
    void equipmentStackAmountIsHidden() throws Exception {
        Sent sent = send(equipment(OTHER, stack(5)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(1, decodeItem(sent).getAmount());
    }

    @Test
    void equipmentDurabilityIsHidden() throws Exception {
        Sent sent = send(equipment(OTHER, damaged(ItemTypes.DIAMOND_SWORD, 5)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(0, decodeItem(sent).getDamageValue());
    }

    @Test
    void equipmentEnchantmentsAreHidden() throws Exception {
        Sent sent = send(equipment(OTHER, enchanted()));
        assertTrue(sent.event.needsReEncode());
        List<Enchantment> enchantments = decodeItem(sent).getEnchantments();
        assertEquals(1, enchantments.size());
        assertEquals(EnchantmentTypes.BLOCK_FORTUNE, enchantments.get(0).getType());
        assertEquals(3, enchantments.get(0).getLevel());
    }

    @Test
    void brokenElytraInHandStaysBrokenAndKeepsOriginalBytes() throws Exception {
        ItemStack elytra = damaged(ItemTypes.ELYTRA, 431);
        elytra.setComponent(ComponentTypes.MAX_DAMAGE, 432);

        assertOriginalBytesKept(equipment(OTHER, elytra));
    }

    @Test
    void brokenElytraInChestSlotIsStillRepaired() throws Exception {
        ItemStack elytra = damaged(ItemTypes.ELYTRA, 431);
        elytra.setComponent(ComponentTypes.MAX_DAMAGE, 432);

        Sent sent = send(new WrapperPlayServerEntityEquipment(OTHER,
                Collections.singletonList(new Equipment(EquipmentSlot.CHEST_PLATE, elytra))));
        assertTrue(sent.event.needsReEncode());
        assertEquals(0, decodeItem(sent).getDamageValue());
    }

    @Test
    void oneSpoofedItemAmongPlainOnesIsEnough() throws Exception {
        Sent sent = send(equipment(OTHER, named(ItemTypes.STONE), stack(5)));
        assertTrue(sent.event.needsReEncode());

        List<Equipment> items = decode(sent, WrapperPlayServerEntityEquipment::new).getEquipment();
        assertEquals(1, items.get(1).getItem().getAmount());
    }

    @Test
    void lastItemUnchangedDoesNotHideAnEarlierChange() throws Exception {
        Sent sent = send(equipment(OTHER, stack(5), named(ItemTypes.STONE)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(1, decodeItem(sent).getAmount());
    }

    @Test
    void disabledItemOptionsLeaveTheValuesAlone() throws Exception {
        settings.getItems().setStackAmount(false);

        assertOriginalBytesKept(equipment(OTHER, stack(5)));
    }

    @Test
    void equipmentAboutTheViewerKeepsTheDefault() throws Exception {
        Sent sent = send(equipment(SELF, named(ItemTypes.STONE)));
        assertTrue(sent.event.needsReEncode());
        assertNotNull(sent.event.getLastUsedWrapper());
    }

    @Test
    void equipmentThatThisPluginDidNotReadKeepsTheDefault() throws Exception {
        settings.getItems().setEnabled(false);

        Sent sent = send(equipment(OTHER, stack(5)));
        assertTrue(sent.event.needsReEncode());
    }

    @Test
    void editByAnEarlierListenerIsNotDropped() throws Exception {
        listener(PacketListenerPriority.LOWEST, event -> {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            packet.getEquipment().get(0).getItem().setComponent(ComponentTypes.ITEM_NAME, Component.text("earlier"));
        });

        Sent sent = send(equipment(OTHER, named(ItemTypes.STONE)));
        assertEquals(Component.text("earlier"), decodeItem(sent).getComponentOr(ComponentTypes.ITEM_NAME, null));
    }

    @Test
    void laterListenerThatMarksItsEditIsNotDropped() throws Exception {
        listener(PacketListenerPriority.NORMAL, event -> {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            packet.getEquipment().get(0).getItem().setComponent(ComponentTypes.ITEM_NAME, Component.text("later"));
            event.markForReEncode(true);
        });

        Sent sent = send(equipment(OTHER, named(ItemTypes.STONE)));
        assertEquals(Component.text("later"), decodeItem(sent).getComponentOr(ComponentTypes.ITEM_NAME, null));
    }

    @Test
    void failureInsideASpooferRestoresTheDefault() throws Exception {
        boolean[] seen = new boolean[1];
        doThrow(new IllegalStateException("boom")).when(CONFIG).getSettings();
        listener(PacketListenerPriority.NORMAL, event -> seen[0] = event.needsReEncode());

        send(equipment(OTHER, named(ItemTypes.STONE)));
        assertTrue(seen[0]);
    }

    // Metadata

    @Test
    void metadataOfUncachedEntityKeepsOriginalBytes() throws Exception {
        assertOriginalBytesKept(metadata(1234, new EntityData<>(8, EntityDataTypes.ITEMSTACK, named(ItemTypes.STONE))));
    }

    @Test
    void metadataWithoutSpoofedFieldsKeepsOriginalBytes() throws Exception {
        assertOriginalBytesKept(metadata(OTHER, new EntityData<>(0, EntityDataTypes.BYTE, (byte) 2)));
    }

    @Test
    void healthIsHidden() throws Exception {
        Sent sent = send(metadata(OTHER, new EntityData<>(player.metadataIndex.HEALTH, EntityDataTypes.FLOAT, 3f)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(20f, decodeValue(sent, 0));
    }

    @Test
    void ironGolemHealthIsMovedToAVisibleStage() throws Exception {
        player.entityCache.addLivingEntity(50, new CachedEntity(EntityTypes.IRON_GOLEM));

        Sent sent = send(metadata(50, new EntityData<>(player.metadataIndex.HEALTH, EntityDataTypes.FLOAT, 12f)));
        assertTrue(sent.event.needsReEncode());
        assertNotEquals(12f, decodeValue(sent, 0));
    }

    @Test
    void absorptionIsHidden() throws Exception {
        Sent sent = send(metadata(OTHER, new EntityData<>(player.metadataIndex.ABSORPTION, EntityDataTypes.FLOAT, 4f)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(0f, decodeValue(sent, 0));
    }

    @Test
    void experienceIsHidden() throws Exception {
        Sent sent = send(metadata(OTHER, new EntityData<>(player.metadataIndex.XP, EntityDataTypes.INT, 900)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(0, decodeValue(sent, 0));
    }

    @Test
    void airTicksAreHidden() throws Exception {
        Sent sent = send(metadata(OTHER, new EntityData<>(player.metadataIndex.AIR_TICKS, EntityDataTypes.INT, 100)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(1, decodeValue(sent, 0));
    }

    @Test
    void valuesThatAlreadyMatchTheSpoofKeepOriginalBytes() throws Exception {
        assertOriginalBytesKept(metadata(OTHER,
                new EntityData<>(player.metadataIndex.ABSORPTION, EntityDataTypes.FLOAT, 0f),
                new EntityData<>(player.metadataIndex.XP, EntityDataTypes.INT, 0),
                new EntityData<>(player.metadataIndex.AIR_TICKS, EntityDataTypes.INT, 1),
                new EntityData<>(player.metadataIndex.HEALTH, EntityDataTypes.FLOAT, 20f)));
    }

    @Test
    void oneSpoofedFieldAmongEqualOnesIsEnough() throws Exception {
        Sent sent = send(metadata(OTHER,
                new EntityData<>(player.metadataIndex.XP, EntityDataTypes.INT, 900),
                new EntityData<>(player.metadataIndex.ABSORPTION, EntityDataTypes.FLOAT, 0f)));
        assertTrue(sent.event.needsReEncode());
        assertEquals(0, decodeValue(sent, 0));
    }

    @Test
    void inPlaceEditOfTheViewersOwnMetadataStillReachesTheClient() throws Exception {
        // LibsDisguises does this for self disguises and never marks the event.
        listener(PacketListenerPriority.NORMAL, event -> {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
            setFirstValue(packet, (byte) 32);
        });

        Sent sent = send(metadata(SELF, new EntityData<>(0, EntityDataTypes.BYTE, (byte) 2)));
        assertEquals((byte) 32, decodeValue(sent, 0));
    }

    @Test
    void metadataThatThisPluginDidNotReadKeepsTheDefault() throws Exception {
        settings.getEntityData().setEnabled(false);

        Sent sent = send(metadata(OTHER, new EntityData<>(player.metadataIndex.HEALTH, EntityDataTypes.FLOAT, 3f)));
        assertTrue(sent.event.needsReEncode());
    }

    // Helpers

    @SuppressWarnings("unchecked")
    private static void setFirstValue(WrapperPlayServerEntityMetadata packet, byte value) {
        ((EntityData<Byte>) packet.getEntityMetadata().get(0)).setValue(value);
    }

    private void assertOriginalBytesKept(PacketWrapper<?> packet) throws Exception {
        Sent sent = send(packet);
        assertFalse(sent.event.needsReEncode(), "packet should not be re-encoded");
        assertNull(sent.event.getLastUsedWrapper(), "wrapper should be dropped");
        assertEquals(sent.inputHex, ByteBufUtil.hexDump(sent.output));
    }

    private void listener(PacketListenerPriority priority, Consumer<PacketSendEvent> action) {
        eventManager.registerListener(new PacketListenerAbstract(priority) {
            @Override
            public void onPacketSend(PacketSendEvent event) {
                action.accept(event);
            }
        });
    }

    private Sent send(PacketWrapper<?> packet) throws Exception {
        final ByteBuf buffer = Unpooled.buffer();
        packet.setBuffer(buffer);
        packet.writeVarInt(packet.getPacketTypeData().getPacketType().getId(CLIENT));
        packet.write();

        final String inputHex = ByteBufUtil.hexDump(buffer);
        final PacketSendEvent event = PacketEventsImplHelper.handleClientBoundPacket(channel, user, null, buffer, false);
        return new Sent(event, inputHex, buffer);
    }

    private <T extends PacketWrapper<?>> T decode(Sent sent, java.util.function.Function<PacketSendEvent, T> reader) throws Exception {
        final ByteBuf copy = sent.output.copy();
        return reader.apply(EventCreationUtil.createSendEvent(channel, user, null, copy, false));
    }

    private ItemStack decodeItem(Sent sent) throws Exception {
        return decode(sent, WrapperPlayServerEntityEquipment::new).getEquipment().get(0).getItem();
    }

    private Object decodeValue(Sent sent, int index) throws Exception {
        return decode(sent, WrapperPlayServerEntityMetadata::new).getEntityMetadata().get(index).getValue();
    }

    private static WrapperPlayServerEntityEquipment equipment(int entityId, ItemStack... items) {
        final EquipmentSlot[] slots = {EquipmentSlot.MAIN_HAND, EquipmentSlot.OFF_HAND, EquipmentSlot.HELMET};
        final List<Equipment> list = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            list.add(new Equipment(slots[i], items[i]));
        }
        return new WrapperPlayServerEntityEquipment(entityId, list);
    }

    private static WrapperPlayServerEntityMetadata metadata(int entityId, EntityData<?>... data) {
        return new WrapperPlayServerEntityMetadata(entityId, new ArrayList<>(Arrays.asList(data)));
    }

    private static ItemStack named(com.github.retrooper.packetevents.protocol.item.type.ItemType type) {
        return ItemStack.builder().type(type).amount(1).component(ComponentTypes.ITEM_NAME, Component.text("plain")).build();
    }

    private static ItemStack stack(int amount) {
        return ItemStack.builder().type(ItemTypes.STONE).amount(amount).build();
    }

    private static ItemStack damaged(com.github.retrooper.packetevents.protocol.item.type.ItemType type, int damage) {
        return ItemStack.builder().type(type).amount(1).component(ComponentTypes.DAMAGE, damage).build();
    }

    private static ItemStack enchanted() {
        ItemStack item = ItemStack.builder().type(ItemTypes.DIAMOND_SWORD).amount(1).build();
        item.setEnchantments(Collections.singletonList(
                Enchantment.builder().type(EnchantmentTypes.SHARPNESS).level(5).build()));
        return item;
    }

    private static final class Sent {
        private final PacketSendEvent event;
        private final String inputHex;
        private final ByteBuf output;

        private Sent(PacketSendEvent event, String inputHex, ByteBuf output) {
            this.event = event;
            this.inputHex = inputHex;
            this.output = output;
        }
    }
}
