package cc.vops.cheatbreaker.client.util.cosmetic.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.joml.Vector3f;

public class WingsModel extends EntityModel<AvatarRenderState> {

    public final ModelPart root;
    public final ModelPart leftWing;
    public final ModelPart leftWingTip;
    public final ModelPart rightWing;
    public final ModelPart rightWingTip;

    public WingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftWing = root.getChild("left_wing");
        this.leftWingTip = leftWing.getChild("left_wing_tip");
        this.rightWing = root.getChild("right_wing");
        this.rightWingTip = rightWing.getChild("right_wing_tip");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        int gap = 16;

        // LEFT WING
        PartDefinition leftWing = root.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create()
                        .mirror()
                        .addBox("bone", 0, -4, -4, 56, 8, 8, 112, 88)
                        .addBox("skin", 0, 0, 2, 56, 0, 56, -56, 88),
                PartPose.offset(gap, 0, 2)
        );

        leftWing.addOrReplaceChild(
                "left_wing_tip",
                CubeListBuilder.create()
                        .mirror()
                        .addBox("bone", 0, -2, -2, 56, 4, 4, 112, 136)
                        .addBox("skin", 0, 0, 2, 56, 0, 56, -56, 144),
                PartPose.offset(56, 0, 0)
        );

        // RIGHT WING
        PartDefinition rightWing = root.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create()
                        .addBox("bone", -56, -4, -4, 56, 8, 8, 112, 88)
                        .addBox("skin", -56, 0, 2, 56, 0, 56, -56, 88),
                PartPose.offset(-gap, 0, 2)
        );

        rightWing.addOrReplaceChild(
                "right_wing_tip",
                CubeListBuilder.create()
                        .addBox("bone", -56, -2, -2, 56, 4, 4, 112, 136)
                        .addBox("skin", -56, 0, 2, 56, 0, 56, -56, 144),
                PartPose.offset(-56, 0, 0)
        );

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(AvatarRenderState state) {
        float f8 = (float) (System.currentTimeMillis() % 2000L) / 2000.0f * (float) Math.PI * 2.0f;

        leftWing.xRot = 1.1f * -0.11363636f - (float) Math.cos(f8) * (0.175f * 1.1428572f);
        leftWing.yRot = -1.069853f * 0.7010309f;
        leftWing.zRot = (float) (Math.sin(f8) + 0.125F) * 0.8F;
        leftWingTip.zRot = (float) (Math.sin(f8 + 2.0f) + 0.5) * 0.75f;

        rightWing.xRot = leftWing.xRot;
        rightWing.yRot = -leftWing.yRot;
        rightWing.zRot = -leftWing.zRot;
        rightWingTip.zRot = -leftWingTip.zRot;
    }
}
