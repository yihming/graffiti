\begin{code}
module CCMachine where

import qualified AbstractSyntax as S
import qualified EvaluationContext as E
import qualified IntegerArithmetic as I

lookupHole :: E.Context -> Maybe (E.Context, E.Context)

lookupHole (E.AppT c1 t) = case c1 of
                             E.Hole -> Just ((E.AppT c1 t), E.Hole)
                             _      -> case lookupHole c1 of
                                        Just (c2, c3) -> Just (c2, (E.AppT c3 t))
lookupHole (E.AppV v c1) = case c1 of 
                             E.Hole -> Just ((E.AppV v c1), E.Hole)
                             _      -> case lookupHole c1 of
                                        Just(c2, c3) -> Just (c2, (E.AppV v c3))

lookupHole (E.If c1 t1 t2) = case c1 of 
                               E.Hole -> Just((E.If c1 t1 t2), E.Hole)
                               _      -> case lookupHole c1 of
                                           Just(c2, c3) -> Just (c2, (E.If c3 t1 t2))

lookupHole (E.BopT op c1 t) = case c1 of
                                E.Hole -> Just ((E.BopT op c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just (c2, c3) -> Just (c2, (E.BopT op c3 t))
                                                              
lookupHole (E.BopV op t c1) = case c1 of
                                E.Hole -> Just ((E.BopV op t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just (c2, c3) -> Just (c2, (E.BopV op t c3))
{-
lookupHole (E.IntAddT c1 t) = case c1 of 
                                E.Hole -> Just((E.IntAddT c1 t), E.Hole)
                               _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntAddT c3 t))
lookupHole (E.IntAddV t c1) = case c1 of
                                E.Hole -> Just((E.IntAddV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                           Just(c2, c3) -> Just (c2, (E.IntAddV t c3))

lookupHole (E.IntSubT c1 t) = case c1 of
                                E.Hole -> Just((E.IntSubT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntSubT c3 t))
lookupHole (E.IntSubV t c1) = case c1 of
                                E.Hole -> Just((E.IntSubV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntSubV t c3))
                    
lookupHole (E.IntMulT c1 t) = case c1 of 
                                E.Hole -> Just((E.IntMulT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntMulT c3 t))

lookupHole (E.IntMulV t c1) = case c1 of
                                E.Hole -> Just((E.IntMulV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntMulV t c3))

lookupHole (E.IntDivT c1 t) = case c1 of
                                E.Hole -> Just((E.IntDivT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntDivT c3 t))
lookupHole (E.IntDivV t c1) = case c1 of
                                E.Hole -> Just((E.IntDivV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntDivV t c3))

lookupHole (E.IntNandT c1 t)= case c1 of
                                E.Hole -> Just((E.IntNandT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntNandT c3 t))
lookupHole (E.IntNandV t c1)= case c1 of 
                                E.Hole -> Just((E.IntNandV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntNandV t c3))
-}

lookupHole (E.BprT p c1 t) = 
  case c1 of
    E.Hole -> Just ((E.BprT p c1 t), E.Hole)
    _      -> case lookupHole c1 of
                Just (c2, c3) -> Just (c2, (E.BprT p c3 t))
                
lookupHole (E.BprV p t c1) =
  case c1 of
    E.Hole -> Just ((E.BprV p t c1), E.Hole)
    _      -> case lookupHole c1 of
                Just (c2, c3) -> Just (c2, (E.BprV p t c3))

{-
lookupHole (E.IntEqT c1 t)= case c1 of 
                              E.Hole -> Just((E.IntEqT c1 t), E.Hole)
                              _      -> case lookupHole c1 of
                                          Just(c2, c3) -> Just (c2, (E.IntEqT c3 t))
lookupHole (E.IntEqV t c1)= case c1 of
                              E.Hole -> Just((E.IntEqV t c1), E.Hole)
                              _   -> case lookupHole c1 of
                                        Just(c2, c3) -> Just (c2, (E.IntEqV t c3))

lookupHole (E.IntLtT c1 t)= case c1 of
                              E.Hole -> Just((E.IntLtT c1 t), E.Hole)
                              _             -> case lookupHole c1 of
                                          Just(c2, c3) -> Just (c2, (E.IntLtT c3 t))
lookupHole (E.IntLtV t c1)= case c1 of
                              E.Hole -> Just((E.IntLtV t c1), E.Hole)
                              _      -> case lookupHole c1 of
                                          Just(c2, c3) -> Just (c2, (E.IntLtV t c3))
-}

lookupHole (E.Let x c1 t1)= case c1 of
                              E.Hole -> Just((E.Let x c1 t1), E.Hole)
                              _      -> case lookupHole c1 of
                                          Just(c2, c3) -> Just (c2, (E.Let x c3 t1))
                 
lookupHole (E.Fix c1) = case c1 of 
                          E.Hole -> Just((E.Fix c1), E.Hole)
                          _      -> case lookupHole c1 of
                                      Just(c2, c3) -> Just (c2, (E.Fix c3))

lookupHole c1  =  Nothing

ccMachineStep :: (S.Term, E.Context) -> Maybe (S.Term, E.Context)
ccMachineStep (t, c) = case t of
  S.App t1 t2
    | not (S.isValue t1)                      ->   Just (t1, E.fillWithContext c (E.AppT E.Hole t2))       {-cc1-}
    | S.isValue t1 && not (S.isValue t2)      ->   Just (t2, E.fillWithContext c (E.AppV t1 E.Hole))       {-cc2-}
  S.App (S.Abs x _ t12) t2                    ->   Just (S.subst x t2 t12, c)                              {-cc$\beta$-}
  
  S.Bop op t1 t2
    | not(S.isValue t1)                       -> Just (t1, E.fillWithContext c (E.BopT op E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)      -> Just (t2, E.fillWithContext c (E.BopV op t1 E.Hole))
    | otherwise                               -> case t1 of
                                                   S.IntConst n1 -> case t2 of
                                                                      S.IntConst n2 -> case op of
                                                                                         S.IntAdd -> Just (S.IntConst (I.intAdd n1 n2), c)
                                                                                         S.IntSub -> Just (S.IntConst (I.intSub n1 n2), c)
                                                                                         S.IntMul -> Just (S.IntConst (I.intMul n1 n2), c)
                                                                                         S.IntDiv -> Just (S.IntConst (I.intDiv n1 n2), c)
                                                                                         S.IntNand-> Just (S.IntConst (I.intNand n1 n2), c)
 {- 
  S.IntAdd t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntAddT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntAddV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> Just (S.IntConst (I.intAdd n1 n2), c)
                                         -}
                                         
     {-cc$\delta$-}
  S.If (S.Tru) t2 t3                                -> Just (t2, c)
  S.If (S.Fls) t2 t3                                -> Just (t3, c)
  S.If t1 t2 t3
    | not (S.isValue t1)                        -> Just (t1, E.fillWithContext c (E.If E.Hole t2 t3))

{-
  S.IntSub t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntSubT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntSubV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> Just (S.IntConst (I.intSub n1 n2), c)
     {-cc$\delta$-}

  S.IntMul t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntMulT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntMulV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> Just (S.IntConst (I.intMul n1 n2), c)
     {-cc$\delta$-}

  S.IntDiv t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntDivT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntDivV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> Just (S.IntConst (I.intDiv n1 n2), c)
     {-cc$\delta$-}

  S.IntNand t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntNandT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntNandV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> Just (S.IntConst (I.intNand n1 n2), c)
     {-cc$\delta$-}
-}

  S.Bpr p t1 t2                                 
    | not(S.isValue t1)                         -> Just (t1, E.fillWithContext c (E.BprT p E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.BprV p t1 E.Hole))
    {-cc3-}
    | otherwise    -> case t1 of
                        S.IntConst n1 -> case t2 of
                                           S.IntConst n2 -> case p of
                                                              S.IntEq -> case I.intEq n1 n2 of
                                                                           True -> Just (S.Tru, c)
                                                                           _    -> Just (S.Fls, c)
                                                              S.IntLt -> case I.intLt n1 n2 of
                                                                           True -> Just (S.Tru, c)
                                                                           _    -> Just (S.Fls, c)
    {-cc$\delta$-}

{-
  S.IntEq t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntEqT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntEqV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 ->  case I.intEq n1 n2 of
                                                                True -> Just(S.Tru, c)
                                                                _    -> Just(S.Fls, c)
     {-cc$\delta$-}
  
  S.IntLt t1 t2
    | not(S.isValue t1)                                -> Just (t1, E.fillWithContext c (E.IntLtT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)        -> Just (t2, E.fillWithContext c (E.IntLtV t1 E.Hole))
     {-cc3-}
    | otherwise        ->  case t1 of
                      S.IntConst n1 -> case t2 of 
                                         S.IntConst n2 -> case I.intLt n1 n2 of
                                                                True -> Just(S.Tru, c)
                                                                _    -> Just(S.Fls, c)
     {-cc$\delta$-}
-}

  S.Let x t1 t2
    | S.isValue t1                                -> Just (S.subst x t1 t2,  c)
    | otherwise                                        -> Just (t1, E.fillWithContext c (E.Let x E.Hole t2))
   
  S.Fix t
    | not (S.isValue t)                                -> Just (t, E.fillWithContext c (E.Fix E.Hole))
    | otherwise                -> case t of 
                                S.Abs x tau1 t2 -> Just (S.subst x (S.Fix (S.Abs x tau1 t2)) t2, c)
                                
  
  _  -> case lookupHole c of 
          Just (c2, c3) -> Just (E.fillWithTerm c2 t, c3)
          Nothing       -> Nothing

ccMachineEval :: S.Term -> S.Term
ccMachineEval t = 
  fst $ f (t, E.Hole)
  where
    f :: (S.Term, E.Context) -> (S.Term, E.Context)
    f (t, c) = 
      case ccMachineStep (t, c) of
        Just (t', c') -> f (t', c')
        Nothing       -> (t, c)
\end{code}
