package com.example.minecolonies_skill_change;

import com.example.minecolonies_skill_change.network.ModNetwork;
import com.example.minecolonies_skill_change.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SkillChangeMod.MODID)
public class SkillChangeMod
{
    public static final String MODID = "minecolonies_skill_change";

    public SkillChangeMod()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModNetwork.init();
    }
}
