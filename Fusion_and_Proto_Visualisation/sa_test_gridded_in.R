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
# Quick Test for gridded data on matrix. Resamples incoming data set to new grid.

require(akima)

jet.colours <- colorRampPalette(c("#00007F", "blue", "#007FFF", "cyan", "#7FFF7F", "yellow",
 "#FF7F00", "red", "#7F0000"))

# Read in gridded data matrix.
# Note: Test data set cannot be distributed, however simply contains 240 (h) x 121 (v) gridded area samples.
tab2d.dat.in <- as.matrix(read.table(file="Source data/era_tcwv_jul_rmse.dat"))
# Define coordinate system.
x.coord.vals <- ((0:(ncol(tab2d.dat.in)-1)) * 1.5) - 179.25
y.coord.vals <- ((0:(nrow(tab2d.dat.in)-1)) * 1.5) - 90

# Convert to matrix.
mat.dat.in <- t(tab2d.dat.in[nrow(tab2d.dat.in):1, ])

# Plot a quick check (contours).
filled.contour(x.coord.vals, y.coord.vals, mat.dat.in, color.palette=jet.colours)

# *** Resample to new grid.
# Replicate coordinate values to produce full sample position set.
x.coords.all <- matrix(rep(x.coord.vals, length(y.coord.vals)), byrow=T, nrow=length(y.coord.vals))
y.coords.all <- matrix(rep(y.coord.vals, length(x.coord.vals)), byrow=F, ncol=length(x.coord.vals))

# Define destination grid.
x.dest <- seq(-179.25, 179.25, length.out=240)
y.dest <- seq(-90, 90, length.out=121)

# Interpolate.
f.interp <- interp(x.coords.all, y.coords.all, t(mat.dat.in),
 xo=x.dest, yo=y.dest)

dev.new()

# Plot as raster image on new grid.
image(f.interp$x, f.interp$y, f.interp$z, col=jet.colours(100))
