\begin{code}
module DeBruijn where

import qualified AbstractSyntax as S
import Data.List
import Data.Char

type Type = S.Type
data Term = Var Int
          | IntConst Integer
          | Tru
          | Fls
          | Abs Type Term
          | App Term Term
          | If  Term Term Term
          | Fix Term
          | Let Term Term
          | Bop BOP Term Term
          | Bpr BPR Term Term

          

instance Show Term where
          show (Var i) = "(Index " ++ show i ++ ")"
          show (IntConst i) = show i
          show Tru = "true"
          show Fls = "false"
          show (Abs tau t) = "abs(" ++ ":" ++ show tau ++ "." ++ show t ++ ")"
          show (App t1 t2) = "app(" ++ show t1 ++ "," ++ show t2 ++ ")"
          show (If t1 t2 t3) = "if " ++ show t1 ++ " then " ++ show t2 ++ " else " ++ show t3 ++ " fi"
          show (Fix t) = "fix " ++ show t
          show (Let t1 t2) = "let "++ show t1 ++ " " ++ show t2
          show (Bop op t1 t2) = show op ++ "(" ++ show t1 ++ ", " ++ show t2 ++ ")"
          show (Bpr p t1 t2) = show p ++ "(" ++ show t1 ++ "," ++ show t2 ++ ")"

-- Binary Operator 
data BOP = Add | Sub | Mul | Div | Nand
          
instance Show BOP where 
          show Add = "+"
          show Sub = "-"
          show Mul = "*"
          show Div = "/"
          show Nand= "^"

-- Binary Predicate
data  BPR = Eq | Lt
instance Show BPR where
  show Eq = "="
  show Lt = "<"

newtype Environment = Env [S.Var]
                      deriving Show 


lookupEnv :: Environment -> S.Var -> Int
lookupEnv (e@(Env [])) x  =  error ("variable " ++ x ++ " not bound in environment " ++ show e)
lookupEnv (Env es) x = 
  case elemIndices x es of
    [] -> error "This term has free variables!"
    _  -> head $ elemIndices x es

  
finalProcess :: S.Term -> Term
finalProcess (S.Var x) = Var (read x::Int)
finalProcess (S.Abs x tau t1) = Abs tau (finalProcess t1)
finalProcess (S.App t1 t2) = App (finalProcess t1) (finalProcess t2)
finalProcess S.Tru = Tru
finalProcess S.Fls = Fls
finalProcess (S.If t1 t2 t3) = If (finalProcess t1) (finalProcess t2) (finalProcess t3)
finalProcess (S.IntConst n) = IntConst n
finalProcess (S.IntAdd t1 t2) = Bop Add (finalProcess t1) (finalProcess t2)
finalProcess (S.IntSub t1 t2) = Bop Sub (finalProcess t1) (finalProcess t2)
finalProcess (S.IntMul t1 t2) = Bop Mul (finalProcess t1) (finalProcess t2)
finalProcess (S.IntDiv t1 t2) = Bop Div (finalProcess t1) (finalProcess t2)
finalProcess (S.IntNand t1 t2) = Bop Nand (finalProcess t1) (finalProcess t2)
finalProcess (S.IntEq t1 t2) = Bpr Eq (finalProcess t1) (finalProcess t2)
finalProcess (S.IntLt t1 t2) = Bpr Lt (finalProcess t1) (finalProcess t2)
finalProcess (S.Fix t) = Fix (finalProcess t)
finalProcess (S.Let x t1 t2) = Let (finalProcess t1) (finalProcess t2)


toDeBruijn :: S.Term -> Term
toDeBruijn t = 
  finalProcess $ f (t, Env [])
  where
    f :: (S.Term, Environment) -> S.Term
    f (S.Abs x tau t1, Env es) = S.Abs x tau (f (t1, Env (x:es)))
    f (S.App t1 t2, e) = S.App (f(t1, e)) (f(t2, e))
    f (S.Var x, e) = S.Var (show (lookupEnv e x))
    f (S.Let x t1 t2, Env es) = S.Let x (f (t1, Env es)) (f (t2, Env (x:es)))
    f (S.If t1 t2 t3, e) = S.If (f(t1, e)) (f(t2, e)) (f(t3, e))
    f (S.IntAdd t1 t2, e) = S.IntAdd (f(t1, e)) (f(t2, e))
    f (S.IntSub t1 t2, e) = S.IntSub (f(t1, e)) (f(t2, e))
    f (S.IntMul t1 t2, e) = S.IntMul (f(t1, e)) (f(t2, e))
    f (S.IntDiv t1 t2, e) = S.IntDiv (f(t1, e)) (f(t2, e))
    f (S.IntNand t1 t2, e) = S.IntNand (f(t1, e)) (f(t2, e))
    f (S.IntEq t1 t2, e) = S.IntEq (f(t1, e)) (f(t2, e))
    f (S.IntLt t1 t2, e) = S.IntLt (f(t1, e)) (f(t2, e))
    f (S.Fix t, e) = S.Fix (f(t, e))
    f (t, e) = t
\end{code}
