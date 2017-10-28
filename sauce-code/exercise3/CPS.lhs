
\begin{code}
module CPS where

import Data.Maybe

import qualified AbstractSyntax as S
import qualified Typing as T

toCPSType :: S.Type -> S.Type -> S.Type
toCPSType S.TypeBool _ = S.TypeBool
toCPSType S.TypeInt _ = S.TypeInt 
toCPSType (S.TypeArrow tau1 tau2) answerType =
  S.TypeArrow (toCPSType tau1 answerType) (S.TypeArrow (S.TypeArrow (toCPSType tau2 answerType) answerType) answerType)

toCPSWithContext :: S.Type -> S.Term -> T.Context -> S.Term
toCPSWithContext answerType t capGamma = 
  case t of
    S.IntConst n    -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (S.Var "kappa") t)
    S.Tru           -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) (S.App (S.Var "kappa") t)
    S.Fls           -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) (S.App (S.Var "kappa") t)
    S.Var x         -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (S.Var "kappa") t)
    S.App t1 t2     -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" (toCPSType (fromJust $ T.typing capGamma t1) answerType) (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" (toCPSType (fromJust $ T.typing capGamma t2) answerType) (S.App (S.App (S.Var "v1") (S.Var "v2")) (S.Var "kappa"))))))
    S.Abs x tau1 t1 -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (S.Var "kappa") (S.Abs x (toCPSType tau1 answerType) (toCPSWithContext answerType t1 (T.Bind capGamma x tau1))))
    S.If t1 t2 t3   -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v" (toCPSType (fromJust $ T.typing capGamma t1) answerType) (S.If (S.Var "v") (S.App (toCPSWithContext answerType t2 capGamma) (S.Var "kappa")) (S.App (toCPSWithContext answerType t3 capGamma) (S.Var "kappa")))))
    S.Let x t1 t2   -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v" (toCPSType (fromJust $ T.typing capGamma t1) answerType) (S.Let x (S.Var "v") (S.App (toCPSWithContext answerType t2 (T.Bind capGamma x (fromJust $ T.typing capGamma t1))) (S.Var "kappa")))))
    S.Fix t1        -> case t1 of
                         S.Abs x tau11 t11 -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" (toCPSType (fromJust $ T.typing capGamma t1) answerType) (S.App (toCPSWithContext answerType t11 (T.Bind capGamma x tau11)) (S.Abs "v2" (toCPSType (fromJust $ T.typing (T.Bind capGamma x tau11) t11) answerType) (S.App (S.Let x (S.Fix (S.Var "v1")) (S.Var "v2")) (S.Var "kappa"))))))
                         _                 -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" (toCPSType (fromJust $ T.typing capGamma t1) answerType) (S.App (S.Fix (S.Var "v1")) (S.Var "kappa"))))
    S.IntAdd t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntAdd (S.Var "v1") (S.Var "v2")) )))))
    S.IntSub t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntSub (S.Var "v1") (S.Var "v2")) )))))
    S.IntMul t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntMul (S.Var "v1") (S.Var "v2")) )))))
    S.IntDiv t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntDiv (S.Var "v1") (S.Var "v2")) )))))
    S.IntNand t1 t2 -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntNand (S.Var "v1") (S.Var "v2")) )))))
    S.IntEq t1 t2   -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntEq (S.Var "v1") (S.Var "v2")) )))))
    S.IntLt t1 t2   -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (toCPSWithContext answerType t1 capGamma) (S.Abs "v1" S.TypeInt (S.App (toCPSWithContext answerType t2 capGamma) (S.Abs "v2" S.TypeInt (S.App (S.Var "kappa") (S.IntLt (S.Var "v1") (S.Var "v2")) )))))
    
toCPS :: S.Type -> S.Term -> S.Term
toCPS answerType t = 
  toCPSWithContext answerType t T.Empty


\end{code}
