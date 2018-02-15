package com.mordenkainen.equivalentenergistics.blocks.base.block;

import java.util.List;

import com.mordenkainen.equivalentenergistics.EquivalentEnergistics;
import com.mordenkainen.equivalentenergistics.items.itemblocks.ItemBlockMulti;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class BlockMulti extends BlockBase {

    public static final PropertyInteger DUMMYTYPE = PropertyInteger.create("type", 0, 15);

    public PropertyInteger type;

    public final int count;

    protected BlockStateContainer multiBlockState;

    public BlockMulti(final Material material, final String name, final int count) {
        super(material, name);
        type = PropertyInteger.create("type", 0, count - 1);
        multiBlockState = new BlockStateContainer(this, new IProperty[] {type});
        setDefaultState(multiBlockState.getBaseState().withProperty(type, 0));
        this.count = count;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {DUMMYTYPE});
    }

    @Override
    public BlockStateContainer getBlockState() {
        return multiBlockState;
    }

    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty(type, meta);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(type);
    }

    @Override
    public int damageDropped(final IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(final Item item, final CreativeTabs tab, final List<ItemStack> list) {
        for (int i = 0; i < count; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
    }

    @Override
    public Item createItemBlock() {
        return new ItemBlockMulti(this).setRegistryName(getRegistryName());
    }

    @Override
    public void registerItemModel(final Item itemBlock) {
        for (int i = 0; i < count; i++) {
            EquivalentEnergistics.proxy.registerItemRenderer(itemBlock, i, name, "type=" + i);
        }
    }

}
