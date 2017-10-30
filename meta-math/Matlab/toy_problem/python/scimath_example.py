#============================================================
# scimath_example.py
# Steven Garcia
#
# Description:
# A simple example using SciMath Units:
#============================================================

from scimath.units.SI import watt, meter
from scimath.units.temperature import kelvin, K
from scimath.units.api import has_units

#parameter information can be specified in the decorator
# @has_units(inputs="k:scalar:units=K;T_front:scalar:units=kelvin;T_back:scalar:units=kelvin;x_front:scalar:meter;x_back:scalar:units=meter",
#            outputs="result:scalar:units=K")


# def calculate_heat_flux(k, T_front, T_back, x_front, x_back):
# #    return k * (T_front - T_back)/(x_front - x_back)
#     return k

@has_units(inputs="a:scalar:units=K",
           outputs="a:scalar:units=K")
def nada(foo):
    return foo


#example 1.1 of heat transfer through a slab
T_front = 50.0 * kelvin
T_back = 110.0 * kelvin
x_front = 0.0 * meter
x_back = 0.03 * meter
k = 35.0 * K #(35.0 * watt)/(meter * kelvin)
l = 2.0 * meter
w = 0.2 * meter
A = l * w
q = k * (T_front - T_back)/(x_front - x_back)

#print out variable unit information
if(False):
    print "T_front: " + str(T_front)
    print "T_back: " + str(T_back)
    print "x_front: " + str(x_front)
    print "x_back: " + str(x_back)
    print "k: " + str(k)
    print "l: " + str(l)
    print "w: " + str(w)
    print "A: " + str(A)
    print "q: " + str(q)

#q_paying_attention = calculate_heat_flux(k,T_front,T_back,x_front,x_back)
result = nada(k)
print result
result_fail = nada(l)
print result_fail
