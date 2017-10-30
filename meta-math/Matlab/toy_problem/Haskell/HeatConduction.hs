module Main where

import qualified Prelude
import Numeric.Units.Dimensional.Prelude
import qualified Numeric.NumType as NT

-- Quantity definition for K.
type DK = Dim NT.Pos1 NT.Pos1 NT.Neg3 NT.Zero NT.Neg1 NT.Zero NT.Zero
type KQuantity = Quantity DK

-- Quantity definition for Heat Flux q.
type DHeatFlux = Dim NT.Zero NT.Pos1 NT.Neg3 NT.Zero NT.Zero NT.Zero NT.Zero
type HeatFlux = Quantity DHeatFlux

type DHeatRate = DPower
type HeatRate = Quantity DHeatRate


calculate_heat_flux :: (Fractional a) => KQuantity a -> ThermodynamicTemperature a -> ThermodynamicTemperature a -> Length a -> Length a -> HeatFlux a
calculate_heat_flux k t_front t_back x_front x_back = 
  k * (t_front - t_back) / (x_front - x_back)
  

simulation :: IO ()
simulation = do
  let t_front = 50 *~ degreeCelsius
  let t_back = 110 *~ degreeCelsius
  let x_front = 0 *~ meter
  let x_back = 0.03 *~ metre
  let k = 35 *~ (watt / (meter * kelvin))
  let capitalA = 0.4 *~ (meter ^ pos2)
  -- The type signature at the end of following statements is for verification.
  let q = calculate_heat_flux k t_front t_back x_front x_back :: HeatFlux Double
  let capitalQ = q * capitalA
  putStrLn $ "Heat Flux (q) = " ++ show q
  putStrLn $ "Heat Transfer Rate (Q) = " ++ show capitalQ


main :: IO ()
main = do
  simulation