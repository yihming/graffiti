\begin{code}
module SCCMachine where

import qualified AbstractSyntax as S
import qualified EvaluationContext as E
import qualified IntegerArithmetic as I
import qualified CCMachine as CC


dealWithContext :: (S.Term,E.Context) -> Maybe (S.Term, E.Context)
dealWithContext (t,c) = case c of
  {-app-}
  E.AppT E.Hole t2 -> Just (t2, E.AppV t E.Hole)
  E.AppV (S.Abs x tau t') E.Hole -> Just ((S.subst x t t'),E.Hole)
  {-if-}
  E.If E.Hole t2 t3 -> case t of
                          S.Tru -> Just (t2, E.Hole)
                          S.Fls -> Just (t3, E.Hole)
  E.BopT op E.Hole t2 -> Just (t2, E.BopV op t E.Hole)
  E.BopV op t1 E.Hole -> case t1 of
                           S.IntConst n1 -> case t of
                                              S.IntConst n2 -> case op of
                                                                 S.IntAdd -> Just (S.IntConst (I.intAdd n1 n2), E.Hole)
                                                                 S.IntSub -> Just (S.IntConst (I.intSub n1 n2), E.Hole)
                                                                 S.IntMul -> Just (S.IntConst (I.intMul n1 n2), E.Hole)
                                                                 S.IntDiv -> Just (S.IntConst (I.intDiv n1 n2), E.Hole)
                                                                 S.IntNand-> Just (S.IntConst (I.intNand n1 n2), E.Hole)
                                              _             -> Nothing
                           _             -> Nothing
  {-add-}
  --E.IntAddT E.Hole t2 -> Just (t2, E.IntAddV t E.Hole)
  --E.IntAddV t1 E.Hole -> case t1 of
  --                        S.IntConst n1 -> case t of
  --                                              S.IntConst n2 -> Just(S.IntConst (I.intAdd n1 n2),E.Hole)
  --                                              _ -> Nothing
  --                        _ -> Nothing
  {-sub-}
  --E.IntSubT E.Hole t2 -> Just (t2, E.IntSubV t E.Hole)
  --E.IntSubV t1 E.Hole -> case t1 of
  --                        S.IntConst n1 -> case t of
  --                                              S.IntConst n2 -> Just(S.IntConst (I.intSub n1 n2),E.Hole) 
  --                                              _ -> Nothing
  --                        _ -> Nothing
  {-mul-}
  --E.IntMulT E.Hole t2 -> Just (t2, E.IntMulV t E.Hole)
  --E.IntMulV t1 E.Hole -> case t1 of
  --                         S.IntConst n1 -> case t of
  --                                               S.IntConst n2 -> Just(S.IntConst (I.intMul n1 n2),E.Hole)
  --                                               _ -> Nothing
  --                         _ -> Nothing
  {-div-}
  --E.IntDivT E.Hole t2 -> Just (t2, E.IntDivV t E.Hole)
  --E.IntDivV t1 E.Hole -> case t1 of
  --                         S.IntConst n1 -> case t of
  --                                               S.IntConst n2 -> Just(S.IntConst (I.intDiv n1 n2),E.Hole)
  --                                               _ -> Nothing
  --                         _ -> Nothing
  {-nand-}
  --E.IntNandT E.Hole t2 -> Just (t2, E.IntNandV t E.Hole)
  --E.IntNandV t1 E.Hole -> case t1 of
  --                          S.IntConst n1 -> case t of
  --                                                S.IntConst n2 -> Just(S.IntConst (I.intNand n1 n2),E.Hole)
  --                                                _ -> Nothing
  --                          _ -> Nothing
  E.BprT p E.Hole t2 -> Just (t2, E.BprV p t E.Hole)
  E.BprV p t1 E.Hole -> case t1 of
                          S.IntConst n1 -> case t of
                                             S.IntConst n2 -> case p of
                                                                S.IntEq -> case I.intEq n1 n2 of
                                                                             True -> Just (S.Tru, E.Hole)
                                                                             _    -> Just (S.Fls, E.Hole)
                                                                S.IntLt -> case I.intLt n1 n2 of
                                                                             True -> Just (S.Tru, E.Hole)
                                                                             _    -> Just (S.Fls, E.Hole)
                                             _             -> Nothing
                          _             -> Nothing
  {-eq-}
  --E.IntEqT E.Hole t2 -> Just (t2, E.IntEqV t E.Hole)
  --E.IntEqV t1 E.Hole -> case t1 of
  --                         S.IntConst n1 -> case t of
  --                                               S.IntConst n2 -> case I.intEq n1 n2 of
  --                                                                      True -> Just (S.Tru,E.Hole)
  --                                                                      False -> Just (S.Fls,E.Hole)
  --                                               _ -> Nothing
  --                         _ -> Nothing
  {-lt-}
  --E.IntLtT E.Hole t2 -> Just (t2, E.IntLtV t E.Hole)
  --E.IntLtV t1 E.Hole -> case t1 of
  --                         S.IntConst n1 -> case t of
  --                                               S.IntConst n2 -> case I.intLt n1 n2 of
  --                                                                      True -> Just (S.Tru,E.Hole)
  --                                                                      False -> Just (S.Fls,E.Hole)
  --                                               _ -> Nothing
  --                         _ -> Nothing

  {-let-}
  E.Let x E.Hole t2 -> Just (S.subst x t t2,E.Hole)
  {-fix-}
  E.Fix E.Hole -> case t of 
                        S.Abs x tau1 t2 -> Just (S.subst x (S.Fix (S.Abs x tau1 t2)) t2, E.Hole)
                        _ -> Nothing
  _ -> Nothing






