package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.nethandler.apollo.ApolloNetHandler;
import cc.vops.cheatbreaker.client.util.RenderUtil;
import com.lunarclient.apollo.button.v1.Button;
import com.lunarclient.apollo.button.v1.ButtonContentPart;
import com.lunarclient.apollo.button.v1.*;
import com.lunarclient.apollo.chat.v1.ChatButton;
import com.lunarclient.apollo.common.v1.*;
import com.lunarclient.apollo.common.v1.Icon;
import com.lunarclient.apollo.common.v1.ItemStackIcon;
import com.lunarclient.apollo.hud.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onExtractRenderState(final GuiGraphicsExtractor graphics, final int mx, final int my, final float a, CallbackInfo ci) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons().isEmpty()) {
            CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons().add(ChatButton.newBuilder()
                    .setButton(Button.newBuilder()
                            .setId("team-chat")
                            .setPosition(HudPosition.newBuilder().setX(0).setY(2).build())
                            .setSize(ButtonSize.newBuilder().setWidth(70).setHeight(16).build())
                            .setShape(ButtonShape.BUTTON_SHAPE_ROUNDED_SQUARE)
                            .setBackgroundColor(Color.newBuilder().setColor(0xFF000000).build())
                            .setBorderColor(Color.newBuilder().setColor(0xFF000000).build())
                            .setContent(ButtonContent.newBuilder()
                                    .addParts(ButtonContentPart.newBuilder()
                                            .setIcon(Icon.newBuilder()
                                                    .setItemStack(ItemStackIcon.newBuilder()
                                                            .setItemName("paper")
                                                    .build())
                                            .build()))
                                    .addParts(ButtonContentPart.newBuilder()
                                            .setAdventureJsonText("{\"text\":\"Team\",\"color\":\"white\"}")
                                            .build())
                                    .setScale(1.0F)
                                    .build())
                            .setTooltip(ButtonTooltip.newBuilder()
                                    .addAdventureJsonLines("{\"text\":\"Click to chat with your team!\",\"color\":\"yellow\"}")
                                    .build())
                            .setRunCommand("/channel team")
                            .build())
                    .build());
        }

        ScreenRectangle rectangle = ((Screen) (Object) this).getRectangle();
        if (CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons().isEmpty()) return;

        float scale = CheatBreaker.getScaleFactor();
        float mouseX = mx;
        float mouseY = my;

        int topRegion = rectangle.height() - 40;
        int bottomRegion = rectangle.height() - 14;

        int y = topRegion + 5;
        int x = 2;

        Font font = Minecraft.getInstance().font;

        graphics.pose().pushMatrix();
        graphics.enableScissor(2, topRegion, rectangle.width() - 2, bottomRegion);
        for (ChatButton chatButton : CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons()) {
            if (!chatButton.hasButton()) continue;
            Button button = chatButton.getButton();
            boolean isHovering = mouseX >= x && mouseX <= x + button.getSize().getWidth() && mouseY >= y && mouseY <= y + button.getSize().getHeight();

            if (isHovering) {
                drawTooltip(graphics, button, mouseX, mouseY, button);
            }

            int background = Minecraft.getInstance().options.getBackgroundColor(Integer.MIN_VALUE);
            if (button.hasBackgroundColor()) background = button.getBackgroundColor().getColor();
            if (isHovering) {
                if (button.hasHoveredBackgroundColor()) background = button.getHoveredBackgroundColor().getColor();
                else background = (background & 0x00FFFFFF) | ((int)(((background >>> 24) & 0xFF) * 0.6) << 24);
            }

            if (button.getShape() == ButtonShape.BUTTON_SHAPE_ROUNDED_SQUARE) {
                RenderUtil.drawRoundedRect(graphics, x, y, x + button.getSize().getWidth(), y + button.getSize().getHeight(), 4, background);
            } else if (button.getShape() == ButtonShape.BUTTON_SHAPE_CIRCLE) {
                RenderUtil.drawCircle(graphics, x + button.getSize().getWidth() / 2, y + button.getSize().getHeight() / 2, button.getSize().getWidth() / 2, background);
            }

            int btnWidth = (int) button.getSize().getWidth();

            int contentWidth = 0;

            for (ButtonContentPart part : button.getContent().getPartsList()) {
                if (part.hasIcon()) {
                    Icon icon = part.getIcon();

                    if (icon.hasItemStack()) {
                        contentWidth += 16 + 2;
                    }
                } else if (part.hasAdventureJsonText()) {
                    Component text = ApolloNetHandler.parseComponent(part.getAdventureJsonText());
                    contentWidth += font.width(text) + 2;
                }
            }

            int partX = x + 1 + (btnWidth - contentWidth) / 2;

            for (ButtonContentPart part : button.getContent().getPartsList()) {
                if (part.hasIcon()) {
                    Icon icon = part.getIcon();
                    if (icon.hasItemStack()) {
                        ItemStack stack = resolveItemStack(icon.getItemStack());
                        graphics.item(stack, partX, (int) (y + (button.getSize().getHeight() - 16) / 2));
                        partX += 16 + 2;
                    }
                } else if (part.hasAdventureJsonText()) {
                    Component text = ApolloNetHandler.parseComponent(part.getAdventureJsonText());
                    RenderUtil.drawString(graphics, font, text, partX, y + 2 + (button.getSize().getHeight() - font.lineHeight) / 2, -1);
                    partX += font.width(text) + 2;
                }
            }

            x += btnWidth + 4;
        }
        graphics.disableScissor();
        graphics.pose().popMatrix();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().player == null) return;
        if (CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons().isEmpty()) return;
        ScreenRectangle rectangle = ((Screen) (Object) this).getRectangle();

        int topRegion = rectangle.height() - 40;

        int y = topRegion + 5;
        int x = 2;

        for (ChatButton chatButton : CheatBreaker.getInstance().getApolloNetHandler().getCurrentChatButtons()) {
            if (!chatButton.hasButton()) continue;
            Button button = chatButton.getButton();
            boolean isHovering = event.x() >= x && event.x() <= x + button.getSize().getWidth() && event.y() >= y && event.y() <= y + button.getSize().getHeight();

            if (isHovering) {
                if (button.hasRunCommand()) {
                    String command = button.getRunCommand();
                    if (command.startsWith("/")) {
                        Minecraft.getInstance().player.connection.send(new ServerboundChatCommandPacket(command.substring(1)));
                    }
                }
                cir.setReturnValue(true);
                return;
            }

            x += (int) (button.getSize().getWidth() + 4);
        }

    }

    @Unique
    private void drawTooltip(GuiGraphicsExtractor graphics, Button button, float mouseX, float mouseY, Button button1) {
        if (button.hasTooltip() && button.getTooltip().getAdventureJsonLinesCount() > 0) {
            List<Component> lines = button.getTooltip().getAdventureJsonLinesList().stream().map(ApolloNetHandler::parseComponent).toList();

            graphics.setTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    lines,
                    Optional.empty(),
                    (int)mouseX, (int)mouseY
            );
        }
    }

    @Unique
    private static ItemStack resolveItemStack(ItemStackIcon icon) {
        Item item = Items.AIR;
        switch (icon.getItemCase()) {
            case ITEM_NAME -> {
                Identifier id = Identifier.withDefaultNamespace(icon.getItemName().toLowerCase());
                item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
            }
            case ITEM_ID -> item = BuiltInRegistries.ITEM.byId(icon.getItemId());
        }

        return new ItemStack(item);
    }
}
