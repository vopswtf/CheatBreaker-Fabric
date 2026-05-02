package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.client.event.EventBus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RenderWorldEvent extends EventBus.Event {
    private final float partialTicks;
}
