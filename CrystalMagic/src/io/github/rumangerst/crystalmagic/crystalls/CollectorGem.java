/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.crystalls;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicWorld;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author ruman
 */
public class CollectorGem extends ReactiveGem
{
    public CollectorGem(String id, Material type, byte data, String name, CrystalMagicPlugin plugin)
    {
        super(id, type, data, name, plugin);
    }
    
    @Override
    public void prepareFill(Player player)
    {
        fill(player);
    }
}
