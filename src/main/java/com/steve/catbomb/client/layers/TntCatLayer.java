package com.steve.catbomb.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.steve.catbomb.client.ClientTntCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class TntCatLayer extends RenderLayer<Cat, CatModel<Cat>> {

    public TntCatLayer(LivingEntityRenderer<Cat, CatModel<Cat>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight,
                       Cat cat, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        var catId = cat.getUUID();

        if (!ClientTntCache.hasTnt(catId))
            return;
        /*
        * 大猫：
        *
        * 位置 -0.3 0.7 0.4
        * 大小 0.6
        *
        * 坐下位置：-0.3 0.7 0.2
        * 大小 0.6
        *
        * 小猫:
        *
        * 位置：-0.15 1.1 0.2
        * 大小 0.3
        *
        * 坐下位置： -0.15 1.1 0.2
        * 坐下大小 0.3
        * */
        int light = packedLight;

        if (ClientTntCache.isLit(catId)) {
            long elapsed = System.currentTimeMillis() - ClientTntCache.getLitStartTime(catId);
            boolean bright = (elapsed / 200) % 2 == 0;
            if (bright) {
                light = 15728880;
            }
        }

        BakedModel tntModel = Minecraft.getInstance().getBlockRenderer()
                .getBlockModel(Blocks.TNT.defaultBlockState());

        poseStack.pushPose();
        if(cat.isBaby()){
            poseStack.scale(0.3F,0.3F,0.3F);
            poseStack.translate(-0.4F,3.3F,0.2F);
        } else {
            poseStack.scale(0.6F,0.6F,0.6F);
            if(cat.isOrderedToSit()){
                poseStack.translate(-0.5F,1.1F,0.2F);
            }else {
                poseStack.translate(-0.5F,1.1F,0.4F);
            }
        }
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        var vertexConsumer = bufferSource.getBuffer(net.minecraft.client.renderer.RenderType.solid());
        Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                .renderModel(poseStack.last(), vertexConsumer, null, tntModel,
                        1,1,1, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());

        poseStack.popPose();
    }
}