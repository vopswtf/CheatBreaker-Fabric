package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.client.event.EventBus;
import cc.vops.cheatbreaker.client.event.data.KeyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.input.KeyEvent;

@Getter
@RequiredArgsConstructor
public class KeyboardEvent extends EventBus.Event {
    private final KeyEvent keyEvent;
}
