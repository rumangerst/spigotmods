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
import io.github.rumangerst.crystalmagic.elements.DetoxElement;
import io.github.rumangerst.crystalmagic.elements.DisarmElement;
import io.github.rumangerst.crystalmagic.elements.Element;
import io.github.rumangerst.crystalmagic.elements.ExplosionElement;
import io.github.rumangerst.crystalmagic.elements.ExplosionShieldElement;
import io.github.rumangerst.crystalmagic.elements.FeatherfallElement;
import io.github.rumangerst.crystalmagic.elements.FireElement;
import io.github.rumangerst.crystalmagic.elements.FireProtectionElement;
import io.github.rumangerst.crystalmagic.elements.InversionElement;
import io.github.rumangerst.crystalmagic.elements.LevitationElement;
import io.github.rumangerst.crystalmagic.elements.LifeElement;
import io.github.rumangerst.crystalmagic.elements.LightningElement;
import io.github.rumangerst.crystalmagic.elements.MagicElement;
import io.github.rumangerst.crystalmagic.elements.ProjectileProtectionElement;
import io.github.rumangerst.crystalmagic.elements.ShieldElement;
import io.github.rumangerst.crystalmagic.elements.ThornsElement;
import io.github.rumangerst.crystalmagic.elements.UnderwaterBreathingElement;
import io.github.rumangerst.crystalmagic.elements.WaterElement;
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
    
    private CrystalMagicGiveGUI givegui;
    
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
        
        givegui = new CrystalMagicGiveGUI(CustomItemsAPI.api(this));
        getCommand("crystalmagicgive").setExecutor(givegui);
        
        getServer().getPluginManager().registerEvents(givegui, this);
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
        api.registerItem(this, new MagicGem("magicmagiccollectorcrystal", Material.NETHER_STAR, (byte)0, "Magischer Sammlerkristall", this));
        //api.registerItem(this, new MagicElementCrystal("magicmagicelementcrystal", "Magischer Elementkristall (Konduktor)"));
        //api.registerItem(this, new MagicElementCrystal("magicmagicelementinstantcrystal", "Magischer Elementkristall (Speicher)"));
        //api.registerItem(this, new MagicElementProjectileCrystal("magicmagicprojectileelementcrystal", "Magischer Projektilelementkristall (Konduktor)", this));
        api.registerItem(this, new CustomItem("magicbrokenmagiccrystal", Material.NETHER_STAR, "Stumpfer Magischer Kristall"));
    }
    
    private void registerElements()
    {
        CustomItemsAPI api = CustomItemsAPI.api(this);
        
        // Basic elements
        api.registerItem(this, new FireElement("magicfireelement", "Zündet das Ziel an", "Kerze", "Fackel", "Kamin", "Inferno"));
        api.registerItem(this, new WaterElement("magicwaterelement", "Verlangsamt das Ziel", "Brise", "Wind", "Sturm", "Leere"));
        api.registerItem(this, new LifeElement("magiclifeelement", "Heilt das Ziel langsam", "Wiese", "Wald", "Quelle", "Leben"));
        api.registerItem(this, new MagicElement("magicmagicelement", "Stößt das Ziel weg", "Quartz", "Smaragd", "Diamant", "Kristall"));
        api.registerItem(this, new InversionElement("magicinversionelement", "Beschwört eine Kreatur", "Sonnenblume", "Schneeflocke", "Welle", "Fraktal"));
        
        //Derived elements
        api.registerItem(this, new ThornsElement("magicthornselement", "Schild, der Angreifern schadet", "Igel"));
        api.registerItem(this, new FireProtectionElement("magicfireprotectionelement", "Feuerschild", "Frosch"));
        api.registerItem(this, new FeatherfallElement("magicfeatherfallelement", "Verringert Fallschaden des Ziels", "Vogel"));
        api.registerItem(this, new ExplosionShieldElement("magicexplosionprotectionelement", "Verringert Schaden durch Explosionen", "Fels"));
        api.registerItem(this, new ProjectileProtectionElement("magicprojectileprotectionelement", "Verringert Schaden durch Projektile", "Obsidian"));
        api.registerItem(this, new UnderwaterBreathingElement("magicunderwaterbreathingelement", "Das Ziel kann länger unter Wasser atmen", "Fisch"));
        api.registerItem(this, new LightningElement("magicboltelement", "Schleudert einen Blitz auf das Ziel", "Himmel"));
        api.registerItem(this, new ExplosionElement("magicexplosionelement", "Erschafft eine Explosion beim Ziel", "Wut"));
        api.registerItem(this, new ShieldElement("magicshieldelement", "Schild", "Schildkröte"));
        //api.registerItem(this, new Element("magicfocuscollectionelement", "", "Kristall"));
        api.registerItem(this, new LevitationElement("magiclevitationelement", "Lässt das Ziel nach oben schweben", "Wolke"));
        
        api.registerItem(this, new LifeElement("magichealelement", "Heilt das Ziel sofort", "Kräuter"));
        api.registerItem(this, new DetoxElement("magicdetoxelement", "Entfernt Effekte", "Milch"));
        api.registerItem(this, new DisarmElement("magicdisarmelement", "Entwaffnet Ziel", "Erosion"));
    }    
    
}
