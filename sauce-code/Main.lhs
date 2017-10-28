\begin{code}
module Main where

import qualified System.Environment
import Data.List
import System.IO

import qualified AbstractSyntax as S
import qualified StructuralOperationalSemantics as SOS
import qualified NaturalSemantics as NS
import qualified IntegerArithmetic as I
import qualified Typing as T


import qualified CESMachine as CES
import qualified DeBruijn as DB
import qualified NSWCAD as NDB

import qualified CPS as CPS
import qualified CE3RMachine as CE3R

main :: IO()
main = 
    do
      args <- System.Environment.getArgs
      let [sourceFile] = args
      source <- readFile sourceFile
      let tokens = S.scan source
      let term = S.parse tokens
      let dbterm = DB.toDeBruijn term
      putStrLn ("----Term----")
      putStrLn (show term ++ "\n")

      putStrLn ("----Type----")
      putStrLn (show (T.typeCheck term) ++ "\n")

      putStrLn ("----DBTerm----")
      putStrLn (show dbterm)
      putStrLn ("----Natural Semantics with Clo,Env and DB Term----")
      putStrLn (show (NDB.eval dbterm) ++ "\n")

      putStrLn ("----CES Machine Code----")
      putStrLn (show (CES.compile dbterm) ++ "\n")
	
      putStrLn ("----CES Eval----")
      putStrLn (show (CES.eval dbterm) ++ "\n")      

      putStrLn ("----Normal Form Using Structural Operational Semantics----")
      putStrLn (show (SOS.eval term) ++ "\n")
      
      let answerType = S.TypeNull
      let cpsterm = CPS.toCPS answerType term
      putStrLn ("----CPS Form----")
      putStrLn (show cpsterm ++ "\n")
      
      putStrLn ("----CPS Type----")
      putStrLn (show (T.typeCheck cpsterm) ++ "\n")
      
      let bodyterm = (S.App cpsterm (S.Abs "x" answerType (S.Var "x")))
      --putStrLn ("----De Bruijn Form of the CPS Term----")
      --putStrLn (show (DB.toDeBruijn bodyterm))
      putStrLn ("----Normal Form of the De Bruijn CPS Term----")
      putStrLn (show (NDB.eval (DB.toDeBruijn bodyterm)) ++ "\n")
      
      putStrLn ("----CPS Body Term----")
      putStrLn (show bodyterm)
      --putStrLn ("----CPS Normal Form with Structural Operational Semantics----")
      --putStrLn (show (SOS.eval bodyterm))
      --putStrLn ("----CPS Normal Form with Natural Semantics----")
      --putStrLn (show (NS.eval bodyterm) ++ "\n")
        
       
      putStrLn ("----CE3R DBterm----")
      putStrLn (show (DB.toDeBruijn bodyterm))
      putStrLn ("----CE3R Machine----")
      putStrLn (show (CE3R.eval (DB.toDeBruijn bodyterm)))
\end{code}
