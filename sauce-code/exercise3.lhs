%% Literate Haskell script intended for lhs2TeX.
\documentclass[10pt]{article}
%include polycode.fmt


%format union = "\cup"
%format `union` = "\cup"
%format Hole = "\square"
%format MachineTerminate ="\varodot"
%format CEKMachineTerminate ="\varodot"
%format alpha = "\alpha"
%format gamma = "\gamma"
%format zeta = "\zeta"
%format "kappa" = "\text{``}\kappa\text{``}"
%format kappa'
%format capGamma = "\Gamma"
%format sigma = "\sigma"
%format tau = "\tau"
%format taus = "\tau s"
%format ltaus = "l\tau s"
%format tau1
%format tau1'
%format tau2
%format tau11
%format tau12
%format upsilon = "\upsilon"
%format xi = "\xi"
%format t12
%format t1
%format t1'
%format t2
%format t2'
%format t3
%format nv1

\usepackage{fullpage}
\usepackage{mathpazo}
\usepackage{verbatim}
\usepackage{graphicx}
\usepackage{color}
\usepackage[centertags]{amsmath}
\usepackage{amsfonts}
\usepackage{amssymb}
\usepackage{mathrsfs}
\usepackage{amsthm}
\usepackage{stmaryrd}
\usepackage{soul}
\usepackage{url}
\usepackage{geometry}
\usepackage{mathrsfs} 

\usepackage{vmargin}
\setpapersize{USletter}
\setmarginsrb{1.1in}{1.0in}{1.1in}{0.6in}{0.3in}{0.3in}{0.0in}{0.2in}
\parindent 0in  \setlength{\parindent}{0in}  \setlength{\parskip}{1ex}

\usepackage{epsfig}
\usepackage{rotating}

\usepackage{mathpazo,amsmath,amssymb}
\title{Exercise 3, CS 555}
\author{\textbf{By:} Sauce Code Team (Dandan Mo, Qi Lu, Yiming Yang)}
\date{\textbf{Due:} April 16th, 2012}
\begin{document}
        \maketitle
        \thispagestyle{empty}
        \newpage

\section{De Bruijn Notation}

\subsection{nameless representation}

\paragraph{}
Formally convert the ordinary lambda terms to nameless terms. Using nature numbers to replace the variable names. 

\parargraph{}
Here, we use the list of variables to keep track of the abstract depth of each one, which is just the de Bruijn indices for them. Besides, in order to go around the presumable error of generating hybrid term (S.Term with some DB.Term inside), we first convert the de Bruijn indices into \textbf{String} as the new names of the variables, and when we finish the variable name substitution, we call \textit{finalProcess} function to convert these names back into \textbf{Int} and remove the variable names in their definitions.

\subsection{Haskell Implementation}

\begin{code}
module DeBruijn where

import qualified AbstractSyntax as S
import Data.List
import Data.Char

type Type = S.Type
data Term = Var Int
          | IntConst Integer
          | Tru
          | Fls
          | Abs Type Term
          | App Term Term
          | If  Term Term Term
          | Fix Term
          | Let Term Term
          | Bop BOP Term Term
          | Bpr BPR Term Term

instance Show Term where
          show (Var i) = "(Index " ++ show i ++ ")"
          show (IntConst i) = show i
          show Tru = "true"
          show Fls = "false"
          show (Abs tau t) = "abs(" ++ ":" ++ show tau ++ "." ++ show t ++ ")"
          show (App t1 t2) = "app(" ++ show t1 ++ "," ++ show t2 ++ ")"
          show (If t1 t2 t3) = "if " ++ show t1 ++ " then " ++ show t2 ++ " else " ++ show t3 ++ " fi"
          show (Fix t) = "fix " ++ show t
          show (Let t1 t2) = "let "++ show t1 ++ " " ++ show t2
          show (Bop op t1 t2) = show op ++ "(" ++ show t1 ++ ", " ++ show t2 ++ ")"
          show (Bpr p t1 t2) = show p ++ "(" ++ show t1 ++ "," ++ show t2 ++ ")"

-- Binary Operator 
data BOP = Add | Sub | Mul | Div | Nand
          
instance Show BOP where 
          show Add = "+"
          show Sub = "-"
          show Mul = "*"
          show Div = "/"
          show Nand= "^"

-- Binary Predicate
data  BPR = Eq | Lt
instance Show BPR where
  show Eq = "="
  show Lt = "<"

newtype Environment = Env [S.Var]
                      deriving Show 

lookupEnv :: Environment -> S.Var -> Int
lookupEnv (e@(Env [])) x  =  error ("variable " ++ x ++ " not bound in environment " ++ show e)
lookupEnv (Env es) x = 
  case elemIndices x es of
    [] -> error "This term has free variables!"
    _  -> head $ elemIndices x es

