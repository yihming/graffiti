{-# LANGUAGE OverloadedStrings #-}
module VisualAST where

import Data.List
import Data.GraphViz
import Data.GraphViz.Attributes.Complete
import Data.GraphViz.Types.Monadic
import Data.Text.Lazy.Internal
import qualified Data.GraphViz.Types.Generalised as G
import Data.Text.Lazy
import Data.GraphViz.Commands.IO
import System.IO

import qualified AbstractSyntax as S

type DotNodeInfo = (Integer, String)
type DotEdgeInfo = (Integer, Integer)

genDotFile :: FilePath -> S.Program -> String -> IO ()
genDotFile fp prog fName = do
  let g = constructDotGraph prog fName
  writeDotFile fp g

constructDotGraph :: S.Program -> String -> G.DotGraph Integer
constructDotGraph p fName = digraph' $ do
  let graphAttrList = [Ordering OutEdges, RankSep [0.4], BgColor [WC (X11Color LightGray) Nothing]]
  let nodeAttrList = [Shape BoxShape, FixedSize False, FontSize 12.0, FontName "Helvetica-bold", FontColor (X11Color Blue),
                      Width 0.25, Height 0.25, Color [WC (X11Color Black) Nothing], FillColor [WC (X11Color White) Nothing],
                      Style [SItem Filled [], SItem Solid [], SItem Bold []]]
  let edgeAttrList = [ArrowSize 0.5, Color [WC (X11Color Black) Nothing], Style [SItem Bold []]]
      
  graphAttrs graphAttrList
  nodeAttrs nodeAttrList
  edgeAttrs edgeAttrList
  
  let (nodeList, edgeList) = genAssocsFromProgram p fName 0
      
  sequence [ node idx [textLabel (pack name)] | (idx, name) <- nodeList]
  sequence [ u --> v | (u, v) <- edgeList]
  
genAssocsFromProgram :: S.Program -> String -> Integer -> ([DotNodeInfo], [DotEdgeInfo])
genAssocsFromProgram (S.MonoFunc f) fName idx = 
  let n1 = (idx, "File \"" ++ fName ++ "\"")
      (nodeList1, edgeList1, retIdx) = genAssocsFromFuncDef f (idx + 1)
      e1 = (idx, idx + 1)
  in ([n1] ++ nodeList1, [e1] ++ edgeList1)
genAssocsFromProgram (S.PolyFunc fs) fName idx = 
  let n1 = (idx, "File \"" ++ fName ++ "\"")
      (nodeList, edgeList, retIdx) = g fs idx ([], [], idx+1)
  in  (n1 : nodeList, edgeList)
      where
        g :: [S.FuncDef] -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
                  -> ([DotNodeInfo], [DotEdgeInfo], Integer)
        g [] _ (nl, el, i) = (nl, el, i)
        g (fd:fds) idxParent (nl, el, idxBegin) = 
          let (nl1, el1, retIdx') = genAssocsFromFuncDef fd idxBegin
              e1 = (idxParent, idxBegin)
              nl' = nl ++ nl1
              el' = el ++ el1 ++ [e1]
          in  g fds idxParent (nl', el', retIdx')

genAssocsFromFuncDef :: S.FuncDef -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromFuncDef (S.Func name argList retList body) idx = 
  let nTag = (idx, "FuncDef")
      nName = (idx + 1, name)
      (nodeList1, edgeList1, retIdx1) = genAssocsFromReturnList retList (idx + 2)
      (nodeList2, edgeList2, retIdx2) = genAssocsFromArgList argList retIdx1
      (nodeList3, edgeList3, retIdx3) = genAssocsFromBody body retIdx2
      e1 = (idx, idx + 2)
      e2 = (idx, idx + 1)
      e3 = (idx, retIdx1)
      e4 = (idx, retIdx2)
      nl = [nTag] ++ nodeList1 ++ [nName] ++ nodeList2 ++ nodeList3
      el = edgeList1 ++ edgeList2 ++ edgeList3 ++ [e1, e2, e3, e4]
  in  (nl, el, retIdx3)

genAssocsFromReturnList :: S.RetList -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromReturnList (S.RetList []) idx = 
  let nTag = (idx, "Return")
  in  ([nTag], [], idx + 1)
genAssocsFromReturnList (S.RetList vl) idx = 
  let nTag = (idx, "Return")
      (nodeList, edgeList, retIdx) = g vl idx ([], [], idx + 1)
  in  ([nTag] ++ nodeList, edgeList, retIdx)
      where
        g :: [S.Var] -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
                  -> ([DotNodeInfo], [DotEdgeInfo], Integer)
        g [] _ (nl, el, i) = (nl, el, i)
        g (v:vs) idxParent (nl, el, idxBegin) = 
          let nVar = (idxBegin, v)
              e1 = (idxParent, idxBegin)
              nl' = nl ++ [nVar]
              el' = el ++ [e1]
              idxBegin' = idxBegin + 1
          in  g vs idxParent (nl', el', idxBegin')

genAssocsFromArgList :: S.ArgumentList -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromArgList (S.ArgList []) idx = 
  let nTag = (idx, "Argument")
  in  ([nTag], [], idx + 1)
genAssocsFromArgList (S.ArgList as) idx = 
  let nTag = (idx, "Argument")
      (nodeList, edgeList, retIdx) = g as idx ([], [], idx + 1) 
  in  ([nTag] ++ nodeList, edgeList, retIdx)
      where
        g :: [S.Arg] -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
               -> ([DotNodeInfo], [DotEdgeInfo], Integer)
        g [] _ (nl, el, i) = (nl, el, i)
        g (a:ass) idxParent (nl, el, idxBegin) = 
          let (nodeList, edgeList, retIdx) = genAssocsFromArg a idxBegin
              e1 = (idxParent, idxBegin)
              nl' = nl ++ nodeList
              el' = el ++ [e1] ++ edgeList
              idxBegin' = retIdx
          in  g ass idxParent (nl', el', idxBegin')

genAssocsFromArg :: S.Arg -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromArg (S.NullArg v) idx = 
  let nVar = (idx, v)
  in  ([nVar], [], idx + 1)
genAssocsFromArg (S.InitArg v n) idx = 
  let nTag = (idx, "=")
      nVar = (idx + 1, v)
      nInit = (idx + 2, show n)
      e1 = (idx, idx + 1)
      e2 = (idx, idx + 2)
  in ([nTag, nVar, nInit], [e1, e2], idx + 3)

genAssocsFromBody :: S.FuncBody -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromBody (S.Body stmts) idx =  
  let nTag = (idx, "Body")
      (nodeList, edgeList, retIdx) = g stmts idx ([], [], idx + 1)
  in  ([nTag] ++ nodeList, edgeList, retIdx)
      where
        g :: [S.Statement] -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
                 -> ([DotNodeInfo], [DotEdgeInfo], Integer)
        g [] _ (nl, el, i) = (nl, el, i)
        g (s:ss) idxParent (nl, el, idxBegin) = 
          let (nodeList, edgeList, retIdx) = genAssocsFromStatement s idxBegin
              e1 = (idxParent, idxBegin)
              nl' = nl ++ nodeList
              el' = el ++ [e1] ++ edgeList
              idxBegin' = retIdx
          in  g ss idxParent (nl', el', idxBegin')

genAssocsFromStatement :: S.Statement -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromStatement (S.ClosedStatement s) idx = 
  let nTag = (idx, "ClosedStmt")
      (nodeList, edgeList, retIdx) = genAssocsFromStmt s (idx+1)
      e1 = (idx, idx+1)
  in  ([nTag] ++ nodeList, [e1] ++ edgeList, retIdx)
genAssocsFromStatement (S.OpenStatement s) idx = 
  let nTag = (idx, "OpenStmt")
      (nodeList, edgeList, retIdx) = genAssocsFromStmt s (idx + 1)
      e1 = (idx, idx + 1)
  in  ([nTag] ++ nodeList, [e1] ++ edgeList, retIdx)

genAssocsFromStmt :: S.Stmt -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromStmt (S.Assign v rhs) idx = 
  let nTag = (idx, "=")
      nVar = (idx + 1, v)
      (nodeList, edgeList, retIdx) = genAssocsFromRHSExpr rhs (idx + 2)
      e1 = (idx, idx+1)
      e2 = (idx, idx+2)
  in  ([nTag, nVar] ++ nodeList, [e1, e2] ++ edgeList, retIdx)
genAssocsFromStmt (S.Return) idx = 
  let nTag = (idx, "Return")
  in  ([nTag], [], idx + 1)
genAssocsFromStmt (S.Break) idx = 
  let nTag = (idx, "Break")
  in  ([nTag], [], idx + 1)
genAssocsFromStmt (S.Continue) idx = 
  let nTag = (idx, "Continue")
  in  ([nTag], [], idx + 1)

genAssocsFromRHSExpr :: S.RHSExpr -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromRHSExpr (S.NumRHS ne) idx = 
  genAssocsFromNumExpr ne idx

genAssocsFromNumExpr :: S.NumExpr -> Integer -> ([DotNodeInfo], [DotEdgeInfo], Integer)
genAssocsFromNumExpr (S.NumInt n) idx = ([(idx, show n)], [], idx+1)
genAssocsFromNumExpr (S.NumReal n) idx = ([(idx, show n)], [], idx+1)
genAssocsFromNumExpr (S.Var v) idx = 
  let nTag = (idx, "Var")
      nVar = (idx+1, v)
      e1 = (idx, idx+1)
  in  ([nTag, nVar], [e1], idx+2)
genAssocsFromNumExpr (S.Bop op ne1 ne2) idx = 
  let nTag = (idx, show op)
      (nodeList1, edgeList1, retIdx1) = genAssocsFromNumExpr ne1 (idx+1)
      (nodeList2, edgeList2, retIdx2) = genAssocsFromNumExpr ne2 retIdx1
      e1 = (idx, idx+1)
      e2 = (idx, retIdx1)
  in  ([nTag] ++ nodeList1 ++ nodeList2, [e1, e2] ++ edgeList1 ++ edgeList2, retIdx2)
genAssocsFromNumExpr (S.Par ne) idx = 
  let nTag = (idx, "Par")
      (nodeList, edgeList, retIdx) = genAssocsFromNumExpr ne (idx+1)
      e1 = (idx, idx+1)
  in  ([nTag] ++ nodeList, [e1] ++ edgeList, retIdx)