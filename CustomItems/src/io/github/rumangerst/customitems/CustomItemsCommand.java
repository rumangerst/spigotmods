/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.customitems;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class CustomItemsCommand implements CommandExecutor
{
    CustomItemsPlugin plugin;
    
    public CustomItemsCommand(CustomItemsPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings)
    {
        if(cs instanceof Player && !((Player)cs).isOp())
            return false;
        
        if(strings.length != 0)
        {
            String subcommand = strings[0];
            
            if(subcommand.equals("list"))
            {
                cs.sendMessage(ChatColor.RED + "== Custom Item IDs ==");
                
                for(CustomItem item : plugin.getAPI().items.values())
                {
                    cs.sendMessage("    " + item.id);
                }
                
                return true;
            }
            else if(subcommand.equals("give") && cs instanceof Player && strings.length >= 2)
            {
                String id = strings[1];
                int amount  = strings.length >= 3 ? Integer.parseInt(strings[2]) : 1;
                
                CustomItem item = plugin.getAPI().getCustomItem(id);
                
                if(amount <= 0 || item == null)
                    return false;
                
                ((Player)cs).getInventory().addItem(item.make(amount));
                
                return true;
                
            }
            else if(subcommand.equals("giveto")&& strings.length >= 3)
            {
                String name = strings[1];
                String id = strings[2];
                int amount  = strings.length >= 3 ? Integer.parseInt(strings[3]) : 1;
                
                Player target = plugin.getServer().getPlayer(name);
                CustomItem item = plugin.getAPI().getCustomItem(id);
                
                if(amount <= 0 || item == null || target == null)
                    return false;
                
                target.getInventory().addItem(item.make(amount));
                
                return true;
            }
        }
        
        return false;
    }
    
}