finalProcess :: S.Term -> Term
finalProcess (S.Var x) = Var (read x::Int)
finalProcess (S.Abs x tau t1) = Abs tau (finalProcess t1)
finalProcess (S.App t1 t2) = App (finalProcess t1) (finalProcess t2)
finalProcess S.Tru = Tru
finalProcess S.Fls = Fls
finalProcess (S.If t1 t2 t3) = If (finalProcess t1) (finalProcess t2) (finalProcess t3)
finalProcess (S.IntConst n) = IntConst n
finalProcess (S.IntAdd t1 t2) = Bop Add (finalProcess t1) (finalProcess t2)
finalProcess (S.IntSub t1 t2) = Bop Sub (finalProcess t1) (finalProcess t2)
finalProcess (S.IntMul t1 t2) = Bop Mul (finalProcess t1) (finalProcess t2)
finalProcess (S.IntDiv t1 t2) = Bop Div (finalProcess t1) (finalProcess t2)
finalProcess (S.IntNand t1 t2) = Bop Nand (finalProcess t1) (finalProcess t2)
finalProcess (S.IntEq t1 t2) = Bpr Eq (finalProcess t1) (finalProcess t2)
finalProcess (S.IntLt t1 t2) = Bpr Lt (finalProcess t1) (finalProcess t2)
finalProcess (S.Fix t) = Fix (finalProcess t)
finalProcess (S.Let x t1 t2) = Let (finalProcess t1) (finalProcess t2)


toDeBruijn :: S.Term -> Term
toDeBruijn t = 
  finalProcess $ f (t, Env [])
  where
    f :: (S.Term, Environment) -> S.Term
    f (S.Abs x tau t1, Env es) = S.Abs x tau (f (t1, Env (x:es)))
    f (S.App t1 t2, e) = S.App (f(t1, e)) (f(t2, e))
    f (S.Var x, e) = S.Var (show (lookupEnv e x))
    f (S.Let x t1 t2, Env es) = S.Let x (f (t1, Env es)) (f (t2, Env (x:es)))
    f (S.If t1 t2 t3, e) = S.If (f(t1, e)) (f(t2, e)) (f(t3, e))
    f (S.IntAdd t1 t2, e) = S.IntAdd (f(t1, e)) (f(t2, e))
    f (S.IntSub t1 t2, e) = S.IntSub (f(t1, e)) (f(t2, e))
    f (S.IntMul t1 t2, e) = S.IntMul (f(t1, e)) (f(t2, e))
    f (S.IntDiv t1 t2, e) = S.IntDiv (f(t1, e)) (f(t2, e))
    f (S.IntNand t1 t2, e) = S.IntNand (f(t1, e)) (f(t2, e))
    f (S.IntEq t1 t2, e) = S.IntEq (f(t1, e)) (f(t2, e))
    f (S.IntLt t1 t2, e) = S.IntLt (f(t1, e)) (f(t2, e))
    f (S.Fix t, e) = S.Fix (f(t, e))
    f (t, e) = t
\end{code}

\section{Natural Semantics with Nameless Terms}
We modify the Natural Semantics evaluation rules by adding environment and closures. The rules we modified are as the followings:\\

\begin{equation*}
e \vdash Tru \Rightarrow True
\end{equation*}

\begin{equation*}
e \vdash Fls \Rightarrow False
\end{equation*}

%const n
\begin{equation*}
e \vdash \text{IntConst $n$} \Rightarrow n
\end{equation*}

\begin{equation*}
\frac{e \vdash t_1 \Rightarrow True, e\vdash t_2 \Rightarrow \alpha}{e\vdash \text{If  $t_1$ $t_2$ $t_3$} \Rightarrow \alpha}
\end{equation*}

\begin{equation*}
\frac{e \vdash t_1 \Rightarrow False, e\vdash t_3 \Rightarrow \beta}{e\vdash \text{If  $t_1$ $t_2$ $t_3$} \Rightarrow \beta}
\end{equation*}

%var
\begin{equation*}
e \vdash \text{Var $i$} \Rightarrow e[i]
\end{equation*}

%app
\begin{equation*}
\frac{e \vdash t_1 \Rightarrow \text{Clo $\lambda.t'$ $e'$}, e \vdash t_2 \Rightarrow v, v:e' \vdash t' \Rightarrow v'}{e \vdash \text{$t_1$ $t_2$} \Rightarrow v'}
\end{equation*}

\begin{equation*}
\frac{e \vdash t_1 \Rightarrow \text{Clo Fix $t'$ $e'$}, e' \vdash \text{Fix $t'$}  \Rightarrow \text{Clo $\lambda.tt$ $ee$}, e \vdash t_2 \Rightarrow v, v:ee \vdash tt \Rightarrow v'}{e \vdash \text{$t_1$ $t_2$} \Rightarrow v'}
\end{equation*}

% abs
\begin{equation*}
e \vdash \lambda.t_1 \Rightarrow \text{Clo $\lambda.t_1$ e}
\end{equation*}

%BOP
\begin{equation*}
\frac{ e\vdash t_1 \Rightarrow v_1, e\vdash t_2 \Rightarrow v_2, \overline{op}(v_1,v_2) = v}{e \vdash op(t_1,t_2) \Rightarrow v}
\end{equation*}
where $op$ are binary arithmetical operations: Add, Sub,Mul,Div,Nand, $\overline{op}$ indicates the real arithmetical functions.

%BRP
\begin{equation*}
\frac{ e\vdash t_1 \Rightarrow v_1, e\vdash t_2 \Rightarrow v_2, \overline{rp}(v_1,v_2) = v}{e \vdash rp(t_1,t_2) \Rightarrow v}
\end{equation*}
where $rp$ are binary relational operations: Eq, Lt, $\overline{rp}$ indicates the real relational functions.

\begin{equation*}
\frac{e \vdash t_1 \Rightarrow \alpha, \alpha:e \vdash t_2 \Rightarrow \beta}{e \vdash \text{Let $t_1$ $t_2$} \Rightarrow \beta}
\end{equation*}

