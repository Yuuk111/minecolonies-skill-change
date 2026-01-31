package com.example.minecolonies_skill_change.network;

import com.example.minecolonies_skill_change.util.MineColoniesBridge;
import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AdjustCitizenSkillMessage
{
    private final int entityId;
    private final String skillName;
    private final int delta;

    public AdjustCitizenSkillMessage(final int entityId, final String skillName, final int delta)
    {
        this.entityId = entityId;
        this.skillName = skillName;
        this.delta = delta;
    }

    public static void encode(final AdjustCitizenSkillMessage message, final FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeUtf(message.skillName);
        buffer.writeInt(message.delta);
    }

    public static AdjustCitizenSkillMessage decode(final FriendlyByteBuf buffer)
    {
        return new AdjustCitizenSkillMessage(buffer.readInt(), buffer.readUtf(), buffer.readInt());
    }

    public static void handle(final AdjustCitizenSkillMessage message, final Supplier<NetworkEvent.Context> context)
    {
        final NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            final ServerPlayer sender = ctx.getSender();
            if (sender == null)
            {
                return;
            }

            final Entity entity = sender.level().getEntity(message.entityId);
            if (entity == null)
            {
                return;
            }

            final Skill skill;
            try
            {
                skill = Skill.valueOf(message.skillName);
            }
            catch (final IllegalArgumentException ex)
            {
                return;
            }

            MineColoniesBridge.adjustSkill(sender, entity, skill, message.delta);
        });
        ctx.setPacketHandled(true);
    }
}
