package com.celestek.hexcraft.item;

import com.celestek.hexcraft.HexCraft;
import com.celestek.hexcraft.block.*;
import com.celestek.hexcraft.init.HexAchievements;
import com.celestek.hexcraft.init.HexBlocks;
import com.celestek.hexcraft.init.HexConfig;
import com.celestek.hexcraft.tileentity.*;
import com.celestek.hexcraft.util.HexEnergyNode;
import com.celestek.hexcraft.util.HexUtils;
import com.celestek.hexcraft.util.NetworkAnalyzer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * @author Thorinair   <celestek@openmailbox.org>
 * @version 0.7.0
 */

public class ItemHexoriumProbe extends Item {

    // Item ID
    public static final String ID = "itemHexoriumProbe";

    /**
     * Constructor for the item.
     * @param itemName Unlocalized name for the item.
     */
    public ItemHexoriumProbe(String itemName) {
        setUnlocalizedName(itemName);
        setCreativeTab(HexCraft.tabMachines);
        setTextureName(HexCraft.MODID + ":" + itemName);
        setMaxStackSize(1);
    }

    /**
     * This is called when the item is used, before the block is activated.
     * @param stack The Item Stack
     * @param player The Player that used the item
     * @param world The Current World
     * @param x Target X Position
     * @param y Target Y Position
     * @param z Target Z Position
     * @param side The side of the target hit
     * @return Return true to prevent any further processing.
     */
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Block block = world.getBlock(x, y, z);

            // Display Port info.
            if (block instanceof IBlockHexEnergyPort) {
                ITileHexEnergyPort port = (ITileHexEnergyPort) world.getTileEntity(x, y, z);
                port.displayInfoPort(player);
            }

            // Display Source info.
            else if (block instanceof IBlockHexEnergySource) {
                ITileHexEnergySource source = (ITileHexEnergySource) world.getTileEntity(x, y, z);
                source.displayInfoSource(player);
            }

            // Display Drain info.
            else if (block instanceof IBlockHexEnergyDrain) {
                ITileHexEnergyDrain drain = (ITileHexEnergyDrain) world.getTileEntity(x, y, z);
                drain.displayInfoDrain(player);
            }

            // Display Pylon info.
            else if (block instanceof BlockEnergyPylon) {
                TileEnergyPylon pylon = (TileEnergyPylon) world.getTileEntity(x, y, z);
                pylon.displayInfoPylon(player);
            }

            // Display Valve info.
            else if (block instanceof BlockTankValve) {
                TileTankValve valve = (TileTankValve) world.getTileEntity(x, y, z);
                valve.displayInfoValve(player);
            }

        }

        return false;
    }
}