%fix
\begin{equation*}
\frac{e \vdash t_1 \Rightarrow \text{Clo $\lambda.t'$ $e'$}, (\text{Clo Fix $\lambda.t'$ e'}) : e' \vdash t' \Rightarrow v}{e \vdash \text{Fix $t_1$} \Rightarrow v}
\end{equation*}


The followings are the implemented codes:\\
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

\section{C-E-S Compiler and Virtual Machine}

\subsection{Formal Rules}

\paragraph{}
Foramally statting the compilation rules of C-E-S compiler and virtual machine:

\[
        C[\text{IntConst n}] = \text{CONST } n
\]
\ \\
\[
        C[\text{Var i}] = \text{ACCESS } i
\]
\ \\
\[
        C[\text{Abs t}] = \text{CLOSE } C[t]; \text{ RETURN}
\]
\ \\
\[
        C[\text{App t1 t2}] = C[t1]; C[t2]; \text{ APPLY}
\]
\ \\
\[
        C[\text{If t0 t1 t2}] = C[t0]; \text{ IF}; [Close C[t1]]; [Close C[t2]] 
\]
\ \\
\[
        C[\text{True}] = \text{ True}
\]
\ \\
\[
        C[\text{False}] = \text{ False}
\]
\ \\
\[
        C[\text{Fix t}] = C[t]; \text{ Fix}
\]
\ \\
\[
        C[\text{Let t1 t2}] = C[t1]; \text{ Let }; C[t2]; \text{ EndLet} 
\]
\ \\
\[
        C[\text{Bop t1 t2}] = C[t1]; C[t2]; \text{ Bop} \text{\quad -Binary operations: $+, -, *, /$}  
\]
\ \\
\[
        C[\text{Bpr t1 t2}] = C[t1]; C[t2]; \text{ Bpr} \text{\quad -Binary predicates: $<$; $==$}  
\]
\ \\

\paragraph{}
Foramally statting the transitions of C-E-S compiler and virtual machine:

\[
        \text{(Access i : c, e, s)} \rightarrow \text{(c, e, (e !! i) : s)}  
\]
\ \\
\[
        \text{(If:(Close t1):(Close t2):c, e, (Value True):s)} \rightarrow \text{(t1++c, e, s)}  
\]
\ \\
\[
        \text{(If:(Close t1):(Close t2):c, e, (Value False):s)} \rightarrow \text{(t2++c, e, s)}  
\]
\ \\

\[
        \text{(Close code':code, env, s)} \rightarrow \text{(code, env, Env [Clo code' env]:s)}  
\]
\ \\

\[
        \text{(Apply:code, env, (Value v):(Env [Clo code' env']):s)} \rightarrow \text{(code', v:env', (Code code):(Env env):s)}  
\]
\ \\
\[
        \text{(Apply:code, env, (Value v):(Value (Clo code' env')):s)} \rightarrow C[\text{(code', v:env', (Code code):(Env env):s)}  
\]
\ \\
\[
        \text{(Apply:code, env, (Value v):(Value (CloFix [Close [Close code', Return], Fix] env')):s)}]    
\]
\[
        \rightarrow \text{(code', v:(CloFix [Close [Close code', Return], Fix] env'):env', (Code code):(Env env):s)}    
\]

\ \\
\[
        {(Return:c, e, s':(Code c'):(Env e'):s)} \rightarrow \text{(c', e', s':s)}  
\]
\ \\
\[
        \text{(Int n:c, e, s)} \rightarrow \text{(c, e, (Value n):s)}  
\]
\ \\
\[
        \text{(Bool b:c, e, s)} \rightarrow \text{(c, e, (Value b):s)}  
\]
\ \\
\[
        \text{(bop:c, e, (Value v2):(Value v1):s)} \rightarrow \text{(c, e, (Value (bop v1 v2)):s)}  
\]
\ \\
\[
        \text{(bpr:c, e, (Value v2):(Value v1):s)} \rightarrow \text{(c, e, (Value (bpr v1 v2)):s)}  
\]
\ \\
\[
        \text{(Let:code, env, (Value v):s)} \rightarrow \text{(code, v:env, s)} 
\]
\ \\

\[
        \text{(EndLet:code, v:env, s)} \rightarrow \text{(code, env, s)}  
\]
\ \\
\[
        \text{(Fix:code, env, (Env [Clo code' env']):s)} \rightarrow \text{(code', (CloFix [Close code', Fix] []):env, (Code code):(Env env):s)}  
\]

\subsubsection{Haskell Implementation}
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
data Value = BoolVal Bool | IntVal Integer | Clo Code Env |CloFix Code Env 
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
	DB.If t0 t1 t2 -> compile t0 ++ [If]++ [Close (compile t1)]++ [Close (compile t2)]
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
	(If:(Close t):(Close f):c, e, (Value (BoolVal v0)):s) -> case v0 of
                                                        True -> Just(t++c, e, s)
                                                        False-> Just(f++c, e, s)
        (Close code':code, env, s) -> Just(code, env, (Value (Clo code' env)):s)
        (Apply:code, env, (Value v):(Value (Clo code' env')):s) -> 
	    Just(code', v:env', (Code code):(Env env):s)
	(Apply:code, env, (Value v):(Value (CloFix [Close [Close code', Return], Fix] env')):s) -> 
	    Just(code', v:(CloFix [Close [Close code', Return], Fix] env'):env', (Code code):(Env env):s)
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
        (EndLet:code, v:env, s) -> Just(code, env, s)
	(Fix:code, env, (Value (Clo code' env')):s) -> 
	    Just(code', (CloFix [Close code', Fix] []):env, (Code code):(Env env):s)
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



