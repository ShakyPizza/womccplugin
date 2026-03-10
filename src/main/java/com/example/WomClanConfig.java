package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("womclan")
public interface WomClanConfig extends Config
{
	@ConfigItem(
		keyName = "groupId",
		name = "WOM Group ID",
		description = "Your clan's Wise Old Man group ID (visible in the URL on wiseoldman.net)",
		position = 1
	)
	default int groupId()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "autoRefresh",
		name = "Auto-refresh (every 30 min)",
		description = "Automatically sync clan stats every 30 minutes while the client is running",
		position = 2
	)
	default boolean autoRefresh()
	{
		return true;
	}
}
