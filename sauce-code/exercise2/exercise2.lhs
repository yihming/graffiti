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
%format kappa = "\kappa"
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

\usepackage{vmargin}
\setpapersize{USletter}
\setmarginsrb{1.1in}{1.0in}{1.1in}{0.6in}{0.3in}{0.3in}{0.0in}{0.2in}
\parindent 0in  \setlength{\parindent}{0in}  \setlength{\parskip}{1ex}

\usepackage{epsfig}
\usepackage{rotating}

\usepackage{mathpazo,amsmath,amssymb}
\title{Exercise 2, CS 555}
\author{\textbf{By:} Sauce Code Team (Dandan Mo, Qi Lu, Yiming Yang)}
\date{\textbf{Due:} March 21st, 2012}
\begin{document}
	\maketitle
	\thispagestyle{empty}
	\newpage

\section{Enriching the Core Lambda Language}

\subsection{Lexer and Parser}
1): We add ``LET",``IN",``END",``FIX" to the Token and add corresponding show Token functions
\begin{code}
data Token = ARROW
	| LPAR
	| COMMA
	| RPAR
	| BOOL
	| INT
	| ABS
	| COLON
	| FULLSTOP
	| APP
	| TRUE
	| FALSE
	| IF
	| THEN
	| ELSE
	| FI
	| PLUS
	| SUB
	| MUL
	| DIV
	| NAND
	| EQUAL
	| LT_keyword
	| ID String
	| NUM String
	| LET
	| IN
	| END
	| FIX
	deriving Eq

instance Show Token where
	show ARROW = "->"
	show LPAR = "("
	show COMMA = ","
	show RPAR = ")"
	show BOOL = "Bool"
	show INT = "Int"
	show ABS = "abs"
	show COLON = ":"
	show FULLSTOP = "."
	show APP = "app"
	show TRUE = "true"
	show FALSE = "false"
	show IF = "if"
	show THEN = "then"
	show ELSE = "else"
	show FI = "fi"
	show PLUS = "+"
	show SUB = "-"
	show MUL = "*"
	show DIV = "/"
	show NAND = "^"
	show EQUAL = "="
	show LT_keyword = "<"
	show (ID id) = id
	show (NUM num) = num
	show (LET) = "let"
	show (IN) = "in"
	show (END) = "end"
	show (FIX) = "fix"

\end{code}

For the Token identifier and decimal number, we use regular expression to recognize them, so we have two corresponding subscan function to deal with them. When we get a identifier, we check if it belongs to the keywords, if so we get the corresponding token, otherwise we get an id.\\

\begin{code}
--reguar expression
ex_num = mkRegex "(0|[1-9][0-9]*)"
ex_id = mkRegex "([a-zA-Z][a-zA-Z0-9_]*)"

--subscan for id and keywords
subscan1 :: String -> Maybe ([Token],String)
subscan1 str = case (matchRegexAll ex_id str) of 
		Just (a1,a2,a3,a4) -> case a1 of
					"" -> case a2 of
						"Bool" -> Just ([BOOL],a3)
						"Int" -> Just ([INT],a3)
						"abs" -> Just ([ABS],a3)
						"app" -> Just ([APP],a3)
						"true" -> Just ([TRUE],a3)
						"false" -> Just ([FALSE],a3)
						"if" -> Just ([IF],a3)
						"then" -> Just ([THEN],a3)
						"else" -> Just ([ELSE],a3)
						"fix" -> Just ([FIX],a3)
						"fi" -> Just ([FI],a3)
						"let" -> Just ([LET],a3)
						"in" -> Just ([IN],a3)
						"end" -> Just ([END],a3)
						_ -> Just ([ID a2],a3)
					_  -> Nothing
		Nothing -> Nothing

--subscan for num
subscan2 :: String -> Maybe ([Token],String)
subscan2 str = case (matchRegexAll ex_num str) of
		Just (a1,a2,a3,a4) -> case a1 of
					"" -> Just ([NUM a2],a3)
					_  -> Nothing
		Nothing -> Nothing
\end{code}

Function scan takes an input string and returns a list tokens. If unexpected symbols exists,or the input string cannot mactch any defined token, the function reports errors and the program stops at the lexer level.\\


\begin{code}
--lexer
scan :: String -> [Token]
scan "" = []

--white spase
scan (' ':xs) = scan xs
scan ('\t':xs) = scan xs
scan ('\n':xs) = scan xs

--symbol
scan (':':xs) = [COLON] ++ scan xs
scan ('-':'>':xs) = [ARROW] ++ scan xs
scan ('(':xs) = [LPAR] ++ scan xs
scan (',':xs) = [COMMA] ++ scan xs
scan (')':xs) = [RPAR] ++ scan xs
scan ('.':xs) = [FULLSTOP] ++ scan xs
--special operator
scan ('+':xs) = [PLUS] ++ scan xs
scan ('-':xs) = [SUB] ++ scan xs
scan ('*':xs) = [MUL] ++ scan xs
scan ('/':xs) = [DIV] ++ scan xs
scan ('^':xs) = [NAND] ++ scan xs
scan ('=':xs) = [EQUAL] ++ scan xs
scan ('<':xs) = [LT_keyword] ++ scan xs 

--id,keywords and num
scan str = case subscan1 str of
		Nothing -> case subscan2 str of
				Nothing -> error "[Scan]err: unexpected symbols!"
				Just (tok,xs) -> tok ++ scan xs
		Just (tok,xs) -> tok ++ scan xs
str str = error "[Scan]err: unexpected symbols!"
 
\end{code}

2):Parser takes a list of tokens, returns a term. We define the two data structures Type and Term, and two functions parseType and parseTerm to deal with them.\\
parseType function returns a matched Type and the remaining tokens, parseTerm function returns a matched Term and the remaining tokens.\\
We add Term ``Let Var Term Term" and ``Fix Term" and their related show functions.\\
Data structure:\\
\begin{code}
data Type = TypeArrow Type Type
	| TypeBool
	| TypeInt
	deriving Eq

instance Show Type where
	show (TypeArrow tau1 tau2) = "->(" ++ show tau1 ++ "," ++ show tau2 ++ ")"
	show TypeBool = "Bool"
	show TypeInt = "Int"

type Var = String
data Term = Var Var
	| Abs Var Type Term
	| App Term Term
	| Tru
	| Fls
	| If Term Term Term
	| IntConst Integer
	| IntAdd Term Term
	| IntSub Term Term
	| IntMul Term Term
	| IntDiv Term Term
	| IntNand Term Term
	| IntEq Term Term
	| IntLt Term Term
	| Fix Term
	| Let Var Term Term
	deriving Eq

