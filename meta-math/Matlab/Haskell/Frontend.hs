module Frontend where

import Text.ParserCombinators.Parsec
import qualified Text.ParserCombinators.Parsec.Language as L
import qualified Text.ParserCombinators.Parsec.Token as T
import qualified AbstractSyntax as S
import Data.List
import Data.Char

test :: IO ()
test = do
  src <- readFile "../Test/heat.m"
  let t = parseMatlab src
  putStrLn $ show t
  

matlabStyle :: L.LanguageDef st
matlabStyle = L.emptyDef
              { L.commentLine = "%"
              , L.nestedComments = False                
              , L.identStart = letter <|> char '_'                   
              , L.identLetter = alphaNum <|> char '_'               
              , L.opStart = L.opLetter matlabStyle                  
              , L.opLetter = oneOf "+-*/^'.~<=>"
              , L.reservedNames = ["function", "end", "if", "endif", "while", "endwhile", "break", "continue",
                                   "for", "in", "endfor", "return"]               
              , L.reservedOpNames = ["=", "@", "~"]                    
              , L.caseSensitive = True                
              }

lexer :: T.TokenParser st
lexer = T.makeTokenParser matlabStyle

float = T.float lexer
identifier = T.identifier lexer
integer = T.integer lexer

{-
identifier = do
  c <- letter <|> char '_'
  cs <- many $ alphaNum <|> char '_'
  return (c:cs)
-}

symbol s = do
  spaces
  string s
  spaces
  
-- Top-level of the Front-end.
parseMatlab :: String -> S.Program
parseMatlab input =
  case parse parseProg "" input of 
    Left err -> error $ "Syntax Error: " ++ show err
    Right val -> val

parseProg :: Parser S.Program
parseProg = program

program = try prog1 -- <|> prog2

prog1 = do
  f <- funcDef 
  return (S.MonoFunc f)

funcDef = do
  symbol "function"
  rl <- retList 
  symbol "="
  name <- identifier
  al <- argList
  b <- body
  optional (symbol "end")
  return (S.Func name al rl b)

retList = (do
   symbol "["
   rl <- identifier `sepBy` (symbol ",")
   symbol "]"
   return (S.RetList rl)) 
   <|> (do
   rl <- identifier `sepBy` (symbol ",")
   return (S.RetList rl))              

argList = do
  symbol "("            
  al <- argument `sepBy` (symbol ",")
  symbol ")"
  return (S.ArgList al)

argument = try (do
  v <- identifier             
  symbol "="
  n <- float
  return (S.InitArg v n))
  <|> (do         
  v <- identifier        
  return (S.NullArg v))

body = do 
  ss <- many statement
  return (S.Body ss)

statement = try (do
  s <- stmt
  symbol ";"
  return (S.ClosedStatement s))
  <|> (do
  s <- stmt
  return (S.OpenStatement s))

stmt = s1 <|> s2

-- s1 ::= Var = NumExpr
s1 = try (do
  v <- identifier           
  symbol "="
  ne <- parseRHS
  return (S.Assign v ne))

-- s2 ::= return | break | continue
s2 = (do
  symbol "return"
  return (S.Return))
  <|> (do
  symbol "break"
  return (S.Break))
  <|> (do
  symbol "continue"
  return (S.Continue))

parseRHS = do
  rhs <- parseNumExpr 
  return (S.NumRHS rhs)

parseNumExpr = term `chainl1` addsubop

term = factor `chainl1` muldivop

factor = (do
  spaces           
  n <- float
  spaces
  return (S.NumReal n))
  <|> (do
  spaces
  n <- integer    
  spaces    
  return (S.NumInt n))
  <|> (do       
  spaces        
  v <- identifier        
  spaces
  return (S.Var v))
  <|> (do       
  spaces        
  symbol "("
  ne <- parseNumExpr
  symbol ")"
  spaces
  return (S.Par ne))

addsubop = (do
  symbol "+"             
  return (S.Bop S.Add))
  <|> (do         
  symbol "-"        
  return (S.Bop S.Sub))

muldivop = (do
  symbol "*"             
  return (S.Bop S.Mul))
  <|> (do         
  symbol "/"        
  return (S.Bop S.Div))
  