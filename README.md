# 🔊 voicechat-interaction-paper

A third-party Paper port of henkelmax's [voicechat-interaction](https://github.com/henkelmax/voicechat-interaction), allowing for Sculk detection of Simple Voice Chat audio on Paper-based servers

Currently, it is using the `PRIME_FUSE` GameEvent (Sculk Sensor frequency `7`). This is for parity with the default frequency of the official mod, as it is not possible (to my knowledge) to inject custom GameEvents into the Sculk Sensor's frequency map. All other config options are implemented, see [here](https://github.com/henkelmax/voicechat-interaction/#config-values) for details.

In versions 1.2.0 of this port and above, an additional config option is present: `default_interaction_toggle` (`true` by default).
This controls the default state of the mod for individual players. \
You can toggle it with `/voicechat_interaction toggle [<player>]` (permission `voicechat_interaction.toggle[.others]`) \
This is not in the original mod and was added as a feature request.

### Dependencies
- Simple Voice Chat