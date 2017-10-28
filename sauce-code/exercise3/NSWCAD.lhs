\begin{code}

module NSWCAD where

import Data.Maybe
import qualified DeBruijn as S
import qualified IntegerArithmetic as I
import Debug.Trace

data Value = BoolVal Bool | IntVal Integer | Clo S.Term Env
  deriving Show

type Env = [Value]

evalInEnv :: Env -> S.Term -> Maybe Value
evalInEnv e t = case t of
  -- true,false
	S.Tru -> Just (BoolVal True)
	S.Fls -> Just (BoolVal False)
  -- integer
	S.IntConst n -> Just (IntVal n)
  -- if
	S.If t1 t2 t3 -> case evalInEnv e t1 of
				Just (BoolVal True) -> case evalInEnv e t2 of
							Just a -> Just a
							_ -> error "if-t2"
				Just (BoolVal False) -> case evalInEnv e t3 of
							Just b -> Just b
							_ -> error "if-t3"
				_ -> error "if-t1"
  -- var
	S.Var i -> Just (e !! i)
  -- app
	S.App t1 t2 -> case evalInEnv e t1 of
		Just (Clo (S.Abs tau t') e') -> case evalInEnv e t2 of
						Just v' -> case evalInEnv ([v'] ++ e') t' of
								Just vv -> Just vv
								_ -> error "app-replacement"
						_ -> error "app-t2 is not a value"
		Just (Clo (S.Fix t') e') -> case evalInEnv e' (S.Fix t') of
						Just (Clo (S.Abs tau' tt) ee) -> case evalInEnv e t2 of
											Just v'-> case evalInEnv ([v']++ee) tt of
													Just vv -> Just vv
													_ -> Nothing
											_ -> Nothing
						_ -> Nothing
		_ -> error "app-t1 is not an abstraction" 			
  -- abs
	S.Abs tau t1 -> Just (Clo (S.Abs tau t1) e)
  -- add, sub,mul,div,nand
	S.Bop op t1 t2 -> case evalInEnv e t1 of
				Just (IntVal v1) -> case evalInEnv e t2 of
							Just (IntVal v2) -> case op of
									S.Add -> Just (IntVal (I.intAdd v1 v2))
									S.Sub -> Just (IntVal (I.intSub v1 v2))
									S.Mul -> Just (IntVal (I.intMul v1 v2))
									S.Div -> Just (IntVal (I.intDiv v1 v2))
									S.Nand -> Just (IntVal (I.intNand v1 v2))
									
							_ -> error "BOP t2 is not a value"
				_ -> error "BOP t1 is not a value"
  -- eq,lt
	S.Bpr pr t1 t2 -> case evalInEnv e t1 of
				Just (IntVal v1) -> case evalInEnv e t2 of
							Just (IntVal v2) -> case pr of
									S.Eq -> case I.intEq v1 v2 of
										  True -> Just (BoolVal True)
										  False -> Just (BoolVal False)
										  
									S.Lt -> case I.intLt v1 v2 of
										  True -> Just (BoolVal True)
										  False -> Just (BoolVal False)
										  
							_ -> error "BRP t2 is not a value"
				_ -> error "BRP t1 is not a value"
  -- let
	S.Let t1 t2 -> case evalInEnv e t1 of
				Just a -> case evalInEnv ([a] ++ e) t2 of
						Just b -> Just b
						_ -> error "let-t2 is not a value"
				_ -> error "let t1 is not a value"
  -- fix
	S.Fix t1 -> case evalInEnv e t1 of
			Just (Clo (S.Abs tau t') e') -> case evalInEnv ([Clo (S.Fix (S.Abs tau t')) e'] ++ e') t' of
								Just b -> Just b
								_ -> error "fix-point error"
			_ -> error "fix-t1 is not an abstraction"


eval :: S.Term -> Value
eval t = fromJust (evalInEnv [] t)


\end{code}
