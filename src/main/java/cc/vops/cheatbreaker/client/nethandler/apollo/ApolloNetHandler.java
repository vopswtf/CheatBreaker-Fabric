package cc.vops.cheatbreaker.client.nethandler.apollo;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.module.type.minimap.Waypoint;
import com.google.gson.JsonParser;
import com.google.protobuf.Any;
import com.lunarclient.apollo.common.v1.Component;
import com.lunarclient.apollo.common.v1.LunarClientVersion;
import com.lunarclient.apollo.common.v1.MinecraftVersion;
import com.lunarclient.apollo.common.v1.Uuid;
import com.lunarclient.apollo.nametag.v1.OverrideNametagMessage;
import com.lunarclient.apollo.nametag.v1.ResetNametagMessage;
import com.lunarclient.apollo.notification.v1.DisplayNotificationMessage;
import com.lunarclient.apollo.player.v1.EmbeddedCheckoutSupport;
import com.lunarclient.apollo.player.v1.PlayerHandshakeMessage;
import com.lunarclient.apollo.team.v1.ResetTeamMembersMessage;
import com.lunarclient.apollo.team.v1.UpdateTeamMembersMessage;
import com.lunarclient.apollo.waypoint.v1.DisplayWaypointMessage;
import com.lunarclient.apollo.waypoint.v1.RemoveWaypointMessage;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class ApolloNetHandler {
    public static final Identifier APOLLO_CHANNEL = Identifier.fromNamespaceAndPath("lunar", "apollo");
    private final HashMap<UUID, List<net.minecraft.network.chat.Component>> adventureNametagOverrides = new HashMap<>();
    @Setter
    private UpdateTeamMembersMessage currentTeam = null;


    public ApolloNetHandler() {
        PayloadTypeRegistry.serverboundPlay().register(ApolloPayload.PACKET_TYPE, ApolloPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ApolloPayload.PACKET_TYPE, ApolloPayload.STREAM_CODEC);

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            PlayerHandshakeMessage handshake = PlayerHandshakeMessage.newBuilder()
                    .setMinecraftVersion(MinecraftVersion.newBuilder().setEnum("v" + SharedConstants.getCurrentVersion().id().replaceAll("\\.", "_")).build())
                    .setLunarClientVersion(LunarClientVersion.newBuilder().setGitBranch("cheatbreaker").setGitCommit(getRandomCommit()).setSemver(CheatBreaker.getVersion()).build())
                    .setEmbeddedCheckoutSupport(EmbeddedCheckoutSupport.EMBEDDED_CHECKOUT_SUPPORT_UNSPECIFIED)
                    .build();

            Any wrapped = Any.pack(handshake);

            ClientPlayNetworking.send(new ApolloPayload(wrapped));
        });

        ClientPlayNetworking.registerGlobalReceiver(
                ApolloPayload.PACKET_TYPE,
                (payload, context) -> {
                    Any any = payload.protobuf();

                    try {
                        if (any.is(OverrideNametagMessage.class)) {
                            overrideNametagMessage(any.unpack(OverrideNametagMessage.class));
                        } else if (any.is(ResetNametagMessage.class)) {
                            this.adventureNametagOverrides.clear();
                        } else if (any.is(ResetNametagMessage.class)) {
                            this.adventureNametagOverrides.remove(convertApolloUUID(any.unpack(ResetNametagMessage.class).getPlayerUuid()));
                        } else if (any.is(DisplayNotificationMessage.class)) {
                            displayNotification(any.unpack(DisplayNotificationMessage.class));
                        } else if (any.is(UpdateTeamMembersMessage.class)) {
                            this.currentTeam = any.unpack(UpdateTeamMembersMessage.class);
                        } else if (any.is(ResetTeamMembersMessage.class)) {
                            this.currentTeam = null;
                        } else if (any.is(DisplayWaypointMessage.class)) {
                            displayWaypoint(any.unpack(DisplayWaypointMessage.class));
                        } else if (any.is(RemoveWaypointMessage.class)) {
                            removeWaypoint(any.unpack(RemoveWaypointMessage.class));
                        } else {
//                            CheatBreaker.LOGGER.info("todo: " + any.getTypeUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    // lol
    private String getRandomCommit() {
        StringBuilder sb = new StringBuilder();
        String chars = "abcdef0123456789";
        for (int i = 0; i < 7; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void displayWaypoint(DisplayWaypointMessage msg) {
        Waypoint existingWaypoint = CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage.getWaypoints().stream()
                .filter(wp -> wp.name().equals(msg.getName()))
                .findFirst()
                .orElse(null);

        Waypoint waypoint = new Waypoint(
                msg.getLocation().getWorld(),
                msg.getLocation().getX(),
                msg.getLocation().getY(),
                msg.getLocation().getZ(),
                new Color(msg.getColor().getColor()),
                msg.getName(),
                msg.getPreventRemoval(),
                msg.getHidden(),
                true
        );

        if (existingWaypoint != null) {
            CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage.replace(existingWaypoint, waypoint);
        } else {
            CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage.create(waypoint);
        }
    }

    private void removeWaypoint(RemoveWaypointMessage msg) {
        CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage.getWaypoints().stream()
                .filter(wp -> wp.name().equals(msg.getName()))
                .findFirst()
                .ifPresent(CheatBreaker.getInstance().getModuleManager().minmap.waypointStorage::remove);
    }

    private void overrideNametagMessage(OverrideNametagMessage msg) {
        UUID playerUuid = convertApolloUUID(msg.getPlayerUuid());

        List<String> nameTagLines;
        if (msg.getAdventureJsonLinesCount() > 0) {
            nameTagLines = msg.getAdventureJsonLinesList();
            this.adventureNametagOverrides.put(playerUuid, nameTagLines.stream().map(ApolloNetHandler::parseComponent).toList());
        }
    }

    private void displayNotification(DisplayNotificationMessage msg) {
        CheatBreaker.getInstance().getModuleManager().notifications.queueNotification(
                "info",
                parseComponent(msg.getDescriptionAdventureJsonLines()).getString(),
                TimeUnit.NANOSECONDS.toMillis(msg.getDisplayTime().getNanos())
        );
    }

    public boolean hasTeam() {
        return this.currentTeam != null && this.currentTeam.getMembersCount() > 0;
    }

    public static UUID convertApolloUUID(Uuid apolloUuid) {
        return new UUID(apolloUuid.getHigh64(), apolloUuid.getLow64());
    }

    public static net.minecraft.network.chat.Component parseComponent(String rawJson) {
        var element = JsonParser.parseString(rawJson);
        if (element.isJsonArray()) {
            MutableComponent root = net.minecraft.network.chat.Component.empty();

            for (var el : element.getAsJsonArray()) {
                var result = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, el);

                if (result.result().isPresent()) {
                    root.append(result.result().get());
                }
            }

            return root;
        }

        var result = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, element);

        if (result.result().isPresent()) {
            return result.result().get();
        }

        throw new IllegalArgumentException(result.error().get().message());
    }
}
