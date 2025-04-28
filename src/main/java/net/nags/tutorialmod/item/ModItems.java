package net.nags.tutorialmod.item;

import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.nags.tutorialmod.TutorialMod;
import net.nags.tutorialmod.entity.ModEntities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);

    public static final RegistryObject<Item> DINO_SPAWN_EGG = ITEMS.register("dino_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DINO, 0x53524b, 0xdac741, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


    public static final RegistryObject<Item> GRASS_ITEM = ITEMS.register("grass_item", () -> new Item(new Item.Properties()));
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.GRASS_ITEM.get());
        }
    }

}
