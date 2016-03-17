/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystalmagic;

import io.github.rumangerst.crystalmagic.crystalls.CollectorGem;
import io.github.rumangerst.crystalmagic.crystalls.MagicCrystal;
import io.github.rumangerst.crystalmagic.crystalls.MagicGem;
import io.github.rumangerst.crystalmagic.crystalls.ReactiveGem;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.crystalmagic.spells.SpellHandler;
import io.github.rumangerst.customitems.CustomItem;
import io.github.rumangerst.customitems.CustomItemsAPI;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class CrystalMagicPlugin extends JavaPlugin implements Listener
{   
    public static PluginLogger LOGGER;
    public static Random RANDOM  = new Random();
    
    public MagicTable magictable;
    public SpellHandler spellhandler;
    
    public FileConfiguration data_storage;
    
    public CrystalMagicPlugin()
    {
        LOGGER = new PluginLogger(this);        
    }
    
    @Override
    public void onEnable()
    {
        data_storage = getConfig();
        
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
        spellhandler = new SpellHandler(this);
        
        getServer().getPluginManager().registerEvents(magictable, this);        
        getServer().getPluginManager().registerEvents(spellhandler, this);   
        
        getServer().getScheduler().runTaskTimer(this, spellhandler, CustomItemsAPI.secondsToTicks(1), CustomItemsAPI.secondsToTicks(1));
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
        api.registerItem(this, new ReactiveGem("magicreactiveemerald", Material.EMERALD, (byte)0, "Reaktiver Smaragd", this));
        api.registerItem(this, new ReactiveGem("magicreactivediamond", Material.DIAMOND, (byte)0, "Reaktiver Diamant", this));
        api.registerItem(this, new ReactiveGem("magicreactivelapis", Material.INK_SACK, (byte)4, "Reaktiver Lapis", this));
        api.registerItem(this, new ReactiveGem("magicreactivequartz", Material.QUARTZ, (byte)0, "Reaktiver Quartz", this));
        api.registerItem(this, new CollectorGem("magiccollectoremerald", Material.EMERALD, (byte)0, "Sammler-Smaragd", this));
        api.registerItem(this, new CollectorGem("magiccollectordiamond", Material.DIAMOND, (byte)0, "Sammler-Diamant", this));
        api.registerItem(this, new CollectorGem("magiccollectorlapis", Material.INK_SACK, (byte)4, "Sammler-Lapis", this));
        api.registerItem(this, new CollectorGem("magiccollectorquartz", Material.QUARTZ, (byte)0, "Sammler-Quartz", this));
        api.registerItem(this, new MagicGem("magicmagicemerald", Material.EMERALD, (byte)0, "Magischer Smaragd", this));
        api.registerItem(this, new MagicGem("magicmagicdiamond", Material.DIAMOND, (byte)0, "Magischer Diamant", this));
        api.registerItem(this, new MagicGem("magicmagiclapis", Material.INK_SACK, (byte)4, "Magischer Lapis", this));
        api.registerItem(this, new MagicGem("magicmagicquartz", Material.QUARTZ, (byte)0, "Magischer Quartz", this));
        api.registerItem(this, new CustomItem("magicbrokenemerald", Material.EMERALD, (byte)0, "Stumpfer Smaragd"));
        api.registerItem(this, new CustomItem("magicbrokendiamond", Material.DIAMOND, (byte)0, "Stumpfer Diamant"));
        api.registerItem(this, new CustomItem("magicbrokenlapis", Material.INK_SACK, (byte)4, "Stumpfer Lapis"));
        api.registerItem(this, new CustomItem("magicbrokenquartz", Material.QUARTZ, (byte)0, "Stumpfer Quartz"));
        
        //Magic crystals
        api.registerItem(this, new MagicCrystal("magicmagiccrystal", "Magischer Kristall", this));
        //api.registerItem(this, new MagicCrystal("magicmagiccollectorcrystal", "Magischer Sammlerkristall"));
        //api.registerItem(this, new MagicElementCrystal("magicmagicelementcrystal", "Magischer Elementkristall (Konduktor)"));
        //api.registerItem(this, new MagicElementCrystal("magicmagicelementinstantcrystal", "Magischer Elementkristall (Speicher)"));
        //api.registerItem(this, new MagicElementProjectileCrystal("magicmagicprojectileelementcrystal", "Magischer Projektilelementkristall (Konduktor)", this));
        api.registerItem(this, new CustomItem("magicbrokenmagiccrystal", Material.NETHER_STAR, "Stumpfer Magischer Kristall"));
    }
    
    private void registerElements()
    {
        CustomItemsAPI api = CustomItemsAPI.api(this);
        
        // Basic elements
        api.registerItem(this, new Element("magicfireelement", "Kerze", "Fackel", "Kamin", "Inferno"));
        api.registerItem(this, new Element("magicwaterelement", "Brise", "Wind", "Sturm", "Leere"));
        api.registerItem(this, new Element("magiclifeelement", "Wiese", "Wald", "Quelle", "Leben"));
        api.registerItem(this, new Element("magicmagicelement", "Quartz", "Smaragd", "Diamant", "Kristall"));
        api.registerItem(this, new Element("magicinversionelement", "Sonnenblume", "Schneeflocke", "Welle", "Fraktal"));
        
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
