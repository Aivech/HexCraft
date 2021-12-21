package com.celestek.hexcraft;

import com.celestek.hexcraft.block.BlockEnergizedHexorium;
import com.celestek.hexcraft.block.BlockHexoriumGenerator;
import com.celestek.hexcraft.init.HexBlocks;
import com.celestek.hexcraft.init.HexItems;
import com.celestek.hexcraft.network.ChannelHandler;
import com.celestek.hexcraft.util.HexEnums;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// @Mod(modid = HexCraft.MODID, name = HexCraft.MODNAME, version = HexCraft.VERSION, dependencies = "after:IC2")
public class HexCraft implements ModInitializer {
    // Set default mod info.
    public static final String MODID = "hexcraft";
    public static final String MODNAME = "HEXCraft";
    // public static final String VERSION = "0.13.2";
    /*
    // Set up the instance.
    @Mod.Instance(value = HexCraft.MODID)
    public static HexCraft instance;
    */

    public static final Logger LOGGER = LogManager.getLogger("MODID");

    // HEXCraft's ID counter for automatic populating.
    public static int idCounter;

    // A fortune number unique for HEXCraft.
    public static int HEX_FORTUNE = 64758;

    // Set up creative tabs.
    public static ItemGroup tabDecorative = FabricItemGroupBuilder.build(
            new Identifier(MODID, "tab_decorative"),
            () -> new ItemStack(HexBlocks.getHexBlock(BlockEnergizedHexorium.ID, HexEnums.Colors.RAINBOW))
    );

    public static ItemGroup tabMachines = FabricItemGroupBuilder.build(
            new Identifier(MODID, "tab_machines"),
            () -> new ItemStack(HexBlocks.getHexBlock(BlockHexoriumGenerator.ID))
    );

    public static ItemGroup tabComponents = FabricItemGroupBuilder.build(
            new Identifier(MODID, "tab_components"),
            () -> new ItemStack(HexItems.itemHexoriumGlassPackage)
    );

    @Override
    public void onInitialize() {
        ChannelHandler.init();
        HexCommonProxy.initialize();
    }
}