\section{CPS}

\paragraph{}

Implement CPS for the core lambda-language consisting of variables, abstractions, applications, primitive constants, primitive operations (+, -, etc.), if, let, and fix.

\subsection{CPS Transformation Rules}

\paragraph{}
The CPS transformation scheme to be used is the one shown in class (the original Fischer-Plotkin CPS transformation). Here it is:

\begin{align*}
        \text{variables:\quad \quad } \llbracket x \rrbracket & = \lambda \kappa.\text{ }\kappa\text{ }x\\
        \text{abstractions:\quad \quad } \llbracket \lambda x.t_1 \rrbracket & = \lambda \kappa.\text{ }\kappa\text{ }(\lambda x.\llbracket t_1 \rrbracket)\\
        \text{applications:\quad \quad } \llbracket t_1\text{ }t_2 \rrbracket & = \lambda \kappa.\text{ }\llbracket t_1 \rrbracket (\lambda v_1.\text{ }\llbracket t_2 \rrbracket (\lambda v_2.\text{ }v_1\text{ }v_2\text{ }\kappa))\\
        \text{constants:\quad \quad} \llbracket c \rrbracket &= \lambda \kappa.\text{ }\kappa\text{ }c
\end{align*}

\paragraph{}
The constants include boolean and integer constants. Besides, we also implemented the following rules:

