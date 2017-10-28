\begin{code}
module CE3RMachine where

import qualified AbstractSyntax as S
import qualified DeBruijn as DB
import qualified CPS as CPS
import qualified IntegerArithmetic as I
import Debug.Trace

--instructions
data Inst = ACCESS1 Int
          | ACCESS2 Int
          | ACCESS3 Int
          | CONST1  Integer
          | CONST2  Integer
          | CONST3  Integer
          | BOOL1 Bool
          | BOOL2 Bool
          | BOOL3 Bool
          | CLOSE1  Code
          | CLOSE2  Code
          | CLOSE3  Code
          --ADD
          | ADD1 Int Int
          | ADD2 Int Int
          | ADD3 Int Int
          --SUB
          | SUB1 Int Int
          | SUB2 Int Int
          | SUB3 Int Int
          --MUL
          | MUL1 Int Int
          | MUL2 Int Int
          | MUL3 Int Int
          --DIV
          | DIV1 Int Int
          | DIV2 Int Int
          | DIV3 Int Int
          --NAND
          | NAND1 Int Int
          | NAND2 Int Int
          | NAND3 Int Int
          --EQ
          | EQ1 Int Int
          | EQ2 Int Int
          | EQ3 Int Int
          --LT
          | LT1 Int Int
          | LT2 Int Int
          | LT3 Int Int
          --TailApply1, TailApply2
          | TAILAPPLY1
          | TAILAPPLY2
          -- IF
          | IF Int Code Code
          -- LET
          | LET Int Code
          | ENDLET
          -- FIX
          | FIX Code
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
    DB.Tru                        -> [acompile 1 t]
    DB.Fls                     -> [acompile 1 t]
    DB.If (DB.Var v) t2 t3   -> [IF v (bcompile t2) (bcompile t3)]
    DB.Let (DB.Var v) t2     -> [LET v (bcompile t2)] ++ [ENDLET]
    _                        -> trace("bcompile unsupported term:" ++ show t ++ "\n") error "bcompile:Unsupported term"

