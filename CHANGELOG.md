# Changelog

All notable changes to King of the Server are documented in this file.

## [1.0.0]

Initial release.

### Added

- Real-time seven-minute (configurable) King rotation.
- Weighted and random King selection strategies with consecutive-King prevention.
- Five King abilities: Royal Speed, Royal Strength, Royal Jump, Royal Vision, and Royal
  Shield.
- Boss bar, action bar, sidebar scoreboard, particle, and sound feedback for the current
  reign.
- Glowing crown indicator with a nametag prefix, avoiding physical-item edge cases.
- `/king` command family covering info, status, abilities, stats, start, stop, next,
  set, and reload.
- Persistent statistics (total rotations, times crowned, total and longest reign time).
- Restart-safe round state so an in-progress reign resumes correctly.
