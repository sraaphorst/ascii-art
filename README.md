# ascii-art

A PNG-to-ASCII converter offering many RGB reducers and options.

Unless the image has a background that is very delineated from the foreground, the conversion
ends up suffering if you shrink the image by too large a factor. It seems that for the images
I tried (which were in the ballpark of 2000 x 800 pixels), a factor of 6 worked well, whereas
a factor of 12 was too much except in some extreme cases.

Examples (as rendered with [`RGBReducers.luminance_BT709`](src/main/kotlin/RGB.kt)):

1. [OMORI](https://www.omori-game.com/en): [[PNG]](src/main/resources/omori.png) / [[TXT]](output/omori.txt) / [[TXT-reversed]](output/omori_reverse.txt)
2. Arrow from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en): [[PNG]](src/main/resources/arrow.png) / [[TXT]](output/arrow.txt)
3. Ringo from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en): [[PNG]](src/main/resources/ringo.png) / [[TXT]](output/ringo.txt)
4. Figue from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en): [[PNG]](src/main/resources/figue.png) / [[TXT]](output/figue.txt)
5. Gundham Tanaka from Danganronpa 2: Goodbye Despair: [[PNG]](src/main/resources/gundham_tanaka.png) / [[TXT]](output/gundham_tanaka.txt)
5. Please do not the cat: [[PNG]](src/main/resources/not_the_cat.png) / [[TXT]](output/not_the_cat.txt)
6. Me: [[PNG]](src/main/resources/me.png) / [[TXT]](output/me.txt)
7. My partner and I (lots of background noise): [[PNG]](src/main/resources/us.png) / [[TXT]](output/us.txt)

**Status:** Requires command-line interface, more experimentation, and easier access to dithering,
but can be used via the [`AsciiArt.kt`](src/main/kotlin/AsciiArt.kt) file.
