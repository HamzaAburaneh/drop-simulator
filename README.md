# Drop Simulator Plugin
This drop simulator plugin allows for the simulation of any number of trials of most NPCs, some non NPCs (CoX, ToB, Barrows, etc.) and all clue scroll tiers. Uses the [osrsbox-api](https://api.osrsbox.com/index.html) to gather an NPC's drop data. Drops are then simulated and displayed in the plugin panel. Hovering over a drop will display the name, quantity, and value of the item stack.

![overview](https://user-images.githubusercontent.com/78482082/108590751-c53f1b00-732a-11eb-97b5-74957b1f2754.png)
![overview2](https://user-images.githubusercontent.com/78482082/108593324-97f96980-7338-11eb-8bec-28445b1f6308.png)
![overview3](https://user-images.githubusercontent.com/78482082/108606342-3c060380-737f-11eb-9971-00cdb9bd1182.PNG)


# Using the plugin
The plugin is pretty straightforward to use. Drops can be simulated either by right-clicking a monster and clicking the popup "simulate drops," or by searching a monster in the panel. The number of trials simulated in both cases is the number of trials in the jpanel. This number can be changed, but have caution when simulating extremely large numbers of trials. The larger the amount of trials, more lag is caused by the amount of time it takes to calculate. Anything up to 1,000,000 trials seems pretty fast on MY PC. Anything greater than 1,000,000 trials should likely not be simulated while in a dangerous area. Test what your PC can handle before simulating in a dangerous area.
## Right click an npc

![rightclickmenu](https://user-images.githubusercontent.com/78482082/108590979-efdda380-732b-11eb-8648-0686f7b9dc1c.png)

If an npc is attackable the menu option to simulate drops will appear. Clicking on the option will simulate drops with the number of trials indicated in the panel. The right-click menu can be turned on or off in plugin configuration.

## Search for an npc

![search1](https://user-images.githubusercontent.com/78482082/108590990-fcfa9280-732b-11eb-8841-ed93f36c2549.png)
![search2](https://user-images.githubusercontent.com/78482082/108590991-fd932900-732b-11eb-8576-1c679465ece9.png)
![search3](https://user-images.githubusercontent.com/78482082/108590992-fe2bbf80-732b-11eb-8d61-2028cd1f19ab.png)

All three searches will result in simulations of General Graardor's drop table. The search is fairly smart, so it is not necessary for the search to match the name exactly. If the input name is searched on the osrs wiki and will bring up that npc's wiki page, then it will search the api to gather drop data.

![search4](https://user-images.githubusercontent.com/78482082/108591268-7cd52c80-732d-11eb-9d18-0a561811fe00.png)

## Settings

1.The default number of trials is the default amount of trials to be simulated.\
2.The right click menu can be turned on or off.\
3.The catacomb drop table can be turned on or off. For example, if it is turned on and a monster that can be found in the catacombs is simulated, the catacomb specific drops can also be rolled. If it is turned off, the catacomb drops will not be rolled.\
4.The wilderness slayer drop table can be turned on or off. It works the same way as the catacomb but for monsters assignable through wilderness slayer.

![config](https://user-images.githubusercontent.com/78482082/108592375-37b3f900-7333-11eb-9ee1-d310896b3c0d.png)

# Future additions
Some additions and improvements likely to be added in the future:
1. The ability to turn on/off brimstone/ecumenial keys.
2. The ability to roll wintertodt crates.
3. The Ability to specify number of points for CoX.
4. The ability to specify number of deaths in ToB.
5. The ability to specify reward potential in Barrows.
6. The ability to roll trials of the gauntlet.
7. The ability to roll trials of barbarian assault gambles.
# Issues
There are some issues with the simulations, most of which are planned to be fixed in future versions.
1. The [osrsbox-api](https://api.osrsbox.com/index.html) has very good data, but it is not perfect. The drop rates are accurate, but without the exact drop-rates published by Jagex the simulation will never be perfect.
2. The [osrsbox-api](https://api.osrsbox.com/index.html) is missing some drops on a few tables. For example, the god wars dungeon rare drop table does not appear on the drop data for any GWD boss, so the drops on this table will never be rolled in the simulation. Another example is the Green Dragon. The majority of its drop table is missing from the api. This is likely the culprit to simulations whose drops make no sense.
3. Any monster that has multiple variants, such as goblins having different drop tables for both armed and unarmed variants, will not simulate accurate drop trials. The api includes all drops in a single goblin table from both variants.
4. The right-click menu appears upon right clicking something that is attackable. Since players are also attackable in certain places, this menu will also appear when right clicking a player in those places. Attempting to simulate the drops on a player will not do anything.
5. The right-click menu will not work with other plugins that also create a new right-click menu.
6. Some drops are different depending upon whether or not the player is in F2P or P2P. The simulation assumes all users are P2P.
7. Brimstone and ecumenial keys are considered tertiary drops. In its current state, the plugin has no way of discerning whether you are actually in the wilderness or on a slayer task from Konar. Therefore, brimstone and ecumenial keys are rolled as tertiary drops for all monsters that have them in their table.
8. The plugin is pretty fast, but simulating anything over 1 million trials while in a dangerous area might get you killed. Test what your PC can handle before simulating lots of trials in a dangerous area. The plugin can handle simulating a LOT of trials, but know that attempting to simulate an absurd amount of trials might take a long time.
9. Monsters whose always drop table isn't comprised of 100% drops, such as Hespori, will not properly roll this table.
10. Monsters with a drop table who drop certain drops in a specific order, such as bludgeon pieces and alchemical hydra brimstone ring pieces will not roll properly. Bludgeon has a workaround that just displays "bludgeon piece", but the Alchemical Hydra will roll 3x as many of these components as it should.

Some assumptions are made which may also lead to inaccuracies in the simulation:
1. If the quantity of a drop is an interval, such as 1-10, the assumption is made that each quantity in the interval has an equal opportunity of appearing.
2. As mentioned before, the droprates of each individual item in the api are accurate but without Jagex published data they are not exact. When adding up the probabilities of each drop they should have a sum of about 1.0 taking rounding errors into consideration. However, some drop tables added up to a probability over 1.0 significant enough that it was not due to rounding errors. For example, Kree'arra's drops added up to over 1.0, but upon subtracting the unique drop table rarities the probability was essentially 1.0. This lead to the belief that some monsters (maybe all?) actually pre-roll their uniques even though the osrs wiki does not specify this as being the case. Therefore, the assumption is made that all unique drops are pre-rolled. This shouldn't have much of an effect on the simulation even if this is not how Jagex actually rolls uniques.
3. The CoX simulation has no way of discerning number of points, it assumes each trial is a 30,000 point solo raid.
4. The ToB simulation has no way of discerning number of deaths, the drop rates are based on the wiki drop rates, which are based on 4 man deathless runs.
5. The Barrows simulation has no way of discerning reward potential, it assumes each trial is all 6 brothers killed with maximum reward potential and that the user has the hard morytania diary unlocked.

# Update 1.3
1. Can now simulate trials of each clue scroll tier.

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