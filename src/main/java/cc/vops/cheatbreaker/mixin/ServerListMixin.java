package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerSelectionList.class)
public class ServerListMixin extends ObjectSelectionList<ServerSelectionList.Entry> {

    @Shadow @Final private JoinMultiplayerScreen screen;

    public ServerListMixin(Minecraft minecraft, int i, int j, int k, int l) {
        super(minecraft, i, j, k, l);
    }

    @ModifyExpressionValue(method = "refreshEntries", at = @At(value = "NEW", target = "(Ljava/util/Collection;)Ljava/util/ArrayList;"))
    private ArrayList<ServerSelectionList.Entry> showPinnedServers(ArrayList<ServerSelectionList.Entry> original) {
        ArrayList<ServerSelectionList.Entry> entries = new ArrayList<>();
        try {
            var constructor = ServerSelectionList.OnlineServerEntry.class.getDeclaredConstructor(
                    ServerSelectionList.class,
                    JoinMultiplayerScreen.class,
                    ServerData.class
            );
            constructor.setAccessible(true);

            for (String[] pinnedServer : CheatBreaker.getInstance().getGlobalSettings().pinnedServers.reversed()) {
                entries.add(constructor.newInstance(
                        (ServerSelectionList)(Object)this,
                        screen,
                        new ServerData(pinnedServer[0], pinnedServer[1], ServerData.Type.OTHER)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // hide pinned in original list just incase they are a huge fan
        for (ServerSelectionList.Entry entry : original) {
            if (entry instanceof ServerSelectionList.OnlineServerEntry onlineEntry) {
                String ip = onlineEntry.getServerData().ip;
                boolean isPinned = CheatBreaker.getInstance().getGlobalSettings().pinnedServers.stream().anyMatch(pinned -> pinned[1].equals(ip));
                if (!isPinned) {
                    entries.add(entry);
                }
            } else {
                entries.add(entry);
            }
        }

        return entries;
    }
}
