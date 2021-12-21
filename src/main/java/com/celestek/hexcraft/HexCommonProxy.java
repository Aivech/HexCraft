package com.celestek.hexcraft;

import com.celestek.hexcraft.compat.HexChisel;
import com.celestek.hexcraft.compat.HexEU;
import com.celestek.hexcraft.compat.HexMultipart;
import com.celestek.hexcraft.compat.HexOreDict;
import com.celestek.hexcraft.init.*;
import com.celestek.hexcraft.util.ChunkManager;
import com.celestek.hexcraft.worldgen.HexWorldGen;

public class HexCommonProxy {
    // we fabric now
    public static void initialize() {
        // Set up config.
        HexConfig.initializeConfig();
        // Set up all ingame blocks, items and recipes.
        HexBlocks.initializeBlocks();
        HexItems.initializeItems();
        HexOreDict.registerOres();
        HexTileEntities.initializeTileEntities();
        // Set up chunk loading.
        // TODO: Chunkloader
        // increaseChunkCap();
        ChunkManager.instance.register();
        HexRecipes.initializeRecipes();
        GameRegistry.registerWorldGenerator(new HexWorldGen(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(HexCraft.instance, new HexGui());
        if (HexConfig.cfgGeneralUseAchievements)
            HexAchievements.initializeAchievements();
        if (Loader.isModLoaded("ForgeMultipart"))
            HexMultipart.initializeMicroblocks();
        if (Loader.isModLoaded("chisel"))
            HexChisel.initializeChisel();
        if (Loader.isModLoaded("IC2"))
            HexEU.initializeEU();
    }

    // TODO: chunkloader
/*    private static void increaseChunkCap() {
        Configuration cfg = ForgeChunkManager.getConfig();
        Property modTC = cfg.get(HexCraft.MODID, "maximumTicketCount", 1000);
        Property modCPT = cfg.get(HexCraft.MODID, "maximumChunksPerTicket", 2000);
        cfg.save();
    }*/
}
