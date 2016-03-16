/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic.spells;

import io.github.rumangerst.crystalmagic.CrystalMagicPlugin;
import io.github.rumangerst.crystalmagic.MagicTable;
import io.github.rumangerst.customitems.helpers.MathHelper;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;

/**
 * Wrapper around magic table interaction
 * @author ruman
 */
public class MagicTableSpell implements Spell
{
    CrystalMagicPlugin plugin;
    Location tableLocation;
    int tableType;
    int tableMode;
    
    double load = 0;
    double max_load;
    
    public MagicTableSpell(CrystalMagicPlugin plugin, Location tableLocation, int tabletype, int tablemode)
    {
        this.plugin = plugin;
        this.tableLocation = tableLocation;
        this.tableType = tabletype;
        this.tableMode = tablemode;
        
        if(tabletype == MagicTable.MAGIC_TABLE_TYPE_BALANCE_SMALL || tabletype == MagicTable.MAGIC_TABLE_TYPE_ORDER_SMALL)
            max_load = 2;
        else 
            max_load = 4;
    }

    @Override
    public void execute()
    {
        if(plugin.magictable.getTableTypeAt(tableLocation) == tableType && plugin.magictable.getTableModeAt(tableLocation) == tableMode)
        {
            if((int)load > 0)
            {
                tableLocation.getWorld().strikeLightningEffect(tableLocation);
                plugin.magictable.executeRecipe(tableLocation, (int)load);
            }
        }
        else
        {
            tableLocation.getWorld().createExplosion(tableLocation.getX(), tableLocation.getY(), tableLocation.getZ(), 4.0f);
        }        
    }

    @Override
    public boolean load()
    {
        if(plugin.magictable.getTableTypeAt(tableLocation) != tableType || plugin.magictable.getTableModeAt(tableLocation) != tableMode)
            return true;
        
        load = load + 0.5 * (max_load + 0.1 - load);
        load = Math.min(max_load, load); //cannot get more than max load
        
        tableLocation.getWorld().playSound(tableLocation, Sound.BLOCK_CHORUS_FLOWER_GROW, 2.0f, MathHelper.lerp(0.2f, 2.0f, load / max_load));
        tableLocation.getWorld().playSound(tableLocation, Sound.ENTITY_ELDER_GUARDIAN_HURT, 2.0f, 0.2f);
        
        return load >= max_load;
    }

    @Override
    public int manaCost()
    {
        return (int)(load + 1) * (int)(load + 1);
    }
    
}
