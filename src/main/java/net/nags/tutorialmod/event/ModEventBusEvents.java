package net.nags.tutorialmod.event;

import net.nags.tutorialmod.TutorialMod;
import net.nags.tutorialmod.entity.ModEntities;
import net.nags.tutorialmod.entity.client.DinoModel;
import net.nags.tutorialmod.entity.custom.DinoEntity;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DinoModel.LAYER_LOCATION, DinoModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DINO.get(), DinoEntity.createAttributes().build());
    }
}
