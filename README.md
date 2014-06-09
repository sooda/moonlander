# Moonlander

Moonlander is a library for integrating Processing with GNU Rocket, which allows you to take full control of any time-varying parameters in your sketches.

If you don't know what GNU Rocket is, read about it [here](https://github.com/kusma/rocket). In short, GNU Rocket is a sync-tracker, a tool mainly used for synchronizing music and visuals in demoscene productions. However, it can be also used to control basically anything that varies with time; cameras, colors, coordinates, you name it. Basically anything that can be presented by a floating point value can be controlled.

## Installation

### From release package

Download the latest release from [this url](http://firebug.kapsi.fi/moonlander/Moonlander.zip). Extract the library into the libraries folder of your Processing sketchbook (..libraries/Moonlander) and restart Processing. The library should be then visible in menu (`Sketch -> Import library`) – it it's there, you're ready to try the example below!

If you encounter any problems, see more info at [Processing Wiki](http://wiki.processing.org/w/How_to_Install_a_Contributed_Library) (section about manual install).

TBD: This library should be in Processing's database (which makes install process super easy)

### From sources (Git repository)

Edit variables `sketchbook.location` and `classpath.local.location` in `resources/build.properties`. By default, the build process only works on Mac – for other platforms, you must edit the mentioned variables for build to succeed (this is clumsy and should be replaced with better solution later on, but suffices for now).

Build the library with `ant -f resources/build.xml` – the build process also automatically installs the library into Processing's library folder, so it will be immediately usable after you restart Processing (must be only done after the first build). Then try the example below!


## Usage

Initialize the library, call `start()` at the end of `setup()`, call `update()` for every frame and you're done. You can query current value of any track by calling `getValue(String trackName)`.

If GNU Rocket is running at it's default address and port (localhost:1338), Moonlander attaches itself to it. If connection cannot be made, the data is loaded from a file called `syncdata.rocket`, which should be in your sketch's folder. Workflow goes like this:

1. Run GNU Rocket.
2. Start your sketch. Sketch connects to GNU Rocket using Moonlander. Rocket takes control of your sketch.
3. Develop and sync. GNU Rocket keeps your data even if you close the running sketch. When you start it again, Rocket lets you continue where you left off. 
4. Before you close Rocket, save project to a file called `syncdata.rocket` and place it in your sketch's folder. When Rocket isn't running, this is the file Moonlander by default tries to load your syncdata from.
5. You can distribute your sketch with the saved syncdata file and everything should work as normal!

Example:

```java
import moonlander.library.*;

// Minim must be imported when using Moonlander with soundtrack.
import ddf.minim.*;

Moonlander moonlander;

void setup() {
    // Parameters: PApplet, filename (file should be in sketch's folder), 
    // beats per minute, rows per beat
    moonlander = Moonlander.initWithSoundtrack(this, "filename.mp3", 120, 4);

    // .. other initialization code ...

    moonlander.start();
}

void draw() {
    // Handles communication with Rocket
    moonlander.update();

    // Get current value of a track
    double value = moonlander.getValue("my_track");

    // Use it somehow
    background((int)value);
}

```

If you want to run Moonlander without a music track, replace line `moonlander = Moonlander.initWithSoundTrack(this, "filename.mp3", 120, 4);` with `moonlander = new Moonlander(this, new TimeController(4));`, where the integer parameter passed to `TimeController` means "rows per second".

## API

Moonlandander is mainly operated through it's main class, which is unsurprisignly called Moonlander. Normally (I'd say 99,9% of cases) you only need this class to use the library. However, if you wan't to do something fancy, like build your own controller, you need to dig a little deeper. 

### Moonlander

TODO: document


## For developers

Want to contribute? As usual, submit a patch by opening a pull request.

Before sending your patch away, please make sure that old tests pass and write tests for new code if necessary. This project uses JUnit for automated testing, because testing Processing libraries by hand is very painful. Run tests with `ant tests.run`, which builds the library and runs all the tests. 

Note that the amount of unit tests should be kept at minimum and parts that would require way too much mocking (like socket connections) should not be unit tested. Use unit tests only for those parts of code that are running "behind the scenes" and thus hard to test and debug in Processing (eg. not easily testable by poking the public API).
