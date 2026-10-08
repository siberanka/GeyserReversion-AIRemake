# Local validation report — 8 October 2026

Release candidate: GeyserReversion 1.0.6, built with Temurin Java 21.0.9.
All checks ran locally on Windows; no GitHub Actions, GitLab CI/CD or hosted
runner was used.

Reference components:

- Geyser 2.11.3 build 1249, commit `f66329d9`, exact Maven core snapshot
  `2.11.3-20261006.093555-15`.
- Official Geyser Standalone SHA-256:
  `1c57b3d1bbe9eca3817e76bb68de5fed518b9295dcbae549ebac322e4d7fbd9a`.
- Floodgate 2.2.5 build 141 metadata and BungeeCord, Spigot and Velocity
  artifact hashes checked through the official download API.
- Ouranos upstream commit `e927ea497cae5a739acbf5d91f81024bf79738f0`.

## Automated results

The complete JUnit run with the local integration target enabled finished with
485 tests, 0 failures, 0 errors and 0 skips:

| Suite | Reported tests | Purpose |
| --- | ---: | --- |
| `BridgeMappingAuditTest` | 3 | Fail-closed item/block bridge invariants |
| `BridgePipelineTest` | 54 | Legacy and native movement packet codec traversal |
| `CodecRegressionTest` | 114 | Helper isolation, catalog parity, bridge selection and chest wire round-trips |
| `GameplayTranslationTest` | 269 | Chest/inventory, recipes, held items, movement and item-use translation |
| `GeyserInternalCompatibilityTest` | 4 | Lifecycle annotations, private listener surface, moved classes and config defaults |
| `LocalNetworkNegotiationTest` | 38 | Test factory plus 37 real UDP/RakNet network-settings negotiations |
| `RuntimeDictionaryTest` | 3 | Per-session item dictionaries and block runtime ordering |

The live negotiations cover every distinct registered protocol at or above
554, where `RequestNetworkSettings` is available, plus current native Geyser
protocols. Older codecs are exercised by the exhaustive wire and translation
suites but cannot use this newer handshake packet.

Tests ran with Netty paranoid leak detection. No leak report, test failure,
mapping-audit failure or translation-failure diagnostic was emitted.

## Runtime smoke test

The built extension JAR was placed in a clean official Geyser Standalone
2.11.3-b1249 `extensions/` directory; neither JAR was modified. Startup reached
`GeyserPostInitializeEvent` without `NoClassDefFoundError`, enabled the
extension, rebound UDP 19132 and selected the genuine shared bridge
`1001 / 1.26.30`.

Before listener replacement, the fail-closed runtime audit verified 1,941
vanilla item runtime IDs and 17,112 block runtime states against Geyser's active
1001 mappings. The 37 loopback clients then received ZLIB network settings
from the running instance. Shutdown completed cleanly.

## Internal API and platform audit

The source compiles against the exact build-1249 core. Reflection tests pin the
private fields deliberately used for listener replacement (`group`,
`childGroup`, `playerGroup`, `listenCount`, `bootstrapFutures`) and verify that
they retain the expected mutable types. The tests also pin current locations
and signatures for `GameProtocol`, `RaknetServer`, `GeyserServerInitializer`,
the RakNet bootstrap/connection/ping handlers, `InvalidPacketHandler` and
`raknetPort`.

JDK dependency analysis of the shaded extension against the official
Standalone artifact found no direct BungeeCord, Velocity, Spigot, Paper,
Floodgate or Geyser platform-implementation reference. All functional direct
Geyser references resolved from build 1249. This supports using one extension
artifact with Geyser's supported standalone, proxy and backend layouts; the
extension does not replace the platform-specific Geyser/Floodgate plugins.

No authenticated Floodgate login or proxy backend switch was possible in this
credential-free local harness. Those remain real-client acceptance tests.

## Packaging and source checks

The shaded plugin and corresponding-source ZIP use reproducible ordering and
timestamps. The source archive is allowlisted, excludes repository metadata,
private working state, IDE settings, credentials and generated server data,
expands the nested Ouranos source, and includes required licenses and exact
dependency source JARs. It is rebuilt independently without `.git` metadata
before publication. That independent build completed 448 tests with 0 failures,
0 errors and only the deliberately opt-in live network factory skipped; its
shaded JAR was byte-for-byte identical to the primary build.

The bundled Geyser-derived handlers retain the MIT notice; GeyserReversion
remains GPL-3.0 and bundled Ouranos remains AGPL-3.0. The source-offer warning
and upstream authorship are preserved in public documentation and artifacts.

## Scope of the evidence

These tests directly exercise the code paths behind basic legacy failures, but
they do not render an old Bedrock UI or authenticate a player against a real
Java backend. Therefore this release does not claim that every legacy client
can flawlessly walk, mutate every chest, craft, reconnect or switch backends on
every plugin combination.

Complete the mandatory staging-client procedure in
[COMPATIBILITY.md](COMPATIBILITY.md#real-client-acceptance-checklist), enable
only certified protocol families, and retain inventory/world backups.
