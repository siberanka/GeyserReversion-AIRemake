# Bedrock compatibility — 8 October 2026

This is an experimental backward-compatibility extension, not a replacement
for Geyser's supported-version policy. A registered codec proves that a packet
format is available; it does **not** certify flawless client gameplay.

| Client versions | Route | Verification / support status |
| --- | --- | --- |
| Bedrock 26.30–26.52 | Native Geyser 2.11.3 build 1249 | Official current range; protocols 1001, 2168, 2169 and 2193 |
| Bedrock 26.0–26.23 | Ouranos, protocols 924 / 944 / 975 → 1001 | Legacy translation added because these codecs left current Geyser; experimental gameplay |
| Bedrock 1.21.110–1.21.132 | Ouranos, protocols 859 / 860 / 898 → 1001 | Codec, mapping, packet and local negotiation coverage; experimental gameplay |
| Bedrock 1.16.100–1.21.100 | Ouranos, protocols 419–844 → 1001 | Local wire/semantic regression coverage; experimental gameplay |
| Bedrock 1.12–1.16.40 | Ouranos, protocols 361 / 388 / 389 / 390 / 407 / 408 → 1001 | Upstream partially playable; not production-certified |
| Unregistered protocols, previews, beta clients and future releases | Rejected unless installed Geyser supports them natively | No guessed protocol or schema aliases |

The reference Geyser build's native protocols are 1001 (26.30–26.34), 2168
(26.40–26.44), 2169 (26.45) and 2193 (26.50–26.52). Protocol **1001 / 26.30**
is the newest genuine codec shared by current Geyser and the patched Ouranos
catalog, so it is selected as the translation bridge.

Legacy registered protocols (56 including the shared native bridge codec):
361, 388, 389, 390, 407, 408, 419, 422, 428, 431, 440, 448, 465, 471, 475,
486, 503, 527, 534, 544, 545, 554, 557, 560, 567, 568, 575, 582, 589, 594,
618, 622, 630, 649, 662, 671, 685, 686, 712, 729, 748, 766, 776, 786, 800,
818, 819, 827, 844, 859, 860, 898, 924, 944, 975, 1001.

## What 1.0.6 updates

- Moves `GameProtocol` to Geyser 2.11.3's
  `org.geysermc.geyser.network.bedrock` package and checks every other direct
  Geyser internal reference against exact build 1249.
- Adapts listener replacement to `RaknetServer`, `GeyserServerInitializer`,
  the current RakNet bootstrap handlers and the current `raknetPort` setting.
- Adds real Ouranos codecs 975 and 1001 and raises the shared translation
  bridge from 944 to 1001. No unrelated modern mapping is aliased to it.
- Preserves per-session mutable helpers and initializes the bridge's item
  dictionary and full block palette from actual Geyser mappings.
- Keeps concrete recipe ingredients/results, furnace/brewing identifiers,
  creative contents, equipment and embedded item-use transactions translated.
- Preserves Geyser online, offline and Floodgate authentication selection.
- Preserves the existing extension configuration keys and defaults.

## Local evidence

The [October validation report](VALIDATION-2026-10.md) records the exact build,
hashes and results. In summary:

- 485 local tests passed with no failures or skips.
- Every one of the 56 legacy codecs completed helper-isolation and chest
  open/close wire round-trips; movement crossed both shaded codec boundaries.
- Crafting, inventories, held items, embedded chest interactions, runtime item
  dictionaries and full block palettes were checked semantically.
- Geyser 2.11.3-b1249 loaded the extension through
  `GeyserPostInitializeEvent`, selected bridge 1001, and verified 1,941 item
  runtime IDs plus 17,112 block states.
- 37 distinct supported protocol families (legacy and native, protocol 554+)
  completed real local RakNet/network-settings negotiation on UDP 19132.
- The built extension contains no direct BungeeCord, Velocity, Spigot, Paper,
  Floodgate or Geyser platform implementation dependency.

## Remaining limitations

Synthetic packets and local unauthenticated negotiation cannot prove what only
a real Bedrock client and authenticated Java backend can prove: walking and
rubber-banding behavior, chest UI state reconciliation, crafting output
consumption, inventory loss/duplication, reconnects, anti-cheat interaction and
backend switches under real latency. No such end-to-end certification is
claimed.

Old clients cannot represent every modern block, item, mob, UI container,
smithing template, trim or protocol feature. Ouranos may polyfill absent items.
Education-only material reducers are not advertised. Pre-1.19.50 recipe
tag/Molang descriptors cannot be represented faithfully, so only affected
recipes are omitted rather than replacing them with arbitrary ingredients or
clearing the recipe book. Pre-1.20 smithing recipes are omitted.

The bridge is 26.30, not 26.52: translated clients do not gain a full
implementation of every 26.52 feature. Native 26.30–26.52 clients continue to
use Geyser's codecs and mappings. Updating Geyser beyond the reference build
requires repeating validation; initialization fails safely if a genuine shared
bridge no longer exists.

Recommended production policy: keep native current clients, or enable only the
legacy protocols certified on a staging copy of the actual server. Use
`min-protocol-id` and `blocked-protocols`, and retain inventory/world backups.

## Real-client acceptance checklist

1. Test Floodgate and online authentication separately; reconnect and confirm
   that Floodgate/offline users never receive a Microsoft device-code prompt.
2. Walk, sprint, jump, swim, sneak, teleport, change dimensions and mount;
   check for rubber-banding and rejected movement.
3. Open chest variants, move/split/shift-transfer stacks both ways, reconnect,
   and check server-side inventory for loss, duplication or ghost items.
4. Craft shaped and shapeless recipes manually and through the recipe book;
   consume outputs and verify ingredient counts on the Java backend.
5. Test furnaces, brewing, creative inventory, hotbar swaps, placement,
   breaking, custom items and custom containers.
6. Repeat with latency/loss, mixed client versions, resource packs, anti-cheat
   and proxy backend switches while monitoring translation diagnostics.

Sources checked on 8 October 2026:
[official Geyser supported versions](https://geysermc.org/wiki/geyser/supported-versions/),
[Geyser build 1249 metadata](https://download.geysermc.org/v2/projects/geyser/versions/2.11.3/builds/1249),
[Floodgate build 141 metadata](https://download.geysermc.org/v2/projects/floodgate/versions/2.2.5/builds/141).
