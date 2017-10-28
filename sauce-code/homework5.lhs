\documentclass[11pt]{article}
%% Literate Haskell script intended for lhs2TeX.

%include polycode.fmt
%format `union` = "\cup"
%format alpha = "\alpha"
%format gamma = "\gamma"
%format capGamma = "\Gamma"
%format tau = "\tau"
%format tau1 = "\tau_{1}"
%format tau2 = "\tau_{2}"
%format tau11 = "\tau_{11}"
%format tau12 = "\tau_{12}"
%format t12 = "t_{12}"
%format t1 = "t_{1}"
%format t1' = "t_{1}^{\prime}"
%format t2 = "t_{2}"
%format t2' = "t_{2}^{\prime}"
%format t3 = "t_{3}"
%format nv1 = "nv_{1}"

\usepackage{fullpage}
\usepackage{mathpazo}
\usepackage{graphicx}
\usepackage[pdftex]{graphics}
\usepackage{color}
\usepackage[centertags]{amsmath}
\usepackage{amsfonts}
\usepackage{amssymb}
\usepackage{amsthm}
\usepackage{listings}
\usepackage{soul}
\usepackage{stmaryrd}

\title{Homework 5, CS 558}
\author{\textbf{By:} Yiming Yang}
\date{\textbf{Due:} November 22nd, 2011}
\begin{document}
\maketitle
\thispagestyle{empty}

\newpage

\section{Concrete syntax}
\begin{verbatim}
Type --> arr lpar Type comma Type rpar
       | bool
       | nat

Term --> identifier
       | abs lpar identifier colon Type fullstop Term rpar
       | app lpar Term comma Term rpar
       | true
       | false
       | if Term then Term else Term fi
       | 0
       | succ lpar Term rpar
       | pred lpar Term rpar
       | iszero lpar Term rpar
       | lpar Term rpar
\end{verbatim}

\newpage

\section{Prologue}

\paragraph*{}
Here, I imported |Char| library so that I can use the built-in functions within it to help scan the identifiers.

\begin{code}
module Main where

import qualified System.Environment
import Data.List
import IO
import Char

class LatexShow tau where
  latexShow :: tau -> String
\end{code}

\section{Abstract-syntactic preliminaries}
\subsection{Source types}
\begin{code}
data Type  =  TypeArrow Type Type
           |  TypeBool
           |  TypeNat
           deriving Eq

instance Show Type where
  show  (TypeArrow tau1 tau2)   =  "->(" ++ show tau1 ++ "," ++ show tau2 ++ ")"
  show  TypeBool                =  "Bool"
  show  TypeNat                 =  "Nat"

instance LatexShow Type where
  latexShow  (TypeArrow tau1 tau2)    =  "$\\rightarrow$ (" ++ latexShow tau1
                                         ++ ", " ++ latexShow tau2 ++ ")"
  latexShow  TypeBool                 =  "Bool"
  latexShow  TypeNat                  =  "Nat"
\end{code}
\subsection{Source terms}
\begin{code}
type Var  =  String

data Term  =  Var Var
           |  Abs Var Type Term
           |  App Term Term
           |  Tru
           |  Fls
           |  If Term Term Term
           |  Zero
           |  Succ Term
           |  Pred Term
           |  IsZero Term
           |  Fix Term
           deriving Eq

instance Show Term where
  show (Var x)        =  x
  show (Abs x tau t)  =  "abs(" ++ x ++ ":" ++ show tau ++ "." ++ show t ++ ")"
  show (App t1 t2)    =  "app(" ++ show t1  ++ "," ++ show t2 ++ ")"
  show Tru            =  "true"
  show Fls            =  "false"
  show (If t1 t2 t3)  =  "if " ++ show t1 ++ " then " ++ show t2 ++ " else " ++ show t3 ++ " fi"
  show Zero           =  "0"
  show (Succ t)       =  "succ(" ++ show t ++ ")"
  show (Pred t)       =  "pred(" ++ show t ++ ")"
  show (IsZero t)     =  "iszero(" ++ show t ++ ")"
  show (Fix t)        =  "fix(" ++ show t ++ ")"

