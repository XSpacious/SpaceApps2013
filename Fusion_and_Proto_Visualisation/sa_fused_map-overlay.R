# XSpacious
# International Space Apps Challenge 2013
#
# Licensed under a Creative Commons Attribution 3.0 Unported License.
# http://creativecommons.org/licenses/by/3.0/
#
# When using this code please attribute as follows
# including code authors and team members:
#
#   This code was developed by the XSpacious project team
#   as part of the International Space Apps Challenge 2013.
# 
#   Code Authors:
#   Matthew C. Forman
#
#   Team Members:
#   Abdulrazaq Abba
#   Rallou Dadioti
#   Matthew C. Forman
#   Ron Herrema
#   Joel Mitchelson, Ogglebox Sensory Computing Ltd.
#   Konstantina Saranti
#   Andy Schofield
#   Tim Trent
#
#
# sa_rused_map_overlay.R - Resamples point-orientated data to regular grid; overlays Leicester city map raster.
#
# Developed under R 2.15.1; uses packages "akima" and "RgoogleMaps".


require(akima)	# For linear interpolation fn.
require(RgoogleMaps)	# Obtains map overlay raster from Google.

# Set up colour map for health stats. layer (256 slots should do..)
cmapfn.vbh <- colorRampPalette(c("#00FF00", "#FF0000"))
cmap.vbh <- cmapfn.vbh(256)
# Append a fixed alpha channel value.
cmap.vbh <- paste(cmap.vbh, "80", sep="")

# Read in source data (CSV).
df.in.env <- read.csv(file="Sample data/pollIndexLeic_20110101T023000.csv")
df.in.health <- read.csv(file="Sample data/HealthVeryBad.csv")
# Subset observations if necessary (or straight copy if not).
df.plot.env <- df.in.env
df.plot.health <- df.in.health

# Fetch map raster from Google Maps; use overall spatial coordinate range in both data files to set map limits.
gmap.leic <- GetMap.bbox(range(df.in.env$long, df.in.health$long), range(df.in.env$lat, df.in.health$lat), destfile="leic.png")
# Define map grid.
x.map <- seq(min(df.in.env$long, df.in.health$long), max(df.in.env$long, df.in.health$long), length.out=gmap.leic$size[1])
y.map <- seq(min(df.in.env$lat, df.in.health$lat), max(df.in.env$lat, df.in.health$lat), length.out=gmap.leic$size[2])
# Draw map base on lat/long spatial grid (as greyscale).
image(x.map, y.map, gmap.leic$myTile[, nrow(gmap.leic$myTile):1], col=grey.colors(256),
 main="Mean pollution index (1st Jan 2011) & poor health (2011 census)", xlab="Longitude (deg.)", ylab="Latitude (deg.)")

# * Resample.

# Define destination grid.
x.dest <- x.map
y.dest <- y.map

# Resample VBadHealth to produce a smoothly varying overlay.
f.vbh.interp <- interp(df.plot.health$long, df.plot.health$lat, df.plot.health$val,
 xo=x.dest, yo=y.dest)
# ...and plot it.
image(f.vbh.interp$x, f.vbh.interp$y, f.vbh.interp$z, col=cmap.vbh, add=TRUE)

# Compute environmental measurement set point sizes, and overplot.
pt.cex <- (df.plot.env$val - min(df.plot.env$val)) / (max(df.plot.env$val) - min(df.plot.env$val)) * 2 + 0.5
points(df.plot.env$long, df.plot.env$lat, pch=16, cex=pt.cex, col="blue")

# Add legend.
legend("topleft", legend="Pollution index", pch=16, col="blue")
