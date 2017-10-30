module AbstractSyntax where

import Data.List

type Var = [Char]

data Program = MonoFunc FuncDef
             | PolyFunc [FuncDef]

instance Show Program where
  show (MonoFunc f) = show f
  show (PolyFunc []) = ""
  show (PolyFunc (f:fs)) = show f ++ "end\n\n" ++ show (PolyFunc fs)
  
data FuncDef = Func Var ArgumentList RetList FuncBody
instance Show FuncDef where
  show (Func v al rl body) = 
    "function [ " ++ show rl ++ " ] = " ++ v ++ "( " ++ show al ++ " )\n" 
    ++ show body

data ArgumentList = ArgList [Arg]
instance Show ArgumentList where
  show (ArgList as) = concat $ intersperse ", " (map show as)

data Arg = NullArg Var 
         | InitArg Var Double
instance Show Arg where
  show (NullArg v) = v
  show (InitArg v d) = v ++ " = " ++ show d

data RetList = RetList [Var]
instance Show RetList where
  show (RetList rl) = concat $ intersperse ", " rl

data FuncBody = Body [Statement]
instance Show FuncBody where
  show (Body []) = ""
  show (Body (s:ss)) = show s ++ show (Body ss) 

data Statement = ClosedStatement Stmt
               | OpenStatement Stmt
instance Show Statement where
  show (ClosedStatement s) = show s ++ ";\n"
  show (OpenStatement s) = show s ++ "\n"

data Stmt = Assign Var RHSExpr
          | Return
          | Break
          | Continue
instance Show Stmt where
  show (Assign v e) = v ++ " = " ++ show e
  show (Return) = "return"
  show (Break) = "break"
  show (Continue) = "continue"

data RHSExpr = NumRHS NumExpr
instance Show RHSExpr where
  show (NumRHS ne) = show ne

data NumExpr = NumInt Integer
             | NumReal Double
             | Var Var
             | Bop Bop NumExpr NumExpr
             | Par NumExpr 

instance Show NumExpr where
  show (NumInt n) = show n
  show (NumReal a) = show a
  show (Var v) = v
  show (Bop bop ne1 ne2) = show ne1 ++ " " ++ show bop ++ " " ++ show ne2
  show (Par ne) = "(" ++ show ne ++ ")"

data Bop = Add | Sub | Mul | Div

instance Show Bop where
  show Add = "+"
  show Sub = "-"
  show Mul = "*"
  show Div = "/"