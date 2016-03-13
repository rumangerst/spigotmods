/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.survivaloverhaul;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author ruman
 */
public class SurvivalWater implements Listener
{
    public static double HUNGER_WATER_SATISFACTION = -0.03;
    public static long WATER_HEAT_PERSISTENCE = 10 * 1000 * 60;
    
    public SurvivalOverhaulPlugin plugin;
    private HashMap<String, SurvivalWaterPotion> potions = new HashMap<>();
    private HashMap<Biome, SurvivalWaterPotion> biomeassociations = new HashMap<>();
    private SurvivalWaterPotion defaultpotion; 
    private Random random = new Random();
    
    public SurvivalWater(SurvivalOverhaulPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public double getPlayerWaterLevel(Player player)
    {
        return plugin.data_storage.getDouble(player.getUniqueId().toString() + ".water_level", 1);
    }
    
    public double setPlayerWaterLevel(Player player, double value)
    {
        value = Math.max(0, Math.min(1, value));       
        plugin.data_storage.set(player.getUniqueId().toString() + ".water_level", value);
        
        return value;
    }
    
    private void addPotion(SurvivalWaterPotion potion)
    {
        potions.put(potion.getId(), potion);
        CustomItemsAPI.api(plugin).registerItem(plugin, potion);
    }
    
    private void associateBiomes(String id, Biome ... biomes)
    {
        for(Biome biome : biomes)
        {
            biomeassociations.put(biome, potions.get(id));
        }
    }
    
    public void registerWater()
    {
        addPotion(new SurvivalWaterPotion(plugin,
                "refreshingwater", 
                "Erfrischendes Wasser", 
                "Sauberes und klares Wasser\nErfrischend!", 
                0.8, 
                new PotionEffect(PotionEffectType.REGENERATION, SurvivalOverhaulPlugin.secondsToTicks(3), 1)));
        addPotion(new SurvivalWaterPotion(plugin,
                "clearwater", 
                "Klares Wasser", 
                "Sauberes und klares Wasser", 
                0.7, 
                new PotionEffect(PotionEffectType.REGENERATION, SurvivalOverhaulPlugin.secondsToTicks(1), 1)));
        addPotion(new SurvivalWaterPotion(plugin,
                "normalwater", 
                "Trinkwasser", 
                "Stillt den Durst", 
                0.5));
        addPotion(new SurvivalWaterPotion(plugin,
                "saltwater", 
                "Salzwasser", 
                "Macht nur noch durstiger", 
                -0.4));
        addPotion(new SurvivalWaterPotion(plugin,
                "bacterialwater", 
                "Abgestandenes Wasser", 
                "Riecht seltsam", 
                0.2, 
                new PotionEffect(PotionEffectType.POISON, SurvivalOverhaulPlugin.secondsToTicks(10), 1)));
        addPotion(new SurvivalWaterPotion(plugin,
                "dirtywater", 
                "Verunreinigtes Wasser", 
                "Es ist trübe und riecht seltsam", 
                -0.1, 
                new PotionEffect(PotionEffectType.POISON, SurvivalOverhaulPlugin.secondsToTicks(10), 3)));
        addPotion(new SurvivalWaterPotion(plugin,
                "verydirtywater", 
                "Dreckiges Wasser", 
                "Es schwimmen Sachen darin herum", 
                -0.1, 
                new PotionEffect(PotionEffectType.POISON, SurvivalOverhaulPlugin.secondsToTicks(10), 3)));
        addPotion(new SurvivalWaterPotion(plugin,
                "cauldronwater", 
                "Metallisches Wasser", 
                "Aus einem abgestandenen Wasserbehälter", 
                -0.4, 
                new PotionEffect(PotionEffectType.POISON, SurvivalOverhaulPlugin.secondsToTicks(10), 3)));
        
        //Auto-associate
        for(Biome biome : Biome.values())
        {
            String name = biome.name().toLowerCase();
                        
            if(name.contains("ocean"))
                associateBiomes("saltwater", biome);
            else if(name.contains("beach"))
                associateBiomes("saltwater", biome);
            else if(name.contains("desert"))
                associateBiomes("dirtywater", biome);
            else if(name.contains("ice"))
                associateBiomes("refreshingwater", biome);
            else if(name.contains("hills"))
                associateBiomes("clearwater", biome);
            else if(name.contains("mountain"))
                associateBiomes("clearwater", biome);
            else if(name.contains("forest"))
                associateBiomes("normalwater", biome);
            else if(name.contains("jungle"))
                associateBiomes("verydirtywater", biome);
            else if(name.contains("taiga"))
                associateBiomes("clearwater", biome);
        }
        
        // Direct associate
        associateBiomes("verydirtywater", Biome.SWAMPLAND);
        associateBiomes("dirtywater", Biome.PLAINS);
        associateBiomes("bacterialwater", Biome.RIVER);
        associateBiomes("normalwater", Biome.STONE_BEACH, Biome.FOREST);
        associateBiomes("clearwater", Biome.EXTREME_HILLS, Biome.FOREST_HILLS);
        associateBiomes("refreshingwater", Biome.ICE_MOUNTAINS);
        
        defaultpotion = getPotion("bacterialwater");        
        CustomItemsAPI.api(plugin).registerOverride(Material.POTION, "bacterialwater");
    }
    
    public SurvivalWaterPotion getPotion(String id)
    {
        if(potions.containsKey(id))                   
            return potions.get(id);
        
        return defaultpotion;
    }
    
    public SurvivalWaterPotion getPotion(ItemStack stack)
    {
        if(stack.getType() == Material.POTION)
        {
            if(stack.getDurability() == 0) //Water potion
            {
                String id = NBTAPI.getString(stack, "customitem/id", "");            
                return getPotion(id);
            }
        }
        
        return null;
    }
    
    
    @EventHandler
    public void onPlayerHungers(FoodLevelChangeEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();            
            int diff = player.getFoodLevel() - event.getFoodLevel();
            
            if(diff > 0)
            {
                double satisfaction = HUNGER_WATER_SATISFACTION * diff;
                hydratePlayer(player, satisfaction);
            }
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        setPlayerWaterLevel(event.getPlayer(), 1);        
    }
    
    /**
     * Player needs to build a filter:
     * Bottom: Caldron, , next to the block: wood
     * @param block
     * @return 
     */
    private boolean isAFilterStructure(Block block)
    {
        Block bottom = block.getRelative(BlockFace.DOWN);
        Block east = block.getRelative(BlockFace.EAST);
        Block west = block.getRelative(BlockFace.WEST);
        Block south = block.getRelative(BlockFace.SOUTH);
        Block north = block.getRelative(BlockFace.NORTH);
        
        if(bottom == null || bottom.getType() != Material.CAULDRON)
            return false;
        if(east == null || east.getType() != Material.WOOD)
            return false;
        if(west == null || west.getType() != Material.WOOD)
            return false;
        if(north == null || north.getType() != Material.WOOD)
            return false;
        if(south == null || south.getType() != Material.WOOD)
            return false;
        
        return true;
    }
    
    private void cookWater(Player player, SurvivalWaterPotion potion, Block target)
    {
        switch (potion.getId())
        {
            case "bacterialwater":

                if (random.nextDouble() < 0.3)
                {
                    player.setItemInHand(getPotion("normalwater").make(1));
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Abkochen war erfolgreich!", SurvivalOverhaulPlugin.secondsToTicks(5));
                }
                else
                {
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Abkochen war nicht erfolgreich! Probiere es nochmal!", SurvivalOverhaulPlugin.secondsToTicks(5));
                }
                break;
            default:
                BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Abkochen bringt hier nichts!", SurvivalOverhaulPlugin.secondsToTicks(5));
                break;

        }
        
        NBTAPI.setLong(player.getItemInHand(), "survival_water_hot", System.currentTimeMillis() + WATER_HEAT_PERSISTENCE); //Water is 10 min hot
        player.sendMessage("Du hast das Wasser erhitzt. Es ist für 10min heiß.");

        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.5f, 0.2f);
        player.playSound(player.getLocation(), Sound.FIZZ, 0.5f, 0.2f);
    }
    
