\begin{code}
module CEKMachine where

import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I


newtype Environment = Env [(S.Var, Closure)]
                      deriving Show

type Closure = (S.Term, Environment)

emptyEnv :: Environment
emptyEnv = Env []

isEmptyEnv :: Environment -> Bool
isEmptyEnv (Env es) = 
  case es of
    [] -> True
    _  -> False

lookupEnv :: Environment -> S.Var -> Closure
lookupEnv (e@(Env [])) x  =  error ("variable " ++ x ++ " not bound in environment " ++ show e)
lookupEnv (Env ((v,c):t)) x
  | x == v     =  c
  | otherwise  =  lookupEnv (Env t) x

updateEnv :: Environment -> S.Var -> Closure -> Environment
updateEnv (Env es) x clo = 
  Env ((x, clo):(filter (\a -> (fst a) /= x) es))

data Cont  =  MachineTerminate
           |  Fun                Closure Cont -- where Closure is a value
           |  Arg                Closure Cont
           |  If                 Closure Closure Cont -- lazy
           |  Bop1      S.BOP    Closure Cont
           |  Bop2      S.BOP    Closure Cont
           |  Bpr1      S.BPR    Closure Cont
           |  Bpr2      S.BPR    Closure Cont
           {-
           |  IntAdd1            Closure Cont
           |  IntAdd2            Closure Cont
           |  IntSub1            Closure Cont
           |  IntSub2            Closure Cont
           |  IntMul1            Closure Cont
           |  IntMul2            Closure Cont
           |  IntDiv1            Closure Cont
           |  IntDiv2            Closure Cont
           |  IntNand1           Closure Cont
           |  IntNand2           Closure Cont
           |  IntEq1             Closure Cont
           |  IntEq2             Closure Cont
           |  IntLt1             Closure Cont
           |  IntLt2             Closure Cont
           -}
           |  Let                S.Var Closure Cont
           |  Fix                Cont

finalSubst :: S.Term -> Environment -> S.Term
finalSubst t (e@(Env [])) = t
finalSubst t (Env ((v, (t', e')):es)) = 
  if v `elem` (S.fv t) then
                         finalSubst (S.subst v (finalSubst t' e') t) (Env es)
                       else
                         finalSubst t (Env es)
--finalSubst t (Env ((v, c):es)) =
--  if v `elem` (S.fv t) then
--                       finalSubst (S.subst v (fst c) t) (Env es)
--                     else
--                       finalSubst t (Env es) 
 
