package net.nags.tutorialmod.entity.client;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.nags.tutorialmod.TutorialMod;
import net.nags.tutorialmod.entity.custom.DinoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public  class DinoRenderer extends MobRenderer<DinoEntity, DinoModel<DinoEntity>> {

    public DinoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new DinoModel<>(pContext.bakeLayer(DinoModel.LAYER_LOCATION)), 0.85f);
    }

    @Override
    public ResourceLocation getTextureLocation(DinoEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID,  "textures/entity/dino/dino_gray.png");
    }

    @Override
    public void render(DinoEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

        if(pEntity.isBaby()){
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            pPoseStack.scale(1f, 1f, 1f);

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
