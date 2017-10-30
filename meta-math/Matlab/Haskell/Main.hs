module Main where

import System.Environment

import Frontend as FE
import VisualAST as PLT

main :: IO ()
main = do
  args <- getArgs
  let filename = head args
  src <- readFile filename
  let t = FE.parseMatlab src
  let astOutDot = filename ++ ".dot"
  PLT.genDotFile astOutDot t filename