cekMachineStep :: (Closure, Cont) -> Maybe (Closure, Cont)
cekMachineStep ((t, e), c) = 
  case t of
    S.App t1 t2    -> Just ((t1, e), Arg (t2, e) c)
    S.If t1 t2 t3  -> Just ((t1, e), If (t2, e) (t3, e) c)
    S.Bop op t1 t2 -> Just ((t1, e), Bop2 op (t2, e) c)
    S.Bpr p t1 t2  -> Just ((t1, e), Bpr2 p (t2, e) c)
    {-
    S.IntAdd t1 t2 -> Just ((t1, e), IntAdd2 (t2, e) c)
    S.IntSub t1 t2 -> Just ((t1, e), IntSub2 (t2, e) c)
    S.IntMul t1 t2 -> Just ((t1, e), IntMul2 (t2, e) c)
    S.IntDiv t1 t2 -> Just ((t1, e), IntDiv2 (t2, e) c)
    S.IntNand t1 t2-> Just ((t1, e), IntNand2 (t2, e) c)
    S.IntEq t1 t2  -> Just ((t1, e), IntEq2 (t2, e) c)
    S.IntLt t1 t2  -> Just ((t1, e), IntLt2 (t2, e) c)
    -}
    S.Var x        -> Just (lookupEnv e x, c)
    S.Tru          -> case c of
                        If (t2, e') (t3, _) c' -> Just ((t2, e'), c')   -- may have problem if e doesn't merge with e'
                        _                      -> Nothing 
    S.Fls          -> case c of
                        If (t2, _) (t3, e') c' -> Just ((t3, e'), c')   -- same problem
                        _                      -> Nothing
    S.Let x t1 t2  -> Just ((t1, e), Let x (t2, e) c)
    S.Fix (S.Abs x tau11 t11) -> Just ((t11, updateEnv e x (S.Fix (S.Abs x tau11 t11), e)), c)
    S.Fix t1       -> Just ((t1, e), Fix c)    
    _
      | S.isValue t -> case c of
                         Fun (S.Abs x tau11 t11, e') c' -> Just ((t11, updateEnv e' x (t, e)), c')
                         Arg (t2, e') c'                -> Just ((t2, e'), Fun (t, e) c')
                         Bop1 op (S.IntConst n1, e') c' -> case t of
                                                             S.IntConst n2 -> case op of
                                                                                S.IntAdd -> Just ((S.IntConst (I.intAdd n1 n2), Env []), c')
                                                                                S.IntSub -> Just ((S.IntConst (I.intSub n1 n2), Env []), c')
                                                                                S.IntMul -> Just ((S.IntConst (I.intMul n1 n2), Env []), c')
                                                                                S.IntDiv -> Just ((S.IntConst (I.intDiv n1 n2), Env []), c')
                                                                                S.IntNand-> Just ((S.IntConst (I.intNand n1 n2), Env []), c')
                                                             _             -> Nothing
                         
                         Bop2 op (t2, e') c'           -> Just ((t2, e'), Bop1 op (t, e) c')
                                                             
                         Bpr1 p (S.IntConst n1, e') c' -> case t of
                                                            S.IntConst n2 -> case p of
                                                                               S.IntEq -> case I.intEq n1 n2 of
                                                                                            True -> Just ((S.Tru, Env []), c')
                                                                                            _    -> Just ((S.Fls, Env []), c')
                                                                               S.IntLt -> case I.intLt n1 n2 of
                                                                                            True -> Just ((S.Tru, Env []), c')
                                                                                            _    -> Just ((S.Fls, Env []), c')
                                                            _             -> Nothing
                   
                         Bpr2 p (t2, e') c'             -> Just ((t2, e'), Bpr1 p (t, e) c')
                         {-
                         IntAdd1 (S.IntConst n1, e') c' -> case t of
                                                             S.IntConst n2 -> Just ((S.IntConst (I.intAdd n1 n2), Env []), c')
                                                             _             -> Nothing
                         IntAdd2 (t2, e') c'            -> Just ((t2, e'), IntAdd1 (t, e) c')
                         IntSub1 (S.IntConst n1, e') c' -> case t of
                                                             S.IntConst n2 -> Just ((S.IntConst (I.intSub n1 n2), Env []), c')
                                                             _             -> Nothing
                         IntSub2 (t2, e') c'            -> Just ((t2, e'), IntSub1 (t, e) c')
                         IntMul1 (S.IntConst n1, e') c' -> case t of
                                                             S.IntConst n2 -> Just ((S.IntConst (I.intMul n1 n2), Env []), c')
                                                             _             -> Nothing
                         IntMul2 (t2, e') c'            -> Just ((t2, e'), IntMul1 (t, e) c')
                         IntDiv1 (S.IntConst n1, e') c' -> case t of
                                                             S.IntConst n2 -> Just ((S.IntConst (I.intDiv n1 n2), Env []), c')
                                                             _             -> Nothing
                         IntDiv2 (t2, e') c'            -> Just ((t2, e'), IntDiv1 (t, e) c')
                         IntNand1 (S.IntConst n1, e') c'-> case t of
                                                             S.IntConst n2 -> Just ((S.IntConst (I.intNand n1 n2), Env []), c')
                                                             _             -> Nothing
                         IntNand2 (t2, e') c'           -> Just ((t2, e'), IntNand1 (t, e) c')
                         IntEq1 (S.IntConst n1, e') c'  -> case t of
                                                             S.IntConst n2 -> case I.intEq n1 n2 of
                                                                                True -> Just ((S.Tru, Env []), c')
                                                                                _    -> Just ((S.Fls, Env []), c')
                                                             _             -> Nothing
                         IntEq2 (t2, e') c'             -> Just ((t2, e'), IntEq1 (t, e) c')
                         IntLt1 (S.IntConst n1, e') c'  -> case t of
                                                             S.IntConst n2 -> case I.intLt n1 n2 of
                                                                                True -> Just ((S.Tru, Env []), c')
                                                                                _    -> Just ((S.Fls, Env []), c')
                                                             _             -> Nothing
                         IntLt2 (t2, e') c'             -> Just ((t2, e'), IntLt1 (t, e) c')
                         -}
                         Let x (t2, e') c'              -> Just ((t2, updateEnv e' x (t, e)), c')
                         Fix c'                         -> Just ((S.Fix t, e), c')
                         MachineTerminate               -> case t of
                                                             S.Abs x tau1 t1 -> if isEmptyEnv e
                                                                                   then Nothing
                                                                                   else Just ((finalSubst t e, Env []), c)
                                                             _               -> Nothing 
      | otherwise -> Nothing

cekMachineEval :: S.Term -> S.Term
cekMachineEval t = 
  case snd (f ((t, emptyEnv), MachineTerminate)) of
    MachineTerminate -> fst $ fst $ f ((t, emptyEnv), MachineTerminate)
    _                -> error "CEK Machine comes to an error"
  where
    f :: (Closure, Cont) -> (Closure, Cont)
    f ((t, e), c) = 
      case cekMachineStep ((t, e), c) of
        Just ((t', e'), c') -> f ((t', e'), c')
        Nothing             -> ((t, e), c)

\end{code}