\begin{align*}
        \text{conditional terms:\quad \quad } \llbracket \text{ If } t_1 \text{ } t_2 \text{ }t_3 \rrbracket & = \lambda \kappa.\text{ }\llbracket t_1 \rrbracket \text{ }(\lambda v.\text{ If } v \text{ }(\llbracket t_2 \rrbracket \text{ }\kappa)\text{ }(\llbracket t_3 \rrbracket \text{ }\kappa))\\
        \text{let bindings \cite{letBinding}:\quad \quad }\llbracket \text{ Let } x\text{ }t_1 \text{ } t_2 \rrbracket & = \lambda \kappa.\text{ }\llbracket t_1 \rrbracket \text{ }(\lambda v.\text{ Let } x \text{ }v\text{ }(\llbracket t_2 \rrbracket \text{ }\kappa))\\
        \text{binary operators:\quad \quad }\llbracket bop\text{ }t_1 \text{ }t_2 \rrbracket &= \lambda \kappa.\text{ }\llbracket t_1 \rrbracket \text{ }(\lambda v_1.\text{ }\llbracket t_2 \rrbracket \text{ }(\lambda v_2.\text{ }(\kappa\text{ }(bop\text{ }v_1\text{ }v_2)))\\                                                                   
        \text{fix terms:\quad \quad }\llbracket \text{ Fix } t_1\text{ }\rrbracket &= \begin{cases}
                                                                                        \lambda \kappa.\text{ }\kappa\text{ }(\text{Fix }\lambda f.\text{ }\lambda x.\text{ }\llbracket t_{11} \rrbracket) &\text{\quad if } t_1 = \lambda f.\text{ }\lambda x.\text{ }t_{11},\\
                                                                                        \lambda \kappa.\text{ }\llbracket t_1 \rrbracket \text{ }(\lambda v_1.\text{ }(\text{Fix } v_1)\text{ }\kappa) &\text{\quad otherwise.}
                                                                                      \end{cases}
\end{align*}

\subsection{Type-Preserved CPS Transformation}

\paragraph{}
In addition, we also implement the type-preserved CPS transformation using a function to transform the types into CPS form, i.e. \textit{toCPSType} function in CPS module. Formally \cite{typeCPS}, given a primitive type $\sigma$ (TypeInt, TypeBool), its CPS form type $\sigma'$ is:
\[
        \sigma' = \sigma
\]
And for function type $\alpha \rightarrow \beta$, the associated CPS form $(\alpha \rightarrow \beta)'$ is:
\[
        (\alpha \rightarrow \beta)' = \alpha' \rightarrow (\beta' \rightarrow o) \rightarrow o 
\]
where $o$ is a pseudo continuation return ``type". Since continuations actually don't return values, we can set this $o$ to any type. In our implementation, we just add one parameter named \textit{answerType} for all CPS transformation functions, leaving it as a user option. But we also add ``\textbf{TypeNull}" in \textbf{Type} data structure of \emph{AbstractSyntax} module for debugging. Besides, for the generated continuation types, for example, given the transformation:
\[
        \llbracket t_1\text{ }t_2 \rrbracket = \lambda \kappa.\text{ }\llbracket t_1 \rrbracket (\lambda v_1.\text{ }\llbracket t_2 \rrbracket (\lambda v_2.\text{ }v_1\text{ }v_2\text{ }\kappa))
\]
if $t_1$ is of type $\alpha \rightarrow \beta$, then $v_1$ must be of type $(\alpha \rightarrow \beta)' = \alpha' \rightarrow (\beta' \rightarrow o) \rightarrow o$, $v_2$ of type $\alpha'$, and $\kappa$ of type $\beta' \rightarrow o$. And it is similar for all the other transformation rules.

\subsection{Get Type With Context}

\paragraph{}
At first, we use \textbf{typeCheck} function in \emph{Typing} module to calculate the type for each subterm. However, for the case of variables and the subterms within abstractions and fix terms, since the type information of some variables are out of the scope, this method will fail.

\paragraph{}
So instead, we use \textbf{typing} function in \emph{Typing} module, which takes one argument of the current typing context $\Gamma$ explicitly. And in \emph{CPS} module, we also write a function \textbf{toCPSWithContext} to take this $\Gamma$ as an explicit argument. Then, we can just make the function \textbf{toCPS} to call it, starting with an empty typing context.
\paragraph{}
With this implementation, we can know that the only cases of adding typing pairs into $\Gamma$ are the \textbf{Let} bindings and the abstractions. For all the other cases, we can recursively call the \textbf{toCPSWithContext} function for the subterms with the current $\Gamma$.




\paragraph{}
The implementation of CPS transformation is as follows:


\begin{code}
module CPS where

import Data.Maybe

import qualified AbstractSyntax as S
import qualified Typing as T

toCPSType :: S.Type -> S.Type -> S.Type
toCPSType S.TypeBool _ = S.TypeBool
toCPSType S.TypeInt _ = S.TypeInt 
toCPSType (S.TypeArrow tau1 tau2) answerType =
  S.TypeArrow (toCPSType tau1 answerType) (S.TypeArrow (S.TypeArrow (toCPSType tau2 answerType) answerType) 
                  answerType)

toCPSWithContext :: S.Type -> S.Term -> T.Context -> S.Term
toCPSWithContext answerType t capGamma = 
  case t of
    S.IntConst n    -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) (S.App (S.Var "kappa") t)
    S.Tru           -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) (S.App (S.Var "kappa") t)
    S.Fls           -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) (S.App (S.Var "kappa") t)
    S.Var x         -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                                     (S.App (S.Var "kappa") t)
    S.App t1 t2     -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" (toCPSType (fromJust $ T.typing capGamma t1) answerType) 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" (toCPSType (fromJust $ T.typing capGamma t2) 
                                                                                   answerType) 
                                                                           (S.App (S.App (S.Var "v1") (S.Var "v2")) 
                                                                                  (S.Var "kappa"))))))
    S.Abs x tau1 t1 -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                                     (S.App (S.Var "kappa") 
                                            (S.Abs x (toCPSType tau1 answerType) 
                                                     (toCPSWithContext answerType t1 (T.Bind capGamma x tau1))))
    S.If t1 t2 t3   -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v" (toCPSType (fromJust $ T.typing capGamma t1) answerType) 
                                                       (S.If (S.Var "v") 
                                                         (S.App (toCPSWithContext answerType t2 capGamma) 
                                                                (S.Var "kappa")) 
                                                         (S.App (toCPSWithContext answerType t3 capGamma) 
                                                                (S.Var "kappa")))))
    S.Let x t1 t2   -> S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v" (toCPSType (fromJust $ T.typing capGamma t1) answerType) 
                                                       (S.Let x (S.Var "v") 
                                                                (S.App (toCPSWithContext answerType t2 
                                                                           (T.Bind capGamma x (fromJust $ T.typing 
                                                                                               capGamma t1))) 
                                                                       (S.Var "kappa")))))
    S.Fix t1        -> 
      case t1 of
        S.Abs f tau1 (S.Abs x tau2 t11) -> 
            S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                          (S.App (S.Var "kappa") 
                                 (S.Fix 
                                   (S.Abs f (toCPSType tau1 answerType) 
                                            (S.Abs x (toCPSType tau2 answerType) 
                                                     (toCPSWithContext answerType t11 
                                                          (T.Bind (T.Bind capGamma f tau1) x tau2))))))
        _                               -> 
            S.Abs "kappa" (S.TypeArrow (toCPSType (fromJust $ T.typing capGamma t) answerType) answerType) 
                          (S.App (toCPSWithContext answerType t1 capGamma) 
                                 (S.Abs "v1" (toCPSType (fromJust $ T.typing capGamma t1) answerType) 
                                             (S.App (S.Fix (S.Var "v1")) 
                                                    (S.Var "kappa"))))
    S.IntAdd t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntAdd (S.Var "v1") 
                                                                                            (S.Var "v2")))))))
    S.IntSub t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntSub (S.Var "v1") 
                                                                                            (S.Var "v2")))))))
    S.IntMul t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntMul (S.Var "v1") 
                                                                                            (S.Var "v2")))))))
    S.IntDiv t1 t2  -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntDiv (S.Var "v1") 
                                                                                            (S.Var "v2")))))))
    S.IntNand t1 t2 -> S.Abs "kappa" (S.TypeArrow S.TypeInt answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntNand (S.Var "v1") 
                                                                                             (S.Var "v2")))))))
    S.IntEq t1 t2   -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntEq (S.Var "v1") 
                                                                                           (S.Var "v2")))))))
    S.IntLt t1 t2   -> S.Abs "kappa" (S.TypeArrow S.TypeBool answerType) 
                                     (S.App (toCPSWithContext answerType t1 capGamma) 
                                            (S.Abs "v1" S.TypeInt 
                                                        (S.App (toCPSWithContext answerType t2 capGamma) 
                                                               (S.Abs "v2" S.TypeInt 
                                                                           (S.App (S.Var "kappa") 
                                                                                  (S.IntLt (S.Var "v1") 
                                                                                           (S.Var "v2")))))))
    
