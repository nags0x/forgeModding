package net.nags.tutorialmod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nags.tutorialmod.TutorialMod;
import net.nags.tutorialmod.entity.custom.DinoEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<EntityType<DinoEntity>> DINO =
            ENTITY_TYPES.register("dino",
                    () -> EntityType.Builder.<DinoEntity>of(DinoEntity::new, MobCategory.CREATURE)
                            .sized(1.5f, 1.5f)
                            .build("dino"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
