package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WomApiClient
{
	private static final String API_BASE = "https://api.wiseoldman.net/v2";

	@Inject
	private OkHttpClient okHttpClient;

	/**
	 * Fetches all members of a WOM group and their stats.
	 *
	 * @param groupId the Wise Old Man group ID
	 * @return list of WomMember, sorted by the caller
	 * @throws IOException on network or non-2xx response
	 */
	public List<WomMember> fetchMembers(int groupId) throws IOException
	{
		String url = API_BASE + "/groups/" + groupId;

		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", "WomClanStats-RuneLitePlugin/1.0")
			.build();

		try (Response response = okHttpClient.newCall(request).execute())
		{
			if (!response.isSuccessful())
			{
				throw new IOException("WOM API error " + response.code() + " for group " + groupId);
			}

			String body = response.body().string();
			List<WomMember> members = parseMembers(body);

			log.debug("Fetched {} members for group {}", members.size(), groupId);
			return members;
		}
	}

	static List<WomMember> parseMembers(String body) throws IOException
	{
		JsonObject root = new JsonParser().parse(body).getAsJsonObject();
		JsonArray memberships = root.has("memberships") && root.get("memberships").isJsonArray()
			? root.getAsJsonArray("memberships")
			: new JsonArray();

		List<WomMember> members = new ArrayList<>();
		for (JsonElement elem : memberships)
		{
			JsonObject obj = elem.getAsJsonObject();
			if (!obj.has("player") || obj.get("player").isJsonNull())
			{
				continue;
			}

			JsonObject player = obj.getAsJsonObject("player");
			if (!player.has("username") || player.get("username").isJsonNull())
			{
				continue;
			}

			String displayName = player.has("displayName") && !player.get("displayName").isJsonNull()
				? player.get("displayName").getAsString()
				: player.get("username").getAsString();

			long totalXp = player.has("exp") && !player.get("exp").isJsonNull()
				? player.get("exp").getAsLong()
				: 0L;

			double ehp = player.has("ehp") && !player.get("ehp").isJsonNull()
				? player.get("ehp").getAsDouble()
				: 0.0;

			double ehb = player.has("ehb") && !player.get("ehb").isJsonNull()
				? player.get("ehb").getAsDouble()
				: 0.0;

			members.add(new WomMember(displayName, totalXp, ehp, ehb));
		}

		return members;
	}
}