instance LatexShow Term where
  latexShow  (Var x)            =  x
  latexShow  (Abs x tau t)      =  "$\\lambda$" ++ x ++ ": " ++ latexShow tau
                                   ++ ". " ++ latexShow t
  latexShow  (App t1 t2)        =  "$\\blacktriangleright$ (" ++ latexShow t1  ++ ", " ++ latexShow t2 ++ ")"
  latexShow  Tru                =  "true"
  latexShow  Fls                =  "false"
  latexShow  (If t1 t2 t3)      =  "if " ++ latexShow t1 ++ " then " ++ latexShow t2
                                   ++ " else " ++ latexShow t3 ++ " fi"
  latexShow Zero                =  "0"
  latexShow (Succ t)            =  "succ(" ++ latexShow t ++ ")"
  latexShow (Pred t)            =  "pred(" ++ latexShow t ++ ")"
  latexShow (IsZero t)          =  "iszero(" ++ latexShow t ++ ")"
  latexShow (Fix t)             =  "fix(" ++ latexShow t ++ ")"
\end{code}

\newpage
\subsection{Binding and free variables}

\paragraph*{}
The function |fv| is used for searching the free variables in a given |Term|. Besides the definition given in Chapter 5 of |TAPL|, I added the cases dealing with all the other kinds of terms appearing in the previous Chapters.
\begin{code}
fv :: Term -> [Var]
-- list of free variables of a term
fv (Var x)        =  [x]
fv (Abs x _ t1)   =  filter (/=x) (fv t1)
fv (App t1 t2)    =  (fv t1) ++ (fv t2)
fv (Succ t)       =  fv t
fv (Pred t)       =  fv t
fv (If t1 t2 t3)  =  (fv t1) ++ (fv t2) ++ (fv t3)
fv (IsZero t)     =  (fv t)
fv _              =  []
\end{code}

\subsection{Substitution}
Substitution: |subst x s t|, or $[x \mapsto s]t$ in Pierce, 
is the result of substituting |s| for |x| in |t|.
\ \\
\paragraph*{}
Since we are dealing with the simply-typed lambda calculus here, the definition for substitution is a little different from Definition 5.3.5 of |TAPL|. Especially, when dealing with the application case, we first compare the variable name being replaced (|x|) and the $\lambda$ declared variable (|y|). If they are the same, then the |x| in the $\lambda$ abstraction body is not a free variable, which cannot be applied by this substitution operation. Otherwise, we can just apply the substitution in the $\lambda$ abstraction body recursively.
\begin{code} 
subst :: Var -> Term -> Term -> Term
subst x s (Var v)        = if x == v then s else (Var v)
subst x s (Abs y tau t1) = 
  if x == y then
              Abs y tau t1
            else
              Abs y tau (subst x s t1)
subst x s (App t1 t2)    = App (subst x s t1) (subst x s t2)
subst x s (Succ t) = Succ (subst x s t)
subst x s (Pred t) = Pred (subst x s t)
subst x s (If t1 t2 t3) = If (subst x s t1) (subst x s t2) (subst x s t3)
subst x s (IsZero t) = IsZero (subst x s t)
subst x s t = t
\end{code}

\newpage
\section{Small-step evaluation relation}

\paragraph*{}
Here, due to the syntax, I added the $\lambda$-abstraction as one of the values, but it's not a numeric value.

\begin{code}
isValue :: Term -> Bool
isValue  (Abs _ _ _)  =  True
isValue  Tru          =  True
isValue  Fls          =  True
isValue  Zero         =  True
isValue  (Succ t)     =  isNumericValue t
isValue  _            =  False

isNumericValue :: Term -> Bool
isNumericValue  Zero      =  True
isNumericValue  (Succ t)  =  isNumericValue t
isNumericValue  _         =  False

\end{code}

\ \\
\paragraph*{}
For most evaluation rules, there is exactly one associated statement with each of them. However, for some others, I have to combine them together in one statment to advoid the pattern overlapping problem, which are shown below:

\begin{code}

eval1 :: Term -> Maybe Term
--one-step evaluation relation
-- E-IFTRUE
eval1  (If Tru t2 t3) = Just t2

-- E-IFFALSE
eval1  (If Fls t2 t3) = Just t3

-- E-IF
eval1  (If t1 t2 t3) =
  case eval1 t1 of
    Just t1' -> Just (If t1' t2 t3)
    Nothing -> Nothing

-- E-SUCC
eval1 (Succ t1) = 
  case eval1 t1 of
    Just t1' -> Just (Succ t1')
    Nothing  -> Nothing

