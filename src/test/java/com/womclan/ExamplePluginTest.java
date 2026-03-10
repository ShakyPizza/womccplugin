package com.womclan;

import com.womclan.WomClanPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WomClanPlugin.class);
		RuneLite.main(args);
	}
}