    private void filterWaterWithGravel(Player player, SurvivalWaterPotion potion, Block target)
    {
        if(!isAFilterStructure(target))
        {
            return;
        }
        
         switch (potion.getId())
        {
            case "verydirtywater":

                if (random.nextDouble() < 0.3)
                {
                    player.setItemInHand(getPotion("dirtywater").make(1));
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Grobes Filtern war erfolgreich!", SurvivalOverhaulPlugin.secondsToTicks(3));
                }
                else
                {
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Grobes Filtern war nicht erfolgreich! Probiere es nochmal!", SurvivalOverhaulPlugin.secondsToTicks(3));
                }
                break;
            default:
                BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Grobes Filtern bringt hier nichts!", SurvivalOverhaulPlugin.secondsToTicks(3));
                break;

        }

        player.playSound(player.getLocation(), Sound.WATER, 0.5f, 1);
    }
    
    private void filterWaterWithSand(Player player, SurvivalWaterPotion potion, Block target)
    {
        if(!isAFilterStructure(target))
        {
            return;
        }
        
         switch (potion.getId())
        {
            case "dirtywater":

                if (random.nextDouble() < 0.3)
                {
                    player.setItemInHand(getPotion("bacterialwater").make(1));
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Feines Filtern war erfolgreich!", SurvivalOverhaulPlugin.secondsToTicks(3));
                }
                else
                {
                    BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Feines Filtern war nicht erfolgreich! Probiere es nochmal!", SurvivalOverhaulPlugin.secondsToTicks(3));
                }
                break;
            default:
                BountifulAPI.sendActionBar(player, ChatColor.AQUA + "Feines Filtern bringt hier nichts!", SurvivalOverhaulPlugin.secondsToTicks(3));
                break;

        }

        player.playSound(player.getLocation(), Sound.WATER, 0.5f, 1);
    }
    
