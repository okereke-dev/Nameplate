# Nameplate

> **Unofficial port to Minecraft 26.2.** This fork ports Nameplate to the
> unobfuscated Minecraft 26.2 / Fabric Loader 0.19.3, maintained by
> [okereke-dev](https://github.com/okereke-dev) — **not affiliated with or
> endorsed by the original author.** All credit for the original mod goes to
> Globox1997/Globox_Z. Original mod page:
> [Modrinth](https://modrinth.com/mod/nameplate).
>
> Get the built jar from [Releases](../../releases) rather than building from
> source, unless you want to modify it further.
>
> Dropped in this port (all required adopting 26.2's new rendering-state
> API for a purely cosmetic payoff, not worth the risk): Jade/WAILA
> integration, GeckoLib-animated entity support, compatibility with the
> original "Traveler's Titles" mod, and the custom floating nameplate/health
> bar renderer (replaced by a simpler mixin that prefixes the vanilla name
> tag with the level, e.g. "[Lvl 5] Zombie"). Core leveling logic is
> unchanged from upstream.
>
> ---

Nameplate adds level plates above mobs to indicate their level.

### Installation
Nameplate is an addon mod for the [RpgDifficulty](https://www.curseforge.com/minecraft/mc-mods/rpgdifficulty) mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately.

### License
Nameplate is licensed under MIT.
