package com.womclan;

import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WomApiClientTest
{
	@Test
	public void parseMembersReadsGroupMemberships() throws IOException
	{
		String json = "{"
			+ "\"id\":2300,"
			+ "\"memberships\":["
			+ "{\"player\":{\"username\":\"alpha\",\"displayName\":\"Alpha\",\"exp\":123,\"ehp\":1.5,\"ehb\":2.5}},"
			+ "{\"player\":{\"username\":\"beta\",\"exp\":456}}"
			+ "]"
			+ "}";

		List<WomMember> members = WomApiClient.parseMembers(json);

		assertEquals(2, members.size());
		assertEquals("Alpha", members.get(0).getDisplayName());
		assertEquals(123L, members.get(0).getTotalXp());
		assertEquals(1.5, members.get(0).getEhp(), 0.0);
		assertEquals(2.5, members.get(0).getEhb(), 0.0);
		assertEquals("beta", members.get(1).getDisplayName());
		assertEquals(456L, members.get(1).getTotalXp());
		assertEquals(0.0, members.get(1).getEhp(), 0.0);
		assertEquals(0.0, members.get(1).getEhb(), 0.0);
	}

	@Test
	public void parseAchievementsReadsRecentMilestones() throws IOException
	{
		String json = "["
			+ "{"
			+ "\"name\":\"Base 70 Stats\","
			+ "\"metric\":\"overall\","
			+ "\"threshold\":737627,"
			+ "\"measure\":\"levels\","
			+ "\"createdAt\":\"2022-10-28T12:42:24.215Z\","
			+ "\"player\":{\"username\":\"alpha\",\"displayName\":\"Alpha\"}"
			+ "}"
			+ "]";

		List<WomAchievement> achievements = WomApiClient.parseAchievements(json);

		assertEquals(1, achievements.size());
		assertEquals("Alpha", achievements.get(0).getDisplayName());
		assertEquals("Base 70 Stats", achievements.get(0).getName());
		assertEquals("overall", achievements.get(0).getMetric());
		assertEquals("levels", achievements.get(0).getMeasure());
		assertEquals(737627L, achievements.get(0).getThreshold());
		assertEquals(Instant.parse("2022-10-28T12:42:24.215Z"), achievements.get(0).getCreatedAt());
	}

	@Test
	public void parseActivityReadsMembershipChanges() throws IOException
	{
		String json = "["
			+ "{"
			+ "\"type\":\"joined\","
			+ "\"role\":null,"
			+ "\"createdAt\":\"2023-10-16T13:20:50.273Z\","
			+ "\"player\":{\"username\":\"beta\",\"displayName\":\"Beta\"}"
			+ "},"
			+ "{"
			+ "\"type\":\"changed_role\","
			+ "\"role\":\"iron\","
			+ "\"createdAt\":\"2023-10-23T20:39:45.104Z\","
			+ "\"player\":{\"username\":\"gamma\"}"
			+ "}"
			+ "]";

		List<WomGroupActivity> activity = WomApiClient.parseActivity(json);

		assertEquals(2, activity.size());
		assertEquals("Beta", activity.get(0).getDisplayName());
		assertEquals("joined", activity.get(0).getType());
		assertEquals("", activity.get(0).getRole());
		assertEquals(Instant.parse("2023-10-16T13:20:50.273Z"), activity.get(0).getCreatedAt());
		assertEquals("gamma", activity.get(1).getDisplayName());
		assertEquals("changed_role", activity.get(1).getType());
		assertEquals("iron", activity.get(1).getRole());
	}
}
