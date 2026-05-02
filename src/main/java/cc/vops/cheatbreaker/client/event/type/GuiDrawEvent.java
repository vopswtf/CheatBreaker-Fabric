package cc.vops.cheatbreaker.client.event.type;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.event.EventBus;
import cc.vops.cheatbreaker.client.ui.AbstractGui;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

@Getter
public class GuiDrawEvent extends EventBus.Event {
    private final GuiGraphicsExtractor graphics;
    @Nullable
    private final DeltaTracker deltaTracker;
    private final int scaledWidth;
    private final int scaledHeight;

    public GuiDrawEvent(GuiGraphicsExtractor graphics,  @Nullable DeltaTracker deltaTracker) {
        this.graphics = graphics;
        this.deltaTracker = deltaTracker;
        float scale = CheatBreaker.getScaleFactor();
        this.scaledWidth = (int) (this.graphics.guiWidth() / scale);
        this.scaledHeight = (int) (this.graphics.guiHeight() / scale);

    }
}
