/**
 * thermal_flux_example
 *
 * An example of using the boost::units C++ library to perform unit
 * checking on values. 
 *
 * This example shows how to define new types of units and how to use
 * them alongside predefined units
 *
 * To see an example of the compiler catching mis-matched units, 
 * uncomment the #define MISMATCHED_UNITS line down below.
 *
 */
#include <iostream>
#include <sstream>

#include <boost/units/cmath.hpp>
#include <boost/units/io.hpp>
#include <boost/units/systems/si.hpp>
#include <boost/units/systems/si/codata/physico-chemical_constants.hpp>
#include <boost/units/systems/si/io.hpp>
#include <boost/units/physical_dimensions.hpp>
#include <boost/mpl/assert.hpp>

namespace mpl = boost::mpl;
using namespace boost::units;
using namespace boost::units::si;

// Uncomment the following line to see the syntax error when units are mismatched
//#define MISMATCHED_UNITS

// Define thermal conductivity unit
typedef unit<thermal_conductivity_dimension, si::system>  thermal_conductivity;

// derived dimension for thermal_flux : Mass^1 Time^-3
typedef derived_dimension<
            mass_base_dimension,   1,
            time_base_dimension,  -3>::type thermal_flux_dimension;

typedef unit<thermal_flux_dimension, si::system> thermal_flux;

// Create constants
BOOST_UNITS_STATIC_CONSTANT(watts_per_meters_kelvin, thermal_conductivity);
BOOST_UNITS_STATIC_CONSTANT(watts_per_meters_sq, thermal_flux);


/**
 * Calculate heat flux
 */
quantity<thermal_flux> calculate_thermal_flux(quantity<thermal_conductivity> k,
                                              quantity<temperature>          t_front,
                                              quantity<temperature>          t_back,
                                              quantity<length>               x_front,
                                              quantity<length>               x_back)
{
   return k * (t_front - t_back) / (x_front - x_back);
}

/**
 * Run test
 */
void test_calculate_heat_flux()
{
   std::stringstream out;

   // Declare some values with units
   quantity<si::temperature>      t_front = 50   * kelvin;
   quantity<si::temperature>      t_back  = 110  * kelvin;
   quantity<si::length>           x_front = 0    * meters;
   quantity<si::length>           x_back  = 0.03 * meters;
   quantity<thermal_conductivity> k       = 35   * watts_per_meters_kelvin;
   
   // Perform the calculation. If the units do not match, the compiler will generate errors
   quantity<thermal_flux>         q       = calculate_thermal_flux(k, t_front, t_back, x_front, x_back);
   
   out << q;

   // Expected output
   std::string expected = "70000 kg s^-3";
   
   // Verify that the test passed
   if(expected != out.str())
   {
      std::cout << "Test failed, expected " << std::endl;
      std::cout << "  "   << expected << std::endl;
      std::cout << "Got " << std::endl;
      std::cout << "  "   << out.str() << std::endl;
   }
   else
   {
      std::cout << "Passed! " << out.str() << std::endl;
   }
   
#ifdef MISMATCHED_UNITS
   // This is an example of mis-matched units. The 5th argument should be a length, but
   // instead, thermal flux is provided. The compiler will reject the following line
   quantity<thermal_flux> q_broken = calculate_thermal_flux(k, t_front, t_back, x_front, k);
#endif
}

/**
 * Entry point
 */
int main()
{    
   test_calculate_heat_flux();
   return 0;
}
