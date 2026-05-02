package cc.vops.cheatbreaker.client.event.type;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class RenderPreviewEvent extends GuiDrawEvent {
    public RenderPreviewEvent(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        super(graphics, deltaTracker);
    }
}
