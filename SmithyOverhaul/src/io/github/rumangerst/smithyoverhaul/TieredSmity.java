/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.smithyoverhaul;

import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import io.github.rumangerst.customitems.nbt.NBTAPI;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;

/**
 *
 * @author ruman
 */
public class TieredSmity implements Listener
{    
    public static double TEMPARATURE_COLD = 0.1;
    public static double TEMPARATURE_HOT = 0.4;
    public static double TEMPARATURE_RED = 0.5;
    public static double TEMPARATURE_ORANGE = 0.7;
    public static double TEMPARATURE_YELLOW = 0.9;
    public static double TEMPARATURE_DECREASE_OPERATION = 0.05;
    public static double TEMPARATURE_DECREASE_WATER = 0.6;
    public static double TEMPARATURE_INCREASE_FURNACE = 0.07;
    public static double RANDOM_SIGMA = 0.3;
    public static double DETAIL_POLISH_THRESHOLD = 0.7;      
    
    private final Random random = new Random();
    
    private SmithyOverhaulPlugin plugin;
    
    public TieredSmity(SmithyOverhaulPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    private double randomIncreaseWeaker(double x, double x_max, double modifier)
    {
        double sg = modifier * RANDOM_SIGMA;
        double r = (0.5 - random.nextDouble()) * sg;
        
        return Math.min(1, x + (modifier + r) * Math.max(0, x_max - x));
    }
    
    private double randomDecreaseWeaker(double x, double x_min, double modifier)
    {
        double sg = modifier * RANDOM_SIGMA;
        double r = (0.5 - random.nextDouble()) * sg;
        
        return Math.max(0, x - (modifier + r) * Math.min(1, x + x_min));
    }
    
    private boolean isSmityItem(ItemStack stack)
    {
        if(stack != null && stack.getType() != Material.AIR)
        {
            CustomItem tool = CustomItemsAPI.api(plugin).getCustomItem(stack);
            
            if(tool != null)
            {
                return tool.getId().equals("unfinishedironpickaxe") 
                        || tool.getId().equals("unfinishedironshovel")
                        || tool.getId().equals("unfinishedironaxe")
                        || tool.getId().equals("unfinishedironhoe");
            }
        }
        
        return false;
    }
    
    private boolean initSmityItem(ItemStack stack)
    {
        if(!NBTAPI.has(stack, "tieredsmithy"))
        {            
            NBTAPI.setDouble(stack, "tieredsmithy/purity", random.nextDouble() / 2.0);
            NBTAPI.setDouble(stack, "tieredsmithy/form_rough", 0);
            NBTAPI.setDouble(stack, "tieredsmithy/form_fine", 0);
            NBTAPI.setDouble(stack, "tieredsmithy/form_detail", 0);
            NBTAPI.setDouble(stack, "tieredsmithy/temparature", 0);
            NBTAPI.setDouble(stack, "tieredsmithy/hardness", 0);
            NBTAPI.setLong(stack, "tieredsmithy/action_timeout", 0);
            
            return true;
        }
        
        return false;
    }
    
    private boolean canDoSomething(ItemStack stack)
    {
        long current = System.currentTimeMillis();
        long timeout = NBTAPI.getLong(stack, "tieredsmithy/action_timeout", 0);
        
        long diff = timeout - current;        
        
        return diff < 0 || diff >= 20000; //already over or more than 20s (maybe caused by server restart etc.) 
    }
    
    private void introduceTimeout(ItemStack stack, double seconds)
    {
        int ticks = (int)(seconds * 1000.0);
        
        NBTAPI.setLong(stack, "tieredsmithy/action_timeout", System.currentTimeMillis() + ticks);
    }
    
    private void updateLore(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
        ArrayList<String> info = new ArrayList<>();
        
        if (isSmityItem(player.getItemInHand()))
        {

            double purity = NBTAPI.getDouble(stack, "tieredsmithy/purity", 0);
            double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);
            double form_rough = NBTAPI.getDouble(stack, "tieredsmithy/form_rough", 0);
            double form_fine = NBTAPI.getDouble(stack, "tieredsmithy/form_fine", 0);
            double form_detail = NBTAPI.getDouble(stack, "tieredsmithy/form_detail", 0);
            double hardness = NBTAPI.getDouble(stack, "tieredsmithy/hardness", 0);

            info.add(ChatColor.BOLD + "Temparatur " + ChatColor.RESET + rateTemparature_chat(temparature));
            info.add(ChatColor.BOLD + "Metallqualität " + ChatColor.RESET + ratePurity(purity));

            if (temparature < TEMPARATURE_COLD)
            {
                info.add(ChatColor.BOLD + "Härte " + ChatColor.RESET + rateHardness(hardness));
            }

            if (temparature < TEMPARATURE_ORANGE)
            {
                info.add(ChatColor.BOLD + "Form " + ChatColor.RESET + rateRoughForm(form_rough));

                if (temparature < TEMPARATURE_RED)
                {
                    info.add(ChatColor.BOLD + "Ausarbeitung " + ChatColor.RESET + rateFineForm(form_fine));

                    if (temparature < TEMPARATURE_COLD)
                    {
                        info.add(ChatColor.BOLD + "Oberfläche " + ChatColor.RESET + rateDetail(form_detail));
                    }
                }
            }

            //Update lore           
            NBTAPI.setStringList(stack, "display/Lore", info);
        }

        
        for(int i = 0; i < 10 - 3 - info.size() - additionalinformation.size(); ++i)
            player.sendMessage("");
        
        player.sendMessage(ChatColor.BLUE.toString() + "Schmieden");
        player.sendMessage(ChatColor.YELLOW + "---------");
        player.sendMessage("");
        
        for(String t : info)
        {
            player.sendMessage(t);            
        }
        
        for(String t : additionalinformation)
        {
            player.sendMessage(t);
        }
        
    }
    
