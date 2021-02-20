/*
 * Copyright (c) 2021, Marshall <https://github.com/mxp190009>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
