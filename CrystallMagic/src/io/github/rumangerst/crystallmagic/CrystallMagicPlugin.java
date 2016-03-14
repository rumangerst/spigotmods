/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.crystallmagic;

import io.github.rumangerst.crystallmagic.elements.Element;
import io.github.rumangerst.customitems.CustomItemsAPI;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class CrystallMagicPlugin extends JavaPlugin
{
    public CrystallMagicPlugin()
    {
        
    }
    
    @Override
    public void onEnable()
    {
        registerItems();
    }
    
    @Override
    public void onDisable()
    {
        
    }
    
    private void registerItems()
    {
        registerElements();
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
    }
}
