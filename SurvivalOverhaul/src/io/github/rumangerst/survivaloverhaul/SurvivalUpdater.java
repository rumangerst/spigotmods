/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.survivaloverhaul;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author ruman
 */
public class SurvivalUpdater implements Runnable
{
    private SurvivalOverhaulPlugin plugin;
    private SurvivalWater survivalwater;
    private SurvivalTemparature survivaltemp;
    
    public  SurvivalUpdater(SurvivalOverhaulPlugin plugin, SurvivalWater water, SurvivalTemparature temp)
    {
        this.plugin = plugin;
        this.survivalwater = water;
        this.survivaltemp = temp;
    }    
    
    @Override
    public void run()
    {
        for(Player player : plugin.getServer().getOnlinePlayers())
        {
            if(player.getGameMode() != GameMode.SURVIVAL)
                continue;
            
            survivalwater.applyHydration(player);
            survivaltemp.applyTargetTemparature(player);
            survivaltemp.applyTemparatureEffects(player);
        }
    }
    
}
