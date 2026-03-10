# WOM Clan Stats

A RuneLite sidebar plugin that pulls member data from the [Wise Old Man](https://wiseoldman.net) API and displays it in a searchable clan panel.

## Features

- Displays each clan member's **Total XP**, **EHP**, and **EHB**
- Members sorted by Total XP descending
- Search/filter members by name
- Manual **Sync Now** button with a 5-minute cooldown
- Optional **auto-refresh** every 30 minutes

## Configuration

| Setting | Description |
|---|---|
| WOM Group ID | The Wise Old Man group ID for your clan |
| Auto-refresh (every 30 min) | Enables background syncing while RuneLite is open |

To find your group ID, go to [wiseoldman.net](https://wiseoldman.net), open your group, and copy the number from the URL.

## Local Development

**Requirements:** Java 11, Gradle wrapper included.

```bash
# Run the plugin in RuneLite developer mode
./gradlew run

# Build the project
./gradlew build
```
