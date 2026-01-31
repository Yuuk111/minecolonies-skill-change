package com.example.minecolonies_skill_change.util;

import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Method;
import java.util.List;

public final class MineColoniesBridge
{
    private MineColoniesBridge()
    {
    }

    public static void adjustSkill(final ServerPlayer player, final Entity entity, final Skill skill, final int delta)
    {
        if (!isCitizenEntity(entity))
        {
            return;
        }

        try
        {
            final Method getCitizenData = entity.getClass().getMethod("getCitizenData");
            final Object citizenData = getCitizenData.invoke(entity);
            if (citizenData == null)
            {
                return;
            }

            final Method getSkillHandler = citizenData.getClass().getMethod("getCitizenSkillHandler");
            final Object skillHandler = getSkillHandler.invoke(citizenData);
            if (skillHandler == null)
            {
                return;
            }

            if (!tryInvoke(skillHandler, skill, delta, List.of("alterSkillLevel", "changeSkillLevel", "incrementLevel")))
            {
                if (!tryInvoke(skillHandler, skill, delta, List.of("addSkill", "increaseSkillLevel")))
                {
                    return;
                }
            }
        }
        catch (final Exception ignored)
        {
            return;
        }
    }

    private static boolean isCitizenEntity(final Entity entity)
    {
        return entity.getClass().getName().contains("minecolonies");
    }

    private static boolean tryInvoke(final Object target, final Skill skill, final int delta, final List<String> methods)
    {
        for (final String name : methods)
        {
            try
            {
                final Method method = target.getClass().getMethod(name, Skill.class, int.class);
                method.invoke(target, skill, delta);
                return true;
            }
            catch (final Exception ignored)
            {
            }
        }
        return false;
    }
}
