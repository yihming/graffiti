\begin{code}
module NaturalSemantics where

import Data.List
import Data.Maybe
import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I

{-
data Value = BoolVal Bool | IntVal Integer | Clo S.Term Env
instance Show Value where
  show (BoolVal v) = show v
  show (IntVal v) = show v
  show (Clo t e) = show t

type Env = [(S.Var, Value)]

lookupEnv :: S.Var -> Env -> Maybe S.Term
lookupEnv x [] = Nothing
lookupEnv x e@((y, v):es) =
  if (x == y)
     then Just v
     else lookupEnv x es


evalWithEnv :: S.Term -> Env -> Maybe Value

evalWithEnv e t = 
  case t of
    S.Tru          -> Just (BoolVal True)      -- B-Value
    S.Fls          -> Just (BoolVal False)     -- B-Value
    S.IntConst n   -> Just (IntVal n)          -- B-Value
    S.Abs x tau t1 -> Just (Clo t e)           -- B-Value
    S.If t1 t2 t3  -> case evalWithEnv e t1 of
                        Just (BoolVal True) -> case evalWithEnv e t2 of
                                                 Just v -> Just v  -- B-IfTrue
                                                 _      -> Nothing
                        Just (BoolVal False)-> case evalWithEnv e t3 of
                                                 Just v -> Just v  -- B-IfFalse
                                                 _      -> Nothing
                        _                   -> Nothing
    S.Var x        -> case lookupEnv x e of
                        Just v  -> Just v
                        Nothing -> Nothing
    S.App t1 t2    -> case evalWithEnv e t1 of
                        Just (Clo (S.Abs x tau t11) e') -> case evalWithEnv e t2 of
                                                             Just v2 -> case evalWithEnv (x, v2):e t11 of
                                                                          Just v -> Just v
                                                                          _      -> Nothing
                                                             _       -> Nothing
                        Just (Clo (S.Fix t11) e')       -> case evalWithEnv e' (S.Fix t11) of
                                                             Just (Clo (S.Abs x tau t') e'') -> case evalWithEnv e t2 of
                                                                                                  Just v2 -> case evalWithEnv (x, v2):e'' t' of
                                                                                                               Just v -> Just v
                                                                                                               _      -> Nothing
                                                                                                  _       -> Nothing
                                                             _                               -> Nothing
                        _                               -> Nothing
    S.IntAdd t1 t2  -> case evalWithEnv e t1 of
                         Just v1 -> case evalWithEnv e t2 of
                                      Just v2 -> 
                                                                                                  

eval :: S.Term -> S.Term
eval t = 
  case evalWithEnv [] t of
    Just v  -> v
    _       -> t
-}


eval :: S.Term -> S.Term

eval (S.If t1 t2 t3) = 
  case eval t1 of
    S.Tru -> eval t2  -- B-IfTrue
    S.Fls -> eval t3  -- B-IfFalse
    _     -> S.If t1 t2 t3 
                                    

-- B-App & B-AppFix
eval (S.App t1 t2) = 
  if (S.isValue $ eval t1)
     then case eval t1 of
            S.Abs x tau t11 -> if ((S.isValue $ eval t2) && ((S.fv (S.Abs x tau t11)) == []))
                                  then (eval (S.subst x (eval t2) t11))
                                  else S.App t1 t2
            S.Fix t'        -> case eval (S.Fix t') of
                                 S.Abs x tau t11 -> if (S.isValue $ eval t2)
                                                       then eval (S.subst x (eval t2) t11)
                                                       else S.App t1 t2
            _               -> (S.App t1 t2)
     else S.App t1 t2

eval (S.Bop op t1 t2) =
 case eval t1 of
   S.IntConst v1 -> case eval t2 of
                      S.IntConst v2 -> case op of
                                         S.IntAdd -> S.IntConst (I.intAdd v1 v2)
                                         S.IntSub -> S.IntConst (I.intSub v1 v2)
                                         S.IntMul -> S.IntConst (I.intMul v1 v2)
                                         S.IntDiv -> S.IntConst (I.intDiv v1 v2)
                                         S.IntNand-> S.IntConst (I.intNand v1 v2)
                      _             -> S.Bop op t1 t2
   _             -> S.Bop op t1 t2

eval (S.Bpr p t1 t2) =
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> case p of
                                          S.IntEq -> case I.intEq v1 v2 of
                                                       True -> S.Tru
                                                       _    -> S.Fls
                                          S.IntLt -> case I.intLt v1 v2 of
                                                       True -> S.Tru
                                                       _    -> S.Fls
                       _             -> S.Bpr p t1 t2
    _             -> S.Bpr p t1 t2

{-
-- B-IntAdd
eval (S.IntAdd t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intAdd v1 v2)
                       _            -> S.IntAdd t1 t2
    _             -> S.IntAdd t1 t2


-- B-IntSub
eval (S.IntSub t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intSub v1 v2)
                       _             -> S.IntSub t1 t2
    _             -> S.IntSub t1 t2

-- B-IntMul
eval (S.IntMul t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intMul v1 v2)
                       _             -> S.IntSub t1 t2
    _             -> S.IntSub t1 t2

-- B-IntDiv
eval (S.IntDiv t1 t2) =
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intDiv v1 v2)
                       _             -> S.IntDiv t1 t2
    _             -> S.IntDiv t1 t2

-- B-IntNand
eval (S.IntNand t1 t2) =

  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intNand v1 v2)
                       _             -> S.IntNand t1 t2
    _             -> S.IntNand t1 t2

-- B-IntEq
eval (S.IntEq t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> case I.intEq v1 v2 of
                                          True  -> S.Tru
                                          False -> S.Fls
                       _             -> S.IntEq t1 t2
    _             -> S.IntEq t1 t2

-- B-IntLt
eval (S.IntLt t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> case I.intLt v1 v2 of
                                          True  -> S.Tru
                                          False -> S.Fls
                       _             -> S.IntLt t1 t2
    _             -> S.IntLt t1 t2

-}
-- B-Let
eval (S.Let x t1 t2) = 
  if (S.isValue (eval t1))
     then if (S.isValue (eval (S.subst x (eval t1) t2)))
             then eval (S.subst x (eval t1) t2)
             else S.Let x t1 t2
     else S.Let x t1 t2

-- B-FIX
eval (S.Fix t1) = 
  case eval t1 of
    S.Abs x tau1 t11 -> if (S.isValue (eval (S.subst x (S.Fix (S.Abs x tau1 t11)) t11)))
                           then eval (S.subst x (S.Fix (S.Abs x tau1 t11)) t11)
                           else S.Fix t1
    _                -> S.Fix t1
  

-- B-Value and Exceptions
eval t = t

\end{code}