    @EventHandler
    public void onCleanWater(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        
        if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.POTION)
        {
            Player player = event.getPlayer();
            ItemStack stack = event.getItem();     
            SurvivalWaterPotion potion = getPotion(stack);
            Material block = event.getClickedBlock().getType();
            
            if(potion != null)
            {
               if(null != block)
                   switch (block)
                {
                    case BURNING_FURNACE:
                        cookWater(player, potion, event.getClickedBlock());
                        break;
                    case GRAVEL:
                        filterWaterWithGravel(player, potion, event.getClickedBlock());
                        break;
                    case SAND:
                        filterWaterWithSand(player, potion, event.getClickedBlock());
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    @EventHandler
    public void onShowPotionInformation(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {      
            if(event.hasItem() && event.getItem().getType() == Material.POTION && event.getItem().getDurability() == 0)
            {
                SurvivalWaterPotion p = getPotion(event.getItem());
                
                //Show information about potion
                if(p != null)
                {
                    int temperature = (int)getPotionTemperature(event.getPlayer(), event.getItem());
                    
                    BountifulAPI.sendActionBar(event.getPlayer(), ChatColor.AQUA + "Getränk " + ChatColor.RESET + p.getName() + ", Temparatur " + temperature + "°C", SurvivalOverhaulPlugin.secondsToTicks(5));
                }
            }
        }
    }
    
    @EventHandler
    public void onFillWater(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {            
            Block target = event.getPlayer().getTargetBlock((Set<Material>) null, 5);

            SurvivalWaterPotion p = null;

            if (target.getType() == Material.CAULDRON)
            {
                if (target.getData() != 0)
                {
                    target.setData((byte) (target.getData() - 1));
                }
                else
                {
                    return;
                }

                p = potions.get("cauldronwater");
            }
            else if (target.getType() == Material.STATIONARY_WATER)
            {
                Biome biome = target.getBiome();
                    
                p = biomeassociations.getOrDefault(biome, defaultpotion);
            }

            if (event.getMaterial() == Material.GLASS_BOTTLE)
            {
                if (p != null)
                {
                    //replace
                    if(event.getPlayer().getItemInHand().getAmount() == 1)                    
                        event.getPlayer().setItemInHand(p.make(1));
                    else
                    {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), p.make(1));
                    }
                }

                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }             
            else if(event.getPlayer().getGameMode() == GameMode.SURVIVAL)
            {
                if(p != null && event.getPlayer().isSneaking())
                {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.SPLASH, 0.2f, 1);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.DRINK, 1, 1);
                    
                    //Apply now
                    p.applyTo(event.getPlayer());
                    plugin.temparature.levelPlayerTemperatureTo(event.getPlayer(), plugin.temparature.getPlayerTargetTemparature(event.getPlayer()), 0.6);
                    
                    event.getPlayer().sendMessage("Du hast folgendes getrunken: " + p.getName());

                    plugin.sendSurvivalInformationTo(event.getPlayer());
                }
                else if(p != null)
                {
                    event.getPlayer().sendMessage(ChatColor.BOLD + "[Shift] + [Rechtsklick] " + ChatColor.RESET + "Trinke " + p.getName());
                    event.setCancelled(true);
                }
            }
        }
    }
    
    public double getPotionTemperature(Player player, ItemStack stack)
    {
        double water_temp = plugin.temparature.getPlayerTemparature(player);

        if (stack != null)
        {
            long timeout = NBTAPI.getLong(stack, "survival_water_hot", 0);

            if (timeout != 0)
            {
                long diff = timeout - System.currentTimeMillis();

                if (diff > 0 && diff <= SurvivalWater.WATER_HEAT_PERSISTENCE)
                {
                    water_temp = 50 * ((double) diff / (double) SurvivalWater.WATER_HEAT_PERSISTENCE);
                    water_temp = Math.max(plugin.temparature.getPlayerTemparature(player), water_temp);
                }
            }
        }
        return water_temp;
    }
    
    @EventHandler
    public void onConsumeWater(PlayerItemConsumeEvent event)
    {
        if(event.isCancelled())
            return;

        if (event.getItem().getType() == Material.POTION)
        {
            ItemStack stack = event.getItem();
            SurvivalWaterPotion potion = getPotion(stack);
            if (potion != null)
            {
                potion.applyTo(event.getPlayer());                
                plugin.temparature.levelPlayerTemperatureTo(event.getPlayer(), getPotionTemperature(event.getPlayer(), stack), 0.6);
                
                event.getPlayer().sendMessage("Du hast folgendes getrunken: " + potion.getName());                
                plugin.sendSurvivalInformationTo(event.getPlayer());
            }
        }
    }    
    
    @EventHandler
    public void onConsumeFood(PlayerItemConsumeEvent event)
    {
        if(event.isCancelled())
            return;
        
        double satisfaction = 0;
        
        switch(event.getItem().getType())
        {
            case APPLE:
                satisfaction = 0.02; //adds water!
                break;
            case MELON:
                satisfaction = 0.05; //adds water!
                break;
            case BREAD:
                satisfaction = -0.1;
                break;
            case PORK:
                satisfaction = -0.05;
                break;
            case CARROT:
                satisfaction = -0.01;
                break;
            case POTATO_ITEM:
                satisfaction = -0.01;
                break;
            case COOKIE:
                satisfaction = -0.02;
                break;
            case MUSHROOM_SOUP:
                satisfaction = 0.1;
                break;
            case MUTTON:
                satisfaction = -0.05;
                break;
            case RAW_BEEF:
                satisfaction = -0.02;
                break;
            case RAW_CHICKEN:
                satisfaction = -0.02;
                break;
            case RAW_FISH:
                satisfaction = -0.02;
                break;
        }
        
        if(satisfaction != 0)
        {
            hydratePlayer(event.getPlayer(), satisfaction);
            plugin.sendSurvivalInformationTo(event.getPlayer());
        }
    }
    
    public void hydratePlayer(Player player, double satisfaction)
    {        
        double water = getPlayerWaterLevel(player);
        double newwater = Math.max(0, Math.min(1, water + satisfaction));
        setPlayerWaterLevel(player, newwater);
        
        informPlayerAboutHydration(player, water, newwater);

        //Induce sickness if too much
        if (water == 1 && satisfaction > 0)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, SurvivalOverhaulPlugin.secondsToTicks(10), 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, SurvivalOverhaulPlugin.secondsToTicks(10), 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, SurvivalOverhaulPlugin.secondsToTicks(10), 1));

            BountifulAPI.sendActionBar(player, "Du hast zu viel getrunken!", SurvivalOverhaulPlugin.secondsToTicks(3));
        }
        
        //If drinking after strong hydration, refresh player
        if(water < 0.2 && newwater > 0.3)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4, 1));
        }
        
        //If drinking and temparature of player is high, cool player down   
        /*if(modify_temparature)
        {
            double temparature = plugin.temparature.getPlayerTemparature(player);
            plugin.temparature.setPlayerTemparature(player, temparature + 0.3 * (water_temparature - temparature));
        }*/
    }
    
    public void informPlayerAboutHydration(Player player, double before, double after)
    {
        if((int)(before * 10) != (int)(after * 10))
        {
            /*player.sendMessage(ChatColor.AQUA + "~~ Durst ~~");
            player.sendMessage(rateHydration(after));*/
            plugin.sendSurvivalInformationTo(player);
        }
    }
    
    public void applyHydration(Player player)
    {
        double water = getPlayerWaterLevel(player);
        
        if(water < 0.1)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, SurvivalOverhaulPlugin.secondsToTicks(6), 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, SurvivalOverhaulPlugin.secondsToTicks(6), 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SurvivalOverhaulPlugin.secondsToTicks(6), 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, SurvivalOverhaulPlugin.secondsToTicks(6), 2));
        }
        else if(water < 0.2)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SurvivalOverhaulPlugin.secondsToTicks(6), 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, SurvivalOverhaulPlugin.secondsToTicks(6), 1));
        }
        else if(water < 0.3)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, SurvivalOverhaulPlugin.secondsToTicks(6), 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, SurvivalOverhaulPlugin.secondsToTicks(6), 1));
        }
    }
    
    /*public String rateHydration(double water)
    {
         if(water < 0.1)
             return "Vollkommen Dehydriert";
         else if(water < 0.2)
             return "Sehr Dehydriert";
         else if(water < 0.3)
             return "Dehydriert";
         else if(water < 0.4)
             return "Sehr Durstig";
         else if(water < 0.5)
             return "Durstig";
         else if(water < 0.6)
             return "Etwas durstig";
         else if(water < 0.7)
             return "Hydriert";
         else if(water < 0.8)
             return "Gut hydriert";
         else if(water < 0.9)
             return "Wohl hydriert";
         else
             return "Vollkommen hydriert";
    }*/
}