toCPS :: S.Type -> S.Term -> S.Term
toCPS answerType t = 
  toCPSWithContext answerType t T.Empty


\end{code}

\section{C-E-3R Compiler and Virtual Machine}

\paragraph{}
The input of the C-E-3R machine are the Debruijn terms. The machine will compile the Debruijn terms into the instructions, which together with the environment(Env) and three registers($r_1$,$r_2$,$r_3$) form the initial state of the machine.We design the transition rules for this machine, and apply them to the initial state until we finish process all the instructions. If error occurred, the machine will stop and output the last valid value stored in $r_1$. 

\paragraph{}
C-E-3R machine is consisted of a compiler and a set of transition rules.The compiler takes the restricted forms of $\lambda$ terms as its input and transform them into the instructions we design. The restricted forms categorizes the DeBruijn terms into ``body" and ``atom", which are also DeBruijn terms but have another name. So we need to define which terms are ``body" and which terms are ``atom"".

\paragraph{}
And then we implement the compilers consisted of two kinds of compiling functions, acompile is for compiling ``atom" terms and bcompile is for compiling ``body" terms.

\paragraph{}
The ``atom" and ``body", compilers, instruction set and transition rules form a complete C-E-3-R machine.

\subsection{Definition of ``body" and ``atom"}
1): ``body" 
\begin{align*}
\text{body}: = ~& t_1~t_2~t_3 &\\
                   & t_1~t_2 &\\
                   & n &\text{$n$ is an integer constant}\\
                   & i   &\text{$i$ is an index for the variable}\\
                   & true & \\
                   & false & \\
                   & \text{if $t_1$ $t_2$ $t_3$}\\
                   & \text{let i t} &\text{i is an index}\\
\end{align*}

2):``atom"
\begin{align*}
\text{atom}: = ~& i & \text{index} \\
                        & n & \text{integer constant}\\
                        & true & \\
                        & false & \\
                        & \lambda.\lambda.t & \\
                        & \lambda.t & \\
                        & bop~v_1~v_2 & \text{$bop$ refers to binary operation}\\
                        & &\text{($+,-,*,/,^,=,<$),$v_1$ and $v_2$ are indexes}\\
                        & \text{fix $i$} & \text{$i$ is an index}\\
                        & \text{fix }\lambda.\lambda.\lambda.t 
\end{align*}

\subsection{Instruction sets}

\paragraph{}
The $i$ appearing in the instructions is $i \in \{1,2,3\}$,$r_i$ is register. Please refer to Table 1.
\begin{figure}
\caption{Instructions}
\label{tab:Inst}
\begin{tabular}{l | l}
\hline
Instruction & Explanation \\ \hline \hline
ACCESS$_i$ j & take the $j$-th element from environment into $r_i$\\
\hline
CONST$_i$ n & put integer constant $n$ into $r_i$\\ 
\hline
BOOL$_i$ b & put boolean constant $b$ into $r_i$\\
\hline
CLOSE$_i$ c & create a closure with Code $c$ and the current environment, put it into $r_i$\\
\hline
BOP$_i$ $v_1$ $v_2$ & $v_1$,$v_2$ are indexes, get their values from the environment, do arithmetic calculation\\
 & put the result into $r_i$\\
 & BOP are ADD, SUB, MUL, DIV, NAND, EQ, LT\\
 \hline
IF $v$ $c_1$ $c_2$ & $v$ is an index, it points to the value of the condition in the environment\\
 & $c_1$,$c_2$ are Code, they are branches\\
 & we go to $c_1$ branch if the condition is true,otherwise go to $c_2$ branch\\
 \hline
LET $v$ $c$ & $v$ is an index pointing to substitutive value,we put this value into the environment\\
\hline
ENDLET & remove the head of the environment\\
\hline
$TAILAPPLY1$ & extract code in $r_1$, put $r_2$'s value into environment, execute the code\\
\hline
$TAILAPPLY2$ & extract code in $r_1$, put $r_2$ and $r_3$ value into environment, execute the code\\
\hline
\end{tabular}
\end{figure}

\subsection{Compilers}
B: compile the body into codes\\
A: compile the atom into instructions\\\\

1): body compiler(Table 2)\\\\
\begin{table}
\caption{body compiler: B}
\label{tab:b} 
\begin{tabular}{l|l}
B(DeBruijn term) & Instructions \\
\hline
B($t_1$ $t_2$ $t_3$) & A1($t_1$); A2($t_2$); A3($t_3$); TAILAPPLY2; \\
B($t_1$ $t_2$) & A1($t_1$); A2($t_2$); TAILAPPLY1;\\
B($n$) & A1($n$) \\
B($i$)  & A1($i$) \\
B(true) & A1(true)\\
B(false)& A1(false) \\
                   B(if $v$ $t_2$ $t_3$) & IF v B($t_2$) B($t_3$)\\
                   B(let $i$ lst) &  LET $i$ B(t); ENDLET\\
\end{tabular}
\end{table}

