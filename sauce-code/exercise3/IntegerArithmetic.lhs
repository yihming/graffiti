\begin{code}

module IntegerArithmetic where
import Data.Bits

intRestrictRangeAddMul :: Integer -> Integer
intRestrictRangeAddMul m = m `mod` 4294967296

intAdd :: Integer -> Integer -> Integer
intAdd m n = intRestrictRangeAddMul (m + n)

intSub :: Integer -> Integer -> Integer
intSub m n = m - n

intMul :: Integer -> Integer -> Integer
intMul m n = intRestrictRangeAddMul (m * n)

intDiv :: Integer -> Integer -> Integer
intDiv m n = if n == 0 then error "integer division by zero" else m `div` n

intNand :: Integer -> Integer -> Integer
intNand m n = complement (m .&. n)

intEq :: Integer -> Integer -> Bool
intEq m n = m == n

intLt :: Integer -> Integer -> Bool
intLt m n = m < n

\end{code}
