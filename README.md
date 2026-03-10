# WOM Clan Stats

`WOM Clan Stats` is a RuneLite plugin that syncs a Wise Old Man group into the client and displays the member list in a sidebar panel, with an optional expanded desktop window for the full table view.

## What It Does

The plugin adds a navigation button to RuneLite and reads clan data from the Wise Old Man group endpoint:

- `GET https://api.wiseoldman.net/v2/groups/{groupId}`
- Parses the group's `memberships` array
- Extracts each member's display name, role, total XP, EHP, and EHB

## Features

- Sidebar panel with a searchable member list
- Members sorted by total XP descending after each sync
- Member roles shown directly in the sidebar
- Manual `Sync Now` action with a 5 minute cooldown
- Optional automatic refresh every 30 minutes
- Separate `GUI` window with a sortable table view
- Graceful handling for missing display names or missing stat fields

## UI Behavior

### Sidebar panel

The main RuneLite panel includes:

- `Sync Now` button
- Current sync status label
- Search field for member name filtering
- Scrollable member list
- `GUI` button that opens the expanded window

If no group ID is configured, the panel stays in a placeholder state until one is provided. If a sync fails, the panel surfaces the API error and prompts the user to verify the group ID and connection.

### Expanded window

The standalone window shows the full dataset in a table with these columns:

- `#`
- `Name`
- `Role`
- `Total XP`
- `EHP`
- `EHB`

The table supports:

- Column sorting
- Name filtering through a separate search field
- Resizable desktop window layout

## Configuration

The plugin exposes two RuneLite config entries:

| Setting | Description |
|---|---|
| `WOM Group ID` | Wise Old Man group ID to sync |
| `Auto-refresh (every 30 min)` | Enables background refresh while RuneLite is running |

To find the group ID, open your group on [wiseoldman.net](https://wiseoldman.net) and copy the numeric ID from the URL.

## Project Layout

- `src/main/java/com/womclan/WomClanPlugin.java`: plugin lifecycle, navigation button, refresh scheduling
- `src/main/java/com/womclan/WomClanPanel.java`: sidebar UI, search, cooldown, placeholder/error states
- `src/main/java/com/womclan/WomExpandedWindow.java`: standalone sortable table window
- `src/main/java/com/womclan/WomApiClient.java`: WOM API fetch and JSON parsing
- `src/main/java/com/womclan/WomMember.java`: immutable member model
- `src/main/java/com/womclan/WomMemberPanel.java`: sidebar row rendering
- `src/main/java/com/womclan/WomClanConfig.java`: RuneLite config items
- `src/test/java/com/womclan/WomApiClientTest.java`: parser coverage for WOM group response shape
- `src/test/java/com/womclan/WomClanPluginTest.java`: local RuneLite launcher entrypoint
- `runelite-plugin.properties`: plugin metadata

## Local Development

### Requirements

- Java 11
- Gradle wrapper included in the repository

### Run in RuneLite developer mode

```bash
./gradlew run
```

This launches RuneLite with:

- developer mode enabled
- debug logging enabled
- RuneLite auto-update disabled for local iteration

### Run tests

```bash
./gradlew test
```

### Build the project

```bash
./gradlew build
```

### Build a fat jar

```bash
./gradlew shadowJar
```

The generated artifact uses the Gradle root project name from `settings.gradle`, currently `wom-clan-stats`.

## Notes

- Automated coverage is currently limited to WOM response parsing
- The plugin depends on RuneLite client APIs and Swing UI components provided by RuneLite
