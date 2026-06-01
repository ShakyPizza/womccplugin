package com.womclan;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

@Slf4j
class WomClanCache
{
	private static final String CACHE_DIRECTORY = "womclan";

	private final Path cacheDir;
	private final Gson gson;

	WomClanCache(Gson gson)
	{
		this(RuneLite.RUNELITE_DIR.toPath().resolve(CACHE_DIRECTORY), gson);
	}

	WomClanCache(Path cacheDir, Gson gson)
	{
		this.cacheDir = cacheDir;
		this.gson = gson.newBuilder()
			.registerTypeAdapter(Instant.class, new InstantAdapter())
			.create();
	}

	WomClanData load(int groupId)
	{
		Path path = cachePath(groupId);
		if (!Files.isRegularFile(path))
		{
			return null;
		}

		try
		{
			String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			return gson.fromJson(json, WomClanData.class);
		}
		catch (RuntimeException | IOException e)
		{
			log.warn("WOM Clan Stats: failed to load cache for group {}: {}", groupId, e.getMessage());
			return null;
		}
	}

	void save(int groupId, WomClanData clanData)
	{
		try
		{
			Files.createDirectories(cacheDir);
			Path path = cachePath(groupId);
			Path tempPath = cacheDir.resolve(path.getFileName() + ".tmp");
			Files.write(tempPath, gson.toJson(clanData).getBytes(StandardCharsets.UTF_8));
			try
			{
				Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (AtomicMoveNotSupportedException e)
			{
				Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e)
		{
			log.warn("WOM Clan Stats: failed to save cache for group {}: {}", groupId, e.getMessage());
		}
	}

	private Path cachePath(int groupId)
	{
		return cacheDir.resolve("group-" + groupId + ".json");
	}

	private static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant>
	{
		@Override
		public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context)
		{
			return src == null ? null : new JsonPrimitive(src.toString());
		}

		@Override
		public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
		{
			return json == null || json.isJsonNull() ? null : Instant.parse(json.getAsString());
		}
	}
}
