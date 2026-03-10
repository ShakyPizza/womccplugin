package com.womclan;

import org.junit.Test;

import java.io.IOException;
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
}