-- compile the nameless axiom to the machine code instructions
acompile :: Int -> DB.Term -> Inst
acompile j t = 
  case t of
    DB.Var i       -> case j of
                        1 -> ACCESS1 i
                        2 -> ACCESS2 i
                        3 -> ACCESS3 i
                        _ -> error "Var i:Code Generating Error"
    DB.IntConst n  -> case j of
                        1 -> CONST1 n
                        2 -> CONST2 n
                        3 -> CONST3 n
                        _ -> error "IntConst n:Code Generating Error"
    DB.Tru         -> case j of
                        1 -> BOOL1 True
                        2 -> BOOL2 True
                        3 -> BOOL3 True
                        _ -> error "Tru:Code Generating Error"
    DB.Fls         -> case j of
                        1 -> BOOL1 False
                        2 -> BOOL2 False
                        3 -> BOOL3 False
                        _ -> error "Fls:Code Generating Error"
    DB.Abs tau1 (DB.Abs tau2 t2) -> case j of
                                      1 -> CLOSE1 (bcompile t2)
                                      2 -> CLOSE2 (bcompile t2)
                                      3 -> CLOSE3 (bcompile t2)
                                      _ -> error "Abs Abs:Code Generating Error"
    DB.Abs tau1 t1 -> case j of
                        1 -> CLOSE1 (bcompile t1)
                        2 -> CLOSE2 (bcompile t1)
                        3 -> CLOSE3 (bcompile t1)
                        _ -> error "Abs:Code Generating Error"
    --add
    DB.Bop DB.Add (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> ADD1 i1 i2
                        2 -> ADD2 i1 i2
                        3 -> ADD3 i1 i2
                        _ -> error "add:Code Generating Error"
    --sub
    DB.Bop DB.Sub (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> SUB1 i1 i2
                        2 -> SUB2 i1 i2
                        3 -> SUB3 i1 i2
                        _ -> error "sub:Code Generating Error"
    --mul
    DB.Bop DB.Mul (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> MUL1 i1 i2
                        2 -> MUL2 i1 i2
                        3 -> MUL3 i1 i2
                        _ -> error "mul:Code Generating Error"
    --div
    DB.Bop DB.Div (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> DIV1 i1 i2
                        2 -> DIV2 i1 i2
                        3 -> DIV3 i1 i2
                        _ -> error "div:Code Generating Error"
    --nand
    DB.Bop DB.Nand (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> NAND1 i1 i2
                        2 -> NAND2 i1 i2
                        3 -> NAND3 i1 i2
                        _ -> error "nand:Code Generating Error"
    --eq
    DB.Bpr DB.Eq (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> EQ1 i1 i2
                        2 -> EQ2 i1 i2
                        3 -> EQ3 i1 i2
                        _ -> error "eq:Code Generating Error"
    --lt
    DB.Bpr DB.Lt (DB.Var i1) (DB.Var i2) -> case j of
                        1 -> LT1 i1 i2
                        2 -> LT2 i1 i2
                        3 -> LT3 i1 i2
                        _ -> error "lt:Code Generating Error"
    --fix
    DB.Fix (DB.Var v) -> case j of
                        1 -> CLOSE1 [FIX (bcompile (DB.Var v))]
                        2 -> CLOSE2 [FIX (bcompile (DB.Var v))]
                        3 -> CLOSE3 [FIX (bcompile (DB.Var v))]
                        _ -> error "fix var:Code Generating Error"
    DB.Fix (DB.Abs tau1 (DB.Abs tau2 (DB.Abs tau3 t3))) -> case j of
                        1 -> CLOSE1 [FIX (bcompile t3)]
                        2 -> CLOSE2 [FIX (bcompile t3)]
                        3 -> CLOSE3 [FIX (bcompile t3)]
                        _ -> error "fix t:Code Generating Error"
    _              -> trace ("unsupported term is "++ show t) error "acompile:Unsupported term"
    



--transition rules
ce3rMachineStep :: State -> Maybe State
ce3rMachineStep ((CONST1 n):c, e,(_,v2,v3)) = Just (c,e,(IntVal n,v2,v3))
ce3rMachineStep ((CONST2 n):c, e,(v1,_,v3)) = Just (c,e,(v1,IntVal n,v3))
ce3rMachineStep ((CONST3 n):c, e,(v1,v2,_)) = Just (c,e,(v1,v2,IntVal n))
ce3rMachineStep ((BOOL1 b):c, e,(_,v2,v3)) = Just (c,e,(BoolVal b,v2,v3))
ce3rMachineStep ((BOOL2 b):c, e,(v1,_,v3)) = Just (c,e,(v1,BoolVal b,v3))
ce3rMachineStep ((BOOL3 b):c, e,(v1,v2,_)) =  Just (c,e,(v1,v2,BoolVal b))
ce3rMachineStep ((ACCESS1 i):c, e, (_,v2,v3)) =  Just (c,e,(e !! i,v2,v3))
ce3rMachineStep ((ACCESS2 i):c, e, (v1,_,v3)) = Just (c,e,(v1,e !! i,v3))
ce3rMachineStep ((ACCESS3 i):c, e, (v1,v2,_)) = Just (c,e,(v1,v2,e !! i))
--close
ce3rMachineStep ((CLOSE1 [FIX c']):c,e,(_,v2,v3)) = Just (c,e,(Clo c' ((Clo [FIX c'] e):e),v2,v3))
ce3rMachineStep ((CLOSE2 [FIX c']):c,e,(v1,_,v3)) =  Just (c,e,(v1,Clo c' ((Clo [FIX c'] e):e),v3))
ce3rMachineStep ((CLOSE3 [FIX c']):c,e,(v1,v2,_)) = Just (c,e,(v1,v2,Clo c' ((Clo [FIX c'] e):e)))
ce3rMachineStep ((CLOSE1 c'):c, e, (_,v2,v3)) =  Just (c,e,(Clo c' e,v2,v3))
ce3rMachineStep ((CLOSE2 c'):c, e, (v1,_,v3)) =  Just (c,e,(v1,Clo c' e,v3))
ce3rMachineStep ((CLOSE3 c'):c, e, (v1,v2,_)) = Just (c,e,(v1,v2,Clo c' e))

--add
ce3rMachineStep ((ADD1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(IntVal (I.intAdd a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((ADD2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,IntVal (I.intAdd a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((ADD3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,v2,IntVal (I.intAdd a b)))
                  _ -> Nothing
    _ -> Nothing

--sub
ce3rMachineStep ((SUB1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(IntVal (I.intSub a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((SUB2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,IntVal (I.intSub a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((SUB3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(v1,v2,IntVal (I.intSub a b)))
                  _ -> Nothing
    _ -> Nothing
--mul
ce3rMachineStep ((MUL1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(IntVal (I.intMul a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((MUL2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(v1,IntVal (I.intMul a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((MUL3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,v2,IntVal (I.intMul a b)))
                  _ -> Nothing
    _ -> Nothing
--div
ce3rMachineStep ((DIV1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(IntVal (I.intDiv a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((DIV2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(v1,IntVal (I.intDiv a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((DIV3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(v1,v2,IntVal (I.intDiv a b)))
                  _ -> Nothing
    _ -> Nothing
--nand
ce3rMachineStep ((NAND1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(IntVal (I.intNand a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((NAND2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,IntVal (I.intNand a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((NAND3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,v2,IntVal (I.intNand a b)))
                  _ -> Nothing
    _ -> Nothing
--eq
ce3rMachineStep ((EQ1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(BoolVal (I.intEq a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((EQ2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,BoolVal (I.intEq a b),v3))
                  _ -> trace("eq2 i2:" ++ show (e !! i2) ++"\n") Nothing
    _ -> trace("eq t1:" ++ show (e !! i1) ++"\n") Nothing
ce3rMachineStep ((EQ3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->Just (c,e,(v1,v2,BoolVal (I.intEq a b)))
                  _ -> trace("eq i2:" ++ show (e !! i2) ++"\n") Nothing
    _ -> trace("eq t1:" ++ show (e !! i1) ++"\n") Nothing
--lt
ce3rMachineStep ((LT1 i1 i2):c,e,(_,v2,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(BoolVal (I.intLt a b),v2,v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((LT2 i1 i2):c,e,(v1,_,v3)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b ->  Just (c,e,(v1,BoolVal (I.intLt a b),v3))
                  _ -> Nothing
    _ -> Nothing
ce3rMachineStep ((LT3 i1 i2):c,e,(v1,v2,_)) = 
  case (e !! i1) of
    IntVal a -> case (e !! i2) of
                  IntVal b -> Just (c,e,(v1,v2,BoolVal (I.intLt a b)))
                  _ -> Nothing
    _ -> Nothing
--if 
ce3rMachineStep ((IF v c1 c2):c,e,(v1,v2,v3)) =
  case (e !! v) of
    BoolVal True ->  Just (c1 ++ c,e,(v1,v2,v3))
    BoolVal False -> Just (c2 ++ c,e,(v1,v2,v3))
    _ -> Nothing
--let
ce3rMachineStep ((LET v c'):c,e,(v1,v2,v3)) = Just (c'++c,(e !! v):e,(v1,v2,v3))
ce3rMachineStep ((ENDLET):c,v:e,(v1,v2,v3)) = Just (c,e,(v1,v2,v3))


ce3rMachineStep ((TAILAPPLY1):c,e,(Clo [FIX c'] e',v,_)) = Just (c',v:(Clo [FIX c'] e'):e',(UNCARE,UNCARE,UNCARE))
ce3rMachineStep ((TAILAPPLY2):c,e,(Clo [FIX c'] e',v1,v2)) = Just (c',v2:v1:(Clo [FIX c'] e'):e',(UNCARE,UNCARE,UNCARE))


ce3rMachineStep ((TAILAPPLY1):c,e,(Clo c' e',v,_)) = Just (c',v:e',(UNCARE,UNCARE,UNCARE))
ce3rMachineStep ((TAILAPPLY2):c,e,(Clo c' e',v1,v2)) = Just (c',v2:v1:e',(UNCARE,UNCARE,UNCARE))


ce3rMachineStep t = Nothing



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
