# GeyserReversion

Backward Bedrock protocol translation for Geyser, maintained as a public fork
by **siberanka**. October 2026 compatibility update: **1.0.6 (experimental)**.

[GitLab](https://gitlab.com/siberanka/GeyserReversion-AIRemake) ·
[GitHub backup](https://github.com/siberanka/GeyserReversion-AIRemake) ·
[Compatibility and limitations](docs/COMPATIBILITY.md) ·
[Local validation report](docs/VALIDATION-2026-10.md)

Geyser natively handles Bedrock **26.30–26.52**. GeyserReversion extends the
experimental legacy route down to **1.12**, including Bedrock **26.0–26.23**,
through a shared **1001 / 26.30** bridge. A registered codec does not guarantee
perfect gameplay; read the support matrix before production deployment.

## Installation

Use Java 21+, Geyser **2.11.3 build 1249**, and, for Floodgate authentication,
Floodgate **2.2.5 build 141**. Place only the `-all.jar` in Geyser's
`extensions/` directory and restart. Configure authentication in Geyser and
Floodgate; this extension preserves the existing configuration schema and does
not add a separate login setting.

The same extension artifact is platform-neutral and has no direct Spigot,
BungeeCord or Velocity dependency. Geyser's normal proxy/backend deployment
rules still apply. Keep Geyser and Floodgate on matching current builds.

Back up inventories and worlds, then certify enabled legacy versions against a
staging copy of the real server. Custom content, anti-cheat, proxy switching and
old-client UI behavior require server-specific tests. Critical translation
failures are logged and disconnect explicitly.

## Local build and verification

```powershell
$env:JAVA_HOME = 'F:\vds\Java\jdk-21.0.9+10'
$env:JAVA_TOOL_OPTIONS = '-Djavax.net.ssl.trustStoreType=Windows-ROOT'
git submodule update --init --recursive
.\gradlew.bat clean test shadowJar sourceRelease --no-daemon
```

The build applies reviewed patches to an isolated `build/ouranos-src/` copy;
it never edits the Ouranos submodule or an existing JAR. The exhaustive palette
matrix may use up to 3 GB of heap. No CI, GitHub Actions or GitLab runners are
required. For live loopback negotiation, start a local Geyser instance and run
tests with `-PintegrationPort=<local-port>`.

Download both the plugin and matching `corresponding-source` release asset.
The source archive expands nested submodules and includes exact dependency
source JARs, so it builds without a `.git` directory.

## License and authors

GeyserReversion retains GPL-3.0; bundled Ouranos retains AGPL-3.0 and its network
source-offer requirements. Server operators must prominently offer users the
matching free source link, including their own modifications.

Original work: **oxy / oryxel1**, **AnarchadiaMC**, **Blackjack200** and their
contributors. Copied GeyserMC and ViaProxy notices remain intact. Inspired by
bundabrg/GeyserReversion; this is a different implementation.

See [upstream attribution](UPSTREAM_ATTRIBUTION.md),
[third-party notices](THIRD_PARTY_NOTICES.md), [GPL license](LICENSE) and
[Ouranos license](Ouranos/LICENSE). Redistribution is allowed subject to these
licenses; no warranty or flawless-compatibility claim is made.
