\begin{code}

module StructuralOperationalSemantics where
import Data.List
import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I

eval1 :: S.Term -> Maybe S.Term
-- E-IFTRUE
eval1 (S.If S.Tru t2 t3) = Just t2

-- E-IFFALSE
eval1 (S.If S.Fls t2 t3) = Just t3

-- E-IF
eval1 (S.If t1 t2 t3) = 
  case eval1 t1 of
    Just t1' -> Just (S.If t1' t2 t3)
    Nothing  -> Nothing

-- E-APPABS, E-APP1 and E-APP2
eval1 (S.App t1 t2) = 
  if S.isValue t1
     then if S.isValue t2
             then case t1 of
                    S.Abs x tau11 t12 -> Just (S.subst x t2 t12) -- E-APPABS
                    _                 -> Nothing
             else case eval1 t2 of
                    Just t2' -> Just (S.App t1 t2')    -- E-APP2
                    Nothing  -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.App t1' t2)   -- E-APP1
            Nothing  -> Nothing

eval1 (S.Bop op t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                                then case t2 of
                                       S.IntConst n2 -> case op of
                                                          S.IntAdd -> Just (S.IntConst (I.intAdd n1 n2))
                                                          S.IntSub -> Just (S.IntConst (I.intSub n1 n2))
                                                          S.IntMul -> Just (S.IntConst (I.intMul n1 n2))
                                                          S.IntDiv -> Just (S.IntConst (I.intDiv n1 n2))
                                                          S.IntNand-> Just (S.IntConst (I.intNand n1 n2))
                                       _             -> Nothing
                                else case eval1 t2 of
                                       Just t2' -> Just (S.Bop op t1 t2')
                                       _        -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.Bop op t1' t2)
            _        -> Nothing

eval1 (S.Bpr p t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                                then case t2 of
                                       S.IntConst n2 -> case p of
                                                          S.IntEq -> case I.intEq n1 n2 of
                                                                       True -> Just S.Tru
                                                                       _    -> Just S.Fls
                                                          S.IntLt -> case I.intLt n1 n2 of
                                                                       True -> Just S.Tru
                                                                       _    -> Just S.Fls
                                       _             -> Nothing
                                else case eval1 t2 of
                                       Just t2' -> Just (S.Bpr p t1 t2')
                                       _        -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.Bpr p t1' t2)
            _        -> Nothing

-- E-FIXBETA
eval1 (S.Fix (S.Abs x tau1 t2)) = 
  Just (S.subst x (S.Fix (S.Abs x tau1 t2)) t2)

-- E-FIX
eval1 (S.Fix t1) = 
  case eval1 t1 of
    Just t1' -> Just (S.Fix t1')
    Nothing  -> Nothing

-- E-LETV and E-LET
eval1 (S.Let x t1 t2) = 
  if (S.isValue t1)
     then Just (S.subst x t1 t2)   -- E-LETV
     else case eval1 t1 of
            Just t1' -> Just (S.Let x t1' t2)  -- E-LET
            Nothing  -> Nothing

-- All other cases
eval1 _ = Nothing

eval :: S.Term -> S.Term
eval t = 
  case eval1 t of
    Just t' -> eval t'
    Nothing -> t 

\end{code}
