# ascii-art

A PNG-to-ASCII converter offering many RGB reducers and options.

Inspired by [The Coding Train Challenge #166](https://www.youtube.com/watch?v=55iwMYv8tGI).

Unless the image has a background that is very delineated from the foreground, the conversion
ends up suffering if you shrink the image by too large a factor. It seems that for the images
I tried (which were in the ballpark of 2000 x 800 pixels), a factor of 6 worked well, whereas
a factor of 12 was too much except in some extreme cases. For very small images, I maintained
the pixel count.

## Quantization of Gray

The 29 quantized shades of gray suggested by the challenge were:
```text
"Ñ@#W$9876543210?!abc;:+=-,._ "
```

For comparative purposes, I wrote the [`FontAnalyzer`](src/main/kotlin/FontAnalyzer.kt) to take a fixed-width font face
(in this case, `JetBrains Mono` was used), render all non-control characters, determine the density of
each character, and then sort the characters based on their densities to represent possible shadings of gray.

Then I asked for a number of bins of gray that were approximately evenly distributed through this list,
and always included the first element as white and the space character as black.

The 29 quantized shades of gray in `JetBrains Mono` as calculated by `FontAnalyzer`:
```text
"@Ñ&æÄÜB½#dòêàñé5axTÎct(ª*¡;¯ "
```

In order to see what effect a higher number of bins would have, here are the 58 quantized shades:
```text
"@MÑ©ÒÐæÅÄm8ÚBÇ6AXõböóÝ¥ÿûSV3úkhÞ¢y±fn]>v}1«ÍLjº³²*I¡~;'¯· "
```

In addition, to perform some normalization, some extra spaces (black bins) were added to the end of each:

* For the 29-quantzations, eight black bins were added.
* For the 58-quantization, 16 black bins were added.

The reversal of each of these lists was evenly divided into subsequent bins in the interval `[0,1]` representing the
level of gray, with:
* the interval beginning at 0 being black; and
* the interval ending at 1 being white.

## RGB Reducers

The space of RGB triples is represented as the elements `(r,g,b) ∈ [0,1]^3`.

It was necessary to reduce the RGB triple for each pixel in the scaled image down to a single value in the interval
`[0,1]` representing the shade of gray of each of the output characters. These were then converted to the quantized
shades of gray as listed above.

The implementation includes a large number of RGB reducers, as can be found in [`RGBReducer`](src/main/kotlin/RGB.kt).

Some experimentation suggested that the rather surprising luminance BT.709 produced the best images.
The luminance functions determine the grey value by taking a linear combination of the values for red, green, and blue,
and BT.709 works as follows:

```text
f: (r,g,b) ∈ [0,1]^3 → [0,1]
                     ↦ 0.2126r + 0.7152b + 0.0722g
```

Note that the overwhelming majority of the contribution of the gray scale comes from the value for blue, whereas
green hardly contributes at all.

## Examples

Here are some examples (as rendered with [`RGBReducers.luminance_BT709`](src/main/kotlin/RGB.kt)).

The link notations are:
* [PNG] for the original PNG file used.
* [TXT_29] represents the image in the suggested 29-state quantization of gray.
* [TXT_29C] represents the image in the calculated 29-state quantization of gray.
* [TXT_58C] represents the image in the calculated 58-state quantization of gray.
* For the OMORI image that was already essentially represented in grayscale, I
calculated the regular mapping and the inverse mapping (i.e. black maps to white, white maps to black.)

It is recommended that you click to view the raw image, and then scale down the text size.

| Image Information                                                              |                  PNG                  | TXT_29 |                               TXT_29C                                |                  TXT_58C                   |
|--------------------------------------------------------------------------------|:-------------------------------------:|:---:|:--------------------------------------------------------------------:|:------------------------------------------:|
| [OMORI](https://www.omori-game.com/en)                                         | [[PNG]](src/main/resources/omori.png) | [[TXT-29]](output/omori_29.txt) | [[TXT-29C]](output/omori_29c.txt) |     [[TXT-58C]](output/omori_58c.txt)      |
| OMORI inverse                                                                  |                                       |  [[TXT-29]](output/omori_29_reversed.txt) | [[TXT-29C]](output/omori_29c_reversed.txt) | [[TXT-58C]](output/omori_58c_reversed.txt) |
| Arrow from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en) | [[PNG]](src/main/resources/arrow.png) | [[TXT-29]](output/arrow_29.txt) | [[TXT-29C]](output/arrow_29c.txt) |     [[TXT-58C]](output/arrow_58c.txt)      |
| Ringo from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en) | [[PNG]](src/main/resources/ringo.png) | [[TXT-29]](output/ringo_29.txt) | [[TXT-29C]](output/ringo_29c.txt) |     [[TXT-58C]](output/ringo_58c.txt)      |
| Figue from [Soul Hackers 2](https://soulhackers2.atlus.com/index.html?lang=en) | [[PNG]](src/main/resources/figue.png) | [[TXT-29]](output/figue_29.txt) | [[TXT-29C]](output/figue_29c.txt) |     [[TXT-58C]](output/figue_58c.txt)      |
| Gundham Tanaka (Danganronpa 2)                                                 | [[PNG]](src/main/resources/gundham_tanaka.png) | [[TXT-29]](output/gundham_tanaka_29.txt) | [[TXT-29C]](output/gundham_tanaka_29c.txt) | [[TXT-58C]](output/gundham_tanaka_58c.txt) |
| Please do not the cat                                                          | [[PNG]](src/main/resources/not_the_cat.png) | [[TXT-29]](output/not_the_cat_29.txt) | [[TXT-29C]](output/not_the_cat_29c.txt) |  [[TXT-58C]](output/not_the_cat_58c.txt)   |
| Me                                                                             | [[PNG]](src/main/resources/me.png) | [[TXT-29]](output/me_29.txt) | [[TXT-29C]](output/me_29c.txt) |       [[TXT-58C]](output/me_58c.txt)       |
| My partner and I (background noise)                                            | [[PNG]](src/main/resources/us.png) | [[TXT-29]](output/us_29.txt) | [[TXT-29C]](output/us_29c.txt) |       [[TXT-58C]](output/us_58c.txt)       |

## Remaining Work

**Status:**
* Requires command-line interface to specify file and parameters.
* Experimentation with dithering.
