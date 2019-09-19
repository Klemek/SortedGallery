[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Klemek/SortedGallery.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Klemek/SortedGallery/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/Klemek/SortedGallery.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Klemek/SortedGallery/alerts/)

# Sorted Gallery
A small slideshow software similar to the Windows one but with more possibilities.
Allows to give a score to images and sort them during slideshow.

## Usage

Create the config.properties file next to the .jar file.
All images in the root folder will be sorted and put in the correct score folder at start.

## Keys

* [Escape] - Exit software
* [Right arrow/Enter/Left click] - Next image
* [Left arrow/Right click] - Previous image
* [Space] - Start/stop autoplaying
* [Numpad +] - Increase delay of autoplay
* [Numpad -] - Reduce delay of autoplay
* [Backspace] - Order by date/random
* [Right parenthesis] - Show/Hide score
* [Page up] - Increase image score
* [Page down] - Decrease image score
* [Begin/End] - Go to first image
* [number] - Show only image with score (number)
* [ctrl+number] - Add all images with score (number)
* [numpad 1-9] - zoom
* [numpad 0] reset zoom

## config.properties

Example file

```
rootFolder=./
minLevel=1
defaultLevel=3
maxLevel=5
defaultDelay=2000
defaultShuffle=false
defaultShowScore=true
cacheSize=10
fileThreshold=2097152
```

* **rootFolder** - relative path to root folder
* **minLevel** - minimal score
* **defaultLevel** - default score
* **maxLevel** - maximal score
* **defaultDelay** - starting delay for autoplay
* **defaultShuffle** - images shuffled a start
* **defaultShowScore** - show image score at start
* **cacheSize** - image cache size
* **fileThreshold** - display warnings if images are to big