2):atom compier (Table 3), A$_j$'s subindex is $j \in \{1,2,3\}$\\
For the fix term, in the compiling process, we just close the instructions code, which is FIX Code.\\\\
\begin{table}
\caption{atom compiler:A}
\label{tab:a}
\begin{tabular}{l|l}
A(DeBruijn term) & Instructions\\
\hline
                 A$_j$( i ) & ACCEESS$_j$ i \\
                 A$_j$(n) & CONST$_j$ n \\
                 A$_j$(true) &BOOL$_j$ True \\
                 A$_j$(false) & BOOL$_j$ False\\
                 A$_j$($\lambda.\lambda.t$) & CLOSE$_j$ B(t) \\
                 A$_j$($\lambda.t$) & CLOSE$_j$ B(t) \\
                 \hline
                 A$_j$(bop $v_1$ $v_2$) & BOP$_j$ $v_1$ $v_2$\\
                 bop is $+,-,*,/,^,=,<$ & BOP is ADD, SUB, MUL, DIV, NAND, EQ, LT\\
                 \hline
                 A$_j$(fix $i$) & CLOSE$_j$ ( FIX B($i$) ) \\
                 A$_j$(fix $\lambda.\lambda.\lambda.t$) & CLOSE$_j$ ( FIX B(t) ) 
\end{tabular}
\end{table}

