# FL4 (Working title - Red)

This project is a 2-dimensional auto-runner / platformer that builds levels based on real-time weather information.
It uses a single JPanel window to display a pixel-art scene consisting of a player avatar, terrain and various hazards.
The game "engine" is an endless loop that updates and displays the game state on a fixed schedule of 60 frames per
second, meaning that a single frame lasts ~17 milliseconds. Interaction is managed through a keyboard input vector using
Java event listeners. Events and state changes are handled through a singleton class; the GameStateManger. Several
relevant factors, some of which have not been implemented at time of writing, are listed below:

 - **Thread structure**: The game consists of two threads, one of which processes inputs and updates the game state
    while the other fetches API data and uses it to construct the levels. Since the graphical interface is comparatively
    simple, I do not know if it would be particularly efficient to split the game GUI and logic backend into separate
    threads.
 - **API/Networking**: No concrete choice. [Open weather map](https://openweathermap.org/api) could be a good choice, if
    the API calls are sufficiently thinned out as to not use up the 1000 free API calls per day.
 - **Sound**: Real-time sound filtering capabilities would be ideal, however I am not sure whether they are available 
    on vanilla Java. I am in fact very unfamiliar with Java sound libraries.
 
### Notes on ODE_FASTLANE.pdf:

 - Inheritance, overriding and overloading are already ubiquitous.
 - Field and method access modifiers are purposefully chosen as to reduce access rights to a minimum.
 - Exception handling is present for regular use-cases, as well as in the form of custom exceptions that handle special
    game events such as player death.
 - File IO is used to load (and, in the future, save) game metadata and graphics.

###  Notes on the game mechanics

Actions available to the player: 

 - Movement will be automatic, right to left.
 - Colliding with terrain will push the player avatar to the right. Being pushed behind the game screen edge results in
    a loss. Avoiding collision for a certain time will cause the player avatar to slowly recover its neutral position.
 - Falling off the level results in a loss.
 - Colliding with hazards damages the player avatar. Sustaining too much damage results in a loss.
 - Jumps are fixed-height.
 - A fast-fall action that cancels an active jump is available.
 - Certain types of damage can be parried. Parrying a certain number of times heals the player avatar by one.