-- E-PREDZERO
eval1 (Pred Zero) = Just Zero
\end{code}
\ \\
\paragraph*{}
For the term with the form \textbf{Pred (Succ nv1)}, the program first judges whether the term $nv_1$ is a numeric value. If so, then it applies rule \textbf{E-PredSucc} on it. Otherwise, the program treats \textbf{Succ nv1} as one whole term $t_1$, and applies rule \textbf{E-Pred} on it.
\ \\
\begin{code}

-- E-PREDSUCC
eval1 (Pred (Succ nv1)) = 
  if isNumericValue nv1
     then -- E-PREDSUCC
       Just nv1
     else -- E-PRED
       case eval1 (Succ nv1) of
         Just t1' -> Just (Pred t1')
         Nothing  -> Nothing

-- E-PRED
eval1 (Pred t1) = 
  case eval1 t1 of
    Just t1' -> Just (Pred t1')
    Nothing  -> Nothing

-- E-ISZEROZERO
eval1 (IsZero Zero) = Just Tru

\end{code}
\paragraph*{}
\textbf{Bug Fixed:} In the source file in Homework 4, I forgot the rule of \textbf{E-ISZERO}. What's more, when dealing with the situation that ``\textit{IsZero (Succ nv1)}", I didn't consider the case that \textbf{nv1} is not a numeric value but \textbf{Succ nv1} is a legal term, like ``\textit{Succ (Pred (Succ Zero))}". So I fix these two bugs in the following two function definition patterns.
\ \\
\begin{code}

-- E-ISZEROSUCC and E-ISZERO
eval1 (IsZero (Succ nv1)) = 
  if isNumericValue nv1 
     then Just Fls    -- E-ISZEROSUCC 
     else case eval1 (Succ nv1) of
               Just t' -> Just (IsZero t')     -- E-ISZERO
               Nothing -> Nothing

-- E-ISZERO
eval1 (IsZero t1) = 
  case eval1 t1 of
    Just t1' -> Just (IsZero t1')
    Nothing  -> Nothing

\end{code}

\paragraph*{}
\textbf{Bug Fixed:} Similarly, in Homework 4, I separated \textit{E-APPABS} from the other two evaluation rules on the application terms. However, I still didn't consider the case that \textbf{v2} is not a numeric value but a term in ``\textbf{App (Abs x tau1 t12) v2}". In this case, we should evaluation \textbf{v2} due to \textit{E-APP2}, rather than using \textit{E-APPABS} to do the substitution. Thus, I fix this bug in the following function definition pattern.
\ \\

\begin{code}

-- E-APPABS, E-APP1 and E-APP2
eval1 (App t1 t2) = 
  if isValue t1
     then if isValue t2
             then case t1 of
                    Abs x tau11 t12 -> Just (subst x t2 t12)    -- E-APPABS
                    _               -> Nothing
             else case eval1 t2 of
                    Just t2' -> Just (App t1 t2')    -- E-APP2
                    Nothing  -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (App t1' t2)
            Nothing  -> Nothing

\end{code}
\paragraph*{}
The following two function definition patterns are to implement the evaluation rules for \textbf{Fix} terms.
\ \\
\begin{code}

-- E-FIXBETA
eval1 (Fix (Abs x tau1 t2)) = 
  Just (subst x (Fix (Abs x tau1 t2)) t2) 

-- E-FIX
eval1 (Fix t1) = case eval1 t1 of
                   Just t1' -> Just (Fix t1')
                   Nothing  -> Nothing

-- All other cases
eval1 _ = Nothing

eval :: Term -> Term
--reduction to normal form
eval t =
  case eval1 t of
    Just t' -> eval t'
    Nothing -> t
\end{code}

\newpage
\section{Typing}
\begin{code}
data TypingContext  =  Empty
                    |  Bind TypingContext Var Type
                    deriving Eq
instance Show TypingContext where
  show Empty                  =  "<>"
  show (Bind capGamma x tau)  =  show capGamma ++ "," ++ x ++ ":" ++ show tau

contextLookup :: Var -> TypingContext -> Maybe Type
contextLookup x Empty  =  Nothing
contextLookup x (Bind capGamma y tau)
  | x == y      =  Just tau
  | otherwise   =  contextLookup x capGamma
