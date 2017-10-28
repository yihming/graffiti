\begin{code}
module Main where

import qualified System.Environment
import Data.List
import IO

import qualified AbstractSyntax as S
import qualified StructuralOperationalSemantics as E
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
      putStrLn (show term)

      putStrLn ("----Type----")
      putStrLn (show (T.typeCheck term))


      putStrLn ("----DBTerm----")
      putStrLn (show dbterm)
      putStrLn ("----Natural Semantics with Clo,Env and DB Term----")
      putStrLn (show (NDB.eval dbterm))
      putStrLn ("----CES Machine Code----")
      putStrLn (show (CES.compile dbterm) ++ "\n")

      putStrLn ("----CES Final state----")
      putStrLn (show (CES.loop (CES.compile dbterm, [], [])) ++ "\n")
      putStrLn ("----CES Eval----")
      putStrLn (show (CES.eval dbterm))


      let answerType = S.TypeInt
      let cpsterm = CPS.toCPS answerType term
      putStrLn ("----CPS Form----")
      putStrLn (show cpsterm)
      putStrLn ("---CPS Normal Form----")
      putStrLn (show (E.eval (S.App cpsterm (S.Abs "x" answerType (S.Var "x")))))
        
      let bodyterm = (S.App cpsterm (S.Abs "x" answerType (S.Var "x"))) 
      putStrLn ("----CE3R DBterm----")
      putStrLn (show (DB.toDeBruijn bodyterm))
      putStrLn ("----CE3R Machine----")
      putStrLn (show (CE3R.eval (DB.toDeBruijn bodyterm)))
\end{code}
