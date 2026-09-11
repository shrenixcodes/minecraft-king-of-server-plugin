# King of the Server 👑

King of the Server is a [Paper](https://papermc.io/) plugin with one simple rule: every
seven real-world minutes, the server crowns a new King. The King gets a handful of fun,
balanced perks and a visible crown until the next rotation — no downloads for players,
no complicated setup for admins.

## Features

- **Real-time rotation.** A new King is chosen every `king.duration` of actual elapsed
  time (7 minutes by default) — never tied to the Minecraft day/night cycle.
- **Fair, configurable selection.** Weighted or uniform-random selection, with the
  current and previous King skipped automatically whenever another eligible player is
  available.
- **Five King abilities.** Royal Speed, Royal Strength, Royal Jump, Royal Vision, and a
  sneak-triggered Royal Shield, each individually toggleable.
- **Rich, non-spammy feedback.** Boss bar, action bar, sidebar scoreboard, subtle
  particles, sounds, a one-minute warning, and a short countdown before each rotation.
- **A crown you can't lose.** The King is marked with a glowing outline and a nametag
  crown instead of a physical item, so death, disconnects, and world changes can never
  duplicate or destroy it.
- **Persistent statistics.** Total rotations, times crowned, total reign time, and
  longest reign are saved to disk and survive a restart.
- **Safe restarts.** An in-progress reign resumes exactly where it left off after a
  server restart.

## How It Works

A timer runs continuously in the background, measured in real seconds — not Minecraft
ticks, days, or weather cycles. When it reaches zero, the current King is deposed (if
online) and a new eligible player is crowned.

```text
00:00  Alex becomes King
07:00  Steve becomes King
14:00  Jamie becomes King
21:00  Alex becomes King
```

Dying does **not** end a reign — the King keeps their crown and the timer keeps running
even if they die and respawn. Only the rotation timer, `/king next`, or `/king set` can
change who holds the crown.

## Installation

1. Download the plugin `.jar` (or build it yourself — see [Contributing](#contributing)).
2. Place it in your server's `plugins` folder.
3. Start or restart the server. A default `config.yml` is created automatically.
4. Adjust `plugins/KingOfTheServer/config.yml` to taste.
5. Run `/king start` if you set `auto-start: false`; otherwise rotation begins as soon
   as an eligible player is online.

## Commands

| Command               | Description                             | Permission           |
|------------------------|------------------------------------------|-----------------------|
| `/king`                | Show the current King and time remaining | `king.use`            |
| `/king info`           | Show detailed King and rotation info     | `king.use`            |
| `/king status`         | Show a concise rotation status           | `king.use`            |
| `/king abilities`      | List the King's abilities                | `king.use`            |
| `/king stats`          | Show King of the Server statistics       | `king.use`            |
| `/king start`          | Start the King rotation                  | `king.admin.start`    |
| `/king stop`           | Stop the King rotation                   | `king.admin.stop`     |
| `/king next`           | Immediately choose the next King         | `king.admin.next`     |
| `/king set <player>`   | Manually assign a player as King         | `king.admin.set`      |
| `/king reload`         | Reload the configuration                 | `king.admin.reload`   |

## Permissions

| Permission             | Description                                             | Default |
|--------------------------|----------------------------------------------------------|---------|
| `king.use`              | Use the basic `/king` commands and be eligible as King    | `true`  |
| `king.admin`            | Grants every `king.admin.*` permission below              | `op`    |
| `king.admin.start`      | Start the rotation                                         | `op`    |
| `king.admin.stop`       | Stop the rotation                                           | `op`    |
| `king.admin.next`       | Force the next rotation                                     | `op`    |
| `king.admin.set`        | Manually assign the King                                    | `op`    |
| `king.admin.reload`     | Reload the configuration                                     | `op`    |
| `king.bypass`           | Excludes the player from King selection entirely             | `false` |

## Configuration

```yaml
king:
  duration: 7m
  selection-mode: weighted
  prevent-consecutive-king: true
  minimum-players: 1
  auto-start: true

abilities:
  royal-speed:
    enabled: true
    amplifier: 1
  royal-shield:
    enabled: true
    duration-seconds: 5
    cooldown-seconds: 45

ui:
  boss-bar:
    enabled: true
  scoreboard:
    enabled: true

selection:
  exclude-spectators: true
  exclude-vanished: true
  offline-king-policy: KEEP
```

See the comments in the shipped `config.yml` for every option. A few worth calling out:

- **`king.duration`** accepts combinations of `d`/`h`/`m`/`s`, e.g. `7m`, `90s`, `1h30m`.
- **`king.selection-mode`** is `weighted` (players who have been King less often are more
  likely to be picked, but nobody is ever fully excluded) or `random` (uniform chance).
- **`king.minimum-players`** pauses rotation until enough eligible players are online,
  useful if you don't want a lone player crowned by default.
- **`selection.offline-king-policy`** controls what happens when the King disconnects:
  `KEEP` (default) leaves them crowned until the next scheduled rotation, `REPLACE`
  crowns a new King immediately.

## King Abilities

| Ability        | Effect                                                                 |
|----------------|-------------------------------------------------------------------------|
| Royal Speed    | Passive Speed I (configurable amplifier) for the whole reign.           |
| Royal Strength | Passive Strength I (configurable amplifier) for the whole reign.        |
| Royal Jump     | Passive Jump Boost I (configurable amplifier) for the whole reign.      |
| Royal Vision   | Passive Night Vision for the whole reign.                               |
| Royal Shield   | Sneak to trigger a few seconds of Resistance + Absorption, on a cooldown.|

Every ability can be disabled independently in `config.yml`. None of them modify a
player's inventory or leave permanent effects behind after the reign ends.

## Statistics

`/king stats` and the on-disk `stats.yml` track:

- Total rotations that have happened on the server.
- How many times each player has been crowned.
- Each player's total accumulated time as King.
- Each player's single longest reign.

## Compatibility

Built and tested against **Paper 26.2** (`paper-api 26.2.build.123-stable`), which
requires **Java 25** or newer. It depends on Paper-specific APIs (Adventure boss bars,
sidebar scoreboard number formats) and will not run on plain Bukkit/Spigot or on older
Paper builds without those APIs.

## Troubleshooting

**The plugin doesn't start / server refuses to load it.**
Check `logs/latest.log` for a startup error. The most common cause is running a server
JVM older than Java 25, or a Paper build older than the one this plugin was built
against.

**No King is ever selected.**
Rotation may be stopped (`/king status` shows this) or `king.minimum-players` may be
higher than the number of currently eligible players. Also check that players have the
`king.use` permission and aren't excluded by `selection.exclude-spectators` /
`selection.exclude-vanished`.

**Commands don't work.**
Confirm the sender has `king.use` for basic commands, or the matching `king.admin.*`
permission for admin commands. `/king` with no permission plugin installed defaults
`king.use` to `true` for all players.

**Configuration changes aren't applying.**
Run `/king reload` after editing `config.yml`. If a value is invalid (e.g. an
unparsable duration), the plugin logs nothing destructive and falls back to its default
for that value only — double-check spelling and indentation.

**Permission problems.**
Permissions are only enforced by whatever permissions plugin (or the operator system)
you use; verify the exact node names in the [Permissions](#permissions) table.

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) for build
instructions and guidelines before opening a pull request.

## License

Released under the [MIT License](LICENSE).
