/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.rumangerst.dynamicmusic;

import io.github.rumangerst.dynamicmusic.conditions.BiomeCondition;
import io.github.rumangerst.dynamicmusic.conditions.HeightCondition;
import io.github.rumangerst.dynamicmusic.conditions.RainCondition;
import io.github.rumangerst.dynamicmusic.conditions.TimeCondition;
import io.github.rumangerst.dynamicmusic.conditions.WorldCondition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author ruman
 */
public class DynamicMusicPlugin extends JavaPlugin
{
    public static PluginLogger LOGGER;
    public static Random RANDOM = new Random();
    
    public DynamicMusicAPI api;
    public MusicManager musicManager;
    public MusicStyleUpdater styleUpdater;
    
    public FileConfiguration preferencesConfiguration;
    public FileConfiguration musicConfiguration;
    
    public DynamicMusicPlugin()
    {
        LOGGER = new PluginLogger(this);
    }
    
    private void addPluginConfigurationDefaults()
    {
        FileConfiguration conf = getConfig();
        
        conf.addDefault("StoppingMusic.Enable", true);
        conf.addDefault("StoppingMusic.SoundName", "record.11");
        conf.addDefault("StoppingMusic.SoundVolume", 0.1f);
        conf.addDefault("StoppingMusic.Iterations", 4);
        
        conf.addDefault("UpdateStyleTicks", 2 * 20);
        conf.addDefault("UpdateQueueTicks", 5);
        conf.addDefault("GenerateHelp", true);
        
        conf.options().copyDefaults(true);
    }
    
    private void addMusicConfigurationDefaults()
    {
        ArrayList<Song> default_songs = new ArrayList<>();
        
        default_songs.add(new Song("record.cat", "C418 - Cat", 3, 7));
        default_songs.add(new Song("record.blocks", "C418 - Blocks", 5, 43));
        default_songs.add(new Song("record.chirp", "C418 - Chirp", 3, 7));
        default_songs.add(new Song("record.far", "C418 - Far", 3, 12));
        default_songs.add(new Song("record.mall", "C418 - Mall", 3, 18));
        default_songs.add(new Song("record.mellohi", "C418 - Mellohi", 1, 38));
        default_songs.add(new Song("record.stal", "C418 - Stal", 2, 32));
        default_songs.add(new Song("record.strad", "C418 - Strad", 3, 9));
        default_songs.add(new Song("record.wait", "C418 - Wait", 3, 54));
        default_songs.add(new Song("record.11", "C418 - 11", 1, 12));
        default_songs.add(new Song("record.13", "C418 - 13", 3, 3));
        
        musicConfiguration.addDefault("songs", default_songs);
        musicConfiguration.options().copyDefaults(true);
    }
    
    public void loadPluginConfiguration()
    {
        ensureFolders();
        
        if((new File("plugins/DynamicMusic/config.yml")).exists())
        {
            try
            {
                getConfig().load("plugins/DynamicMusic/config.yml");
            }
            catch (IOException | InvalidConfigurationException ex)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try
            {
                getConfig().save("plugins/DynamicMusic/config.yml");
            }
            catch (IOException ex)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    public void loadPlayerPreferencesConfiguration()
    {
        ensureFolders();
        
        if((new File("plugins/DynamicMusic/playerPreferences.yml")).exists())
        {
            try
            {
                preferencesConfiguration.load("plugins/DynamicMusic/playerPreferences.yml");
            }
            catch (IOException | InvalidConfigurationException ex)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            savePlayerPreferencesConfiguration();
        }
    }
    
    public void loadMusicConfiguration()
    {
        ensureFolders();
        
        if((new File("plugins/DynamicMusic/music.yml")).exists())
        {
            try
            {
                musicConfiguration.load("plugins/DynamicMusic/music.yml");
                
                // Register the loaded values into the API
                for(Song song : (List<Song>)musicConfiguration.get("songs", new ArrayList<Song>()))
                {
                    api.registerSong(song);
                }
                
                for(Style style : (List<Style>)musicConfiguration.get("styles", new ArrayList<Style>()))
                {
                    api.registerStyle(style);
                }
            }
            catch (IOException | InvalidConfigurationException ex)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            saveMusicConfiguration();
        }
    }
    
     public void saveMusicConfiguration()
    {
        ensureFolders();
        
        try
        {           
            musicConfiguration.save("plugins/DynamicMusic/music.yml");
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public void savePlayerPreferencesConfiguration()
    {
        ensureFolders();
        
        try
        {
            preferencesConfiguration.save("plugins/DynamicMusic/playerPreferences.yml");
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private void ensureFolders()
    {
        File folder = new File("plugins/DynamicMusic");
        if(!folder.isDirectory())
        {
            folder.mkdirs();
        }
    }
    
    @Override
    public void onEnable()
    {
        api = new DynamicMusicAPI(this);
        musicManager = new MusicManager(this);
        styleUpdater = new MusicStyleUpdater(this);
        
        preferencesConfiguration = new YamlConfiguration();
        musicConfiguration = new YamlConfiguration();
        
        ConfigurationSerialization.registerClass(Song.class, "DynamicMusicSong");
        ConfigurationSerialization.registerClass(Style.class, "DynamicMusicStyle");
        
        api.registerConditionType(BiomeCondition.class, "DynamicMusicBiomeCondition");
        api.registerConditionType(WorldCondition.class, "DynamicMusicWorldCondition");
        api.registerConditionType(HeightCondition.class, "DynamicMusicHeightCondition");
        api.registerConditionType(TimeCondition.class, "DynamicMusicTimeCondition");
        api.registerConditionType(RainCondition.class, "DynamicMusicRainCondition");
        
        addMusicConfigurationDefaults();
        addPluginConfigurationDefaults();
        
        loadPluginConfiguration();
        loadMusicConfiguration();
        loadPlayerPreferencesConfiguration();
        
        getCommand("music").setExecutor(new MusicCommand(this));
        
        getServer().getPluginManager().registerEvents(musicManager, this);
        getServer().getScheduler().runTaskTimer(this, musicManager, 5 * 20, getConfig().getInt("UpdateQueueTicks"));
        getServer().getScheduler().runTaskTimer(this, styleUpdater, 5 * 20, getConfig().getInt("UpdateStyleTicks"));
        
        LOGGER.info("Updating Style every " + getConfig().getInt("UpdateStyleTicks") + " ticks");
        
        if(getConfig().getBoolean("GenerateHelp"))
        {
            api.createHelpFile(new File("plugins/DynamicMusic/README.txt"));
        }
    }
    
    @Override
    public void onDisable()
    {
        savePlayerPreferencesConfiguration();
    }
}