\subsection{Transition rules}
For integer constants,boolean constants,index and ``let" term, the transition rules are as described in class;\\
For binary operation, we look up the values in the environment according to the indexes provided by the instruction, and do the arithmetic calculation;\\
For ``if" terms, we look up the value of the condition part in the environment according to the index provided by the instruction, and choose the correct branch;\\\\
Fix term is the most difficult part, in order to achieve the recursive operation, we modify the transition rules for \textbf{CLOSE} and \textbf{TAILAPPLY1},\textbf{TAILAPPLY2}:\\\\
1): if we are closing a FIX instruction with environment $e$, e.g, CLOSE$_1$(FIX c'), we need to create a closure with $c'$ and a new environment e', where $e' = (Clo~[FIX ~c'] ~e):e$, so the closure will be Clo $c'$ ((Clo [FIX $c'$] $e$):$e$). In this way, the machine knows that fix itself is also an argument for fix.\\\\
2): if we are applying a FIX closure, e.g Clo [Fix c'] e, to the other argument, we must put itself into its environment after the other argument. For example:\\
((TAILAPPLY1):c, e, (Clo [Fix c'] e'),v,$\_$) $\Rightarrow$ (c',v:(Clo [FIX c'] e'):e',(UNCARE,UNCARE,UNCARE)).  \\
In this way, fix term itself are added into the environment as an argument.\\\\
The FIX CLOSE and FIX APPLY rules guarantee the recursive functions. For details, please refer to Table~\ref{tab:rule}\\
\begin{table}
\caption{Transition Rules}
\label{tab:rule}
\begin{tabular}{l|l}
\hline
current state & next state\\
\hline
\hline
((CONST$_1$ n):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(n,$v_2$,$v_3$))\\
((CONST$_2$ n):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,n,$v_3$))\\
((CONST$_3$ n):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,n))\\
\hline
((BOOL$_1$ b):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(b,$v_2$,$v_3$))\\
((BOOL$_2$ b):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,b,$v_3$))\\
((BOOL$_3$ b):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,b))\\
\hline
((ACCESS$_1$ i):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(e[i],$v_2$,$v_3$))\\
((ACCESS$_2$ i):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,e[i],$v_3$))\\
((ACCESS$_3$ i):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,e[i]))\\
\hline
\hline
((CLOSE$_1$ [FIX c']):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(Clo c' ((Clo [FIX c'] e):e),$v_2$,$v_3$))\\
((CLOSE$_2$ [FIX c']):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,Clo c' ((Clo [FIX c'] e):e),$v_3$))\\
((CLOSE$_3$ [FIX c']):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,Clo c' ((Clo [FIX c'] e):e)))\\
\hline
((CLOSE$_1$ c'):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(Clo c' e,$v_2$,$v_3$))\\
((CLOSE$_2$ c'):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,Clo c' e,$v_3$))\\
((CLOSE$_3$ c'):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,Clo c' e))\\
\hline
\hline
((BOP$_1$ $x_1$,$x_2$):c,e,($\_$,$v_2$,$v_3$)) &  (c,e,(bop($e[x_1]$,$e[x_2]$),$v_2$,$v_3$))\\
((BOP$_2$ $x_1$,$x_2$):c,e,($v_1$,$\_$,$v_3$)) &  (c,e,($v_1$,bop($e[x_1]$,$e[x_2]$),$v_3$))\\
((BOP$_3$ $x_1$,$x_2$):c,e,($v_1$,$v_2$,$\_$)) &  (c,e,($v_1$,$v_2$,bop($e[x_1]$,$e[x_2]$)))\\
\hline
((IF v $c_1$ $c_2$):c,e,($v_1$,$v_2$,$v_3$)) & ($c_1$++ c,e,($v_1$,$v_2$,$v_3$))--------$e[v]=true$\\
((IF v $c_1$ $c_2$):c,e,($v_1$,$v_2$,$v_3$)) & ($c_2$++ c,e,($v_1$,$v_2$,$v_3$))--------$e[v]=false$\\
\hline
((LET v c'):c,e,($v_1$,$v_2$,$v_3$)) &  (c'++c,e[v]:e,($v_1$,$v_2$,$v_3$))\\
((ENDLET):c,v:e,($v_1$,$v_2$,$v_3$)) & = (c,e,($v_1$,$v_2$,$v_3$))\\
\hline
\hline
((TAILAPPLY1:c,e,(Clo [FIX c'] e',v,$\_$))) &  (c',v:(Clo [FIX c'] e'):e',($\_$,$\_$,$\_$))\\
((TAILAPPLY2):c,e,(Clo [FIX c'] e',$v_1$,$v_2$)) &  (c',$v_2$:$v_1$:(Clo [FIX c'] e'):e',($\_$,$\_$,$\_$))\\
((TAILAPPLY1:c,e,(Clo c' e',v,$\_$))) &  (c',v:e',($\_$,$\_$,$\_$))\\
((TAILAPPLY2):c,e,(Clo c' e',$v_1$,$v_2$)) &  (c',$v_2$:$v_1$:e',($\_$,$\_$,$\_$))\\
\hline
\hline
\end{tabular}
\end{table}

\subsection{Implementation and testing}
The codes are as followings. We test factorial functions and all the 6 more complicated testcases given in exercise2, the C-E-3R machine passes all theses test cases.\\

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


ce3rMachineStep ((TAILAPPLY1):c,e,(Clo [FIX c'] e',v,_)) = 
  Just (c',v:(Clo [FIX c'] e'):e',(UNCARE,UNCARE,UNCARE))
ce3rMachineStep ((TAILAPPLY2):c,e,(Clo [FIX c'] e',v1,v2)) = 
  Just (c',v2:v1:(Clo [FIX c'] e'):e',(UNCARE,UNCARE,UNCARE))


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
                s                        -> error "Evaluation Error Occurred!"
                


            
\end{code}


\section{Main}

\paragraph{}
In module \emph{Main}, we set \textbf{answerType} to be \textbf{TypeNull}, which will be printed out as ``\textbf{0}". With this pseudo-type, we can see the type of the CPS term more clearly. However, when putting it into practice, we can set it to any concrete type we want instead.

\begin{code}
module Main where

import qualified System.Environment
import Data.List
import System.IO

import qualified AbstractSyntax as S
import qualified StructuralOperationalSemantics as E
import qualified IntegerArithmetic as I
import qualified Typing as T


import qualified CESMachine as CES
import qualified DeBruijn as DB
import qualified NSWCAD as NDB

import qualified CPS as CPS
import qualified CE3RMachine as CE3R

main :: IO()
main = 
    do
      args <- System.Environment.getArgs
      let [sourceFile] = args
      source <- readFile sourceFile
      let tokens = S.scan source
      let term = S.parse tokens
      let dbterm = DB.toDeBruijn term
      putStrLn ("----Term----")
      putStrLn (show term)

      putStrLn ("----Type----")
      putStrLn (show (T.typeCheck term))
      
      putStrLn ("----Normal Form----")
      putStrLn (show (E.eval term))

      putStrLn ("----DBTerm----")
      putStrLn (show dbterm)
      putStrLn ("----Natural Semantics with Clo,Env and DB Term----")
      putStrLn (show (NDB.eval dbterm))

      putStrLn ("----CES Machine Code----")
      putStrLn (show (CES.compile dbterm) ++ "\n")

      putStrLn ("----CES Eval----")
      putStrLn (show (CES.eval dbterm) ++ "\n")    


      
      let answerType = S.TypeNull
      
      let cpsterm = CPS.toCPS answerType term
      putStrLn ("----CPS Form----")
      putStrLn (show cpsterm)
      putStrLn ("----CPS Type----")
      putStrLn (show (T.typeCheck cpsterm))
      
      let bodyterm = (S.App cpsterm (S.Abs "x" answerType (S.Var "x")))
      putStrLn ("---CPS Normal Form----")
      putStrLn (show (E.eval bodyterm))
        
       
      putStrLn ("----CE3R Machine Code----")
      putStrLn (show (CE3R.bcompile (DB.toDeBruijn bodyterm)))
      putStrLn ("----CE3R Machine----")
      putStrLn (show (CE3R.eval (DB.toDeBruijn bodyterm)))
      
\end{code}

\section{Test Cases}

\subsection{Test 1:\quad $\mathbf{iseven\text{ }7}$}
\verbatiminput{test1.TLBN}
\verbatiminput{test1.out}

\subsection{Test 2:\quad $\mathbf{isven\text{ }7}$ in fix term form}
\verbatiminput{test2.TLBN}
\verbatiminput{test2.out}

\subsection{Test 3:\quad calculate $\mathbf{2^3}$}
\verbatiminput{test3.TLBN}
\verbatiminput{test3.out}

\subsection{Test 4:\quad calculate $\mathbf{(3!)!}$}
\verbatiminput{test4.TLBN}
\verbatiminput{test4.out}

\subsection{Test 5}
\verbatiminput{test5.TLBN}
\verbatiminput{test5.out}


\begin{thebibliography}{9}

\bibitem{letBinding}
 Cormac Flanagan, Amr Sabry, Bruce F. Duda, and Matthias Felleisen.
 The essence of compiling with continuations.
 In \emph{PLDI's} 93 [36], pages 237-247, 1993.

\bibitem{typeCPS}
Albert R. Meyer and Mitchell Wand.
Continuation Semantics in Typed Lambda-Calculi (summary).
In \emph{Rohit Parikh, editor, logics of Programs}, vol. 224 of \emph{Lecture Notes in Computer Science}, pages 219-224, Springer-Verlag, 1985.



\end{thebibliography}

\end{document}

