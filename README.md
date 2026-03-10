# WOM Clan Stats

`WOM Clan Stats` is a RuneLite sidebar plugin that pulls member data from the Wise Old Man API and shows it in a searchable clan panel.

The panel displays:

- Total XP
- EHP
- EHB

Members are sorted by total XP, can be filtered by name, and can be refreshed manually or on a 30 minute timer.

## What It Does

The plugin adds a sidebar button to RuneLite and loads members from a Wise Old Man group using the configured group ID.

Current behavior:

- Fetches members from `https://api.wiseoldman.net/v2/groups/{groupId}/members`
- Falls back to username when a display name is not present
- Sorts the list by total XP descending
- Supports name filtering through the sidebar search field
- Supports automatic refresh every 30 minutes
- Applies a 5 minute cooldown to the manual `Sync Now` action

## Configuration

The plugin exposes two settings:

- `WOM Group ID`: the Wise Old Man group ID for the clan
- `Auto-refresh (every 30 min)`: enables background syncing while RuneLite is open

If no group ID is set, the panel stays in a placeholder state until one is provided.

## Project Layout

- `src/main/java/com/example/WomClanPlugin.java`: plugin lifecycle and refresh scheduling
- `src/main/java/com/example/WomApiClient.java`: Wise Old Man API client
- `src/main/java/com/example/WomClanPanel.java`: main sidebar panel
- `src/main/java/com/example/WomMemberPanel.java`: per-member row rendering
- `src/main/java/com/example/WomClanConfig.java`: RuneLite config entries
- `runelite-plugin.properties`: plugin metadata shown by RuneLite

There are still a few template-era names in the project, especially around the Gradle entrypoint:

- The Gradle project name is still `example`
- The local launcher class is `ExamplePluginTest`
- Unused example classes are still present under `src/main/java/com/example/`

Those do not stop the plugin from running, but they are worth cleaning up if this repository is going to be published or maintained long term.

## Local Development

### Requirements

- Java 11
- Gradle wrapper included in the repository

### Run the plugin in RuneLite

```bash
./gradlew run
```

This launches RuneLite in developer mode and loads `WomClanPlugin` through the test launcher.

### Build the project

```bash
./gradlew build
```

### Build a fat jar

```bash
./gradlew shadowJar
```

The generated artifact is written with the project name from `settings.gradle`, so the file name currently uses `example` rather than `wom-clan-stats`.

## Notes

- The repository currently contains only a basic launcher test and no meaningful automated coverage of the WOM fetch or Swing UI behavior.
- The code depends on RuneLite's client APIs and uses the plugin panel components provided by RuneLite.
