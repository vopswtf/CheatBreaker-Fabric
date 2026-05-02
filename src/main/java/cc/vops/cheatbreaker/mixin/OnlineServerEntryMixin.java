package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.SelectableEntry;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin extends ServerSelectionList.Entry implements SelectableEntry {
    @Shadow @Final private ServerData serverData;
    private Identifier starIcon = CheatBreaker.asset("icons/star-64.png");
    private Identifier cbIcon = CheatBreaker.asset("icons/cb.png");

    @Inject(method = "extractContent", at = @At("HEAD"))
    private void renderPinnedIcon(GuiGraphicsExtractor GuiGraphicsExtractor, int i, int j, boolean bl, float f, CallbackInfo ci) {
        if (CheatBreaker.getInstance().getGlobalSettings().pinnedServers.stream().noneMatch(pinned -> pinned[1].equals(serverData.ip))) {
            return;
        }

        int color = CheatBreaker.getColor(1.0f, 0.40909088f * 2.2f, 0.0f, 1.0f);

        int x = this.getContentX();
        int y = this.getContentY();

        RenderUtil.drawIcon(
                GuiGraphicsExtractor,
                this.starIcon,
                (float)5,
                (float)(x - 17),
                (float)(y + (Boolean.FALSE ? 4 : 12)),
                color
        );
    }
}