instance Show Term where
	show (Var x) = x
	show (Abs x tau t) = "abs(" ++ x ++ ":" ++ show tau ++ "." ++ show t ++ ")"
	show (App t1 t2) = "app(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show Tru = "true"
	show Fls = "false"
	show (If t1 t2 t3) = "if " ++ show t1 ++ " then " ++ show t2 ++ " else " ++ show t3 ++ " fi"
	show (IntConst n) = show n
	show (IntAdd t1 t2) = "+(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (IntSub t1 t2) = "-(" ++ show t1 ++ ","  ++ show t2 ++ ")"
	show (IntMul t1 t2) = "*(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (IntDiv t1 t2) = "/(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (IntNand t1 t2) = "^(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (IntEq t1 t2) = "=(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (IntLt t1 t2) = "<(" ++ show t1 ++ "," ++ show t2 ++ ")"
	show (Fix t) = "fix " ++ show t
        show (Let v t1 t2) = "let " ++ v ++ "=" ++ show t1 ++ "in" ++ show t2
\end{code}

Function parseType, parseTerm and parse:\\
In parseTerm, we add new pattern for let-in-end expression and fix-expression.\\
\begin{code}
--parser

--type parser
parseType :: [Token] -> Maybe (Type,[Token])
parseType (BOOL:ty) = Just (TypeBool,ty)
parseType (INT:ty) = Just (TypeInt,ty)
parseType (RPAR:ty) = parseType ty
parseType (COMMA:ty) = parseType ty
parseType (ARROW:LPAR:ty) = 
	case parseType ty of
		Just (t1,(COMMA:tl)) -> case parseType tl of
						Just (t2,(RPAR:tll)) -> Just ((TypeArrow t1 t2),tll)
						Nothing -> Nothing
				Nothing -> Nothing
parseType tok = error "[P]err: type parsing error!"

--term parser
parseTerm :: [Token] -> Maybe (Term,[Token])
----id
parseTerm ((ID id):ts) = Just ((Var id),ts)
----num
parseTerm ((NUM num):ts) = Just ((IntConst (read num::Integer)),ts)
----symbol
--parseTerm (COMMA:ts) = parseTerm ts
--parseTerm (COLON:ts) = parseTerm ts
--parseTerm (RPAR:ts) = parseTerm ts
--parseTerm (FULLSTOP:ts) = parseTerm ts
----keyword
parseTerm (THEN:ts) = parseTerm ts
parseTerm (ELSE:ts) = parseTerm ts
parseTerm (FI:ts) = parseTerm ts
parseTerm (TRUE:ts) = Just (Tru,ts)
parseTerm (FALSE:ts) = Just (Fls,ts)
----(term)
parseTerm (LPAR:ts) = case parseTerm ts of
			Just (t,(RPAR:tl)) -> Just (t,tl)
			Nothing -> Nothing
			_ -> error "[P]err: t is not a term in the (t)"
----op
parseTerm (PLUS:LPAR:ts) = 
	case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntAdd t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: plus term"
		Nothing -> Nothing
		_ -> error "[P]err: plus term"
parseTerm (SUB:LPAR:ts) = 
	case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntSub t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: sub term"
		Nothing -> Nothing
		_ -> error "[P]err: sub term"
parseTerm (MUL:LPAR:ts) = 
	case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntMul t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: mul term"
		Nothing -> Nothing
		_ -> error "[P]err: mul term"
parseTerm (DIV:LPAR:ts) =
	 case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntDiv t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: div term"
		Nothing -> Nothing
		_ -> error "[P]err: div term"
parseTerm (NAND:LPAR:ts) = 
	case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntNand t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: nand term"
		Nothing -> Nothing
		_ -> error "[P]err: nand term"
parseTerm (EQUAL:LPAR:ts) =
	 case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntEq t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: eq term"
		Nothing -> Nothing
		_ -> error "[P]err: eq term"
parseTerm (LT_keyword:LPAR:ts) = 
	case parseTerm ts of
		Just (t1,(COMMA:tl)) -> case parseTerm tl of
						Just (t2,(RPAR:tll)) -> Just ((IntLt t1 t2),tll)
						Nothing -> Nothing
						_ -> error "[P]err: lt term"
		Nothing -> Nothing
		_ -> error "[P]err: lt term"
----if-then-else
parseTerm (IF:ts) = 
	case parseTerm ts of
		Just (t1,(THEN:tl)) -> case parseTerm tl of
					Just (t2,(ELSE:tll)) -> case parseTerm tll of
								Just (t3,(FI:tn)) -> Just((If t1 t2 t3),tn)
								Nothing -> Nothing
								_ -> error "[P]err: if term"
					Nothing -> Nothing
					_ -> error "[P]err: if term"
		Nothing -> Nothing
		_ -> error "[P]err: if term"

----fix
parseTerm (FIX:LPAR:ts) = case parseTerm ts of
				Just (t1,(RPAR:tl)) -> Just ((Fix t1),tl)
				Nothing -> Nothing
				_ -> error "[P]err: fix term"


----abs
parseTerm (ABS:LPAR:(ID id):COLON:ts) = 
	case parseType ts of
		Just (ty,(FULLSTOP:tl)) -> case parseTerm tl of
						Just (t,(RPAR:tll)) -> Just ((Abs id ty t),tll)
						Nothing -> Nothing
						_ -> error "[P]err: abs term"
		 Nothing -> Nothing
		 _ -> error "[P]err: abs term"
----app
parseTerm (APP:LPAR:ts) = case parseTerm ts of
				Just (t1,(COMMA:tl)) -> case parseTerm tl of
							  Just (t2,(RPAR:tll)) -> Just ((App t1 t2),tll)
							  Nothing -> Nothing
							  _ -> error "[P]err: app term"
				Nothing -> Nothing
				_ -> error "[P]err: app term"
----let-in-end
parseTerm (LET:(ID id):EQUAL:ts) = case parseTerm ts of
				      Just (t1,(IN:tl)) -> case parseTerm tl of
						             Just (t2,(END:tll)) -> Just ((Let id t1 t2),tll)
							     Nothing -> Nothing
							     _ -> error "[P]err: let term"
				      Nothing -> Nothing
				      _ -> error "[P]err: let term"

----otherwise
parseTerm tok = Nothing

--parser
parse :: [Token] -> Term
parse t = 
	  case parseTerm t of
		Just (x,t) -> case t of 
				[] -> x
				_ -> error "parsing error!"
		Nothing -> error "parsing error!"


\end{code}

If the input string can't match any defined Term, function parser reports an error and the program stops at the parser level.\\

\subsection{Auxiliary Functions}

\paragraph{}
The implementation of the auxiliary functions are in the AbstractSyntax module.
\ \\
\begin{code}
-- list of free variables of a term
fv :: Term -> [Var]
fv (Var x) = [x]
fv (Abs x _ t) = filter (/=x) (fv t)
fv (App t1 t2) = (fv t1) ++ (fv t2)
fv (If t1 t2 t3) = (fv t1) ++ (fv t2) ++ (fv t3)
fv (IntAdd t1 t2) = (fv t1) ++ (fv t2)
fv (IntSub t1 t2) = (fv t1) ++ (fv t2)
fv (IntMul t1 t2) = (fv t1) ++ (fv t2)
fv (IntDiv t1 t2) = (fv t1) ++ (fv t2)
fv (IntNand t1 t2) = (fv t1) ++ (fv t2)
fv (IntEq t1 t2) = (fv t1) ++ (fv t2)
fv (IntLt t1 t2) = (fv t1) ++ (fv t2)
fv (Fix t) = fv t
fv (Let x t1 t2) = (fv t1) ++ (filter (/=x) (fv t2))
fv _ = []


subst :: Var -> Term -> Term -> Term
subst x s (Var v) = if x == v then s else (Var v)
subst x s (Abs y tau t1) = 
  if x == y then
              Abs y tau t1
            else 
              Abs y tau (subst x s t1)
subst x s (App t1 t2) = App (subst x s t1) (subst x s t2)
subst x s (If t1 t2 t3) = If (subst x s t1) (subst x s t2) (subst x s t3)
subst x s (IntAdd t1 t2) = IntAdd (subst x s t1) (subst x s t2)
subst x s (IntSub t1 t2) = IntSub (subst x s t1) (subst x s t2)
subst x s (IntMul t1 t2) = IntMul (subst x s t1) (subst x s t2)
subst x s (IntDiv t1 t2) = IntDiv (subst x s t1) (subst x s t2)
subst x s (IntNand t1 t2) = IntNand (subst x s t1) (subst x s t2)
subst x s (IntEq t1 t2) = IntEq (subst x s t1) (subst x s t2)
subst x s (IntLt t1 t2) = IntLt (subst x s t1) (subst x s t2)
subst x s (Fix t) = Fix (subst x s t)
subst x s (Let y t1 t2) = Let y (subst x s t1) (subst x s t2)
subst x s t = t

isValue :: Term -> Bool
isValue (Abs _ _ _) = True
isValue Tru = True
isValue Fls = True
isValue (IntConst _) = True
isValue _ = False
\end{code}
\ \\
\subsection{Arithmetic}

\begin{code}

module IntegerArithmetic where
import Data.Bits

intRestrictRangeAddMul :: Integer -> Integer
intRestrictRangeAddMul m = m `mod` 4294967296

intAdd :: Integer -> Integer -> Integer
intAdd m n = intRestrictRangeAddMul (m + n)

intSub :: Integer -> Integer -> Integer
intSub m n = m - n

intMul :: Integer -> Integer -> Integer
intMul m n = intRestrictRangeAddMul (m * n)

intDiv :: Integer -> Integer -> Integer
intDiv m n = if n == 0 then error "integer division by zero" else m `div` n

intNand :: Integer -> Integer -> Integer
intNand m n = complement (m .&. n)

intEq :: Integer -> Integer -> Bool
intEq m n = m == n

intLt :: Integer -> Integer -> Bool
intLt m n = m < n

\end{code}

\subsection{Structural Operational Semantics}

\subsubsection{Formal Rules}

\paragraph{}
Formally stating the rules that give the structural operational semantics of the core lambda language, the rules are listed below:
\[
	\text{if true then } t_2 \text{ else } t_3 \rightarrow t_2 \text{\quad (\textsc{E-IfTrue})}
\]
\ \\
\[
        \text{if false then } t_2 \text{ else } t_3 \rightarrow t_3 \text{(\quad \textsc{E-IfFalse})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{\text{if } t_1 \text{ then } t_2 \text{ else } t_3 \rightarrow \text{if } t'_1 \text{ then } t_2 \text{ else} t_3} \text{\quad (\textsc{E-If})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{t_1 \text{\ } t_2 \rightarrow t'_1 \text{\ } t_2}\text{\quad {\textsc{E-App1}}}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{t_1 \text{\ } t_2 \rightarrow t_1 \text{\ } t'_2}\text{\quad {\textsc{E-App2}}}
\]
\ \\
\[
	(\lambda x: T_{11}.t_{12})v_2 \rightarrow [ x \mapsto v_2 ] _{12} \text{\quad (\textsc{E-AppAbs})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{+(t_1, t_2) \rightarrow +(t'_1, t_2)} \text{\quad (\textsc{E-IntAdd1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{+(t_1, t_2) \rightarrow +(t_1, t'_2)} \text{\quad (\textsc{E-IntAdd2})}
\]
\ \\
\[
	+(v_1, v_2) \rightarrow v_1 \widetilde{+} v_2 \text{\quad (\textsc{E-IntAppAdd})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{\-(t_1, t_2) \rightarrow -(t'_1, t_2)} \text{\quad (\textsc{E-IntSub1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{\-(t_1, t_2) \rightarrow -(t_1, t'_2)} \text{\quad (\textsc{E-IntSub2})}
\]
\ \\
\[
	-(v_1, v_2) \rightarrow v_1 \widetilde{-} v_2 \text{\quad (\textsc{E-AppIntSub})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{*(t_1, t_2) \rightarrow *(t'_1, t_2)} \text{\quad (\textsc{E-IntMul1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{*(t_1, t_2) \rightarrow *(t_1, t'_2)} \text{\quad (\textsc{E-IntMul2})}
\]
\ \\
\[
	*(v_1, v_2) \rightarrow v_1 \widetilde{*} v_2 \text{\quad (\textsc{E-AppIntMul})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{/(t_1, t_2) \rightarrow /(t'_1, t_2)} \text{\quad (\textsc{E-IntDiv1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{/(t_1, t_2) \rightarrow /(t_1, t'_2)} \text{\quad (\textsc{E-IntDiv2})}
\]
\ \\
\[
	/(v_1, v_2) \rightarrow v_1 \widetilde{/} v_2 \text{\quad (\textsc{E-AppIntDiv})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{\wedge(t_1, t_2) \rightarrow \wedge(t'_1, t_2)} \text{\quad (\textsc{E-IntNand1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{\wedge(t_1, t_2) \rightarrow \wedge(t_1, t'_2)} \text{\quad (\textsc{E-IntNand2})}
\]
\ \\
\[
	\wedge(v_1, v_2) \rightarrow v_1 \widetilde{\wedge} v_2 \text{\quad (\textsc{E-AppIntNand})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{=(t_1, t_2) \rightarrow =(t'_1, t_2)} \text{\quad (\textsc{E-IntEq1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{=(t_1, t_2) \rightarrow =(t_1, t'_2)} \text{\quad (\textsc{E-IntEq2})}
\]
\ \\
\[
	=(v_1, v_2) \rightarrow v_1 \widetilde{\equiv} v_2 \text{\quad (\textsc{E-AppIntEq})}
\]
\ \\
\[
	\frac{t_1 \rightarrow t'_1}{<(t_1, t_2) \rightarrow <(t'_1, t_2)} \text{\quad (\textsc{E-IntLt1})}
\]
\ \\
\[
	\frac{t_2 \rightarrow t'_2}{<(t_1, t_2) \rightarrow <(t_1, t'_2)} \text{\quad (\textsc{E-IntLt2})}
\]
\ \\
\[
	<(v_1, v_2) \rightarrow v_1 \widetilde{<} v_2 \text{\quad (\textsc{E-AppIntLt})}
\]
\ \\
where
\begin{align*}
	\widetilde{+} &\text{\quad \quad is the funtion that adds the two arguments and returns an Integer result}\\
	\widetilde{-} &\text{\quad \quad is the function that subtracts the two arguments and returns an Integer result}\\
	\widetilde{*} &\text{\quad \quad is the function that times the two arguments and returns an Integer result}\\
	\widetilde{/} &\text{\quad \quad is the function that divides the two arguments and returns an Integer result}\\
	\widetilde{\wedge} &\text{\quad \quad is the function that gets the nand result of the two arguments and returns it }\\
	\widetilde{\equiv} &\text{\quad \quad is the function that judges whether the two values are equal. If so, returns \textbf{true}, otherwise \textbf{false}}\\
	\widetilde{<} &\text{\quad \quad is the function that judges whether the first value is less than the second one.} \\
        &\text{\quad \quad \ If so, returns \textbf{true}, otherwise \textbf{false}}
\end{align*}

\[
	\text{fix }(\lambda x:T_1.\text{\ }t_2) \rightarrow [x\mapsto \text{fix } (\lambda x:T_1.\text{\ }t_2)]t_2 \text{\quad (\textsc{E-FixBeta})}
\]

\[
	\frac{t_1 \rightarrow t'_1}{\text{fix }t_1 \rightarrow \text{fix } t'_1} \text{\quad (\textsc{E-Fix})}
\]

\[
	\text{let } x=v_1 \text{ in } t_2 \rightarrow [x\mapsto v_1] t_2 \text{\quad (\textsc{E-LetV})}
\]

\[
	\frac{t_1 \rightarrow t'_1}{\text{let } x=t_1 \text{ in } t_2 \rightarrow \text{let } x=t'_1 \text{ in } t_2} \text{\quad (\textsc{E-Let})}
\]

\subsubsection{Haskell Implementation}
\begin{code}

module StructuralOperationalSemantics where
import List
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

eval1 (S.IntAdd t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> Just (S.IntConst (I.intAdd n1 n2))
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntAdd t1 t2')
                                     Nothing  -> Nothing
            _           -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntAdd t1' t2)
            Nothing  -> Nothing

eval1 (S.IntSub t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> Just (S.IntConst (I.intSub n1 n2))
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntSub t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntSub t1' t2)
            Nothing  -> Nothing

eval1 (S.IntMul t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> Just (S.IntConst (I.intMul n1 n2))
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntMul t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntMul t1' t2)
            Nothing  -> Nothing

eval1 (S.IntDiv t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> Just (S.IntConst (I.intDiv n1 n2))
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntDiv t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntDiv t1' t2)
            Nothing  -> Nothing

eval1 (S.IntNand t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> Just (S.IntConst (I.intNand n1 n2))
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntNand t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntNand t1' t2)
            Nothing  -> Nothing

eval1 (S.IntEq t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> case I.intEq n1 n2 of
                                                      True -> Just S.Tru
                                                      _    -> Just S.Fls
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntEq t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntEq t1' t2)
            Nothing  -> Nothing

eval1 (S.IntLt t1 t2) = 
  if S.isValue t1
     then case t1 of
            S.IntConst n1 -> if S.isValue t2
                              then case t2 of
                                     S.IntConst n2 -> case I.intLt n1 n2 of
                                                      True -> Just S.Tru
                                                      _    -> Just S.Fls
                                     _             -> Nothing
                              else case eval1 t2 of
                                     Just t2' -> Just (S.IntLt t1 t2')
                                     Nothing  -> Nothing
            _             -> Nothing
     else case eval1 t1 of
            Just t1' -> Just (S.IntLt t1' t2)
            Nothing  -> Nothing

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

\subsection{Natural Semantics}

\subsubsection{Formal Rules}

\paragraph{}
The formal rules of the natural semantics for this programming language is as follows:
\[
	a\Downarrow v \text{\quad (\textsc{B-ClosedForm})}	
\]
for closed form $a$, and $a$ should have no free variable inside.
\[
	v \Downarrow v \text{\quad (\textsc{B-Value})}
\]
\ \\
\[
	\frac{a\Downarrow \lambda x.a'\text{\quad} b\Downarrow v'\text{\quad} [x\mapsto v']a'\Downarrow v}{a\text{\ }b \Downarrow v}\text{\quad (\textsc{B-App})}
\]
\ \\
\[
	\frac{t_1 \Downarrow \text{ true\quad } t_2 \Downarrow v_2}{\text{if } t_1 \text{ then } t_2 \text{ else } t_3 \Downarrow v_2}\text{\quad (\textsc{B-IfTrue})}
\]
\ \\
\[
	\frac{t_1 \Downarrow \text{ false\quad} t_3 \Downarrow v_3}{\text{if } t_1 \text{ then } t_2 \text{ else } t_3 \Downarrow v_3}\text{\quad (\textsc{B-IfFalse})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{+}(v_1, v_2)}{+(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntAdd})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{\-}(v_1, v_2)}{\-(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntSub})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{*}(v_1, v_2)}{*(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntMul})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{/}(v_1, v_2)}{/(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntDiv})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{\wedge}(v_1, v_2)}{\uparrow(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntNand})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{=}(v_1, v_2)}{=(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntEq})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1\text{\quad} t_2 \Downarrow v_2\text{\quad} v = \widetilde{<}(v_1, v_2)}{<(t_1, t_2) \Downarrow v}\text{\quad (\textsc{B-IntLt})}
\]
\ \\
\[
	\frac{t_1 \Downarrow v_1 \text{\quad} [x\mapsto v_1]t_2 \Downarrow v}{\text{let } x=t_1 \text{ in } t_2 \Downarrow v} \text{\quad (\textsc{B-Let})}
\]
\ \\
\[
	\frac{t \Downarrow (\lambda x:T_1.\text{\ }t_{11}) \text{\quad} [x\mapsto \text{fix }(\lambda x:T_1.\text{\ }t_{11})]t_{11} \Downarrow v}{\text{fix } t \Downarrow v} \text{\quad (\textsc{B-Fix})}
\]
\ \\
\subsubsection{Haskell Implementation}

\begin{code}
module NaturalSemantics where

import List
import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I

eval :: S.Term -> S.Term

eval (S.If t1 t2 t3) = 
  case eval t1 of
    S.Tru -> eval t2  -- B-IfTrue
    S.Fls -> eval t3  -- B-IfFalse
    _     -> S.If t1 t2 t3 

-- B-App \& B-AppFix
eval (S.App t1 t2) = 
  if (S.isValue $ eval t1)
     then case eval t1 of
            S.Abs x tau t11 -> if ((S.isValue $ eval t2) && ((S.fv (S.Abs x tau t11)) == []))
                                  then eval (S.subst x (eval t2) t11)
                                  else S.App t1 t2
            _               -> S.App t1 t2
     else S.App t1 t2

-- B-IntAdd
eval (S.IntAdd t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intAdd v1 v2)
                       _            -> S.IntAdd t1 t2
    _             -> S.IntAdd t1 t2


-- B-IntSub
eval (S.IntSub t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intSub v1 v2)
                       _             -> S.IntSub t1 t2
    _             -> S.IntSub t1 t2

-- B-IntMul
eval (S.IntMul t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intMul v1 v2)
                       _             -> S.IntSub t1 t2
    _             -> S.IntSub t1 t2

-- B-IntDiv
eval (S.IntDiv t1 t2) =
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intDiv v1 v2)
                       _             -> S.IntDiv t1 t2
    _             -> S.IntDiv t1 t2

-- B-IntNand
eval (S.IntNand t1 t2) =
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> S.IntConst (I.intNand v1 v2)
                       _             -> S.IntNand t1 t2
    _             -> S.IntNand t1 t2

-- B-IntEq
eval (S.IntEq t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> case I.intEq v1 v2 of
                                          True  -> S.Tru
                                          False -> S.Fls
                       _             -> S.IntEq t1 t2
    _             -> S.IntEq t1 t2

-- B-IntLt
eval (S.IntLt t1 t2) = 
  case eval t1 of
    S.IntConst v1 -> case eval t2 of
                       S.IntConst v2 -> case I.intLt v1 v2 of
                                          True  -> S.Tru
                                          False -> S.Fls
                       _             -> S.IntLt t1 t2
    _             -> S.IntLt t1 t2

-- B-Let
eval (S.Let x t1 t2) = 
  if (S.isValue (eval t1))
     then if (S.isValue (eval (S.subst x (eval t1) t2)))
             then eval (S.subst x (eval t1) t2)
             else S.Let x t1 t2
     else S.Let x t1 t2

-- B-FIX
eval (S.Fix t) = 
  if S.isValue $ eval t
     then case eval t of
            S.Abs x tau1 t11 -> if (S.isValue (eval (S.subst x (S.Fix (S.Abs x tau1 t11)) t11)))
                                   then eval (S.subst x (S.Fix (S.Abs x tau1 t11)) t11)
                                   else S.Fix t
            _                -> S.Fix t
     else S.Fix t

-- B-Value and Exceptions
eval t = t

\end{code}

\subsection{Type Checker}
\subsubsection{Formal Rules}
It is always good to be sure a program is well-typed before we try to evaluate it. You can use the following type checker or write your own. The typing rules are listed below:
\[
	\frac{x:T\in \Gamma}{\Gamma \vdash x:T} \text{\quad (\textsc{T-Var})}
\]
\ \\
\[
	\frac{\Gamma, x:T_1 \vdash t_2: T_2 }{\Gamma \vdash \lambda x:T_1. t_2:T_1 \rightarrow T_2} \text{\quad (\textsc{T-Abs})}
\]
\ \\
\[
	\frac{\Gamma \vdash t_1: T_{11} \rightarrow T_{12} \mbox{   } \Gamma \vdash t_2:T_{11}}{\Gamma \vdash t_1\mbox{ } t_2:T_{12}} \text{\quad (\textsc{T-App})}
\]
\ \\
\[
	\text{true: Bool}  \text{\quad (\textsc{T-True})}
\]
\ \\
\[
        \text{false: Bool} \text{\quad (\textsc{T-False})}
\]
\ \\
\[
	\frac{t:Bool\mbox{    } t_2:T \mbox{    } t_3:T}{\text{if } t_1 \text{ then } t_2 \text{ else } t_3:T} \text{\quad (\textsc{T-If})}
\]
\ \\
\[
	\frac{\Gamma \vdash [x \mapsto t_1]t_2: T_2\mbox{   } \Gamma \vdash t_1: T_1}{\Gamma \vdash \mbox{ let } x=t_1 \mbox{ in } t_2 : T_2}\text{\quad {(\textsc{T-LetPoly})}}
\]
\ \\
\[
	\frac{\Gamma \vdash t_1: T_1\leftarrow T_1}{\Gamma \vdash \mbox{ fix } t_1 : T_1}\text{\quad {(\textsc{T-Fix})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{+(t_1, t_2):Int}\text{\quad {(\textsc{T-IntAdd})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{\-(t_1, t_2):Int}\text{\quad {(\textsc{T-IntSub})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{*(t_1, t_2):Int}\text{\quad {(\textsc{T-IntMul})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{\wedge(t_1, t_2):Int}\text{\quad {(\textsc{T-IntNand})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{=(t_1, t_2):Bool}\text{\quad {(\textsc{T-IntEq})}}
\]
\ \\
\[
	\frac{t_1:Int \mbox{   } t_2:Int}{<(t_1, t_2):Bool}\text{\quad {(\textsc{T-IntLt})}}
\]
\ \\
\subsubsection{Haskell Implementation}
\begin{code}
module Typing where
import qualified AbstractSyntax as S
import List
data Context  =  Empty
              |  Bind Context S.Var S.Type
              deriving Eq
instance Show Context where
  show Empty                  =  "<>"
  show (Bind capGamma x tau)  =  show capGamma ++ "," ++ x ++ ":" ++ show tau

contextLookup :: S.Var -> Context -> Maybe S.Type
contextLookup x Empty  =  Nothing
contextLookup x (Bind capGamma y tau)
  | x == y      =  Just tau
  | otherwise   =  contextLookup x capGamma

typing :: Context -> S.Term -> Maybe S.Type
--T-Var
typing capGamma (S.Var x) = contextLookup x capGamma
--T-Abs
typing capGamma (S.Abs x tau_1 t2) = case typing (Bind capGamma x tau_1) t2 of 
                         Just(tp0) -> Just (S.TypeArrow tau_1 tp0)
			 Nothing -> Nothing
typing capGamma (S.App t0 t2)=
	case typing capGamma t0 of 
		 Just (S.TypeArrow tp tp0) -> case typing capGamma t2 of 
						       Just tp' -> if tp==tp' 
						                      then Just tp0
								      else Nothing
						       Nothing  -> Nothing
		 _ -> Nothing

--T-True
typing capGamma S.Tru = Just S.TypeBool

--T-False
typing capGamma S.Fls = Just S.TypeBool

--T-If
typing capGamma (S.If t0 t2 t3) 
	| (typing capGamma t2 == typing capGamma t3 && typing capGamma t0 == Just S.TypeBool) = typing capGamma t2
	| otherwise = Nothing

typing capGamma (S.IntConst _) = Just S.TypeInt

--T-LetPoly
typing capGamma (S.Let x t0 t2) =
	case typing capGamma (S.subst x t0 t2) of 
		Just tp0 -> case typing capGamma t0 of
				    Just tp1 -> Just tp0
		                    _	     -> Nothing
		_        -> Nothing

--T-Fix
typing capGamma (S.Fix t) =
	case typing capGamma t of
		Just (S.TypeArrow tp0 tp2) -> if tp0 == tp2 
		                                  then Just tp0
						  else Nothing
		_			-> Nothing

--T-IntAdd 
typing capGamma (S.IntAdd t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeInt -> case typing capGamma t1 of 
		                          Just S.TypeInt -> Just S.TypeInt
					  Nothing -> Nothing
--T-IntSub 
typing capGamma (S.IntSub t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeInt -> case typing capGamma t1 of 
		                          Just S.TypeInt -> Just S.TypeInt
					  Nothing -> Nothing
--T-IntMul 
typing capGamma (S.IntMul t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeInt -> case typing capGamma t1 of 
		                          Just S.TypeInt -> Just S.TypeInt
					  Nothing -> Nothing
--T-IntDiv
typing capGamma (S.IntDiv t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeInt -> case typing capGamma t1 of 
		                          Just S.TypeInt -> Just S.TypeInt
					  Nothing -> Nothing
--T-IntNand 
typing capGamma (S.IntNand t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeInt -> case typing capGamma t1 of 
		                          Just S.TypeInt -> Just S.TypeInt
					  Nothing -> Nothing
--T-IntEq
typing capGamma (S.IntEq t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeBool -> case typing capGamma t1 of 
		                          Just S.TypeBool -> Just S.TypeBool
					  Nothing -> Nothing
--T-IntLt
typing capGamma (S.IntLt t1 t2) =
        case typing capGamma t1 of 
	         Just S.TypeBool -> case typing capGamma t1 of 
		                          Just S.TypeBool -> Just S.TypeInt
					  Nothing -> Nothing
typeCheck :: S.Term -> S.Type
typeCheck t =
  case typing Empty t of
    Just tau -> tau
    _ -> error "type error"
\end{code}

\subsubsection{Formal Rules}

\subsubsection{Haskell Implementation}

\subsection{Main Program}

\begin{code}
module Main where

import qualified System.Environment
import Data.List
import IO
import qualified AbstractSyntax as S
import qualified StructuralOperationalSemantics as E
import qualified NaturalSemantics as N
import qualified IntegerArithmetic as I
import qualified Typing as T

main :: IO()
main = 
    do
      args <- System.Environment.getArgs
      let [sourceFile] = args
      source <- readFile sourceFile
      let tokens = S.scan source
      let term = S.parse tokens
      putStrLn ("----Term----")
      putStrLn (show term)
      putStrLn ("----Type----")
      putStrLn (show (T.typeCheck term))
      putStrLn ("----Normal Form in Structureal Operational Semantics----")
      putStrLn (show (E.eval term))
      putStrLn ("----Normal Form of Natural Semantics----")
      putStrLn (show (N.eval term))
\end{code}

\section{Reduction Semantics}

\subsection{Evaluation contexts}

\begin{code}

module EvaluationContext where
import qualified AbstractSyntax as S

data Context = Hole
             | AppT Context S.Term
             | AppV S.Term Context   -- where Term is a value
             | If   Context S.Term S.Term
	     | IntAddT Context S.Term
	     | IntAddV S.Term Context
	     | IntSubT Context S.Term
	     | IntSubV S.Term Context
	     | IntMulT Context S.Term
	     | IntMulV S.Term Context
	     | IntDivT Context S.Term
	     | IntDivV S.Term Context
	     | IntNandT Context S.Term
	     | IntNandV S.Term Context
	     | IntEqT Context S.Term
	     | IntEqV S.Term Context
	     | IntLtT Context S.Term
	     | IntLtV S.Term Context
             | Let S.Var Context S.Term
	     | Fix Context
	     deriving Eq

fillWithTerm :: Context -> S.Term -> S.Term
fillWithTerm c t = case c of
  Hole        -> t
  AppT c1 t2  -> S.App (fillWithTerm c1 t) t2
  AppV t1 c2  -> S.App t1 (fillWithTerm c2 t)
  If c1 t2 t3 -> S.If (fillWithTerm c1 t) t2 t3
  IntAddV t1 c2 -> S.IntAdd t1 (fillWithTerm c2 t)
  IntAddT c1 t2 -> S.IntAdd (fillWithTerm c1 t) t2
  IntSubV t1 c2 -> S.IntSub t1 (fillWithTerm c2 t)
  IntSubT c1 t2 -> S.IntSub (fillWithTerm c1 t) t2
  IntMulV t1 c2 -> S.IntMul t1 (fillWithTerm c2 t)
  IntMulT c1 t2 -> S.IntMul (fillWithTerm c1 t) t2

  IntDivV t1 c2 -> S.IntDiv t1 (fillWithTerm c2 t)
  IntDivT c1 t2 -> S.IntDiv (fillWithTerm c1 t) t2
  IntNandV t1 c2 -> S.IntNand t1 (fillWithTerm c2 t)
  IntNandT c1 t2 -> S.IntNand (fillWithTerm c1 t) t2
  IntEqV t1 c2 -> S.IntEq t1 (fillWithTerm c2 t)
  IntEqT c1 t2 -> S.IntEq (fillWithTerm c1 t) t2
  IntLtV t1 c2 -> S.IntLt t1 (fillWithTerm c2 t)
  IntLtT c1 t2 -> S.IntLt (fillWithTerm c1 t) t2

  Let x c1 t2 -> S.Let x (fillWithTerm c1 t) t2
  Fix c -> S.Fix (fillWithTerm c t)

fillWithContext :: Context -> Context -> Context
fillWithContext c c' = case c of
  Hole        -> c'
  AppT c1 t2  -> AppT (fillWithContext c1 c') t2
  AppV t1 c2  -> AppV t1 (fillWithContext c2 c')
  If c1 t2 t3 -> If (fillWithContext c1 c') t2 t3
  IntAddV t1 c2 -> IntAddV t1 (fillWithContext c2 c')
  IntAddT c1 t2 -> IntAddT (fillWithContext c1 c') t2
  IntSubV t1 c2 -> IntSubV t1 (fillWithContext c2 c')
  IntSubT c1 t2 -> IntSubT (fillWithContext c1 c') t2
  IntMulV t1 c2 -> IntMulV t1 (fillWithContext c2 c')
  IntMulT c1 t2 -> IntMulT (fillWithContext c1 c') t2
  IntDivV t1 c2 -> IntDivV t1 (fillWithContext c2 c')
  IntDivT c1 t2 -> IntDivT (fillWithContext c1 c') t2
  IntNandV t1 c2 -> IntNandV t1 (fillWithContext c2 c')
  IntNandT c1 t2 -> IntNandT (fillWithContext c1 c') t2
  IntEqV t1 c2 -> IntEqV t1 (fillWithContext c2 c')
  IntEqT c1 t2 -> IntEqT (fillWithContext c1 c') t2
  IntLtV t1 c2 -> IntLtV t1 (fillWithContext c2 c')
  IntLtT c1 t2 -> IntLtT (fillWithContext c1 c') t2
  Let x c1 t2 -> Let x (fillWithContext c1 c') t2
  Fix c -> Fix (fillWithContext c c')
\end{code}

\subsection{Standard reduction}

\begin{code}

module ReductionSemantics where
import qualified AbstractSyntax as S
import qualified EvaluationContext as E
import qualified StructuralOperationalSemantics as S
import qualified IntegerArithmetic as I

makeEvalContext :: S.Term -> Maybe (S.Term, E.Context)
makeEvalContext t = case t of
  S.App (S.Abs x tau11 t12) t2
    | S.isValue t2 -> Just (t, E.Hole)
  S.App t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                        Just (t2', c2) -> Just (t2', (E.AppV t1 c2))
                        _              -> Nothing
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just (t1', (E.AppT c1 t2))
                        _             -> Nothing
  S.If (S.Tru) t2 t3 -> Just (t, E.Hole)
  S.If (S.Fls) t2 t3 -> Just (t, E.Hole)
  S.If t1 t2 t3 -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just (t1', (E.If c1 t2 t3))
                        _             -> Nothing
  S.IntAdd t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntAddV t1 c2))
                           Nothing       -> Just(t, E.Hole)
    | otherwise    -> case makeEvalContext t1 of
                           Just(t1', c1) -> Just(t1', (E.IntAddT c1 t2))
                           _             -> Nothing
  S.IntSub t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntSubV t1 c2))
                           Nothing -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.IntSubT c1 t2))
                        _             -> Nothing
  S.IntMul t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntMulV t1 c2))
                           Nothing       -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.IntMulT c1 t2))
                        _             -> Nothing
  S.IntDiv t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntDivV t1 c2))
                           Nothing -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.IntDivT c1 t2))
                        _             -> Nothing
  S.IntNand t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntNandV t1 c2))
                           Nothing -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                           Just(t1', c1) -> Just(t1', (E.IntNandT c1 t2))
                           _             -> Nothing
  S.IntEq t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntEqV t1 c2))
                           Nothing -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.IntEqT c1 t2))
                        _ -> Nothing
  S.IntLt t1 t2
    | S.isValue t1 -> case makeEvalContext t2 of
                           Just(t2', c2) -> Just(t2', (E.IntLtV t1 c2))
                           Nothing -> Just(t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.IntLtT c1 t2))
                        _             -> Nothing
  S.Let x t1 t2
    | S.isValue t1 -> Just (t, E.Hole)
    | otherwise -> case makeEvalContext t1 of
                        Just(t1', c1) -> Just(t1', (E.Let x c1 t2))
                        _             -> Nothing
  S.Fix (S.Abs x tau1 t2) -> Just (t, E.Hole)
  S.Fix t -> case makeEvalContext t of
                  Just(t', c) -> Just(t', E.Fix c)
                  _           -> Nothing
  _ -> Nothing

makeContractum :: S.Term -> S.Term
makeContractum t = case t of
  S.App (S.Abs x tau11 t12) t2 -> S.subst x t2 t12
  S.If (S.Tru) t2 t3 -> t2
  S.If (S.Fls) t2 t3 -> t3
  S.IntAdd (S.IntConst n1) (S.IntConst n2) -> S.IntConst (I.intAdd n1 n2)
  S.IntSub (S.IntConst n1) (S.IntConst n2) -> S.IntConst (I.intSub n1 n2)
  S.IntMul (S.IntConst n1) (S.IntConst n2) -> S.IntConst (I.intMul n1 n2)
  S.IntDiv (S.IntConst n1) (S.IntConst n2) -> S.IntConst (I.intDiv n1 n2)
  S.IntNand (S.IntConst n1) (S.IntConst n2) -> S.IntConst (I.intNand n1 n2)
  S.IntEq (S.IntConst n1) (S.IntConst n2) -> if I.intEq n1 n2 then S.Tru else S.Fls
  S.IntLt (S.IntConst n1) (S.IntConst n2) -> if I.intLt n1 n2 then S.Tru else S.Fls
  S.Let x t1 t2 -> S.subst x t1 t2
  S.Fix (S.Abs x tau1 t2) -> S.subst x (S.Fix (S.Abs x tau1 t2)) t2

textualMachineStep :: S.Term -> Maybe S.Term
textualMachineStep t =
  case makeEvalContext t of
       Just(t1, c) -> Just (E.fillWithTerm c (makeContractum t1))
       Nothing     -> Nothing

textualMachineEval :: S.Term -> S.Term
textualMachineEval t =
  case textualMachineStep t of
       Just t' -> textualMachineEval t'
       Nothing -> t

\end{code}


\section{Abstract Register Machines}

\subsection{CC Machine}

\begin{code}
module CCMachine where

import qualified AbstractSyntax as S
import qualified EvaluationContext as E
import qualified IntegerArithmetic as I

lookupHole :: E.Context -> Maybe (E.Context, E.Context)

lookupHole (E.AppT c1 t) = case c1 of
			     E.Hole -> Just ((E.AppT c1 t), E.Hole)
			     _      -> case lookupHole c1 of
				        Just (c2, c3) -> Just (c2, (E.AppT c3 t))
lookupHole (E.AppV v c1) = case c1 of 
                             E.Hole -> Just ((E.AppV v c1), E.Hole)
                             _      -> case lookupHole c1 of
				        Just(c2, c3) -> Just (c2, (E.AppV v c3))

lookupHole (E.If c1 t1 t2) = case c1 of 
			       E.Hole -> Just((E.If c1 t1 t2), E.Hole)
			       _      -> case lookupHole c1 of
					   Just(c2, c3) -> Just (c2, (E.If c3 t1 t2))
		  
lookupHole (E.IntAddT c1 t) = case c1 of 
				E.Hole -> Just((E.IntAddT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntAddT c3 t))
lookupHole (E.IntAddV t c1) = case c1 of
                                E.Hole -> Just((E.IntAddV t c1), E.Hole)
                                _      -> case lookupHole c1 of
                                           Just(c2, c3) -> Just (c2, (E.IntAddV t c3))

lookupHole (E.IntSubT c1 t) = case c1 of
                                E.Hole -> Just((E.IntSubT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntSubT c3 t))
lookupHole (E.IntSubV t c1) = case c1 of
				E.Hole -> Just((E.IntSubV t c1), E.Hole)
				_      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntSubV t c3))
		    
lookupHole (E.IntMulT c1 t) = case c1 of 
				E.Hole -> Just((E.IntMulT c1 t), E.Hole)
				_      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntMulT c3 t))

lookupHole (E.IntMulV t c1) = case c1 of
				E.Hole -> Just((E.IntMulV t c1), E.Hole)
				_      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntMulV t c3))

lookupHole (E.IntDivT c1 t) = case c1 of
				E.Hole -> Just((E.IntDivT c1 t), E.Hole)
                                _      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntDivT c3 t))
lookupHole (E.IntDivV t c1) = case c1 of
				E.Hole -> Just((E.IntDivV t c1), E.Hole)
				_      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntDivV t c3))

lookupHole (E.IntNandT c1 t)= case c1 of
				E.Hole -> Just((E.IntNandT c1 t), E.Hole)
				_      -> case lookupHole c1 of
                                            Just(c2, c3) -> Just (c2, (E.IntNandT c3 t))
lookupHole (E.IntNandV t c1)= case c1 of 
				E.Hole -> Just((E.IntNandV t c1), E.Hole)
				_      -> case lookupHole c1 of
					    Just(c2, c3) -> Just (c2, (E.IntNandV t c3))

lookupHole (E.IntEqT c1 t)= case c1 of 
			      E.Hole -> Just((E.IntEqT c1 t), E.Hole)
			      _      -> case lookupHole c1 of
				          Just(c2, c3) -> Just (c2, (E.IntEqT c3 t))
lookupHole (E.IntEqV t c1)= case c1 of
			      E.Hole -> Just((E.IntEqV t c1), E.Hole)
			      _   -> case lookupHole c1 of
					Just(c2, c3) -> Just (c2, (E.IntEqV t c3))

lookupHole (E.IntLtT c1 t)= case c1 of
			      E.Hole -> Just((E.IntLtT c1 t), E.Hole)
			      _	     -> case lookupHole c1 of
					  Just(c2, c3) -> Just (c2, (E.IntLtT c3 t))
lookupHole (E.IntLtV t c1)= case c1 of
			      E.Hole -> Just((E.IntLtV t c1), E.Hole)
			      _      -> case lookupHole c1 of
					  Just(c2, c3) -> Just (c2, (E.IntLtV t c3))
 
lookupHole (E.Let x c1 t1)= case c1 of
			      E.Hole -> Just((E.Let x c1 t1), E.Hole)
			      _      -> case lookupHole c1 of
					  Just(c2, c3) -> Just (c2, (E.Let x c3 t1))
		 
lookupHole (E.Fix c1) = case c1 of 
			  E.Hole -> Just((E.Fix c1), E.Hole)
			  _      -> case lookupHole c1 of
				      Just(c2, c3) -> Just (c2, (E.Fix c3))

lookupHole c1  =  Nothing

ccMachineStep :: (S.Term, E.Context) -> Maybe (S.Term, E.Context)
ccMachineStep (t, c) = case t of
  S.App t1 t2
    | not (S.isValue t1)                      ->   Just (t1, E.fillWithContext c (E.AppT E.Hole t2))       {-cc1-}
    | S.isValue t1 && not (S.isValue t2)      ->   Just (t2, E.fillWithContext c (E.AppV t1 E.Hole))       {-cc2-}
  S.App (S.Abs x _ t12) t2                    ->   Just (S.subst x t2 t12, c)                              {-cc$\beta$-}
  
  S.IntAdd t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntAddT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntAddV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> Just (S.IntConst (I.intAdd n1 n2), c)
     {-cc$\delta$-}
  S.If (S.Tru) t2 t3				-> Just (t2, c)
  S.If (S.Fls) t2 t3				-> Just (t3, c)
  S.If t1 t2 t3
    | not (S.isValue t1)			-> Just (t1, E.fillWithContext c (E.If E.Hole t2 t3))

  S.IntSub t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntSubT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntSubV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> Just (S.IntConst (I.intSub n1 n2), c)
     {-cc$\delta$-}

  S.IntMul t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntMulT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntMulV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> Just (S.IntConst (I.intMul n1 n2), c)
     {-cc$\delta$-}

  S.IntDiv t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntDivT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntDivV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> Just (S.IntConst (I.intDiv n1 n2), c)
     {-cc$\delta$-}

  S.IntNand t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntNandT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntNandV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> Just (S.IntConst (I.intNand n1 n2), c)
     {-cc$\delta$-}

  S.IntEq t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntEqT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntEqV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 ->  case I.intEq n1 n2 of
								True -> Just(S.Tru, c)
								_    -> Just(S.Fls, c)
     {-cc$\delta$-}
  
  S.IntLt t1 t2
    | not(S.isValue t1)				-> Just (t1, E.fillWithContext c (E.IntLtT E.Hole t2))
    | S.isValue t1 && not (S.isValue t2)	-> Just (t2, E.fillWithContext c (E.IntLtV t1 E.Hole))
     {-cc3-}
    | otherwise	->  case t1 of
		      S.IntConst n1 -> case t2 of 
					 S.IntConst n2 -> case I.intLt n1 n2 of
								True -> Just(S.Tru, c)
								_    -> Just(S.Fls, c)
     {-cc$\delta$-}

  S.Let x t1 t2
    | S.isValue t1				-> Just (S.subst x t1 t2,  c)
    | otherwise					-> Just (t1, E.fillWithContext c (E.Let x E.Hole t2))
   
  S.Fix t
    | not (S.isValue t)				-> Just (t, E.fillWithContext c (E.Fix E.Hole))
    | otherwise		-> case t of 
				S.Abs x tau1 t2 -> Just (S.subst x (S.Fix (S.Abs x tau1 t2)) t2, c)
				
  
  _  -> case lookupHole c of 
          Just (c2, c3) -> Just (E.fillWithTerm c2 t, c3)
	  Nothing       -> Nothing

ccMachineEval :: S.Term -> S.Term
ccMachineEval t = 
  fst $ f (t, E.Hole)
  where
    f :: (S.Term, E.Context) -> (S.Term, E.Context)
    f (t, c) = 
      case ccMachineStep (t, c) of
        Just (t', c') -> f (t', c')
        Nothing       -> (t, c)
\end{code}

\subsection{SCC Machine}

\begin{code}
module SCCMachine where

import qualified AbstractSyntax as S
import qualified EvaluationContext as E
import qualified IntegerArithmetic as I
import qualified CCMachine as CC


dealWithContext :: (S.Term,E.Context) -> Maybe (S.Term, E.Context)
dealWithContext (t,c) = case c of
  {-app-}
  E.AppT E.Hole t2 -> Just (t2, E.AppV t E.Hole)
  E.AppV (S.Abs x tau t') E.Hole -> Just ((S.subst x t t'),E.Hole)
  {-if-}
  E.If E.Hole t2 t3 -> case t of
			  S.Tru -> Just (t2, E.Hole)
			  S.Fls -> Just (t3, E.Hole)
  {-add-}
  E.IntAddT E.Hole t2 -> Just (t2, E.IntAddV t E.Hole)
  E.IntAddV t1 E.Hole -> case t1 of
			  S.IntConst n1 -> case t of
						S.IntConst n2 -> Just(S.IntConst (I.intAdd n1 n2),E.Hole)
					        _ -> Nothing
			  _ -> Nothing
  {-sub-}
  E.IntSubT E.Hole t2 -> Just (t2, E.IntSubV t E.Hole)
  E.IntSubV t1 E.Hole -> case t1 of
			  S.IntConst n1 -> case t of
					        S.IntConst n2 -> Just(S.IntConst (I.intSub n1 n2),E.Hole) 
					        _ -> Nothing
			  _ -> Nothing
  {-mul-}
  E.IntMulT E.Hole t2 -> Just (t2, E.IntMulV t E.Hole)
  E.IntMulV t1 E.Hole -> case t1 of
		           S.IntConst n1 -> case t of
						 S.IntConst n2 -> Just(S.IntConst (I.intMul n1 n2),E.Hole)
						 _ -> Nothing
	        	   _ -> Nothing
  {-div-}
  E.IntDivT E.Hole t2 -> Just (t2, E.IntDivV t E.Hole)
  E.IntDivV t1 E.Hole -> case t1 of
			   S.IntConst n1 -> case t of
					         S.IntConst n2 -> Just(S.IntConst (I.intDiv n1 n2),E.Hole)
						 _ -> Nothing
			   _ -> Nothing
  {-nand-}
  E.IntNandT E.Hole t2 -> Just (t2, E.IntNandV t E.Hole)
  E.IntNandV t1 E.Hole -> case t1 of
			    S.IntConst n1 -> case t of
					          S.IntConst n2 -> Just(S.IntConst (I.intNand n1 n2),E.Hole)
						  _ -> Nothing
			    _ -> Nothing
  {-eq-}
  E.IntEqT E.Hole t2 -> Just (t2, E.IntEqV t E.Hole)
  E.IntEqV t1 E.Hole -> case t1 of
			   S.IntConst n1 -> case t of
						 S.IntConst n2 -> case I.intEq n1 n2 of
									True -> Just (S.Tru,E.Hole)
									False -> Just (S.Fls,E.Hole)
						 _ -> Nothing
			   _ -> Nothing
  {-lt-}
  E.IntLtT E.Hole t2 -> Just (t2, E.IntLtV t E.Hole)
  E.IntLtV t1 E.Hole -> case t1 of
			   S.IntConst n1 -> case t of
						 S.IntConst n2 -> case I.intLt n1 n2 of
									True -> Just (S.Tru,E.Hole)
									False -> Just (S.Fls,E.Hole)
						 _ -> Nothing
			   _ -> Nothing

  {-let-}
  E.Let x E.Hole t2 -> Just (S.subst x t t2,E.Hole)
  {-fix-}
  E.Fix E.Hole -> case t of 
			S.Abs x tau1 t2 -> Just (S.subst x (S.Fix (S.Abs x tau1 t2)) t2, E.Hole)
			_ -> Nothing
  _ -> Nothing






sccMachineStep :: (S.Term, E.Context) -> Maybe (S.Term, E.Context)
sccMachineStep (t,c) = case S.isValue t of
  {-t is a term-}
  False -> case t of  
    S.App t1 t2 -> Just (t1, E.fillWithContext c (E.AppT E.Hole t2))
    S.If t1 t2 t3 -> Just (t1, E.fillWithContext c (E.If E.Hole t2 t3))
    S.IntAdd t1 t2 -> Just (t1, E.fillWithContext c (E.IntAddT E.Hole t2))				
    S.IntSub t1 t2 -> Just (t1, E.fillWithContext c (E.IntSubT E.Hole t2))
    S.IntMul t1 t2 -> Just (t1, E.fillWithContext c (E.IntMulT E.Hole t2))
    S.IntDiv t1 t2 -> Just (t1, E.fillWithContext c (E.IntDivT E.Hole t2))
    S.IntNand t1 t2 -> Just (t1, E.fillWithContext c (E.IntNandT E.Hole t2))
    S.IntEq t1 t2 -> Just (t1, E.fillWithContext c (E.IntEqT E.Hole t2))
    S.IntLt t1 t2 -> Just (t1,E.fillWithContext c (E.IntLtT E.Hole t2))
    S.Let x t1 t2 -> Just (t1, E.fillWithContext c (E.Let x E.Hole t2))
    S.Fix t -> Just (t, E.fillWithContext c (E.Fix E.Hole))
    _ -> Nothing
 {-t is a value-}
  True -> case dealWithContext (t,c) of
    Just (t',c') ->  Just (t',c')
    Nothing -> case c of
	{-app-}
	E.AppT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.AppT (E.fillWithContext cl cc) t2)
			_	     -> Nothing
		_	     -> Nothing
	E.AppV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.AppV t1 (E.fillWithContext cl cc))
			_	     -> Nothing
		_ -> Nothing
	{-if-}
	E.If c1 t2 t3 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.If (E.fillWithContext cl cc) t2 t3)
			_ -> Nothing
		_ -> Nothing
	{-add-}
	E.IntAddT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntAddT (E.fillWithContext cl cc) t2) 
			_ -> Nothing
		_ -> Nothing
	E.IntAddV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntAddV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-sub-}
	E.IntSubT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntSubT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntSubV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntSubV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-mul-}
	E.IntMulT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntMulT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntMulV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntMulV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-div-}
	E.IntDivT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntDivT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntDivV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntDivV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-nand-}
	E.IntNandT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntNandT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntNandV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntNandV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-eq-}
	E.IntEqT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntEqT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntEqV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntEqV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-lt-}
	E.IntLtT c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntLtT (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	E.IntLtV t1 c2 -> case CC.lookupHole c2 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.IntLtV t1 (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	{-let-}
	E.Let x c1 t2 -> case CC.lookupHole c1 of
		Just (c',cl) -> case dealWithContext (t,c') of
			Just (t',cc) -> Just (t',E.Let x (E.fillWithContext cl cc) t2)
			_ -> Nothing
		_ -> Nothing
	{-fix-}
	E.Fix c1 -> case CC.lookupHole c1 of
		Just (c',cl) ->case dealWithContext (t,c') of
			Just (t',cc) ->  Just (t',E.Fix (E.fillWithContext cl cc))
			_ -> Nothing
		_ -> Nothing
	_ -> Nothing



sccMachineEval :: S.Term -> S.Term
sccMachineEval t = 
  fst $ f (t,E.Hole)
  where
    f :: (S.Term, E.Context) -> (S.Term, E.Context)
    f (t,c) = 
      case sccMachineStep (t,c) of
        Just (t',c') -> f (t',c')
        Nothing -> (t,c)	
	
\end{code}

\subsection{CK Machine}

\begin{code}
module CKMachine where

import qualified AbstractSyntax as S
import qualified IntegerArithmetic as I

data Cont  =  MachineTerminate
           |  Fun              S.Term Cont -- where Term is a value
           |  Arg              S.Term Cont
           |  If               S.Term S.Term Cont
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
           |  Let              S.Var S.Term Cont
           |  Fix              Cont

ckMachineStep :: (S.Term, Cont) -> Maybe (S.Term, Cont)
ckMachineStep (t,c) = 
  case t of
    S.App t1 t2 -> Just (t1, (Arg t2 c))
    S.IntAdd t1 t2 -> Just (t1, (IntAdd2 t2 c))
    S.IntSub t1 t2 -> Just (t1, (IntSub2 t2 c))
    S.IntMul t1 t2 -> Just (t1, (IntMul2 t2 c))
    S.IntDiv t1 t2 -> Just (t1, (IntDiv2 t2 c))
    S.IntNand t1 t2-> Just (t1, (IntNand2 t2 c))
    S.IntEq t1 t2  -> Just (t1, (IntEq2 t2 c))
    S.IntLt t1 t2  -> Just (t1, (IntLt2 t2 c))
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

\paragraph{}
In the implementation of \textit{ckMachineEval}, we justify the final state. If its continuation is \textbf{MachineTerminate} alone, then the term within it is the evaluated value to return. Otherwise, we can know that the machine gets stuck halfway, and it prints out the related error message.

\subsection{CEK Machine}

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

\end{code}

\paragraph{}
To implement the function \textit{updateEnv}, we follow the formal rule
\[
	\mathscr{E}[X\leftarrow c] = \{\langle X, c \rangle \} \cup \{ \langle Y, c' \rangle \text{ } \vert \text{ }\langle Y, c' \rangle \in \mathscr{E} \text{ and } Y \neq X \}
\]
which delays the substitution until the final step.

\begin{code}

updateEnv :: Environment -> S.Var -> Closure -> Environment
updateEnv (Env es) x clo = 
  Env ((x, clo):(filter (\a -> (fst a) /= x) es))

data Cont  =  MachineTerminate
           |  Fun                Closure Cont -- where Closure is a value
           |  Arg                Closure Cont
           |  If                 Closure Closure Cont -- lazy
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
           |  Let                S.Var Closure Cont
           |  Fix                Cont

finalSubst :: S.Term -> Environment -> S.Term
finalSubst t (e@(Env [])) = t
finalSubst t (Env ((v, (t', e')):es)) = 
  if v `elem` (S.fv t) then
                         finalSubst (S.subst v (finalSubst t' e') t) (Env es)
                       else
                         finalSubst t (Env es)
\end{code}

\paragraph{}
The above is the implementation of function \textit{finalSubst}. This happens when CEK Machine comes to a state with \textbf{MachineTerminate} continuation and the current term is a $\lambda$-abstraction (i.e., a function term). If so, the machine will do one more step: apply the substitution from the current environment to the presumable free variables in the $\lambda$-abstraction. What's more, in order to eliminate the harm of bringing new free variables, before applying each substitution, we make the candidate term in the environment containing no free variable if possible. This is why we make a recursive call of \textit{finalSubst} in the candidate closure. We continue this step until the current environment is empty.

\paragraph{}
After that, the CEK Machine can return the final evaluated value, which is a function.

\begin{code}

 
cekMachineStep :: (Closure, Cont) -> Maybe (Closure, Cont)
cekMachineStep ((t, e), c) = 
  case t of
    S.App t1 t2    -> Just ((t1, e), Arg (t2, e) c)
    S.If t1 t2 t3  -> Just ((t1, e), If (t2, e) (t3, e) c)
    S.IntAdd t1 t2 -> Just ((t1, e), IntAdd2 (t2, e) c)
    S.IntSub t1 t2 -> Just ((t1, e), IntSub2 (t2, e) c)
    S.IntMul t1 t2 -> Just ((t1, e), IntMul2 (t2, e) c)
    S.IntDiv t1 t2 -> Just ((t1, e), IntDiv2 (t2, e) c)
    S.IntNand t1 t2-> Just ((t1, e), IntNand2 (t2, e) c)
    S.IntEq t1 t2  -> Just ((t1, e), IntEq2 (t2, e) c)
    S.IntLt t1 t2  -> Just ((t1, e), IntLt2 (t2, e) c)
    S.Var x        -> Just (lookupEnv e x, c)
    S.Tru          -> case c of
                        If (t2, e') (t3, _) c' -> Just ((t2, e'), c')   
                        _                      -> Nothing 
    S.Fls          -> case c of
                        If (t2, _) (t3, e') c' -> Just ((t3, e'), c')   
                        _                      -> Nothing
    S.Let x t1 t2  -> Just ((t1, e), Let x (t2, e) c)
    S.Fix (S.Abs x tau11 t11) -> Just ((t11, updateEnv e x (S.Fix (S.Abs x tau11 t11), e)), c)
    S.Fix t1       -> Just ((t1, e), Fix c)    
    _
      | S.isValue t -> case c of
                         Fun (S.Abs x tau11 t11, e') c' -> Just ((t11, updateEnv e' x (t, e)), c')
                         Arg (t2, e') c'                -> Just ((t2, e'), Fun (t, e) c')
                         IntAdd1 (S.IntConst n1, e') c' -> 
                            case t of
                              S.IntConst n2 -> Just ((S.IntConst (I.intAdd n1 n2), Env []), c')
                              _             -> Nothing
                         IntAdd2 (t2, e') c'            -> Just ((t2, e'), IntAdd1 (t, e) c')
                         IntSub1 (S.IntConst n1, e') c' -> 
                            case t of
                              S.IntConst n2 -> Just ((S.IntConst (I.intSub n1 n2), Env []), c')
                              _             -> Nothing
                         IntSub2 (t2, e') c'            -> Just ((t2, e'), IntSub1 (t, e) c')
                         IntMul1 (S.IntConst n1, e') c' -> 
                           case t of
                             S.IntConst n2 -> Just ((S.IntConst (I.intMul n1 n2), Env []), c')
                             _             -> Nothing
                         IntMul2 (t2, e') c'            -> Just ((t2, e'), IntMul1 (t, e) c')
                         IntDiv1 (S.IntConst n1, e') c' -> 
                           case t of
                             S.IntConst n2 -> Just ((S.IntConst (I.intDiv n1 n2), Env []), c')
                             _             -> Nothing
                         IntDiv2 (t2, e') c'            -> Just ((t2, e'), IntDiv1 (t, e) c')
                         IntNand1 (S.IntConst n1, e') c'-> 
                           case t of
                             S.IntConst n2 -> Just ((S.IntConst (I.intNand n1 n2), Env []), c')
                             _             -> Nothing
                         IntNand2 (t2, e') c'           -> Just ((t2, e'), IntNand1 (t, e) c')
                         IntEq1 (S.IntConst n1, e') c'  -> 
                            case t of
                              S.IntConst n2 -> case I.intEq n1 n2 of
                                                 True -> Just ((S.Tru, Env []), c')
                                                 _    -> Just ((S.Fls, Env []), c')
                         IntEq2 (t2, e') c'             -> Just ((t2, e'), IntEq1 (t, e) c')
                         IntLt1 (S.IntConst n1, e') c'  -> 
                            case t of
                              S.IntConst n2 -> case I.intLt n1 n2 of
                                                 True -> Just ((S.Tru, Env []), c')
                                                 _    -> Just ((S.Fls, Env []), c')
                         IntLt2 (t2, e') c'             -> Just ((t2, e'), IntLt1 (t, e) c')
                         Let x (t2, e') c'              -> Just ((t2, updateEnv e' x (t, e)), c')
                         Fix c'                         -> Just ((S.Fix t, e), c')
                         MachineTerminate               -> 
                           case t of
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

\paragraph{}
In the implementation of \textit{cekMachineEval}, similarly, we check the final returned state. If its continuation is \textbf{MachineTerminate} alone, then its term is the evaluated value, and we should return it. Otherwise, we can know that the machine gets stuck halfway, and it prints out the error message.


\subsection{Main}

\begin{code}
module Main where

import qualified System.Environment
import Data.List
import IO
import qualified AbstractSyntax as S
import qualified StructuralOperationalSemantics as E
import qualified NaturalSemantics as N
import qualified IntegerArithmetic as I
import qualified Typing as T

import qualified EvaluationContext as E
import qualified ReductionSemantics as R

import qualified CCMachine as CC
import qualified CKMachine as CK
import qualified CEKMachine as CEK
import qualified SCCMachine as SCC

main :: IO()
main = 
    do
      args <- System.Environment.getArgs
      let [sourceFile] = args
      source <- readFile sourceFile
      let tokens = S.scan source
      let term = S.parse tokens
      putStrLn ("----Term----")
      putStrLn (show term)
      putStrLn ("----Type----")
      putStrLn (show (T.typeCheck term))
      putStrLn ("----Normal Form in Structureal Operational Semantics----")
      putStrLn (show (E.eval term))
      putStrLn ("----Normal Form of Natural Semantics----")
      putStrLn (show (N.eval term))
      putStrLn ("----Standard Reduction----")
      putStrLn (show (R.textualMachineEval term))
      putStrLn ("----CC Machine----")
      putStrLn (show (CC.ccMachineEval term))
      putStrLn ("----CK Machine----")
      putStrLn (show (CK.ckMachineEval term))
      putStrLn ("----CEK Machine----")
      putStrLn (show (CEK.cekMachineEval term))
      putStrLn ("----SCC Machine----")
      putStrLn (show (SCC.sccMachineEval term))
\end{code}

\subsection{Test Cases}

\subsubsection{Test 1: $iseven\text{ }7$ in let binding form}
\verbatiminput{test1.TLBN}
\verbatiminput{test1.out2}

\subsubsection{Test 2: $iseven\text{ }7$ in fix form}
\verbatiminput{test2.TLBN}
\verbatiminput{test2.out2}

\subsubsection{Test 3: Compute $2^3$ using fix}
\verbatiminput{test3.TLBN}
\verbatiminput{test3.out2}

\subsubsection{Test 4: Compute $(3!)!$ using fix}
\verbatiminput{test4.TLBN}
\verbatiminput{test4.out2}

\subsubsection{Test 5}
\verbatiminput{test5.TLBN}
\verbatiminput{test5.out2}

\subsubsection{Test 6: Test 5 in pure fix form}
\verbatiminput{test6.TLBN}
\verbatiminput{test6.out2}

\subsubsection{Test 7: Special test for CEK Machine}

\paragraph{}
Here, we sincerely appreciate the inspiration from the first group. And we test our modified CEK Machine directly following their test case.

\verbatiminput{test7.TLBN}
\verbatiminput{test7.out2}


\end{document}
