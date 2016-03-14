package io.github.rumangerst.smithyoverhaul;

import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import io.github.rumangerst.smithyoverhaul.items.CraftedTool;
import io.github.rumangerst.smithyoverhaul.items.UnfinishedSword;
import io.github.rumangerst.smithyoverhaul.items.UnfinishedTool;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class SmithyOverhaulPlugin extends JavaPlugin implements Listener
{    
    public static PluginLogger LOGGER = null;    
    private TieredSmity smithy;
    
    public SmithyOverhaulPlugin()
    {
        LOGGER = new PluginLogger(this);
        smithy = new TieredSmity(this);
    }
    
    @Override
    public void onEnable()
    {
        /*MinecraftForge.EVENT_BUS.register(eventHandler);
        MinecraftForge.EVENT_BUS.register(smity);*/
        
        //TieredTool.InitializeTieredTools();
        
        /*GameRegistry.addSmelting(new ItemStack(Items.iron_pickaxe), new ItemStack(Items.iron_ingot, 2), 0);
        GameRegistry.addSmelting(new ItemStack(Items.iron_shovel), new ItemStack(Items.iron_ingot, 1), 0);
        GameRegistry.addSmelting(new ItemStack(Items.iron_hoe), new ItemStack(Items.iron_ingot, 1), 0);
        GameRegistry.addSmelting(new ItemStack(Items.iron_axe), new ItemStack(Items.iron_ingot, 2), 0);
        GameRegistry.addSmelting(Items.coal, new ItemStack(Blocks.gravel, 1).setStackDisplayName("Asche"), 0);        */
        
        createFurnaceCoalSmelting();
        
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(smithy, this);
        
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toywoodenpickaxe", Material.WOOD_PICKAXE, "Spielzeugspitzhacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toywoodenaxe", Material.WOOD_AXE, "Spielzeugaxt"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toywoodenshovel", Material.WOOD_SPADE, "Spielzeugschaufel"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toywoodenhoe", Material.WOOD_HOE, "Spielzeughacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedSword("toywoodensword", Material.WOOD_SWORD, "Spielzeugschwert"));
        
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toystonepickaxe", Material.STONE_PICKAXE, "Spielzeugspitzhacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toystoneaxe", Material.STONE_AXE, "Spielzeugaxt"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toystoneshovel", Material.STONE_SPADE, "Spielzeugschaufel"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("toystonehoe", Material.STONE_HOE, "Spielzeughacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedSword("toystonesword", Material.STONE_SWORD, "Spielzeugschwert"));
        
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("unfinishedironpickaxe", Material.IRON_PICKAXE, "Unfertige Spitzhacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("unfinishedironaxe", Material.IRON_AXE, "Unfertige Axt"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("unfinishedironshovel", Material.IRON_SPADE, "Unfertige Schaufel"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedTool("unfinishedironhoe", Material.IRON_HOE, "Unfertige Hacke"));
        CustomItemsAPI.api(this).registerItem(this, new UnfinishedSword("unfinishedironsword", Material.IRON_SWORD, "Unfertiges Schwert"));
        
        CustomItemsAPI.api(this).registerItem(this, new CraftedTool("craftedironpickaxe", Material.IRON_PICKAXE, "Geschmiedete Spitzhacke"));
        CustomItemsAPI.api(this).registerItem(this, new CraftedTool("craftedironaxe", Material.IRON_AXE, "Geschmiedete Axt"));
        CustomItemsAPI.api(this).registerItem(this, new CraftedTool("craftedironshovel", Material.IRON_SPADE, "Geschmiedete Schaufel"));
        CustomItemsAPI.api(this).registerItem(this, new CraftedTool("craftedironhoe", Material.IRON_HOE, "Geschmiedete Hacke"));
        //CustomItemsAPI.api(this).registerItem(this, new CraftedSword("craftedironsword", Material.IRON_SWORD, "Geschmiedetes Schwert"));
        
        CustomItemsAPI.api(this).registerOverride(Material.WOOD_PICKAXE, "toywoodenpickaxe");
        CustomItemsAPI.api(this).registerOverride(Material.WOOD_AXE, "toywoodenaxe");
        CustomItemsAPI.api(this).registerOverride(Material.WOOD_SPADE, "toywoodenshovel");
        CustomItemsAPI.api(this).registerOverride(Material.WOOD_HOE, "toywoodenhoe");
        CustomItemsAPI.api(this).registerOverride(Material.WOOD_SWORD, "toywoodensword");
        
        CustomItemsAPI.api(this).registerOverride(Material.STONE_PICKAXE, "toystonepickaxe");
        CustomItemsAPI.api(this).registerOverride(Material.STONE_AXE, "toystoneaxe");
        CustomItemsAPI.api(this).registerOverride(Material.STONE_SPADE, "toystoneshovel");
        CustomItemsAPI.api(this).registerOverride(Material.STONE_HOE, "toystonehoe");
        CustomItemsAPI.api(this).registerOverride(Material.STONE_SWORD, "toystonesword");
        
        CustomItemsAPI.api(this).registerOverride(Material.IRON_PICKAXE, "unfinishedironpickaxe");
        CustomItemsAPI.api(this).registerOverride(Material.IRON_AXE, "unfinishedironaxe");
        CustomItemsAPI.api(this).registerOverride(Material.IRON_SPADE, "unfinishedironshovel");
        CustomItemsAPI.api(this).registerOverride(Material.IRON_HOE, "unfinishedironhoe");
        CustomItemsAPI.api(this).registerOverride(Material.IRON_SWORD, "unfinishedironsword");
    }
    
    private void createFurnaceCoalSmelting()
    {
        ItemStack dst = new ItemStack(Material.GRAVEL);        
        NBTAPI.setString(dst, "display/Name", "Asche");
        
        getServer().addRecipe(new FurnaceRecipe(dst, Material.COAL));
    }
    
    @Override
    public void onDisable()
    {
        
    }
    
    /*@EventHandler
    public void useTieredToolEvent(PlayerInteractEvent event)
    {
        if(!event.isCancelled() && event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem())
        {
            TieredTool.applyToolToPlayer(event.getPlayer());
        }
    }*/
    
    @EventHandler
    public void disableRepairRecipe(PrepareItemCraftEvent event)
    {
        if(event.isRepair())
            event.getInventory().setResult(new ItemStack(Material.AIR));
    }
}
