package com.DropSimulator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("DropSimulator")
public interface DropSimulatorConfig extends Config
{
	@ConfigItem(
			position = 2,
			keyName = "rightClickConfig",
			name = "Right Click Menu",
			description = "Turn the right click menu off or on"
	)

	default boolean rightClickMenuConfig(){
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "intConfig",
			name = "Default Trials",
			description = "The default number of drop trials to be simulated"
	)
	default int simulatedTrialsConfig()
	{
		return 1000;
	}

	@ConfigItem(
			position = 3,
			keyName = "catacombConfig",
			name = "Catacomb Drop Table",
			description = "Turn the catacomb drop table on/off for monsters with this drop table"
	)
	default boolean catacombConfig()
	{
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "wildernessConfig",
			name = "Wilderness Slayer Drop Table",
			description = "Turn the wilderness slayer drop table on/off for monsters with this drop table"
	)
	default boolean wildernessConfig()
	{
		return true;
	}
}
