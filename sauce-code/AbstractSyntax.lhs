\begin{code}

module AbstractSyntax where 
 
import Data.Maybe
import Text.Regex
import Data.Char


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
        show (ID id) = "ID " ++ id
        show (NUM num) = num
        show (LET) = "let"
        show (IN) = "in"
        show (END) = "end"
        show (FIX) = "fix"

data Type = TypeArrow Type Type
        | TypeBool
        | TypeInt
        | TypeNull    -- Just for debugging
        deriving Eq

instance Show Type where
        show (TypeArrow tau1 tau2) = "->(" ++ show tau1 ++ "," ++ show tau2 ++ ")"
        show TypeBool = "Bool"
        show TypeInt = "Int"
        show TypeNull = "0"
        
        
type Var = String
data Term = Var Var
        | Abs Var Type Term
        | App Term Term
        | Tru
        | Fls
        | If Term Term Term
        | IntConst Integer
        | Bop BOP Term Term
        | Bpr BPR Term Term
        | Fix Term
        | Let Var Term Term

instance Show Term where
        show (Var x) = x
        show (Abs x tau t) = "abs(" ++ x ++ ":" ++ show tau ++ "." ++ show t ++ ")"
        show (App t1 t2) = "app(" ++ show t1 ++ "," ++ show t2 ++ ")"
        show Tru = "true"
        show Fls = "false"
        show (If t1 t2 t3) = "if " ++ show t1 ++ " then " ++ show t2 ++ " else " ++ show t3 ++ " fi"
        show (IntConst n) = show n
        show (Bop op t1 t2) = show op ++ "(" ++ show t1 ++ ", " ++ show t2 ++ ")"
        show (Bpr p t1 t2) = show p ++ "(" ++ show t1 ++ ", " ++ show t2 ++ ")"
        show (Fix t) = "fix [" ++ show t ++ "]"
        show (Let v t1 t2) = "let [" ++ v ++ "] [" ++ show t1 ++ "] [" ++ show t2 ++ "]"

-- Binary Operations
data BOP = IntAdd | IntSub | IntMul | IntDiv | IntNand

instance Show BOP where
  show IntAdd = "+"
  show IntSub = "-"
  show IntMul = "*"
  show IntDiv = "/"
  show IntNand = "^"

-- Binary Predicates
data BPR = IntEq | IntLt

instance Show BPR where
  show IntEq = "="
  show IntLt = "<"

-- list of free variables of a term
fv :: Term -> [Var]
fv (Var x) = [x]
fv (Abs x _ t) = filter (/=x) (fv t)
fv (App t1 t2) = (fv t1) ++ (fv t2)
fv (If t1 t2 t3) = (fv t1) ++ (fv t2) ++ (fv t3)
fv (Bop _ t1 t2) = (fv t1) ++ (fv t2)
fv (Bpr _ t1 t2) = (fv t1) ++ (fv t2)
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
subst x s (Bop op t1 t2) = Bop op (subst x s t1) (subst x s t2)
subst x s (Bpr p t1 t2) = Bpr p (subst x s t1) (subst x s t2)
subst x s (Fix t) = Fix (subst x s t)
subst x s (Let y t1 t2) = Let y (subst x s t1) (subst x s t2)
subst x s t = t

isValue :: Term -> Bool
isValue (Abs _ _ _) = True
isValue Tru = True
isValue Fls = True
isValue (IntConst _) = True
isValue _ = False


--reguar expresiion
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

--lexer
scan :: String -> [Token]
scan "" = []
----white spase
scan (' ':xs) = scan xs
scan ('\t':xs) = scan xs
scan ('\n':xs) = scan xs
----symbol
scan (':':xs) = [COLON] ++ scan xs
scan ('-':'>':xs) = [ARROW] ++ scan xs
scan ('(':xs) = [LPAR] ++ scan xs
scan (',':xs) = [COMMA] ++ scan xs
scan (')':xs) = [RPAR] ++ scan xs
scan ('.':xs) = [FULLSTOP] ++ scan xs
-------"+,-,*,/,^,=,<."
scan ('+':xs) = [PLUS] ++ scan xs
scan ('-':xs) = [SUB] ++ scan xs
scan ('*':xs) = [MUL] ++ scan xs
scan ('/':xs) = [DIV] ++ scan xs
scan ('^':xs) = [NAND] ++ scan xs
scan ('=':xs) = [EQUAL] ++ scan xs
scan ('<':xs) = [LT_keyword] ++ scan xs 
----id,keywords and num
scan str = case subscan1 str of
                Nothing -> case subscan2 str of
                                Nothing -> error "[Scan]err: unexpected symbols!"
                                Just (tok,xs) -> tok ++ scan xs
                Just (tok,xs) -> tok ++ scan xs
str str = error "[Scan]err: unexpected symbols!"

--parser

--type parser
parseType :: [Token] -> Maybe (Type,[Token])
parseType (BOOL:ty) = Just (TypeBool,ty)
parseType (INT:ty) = Just (TypeInt,ty)
parseType (RPAR:ty) = parseType ty
parseType (COMMA:ty) = parseType ty
parseType (ARROW:LPAR:ty) = case parseType ty of
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
parseTerm (PLUS:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bop IntAdd t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: plus term"
                                Nothing -> Nothing
                                _ -> error "[P]err: plus term"
parseTerm (SUB:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bop IntSub t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: sub term"
                                Nothing -> Nothing
                                _ -> error "[P]err: sub term"
parseTerm (MUL:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bop IntMul t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: mul term"
                                Nothing -> Nothing
                                _ -> error "[P]err: mul term"
parseTerm (DIV:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bop IntDiv t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: div term"
                                Nothing -> Nothing
                                _ -> error "[P]err: div term"
parseTerm (NAND:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bop IntNand t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: nand term"
                                Nothing -> Nothing
                                _ -> error "[P]err: nand term"
parseTerm (EQUAL:LPAR:ts) = case parseTerm ts of
                                Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                Just (t2,(RPAR:tll)) -> Just ((Bpr IntEq t1 t2),tll)
                                                                Nothing -> Nothing
                                                                _ -> error "[P]err: eq term"
                                Nothing -> Nothing
                                _ -> error "[P]err: eq term"
parseTerm (LT_keyword:LPAR:ts) = case parseTerm ts of
                                        Just (t1,(COMMA:tl)) -> case parseTerm tl of
                                                                        Just (t2,(RPAR:tll)) -> Just ((Bpr IntLt t1 t2),tll)
                                                                        Nothing -> Nothing
                                                                        _ -> error "[P]err: lt term"
                                        Nothing -> Nothing
                                        _ -> error "[P]err: lt term"
----if-then-else
parseTerm (IF:ts) = case parseTerm ts of
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
parseTerm (ABS:LPAR:(ID id):COLON:ts) = case parseType ts of
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
