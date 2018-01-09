package com.mordenkainen.equivalentenergistics.blocks.base.block;

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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class BlockMulti extends BlockBase {

	public static final PropertyInteger DUMMYTYPE = PropertyInteger.create("type", 0, 15);
	
	public PropertyInteger TYPE;
	
	public final int count;
	
	private final BlockStateContainer multiBlockState;
	
	public BlockMulti(Material material, String name, int count) {
		super(material, name);
		TYPE = PropertyInteger.create("type", 0, count - 1);
		multiBlockState = createRealBlockState();
		setDefaultState(multiBlockState.getBaseState().withProperty(TYPE, 0));
		this.count = count;
	}

	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, new IProperty[] {DUMMYTYPE});
	}
	
	protected BlockStateContainer createRealBlockState() {
	    return new BlockStateContainer(this, new IProperty[] {TYPE});
	}
	
	@Override
	public BlockStateContainer getBlockState() {
        return multiBlockState;
    }

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
	    for (int i = 0; i < count; i++) {
	    	list.add(new ItemStack(this, 1, i));
	    }
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
	    return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
	}
	
	@Override
	public Item createItemBlock() {
		return new ItemBlockMulti(this).setRegistryName(getRegistryName());
	}
	
	@Override
	public void registerItemModel(Item itemBlock) {
		for (int i = 0; i < count; i++) {
			EquivalentEnergistics.proxy.registerItemRenderer(itemBlock, i, name, "type=" + i);
		}
	}
	
}
