package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.client.event.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@RequiredArgsConstructor @Getter
public class CustomPacketPayloadEvent extends EventBus.Event {
    private final CustomPacketPayload payload;
}
