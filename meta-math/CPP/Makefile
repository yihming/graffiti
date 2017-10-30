all:	CPP.g CPPGen.g MetaMath.java
	java org.antlr.Tool CPP.g CPPGen.g
	javac *.java

clean:	
	rm -f *.class *.tokens *~ 
	rm -f CPPLexer.java CPPParser.java CPPGen.java
