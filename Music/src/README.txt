***********************************
=          Dynamic Music          =
Flexible solution for dynamic music
***********************************

With Dynamic Music you can play custom background music and ambient loops
to your players if they decide to install a mathching resource pack.

Configuration
=============

playerPreferences.yml       Contains player-specific settings (currently only if 
                            music is enabled or not)

config.yml                  General plugin settings

music.yml                   Songs and styles

Songs
=====

A song has a name, a minecraft sound event ID and a duration.
As the server is unable to know the progress of a song the player is playing,
the plugin tries to estimate the progress itself.
The sound event ID can be one of minecraft's included records or any
custom song.

Note: Don't forget to set "stream" : true to your custom music or stopping won't work!

You can use "Minutes:Seconds" annotation or "Milliseconds" to determine the duration.

Example
-------

- ==: io.github.rumangerst.dynamicmusic.Song
  soundid: music.cursed.1
  duration: "1:37"
  name: Cursed1

Style
=====

A style is a set of conditions, a relation between these conditions and a list of songs.
The list of styles will be tested one-by-one from top to bottom. The first match will be used.

Condition                   A condition can apply or don't apply to the current situation of
                            a player. Every condition has a label - one character such as "A" or "B".

                            Down below you will find a documentation for all conditions.

Term                        The relation between the condition is defined by the term. By using complex
                            terms you can connect multiple conditions and refine the experience for your
                            players.

                            Supported operations: AND(X,Y) OR(X,Y) NOT(X) X

                            Note: Don't make any errors in the term string! There are currently no checks!

Songs                       The song "list" is in fact a "list of list" of songs. By setting more than one
                            list of songs, you can play additional ambient loops etc.

                            Up to 4 song lists are supported (This is determined by Minecraft itself)

Example 1
---------

Play songs if you are in the mines (lower than Y=40)

- ==: DynamicMusicStyle
  name: "Mines"
  songs: [["Mines1", "Mines2", "Mines3", "Mines4", "Mines5", "Mines6", "Mines7", "Mines8"]]
  condition-term: "A"
  conditions:
  - ==: DynamicMusicHeightCondition
    label: "A"
    height: "<40"

Example 2
---------

We want to play the "Mines" music only in our survival world

- ==: DynamicMusicStyle
  name: "Mines"
  songs: [["Mines1", "Mines2", "Mines3", "Mines4", "Mines5", "Mines6", "Mines7", "Mines8"]]
  condition-term: "AND(A, B)"
  conditions:
  - ==: DynamicMusicWorldCondition
    label: "A"
    worlds: ["SurvivalWorld"] 
  - ==: DynamicMusicHeightCondition
    label: "B"
    height: "<40"

Example 3
---------

We want MUTATED_SWAMPLAND biome as "cursed land". Don't forget to put this style above "Mines" style

- ==: DynamicMusicStyle
  name: "Cursed Land"
  songs: [["Cursed1", "Cursed2", "Cursed3", "Cursed4", "Cursed5", "Cursed6", "Cursed7"]]
  condition-term: "AND(A, B)"
  conditions:
  - ==: DynamicMusicWorldCondition
    label: "A"
    worlds: ["SurvivalWorld"]
  - ==: DynamicMusicBiomeCondition
    label: "B"
    biomes: ["MUTATED_SWAMPLAND"]

Stopping
========

Unfortunatly there is no "real" method to stop playing music at the client's side.
This plugin uses a "dummy song" to overload Minecraft's streaming system to stop playing music.

You may need to find out the correct properties by yourself.

Here are the properties, we used:
config.yml

StoppingMusic:
  Enable: true
  SoundName: music.empty --> In our resource pack; 15s of silence worked so far
  SoundVolume: 1 --> It is silent, anyways 
  Iterations: 4 --> The song is played 4 times in a row to stop all stream type sounds

Conditions
==========
