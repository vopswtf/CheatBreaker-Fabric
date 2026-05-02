package cc.vops.cheatbreaker.mixin;

import cc.vops.cheatbreaker.CheatBreaker;
import cc.vops.cheatbreaker.client.util.cosmetic.Cosmetic;
import cc.vops.cheatbreaker.client.util.cosmetic.CosmeticModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
    @Shadow protected abstract boolean hasLayer(ItemStack itemStack, EquipmentClientInfo.LayerType layerType);

    @Shadow @Final private HumanoidModel<AvatarRenderState> model;

    @Inject(method = "submit*", at = @At("HEAD"), cancellable = true)
    private void onSubmit(PoseStack pose, SubmitNodeCollector p_430860_, int p_427257_, AvatarRenderState p_428454_, float p_429917_, float p_424453_, CallbackInfo ci) {
        ci.cancel();
        if (Minecraft.getInstance().level == null) return;
        var entity = Minecraft.getInstance().level.getEntity(p_428454_.id);
        if (entity == null) return;

        if (!p_428454_.isInvisible && p_428454_.showCape) {
            PlayerSkin playerskin = p_428454_.skin;
            Cosmetic cape = CheatBreaker.getInstance().getActiveCosmetic(Cosmetic.CosmeticType.CAPE, entity.getUUID());

            if (playerskin.cape() != null || cape != null) {
                if (!this.hasLayer(p_428454_.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
                    pose.pushPose();
                    if (this.hasLayer(p_428454_.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                        pose.translate(0.0F, -0.053125F, 0.06875F);
                    }

                    p_430860_.submitModel(
                            this.model,
                            p_428454_,
                            pose,
                            RenderTypes.entitySolid(
                                    cape != null ? cape.getLocation() : playerskin.cape().texturePath()
                            ),
                            p_427257_,
                            OverlayTexture.NO_OVERLAY,
                            p_428454_.outlineColor,
                            null
                    );
                    pose.popPose();
                }
            }
        }

        Cosmetic wings = CheatBreaker.getInstance().getActiveCosmetic(Cosmetic.CosmeticType.WINGS, entity.getUUID());
        if (wings != null && !p_428454_.isInvisible && !hasLayer(p_428454_.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
            pose.pushPose();

            pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
            pose.translate(0.0F, 0.0F, 0.125F);
            pose.scale(wings.getScale(), wings.getScale(), wings.getScale());

            CosmeticModels.WINGS.setupAnim(p_428454_);

            p_430860_.submitModel(
                    CosmeticModels.WINGS,
                    p_428454_,
                    pose,
                    RenderTypes.entityTranslucent(wings.getLocation()),
                    p_427257_,
                    OverlayTexture.NO_OVERLAY,
                    p_428454_.outlineColor,
                    null
            );

            pose.scale(1.0F, 1.0F, 1.0F);
            pose.popPose();
        }
    }
}
