/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import io.github.rumangerst.crystalmagic.crystalls.CollectorGem;
import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicElementCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicElementProjectileCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.crystalmagic.crystalls.ReactiveGem;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.crystalmagic.patterns.ModusOpen;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class CrystalMagicPlugin extends JavaPlugin implements Listener
{   
    public static PluginLogger LOGGER;
    public MagicTable magictable;
    
    public FileConfiguration data_storage;
    
    public CrystalMagicPlugin()
    {
        LOGGER = new PluginLogger(this);
    }
    
    @Override
    public void onEnable()
    {
        try
        {            
            data_storage.load("crystalmagic.yml");
        }
        catch (IOException | InvalidConfigurationException ex)
        {            
            LOGGER.log(Level.SEVERE, ex.toString());
        }
        
        registerItems();
        magictable = new MagicTable(this);       
        
        getServer().getPluginManager().registerEvents(magictable, this);
        
        
    }
    
    @Override
    public void onDisable()
    {
        magictable.disable();
        
        try
        {
            data_storage.save("crystalmagic.yml");
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, ex.toString());
        }
    }
    
    private void registerItems()
    {
        registerElements();
        registerCrystals();
    }
    
    private void registerCrystals()
    {
        CustomItemsAPI api = CustomItemsAPI.api(this);
        
        //Gems
        api.registerItem(this, new ReactiveGem("magicreactiveemerald", Material.EMERALD, (byte)0, "Reaktiver Smaragd"));
        api.registerItem(this, new ReactiveGem("magicreactivediamond", Material.DIAMOND, (byte)0, "Reaktiver Diamant"));
        api.registerItem(this, new ReactiveGem("magicreactivelapis", Material.INK_SACK, (byte)4, "Reaktiver Lapis"));
        api.registerItem(this, new CollectorGem("magiccollectoremerald", Material.EMERALD, (byte)0, "Sammler-Smaragd"));
        api.registerItem(this, new CollectorGem("magiccollectordiamond", Material.DIAMOND, (byte)0, "Sammler-Diamant"));
        api.registerItem(this, new CollectorGem("magiccollectorlapis", Material.INK_SACK, (byte)4, "Sammler-Lapis"));
        api.registerItem(this, new MagicGem("magicmagicemerald", Material.EMERALD, (byte)0, "Magischer Smaragd"));
        api.registerItem(this, new MagicGem("magicmagicdiamond", Material.DIAMOND, (byte)0, "Magischer Diamant"));
        api.registerItem(this, new MagicGem("magicmagiclapis", Material.INK_SACK, (byte)4, "Magischer Lapis"));
        
        //Magic crystals
        api.registerItem(this, new MagicCrystal("magicmagiccrystal", "Magischer Kristall"));
        api.registerItem(this, new MagicCrystal("magicmagiccollectorcrystal", "Magischer Sammlerkristall"));
        api.registerItem(this, new MagicElementCrystal("magicmagicelementcrystal", "Magischer Elementkristall"));
        api.registerItem(this, new MagicElementProjectileCrystal("magicmagicprojectileelementcrystal", "Magischer Elementkristall (Projektil)", this));
    }
    
    private void registerElements()
    {
        CustomItemsAPI api = CustomItemsAPI.api(this);
        
        // Basic elements
        api.registerItem(this, new Element("magicfireelement", "Kerze", "Fackel", "Kamin", "Tiefe", "Inferno"));
        api.registerItem(this, new Element("magicwaterelement", "Brise", "Wind", "Sturm", "Eis", "Leere"));
        api.registerItem(this, new Element("magicliveelement", "Wiese", "Wald", "Quelle", "Berg", "Leben"));
        api.registerItem(this, new Element("magicmagicelement", "Graphit", "Quartz", "Malachit", "Diamant", "Kristall"));
        api.registerItem(this, new Element("magicinversionelement", "Sonnenblume", "Schneeflocke", "Welle", "Erde", "Fraktal"));
        
        //Derived elements
        api.registerItem(this, new Element("magicthornselement", "Igel"));
        api.registerItem(this, new Element("magicfireprotectionelement", "Frosch"));
        api.registerItem(this, new Element("magicfeatherfallelement", "Vogel"));
        api.registerItem(this, new Element("magicexplosionprotectionelement", "Fels"));
        api.registerItem(this, new Element("magicprojectileprotectionelement", "Obsidian"));
        api.registerItem(this, new Element("magicunderwaterbreathingelement", "Fisch"));
        api.registerItem(this, new Element("magicboltelement", "Himmel"));
        api.registerItem(this, new Element("magicexplosionelement", "Wut"));
        api.registerItem(this, new Element("magicshieldelement", "Schildkröte"));
        api.registerItem(this, new Element("magicfocuscollectionelement", "Kristall"));
        api.registerItem(this, new Element("magiclevitationelement", "Wolke"));
    }    
}
