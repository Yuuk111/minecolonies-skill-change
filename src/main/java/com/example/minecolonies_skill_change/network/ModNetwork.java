package com.example.minecolonies_skill_change.network;

import com.example.minecolonies_skill_change.SkillChangeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public final class ModNetwork
{
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(SkillChangeMod.MODID, "main"),
        () -> PROTOCOL,
        PROTOCOL::equals,
        PROTOCOL::equals
    );

    private ModNetwork()
    {
    }

    public static void init()
    {
        CHANNEL.registerMessage(0, AdjustCitizenSkillMessage.class, AdjustCitizenSkillMessage::encode, AdjustCitizenSkillMessage::decode, AdjustCitizenSkillMessage::handle);
    }
}
