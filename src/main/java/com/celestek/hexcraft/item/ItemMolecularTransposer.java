package com.celestek.hexcraft.item;

import com.celestek.hexcraft.HexCraft;
import com.celestek.hexcraft.block.*;
import com.celestek.hexcraft.init.HexAchievements;
import com.celestek.hexcraft.init.HexBlocks;
import com.celestek.hexcraft.init.HexConfig;
import com.celestek.hexcraft.init.HexGui;
import com.celestek.hexcraft.util.HexDamage;
import com.celestek.hexcraft.util.HexEnums;
import com.celestek.hexcraft.util.HexUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemMolecularTransposer extends Item {

    // Item ID
    public static final String ID = "toolMolecularTransposer";

    /**
     * Constructor for the item.
     * @param itemName Unlocalized name for the item.
     */
    public ItemMolecularTransposer(String itemName) {
        setUnlocalizedName(itemName);
        setCreativeTab(HexCraft.tabMachines);
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
            // Fired if the player is not sneaking.
            if (!player.isSneaking()) {
                ItemStack inventory = readNBT(stack);
                if (inventory != null) {
                    // If the block in container is Energized Hexorium...
                    if (Block.getBlockFromItem(inventory.getItem()) instanceof BlockEnergizedHexorium) {
                        Block block = world.getBlock(x, y, z);

                        // If the block is one of the decorative blocks...
                        if (block instanceof IBlockUsableTransposer) {
                            if ((block instanceof IBlockMultiBlock) && HexUtils.getMetaBit(HexBlocks.META_STRUCTURE_IS_PART, world, x, y, z)) {
                                player.addChatMessage(new ChatComponentTranslation("msg.cannotSwap.txt"));
                                return false;
                            }

                            // Get required types
                            IHexBlock blockID = (IHexBlock) block;
                            IBlockHexColor blockColor = (IBlockHexColor) block;
                            IBlockHexVariant blockVariant = (IBlockHexVariant) block;

                            // Fetch Energized from tool.
                            Block blockInventory = Block.getBlockFromItem(inventory.getItem());
                            if (blockInventory instanceof BlockEnergizedHexorium) {
                                // Check if the color is different.
                                IBlockHexColor blockEnergized = (IBlockHexColor) blockInventory;
                                if (blockEnergized.getColor() != blockColor.getColor()) {
                                    // Drop the old Energized.
                                    EntityItem entity = new EntityItem(world, player.posX, player.posY, player.posZ);
                                    entity.setEntityItemStack(new ItemStack(HexBlocks.getHexBlock(BlockEnergizedHexorium.ID, blockColor.getColor()), 1));
                                    world.spawnEntityInWorld(entity);

                                    Block blockNew = HexBlocks.getHexBlock(blockID.getID(), blockVariant.getVariant(), blockEnergized.getColor());
                                    // Place the new block.
                                    if (block instanceof BlockHexoriumDoor) {
                                        if (world.getBlock(x, y - 1, z) instanceof BlockHexoriumDoor) {
                                            int meta1 = world.getBlockMetadata(x, y - 1, z);
                                            int meta2 = world.getBlockMetadata(x, y, z);
                                            world.setBlockToAir(x, y - 1, z);
                                            world.setBlockToAir(x, y, z);
                                            world.setBlock(x, y - 1, z, blockNew, meta1, HexUtils.META_NOTIFY_NOTHING);
                                            world.setBlock(x, y, z, blockNew, meta2, HexUtils.META_NOTIFY_UPDATE);
                                        }
                                        else {
                                            int meta1 = world.getBlockMetadata(x, y, z);
                                            int meta2 = world.getBlockMetadata(x, y + 1, z);
                                            world.setBlockToAir(x, y, z);
                                            world.setBlockToAir(x, y + 1, z);
                                            world.setBlock(x, y, z, blockNew, meta1, HexUtils.META_NOTIFY_NOTHING);
                                            world.setBlock(x, y + 1, z, blockNew, meta2, HexUtils.META_NOTIFY_UPDATE);
                                        }
                                    }
                                    else {
                                        int meta = world.getBlockMetadata(x, y, z);
                                        world.setBlock(x, y, z, blockNew, meta, HexUtils.META_NOTIFY_UPDATE);
                                    }

                                    // Decrement the count of Energized Hexorium.
                                    inventory.stackSize--;
                                    if (inventory.stackSize == 0)
                                        inventory = null;

                                    // Write the items.
                                    NBTTagList tagsItems = new NBTTagList();
                                    if (inventory != null) {
                                        NBTTagCompound tagCompoundLoop = new NBTTagCompound();
                                        inventory.writeToNBT(tagCompoundLoop);
                                        tagsItems.appendTag(tagCompoundLoop);
                                    }
                                    stack.stackTagCompound.setTag("items", tagsItems);
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);

                                    // Grant player the achievement.
                                    if (HexConfig.cfgGeneralUseAchievements)
                                        player.addStat(HexAchievements.achUseTransposer, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote)
            // Fired if the player is sneaking.
            if (player.isSneaking())
                // Open the GUI.
                player.openGui(HexCraft.instance, HexGui.GUI_ID_MOLECULAR_TRANSPOSER, world, (int) player.posX, (int) player.posY, (int) player.posZ);

        return stack;
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
        if (!entity.worldObj.isRemote)
            if (HexConfig.cfgGeneralTransposerAttack)
                if (!(entity instanceof EntityPlayer) || HexConfig.cfgGeneralTransposerAttackPlayers) {
                    ItemStack inventory = readNBT(itemstack);
                    if (inventory != null)
                        // If the block in container is Energized Hexorium...
                        if (Block.getBlockFromItem(inventory.getItem()) instanceof BlockEnergizedHexorium)
                            // Check if the target can be attacked.
                            if (entity.canAttackWithItem()) {
                                // If yes, deal damage.
                                entity.attackEntityFrom(HexDamage.transposer, 20);

                                // Decrement the count of Energized Hexorium.
                                inventory.stackSize--;
                                if (inventory.stackSize == 0)
                                    inventory = null;

                                // Write the items.
                                NBTTagList tagsItems = new NBTTagList();
                                if (inventory != null) {
                                    NBTTagCompound tagCompoundLoop = new NBTTagCompound();
                                    inventory.writeToNBT(tagCompoundLoop);
                                    tagsItems.appendTag(tagCompoundLoop);
                                }
                                itemstack.stackTagCompound.setTag("items", tagsItems);
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, itemstack);

                                // Grant player the achievement.
                                if (HexConfig.cfgGeneralUseAchievements)
                                    player.addStat(HexAchievements.achAttackTransposer, 1);

                                return true;
                            }
                }

        return false;
    }

    /**
     * If the durability bar should be showed.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack) {
        ItemStack inventory = readNBT(stack);

        return (inventory != null);
    }

    /**
     * Renders the durability bar.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemStack inventory = readNBT(stack);

        // Calculate bar.
        if (inventory != null)
            return 1 - (double)inventory.stackSize / (double)64;
        else
            return 1;
    }

    // Prepare the icons.
    @SideOnly(Side.CLIENT)
    private IIcon icon[];

    /**
     * Registers the icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        // Initialize the icons.
        icon = new IIcon[19];

        // Load the empty icon.
        icon[0] = iconRegister.registerIcon(HexCraft.MODID + ":" + ID + "/" + ID);

        // Load all the different color icons.
        int i = 1;
        for (HexEnums.Colors color : HexEnums.Colors.values()) {
            icon[i] = iconRegister.registerIcon(HexCraft.MODID + ":" + ID + "/" + ID + color.name);
            i++;
        }
    }

    /**
     * Gets an icon index based on player.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIconIndex(stack);
    }

    /**
     * Gets an icon index based on an item's damage value.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        ItemStack inventory = readNBT(stack);

        // Get a different icon based on Energized Hexorium.
        if (inventory != null) {
            Block block = Block.getBlockFromItem(inventory.getItem());
            if (block instanceof BlockEnergizedHexorium) {
                IBlockHexColor blockColor = (IBlockHexColor) block;

                int i = 1;
                for (HexEnums.Colors color : HexEnums.Colors.values()) {
                    if (blockColor.getColor() == color)
                        return icon[i];
                    i++;
                }
            }
        }

        return icon[0];
    }

    /**
     * Reads the container.
     */
    private ItemStack readNBT(ItemStack device) {
        ItemStack inventory;

        if (device.stackTagCompound == null)
            device.stackTagCompound = new NBTTagCompound();

        NBTTagList tagsItems = device.stackTagCompound.getTagList("items", 10);
        NBTTagCompound tagCompound1 = tagsItems.getCompoundTagAt(0);
        inventory = ItemStack.loadItemStackFromNBT(tagCompound1);

        return inventory;
    }

    public static Item registerItem() {
        Item item = new ItemMolecularTransposer(ID);
        GameRegistry.registerItem(item, ID);
        return item;
    }
}
