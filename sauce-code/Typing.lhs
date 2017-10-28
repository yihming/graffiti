\begin{code}
module Typing where
import qualified AbstractSyntax as S
import Data.List
data Context  =  Empty
              |  Bind Context S.Var S.Type
              deriving Eq
instance Show Context where
  show Empty                  =  "<>"
  show (Bind capGamma x tau)  =  show capGamma ++ "," ++ x ++ ":" ++ show tau

contextLookup :: S.Var -> Context -> Maybe S.Type
contextLookup x Empty  =  Nothing
contextLookup x (Bind capGamma y tau)
  | x == y      =  Just tau
  | otherwise   =  contextLookup x capGamma

typing :: Context -> S.Term -> Maybe S.Type
--T-Var
typing capGamma (S.Var x) = contextLookup x capGamma
--T-Abs
typing capGamma (S.Abs x tau_1 t2) = case typing (Bind capGamma x tau_1) t2 of 
                         Just(tp0) -> Just (S.TypeArrow tau_1 tp0)
                         Nothing -> Nothing
--T-App
typing capGamma (S.App t0 t2)=
        case typing capGamma t0 of 
                 Just (S.TypeArrow tp tp0) -> case typing capGamma t2 of 
                                                Just tp' -> if tp == tp' 
                                                               then Just tp0
                                                               else Nothing
                                                Nothing -> Nothing
                 _                         -> Nothing
--T-LetPoly
typing capGamma (S.Let x t0 t2) =
        case typing capGamma (S.subst x t0 t2) of 
                Just tp0 -> case typing capGamma t0 of
                                    Just tp1 -> Just tp0
                                    _             -> Nothing
                _        -> Nothing

--T-Fix
typing capGamma (S.Fix t) =
        case typing capGamma t of
                Just (S.TypeArrow tp0 tp2) -> if tp0 == tp2 
                                                  then Just tp0
                                                  else Nothing
                _                        -> Nothing

--T-True
typing capGamma S.Tru = Just S.TypeBool

--T-False
typing capGamma S.Fls = Just S.TypeBool

--T-If
typing capGamma (S.If t0 t2 t3) 
        | (typing capGamma t2 == typing capGamma t3 && typing capGamma t0 == Just S.TypeBool) = typing capGamma t2
        | otherwise = Nothing

typing capGamma (S.IntConst _) = Just S.TypeInt

-- T-IntBinaryOperation
typing capGamma (S.Bop op t1 t2) = 
  case typing capGamma t1 of
    Just S.TypeInt -> case typing capGamma t2 of
                        Just S.TypeInt -> Just S.TypeInt
                        _              -> Nothing
    _              -> Nothing

-- T-IntBinaryPredicate
typing capGamma (S.Bpr p t1 t2) =
  case typing capGamma t1 of
    Just S.TypeInt -> case typing capGamma t2 of
                        Just S.TypeInt -> Just S.TypeBool
                        _              -> Nothing
    _              -> Nothing

typeCheck :: S.Term -> S.Type
typeCheck t =
  case typing Empty t of
    Just tau -> tau
    _ -> error "type error"
\end{code}
