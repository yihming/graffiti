\begin{code}
module CE3RMachine where

import qualified AbstractSyntax as S
import qualified DeBruijn as DB
import qualified CPS as CPS


--instructions
data Inst = ACCESS1 Int
          | ACCESS2 Int
          | ACCESS3 Int
          | CONST1  Integer
          | CONST2  Integer
          | CONST3  Integer
          | CLOSE1  Code
          | CLOSE2  Code
          | CLOSE3  Code
          | TAILAPPLY1
          | TAILAPPLY2
          deriving Show


--define the nameless body and atom
type Type = S.Type

--code,environment,values,regs, state
type Code = [Inst]

type Env = [Value]

data Value = BoolVal Bool 
           | IntVal Integer 
           | Clo Code Env
           | UNCARE
           deriving Show

type Regs = (Value, Value, Value)

type State = (Code, Env, Regs)


--compile the nameless body to the machine code

bcompile :: DB.Term -> Code
bcompile t =
  case t of
    DB.App (DB.App t1 t2) t3 -> [acompile 1 t1] ++ [acompile 2 t2] ++ [acompile 3 t3] ++ [TAILAPPLY2]
    DB.App t1 t2             -> [acompile 1 t1] ++ [acompile 2 t2] ++ [TAILAPPLY1]
    DB.Var i                 -> [acompile 1 t]
    DB.IntConst n            -> [acompile 1 t]
    _                        -> error "Unsupported term"

-- compile the nameless axiom to the machine code instructions
acompile :: Int -> DB.Term -> Inst
acompile j t = 
  case t of
    DB.Var i       -> case j of
                        1 -> ACCESS1 i
                        2 -> ACCESS2 i
                        3 -> ACCESS3 i
                        _ -> error "Code Generating Error"
    DB.IntConst n  -> case j of
                        1 -> CONST1 n
                        2 -> CONST2 n
                        3 -> CONST3 n
                        _ -> error "Code Generating Error"
    DB.Abs tau1 (DB.Abs tau2 t2) -> case j of
                                      1 -> CLOSE1 (bcompile t2)
                                      2 -> CLOSE2 (bcompile t2)
                                      3 -> CLOSE3 (bcompile t2)
                                      _ -> error "Code Generating Error"
    DB.Abs tau1 t1 -> case j of
                        1 -> CLOSE1 (bcompile t1)
                        2 -> CLOSE2 (bcompile t1)
                        3 -> CLOSE3 (bcompile t1)
                        _ -> error "Code Generating Error"
    _              -> error "Unsupported term"
    



--transition rules
ce3rMachineStep :: State -> Maybe State
ce3rMachineStep ((CONST1 n):c, e,(UNCARE,v2,v3)) = Just (c,e,(IntVal n,v2,v3))
ce3rMachineStep ((CONST2 n):c, e,(v1,UNCARE,v3)) = Just (c,e,(v1,IntVal n,v3))
ce3rMachineStep ((CONST3 n):c, e,(v1,v2,UNCARE)) = Just (c,e,(v1,v2,IntVal n))
ce3rMachineStep ((ACCESS1 i):c, e, (UNCARE,v2,v3)) = Just (c,e,(e !! i,v2,v3))
ce3rMachineStep ((ACCESS2 i):c, e, (v1,UNCARE,v3)) = Just (c,e,(v1,e !! i,v3))
ce3rMachineStep ((ACCESS3 i):c, e, (v1,v2,UNCARE)) = Just (c,e,(v1,v2,e !! i))
ce3rMachineStep ((CLOSE1 c'):c, e, (UNCARE,v2,v3)) = Just (c,e,(Clo c' e,v2,v3))
ce3rMachineStep ((CLOSE2 c'):c, e, (v1,UNCARE,v3)) = Just (c,e,(v1,Clo c' e,v3))
ce3rMachineStep ((CLOSE3 c'):c, e, (v1,v2,UNCARE)) = Just (c,e,(v1,v2,Clo c' e))
ce3rMachineStep ((TAILAPPLY1):c,e,(Clo c' e',v,UNCARE)) = Just (c',v:e',(UNCARE,UNCARE,UNCARE))
ce3rMachineStep ((TAILAPPLY2):c,e,(Clo c' e',v1,v2)) = Just (c',v2:v1:e',(UNCARE,UNCARE,UNCARE))
ce3rMachineStep _ = Nothing



--apply transition rules
loop :: State -> State
loop state = 
        case ce3rMachineStep state of
                Just state' -> loop state'
                Nothing -> state

--evaluation
eval :: DB.Term -> Value
eval t = case loop (bcompile t,[],(UNCARE,UNCARE,UNCARE)) of
                (_,_,(v1,UNCARE,UNCARE)) -> v1
                _                        -> error "Evaluation Error Occurred!"
                


            
\end{code}
