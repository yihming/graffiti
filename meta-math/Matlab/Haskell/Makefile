SRCS = AbstractSyntax.hs Frontend.hs VisualAST.hs 

all: Main.hs $(SRCS)
	rm -f MetaMath
	ghc --make -o MetaMath Main.hs

clean: 
	rm -f *~ *.hi *.o MetaMath 