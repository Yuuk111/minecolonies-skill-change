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

    //30-50
    public static final RegistryObject<Item> KNOWLEDGE_SCROLL_FAINT = ITEMS.register("knowledge_scroll_faint", () -> new SkillTunerItem(30, 50));
    //50-70
    public static final RegistryObject<Item> KNOWLEDGE_SCROLL_DIM = ITEMS.register("knowledge_scroll_dim", () -> new SkillTunerItem(50, 70));
    //70-80
    public static final RegistryObject<Item> KNOWLEDGE_SCROLL_MELLOW = ITEMS.register("knowledge_scroll_mellow", () -> new SkillTunerItem(70, 80));
    //80-90
    public static final RegistryObject<Item> KNOWLEDGE_SCROLL_VIVID = ITEMS.register("knowledge_scroll_vivid", () -> new SkillTunerItem(80, 90));
    //90-99
    public static final RegistryObject<Item> KNOWLEDGE_SCROLL_INTENSE = ITEMS.register("knowledge_scroll_intense", () -> new SkillTunerItem(90, 99));
    public static void register(final IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }

    @SubscribeEvent
    public static void addToCreativeTab(final BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(KNOWLEDGE_SCROLL_FAINT.get());
            event.accept(KNOWLEDGE_SCROLL_DIM.get());
            event.accept(KNOWLEDGE_SCROLL_MELLOW.get());
            event.accept(KNOWLEDGE_SCROLL_VIVID.get());
            event.accept(KNOWLEDGE_SCROLL_INTENSE.get());
        }
    }
}
