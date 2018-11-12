# Droids

Droids is a Tetris clone. The game includes a playing field consisting of a 12 × 10 grid and 7 geometric figures called "tetrominoes". The aimof the game is to fit these tetrominoes between them in order to complete lines that will be deleted. Each level has a goal (Goal) of lines to complete. Exceeded this goal, the level increases and with it the speed of tetrominoes. The ultimate goal is to be able to gain the highest score possible before the game ends. The game ends when a tetramino reaches the top edge of the playing field. The player wins points every time you complete a line or accelerates the fall of a tetromino.

This game has been created only for educational purpose, it has no claim to be a complete game distributable through the Android market. It's my belief that you can get inspiration from this source code to implement your own video games.

# Screenshots

![Main Menu](https://raw.githubusercontent.com/wiki/sasadangelo/Droids/img/Screenshot_Droids_Home.png) ![Game](https://raw.githubusercontent.com/wiki/sasadangelo/Droids/img/Screenshot_Droids.png)

# Limitations

Currently the game could go on forever and it is not expected that the player finish the game after a certain number of levels. The level change does not result in a change of graphics, simply at each level the game difficulty increases because the tetrominoes descend faster.

# Credits

The author of the framework code, later modified by me, is [Mario Zachner](https://github.com/badlogic) (@github.com/badlogic) that released the code with GPL3 license as a resource of the Beginning Android Games book. The framework is a very simplified version of the open source library Libgdx released under GPL3 license. 

# License
[GPL3](https://www.gnu.org/licenses/gpl-3.0.en.html)

# Related Projects

[Alien Invaders](https://github.com/sasadangelo/AlienInvaders), [Mr Snake](https://github.com/sasadangelo/MrSnake)

# Installation & Run

[Download and install Android Studio](http://code4projects.altervista.org/how-to-install-android-studio/). If you already have Android Studio installed, make sure it is at the latest level. Once Android Studio is up and running make sure all projects are closed (if a project is open do File->Close Project), the "Welcome to Android Studio" Panel appears. Select the option "Check out project from version control" and then GitHub. 

Fill the following fields:

    Git Repository URL: https://github.com/sasadangelo/Droids.git
    Parent Directory: "an empty directory previously created"
    Directory Name: Droids

The source code will be downloaded and the Droids project will be created. Now you can run the code doing Run->Run. You can execute the code on Physical or Virtual device. For more details, you can read the last three sections of the following [article](http://code4projects.altervista.org/how-to-create-an-android-application/).

# Troubleshooting

Sometime could happen that there is incompatibility between the level of gradle declared in the source code with the one installed in the development environment. When this occurs Android Studio will show also a link to fix it. Click the link to solve the issue.
