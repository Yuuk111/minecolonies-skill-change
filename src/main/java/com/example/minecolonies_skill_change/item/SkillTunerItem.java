package com.example.minecolonies_skill_change.item;

//import com.example.minecolonies_skill_change.network.AdjustCitizenSkillMessage;
//import com.example.minecolonies_skill_change.network.ModNetwork;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.ICitizenDataView;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSkillHandler;
import com.minecolonies.core.Network;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.network.messages.server.colony.citizen.AdjustSkillCitizenMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

    /*
    * Right-click to change ability
    * */
    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
//        if (level.isClientSide)
//        {
        final boolean reverse = player.isShiftKeyDown();
        final Skill current = getSelectedSkill(stack);
        final Skill next = cycleSkill(current, reverse);
        setSelectedSkill(stack, next);
        player.displayClientMessage(Component.literal("Selected skill: " + next.name()), true);
//        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean onLeftClickEntity(final ItemStack stack, final Player player, final net.minecraft.world.entity.Entity target)
    {
         /*if (player.level().isClientSide)
        {

            final Skill selected = getSelectedSkill(stack);
            final int delta = player.isShiftKeyDown() ? -1 : 1;
            ModNetwork.CHANNEL.sendToServer(new AdjustCitizenSkillMessage(target.getId(), selected.name(), delta));


            Network.getNetwork().sendToServer(new AdjustSkillCitizenMessage(colony, citizen, 1, skill));
        }*/

        if (player.level().isClientSide) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("[Debug] Clicked: " + target.getClass().getSimpleName()), true);
        }
        if (target instanceof net.minecraft.world.entity.animal.Pig)
        {
            player.displayClientMessage(Component.literal("Don't attack llm"), true);
        }

        if (target instanceof EntityCitizen citizenEntity)
        {
            final Skill selected = getSelectedSkill(stack);
            final int delta = player.isShiftKeyDown() ? -1 : 1;

            player.displayClientMessage(Component.literal("I can diff citizen1"), false);

            if (!player.level().isClientSide)
            {
                player.displayClientMessage(Component.literal("I can diff citizen2"), false);

//                final ICitizenDataView dataView = citizenEntity.getCitizenDataView();
                final ICitizenData citizenData = citizenEntity.getCitizenData();

                if(citizenData != null)
                {
                    final ICitizenSkillHandler handler = citizenData.getCitizenSkillHandler();

                    int oldLevel = handler.getLevel(selected);
                    handler.incrementLevel(selected, delta);
                    int newLevel = handler.getLevel(selected);
                    citizenData.markDirty(0);

                    final var colony = citizenData.getColony();
                    if (colony != null)
                    {

                        final var pm = colony.getPackageManager();
                        if (pm instanceof com.minecolonies.core.colony.managers.ColonyPackageManager cpm)
                        {
                            cpm.setDirty();
                            colony.getCitizenManager().sendPackets(cpm.getCloseSubscribers(), new java.util.HashSet<>());
                        }
                    }

                    player.sendSystemMessage(Component.literal("[+Server+] Level has changed "+selected.name()));
//                    Network.getNetwork().sendToServer(new AdjustSkillCitizenMessage(colony,dataView,delta,selected));
                }
            }
            else
            {
                player.displayClientMessage(Component.literal("Adjusted skill: " + selected.name()), false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag)
    {
        final Skill selected = getSelectedSkill(stack);
        tooltip.add(Component.literal("Selected skill: " + selected.name()));
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