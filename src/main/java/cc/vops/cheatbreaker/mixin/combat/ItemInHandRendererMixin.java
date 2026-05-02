package cc.vops.cheatbreaker.mixin.combat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f);

    @Redirect(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V"
            )
    )
    private void redirectApplyItemArmTransform(
            ItemInHandRenderer instance,
            PoseStack poseStack,
            HumanoidArm arm,
            float equipProgress,
            AbstractClientPlayer player,
            float tickDelta,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float originalEquipProgress,
            PoseStack poseStack2,
            SubmitNodeCollector collector,
            int light
    ) {
        int dir = arm == HumanoidArm.RIGHT ? 1 : -1;

        if (
                player.isBlocking()
                        && stack.has(DataComponents.BLOCKS_ATTACKS)
                        && !(stack.getItem() instanceof ShieldItem)
        ) {
            poseStack.translate(dir * 0.56F, -0.52F, -0.72F);
            return;
        }

        applyItemArmTransform(poseStack, arm, equipProgress);
    }
}