    private void heat(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {               
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);        
        double hardness = NBTAPI.getDouble(stack, "tieredsmithy/hardness", 0);
        
        //double m = 0.05 + random.nextDouble() / 20.0;
        //double newtemparature = Math.min(1, temparature + m * (1.0 - temparature));
        
        double newtemparature = randomIncreaseWeaker(temparature, 1, TEMPARATURE_INCREASE_FURNACE);
        double newhardness = Math.max(0, hardness - newtemparature);
        
        NBTAPI.setDouble(stack, "tieredsmithy/temparature", newtemparature); 
        NBTAPI.setDouble(stack, "tieredsmithy/hardness", newhardness);
        
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.5f, 0.2f);
        introduceTimeout(stack, 1);
        
        if(newtemparature >= 0.94)
        {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(null);

            additionalinformation.add(ChatColor.RED + "Es ist geschmolzen!");
        }
    }
    
    private void water(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);          
        double hardness = NBTAPI.getDouble(stack, "tieredsmithy/hardness", 0);
        
        //double m = 0.3 + random.nextDouble() / 10.0;
        //double newtemparature = Math.max(0, temparature - m * temparature);
        
        double newtemparature = randomDecreaseWeaker(temparature, 0, TEMPARATURE_DECREASE_WATER);
        
        NBTAPI.setDouble(stack, "tieredsmithy/temparature", newtemparature); 
        NBTAPI.setDouble(stack, "tieredsmithy/hardness", Math.max(hardness, temparature));
        
        if(temparature >= TEMPARATURE_HOT)
            player.playSound(player.getLocation(), Sound.FIZZ, 1, 1); //if temp high enough
        else
            player.playSound(player.getLocation(), Sound.SPLASH, 0.8f, 1);
        introduceTimeout(stack, 1);
    }
    
    private void smith(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
        double purity = NBTAPI.getDouble(stack, "tieredsmithy/purity", 0);
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);
        double form_rough = NBTAPI.getDouble(stack, "tieredsmithy/form_rough", 0);
        double form_fine = NBTAPI.getDouble(stack, "tieredsmithy/form_fine", 0);        
                
        if(temparature < TEMPARATURE_HOT)
        {
            // OK. Player wants to destroy it
            if(random.nextDouble() < 0.33)
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                player.setItemInHand(null);
                
                additionalinformation.add(ChatColor.RED + "Du hast dein Werkstück kaputtgehauen!");
                
                return;
            }
            
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", 0);
        }
        else if(temparature < TEMPARATURE_RED)
        {
            //double m = 0.05 + random.nextDouble() / 20.0;
            //double newfineform = Math.min(1, form_fine + m * (1.0 - form_fine));
            
            double newform = randomIncreaseWeaker(form_fine, 1, 0.045);
            NBTAPI.setDouble(stack,"tieredsmithy/form_fine", newform);
            
            // Destroy all other properties
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", 0);
        }
        else if(temparature < TEMPARATURE_ORANGE)
        {
            //double m = 0.05 + random.nextDouble() / 20.0;
            //double newroughform = Math.min(1, form_rough + m * (1.0 - form_rough));

            double newform = randomIncreaseWeaker(form_rough, 1, 0.07);
            NBTAPI.setDouble(stack,"tieredsmithy/form_rough", newform);
            
            // Destroy all other properties
            NBTAPI.setDouble(stack,"tieredsmithy/form_fine", 0);
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", 0);
        }
        else if(temparature < 0.9)
        {
            //double m = 0.05 + random.nextDouble() / 20.0;
            //double newpurity = Math.min(1, purity + m * (1.0 - purity));

            double newpurity = randomIncreaseWeaker(purity, 1, 0.05);
            NBTAPI.setDouble(stack,"tieredsmithy/purity", newpurity);
            
            // Destroy all other properties
            NBTAPI.setDouble(stack,"tieredsmithy/form_rough", 0);
            NBTAPI.setDouble(stack,"tieredsmithy/form_fine", 0);
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", 0);
        }
        else
        {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(null);

            additionalinformation.add(ChatColor.RED + "Du hast dein Werkstück kaputtgehauen!");

            return;
        }
        
        //Working decreases temparature
        //double m = 0.05 + random.nextDouble() / 20.0;
        //double newtemparature = Math.max(0, temparature - m * temparature);
        
        double newtemparature = randomDecreaseWeaker(temparature, 0, TEMPARATURE_DECREASE_OPERATION);
        NBTAPI.setDouble(stack,"tieredsmithy/temparature", newtemparature);
        
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
        introduceTimeout(stack, 3);
    }
    
    private void smoothen(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);
        double form_detail = NBTAPI.getDouble(stack, "tieredsmithy/form_detail", 0); 

        if(temparature < TEMPARATURE_COLD)
        {
            //double m = 0.05 + random.nextDouble() / 20.0;
            //double newdetailedform = Math.min(1, form_detail + m * (0.6 - form_detail));
            
            double newdetailedform = randomIncreaseWeaker(form_detail, DETAIL_POLISH_THRESHOLD + 0.1, 0.055);
            
            if(form_detail >= DETAIL_POLISH_THRESHOLD)
            {
                newdetailedform = Math.max(0, form_detail - 0.1); //Punish using the wrong tool
                
                additionalinformation.add(ChatColor.RED + "Du hast dein Werkstück zerkratzt! Poliere es lieber!");
            }

            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", newdetailedform);
        }
        else if(temparature < TEMPARATURE_HOT)
        {
            double form_fine = NBTAPI.getDouble(stack, "tieredsmithy/form_fine", 0);
            // Damage the properties
            double newdetailedform = randomDecreaseWeaker(form_detail, 0, 0.05);
            double newfineform = randomDecreaseWeaker(form_fine, 0, 0.05);
            
            additionalinformation.add(ChatColor.RED + "Du hast dein Werkstück beschädigt! Es ist zu heiß um geschliffen zu werden!");
            
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", newdetailedform);
            NBTAPI.setDouble(stack,"tieredsmithy/form_fine", newfineform);
        }
        else
        {
            // OK. Player wants to destroy it
            if(random.nextDouble() < 0.33)
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                player.setItemInHand(null);
                
                additionalinformation.add(ChatColor.RED + "Dein Werkstück ist kaputtgegangen! Kühle es vorher ab!");
                
                return;
            }
            
            NBTAPI.setDouble(stack,"tieredsmithy/form_detail", 0);
        }
        
        player.playSound(player.getLocation(), Sound.HORSE_BREATHE, 1, 1);
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1.4f);
        
        introduceTimeout(stack, 1);
    }
    
    private void polish(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
       
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);           
        double form_detail = NBTAPI.getDouble(stack, "tieredsmithy/form_detail", 0);
        
        if(temparature < TEMPARATURE_COLD)
        {
            //double m = 0.05 + random.nextDouble() / 20.0;
            //double newdetailedform = Math.min(1, form_detail + m * (0.6 - form_detail));
            
            double newdetailedform = randomIncreaseWeaker(form_detail, 1, 0.1);
            
            if(form_detail < DETAIL_POLISH_THRESHOLD)
            {
                additionalinformation.add(ChatColor.RED + "Das hat nichts gebracht! Schleife dein Werkstück vorher!");
            }
            else
            {
                NBTAPI.setDouble(stack,"tieredsmithy/form_detail", newdetailedform);
            }
        }
        else if(temparature < TEMPARATURE_HOT)
        {
            block.setType(Material.AIR);
            
            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
            player.playSound(player.getLocation(), Sound.FIRE, 1, 1);
            additionalinformation.add(ChatColor.RED + "Du hast die Wolle in Brand gesteckt!");
        }
        else
        {
            // OK. Player wants to destroy it
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(null);
            
            // Destroy the polish tool
            block.setType(Material.AIR);
            
            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
            player.playSound(player.getLocation(), Sound.FIRE, 1, 1);

            additionalinformation.add(ChatColor.RED + "Dein Werkstück ist kaputtgegangen! Kühle es vorher ab!");
            additionalinformation.add(ChatColor.RED + "Du hast die Wolle in Brand gesteckt!");
        }
        
        player.playSound(player.getLocation(), Sound.HORSE_BREATHE, 1, 1);
        introduceTimeout(stack, 1);
    }
    
    private void finish_durability(ItemStack stack, double durability)
    {        
        //Handle durability now
        if (durability < 0.5)
        {
            int max_durability = stack.getDurability();
            int new_durability = (int) (max_durability * durability);

            stack.setDurability((short)new_durability);
        }
        else if (durability < 0.6)
        {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        else if (durability < 0.7)
        {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
        }
        else if (durability < 0.8)
        {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        }
        else if (durability < 0.9)
        {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 4);
        }
        else
        {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        }
    }
    
    private void finish_tool_precision(ItemStack stack, double precision)
    {
        if (precision < 0.5)
        {
           
        }      
        else if (precision < 0.7)
        {
            stack.addUnsafeEnchantment(Enchantment.LUCK, 1);
        }       
        else if (precision < 0.9)
        {
            stack.addUnsafeEnchantment(Enchantment.LUCK, 2);
        }
        else
        {
            stack.addUnsafeEnchantment(Enchantment.LUCK, 3);
        }
    }
    
    private void finish_tool_efficiency(ItemStack stack, double efficiency)
    {
        if (efficiency < 0.5)
        {
           
        } 
        else if (efficiency < 0.7)
        {
            stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        }       
        else if (efficiency < 0.9)
        {
            stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
        }
        else
        {
            stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        }
    }
    
    private void finish(ItemStack stack, Block block, Player player, ArrayList<String> additionalinformation)
    {
        double purity = NBTAPI.getDouble(stack, "tieredsmithy/purity", 0);
        double temparature = NBTAPI.getDouble(stack, "tieredsmithy/temparature", 0);
        double form_rough = NBTAPI.getDouble(stack, "tieredsmithy/form_rough", 0);
        double form_fine = NBTAPI.getDouble(stack, "tieredsmithy/form_fine", 0);
        double form_detail = NBTAPI.getDouble(stack, "tieredsmithy/form_detail", 0);
        double hardness = NBTAPI.getDouble(stack, "tieredsmithy/hardness", 0);
        
        if(temparature >= TEMPARATURE_HOT)
        {
            // OK. Player wants to destroy it
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(null);

            additionalinformation.add("Dein Werkstück ist kaputtgegangen! Kühle es vorher ab!");
            
            block.setType(Material.AIR);
            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
            player.playSound(player.getLocation(), Sound.FIRE, 1, 1);
            additionalinformation.add(ChatColor.RED + "Du hast den Tisch in Brand gesteckt!");
            
            return;
        }        
        else if(temparature >= TEMPARATURE_COLD)
        {
            block.setType(Material.AIR);
            player.playSound(player.getLocation(), Sound.FIRE_IGNITE, 1, 1);
            player.playSound(player.getLocation(), Sound.FIRE, 1, 1);
            additionalinformation.add(ChatColor.RED + "Du hast den Tisch in Brand gesteckt!");
            
            return;
        }
        else if(stack.getType() == Material.IRON_PICKAXE || stack.getType() == Material.IRON_AXE || stack.getType() == Material.IRON_SPADE || stack.getType() == Material.IRON_HOE)
        {
            double durability = purity * Math.pow(Math.E, -0.5 * Math.pow((hardness - 0.6) / 0.3, 2));
            double exhaustion = 1.0 - form_rough;
            double efficiency = form_fine * form_rough;
            double precision = (1 - hardness) * form_detail;
            
            finish_durability(stack, durability);
            finish_tool_precision(stack, precision);
            finish_tool_efficiency(stack, efficiency);
            
             //Store the final data in the tag  
            NBTAPI.setString(stack, "craftedtool/manufacturer", player.getName());
            NBTAPI.setDouble(stack,"craftedtool/durability", durability);
            NBTAPI.setDouble(stack,"craftedtool/exhaustion", exhaustion);
            NBTAPI.setDouble(stack,"craftedtool/efficiency", efficiency);
            NBTAPI.setDouble(stack,"craftedtool/precision", precision);
            
            //Transform the tool
            String dst_id = CustomItemsAPI.api(plugin).getCustomItem(stack).getId().replace("unfinished", "crafted");
            CustomItemsAPI.api(plugin).getCustomItem(dst_id).transform(stack);
        
            additionalinformation.add(ChatColor.RED + "Du hast den Werkstück fertiggestellt!");
        }
        
        NBTAPI.remove(stack, "tieredsmithy");
        
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(!event.isCancelled() && event.getPlayer() != null && isSmityItem(event.getPlayer().getItemInHand()))
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInteraction(PlayerInteractEvent event)
    {
        if(event.isCancelled())
            return;
        
        //Disable anvil
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getGameMode() == GameMode.SURVIVAL)
        {
            Block block = event.getClickedBlock();
            
            if(block.getType() == Material.ANVIL)
            {
                event.setCancelled(true);
            }
        }
        else if(event.getAction() == Action.LEFT_CLICK_BLOCK && isSmityItem(event.getItem()))
        {
            ItemStack stack = event.getItem();      
            if(initSmityItem(stack))
            {
                event.getPlayer().sendMessage("Du hast mit dem Schmieden begonnen!");
            }
            
            if(!canDoSomething(stack))
            {
                //event.entityPlayer.addChatComponentMessage(new ChatComponentText("Du kannst dies noch nicht tun!"));
                return;
            }
            
            Block block = event.getClickedBlock();
            ArrayList<String> additionalinformation = new ArrayList<>();
            
            if (null != block.getType())
            {
                switch (block.getType())
                {
                    case BURNING_FURNACE:
                        heat(stack, block, event.getPlayer(), additionalinformation);
                        break;
                    case CAULDRON:
                        Cauldron c = new Cauldron(block.getData());
                        if (!c.isEmpty())
                        {
                            water(stack, block, event.getPlayer(), additionalinformation);
                        }
                        break;
                    case ANVIL:
                        smith(stack, block, event.getPlayer(), additionalinformation);
                        break;
                    case COBBLESTONE:
                        smoothen(stack, block, event.getPlayer(), additionalinformation);
                        break;
                    case WOOL:
                        polish(stack, block, event.getPlayer(), additionalinformation);
                        break;
                    case WORKBENCH:
                        finish(stack, block, event.getPlayer(), additionalinformation);
                        break;
                    default:
                        break;
                }

                updateLore(stack, block, event.getPlayer(), additionalinformation);
                event.setCancelled(true); //Cancel the event
            }
        }
    }
        
     public static String rateHardness(double percentage)
    {
        if(percentage < 0.1)
            return "Sehr weich";
        else if(percentage < 0.2)
            return "Sehr schlecht ausgearbeitet";
        else if(percentage < 0.3)
            return "Weich";
        else if(percentage < 0.4)
            return "Etwas weich";
        else if(percentage < 0.5)
            return "Hart";
        else if(percentage < 0.6)
            return "Härter";
        else if(percentage < 0.7)
            return "Wenig spröde";
        else if(percentage < 0.8)
            return "Etwas spröde";
        else if(percentage < 0.9)
            return "Spröde";
        else
            return "Sehr spröde";
    }
    
    public static String rateDetail(double percentage)
    {
        if(percentage < DETAIL_POLISH_THRESHOLD)
        {
            percentage = percentage / DETAIL_POLISH_THRESHOLD;
            
            if(percentage < 0.1)
                return "Sehr uneben";
            else if(percentage < 0.3)
                return "Uneben";
            else if(percentage < 0.5)
                return "Wenig uneben";
            else if(percentage < 0.7)
                return "Akzeptabel geschliffen";
            else if(percentage < 0.9)
                return "Exzellent geschliffen";
            else
                return "Exzellent geschliffen";
        }
        else
        {
            percentage = (percentage - DETAIL_POLISH_THRESHOLD) / (1 - DETAIL_POLISH_THRESHOLD);
            
            if(percentage < 0.1)
                return "Unpoliert";      
            else if(percentage < 0.3)
                return "Sehr wenig poliert";
            else if(percentage < 0.5)
                return "Wenig poliert";
            else if(percentage < 0.7)
                return "Schön poliert";
            else if(percentage < 0.9)
                return "Glänzend poliert";
            else
                return "Gewissenhaft poliert";
        }
    }
    
    public static String ratePurity(double percentage)
    {
        if(percentage < 0.1)
            return "Vollkommen verunreinigt";
        else if(percentage < 0.2)
            return "Sehr stark verunreinigt";
        else if(percentage < 0.3)
            return "Stark verunreinigt";
        else if(percentage < 0.4)
            return "Sehr verunreinigt";
        else if(percentage < 0.5)
            return "Verunreinigt";
        else if(percentage < 0.6)
            return "Leicht verunreinigt";
        else if(percentage < 0.7)
            return "Etwas verunreinigt";
        else if(percentage < 0.8)
            return "Kaum verunreinigt";
        else if(percentage < 0.9)
            return "Fast rein";
        else
            return "Pur";
    }
    
    public static String rateFineForm(double percentage)
    {
        if(percentage < 0.1)
            return "Nicht ausgearbeitet";
        else if(percentage < 0.2)
            return "Sehr schlecht ausgearbeitet";
        else if(percentage < 0.3)
            return "Schlecht ausgearbeitet";
        else if(percentage < 0.4)
            return "Etwas ausgearbeitet";
        else if(percentage < 0.5)
            return "Wenig ausgearbeitet";
        else if(percentage < 0.6)
            return "Akzeptabel ausgearbeitet";
        else if(percentage < 0.7)
            return "Ordentlich ausgearbeitet";
        else if(percentage < 0.8)
            return "Schön ausgearbeitet";
        else if(percentage < 0.9)
            return "Exzellent ausgearbeitet";
        else
            return "Meisterlich ausgearbeitet";
    }
    
    public static String rateRoughForm(double percentage)
    {
        if(percentage < 0.1)
            return "Vollkommen unförmig";
        else if(percentage < 0.2)
            return "Sehr stark verformt";
        else if(percentage < 0.3)
            return "Stark verformt";
        else if(percentage < 0.4)
            return "Verformt";
        else if(percentage < 0.5)
            return "Etwas verformt";
        else if(percentage < 0.6)
            return "Akzeptabel geformt";
        else if(percentage < 0.7)
            return "Ordentlich geformt";
        else if(percentage < 0.8)
            return "Gut geformt";
        else if(percentage < 0.9)
            return "Exzellent geformt";
        else
            return "Meisterlich geformt";
    }
    
    public static String rateTemparature_chat(double percentage)
    {
        if(percentage < TEMPARATURE_COLD)
            return ChatColor.AQUA.toString() + "Kalt";
        else if(percentage < TEMPARATURE_HOT)
            return ChatColor.GRAY.toString() + "Heiß";
        else if(percentage < TEMPARATURE_RED)
            return ChatColor.RED.toString() +"Rot glühend";
        else if(percentage < TEMPARATURE_ORANGE)
            return ChatColor.GOLD.toString() +"Orange glühend";
        else if(percentage < TEMPARATURE_YELLOW)
            return ChatColor.YELLOW.toString() +"Gelb glühend";
        else
            return ChatColor.UNDERLINE.toString() +"Weiß glühend";
    }
    
    public static String rateTemparature(double percentage)
    {
        if(percentage < TEMPARATURE_COLD)
            return "Kalt";
        else if(percentage < TEMPARATURE_HOT)
            return "Heiß";
        else if(percentage < TEMPARATURE_RED)
            return "Rot glühend";
        else if(percentage < TEMPARATURE_ORANGE)
            return "Orange glühend";
        else if(percentage < TEMPARATURE_YELLOW)
            return "Gelb glühend";
        else
            return "Weiß glühend";
    }
    
    public static String ratePercentage(double percentage)
    {
        if(percentage < 0.1)
            return "Grausam";
        else if(percentage < 0.2)
            return "Sehr schlecht";
        else if(percentage < 0.3)
            return "Schlecht";
        else if(percentage < 0.4)
            return "Roh";
        else if(percentage < 0.5)
            return "Grob";
        else if(percentage < 0.6)
            return "Akzeptabel";
        else if(percentage < 0.7)
            return "Ordentlich";
        else if(percentage < 0.8)
            return "Gut";
        else if(percentage < 0.9)
            return "Exzellent";
        else
            return "Meister";
    }
}
