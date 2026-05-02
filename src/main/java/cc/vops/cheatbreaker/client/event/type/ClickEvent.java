package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.client.event.EventBus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClickEvent extends EventBus.Event {
    private final int mouseButton;
}
