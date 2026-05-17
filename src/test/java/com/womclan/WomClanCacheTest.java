package com.womclan;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WomClanCacheTest
{
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void saveAndLoadRoundTripsClanData()
	{
		WomClanCache cache = new WomClanCache(temporaryFolder.getRoot().toPath());
		WomClanData clanData = new WomClanData(
			new WomClanInfo("Wise Old Clan", "WOM CC", 1, 123L, 4.5, 6.5),
			Collections.singletonList(new WomMember("Alpha", "leader", 123L, 4.5, 6.5)),
			Collections.singletonList(new WomAchievement(
				"Alpha",
				"Base 70 Stats",
				"overall",
				"levels",
				737627L,
				Instant.parse("2024-01-01T00:00:00Z")
			)),
			Collections.singletonList(new WomGroupActivity(
				"Alpha",
				"joined",
				"leader",
				Instant.parse("2024-01-02T00:00:00Z")
			)),
			Arrays.asList(
				new WomNameChange(
					"Alpha",
					"Old Alpha",
					"Alpha",
					"approved",
					Instant.parse("2024-01-03T00:00:00Z"),
					Instant.parse("2024-01-02T00:00:00Z")
				)
			)
		);

		cache.save(2300, clanData);

		assertEquals(clanData, cache.load(2300));
	}

	@Test
	public void loadReturnsNullForMissingCache()
	{
		WomClanCache cache = new WomClanCache(temporaryFolder.getRoot().toPath());

		assertNull(cache.load(2300));
	}
}
