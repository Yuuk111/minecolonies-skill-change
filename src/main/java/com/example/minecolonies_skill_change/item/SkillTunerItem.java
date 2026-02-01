package com.example.minecolonies_skill_change.item;

//import com.example.minecolonies_skill_change.network.AdjustCitizenSkillMessage;
//import com.example.minecolonies_skill_change.network.ModNetwork;
import com.minecolonies.api.colony.ICitizenData;

import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSkillHandler;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkillTunerItem extends Item
{
    private static final String NBT_SKILL = "SelectedSkill";
//    private final Skill targetSkill;
    private final int minLevel;
    private final int maxLevel;
    public SkillTunerItem(int minLevel,int maxLevel)
    {
        super(new Item.Properties().stacksTo(16));
//        this.targetSkill = targetSkill;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    /*
    * Right-click to change ability
    * */
    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand)
    {
        /*final ItemStack stack = player.getItemInHand(hand);

        final boolean reverse = player.isShiftKeyDown();
        final Skill current = getSelectedSkill(stack);
        final Skill next = cycleSkill(current, reverse);
        setSelectedSkill(stack, next);
        player.displayClientMessage(Component.literal("Selected skill: " + next.name()), true);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);*/
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public boolean onLeftClickEntity(final ItemStack stack, final Player player, final net.minecraft.world.entity.Entity target)
    {
        if (target instanceof net.minecraft.world.entity.animal.Pig)
        {
            player.displayClientMessage(Component.literal("§9 不要喂给llm,他看不懂啊！"), true);
        }

        if (target instanceof EntityCitizen citizenEntity)
        {
//            final Skill selected = getSelectedSkill(stack);
//            final int delta = player.isShiftKeyDown() ? -1 : 1;
            final Skill selected = Skill.Intelligence;
            final int delta = 1;

            if (!player.level().isClientSide)
            {

                final ICitizenData citizenData = citizenEntity.getCitizenData();

                if(citizenData != null)
                {
                    final ICitizenSkillHandler handler = citizenData.getCitizenSkillHandler();

                    int currentLevel = handler.getLevel(selected);
                    if (currentLevel < this.minLevel )
                    {
                        player.sendSystemMessage(Component.literal("这份卷轴里的知识或许对他来说太深奥了，或许他应该先去扫个盲？"));
                        return true;
                    }
                    if (currentLevel >= this.maxLevel)
                    {
                        player.sendSystemMessage(Component.literal("这个人已经完全理解这份卷轴里的知识了，对他来说没用了~"));
                        return true;
                    }
                    handler.incrementLevel(selected, delta);
                    citizenData.markDirty(0);

                    //consume item 1
                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }

                    syncToClient(citizenData);

                    /*final var colony = citizenData.getColony();
                    if (colony != null)
                    {

                        final var pm = colony.getPackageManager();
                        if (pm instanceof com.minecolonies.core.colony.managers.ColonyPackageManager cpm)
                        {
                            cpm.setDirty();
                            colony.getCitizenManager().sendPackets(cpm.getCloseSubscribers(), new java.util.HashSet<>());
                        }
                    }*/

                    player.sendSystemMessage(Component.literal("[+uki+] 这个居民变得更有智慧了~~~ "+selected.name()));
//                    Network.getNetwork().sendToServer(new AdjustSkillCitizenMessage(colony,dataView,delta,selected));
                }
            }
            else
            {
                /*player.displayClientMessage(Component.literal("Level of " + selected.name() + "has been upped 1 by Yuuki"), false);*/
            }
            return true;
        }
        return false;
    }

    private void syncToClient(ICitizenData data)
    {
        final var colony = data.getColony();
        if (colony != null)
        {

            final var pm = colony.getPackageManager();
            if (pm instanceof com.minecolonies.core.colony.managers.ColonyPackageManager cpm)
            {
                cpm.setDirty();
                colony.getCitizenManager().sendPackets(cpm.getCloseSubscribers(), new java.util.HashSet<>());
            }
        }
    }

    @Override
    public void appendHoverText(final ItemStack stack, final Level level, final List<Component> tooltip, final TooltipFlag flag)
    {
        tooltip.add(Component.literal("§e 这份卷轴可以让你的居民变得更聪明~"));
        tooltip.add(Component.literal("§e 适用区间: §b" + this.minLevel + "---" + this.maxLevel));
        /*
        * 以卷轴等级来判断稀有度描述*/
        MutableComponent flavorText = Component.literal("");
        if(this.maxLevel <= 50)
        {
            //30-50:Faint - 灰色
            flavorText = Component.literal("微弱的光芒闪烁，适合入门者。");
            flavorText.withStyle(ChatFormatting.GRAY);
        }
        else if(this.maxLevel <= 70)
        {
            //50-70:Dim - 绿色
            flavorText = Component.literal("隐约可见的符文，蕴含进阶知识。");
            flavorText.withStyle(ChatFormatting.DARK_GREEN);
        }
        else if(this.maxLevel <= 80)
        {
            //70-80:Mellow - 青色
            flavorText = Component.literal("柔和而稳定的能量，专家之选。");
            flavorText.withStyle(ChatFormatting.AQUA);
        }
        else if(this.maxLevel <= 90)
        {
            //80-90:Vivid - 紫色
            flavorText = Component.literal("生动的智慧跃然纸上，大师珍藏。");
            flavorText.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        else if(this.maxLevel <= 99)
        {
            //90-99:Intense - 金色
            flavorText = Component.literal("强烈的力量仿佛要撕裂卷轴，传说级！");
            flavorText.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        }

        tooltip.add(flavorText);

        /*final Skill selected = getSelectedSkill(stack);
        tooltip.add(Component.literal("Selected skill: " + selected.name()));
        tooltip.add(Component.literal("Right click air to cycle (sneak reverses)."));
        tooltip.add(Component.literal("Right click citizen to adjust (sneak lowers)."));*/

        tooltip.add(Component.literal("对着你的居民左键以提升他的属性"));
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