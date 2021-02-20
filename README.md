# Drop Simulator Plugin
This drop simulator plugin allows for the simulation of any number of trials of most NPCs with a drop table. Uses the [osrsbox-api](https://api.osrsbox.com/index.html) to gather an NPC's drop data. Drops are then simulated and displayed in the plugin panel.

![overview](https://user-images.githubusercontent.com/78482082/108590751-c53f1b00-732a-11eb-97b5-74957b1f2754.png)

# Using the plugin
The plugin is pretty straightforward to use. Drops can be simulated either by right-clicking a monster and clicking the popup "simulate drops," or by searching a monster in the panel.

## Right click an npc

![rightclickmenu](https://user-images.githubusercontent.com/78482082/108590979-efdda380-732b-11eb-8648-0686f7b9dc1c.png)

If an npc is attackable the menu option to simulate drops will appear. Clicking on the option will simulate drops with the number of trials indicated in the panel. The right-click menu can be turned on or off in plugin configuration.

## Search for an npc

![search1](https://user-images.githubusercontent.com/78482082/108590990-fcfa9280-732b-11eb-8841-ed93f36c2549.png)
![search2](https://user-images.githubusercontent.com/78482082/108590991-fd932900-732b-11eb-8576-1c679465ece9.png)
![search3](https://user-images.githubusercontent.com/78482082/108590992-fe2bbf80-732b-11eb-8d61-2028cd1f19ab.png)

All three searches will result in simulations of General Graardor's drop table. The search is fairly smart, so it is not necessary for the search to match the name exactly. If the input name is searched on the osrs wiki and will bring up that npc's wiik page, then it will search the api to gather drop data.

![search4](https://user-images.githubusercontent.com/78482082/108591268-7cd52c80-732d-11eb-9d18-0a561811fe00.png)

## Settings

The default number of trials is the default amount of trials to be simulated.
The right click menu can be turned on or off.
The catacomb drop table can be turned on or off. For example, if it is turned on and a monster that can be found in the catacombs is simulated, the catacomb specific drops can also be rolled. If it is turned off, the catacomb drops will not be rolled.
The wilderness slayer drop table can be turned on or off. It works the same way as the catacomb but for monsters assignable through wilderness slayer. This method works, but 

![config](https://user-images.githubusercontent.com/78482082/108592375-37b3f900-7333-11eb-9ee1-d310896b3c0d.png)



# Issues
There are some issues with the simulations. 
1. The [osrsbox-api](https://api.osrsbox.com/index.html) has very good data, but it is not perfect. The drop rates are accurate, but without the exact drop-rates published by Jagex the simulation will never be perfect.
2. The [osrsbox-api](https://api.osrsbox.com/index.html) is missing some drops on a few tables. For example, the god wars dungeon rare drop table does not appear on the drop data for any GWD boss, so the drops on this table will never be rolled in the simulation. Another example is the Green Dragon. The majority of its drop table is missing from the api. This is likely the culprit to simulations whose drops make no sense.
3. Any monster that has multiple variants, such as goblins having different drop tables for both armed and unarmed variants, will not simulate accurate drop trials. The api includes all drops in a single goblin table from both variants.
4. The right-click menu appears upon right clicking something that is attackable. Since players are also attackable in certain places, this menu will appear also appear when right clicking a player in those places. Clicking the menu on a player will not do anything.
5. The right-click menu will not work with other plugins that also create a new right-click menu.
6. Some drops are different depending upon whether or not the player is in F2P or P2P. The simulation assumes all users are P2P.
7. Monsters that roll two separate main drops per kill and also have a pre-roll drop table (The Alchemical Hydra) are twice as likely to roll the pre-roll table. Therefore, upon simulating Alchemical Hydra drops, the simulation will give twice as many uniques as otherwise should have been expected.
8. Whether or not the drop data of a monster with wilderness specific tertiary drops actually contains the data seems pretty random. Despite being able to turn the wilderness slayer drop table on or off for monsters assignable through wilderness slayer, it is unlikely the simulation will actually roll these drops.
9. Brimstone and ecumenial keys are considered tertiary drops. In its current state, the plugin has no way of discerning whether you are actually in the wilderness or on a slayer task from Konar. Therefore, brimstone and ecumenial keys are rolled as tertiary drops for all monsters that have them in their table.

Some assumptions are made which may also lead to innacuracies in the simulation:
1. If the quantity of a drop is an interval, such as 1-10, the assumption is made that each quantity in the interval has an equal opportunity of appearing.
2. As mentioned before, the droprates of each individual item in the api are accurate but without Jagex published data they are not exact. When adding up the probabilities of each drop they should have a sum of about 1.0 taking rounding errors into consideration. However, some drop tables added up to a probability over 1.0 significant enough that it was not due to rounding errors. For example, Kree'arra's drops added up to over 1.0, but upon subtracting the unique drop table rarities the probability was essentially 1.0. This lead to the belief that some monsters (maybe all?) actually pre-roll their uniques even though the osrs wiki does not specify this as being the case. Therefore, the assumption is made that all unique drops are pre-rolled. This shouldn't have much of an effect on the simulation even if this is not how Jagex actually rolls uniques.
