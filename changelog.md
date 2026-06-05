Upcoming features:

**FEATURE:** Added height support to kits
**FEATURE:** Added attribute support to kits
**FEATURE:** /kit save - Saves a kit from your active inventory state
**FEATURE:** Kingdom roles, multiple leaders
**FEATURE:** Player abilities
**FEATURE:** Capped xp cost at 1 level when renaming an item

**CobaltKingdoms Version 1.3.1**

- **BUGFIX:** Fixed missing locale messages

**CobaltKingdoms Version 1.3.0**

- **FEATURE:** Added towns. Create a new town using the '/town create' command. In order to create a town, you must
  be a member of a kingdom.
- **FEATURE:** Added town levels. Complete quests to level up your town and increase rewards from quests, and unlock
  more quest types.
- **FEATURE:** Added jails. Create a new jail using the '/town jail create' command. In order to create a jail, you have
  to be an elevated member of a town.
- **FEATURE:** Added quests. There are currently three different types of quests. Each quest type has a different loot
  pool.
- **FEATURE:** Added Bounty Quests
- **FEATURE:** Added Delivery Quests
- **FEATURE:** Added Artifact Hunt Quests
- **FEATURE:** Right-clicking an armor stand while sneaking will switch all the slots with yours
- **UPDATE:** The Wandering Trader now sells a Bounty Letter
- **COMMAND:** New command: '/town create'
- **COMMAND:** New command: '/town delete'
- **COMMAND:** New command: '/town info'
- **COMMAND:** New command: '/town invite'
- **COMMAND:** New command: '/town kick'
- **COMMAND:** New command: '/town leave'
- **COMMAND:** New command: '/town list'
- **COMMAND:** New command: '/town modify'
- **COMMAND:** New command: '/town move'
- **COMMAND:** New command: '/town jail create'
- **COMMAND:** New command: '/town jail delete'
- **COMMAND:** New command: '/town jail info'
- **COMMAND:** New command: '/town jail list'
- **COMMAND:** New command: '/bounty enable'
- **COMMAND:** New command: '/bounty disable'

**CobaltKingdoms Version 1.2.3**

- **FEATURE:** The discord bot now displays the status for vanished players
- **UPDATE:** Player leave and join messages now appear for vanished players

**CobaltKingdoms Version 1.2.2**

- **FEATURE:** Added discord integration
- **COMMAND:** New discord command: '/info'
- **BUGFIX:** Fixed incorrect player chat messages

**CobaltKingdoms Version 1.2.1**

- **FEATURE:** Updated chat messages to the new Cobalt Color Profile (tm)
- **FEATURE:** Nametags are now invisible for players in character
- **BUGFIX:** Fixed missing locale messages
- **BUGFIX:** Fixed join/quit messages displaying for players with vanish permissions
- **BUGFIX:** Fixed incorrect status prefix
- **BUGFIX:** Fixed letters not getting sent if character profile name is empty

**CobaltKingdoms Version 1.2.0**

- **FEATURE:** Added Player Character Profiles. Create profiles with information about your character for other players
- **FEATURE:** Sneak right-clicking a player with an empty hand displays their Character Profile
- **FEATURE:** Omitting information from the character profile will make it show up as '???'
- **FEATURE:** Support for multiple Character Profiles for players with the correct permission node
- **FEATURE:** Added Kits. Use the /kit command to set your inventory to that specified in the kit
- **FEATURE:** Added item support to Kits
- **FEATURE:** Added Character support to Kits
- **FEATURE:** Added potion effect support to Kits
- **FEATURE:** Added teleport support to Kits
- **FEATURE:** Bone meal any flower to get more of them
- **FEATURE:** Letters can now be sent to offline players
- **FEATURE:** If using a Character Profile, letters will get sent in that name instead of your username
- **FEATURE:** Players with the Admin role displays as [A] next to their name in tab
- **FEATURE:** Players with the Leader role displays as [L] next to their name in tab
- **FEATURE:** Players with the Villager role displays as [V] next to their name in tab
- **FEATURE:** New player status: Open for In Character
- **COMMAND:** New command: '/character create [id]'
- **COMMAND:** New command: '/character info <player>'
- **COMMAND:** New command: '/character set name [name]'
- **COMMAND:** New command: '/character set pronouns [pronouns]'
- **COMMAND:** New command: '/character set age [age]'
- **COMMAND:** New command: '/character set height [height]'
- **COMMAND:** New command: '/character set description [description]'
- **COMMAND:** New command: '/character active <id>'
- **COMMAND:** New command: '/height set [height]'. Can be used to set player height
- **COMMAND:** New command: '/kit get [id]'
- **COMMAND:** New command: '/kit pastebin test [id]'
- **COMMAND:** New command: '/kit pastebin save [id]'
- **COMMAND:** New command: '/colors'
- **COMMAND:** New command: '/letter send [player]'
- **COMMAND:** New command: '/letter read'
- **COMMAND:** New command: '/oic'
- **UPDATE:** If a player could not be found when using /kingdom info, display UUID instead
- **UPDATE:** Hats can no longer be placed on the ground
- **UPDATE:** Updated to the new Cobalt Color Profile (tm)
- **REMOVED:** Letters no longer get sent when the title is set to a player username

**CobaltKingdoms Version 1.1.0**

- **FEATURE:** Colored item names when renaming using an anvil
- **FEATURE:** Right-clicking on a stair or slab with red wool placed under it will cause the player to sit
- **FEATURE:** Player status is now displayed next to the player's name tag
- **FEATURE:** Sneak-right clicking with a lodestone compass displays the distance in chat
- **FEATURE:** Added armor stand poses accessible through the '/armorstand' command
- **FEATURE:** Added armor stand modification accessible through the '/armorstand' command
- **COMMAND:** New command: '/armorstand set arms [visible]'
- **COMMAND:** New command: '/armorstand set base [visible]'
- **COMMAND:** New command: '/armorstand set invulnerable [invulnerable]'
- **COMMAND:** New command: '/armorstand set small [small]'
- **COMMAND:** New command: '/armorstand set lock [slot] [lock_type] <lock>'
- **COMMAND:** New command: '/armorstand pose preset [pose]'
- **UPDATE:** Changed the color of player statuses in the tab menu
- **BUGFIX:** Changing status displays twice
- **BUGFIX:** Added additional null-checks to villager trade generation

**CobaltKingdoms Version 1.0.1**

- FEATURE: Displays your active status in chat when you join
- BUGFIX: Fix villager trades (again)

**CobaltKingdoms Version 1.0.0**