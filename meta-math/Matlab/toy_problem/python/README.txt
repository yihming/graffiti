#============================================================
# README.txt
# Steven Garcia
#============================================================

I chose to investigate the use of SciMath in order to tackle our toy problem of raising an 
exception when the units don't match.

Scimath:
http://docs.enthought.com/scimath/

I installed Scipy using the following guide:
http://nerdslearning.wordpress.com/2012/06/08/installing-scipy-on-mac-os-x-10-7-4-with-python-2-7-3/

I also had to install the traitsui module in order for Scimath to work:
https://pypi.python.org/pypi/traitsui

The traitsui module depends on the following:
https://github.com/enthought/traits
https://github.com/enthought/pyface

Scimath has a feature called 'Unitted Functions' that allows you to mark up the units that a function 
accepts.  Using this unitting feature and the Scimath units I attempted to raise an exception by providing
the incorrect units with a function.  It may be possible to accomplish this by extending the unit parser
but that avenue was not explored.
