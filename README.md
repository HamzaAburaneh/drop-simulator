# Drop Simulator Plugin
This drop simulator plugin allows for the simulation of any number of trials of most NPCs, some non NPCs (CoX, ToB, Barrows, etc.) and all clue scroll tiers. Uses the [osrsbox-api](https://api.osrsbox.com/index.html) to gather an NPC's drop data. Drops are then simulated and displayed in the plugin panel. Hovering over a drop will display the name, quantity, and value of the item stack.

![overview2](https://user-images.githubusercontent.com/78482082/112745122-b7a13480-8f6b-11eb-9cdf-23a933e738b2.png)
![overview3](https://user-images.githubusercontent.com/78482082/112745123-b96af800-8f6b-11eb-9df5-36e424a4861d.png)
![overview4](https://user-images.githubusercontent.com/78482082/112745124-ba9c2500-8f6b-11eb-86d0-088f2a610337.png)

# Using the plugin
The plugin is pretty straightforward to use. Drops can be simulated either by right-clicking a monster and clicking the popup "simulate drops," or by searching a drop source in the panel. The number of trials simulated in both cases is the number of trials in the panel.

![overview1](https://user-images.githubusercontent.com/78482082/114604884-15699800-9c5f-11eb-801f-597e1437ca75.png)

## Right click an npc

![rightclickmenu](https://user-images.githubusercontent.com/78482082/108590979-efdda380-732b-11eb-8648-0686f7b9dc1c.png)

If an npc is attackable, the menu option to simulate drops will appear. Clicking on the option will simulate drops with the number of trials indicated in the panel. The right-click menu can be turned on or off in plugin configuration. If npc attack options are hidden, the right click option of simulating drops will not appear.

## Search for a drop table
### Can search for NPC tables, raids tables, clue scroll tables and more

The search is fairly smart, so it is not necessary for the search to match the name exactly, but the closer the search is to the exact name, the faster the search will likely be. For example, Graardor, grardor, and General Graardor will all return simulations for General Graardor.

The search works by first comparing the input against a list of special drop tables. The special drop tables include clue scrolls, raids, barrows, gwd bosses, slayer bosses, and zulrah. Each of these tables has its own json file of drop data, so their simulations are very fast. If the input is NOT found to match closely enough to any table name in the list, then it will search the input on the wiki. If the wiki returns a page, the title of that page will be used to search the API for drop data. Connecting to the wiki adds simulation time, so the closer a search is to the actual name the more likely the trial will complete faster. (A full second faster in some cases).

The first simulation of any npc using the wiki search or API connection method will always take longer than the following simulations. The connections to the pages are cached for a few minutes.
# Settings

1.The default number of trials is the default amount of trials to be simulated.\
2.The right click menu can be turned on or off.\
3.The catacomb drop table can be turned on or off. For example, if it is turned on and a monster that can be found in the catacombs is simulated, the catacomb specific drops can also be rolled. If it is turned off, the catacomb drops will not be rolled.\
4.The wilderness slayer drop table can be turned on or off. It works the same way as the catacomb but for monsters assignable through wilderness slayer.

![config](https://user-images.githubusercontent.com/78482082/108592375-37b3f900-7333-11eb-9ee1-d310896b3c0d.png)

# Future additions
Some additions and improvements likely to be added in the future:
1. The ability to roll Wintertodt crates.
2. The ability to roll Tempoross drops.   
3. The ability to roll trials of the gauntlet.
4. The ability to roll trials of barbarian assault gambles.
5. All (or at least just the most popular) bosses having their own json files for instantaneous simulation times. (All NPCs would be nice but RuneLite plugins have a file size limit.)
# Issues
There are some issues with the simulations, most of which are planned to be fixed in future versions.
1. The [osrsbox-api](https://api.osrsbox.com/index.html) has very good data, but it is not perfect. The drop rates are accurate, but without the exact drop-rates published by Jagex the simulation will never be perfect.
2. The [osrsbox-api](https://api.osrsbox.com/index.html) is missing some drops on a few tables. For example, the majority of the drop table of a green dragon is missing from the api. This is likely the culprit to simulations whose drops make no sense.
3. Any monster that has multiple variants, such as goblins having different drop tables for both armed and unarmed variants, will not simulate accurate drop trials. The api includes all drops in a single goblin table from both variants.
4. The right-click menu will probably not work with other plugins that also create a new right-click menu.
5. Some drops are different depending upon whether the player is in F2P or P2P. The simulation assumes all users are P2P.
6. Brimstone and ecumenial keys are considered tertiary drops. In its current state, the plugin has no way of discerning whether you are actually in the wilderness or on a slayer task from Konar. Therefore, brimstone and ecumenial keys are rolled as tertiary drops for all monsters that have them in their table.
7. Monsters whose always drop table isn't comprised of 100% drops, such as the Hespori, will not properly roll this table.
8. The Nightmare table will not have accurate results. Its table is really weird, unlike any other table, and will be fixed in the future.

Some assumptions are made which may also lead to inaccuracies in the simulation:
1. If the quantity of a drop is an interval, such as 1-10, the assumption is made that each quantity in the interval has an equal opportunity of appearing.
2. The CoX simulation has no way of discerning number of points, it assumes each trial is a 30,000 point solo raid.
3. The ToB simulation has no way of discerning number of deaths, the drop rates are based on the wiki drop rates, which are based on 4 man deathless runs.
4. The Barrows simulation has no way of discerning reward potential, it assumes each trial is all 6 brothers killed with maximum reward potential and that the user does NOT have the hard Morytania diary unlocked.

# Update 1.3.5
1. Tables built with the custom python script (most bosses) were using placeholder bank IDs for stackable items (Zulrah
scales, seeds, purple sweets, etc.) which returned an incorrect monetary value of 0. These items now use
the correct IDs.

# Update 1.3.4
1. Wilderness boss tables now roll correctly.

# Update 1.3.3
1. No longer causes lag to the game client.
2. UI changes.

# Update 1.3.2
1. More simulation speed increases. Slayer bosses, GWD bosses, and Zulrah now have their own json files causing their simulations to be almost instant.
2. The alchemical hydra table now rolls properly.
3. GWD bosses now roll the GWD rare drop table.
4. ALL unique drops are no longer considered to be pre-roll drops.
5. Players in attackable areas no longer have the simulate drops menu entry.

# Update 1.3.1
1. Drastically improved simulation speeds.

No longer gets the conventional wiki name of a searched drop source. Now finds the Jaro Winkler distance between the search and a list of non npc tables and most bosses. As a result, most bosses and non npc tables have had their speeds increased drastically. The non-npc tables (Clue scrolls, raids, barrows, etc.) are extremely fast. These trials are now rolled almost instantaneously. Most bosses are fast, but not as fast as the non-npc tables. Normal npcs have not had their simulation speeds improved.

2. Barrows no longer assumes hard morytania completed.
3. Unsired rolls bludgeon pieces in the proper order.

# Update 1.3
1. Can now simulate trials of each clue scroll tier using a small json file included in the plugin that was built with a [python script](https://github.com/mxp190009/drop-table-builder).
# Update 1.2
1. Pre-roll drop rolling algorithm improved, the Alchemical hydra now rolls the proper number of pre-rolls except for the brimstone ring pieces.
2. Can now simulate trials of the Theatre of Blood.
3. Can now simulate trials of the Chambers of Xeric.   
4. Can now simulate trials of Barrows.
5. Can now simulate trails of Unsired offerings.
6. Can now simulate trials of the Grotesque Guardians.
7. Fixed total value displaying incorrect values.
8. Fixed panels not ordering properly.

All new added drop sources not coming from an NPC can be simulated from the search bar. As before, the search will find 
it fairly easily. 'Cox', 'cox', 'chambers', and 'raids 1' are all searches that will simulate trials of the Chambers of 
Xeric.
# Update 1.1
1. Fixed lingering jpopupmenus.
2. Fixed trials not updating properly.
