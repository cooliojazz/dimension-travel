Dimension Travel Mod Thing
===
- Allows opening doors to alternate realities
- Realties are described via a vector of their properties
- Some writing system to describe dimensional coordinates
  - Could use a nerual net to output a recognition vector describing some set of coordinate symbols
- Ender pearls for dimensional energy?


TODO
===
- Add survival interactions
  - Player control
    - Smaller portals that can be oriented in any direction
    - Needs player energy?
  - Passive control
    - Block aligned portals
    - Spawned by some block structure
    - Needs some sort of mechanical/warp energy?
  - Writing/Speicifcation/Array/Whatever stuff
- Add many more reality coordinate dimensions
 - Generator noise settings
- Portal rendering
  - Add backing entity to cover rear portal and add a small outlline to the front
    - Possibly make this somehow optional for aethetics of seamless portals?
  - Cool opening animations?
  - Portal shapes
    - Oval for personal portals? Maybe they should change shape depending on angle?
    - Simple block-aligned rectangle for passive portals
- Coordinates don't reflect attractor model (components are reduced in spatial size on usage, while worlds are indexed by their raw values, leading to mutiple identical worlds with differeing coordinates)
- maybe change realitycoorinate external api to use ints and only use shorts for internal array storage?
- Or should reality coordinates be dynamically sized per-parameter to more easily allow coordinate copmarison in the case two points above
  - To note though, this would remove the "smoothness" that the current system gives to subdivided components when transition between very few possible states
- Issue loading game from alternate dimension
- Add the travel dimension
  - Create barrier blocked hallways between the portals
  - The length should be proportional to the magnitude of the difference of the reality vectors
  - Maybe filled with some (breathable) liquid?
  - Some sort of bad thing that could happen there? like an entity, or a badd effect?
  - Or should it just be a super liminal space?
  - Should probaby just be all black, unsure if too boring though even if it ends up being liminal


Name Ideas
===
Pathfinding Through Reality
Reality Pathfinders
Exploring Reality
Reality Perusers
