import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;


public class MetaMath {
	public static void main(String[] args) throws Exception {
		String templateFilename = "CPP.stg";
		CharStream input = null;
		String sourceFilename;
		
		if (args.length > 0) {
			input = new ANTLRFileStream(args[0]);
			sourceFilename = new String(args[0]);
		} else {
			input = new ANTLRInputStream(System.in);
			sourceFilename = "input";
		}
		
		CPPLexer lexer = new CPPLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		CPPParser parser = new CPPParser(tokens);
		CPPParser.translation_unit_return r = parser.translation_unit();
		
		CommonTree t = (CommonTree)r.getTree();
		
		DOTTreeGenerator gen = new DOTTreeGenerator();
		StringTemplate st = gen.toDOT(t);
		
		// Write the AST content to file
		FileWriter fstream = new FileWriter(sourceFilename + ".dot");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(st.toString());
		out.close();
		
		// LOAD TEMPLATES (via classpath)
        ClassLoader cl = MetaMath.class.getClassLoader();
        InputStream in = cl.getResourceAsStream(templateFilename);
        Reader rd = new InputStreamReader(in);
        StringTemplateGroup templates = new StringTemplateGroup(rd);
        rd.close();
        
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
        nodes.setTokenStream(tokens);
        
        // Generate Code
        nodes.reset();
        CPPGen walker = new CPPGen(nodes);
        walker.setTemplateLib(templates);
        CPPGen.translation_unit_return ret2 = walker.translation_unit();
        
        // Write the C++ Code into File
        String genFilename = sourceFilename.substring(0, sourceFilename.lastIndexOf(".")) + ".output.cpp";
        FileWriter fstream2 = new FileWriter(genFilename);
        BufferedWriter out2 = new BufferedWriter(fstream2);
        out2.write(ret2.getTemplate().toString());
        out2.close();
	}
}