sccMachineStep :: (S.Term, E.Context) -> Maybe (S.Term, E.Context)
sccMachineStep (t,c) = case S.isValue t of
  {-t is a term-}
  False -> case t of  
    S.App t1 t2 -> Just (t1, E.fillWithContext c (E.AppT E.Hole t2))
    S.If t1 t2 t3 -> Just (t1, E.fillWithContext c (E.If E.Hole t2 t3))
    S.Bop op t1 t2 -> Just (t1, E.fillWithContext c (E.BopT op E.Hole t2))
    S.Bpr p t1 t2  -> Just (t1, E.fillWithContext c (E.BprT p E.Hole t2))
    
    {-
    S.IntAdd t1 t2 -> Just (t1, E.fillWithContext c (E.IntAddT E.Hole t2))                                
    S.IntSub t1 t2 -> Just (t1, E.fillWithContext c (E.IntSubT E.Hole t2))
    S.IntMul t1 t2 -> Just (t1, E.fillWithContext c (E.IntMulT E.Hole t2))
    S.IntDiv t1 t2 -> Just (t1, E.fillWithContext c (E.IntDivT E.Hole t2))
    S.IntNand t1 t2 -> Just (t1, E.fillWithContext c (E.IntNandT E.Hole t2))
    S.IntEq t1 t2 -> Just (t1, E.fillWithContext c (E.IntEqT E.Hole t2))
    S.IntLt t1 t2 -> Just (t1,E.fillWithContext c (E.IntLtT E.Hole t2))
    -}
    
    S.Let x t1 t2 -> Just (t1, E.fillWithContext c (E.Let x E.Hole t2))
    S.Fix t -> Just (t, E.fillWithContext c (E.Fix E.Hole))
    _ -> Nothing
 {-t is a value-}
  True -> case dealWithContext (t,c) of
    Just (t',c') ->  Just (t',c')
    Nothing -> case c of
        {-app-}
        E.AppT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.AppT (E.fillWithContext cl cc) t2)
                        _             -> Nothing
                _             -> Nothing
        E.AppV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.AppV t1 (E.fillWithContext cl cc))
                        _             -> Nothing
                _ -> Nothing
        {-if-}
        E.If c1 t2 t3 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.If (E.fillWithContext cl cc) t2 t3)
                        _ -> Nothing
                _ -> Nothing
        
        E.BopT op c1 t2 -> case CC.lookupHole c1 of
               Just (c', cl) -> case dealWithContext (t, c') of
                       Just (t', cc) -> Just (t', E.BopT op (E.fillWithContext cl cc) t2)
                       _              -> Nothing
               _             -> Nothing
        E.BopV op t1 c2 -> case CC.lookupHole c2 of
             Just (c', cl) -> case dealWithContext (t, c') of
                  Just (t', cc) -> Just (t', E.BopV op t1 (E.fillWithContext cl cc))
                  _             -> Nothing
             _             -> Nothing  
        {-
        {-add-}
        E.IntAddT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntAddT (E.fillWithContext cl cc) t2) 
                        _ -> Nothing
                _ -> Nothing
        E.IntAddV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntAddV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        {-sub-}
        E.IntSubT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntSubT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntSubV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntSubV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        {-mul-}
        E.IntMulT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntMulT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntMulV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntMulV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        {-div-}
        E.IntDivT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntDivT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntDivV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntDivV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        {-nand-}
        E.IntNandT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntNandT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntNandV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntNandV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        -}
        
        E.BprT p c1 t2 -> case CC.lookupHole c1 of
             Just (c', cl) -> case dealWithContext (t, c') of
                   Just (t', cc) -> Just (t', E.BprT p (E.fillWithContext cl cc) t2)
                   _             -> Nothing
             _             -> Nothing
        E.BprV p t1 c2 -> case CC.lookupHole c2 of
             Just (c', cl) -> case dealWithContext (t, c') of
                      Just (t', cc) -> Just (t', E.BprV p t1 (E.fillWithContext cl cc))
                      _             -> Nothing
             _             -> Nothing
        
        {-
        {-eq-}
        E.IntEqT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntEqT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntEqV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntEqV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        {-lt-}
        E.IntLtT c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntLtT (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        E.IntLtV t1 c2 -> case CC.lookupHole c2 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.IntLtV t1 (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        -}
        
        {-let-}
        E.Let x c1 t2 -> case CC.lookupHole c1 of
                Just (c',cl) -> case dealWithContext (t,c') of
                        Just (t',cc) -> Just (t',E.Let x (E.fillWithContext cl cc) t2)
                        _ -> Nothing
                _ -> Nothing
        {-fix-}
        E.Fix c1 -> case CC.lookupHole c1 of
                Just (c',cl) ->case dealWithContext (t,c') of
                        Just (t',cc) ->  Just (t',E.Fix (E.fillWithContext cl cc))
                        _ -> Nothing
                _ -> Nothing
        _ -> Nothing



sccMachineEval :: S.Term -> S.Term
sccMachineEval t = 
  fst $ f (t,E.Hole)
  where
    f :: (S.Term, E.Context) -> (S.Term, E.Context)
    f (t,c) = 
      case sccMachineStep (t,c) of
        Just (t',c') -> f (t',c')
        Nothing -> (t,c)        



\end{code}
