\begin{code}
module CKMachine where

import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I

data Cont  =  MachineTerminate
           |  Fun              S.Term Cont -- where Term is a value
           |  Arg              S.Term Cont
           |  If               S.Term S.Term Cont
           |  Bop1     S.BOP   S.Term Cont
           |  Bop2     S.BOP   S.Term Cont
           |  Bpr1     S.BPR   S.Term Cont
           |  Bpr2     S.BPR   S.Term Cont
           {-
           |  IntAdd1          S.Term Cont  -- where Term is a value  
           |  IntAdd2          S.Term Cont
           |  IntSub1          S.Term Cont
           |  IntSub2          S.Term Cont
           |  IntMul1          S.Term Cont
           |  IntMul2          S.Term Cont
           |  IntDiv1          S.Term Cont
           |  IntDiv2          S.Term Cont
           |  IntNand1         S.Term Cont
           |  IntNand2         S.Term Cont
           |  IntEq1           S.Term Cont
           |  IntEq2           S.Term Cont
           |  IntLt1           S.Term Cont
           |  IntLt2           S.Term Cont
           -}
           |  Let              S.Var S.Term Cont
           |  Fix              Cont

ckMachineStep :: (S.Term, Cont) -> Maybe (S.Term, Cont)
ckMachineStep (t,c) = 
  case t of
    S.App t1 t2 -> Just (t1, (Arg t2 c))
    S.Bop op t1 t2 -> Just (t1, (Bop2 op t2 c))
    {-
    S.IntAdd t1 t2 -> Just (t1, (IntAdd2 t2 c))
    S.IntSub t1 t2 -> Just (t1, (IntSub2 t2 c))
    S.IntMul t1 t2 -> Just (t1, (IntMul2 t2 c))
    S.IntDiv t1 t2 -> Just (t1, (IntDiv2 t2 c))
    S.IntNand t1 t2-> Just (t1, (IntNand2 t2 c))
    -}
    S.Bpr p t1 t2  -> Just (t1, (Bpr2 p t2 c))
    {-
    S.IntEq t1 t2  -> Just (t1, (IntEq2 t2 c))
    S.IntLt t1 t2  -> Just (t1, (IntLt2 t2 c))
    -}
    S.If t1 t2 t3  -> Just (t1, (If t2 t3 c))
    S.Tru          -> case c of
                        If t2 t3 c' -> Just (t2, c')
                        _           -> Nothing
    S.Fls          -> case c of
                        If t2 t3 c' -> Just (t3, c')
                        _           -> Nothing
    S.Let x t1 t2  -> Just (t1, Let x t2 c)
    S.Fix (S.Abs x tau11 t11) -> Just (S.subst x (S.Fix (S.Abs x tau11 t11)) t11, c) 
    S.Fix t'       -> Just (t', Fix c)
    _
      | S.isValue t -> case c of
                         Fun (S.Abs x tau11 t11) c' -> Just (S.subst x t t11, c')
                         Arg t2 c'                  -> Just (t2, Fun t c')
                         Bop1 op (S.IntConst n1) c' -> case t of
                                                         S.IntConst n2 -> case op of
                                                                            S.IntAdd -> Just (S.IntConst (I.intAdd n1 n2), c')
                                                                            S.IntSub -> Just (S.IntConst (I.intSub n1 n2), c')
                                                                            S.IntMul -> Just (S.IntConst (I.intMul n1 n2), c')
                                                                            S.IntDiv -> Just (S.IntConst (I.intDiv n1 n2), c')
                                                                            S.IntNand-> Just (S.IntConst (I.intNand n1 n2), c')
                                                         _             -> Nothing
                         Bop2 op t2 c'              -> Just (t2, Bop1 op t c')
                         {-
                         IntAdd1 (S.IntConst n1) c' -> case t of
                                                         S.IntConst n2 -> Just (S.IntConst (I.intAdd n1 n2), c')
                                                         _             -> Nothing
                         IntAdd2 t2 c'              -> Just (t2, IntAdd1 t c')
                         IntSub1 (S.IntConst n1) c' -> case t of
                                                         S.IntConst n2 -> Just (S.IntConst (I.intSub n1 n2), c')
                                                         _             -> Nothing
                         IntSub2 t2 c'              -> Just (t2, IntSub1 t c')
                         IntMul1 (S.IntConst n1) c' -> case t of
                                                         S.IntConst n2 -> Just (S.IntConst (I.intMul n1 n2), c')
                                                         _             -> Nothing
                         IntMul2 t2 c'              -> Just (t2, IntMul1 t c')
                         IntDiv1 (S.IntConst n1) c' -> case t of
                                                         S.IntConst n2 -> Just (S.IntConst (I.intDiv n1 n2), c')
                                                         _             -> Nothing
                         IntDiv2 t2 c'              -> Just (t2, IntDiv1 t c')
                         IntNand1 (S.IntConst n1) c'-> case t of
                                                         S.IntConst n2 -> Just (S.IntConst (I.intNand n1 n2), c')
                                                         _             -> Nothing
                         IntNand2 t2 c'             -> Just (t2, IntNand1 t c')
                         -}
                         Bpr1 p (S.IntConst n1) c'  -> case t of
                                                         S.IntConst n2 -> case p of
                                                                            S.IntEq -> case I.intEq n1 n2 of
                                                                                         True -> Just (S.Tru, c')
                                                                                         _    -> Just (S.Fls, c')
                         Bpr2 p t2 c'               -> Just (t2, Bpr1 p t c')
                         {-
                         IntEq1 (S.IntConst n1) c'  -> case t of
                                                         S.IntConst n2 -> case I.intEq n1 n2 of
                                                                            True -> Just (S.Tru, c')
                                                                            _    -> Just (S.Fls, c')
                                                         _             -> Nothing
                         IntEq2 t2 c'               -> Just (t2, IntEq1 t c')
                         IntLt1 (S.IntConst n1) c'  -> case t of
                                                         S.IntConst n2 -> case I.intLt n1 n2 of
                                                                            True -> Just (S.Tru, c')
                                                                            _    -> Just (S.Fls, c')
                                                         _             -> Nothing
                         IntLt2 t2 c'               -> Just (t2, IntLt1 t c')
                         -}
                         Let x t2 c'                -> Just (S.subst x t t2, c')
                         Fix c'                     -> Just (S.Fix t, c')
                         _                          -> Nothing
      | otherwise   -> Nothing

ckMachineEval :: S.Term -> S.Term
ckMachineEval t =
  case snd (f (t, MachineTerminate)) of
    MachineTerminate -> fst $ f (t, MachineTerminate)
    _                -> error "CK Machine comes to an error" 
  where
    f :: (S.Term, Cont) -> (S.Term, Cont)
    f (t, c) = 
      case ckMachineStep (t, c) of
        Just (t', c') -> f (t', c')
        Nothing       -> (t, c)

\end{code}
