# Contributing to King of the Server

Thanks for your interest in improving the plugin. This document covers everything
needed to build the project, run its tests, and get a change merged.

## Requirements

- Java Development Kit **25** or newer.
- No local Gradle install is required; use the included wrapper (`./gradlew` or
  `gradlew.bat`), which will download the correct Gradle version automatically.

## Building

```bash
./gradlew build
```

This compiles the plugin, runs the test suite, and produces
`build/libs/KingOfTheServer-<version>.jar`.

## Running the tests

```bash
./gradlew test
```

Tests cover King selection (including consecutive-King prevention and fairness),
round/timer transitions, player eligibility, manual King assignment, statistics
bookkeeping, and configuration parsing. Please add tests for any new logic that doesn't
require a live server to exercise.

## Project structure

```text
src/main/java/com/kingoftheserver/plugin/
├── KingOfTheServerPlugin.java   Plugin entry point / wiring
├── ability/                     KingAbility interface and implementations
├── command/                     /king subcommands
├── config/                      config.yml parsing
├── king/                        Selection, round timing, and statistics
├── listener/                    Bukkit event listeners
├── persistence/                 YAML-backed stats and round-state storage
├── ui/                          Boss bar, scoreboard, particles, broadcasts
└── util/                        Small, dependency-free helpers
```

If you're adding a new ability, implement `KingAbility` and register it in
`AbilityManager` — no other class needs to change. The same pattern applies to King
selection strategies (`KingSelector`) and command subcommands (`KingSubcommand`).

## Code style

- Favor clear names and small methods over clever abstractions.
- Keep core game logic (selection, timing, statistics) free of direct Bukkit API calls
  where practical, so it stays unit-testable without a running server.
- Don't add configuration options, abilities, or commands beyond what's asked for in an
  issue or discussion — open one first if you want to propose something larger.

## Submitting changes

1. Fork the repository and create a feature branch.
2. Make your change, including tests where practical.
3. Run `./gradlew build` and confirm it passes.
4. Open a pull request describing what changed and why.

## Reporting bugs

Please include your server software and version (e.g. Paper 26.2), the plugin version,
your `config.yml` (with anything sensitive removed), and the exact steps to reproduce.
