\begin{code} 

module CESMachine where 
import Debug.Trace
import qualified IntegerArithmetic as I
import qualified DeBruijn as DB

data Inst = Int Integer
        | Bool Bool
        | Bop BOP
        | Bpr BPR
        | Access Int
        | Close Code
        | Let
        | EndLet
        | Apply
        | Return
        | If
        | Fix
        deriving Show

data BOP = Add | Sub | Mul | Div | Nand
instance Show BOP where 
          show Add = "+" 
          show Sub = "-" 
          show Mul = "*" 
          show Div = "/"
          show Nand = "^"

data BPR = Eq | Lt
instance Show BPR where
  show Eq = "="
  show Lt = "<"

type Code = [ Inst ]
data Value = BoolVal Bool | IntVal Integer | Clo Code Env
        deriving Show
type Env = [Value]
data Slot = Value Value | Code Code | Env Env
        deriving Show
type Stack = [Slot]
type State = (Code, Env, Stack)
compile :: DB.Term -> Code
compile t = case t of
        DB.Var n -> [Access n]
        DB.IntConst n -> [Int n]
        DB.Abs tp t0 -> case compile t0 of t1 -> [Close (t1 ++ [Return])]
        DB.App t1 t2 -> case compile t1 of 
                    t1' -> case compile t2 of 
                       t2' -> t1' ++ t2' ++ [Apply]
        DB.If t0 t1 t2 -> case compile t0 of 
                      t0' -> case compile t1 of 
                         t1' -> case compile t2 of 
                            t2' -> t0' ++ t1' ++ t2' ++ [If]
        DB.Tru -> [Bool True]
        DB.Fls -> [Bool False]
        DB.Fix t0 -> case compile t0 of t0' -> t0' ++ [Fix]
        DB.Let t1 t2 -> case compile t1 of 
                    t1' -> case compile t2 of 
                       t2' -> t1' ++ [Let] ++ t2' ++ [EndLet]

        DB.Bop bop t1 t2 -> case compile t1 of
                        t1' -> case compile t2 of
                           t2' -> case bop of
                              DB.Add -> t1' ++ t2' ++ [Bop Add] 
                              DB.Sub -> t1' ++ t2' ++ [Bop Sub] 
                              DB.Mul -> t1' ++ t2' ++ [Bop Mul] 
                              DB.Div -> t1' ++ t2' ++ [Bop Div] 
                              DB.Nand -> t1' ++ t2' ++ [Bop Nand] 

        DB.Bpr bpr t1 t2 -> case compile t1 of
                        t1' -> case compile t2 of
                           t2' -> case bpr of
                              DB.Eq -> t1' ++ t2' ++ [Bpr Eq]
                              DB.Lt -> t1' ++ t2' ++ [Bpr Lt]        
step :: State -> Maybe State
step state = case state of        
        (Access i : c, e, s) -> Just (c, e,Value (e !! i) : s)
        (If:c, e, s2:s1:(Value (BoolVal v0)):s) -> case v0 of
                                                        True -> Just(c, e, s1:s)
                                                        False -> Just(c, e, s2:s)
        (Close code':code, env, s) -> Just(code, env, Env [Clo code' env]:s)
        (Apply:code, env, (Value v):(Env [Clo code' env']):s) -> Just(code', v:env', (Code code):(Env env):s)
        (Apply:code, env, (Value v):(Value (Clo code' env')):s) -> Just(code', v:env', (Code code):(Env env):s)
        (Return:c, e, s':(Code c'):(Env e'):s) -> Just(c', e', s':s)
        (Int n:c, e, s) -> Just(c, e, (Value (IntVal n)):s)
        (Bool b:c, e, s) -> Just(c, e, (Value (BoolVal b)):s)
        ((Bop bop):c, e, (Value (IntVal v2)):(Value (IntVal v1)):s) -> case bop of
                                Add -> Just (c, e, (Value (IntVal (I.intAdd v1 v2))):s)
                                Sub -> Just (c, e, (Value (IntVal (I.intSub v1 v2))):s)
                                Mul -> Just (c, e, (Value (IntVal (I.intMul v1 v2))):s)
                                Div -> Just (c, e, (Value (IntVal (I.intDiv v1 v2))):s)
                                Nand-> Just (c, e, (Value (IntVal (I.intNand v1 v2))):s)
        ((Bpr bpr):c, e, (Value (IntVal v2)):(Value (IntVal v1)):s) -> case bpr of
                                Eq -> Just (c, e, (Value (BoolVal (I.intEq v1 v2))):s)
                                Lt -> Just (c, e, (Value (BoolVal (I.intLt v1 v2))):s)
        (Let:code, env, (Value v):s) -> Just(code, v:env, s) 
        (Let:code, env, (Env env'):s) -> Just(code, env'++env, s) 
        (EndLet:code, v:env, s) -> Just(code, env, s)
        (Fix:code, env, (Env [Clo code' env']):s) -> Just(code', (Clo [Close code', Fix] []):env, (Code code):(Env env):s)
        _ -> Nothing
loop :: State -> State
loop state =
        case step state of
                Just state'-> loop state'
                Nothing -> state

eval :: DB.Term -> Value
eval t = case loop (compile t, [], []) of
                (_,_,Value v:_) -> v
                _               ->  error "not a value"
\end{code}
