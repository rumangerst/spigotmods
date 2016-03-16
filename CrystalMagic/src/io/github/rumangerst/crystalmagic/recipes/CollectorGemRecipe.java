/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.recipes;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicRecipe;
import io.github.rumangerst.crystalmagic.MagicTable;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author ruman
 */
public class CollectorGemRecipe extends MagicRecipe
{

    public CollectorGemRecipe(CrystalMagicPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public boolean execute(Inventory items, MagicTable.Seal seal, MagicTable.Modus modus, int level)
    {
        return false;
    }
}
