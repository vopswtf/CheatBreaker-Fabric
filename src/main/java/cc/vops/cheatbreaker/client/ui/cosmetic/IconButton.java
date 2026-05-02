package cc.vops.cheatbreaker.client.ui.cosmetic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@AllArgsConstructor
@Getter
public class IconButton {
    private final Object object;
    private final String name;
    private final Identifier image;
}
