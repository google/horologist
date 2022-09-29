# Media sample app

The goal of this sample is to show how to implement an audio media app for Wear OS, using the
Horologist media libraries, following the design principles described
in [Considerations for media apps](https://developer.android.com/training/wearables/principles#media-apps)
.

The app supports listening to downloaded music. It loads
a [music catalog](https://storage.googleapis.com/uamp/catalog.json) from a remote server and allows
the user to browse the albums and songs. Tapping on a song will play it through connected speakers
or headphones. Under the hood it
uses [Media3](https://developer.android.com/jetpack/androidx/releases/media3).

## Features

The app showcases the implementation of the following features:

- Media playback, restricted to paired Bluetooth devices
- Launch of Bluetooth settings to connect devices for media playback
- Volume control
- Radial background based on media artwork color palette
- Download media
- API sync with WorkManager
- Network rules
- Splash screen
- Marquee text for song titles
- Tiles

This list is not exhaustive.

## Audio

Music provided by the [Free Music Archive](http://freemusicarchive.org/).

- [Wake Up](http://freemusicarchive.org/music/The_Kyoto_Connection/Wake_Up_1957/) by
  [The Kyoto Connection](http://freemusicarchive.org/music/The_Kyoto_Connection/).

Recordings provided by the [Ambisonic Sound Library](https://library.soundfield.com/).

- [Pre Game Marching Band](https://library.soundfield.com/track/163) by Watson Wu
- [Chickens on a Farm](https://library.soundfield.com/track/129) by Watson Wu
- [Rural Market Busker](https://library.soundfield.com/track/55) by Stephan Schutze
- [Steamtrain Interior](https://library.soundfield.com/track/65) by Stephan Schutze
- [Rural Road Car Pass](https://library.soundfield.com/track/57) by Stephan Schutze
- [10 Feet from Shore](https://library.soundfield.com/track/114) by Watson Wu
