\begin{code}
module Typing where
import qualified AbstractSyntax as S
import List
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

--T-IntAdd 
typing capGamma (S.IntAdd t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t1 of 
                                          Just S.TypeInt -> Just S.TypeInt
                                          Nothing -> Nothing

                 _              -> Nothing
--T-IntSub 
typing capGamma (S.IntSub t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t1 of 
                                          Just S.TypeInt -> Just S.TypeInt
                                          Nothing -> Nothing

                 _              -> Nothing
--T-IntMul 
typing capGamma (S.IntMul t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t1 of 
                                          Just S.TypeInt -> Just S.TypeInt
                                          Nothing -> Nothing

                 _              -> Nothing
--T-IntMul
typing capGamma (S.IntDiv t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t1 of 
                                          Just S.TypeInt -> Just S.TypeInt
                                          Nothing -> Nothing
                 _              -> Nothing
--T-IntNand 
typing capGamma (S.IntNand t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t1 of 
                                          Just S.TypeInt -> Just S.TypeInt
                                          Nothing -> Nothing
                 _              -> Nothing

--T-IntEq
typing capGamma (S.IntEq t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t2 of 
                                          Just S.TypeInt -> Just S.TypeBool
                                          Nothing -> Nothing
                 _              -> Nothing

--T-IntLt
typing capGamma (S.IntLt t1 t2) =
        case typing capGamma t1 of 
                 Just S.TypeInt -> case typing capGamma t2 of 
                                          Just S.TypeInt -> Just S.TypeBool
                                          Nothing -> Nothing
                 _              -> Nothing

typeCheck :: S.Term -> S.Type
typeCheck t =
  case typing Empty t of
    Just tau -> tau
    _ -> error "type error"
\end{code}
