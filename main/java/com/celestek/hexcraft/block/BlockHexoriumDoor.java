package com.celestek.hexcraft.block;

import com.celestek.hexcraft.HexCraft;
import com.celestek.hexcraft.client.renderer.HexModelRendererDoor;
import com.celestek.hexcraft.init.HexAchievements;
import com.celestek.hexcraft.init.HexBlocks;
import com.celestek.hexcraft.init.HexConfig;
import com.celestek.hexcraft.init.HexItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static net.minecraftforge.common.util.ForgeDirection.*;

/**
 * @author Thorinair   <celestek@openmailbox.org>
 * @version 0.7.0
 */

public class BlockHexoriumDoor extends HexBlockModel {

    // Set default block name.
    public static String UNLOCALISEDNAME = "blockHexoriumDoor";

    /**
     * Constructor for the block.
     * @param blockName Unlocalized name for the block. Contains color name.
     */
    public BlockHexoriumDoor(String blockName) {
        super(Material.iron);

        // Set all block parameters.
        this.setBlockName(blockName);
        this.setCreativeTab(HexCraft.tabDecorative);

        // Assign harvest levels to all metas.
        for (int i = 0; i < 8; i++)
            this.setHarvestLevel("pickaxe", 2, i);

        for (int i = 8; i < 16; i++)
            this.setHarvestLevel("pickaxe", 3, i);

        this.setStepSound(Block.soundTypeMetal);
        this.setLightOpacity(0);
    }

