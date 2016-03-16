/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import org.bukkit.inventory.Inventory;

/**
 *
 * @author ruman
 */
public abstract class MagicRecipe
{
    protected CrystalMagicPlugin plugin;
    
    public MagicRecipe(CrystalMagicPlugin plugin)
    {
        this.plugin = plugin;
    }
    
    public abstract boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level);
}
