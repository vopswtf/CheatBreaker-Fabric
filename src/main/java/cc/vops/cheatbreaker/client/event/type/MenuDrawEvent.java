package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.EventBus;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

@Getter
public class MenuDrawEvent extends EventBus.Event {
    private final GuiGraphicsExtractor graphics;
    private final int scaledWidth;
    private final int scaledHeight;

    public MenuDrawEvent(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
        float scale = CheatBreaker.getScaleFactor();
        this.scaledWidth = (int) (this.graphics.guiWidth() / scale);
        this.scaledHeight = (int) (this.graphics.guiHeight() / scale);
    }
}