    /**
     * Returns the block hardness at a location. Args: world, x, y, z
     */
    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        // If this is a normal block, return normal hardness.
        if (world.getBlockMetadata(x, y, z) < 8)
            return 1.5F;
            // If this is a reinforced block, return obsidian hardness.
        else
            return 50F;
    }

    /**
     * Location sensitive version of getExplosionRestance
     */
    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        // If this is a normal block, return normal resistance.
        if (world.getBlockMetadata(x, y, z) < 8)
            return 30F / 5F;
            // If this is a reinforced block, return obsidian resistance.
        else
            return 6000F / 5F;
    }

    /**
     * Returns if mobs can walk through the block.
     */
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        // Check meta and return true if the door is open.
        if (world.getBlock(x, y + 1, z) == this) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta > 7)
                meta = meta - 8;
            if (meta > 3)
                return true;
        }
        else if (world.getBlock(x, y - 1, z) == this) {
            int meta = world.getBlockMetadata(x, y - 1, z);
            if (meta > 7)
                meta = meta - 8;
            if (meta > 3)
                return true;
        }
        return false;
    }

    /**
     * Called when a player tries to place the door.
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Check if bottom block is solid and if there is space above.
        return (world.isSideSolid(x, y - 1, z, UP) && world.isAirBlock(x, y + 1, z)) ||
                (world.isSideSolid(x, y - 2, z, UP) && world.getBlock(x, y - 1, z) == this);
    }

    /**
     * Called when a block is placed by a player.
     */
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
        // Calculate door orientation.
        int direction = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        // Set the upper door.
        if (world.isAirBlock(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, this);
        }

        // Set the block's meta data according to direction.
        world.setBlockMetadataWithNotify(x, y, z, direction, 3);

        // Set make the door flipped if it should be double-doors.
        if (direction == 0) {
            if (world.getBlock(x + 1, y, z) instanceof BlockHexoriumDoor) {
                int meta = world.getBlockMetadata(x + 1, y, z);
                if (meta == 0 || meta == 4 || meta == 8 || meta == 12)
                    world.setBlockMetadataWithNotify(x, y + 1, z, 1, 3);
            }
        }
        else if (direction == 1) {
            if (world.getBlock(x, y, z + 1) instanceof BlockHexoriumDoor) {
                int meta = world.getBlockMetadata(x, y, z + 1);
                if (meta == 1 || meta == 5 || meta == 9 || meta == 13)
                    world.setBlockMetadataWithNotify(x, y + 1, z, 1, 3);
            }
        }
        else if (direction == 2) {
            if (world.getBlock(x - 1, y, z) instanceof BlockHexoriumDoor) {
                int meta = world.getBlockMetadata(x - 1, y, z);
                if (meta == 2 || meta == 6 || meta == 10 || meta == 14)
                    world.setBlockMetadataWithNotify(x, y + 1, z, 1, 3);
            }
        }
        else if (direction == 3) {
            if (world.getBlock(x, y, z - 1) instanceof BlockHexoriumDoor) {
                int meta = world.getBlockMetadata(x, y, z - 1);
                if (meta == 3 || meta == 7 || meta == 11 || meta == 15)
                    world.setBlockMetadataWithNotify(x, y + 1, z, 1, 3);
            }
        }
    }

    /**
     * Called when a block near is changed.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        // Check if the block below is still solid and if the upper door still exists, or if the lower door still exists.
        if (!((world.getBlock(x, y - 1, z).isSideSolid(world, x, y, z, UP) && world.getBlock(x, y + 1, z) == this) ||
                (world.getBlock(x, y - 1, z) == this))) {
            // If it doesn't, and this is the lower door, drop it and set the block to air.
            if (world.getBlock(x, y + 1, z) == this)
                this.dropBlockAsItem(world, x, y, z, 0, 0);
            world.setBlockToAir(x, y, z);
        }

        // Calculate if the door is powered and toggle the door state if needed.
        boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        if ((powered || block.canProvidePower()) && block != this)
        {
            // Get the meta and normalize it.
            int meta = 0;
            if (world.getBlock(x, y + 1, z) == this)
                meta = world.getBlockMetadata(x, y, z);
            else if (world.getBlock(x, y - 1, z) == this)
                meta = world.getBlockMetadata(x, y - 1, z);
            if (meta > 7)
                meta = meta - 8;

            // Open the door.
            if (meta < 4 && powered) {
                if (world.getBlock(x, y + 1, z) == this) {
                    world.setBlockMetadataWithNotify(x, y, z, meta + 4, 3);
                    world.markBlockForUpdate(x, y + 1, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
                else if (world.getBlock(x, y - 1, z) == this) {
                    world.setBlockMetadataWithNotify(x, y - 1, z, meta + 4, 3);
                    world.markBlockForUpdate(x, y, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
            }
            // Close the door.
            else if (meta > 3 && !powered) {
                if (world.getBlock(x, y + 1, z) == this) {
                    world.setBlockMetadataWithNotify(x, y, z, meta - 4, 3);
                    world.markBlockForUpdate(x, y + 1, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
                else if (world.getBlock(x, y - 1, z) == this) {
                    world.setBlockMetadataWithNotify(x, y - 1, z, meta - 4, 3);
                    world.markBlockForUpdate(x, y, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
            }
        }
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Updates the blocks bounds based on its current state.
     */
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        // Prepare the variables.
        float dThck = HexModelRendererDoor.dThck;

        // Prepare block meta and if the door is flipped, normalize meta.
        int meta = 0;
        boolean flippedDoor = false;
        if (world.getBlock(x, y - 1, z) == this) {
            meta = world.getBlockMetadata(x, y - 1, z);
            if (world.getBlockMetadata(x, y, z) == 1 || world.getBlockMetadata(x, y, z) == 9)
                flippedDoor = true;
        }
        else if (world.getBlock(x, y + 1, z) == this) {
            meta = world.getBlockMetadata(x, y, z);
            if (world.getBlockMetadata(x, y + 1, z) == 1 || world.getBlockMetadata(x, y + 1, z) == 9)
                flippedDoor = true;
        }
        if (meta > 7)
            meta = meta - 8;

        // Set block bounds depending on parameters.
        if (flippedDoor) {
            if (meta == 0 || meta == 5)
                this.setBlockBounds(0, 0, 0, 1, 1, dThck);
            else if (meta == 1 || meta == 6)
                this.setBlockBounds(1 - dThck, 0, 0, 1, 1, 1);
            else if (meta == 2 || meta == 7)
                this.setBlockBounds(0, 0, 1 - dThck, 1, 1, 1);
            else if (meta == 3 || meta == 4)
                this.setBlockBounds(0, 0, 0, dThck, 1, 1);
        }
        else {
            if (meta == 0 || meta == 7)
                this.setBlockBounds(0, 0, 0, 1, 1, dThck);
            else if (meta == 1 || meta == 4)
                this.setBlockBounds(1 - dThck, 0, 0, 1, 1, 1);
            else if (meta == 2 || meta == 5)
                this.setBlockBounds(0, 0, 1 - dThck, 1, 1, 1);
            else if (meta == 3 || meta == 6)
                this.setBlockBounds(0, 0, 0, dThck, 1, 1);
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int a, float b, float c, float d)
    {
        // If this is client side...
        if (world.isRemote)
        {
            // Simply return true.
            return true;
        }
        else
        {
            // Prepare a variable if the door is in a usable state.
            boolean use = false;
            // If player has no item in hand.
            if (player.getCurrentEquippedItem() == null)
                use = true;
            // If player has an item in hand.
            else {
                // Don't use door if Hexorium Manipulator is present.
                if (player.getCurrentEquippedItem().getItem() == HexItems.itemHexoriumReinforcer) {
                    // If the door is not upgraded, upgrade it.
                    if (world.getBlockMetadata(x, y, z) < 8) {
                        world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 8, 3);
                        if (world.getBlock(x, y, z) == world.getBlock(x, y - 1, z))
                            world.setBlockMetadataWithNotify(x, y - 1, z, world.getBlockMetadata(x, y - 1, z) + 8, 3);
                        else if (world.getBlock(x, y, z) == world.getBlock(x, y + 1, z))
                            world.setBlockMetadataWithNotify(x, y + 1, z, world.getBlockMetadata(x, y + 1, z) + 8, 3);

                        // Grant player the achievement.
                        if (HexConfig.cfgGeneralUseAchievements)
                            player.addStat(HexAchievements.achUseReinforcer, 1);

                        ItemStack stack = player.getCurrentEquippedItem();
                        stack.stackSize--;
                        if (stack.stackSize == 0)
                            stack = null;
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                    } else
                        use = true;
                }
                else
                    use = true;
            }

            // Use door.
            if (use) {
                // get block meta.
                int meta;
                if (world.getBlock(x, y - 1, z) == this)
                    meta = world.getBlockMetadata(x, y - 1, z);
                else
                    meta = world.getBlockMetadata(x, y, z);

                // Change meta on click.
                if (meta < 8) {
                    if (meta < 4)
                        meta = meta + 4;
                    else
                        meta = meta - 4;
                } else {
                    if (meta < 12)
                        meta = meta + 4;
                    else
                        meta = meta - 4;
                }

                // Set according block meta and play sound.
                if (world.getBlock(x, y - 1, z) == this) {
                    world.setBlockMetadataWithNotify(x, y - 1, z, meta, 3);
                    world.markBlockForUpdate(x, y, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y - 1, z, 0);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                    world.markBlockForUpdate(x, y + 1, z);
                    world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
            }

            return true;
        }
    }

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int i) {
        // Set the other door part to air.
        if (world.getBlock(x, y - 1, z) == this)
            world.setBlockToAir(x, y - 1, z);
        else if (world.getBlock(x, y + 1, z) == this)
            world.setBlockToAir(x, y + 1, z);
    }

    // Prepare the icons.
    @SideOnly(Side.CLIENT)
    private IIcon icon[];

    /**
     * Registers the icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        // Initialize the icons.
        icon = new IIcon[11];
        // Load the outer textures.
        icon[0] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "01");
        icon[1] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "02");
        // Load the flipped outer textures.
        icon[2] = new IconFlipped(icon[0], true, false);
        icon[3] = new IconFlipped(icon[1], true, false);
        icon[4] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "03");
        // Load the outer reinforced textures.
        icon[5] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "04");
        icon[6] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "05");
        // Load the flipped outer reinforced textures.
        icon[7] = new IconFlipped(icon[5], true, false);
        icon[8] = new IconFlipped(icon[6], true, false);
        icon[9] = iconRegister.registerIcon(HexCraft.MODID + ":" + UNLOCALISEDNAME + "/" + UNLOCALISEDNAME + "06");
        // Load the inner texture.
        if(this == HexBlocks.blockHexoriumDoorRainbow)
            icon[10] = iconRegister.registerIcon(HexCraft.MODID + ":" + "glowRainbow");
        else
            icon[10] = iconRegister.registerIcon(HexCraft.MODID + ":" + "glow");
    }

    /**
     * Retrieves the icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        // Retrieve icon based on side and meta.
        if (meta == 1)
            return icon[side];
        else
            return icon[0];
    }


    /**
     * Retrieves the icons and sets the connected texture.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        // Get the meta and determine if door should be flipped.
        int meta = 0;
        boolean flippedDoor = false;
        if (world.getBlock(x, y - 1, z) == this) {
            meta = world.getBlockMetadata(x, y - 1, z);
            if (world.getBlockMetadata(x, y, z) == 1 || world.getBlockMetadata(x, y, z) == 9)
                flippedDoor = true;
        }
        else if (world.getBlock(x, y + 1, z) == this) {
            meta = world.getBlockMetadata(x, y, z);
            if (world.getBlockMetadata(x, y + 1, z) == 1 || world.getBlockMetadata(x, y + 1, z) == 9)
                flippedDoor = true;
        }

        // Prepare texture parameters: rei - reinforced, flp - flipped, upp - upper door
        int rei = 0;
        int flp = 0;
        int upp = 0;

        // Calculate different parameters.
        if (meta > 7) {
            rei = 5;
            meta = meta - 8;
        }
        if (flippedDoor && meta < 4)
            flp = 2;
        if (world.getBlock(x, y - 1, z) == this)
            upp = 1;

        // Return icons based on parameters, side and meta.
        if (meta == 0 || meta == 5)
            switch (side) {
                case 0:
                    return icon[4 + rei];
                case 1:
                    return icon[4 + rei];
                case 2:
                    return icon[1 + rei + flp - upp];
                case 3:
                    return icon[3 + rei - flp - upp];
                case 4:
                    return icon[4 + rei];
                case 5:
                    return icon[4 + rei];
            }
        else if (meta == 1 || meta == 6)
            switch (side) {
                case 0:
                    return icon[4 + rei];
                case 1:
                    return icon[4 + rei];
                case 2:
                    return icon[4 + rei];
                case 3:
                    return icon[4 + rei];
                case 4:
                    return icon[3 + rei - flp - upp];
                case 5:
                    return icon[1 + rei + flp - upp];
            }
        else if (meta == 2 || meta == 7)
            switch (side) {
                case 0:
                    return icon[4 + rei];
                case 1:
                    return icon[4 + rei];
                case 2:
                    return icon[3 + rei - flp - upp];
                case 3:
                    return icon[1 + rei + flp - upp];
                case 4:
                    return icon[4 + rei];
                case 5:
                    return icon[4 + rei];
            }
        else if (meta == 3 || meta == 4)
            switch (side) {
                case 0:
                    return icon[4 + rei];
                case 1:
                    return icon[4 + rei];
                case 2:
                    return icon[4 + rei];
                case 3:
                    return icon[4 + rei];
                case 4:
                    return icon[1 + rei + flp - upp];
                case 5:
                    return icon[3 + rei - flp - upp];
            }
        return icon[0];
    }

    /**
     * Hide the touching sides.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        // Hide side touching the upper or lower door.
        if (side == 0 && world.getBlock(x, y, z) == this)
            return false;
        else if (side == 1 && world.getBlock(x, y, z) == this)
            return false;
        else
            return true;
    }
}
