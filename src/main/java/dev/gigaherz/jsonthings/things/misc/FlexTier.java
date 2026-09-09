package dev.gigaherz.jsonthings.things.misc;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Fabric 移植：等价上游 Neo 1.21.1 TierBuilder 使用的 net.neoforged.neoforge.common.SimpleTier。
 *
 * <p>1.21.1 vanilla {@link Tier} 接口已重构：移除了 getLevel()/getTag()，新增
 * {@link #getIncorrectBlocksForDrops()}（挖掘"不正确"集合，供 Tool 组件判断掉落）。
 * 因此 tier JSON 的 "tag" 键在 1.21.1 语义为 incorrect_for_drops（与 1.20.x 的"正确工具 tag"不同）；
 * 未配置时回退到木质工具档（wood 的 incorrect 集合），保证 createToolProperties 不产生 null。
 */
public class FlexTier implements Tier
{
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    @Nullable
    private final TagKey<Block> incorrectForDrops;
    private final Supplier<Ingredient> repairIngredient;

    public FlexTier(int uses, float speed, float attackDamageBonus, int enchantmentValue, @Nullable TagKey<Block> incorrectForDrops, Supplier<Ingredient> repairIngredient)
    {
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.incorrectForDrops = incorrectForDrops;
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
    public TagKey<Block> getIncorrectBlocksForDrops()
    {
        return incorrectForDrops != null ? incorrectForDrops : Tiers.WOOD.getIncorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return repairIngredient.get();
    }

    @Override
    public String toString()
    {
        return "FlexTier{" +
                "uses=" + uses +
                ", speed=" + speed +
                ", attackDamageBonus=" + attackDamageBonus +
                ", enchantmentValue=" + enchantmentValue +
                ", incorrectForDrops=" + incorrectForDrops +
                '}';
    }
}