\end{code}
\ \\
\paragraph*{}
For the typing rules, I constructed one statement for each typing rules, as shown below:
\ \\
\begin{code}
typing :: TypingContext -> Term -> Maybe Type
--the typing relation
-- T-VAR
typing capGamma (Var x) = contextLookup x capGamma

-- T-ABS
typing capGamma (Abs x tau t)
  = case (typing (Bind capGamma x tau) t) of
      Just tau2 -> Just (TypeArrow tau tau2)
      _         -> Nothing

-- T-APP
typing capGamma (App t1 t2) = case (typing capGamma t2) of
                                Just tau11 -> case (typing capGamma t1) of
                                                Just (TypeArrow tau11' tau12) -> if tau11' == tau11
                                                                                    then Just tau12
                                                                                    else Nothing
                                                _                             -> Nothing 
                                _          -> Nothing

-- T-TRUE
typing capGamma Tru = Just TypeBool

-- T-FALSE
typing capGamma Fls = Just TypeBool

-- T-IF
typing capGamma (If t1 t2 t3) =
  case (typing capGamma t1) of
    Just TypeBool -> case (typing capGamma t2) of
                       Just tau2 -> case (typing capGamma t3) of
                                      Just tau3 -> if (tau2 == tau3)
                                                      then Just tau3
                                                      else Nothing
                                      _         -> Nothing
                       _         -> Nothing
    _             -> Nothing 

-- T-SUCC
typing capGamma (Succ t1) = case (typing capGamma t1) of
                              Just TypeNat -> Just TypeNat
                              _            -> Nothing

-- T-PRED
typing capGamma (Pred t1) = case (typing capGamma t1) of
                              Just TypeNat -> Just TypeNat
                              _            -> Nothing

-- T-ZERO
typing capGamma Zero = Just TypeNat

-- T-ISZERO
typing capGamma (IsZero t1) = case (typing capGamma t1) of
                                Just TypeNat -> Just TypeBool
                                _            -> Nothing
\end{code}

\paragraph*{}
The following definition pattern for function \textit{typing} is to implement the typing rule for \textbf{Fix} terms.
\ \\
\begin{code}
-- T-FIX
typing capGamma (Fix t1) = case (typing capGamma t1) of
                             Just (TypeArrow tau1 tau1') -> if tau1 == tau1'
                                                               then Just tau1
                                                               else Nothing
                             Nothing                     -> Nothing

typeCheck :: Term -> Type
typeCheck t =
  case typing Empty t of
    Just tau -> tau
    _ -> error "type error"
\end{code}

\newpage
\section{Scanning and parsing}
\begin{code}
data Token  =  Identifier String
            |  AbsKeyword
            |  AppKeyword
            |  TrueKeyword
            |  FalseKeyword
            |  IfKeyword
            |  ThenKeyword
            |  ElseKeyword
            |  FiKeyword
            |  BoolKeyword
            |  ZeroKeyword
            |  SuccKeyword
            |  PredKeyword
            |  IsZeroKeyword
            |  NatKeyword
            |  FixKeyword
            |  LPar
            |  RPar
            |  Comma
            |  Colon
            |  FullStop
            |  RightArrow
            deriving (Eq, Show)
\end{code}
\ \\
\paragraph*{}
Here, I defined the function \textit{isIDword}, which uses the built-in function \textit{isAlphaNum} in \textbf{Char} package to detect whether the given character can be part of the identifier.
\paragraph*{}
Then, in the subfunction \textit{g} of \textit{scan}, it uses \textit{isIDword} to lex the identifiers. Here, since there is no definition of the identifier, I just make \textbf{[\underline{   }a-zA-Z0-9]+} as the identifier pattern.
\ \\
\begin{code}
isIDword :: Char -> Bool
isIDword c = isAlphaNum c || c == '_'

scan :: String -> [Token]
scan s = map makeKeyword (f s)
  where
    f :: [Char] -> [Token]
    f []               =  []
    f (' ':t)          =  f t
    f ('\t':t)         =  f t
    f ('\n':t)         =  f t
    f ('\r':t)         =  f t
    f ('-':'>':t)      =  RightArrow : f t
    f ('(':t)          =  LPar : f t
    f (')':t)          =  RPar : f t
    f (',':t)          =  Comma : f t
    f (':':t)          =  Colon : f t
    f ('.':t)          =  FullStop : f t
    f (h:t)            =  g [h] t

    g :: [Char] -> [Char] -> [Token]
    g c1 t = (Identifier (c1 ++ (takeWhile (isIDword) t))) : (f $ dropWhile (isIDword) t)
    
    makeKeyword :: Token -> Token
    makeKeyword (Identifier s)
      |  s == "abs"     =  AbsKeyword
      |  s == "app"     =  AppKeyword
      |  s == "true"    =  TrueKeyword
      |  s == "false"   =  FalseKeyword
      |  s == "if"      =  IfKeyword
      |  s == "then"    =  ThenKeyword
      |  s == "else"    =  ElseKeyword
      |  s == "fi"      =  FiKeyword
      |  s == "Bool"    =  BoolKeyword
      |  s == "0"       =  ZeroKeyword
      |  s == "succ"    =  SuccKeyword
      |  s == "pred"    =  PredKeyword
      |  s == "iszero"  =  IsZeroKeyword
      |  s == "Nat"     =  NatKeyword
      |  s == "fix"     =  FixKeyword
      |  otherwise      =  Identifier s
    makeKeyword t = t

parseType :: [Token] -> Maybe (Type, [Token])
parseType (RightArrow:LPar:tl) =
  case parseType tl of
    Just (tau1, Comma:tl') ->
      case parseType tl' of
        Just (tau2, RPar:tl'') -> Just (TypeArrow tau1 tau2, tl'')
        _ -> Nothing
    _ -> Nothing
parseType (BoolKeyword:tl) = Just (TypeBool, tl)
parseType (NatKeyword:tl) = Just (TypeNat, tl)

parseTerm :: [Token] -> Maybe (Term, [Token])
parseTerm (Identifier s:tl) = Just (Var s, tl)
parseTerm (AbsKeyword:h:tl)
  = if h == LPar
      then case parseTerm tl of
             Just (Var v, tl2) -> if (head tl2 == Colon)
                                    then case (parseType $ tail tl2) of
                                           Just (tau, tl3) -> if (head tl3 == FullStop)
                                                               then case (parseTerm $ tail tl3) of
                                                                      Just (t, tl4) -> 
                                                                        if (head tl4 == RPar)
                                                                           then
                                                                             Just (Abs v tau t, tail tl4)
                                                                           else Nothing
                                                                      Nothing       -> Nothing
                                                               else Nothing
                                           Nothing         -> Nothing
                                    else Nothing
             Nothing           -> Nothing
      else Nothing 
parseTerm (AppKeyword:h:tl)
  = if h == LPar
      then case parseTerm tl of
             Just (t1, tl2) -> if head tl2 == Comma
                                 then case (parseTerm $ tail tl2) of
                                           Just (t2, tl3) -> if head tl3 == RPar
                                                               then 
                                                                 Just (App t1 t2, tail tl3)
                                                               else Nothing
                                           Nothing        -> Nothing
                                 else Nothing
             Nothing        -> Nothing
      else Nothing
parseTerm (TrueKeyword:tl)  = Just (Tru, tl)
parseTerm (FalseKeyword:tl) = Just (Fls, tl)
parseTerm (IfKeyword:tl)    
  = case parseTerm tl of
      Just (t1, tl2) -> if (head tl2 == ThenKeyword)
                          then
                            case (parseTerm $ tail tl2) of
                              Just (t2, tl3) -> if (head tl3 == ElseKeyword)
                                                   then
                                                     case (parseTerm $ tail tl3) of
                                                       Just (t3, tl4) -> if (head tl4 == FiKeyword)
                                                                            then 
                                                                              Just (If t1 t2 t3,
                                                                                     tail tl4)
                                                                            else 
                                                                              Nothing
                                                       Nothing        -> Nothing
                                                   else Nothing
                              Nothing        -> Nothing
                          else Nothing
      Nothing        -> Nothing             
parseTerm (ZeroKeyword:tl)  = Just (Zero, tl)
parseTerm (SuccKeyword:h:tl)  = if h == LPar
                                   then case parseTerm tl of
                                          Just (t, tl2) -> if (head tl2 == RPar)
                                                              then Just (Succ t, tail tl2)
                                                              else Nothing
                                          Nothing       -> Nothing
                                   else Nothing
parseTerm (PredKeyword:h:tl)  = if h == LPar
                                   then case parseTerm tl of
                                          Just (t, tl2) -> if (head tl2 == RPar)
                                                              then Just (Pred t, tail tl2)
                                                              else Nothing
                                          Nothing       -> Nothing
                                   else Nothing
parseTerm (IsZeroKeyword:h:tl)= if h == LPar
                                   then case (parseTerm tl) of
                                          Just (t, tl2) -> if (head tl2 == RPar)
                                                              then Just (IsZero t, tail tl2)
                                                              else Nothing
                                          Nothing       -> Nothing
                                   else Nothing
parseTerm (LPar:tl) = case parseTerm tl of
                        Just (t, tl2) -> if head tl2 == RPar then Just (t, tail tl2)
                                                             else Nothing
                        Nothing       -> Nothing
parseTerm (FixKeyword:h:tl) = if h == LPar
                                 then case (parseTerm tl) of
                                        Just (t, tl2) -> if (head tl2 == RPar)
                                                            then Just (Fix t, tail tl2)
                                                            else Nothing
                                        Nothing       -> Nothing
                                 else Nothing

parse :: [Token] -> Term
parse ts =
  case parseTerm ts of
    Just (t, []) -> t
    Just (t, _) -> error "syntax error: spurious input at end"
    Nothing -> error "syntax error"
\end{code}

\newpage
\section{Epilogue}
\begin{code}
main :: IO ()
main =
    do
      args <- System.Environment.getArgs
      let [sourceFile] = args
      source <- readFile sourceFile
      let tokens = scan source
      let term = parse tokens
      putStrLn ("---Term:---")
      putStrLn (show term)
      --putStrLn (latexShow term)
      putStrLn ("---Type:---")
      putStrLn (show (typeCheck term))
      --putStrLn (latexShow (typeCheck term))
      putStrLn ("---Normal form:---")
      putStrLn (show (eval term))
      --putStrLn (latexShow (eval term))
\end{code}

\newpage
\section{Test Cases}
\subsection{\textit{iseven} function -- \textit{iseven 7}}
\paragraph{Souce Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  fix(
    abs(ie:->(Nat, Bool). 
      abs(x:Nat. 
        if iszero(x) 
           then true 
           else if iszero(pred(x)) 
                   then false 
                   else app(ie, pred(pred(x))) 
                fi 
        fi))), 
  succ(succ(succ(succ(succ(succ(succ(0))))))))
\end{lstlisting}
\ \\
\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(fix(abs(ie:-$>$(Nat,Bool).abs(x:Nat.if iszero(x) then true else if iszero(pred(x)) then false else app(ie,pred(pred(x))) fi fi))),succ(succ(succ(succ(succ(succ(succ(0))))))))
\ \\
---Type:---\\
Bool\\
---Normal form:---\\
false\\
\ \\
\subsection{\textit{leq} function -- \textit{leq 2 3}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  app(
    fix(abs(f:->(Nat, ->(Nat, Bool)).
	  abs(x:Nat.
	    abs(y:Nat.
	      if iszero(x)
		 then true
		 else if iszero(y)
			 then false
			 else app(app(f, pred(x)), pred(y)) 
		      fi
	      fi)))),
    succ(succ(0))),
  succ(succ(succ(0))))
\end{lstlisting}
\ \\
\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(app(fix(abs(f:-$>$(Nat,-$>$(Nat,Bool)).abs(x:Nat.abs(y:Nat.if iszero(x) then true else if iszero(y) then false else app(app(f,pred(x)),pred(y)) fi fi)))),succ(succ(0))),succ(succ(succ(0))))
\ \\
---Type:---\\
Bool\\
---Normal form:---\\
true\\
\ \\
\subsection{\textit{eq} function -- \textit{eq 2 3}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  app(
    fix(abs(f:->(Nat, ->(Nat, Bool)).
          abs(x:Nat.
	    abs(y:Nat.
		if iszero(x)
		    then if iszero(y)
			    then true
			    else false
		         fi
		    else if iszero(y)
			    then false
			    else app(app(f, pred(x)), pred(y))
		         fi
		fi)))),
    succ(succ(0))),
  succ(succ(succ(0))))
\end{lstlisting}
\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(app(fix(abs(f:-$>$(Nat,-$>$(Nat,Bool)).abs(x:Nat.abs(y:Nat.if iszero(x) then if iszero(y) then true else false fi else if iszero(y) then false else app(app(f,pred(x)),pred(y)) fi fi)))),succ(succ(0))),succ(succ(succ(0))))
\ \\
---Type:---\\
Bool\\
---Normal form:---\\
false\\
\ \\

\subsection{\textbf{plus} function -- \textit{plus 2 3}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  app(
    fix(abs(f:->(Nat, ->(Nat, Nat)).
	   abs(x:Nat.
	       abs(y:Nat.
		   if iszero(x) 
                      then y
		      else app(app(f, pred(x)), succ(y))
	           fi)))),
    succ(succ(0))),
  succ(succ(succ(0))))
\end{lstlisting}

\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(app(fix(abs(f:-$>$(Nat,-$>$(Nat,Nat)).abs(x:Nat.abs(y:Nat.if iszero(x) then y else app(app(f,pred(x)),succ(y)) fi)))),succ(succ(0))),succ(succ(succ(0))))
\ \\
---Type:---\\
Nat\\
---Normal form:---\\
succ(succ(succ(succ(succ(0)))))\\
\ \\
\subsection{\textit{times} function -- \textit{times 2 3}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  app(
    fix(abs(f:->(Nat, ->(Nat, Nat)). 
           abs(x:Nat. 
              abs(y:Nat.
		  if iszero(x)
		     then 0
		     else app(
                            app(
			      fix(abs(f1:->(Nat, ->(Nat, Nat)). 
                                     abs(x1:Nat. 
                                        abs(y1:Nat.
					    if iszero(x1)
					       then y1
					       else app(
                                                      app(
                                                        f1, 
                                                        pred(x1)), 
                                                      succ(y1)) 
					    fi)))), 
                              app(app(f, pred(x)), y)), 
                          y)	     
	          fi)))),
    succ(succ(0))), 
  succ(succ(succ(0))))
\end{lstlisting}
\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(app(fix(abs(f:-$>$(Nat,-$>$(Nat,Nat)).abs(x:Nat.abs(y:Nat.if iszero(x) then 0 else app(app(fix(abs(f1:-$>$(Nat,-$>$(Nat,Nat)).abs(x1:Nat.abs(y1:Nat.if iszero(x1) then y1 else app(app(f1,pred(x1)),succ(y1)) fi)))),app(app(f,pred(x)),y)),y) fi)))),succ(succ(0))),succ(succ(succ(0))))
\ \\
---Type:---\\
Nat\\
---Normal form:---\\
succ(succ(succ(succ(succ(succ(0))))))\\
\ \\
\subsection{\texit{exp} function -- \textit{exp 2 3}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  app(
    fix(
      abs(f:->(Nat, ->(Nat, Nat)).
         abs(x:Nat.
            abs(y:Nat.
               if iszero(y)
                  then succ(0)
                  else app(
                         app(
                           fix(
                             abs(f1:->(Nat, ->(Nat, Nat)). 
                                abs(x1:Nat. 
                                   abs(y1:Nat.
		                      if iszero(x1)
		                         then 0
		                         else 
                                           app(
                                             app(
			                       fix(
                                                 abs(f2:->(Nat, ->(Nat, Nat)). 
                                                    abs(x2:Nat. 
                                                       abs(y2:Nat.
					                  if iszero(x2)
					                     then y2
					                     else 
                                                               app(
                                                                 app(
                                                                   f2, 
                                                                   pred(x2)), 
                                                                 succ(y2)) 
					                  fi)))), 
                                               app(
                                                 app(
                                                   f1, 
                                                   pred(x1)), 
                                                 y1)), 
                                             y1)	     
	                              fi)))), 
                           x), 
                         app(app(f, x), pred(y)))
               fi)))),
    succ(succ(0))),
  succ(succ(succ(0))))
\end{lstlisting}
\paragraph{Runtime Result:}
\ \\
\par
---Term:---
\par
app(app(fix(abs(f:-$>$(Nat,-$>$(Nat,Nat)).abs(x:Nat.abs(y:Nat.if iszero(y) then succ(0) else app(app(fix(abs(f1:-$>$(Nat,-$>$(Nat,Nat)).abs(x1:Nat.abs(y1:Nat.if iszero(x1) then 0 else app(app(fix(abs(f2:-$>$(Nat,-$>$(Nat,Nat)).abs(x2:Nat.abs(y2:Nat.if iszero(x2) then y2 else app(app(f2,pred(x2)),succ(y2)) fi)))),app(app(f1,pred(x1)),y1)),y1) fi)))),x),app(app(f,x),pred(y))) fi)))),succ(succ(0))),succ(succ(succ(0))))
\ \\
---Type:---\\
Nat\\
---Normal form:---\\
succ(succ(succ(succ(succ(succ(succ(succ(0))))))))\\
\ \\
\subsection{\textit{fact} function -- \textit{fact (fact 3)}}
\paragraph{Source Code:}
\ \\
\begin{lstlisting}[language=C]
app(
  fix(
    abs(f:->(Nat, Nat).
      abs(n:Nat.
         if iszero(n)
            then succ(0)
            else 
              app(
                app(
                  fix(
                    abs(f1:->(Nat, ->(Nat, Nat)). 
                       abs(x1:Nat. 
                          abs(y1:Nat.
		             if iszero(x1)
		                then 0
		                else 
                                  app(
                                    app(
			              fix(
                                        abs(f2:->(Nat, ->(Nat, Nat)). 
                                           abs(x2:Nat. 
                                              abs(y2:Nat.
                                                 if iszero(x2)
                                                    then y2
                                                    else 
                                                      app(
                                                        app(
                                                          f2, 
                                                          pred(x2)), 
                                                        succ(y2)) 
                                                 fi)))), 
                                      app(app(f1, pred(x1)), y1)), 
                                    y1)	     
	                     fi)))), 
                  n), 
                app(f, pred(n)))
         fi))),
  app(
    fix(
      abs(g:->(Nat, Nat).
         abs(n2:Nat.
            if iszero(n2)
               then succ(0)
               else 
                 app(
                   app(
                     fix(
                       abs(g1:->(Nat, ->(Nat, Nat)). 
                          abs(a1:Nat. 
                             abs(b1:Nat.
		                if iszero(a1)
		                   then 0
		                   else 
                                     app(
                                       app(
			                 fix(
                                           abs(g2:->(Nat, ->(Nat, Nat)). 
                                              abs(a2:Nat. 
                                                 abs(b2:Nat.
					            if iszero(a2)
					               then b2
					               else 
                                                         app(
                                                           app(
                                                             g2, 
                                                             pred(a2)), 
                                                           succ(b2)) 
					            fi)))), 
                                         app(app(g1, pred(a1)), b1)), 
                                       b1)	     
	                        fi)))), 
                     n2), 
                   app(g, pred(n2)))
            fi))),
    succ(succ(succ(0)))))
\end{lstlisting}
\paragraph{Runtime Result:}
\ \\
\begin{figure}[!hbp]
\centering
\scalebox{0.33}{\includegraphics{fact_runtime_result.png}}
\caption{Runtime Result of \textit{fact (fact 3)}}\label{Fig 1}
\end{figure}
\paragraph{Some More Explanation}
\ \\
\par
Since the normal form of \textit{fact (fact 3)} is quite long, I write a C++ program \textit{getValue} to calculate the number of \textbf{succ} within the returned normal form. The source code of \textit{getValue.cc} is shown below:
\ \\
\begin{lstlisting}[language=C++]
/*------------- getValue.cc--------------------*/
\#include <iostream>
\#include <fstream>
\#include <string>
using namespace std;

// This is used to read the given file content.
string readDataFromFile(string filename) {
    ifstream fin(filename.c_str());
    string res;
    getline(fin, res);

    return res;
}

// This is used to calculate the number of ``succ".
int calcSucc(string n) {
    int res = 0;
    size_t pos;

    while (n.at(0) != '0') {
        pos = n.find("succ(");
        n = n.substr(pos+5);
        ++res;
    }

    return res;
}

int main(int argc, char* argv[]) {
    string filename(argv[1]);
    string n;
    int value;

    n = readDataFromFile(filename);


    value = calcSucc(n);

    cout << value << endl;

    return 0;
}
\end{lstlisting}
\ \\
\par
Therefore, I can just use the command
\begin{center}
	./homework5 fact\_example.TLBN $>$ result\_fact\\
\end{center}
to generate the result file. Then I just remove all the content except the normal form, which is just one line result. After that, I can run the \textit{getValue} program to calculate the decimal value of this form using the following command:
\begin{center}
	./getValue result\_fact\\
\end{center}
\par
And we can see from the figure above, the result is 720, which is correct.

\ \\
\end{document}
