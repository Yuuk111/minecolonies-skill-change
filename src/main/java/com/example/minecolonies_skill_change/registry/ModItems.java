package com.example.minecolonies_skill_change.registry;

import com.example.minecolonies_skill_change.SkillChangeMod;
import com.example.minecolonies_skill_change.item.SkillTunerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SkillChangeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModItems
{
    private ModItems()
    {
    }

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SkillChangeMod.MODID);

    public static final RegistryObject<Item> SKILL_TUNER = ITEMS.register("skill_tuner", SkillTunerItem::new);

    public static void register(final IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }

    @SubscribeEvent
    public static void addToCreativeTab(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(SKILL_TUNER.get());
        }
    }
}
