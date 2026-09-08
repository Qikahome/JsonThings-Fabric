package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Fabric 移植：等价上游 Forge 版 TierBuilder 使用的 net.minecraftforge.common.ForgeTier。
 * 1.20.1 原版 Tier 接口已含 getTag()（1.19.3+），此处仅按原字段复刻。
 */
public class FlexTier implements Tier
{
    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    @Nullable
    private final TagKey<Block> tag;
    private final Supplier<Ingredient> repairIngredient;

    public FlexTier(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue, @Nullable TagKey<Block> tag, Supplier<Ingredient> repairIngredient)
    {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.tag = tag;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses()
    {
        return uses;
    }

    @Override
    public float getSpeed()
    {
        return speed;
    }

    @Override
    public float getAttackDamageBonus()
    {
        return attackDamageBonus;
    }

    @Override
    public int getLevel()
    {
        return level;
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantmentValue;
    }

    @Override
    @Nullable
    public TagKey<Block> getTag()
    {
        return tag;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return repairIngredient.get();
    }

    public boolean isCorrectForDrops(BlockState state)
    {
        if (tag == null) return false;
        return state.is(tag);
    }

    @Override
    public String toString()
    {
        return "FlexTier{" +
                "level=" + level +
                ", uses=" + uses +
                ", speed=" + speed +
                ", attackDamageBonus=" + attackDamageBonus +
                ", enchantmentValue=" + enchantmentValue +
                ", tag=" + tag +
                '}';
    }
}
