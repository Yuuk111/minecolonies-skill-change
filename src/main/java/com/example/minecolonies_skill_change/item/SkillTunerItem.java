package com.example.minecolonies_skill_change.item;

import com.example.minecolonies_skill_change.network.AdjustCitizenSkillMessage;
import com.example.minecolonies_skill_change.network.ModNetwork;
import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SkillTunerItem extends Item
{
    private static final String NBT_SKILL = "SelectedSkill";

    public SkillTunerItem()
    {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide)
        {
            final boolean reverse = player.isShiftKeyDown();
            final Skill current = getSelectedSkill(stack);
            final Skill next = cycleSkill(current, reverse);
            setSelectedSkill(stack, next);
            player.displayClientMessage(Component.literal("Selected skill: " + next.getTranslationKey()), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult interactLivingEntity(final ItemStack stack, final Player player, final LivingEntity target, final InteractionHand hand)
    {
        if (player.level().isClientSide)
        {
            final Skill selected = getSelectedSkill(stack);
            final int delta = player.isShiftKeyDown() ? -1 : 1;
            ModNetwork.CHANNEL.sendToServer(new AdjustCitizenSkillMessage(target.getId(), selected.name(), delta));
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag)
    {
        final Skill selected = getSelectedSkill(stack);
        tooltip.add(Component.literal("Selected skill: " + selected.getTranslationKey()));
        tooltip.add(Component.literal("Right click air to cycle (sneak reverses)."));
        tooltip.add(Component.literal("Right click citizen to adjust (sneak lowers)."));
    }

    private static Skill getSelectedSkill(final ItemStack stack)
    {
        final CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_SKILL))
        {
            return Skill.values()[0];
        }

        try
        {
            return Skill.valueOf(tag.getString(NBT_SKILL));
        }
        catch (final IllegalArgumentException ignored)
        {
            return Skill.values()[0];
        }
    }

    private static void setSelectedSkill(final ItemStack stack, final Skill skill)
    {
        stack.getOrCreateTag().putString(NBT_SKILL, skill.name());
    }

    private static Skill cycleSkill(final Skill current, final boolean reverse)
    {
        final Skill[] values = Skill.values();
        final int index = current.ordinal();
        final int nextIndex = reverse ? index - 1 : index + 1;
        return values[Mth.positiveModulo(nextIndex, values.length)];
    }
}
