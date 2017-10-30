tree grammar CPPGen;

options {
  tokenVocab = CPP;
  language = Java;
  output = template;
  ASTLabelType = CommonTree;
  backtrack = true;
}

scope CPPScope {
  String name;
  Map<String, Map<String, String>> metamathMap;
  String curRetval;
}

@header {
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
}

@members {
    String ctrMetaMathClass(String quantity, String bindedtype) {
        String code = "class " + quantity + " {\n";
        code = code + "private:\n\t" + bindedtype + "  member;\n" + "public:\n\t";
        code = code + quantity + "() {}\n\t";
        code = code + quantity + "(const " + bindedtype + " newMember): member(newMember) {}\n\t";
        code = code + "operator " + bindedtype + "() { return member; }\n\t";
        code = code + quantity + "& operator=(const " + bindedtype + " newMember) {\n\t\t";
        code = code + "member = newMember;\n\t\t";
        code = code + "return *this;\n\t}\n};\n";
        
        return code;
    }
}

translation_unit
scope CPPScope;
@init {
    $CPPScope::name = "global";
    $CPPScope::metamathMap = new LinkedHashMap<String, Map<String, String>>();
    List<String> metamathList = new ArrayList<String>();
}
    :     ^(FILE (header_declarations)? ed+=external_declaration*)
          {
                Set s = $CPPScope::metamathMap.entrySet();
                Iterator i = s.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> subMap = (Map<String, String>)entry.getValue();
                    if (subMap.containsKey("quantity")) {
                        String curQuan = "class " + subMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                    
                }
                
          }
        ->    file(header={$header_declarations.st}, decls={$ed}, metamath={metamathList})
    ;
    
header_declarations
    :   ^(HEADER p+=PreProcDirective+)
        ->    headerGen(items={$p})
    ;
    
external_declaration
    :     function_definition  ->  {$function_definition.st}
    |     (metamath_block)* declaration[true]    ->    {$declaration.st}
    |     namespace_definition      ->      {$namespace_definition.st}
    |     class_whole_block         ->      {$class_whole_block.st}
    ;   

template_head
    :     ^('template' template_parameter_list)    ->   templateGen(tplist={$template_parameter_list.st})
    ;
    
template_parameter_list
    :     ^(TPLIST (p+=tpl_parameter_decl)* )   ->    tplistGen(params={$p})
    ;

tpl_parameter_decl
@init {
    String tag = "";
    boolean assignFlag = false;
}
    :     ^(('class' {tag = "class";} | 'typename' {tag = "typename";}) (ID)? (td=type_declaration {assignFlag = true;})?) 
          ->  tplParamGen1(tag={tag}, name={$ID.text}, flag={assignFlag}, value={$td.prefixTags + $td.typeName})
    |
          parameter_decl    ->    {$parameter_decl.st}
    ;

// Declarations within class definitions.
member_declaration
    :     function_definition  ->  {$function_definition.st}
    |     (metamath_block)* declaration[true]    ->    {$declaration.st}
    |     namespace_definition      ->      {$namespace_definition.st}
    |     class_whole_block         ->      {$class_whole_block.st}
    |     access_specifier          ->      accessSpecifierGen(name={$access_specifier.name})
    |     ctor_definition           ->      {$ctor_definition.st}
    ; 
    
class_whole_block
@init {
    boolean isExtern = false;
    List<String> functionSpecs = new ArrayList<String>();
}
    :     ^(CLASS ('extern' {isExtern = true;})? (function_specifier {functionSpecs.add($function_specifier.name);})* 
                  class_decl_or_def (^(VARLIST (m+=member_declarator["", "", false])+))?
           )
          ->  classWholeGen(isExtern={isExtern}, func_specs={functionSpecs}, restDecl={$class_decl_or_def.st}, varList={$m})
    ;

class_decl_or_def
scope CPPScope;
@init {
    $CPPScope::name = "class";
    $CPPScope::metamathMap = new LinkedHashMap<String, Map<String, String>>();
    List<String> metamathList = new ArrayList<String>();
}
    :     ^(CLASSDECL c1=class_specifier (d1+=declspec_expr)* q1=qualified_id {$CPPScope::name = "_" + $q1.name;} (member_declarator["", "", false])?)
          ->  classdeclGen(head={$c1.name}, declspecs={$d1}, name={$q1.st}, var={$member_declarator.st})
    |     ^(CLASSDEF c2=class_specifier (d2+=declspec_expr)* (q2=qualified_id {$CPPScope::name += "_" + $q2.name;})? {$CPPScope::name += "_level" + $CPPScope.size();} 
              (base_clause)? ^(BLOCK (m+=member_declaration)*)
           )
          {
                Set s = $CPPScope::metamathMap.entrySet();
                Iterator i = s.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> subMap = (Map<String, String>)entry.getValue();
                    
                    if (subMap.containsKey("quantity")) {
                        String curQuan = "class " + subMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                    
                }
                
          }
          ->  classdefGen(head={$c2.name}, declspecs={$d2}, name={$q2.st}, baseclause={$base_clause.st}, body={$m}, metamath={metamathList})
    ;

base_clause
@init {
    List<String> specifierList = new ArrayList<String>();
}
    :     ^(BASECLAUSE (base_specifier {specifierList.add($base_specifier.name);} )+)
          ->   baseClauseGen(content={specifierList})
    ;

ctor_definition
    :     ^(CTOR ctor_head ctor_body)   ->  ctorDefGen(head={$ctor_head.st}, body={$ctor_body.st})
    ;

ctor_head
@init {
    String prefixTags = "";
}
    :     ^(CTORHEAD ^(CTORDECLSPEC (ctor_decl_spec {prefixTags += $ctor_decl_spec.name; prefixTags += " ";})*) 
                      ctor_declarator
           )
          ->  ctorHeadGen(prefix={prefixTags}, content={$ctor_declarator.st})
    ;
    
ctor_decl_spec returns [String name]
    :     (('inline' {$name = "inline";} | '_inline' {$name = "_inline";} | '__inline' {$name = "__inline";}) | 'explicit' {$name = "explicit";})
    ;
    
ctor_declarator
    :     ^(CTORDECL  qualified_id  (parameter_list)? )
          ->    ctorDeclaratorGen(name={$qualified_id.st}, params={$parameter_list.st})
    ;
    
ctor_body
@init {
    boolean hasInitializer = false;
}
    :     ^(CTORBODY  (ctor_initializer {hasInitializer = true;})? compound_statement)
          ->    ctorBodyGen(init={$ctor_initializer.st}, hasInitializer={hasInitializer}, content={$compound_statement.st})
    ;
    
ctor_initializer
    :     ^(CTORINIT  (s+=superclass_init)+)   ->  ctorInitializerGen(items={$s})
    ;
    
superclass_init
    :     ^(CTORINITITEM  qualified_id  (expression)?)
          ->    superclassInitGen(name={$qualified_id.st}, expr={$expression.st})
    ;
    
base_specifier returns [String name]
@init {
      $name = "";
}
    :         
    ( 
          'virtual' {$name += "virtual ";} (a1=access_specifier {$name += $a1.name + " ";})? q1=qualified_type {$name += $q1.name;} 
    |
          a2=access_specifier {$name += $a2.name + " ";} ('virtual' {$name += "virtual ";})? q2=qualified_type {$name += $q2.name;}
    |
          q3=qualified_type {$name += $q3.name;}
    )
    ;
    
qualified_type returns [String name]
@init {
    $name = "";
    String scopeName = "";
    boolean isTemplate = false;
}
    :      ^(ID scope_override
                {
                    // Fetch the complete scope name.
                    while (!$scope_override.scopeStack.empty()) {
                        String curScope = (String)$scope_override.scopeStack.pop();
                        if (!curScope.equals("global")) {
                            scopeName = curScope + "::" + scopeName;
                        } else {
                            scopeName = "::" + scopeName;
                        }
                    }
                    $name = scopeName;
                
                    // Get the type name.
                    $name += $ID.text;
                
                    // Get the template argument names.
                
                }
                (   {$name += "< "; isTemplate = true;}
                    ^(TALIST (type_declaration
                            {
                                String typeArgName = $type_declaration.prefixTags + $type_declaration.typeName;
                                $name = $name + typeArgName + ", ";
                            }
                          )+)
                    {
                        if ($name.contains(", ")) {
                            $name = $name.substring(0, $name.length() - 2);
                        }
                        $name += " >";
                    }
                )?
             )
           
          
    ;

access_specifier returns [String name]
    :     
          'public' {$name = "public";} | 'protected' {$name = "protected";} | 'private' {$name = "private";}
    ;
    

declspec returns [String name]
    :   ('_declspec' {$name = "_declspec";} | '__declspec'  {$name = "__declspec";})
    ;

declspec_expr
    :     declspec expression     ->   declspecExprGen(head={$declspec.name}, content={$expression.st})
    ;

type_qualifier returns [String name]
    :     (
            'const'   {$name = "const";} 
          | 'volatile'    {$name = "volatile";}
          )
    ;

function_specifier returns [String name]
    :     ('inline' {$name = "inline";} | '_inline' {$name = "_inline";} | '__inline' {$name = "__inline";}) 
    |     'virtual'   {$name = "virtual";}
    |     'explicit'   {$name = "explicit";}
    ;

storage_class_specifier returns [String name]
    :     'auto' {$name = "auto";}  
    |     'register' {$name = "register";}  
    |     'static'  {$name = "static";}  
    |     'extern'  {$name = "extern";}  
    |     'mutable'   {$name = "mutable";}
    ;

member_declarator[String prefixTag, String typeName, boolean isDecl] returns [String name]
@init {
  boolean isPointer = false;
  boolean hasMetaMath = false;
  String metamathName = "";
  boolean isInit = false;
}
    :     ^(VAR q1=qualified_id {$name = $q1.name;}  (e1=expression {isInit = true;})?) 
          {
              if (isDecl) {
                  if ($CPPScope::metamathMap.containsKey($name)) {
                    // Substitute its binded MetaMath quantity for the original type.
                    Map<String, String> varMap = $CPPScope::metamathMap.get($name);
                    if (varMap.containsKey("quantity")) {
                        metamathName = varMap.get("quantity");
                        hasMetaMath = true;
                    }
                     
                } 
              }
          }
          ->  varGen(name={$q1.st}, isPointer={isPointer}, isDecl={$isDecl}, prefix={prefixTag}, type={$typeName}, metamath={metamathName}, hasMetaMath={hasMetaMath},
                      isInit={isInit}, init={$e1.st})
    |     ^(VAR ^(POINTER scope_override ptr_operator q2=qualified_id {$name = $q2.name;}) (e2=expression {isInit = true;})?) {isPointer = true;} 
          {
              if (isDecl) {
                  if ($CPPScope::metamathMap.containsKey($name)) {
                    // Substitute its binded MetaMath quantity for the original type.
                    Map<String, String> varMap = $CPPScope::metamathMap.get($name);
                    if (varMap.containsKey("quantity")) {
                        metamathName = varMap.get("quantity");
                        hasMetaMath = true;
                    } 
                } 
              }
          }
       ->  varGen(name={$q2.st}, isPointer={isPointer}, ptrOpt={$ptr_operator.name}, isDecl={$isDecl}, prefix={prefixTag}, type={$typeName}, metamath={metamathName}, hasMetaMath={hasMetaMath},
                  isInit={isInit}, init={$e2.st})
    ;


namespace_definition
scope CPPScope;
@init {
    $CPPScope::metamathMap = new LinkedHashMap<String, Map<String, String>>();
    List<String> metamathList = new ArrayList<String>();
}
    :     ^(NAMESPACE (ID)? (e+=external_declaration)*)
          {
                Set s = $CPPScope::metamathMap.entrySet();
                Iterator i = s.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> subMap = (Map<String, String>)entry.getValue();
                    
                    if (subMap.containsKey("quantity")) {
                        String curQuan = "class " + subMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                    
                }
                
          }
          ->    namespaceGen(name={$ID.text}, content={$e}, metamath={metamathList})
    ;
    
declaration[boolean isStmt]
    :     using_statement   ->    {$using_statement.st}
    |     simple_declaration[isStmt]    ->    {$simple_declaration.st}
    ;
    
metamath_block
    :     ^(METAMATH 'param' qualified_id  metamath_rest)
          {
                String varName = $qualified_id.name;
                if ($CPPScope::metamathMap.containsKey(varName)) {
                    Map<String, String> varMap = $CPPScope::metamathMap.get(varName);
                    
                    if ($metamath_rest.attrMap.containsKey("quantity")) {
                        String quanName = $metamath_rest.attrMap.get("quantity");
                        varMap.put("quantity", quanName);
                    } else {
                        varMap.put("quantity", "");
                    }
                    
                } else {
                    
                    $CPPScope::metamathMap.put(varName, $metamath_rest.attrMap);
                }
                
          }
    |     ^(METAMATH 'retval' metamath_rest)
          {
                String quanName = "";
                if ($CPPScope::metamathMap.containsKey("retvalStack")) {
                    Map<String, String> retMap = $CPPScope::metamathMap.get("retvalStack");
                    if ($metamath_rest.attrMap.containsKey("quantity")) {
                        quanName = $metamath_rest.attrMap.get("quantity");
                        
                    }
                    retMap.put("quantity", quanName);
                    
                    
                } else {
                    $CPPScope::metamathMap.put("retvalStack", $metamath_rest.attrMap);
                
                }
                $CPPScope::curRetval = $metamath_rest.attrMap.get("quantity");
                
          }
    ;

metamath_rest returns [Map<String, String> attrMap]
@init {
    $attrMap = new LinkedHashMap<String, String>();
}
    :     ^(METAMATHINFO  (metamath_entry {$attrMap.put($metamath_entry.attr, $metamath_entry.content);})* )
    ;
    
metamath_entry returns [String attr, String content]
@init {
    $attr = "";
    $content = "";
}
    :     ^(METAMATHENTRY metamath_attribute  metamath_content)
          {
                $attr = $metamath_attribute.name;
                $content = $metamath_content.name;
          }
    ;
    
metamath_attribute returns [String name]
@init {
    $name = "";
}
    :     'quantity' {$name = "quantity";}
    |     'unit'    {$name = "unit";}
    ;

metamath_content returns [String name]
@init {
    $name = "";
}
    :     ID  {$name = $ID.text;}
    |     metamath_unit 
    ;
    
metamath_unit
    :     ID BITWISEXOR '0..9' (metamath_unit)?
    |     ID BITWISEXOR LCURLY (MINUS)? Number RCURLY (metamath_unit)?
    ;
    
simple_declaration[boolean isStmt]
@init {
    //boolean flag = false;
    //String metamathName = new String();
    //boolean isInit = false;
    String prefixTags = "";
}
    :    // ^(DECL ^(TYPE t=simple_type_specifier) ^(VARLIST ^(VAR v=ID (e=expr {isInit = true;})?)))
         // {
         //       if ($CPPScope::metamathMap.containsKey($v.text)) {
         //           // Substitute its binded MetaMath quantity for the original type.
         //           List<String> quanList = $CPPScope::metamathMap.get($v.text);
         //           metamathName = quanList.get(quanList.size() - 1);
         //           flag = true; 
         //       }
         // }
          
         // ->    declGen(flag={flag}, name={$v.text}, isInit={isInit}, init={$e.st}, typeName={$t.name}, metamathName={metamathName}, isStmt={isStmt})
    //|
          ^(DECL type_declaration ^(VARLIST (md+=member_declarator[$type_declaration.prefixTags, $type_declaration.typeName, isStmt])+))
          ->    vardeclGen(content={$md})
    ;
    


function_definition
scope CPPScope;
@init {
    $CPPScope::metamathMap = new LinkedHashMap<String, Map<String, String>>();
    List<String> paramMetaMathList = new ArrayList<String>();
    List<String> paramMetaMathCodeList = new ArrayList<String>();
    List<String> metamathList = new ArrayList<String>();
    String returnType = "";
}
@after {  // For runtime debugging.
    System.out.println("==============\n" + $CPPScope::name);
    Iterator it = $CPPScope::metamathMap.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry entry = (Map.Entry)it.next();
        String key = (String)entry.getKey();
        Map<String, String> varMap = (Map<String, String>)entry.getValue();
        System.out.println(key + ":");
        System.out.println(varMap);
    }
}
    :
          metamath_block+
          {
              Set sets = $CPPScope::metamathMap.entrySet();
              Iterator i = sets.iterator();
              while (i.hasNext()) {
                  Map.Entry entry = (Map.Entry)i.next();
                  Map<String, String> varMap = (Map<String, String>)entry.getValue();
                  
                  if (varMap.containsKey("quantity")) {
                      String curQuan = varMap.get("quantity");
                      paramMetaMathList.add(curQuan);
                  }
              }
              
          }
          ^(FUNCDEF (th1=template_head)? 
                ^(FUNCDEFBODY td1=type_declaration
                    {   // Return type.
                        if ($CPPScope::curRetval != null) { // The return type has a Meta-Math quantity.
                            returnType = $CPPScope::curRetval;
                            String originType = $td1.typeName;
                            
                            // Construct the class with regard to its binded primitive type.
                            String code = ctrMetaMathClass(returnType, originType);
                            
                            paramMetaMathCodeList.add(code);
                            
                        } else {
                            returnType = $td1.prefixTags + $td1.typeName;
                        }
                    }
                    v1=qualified_id {$CPPScope::name = $v1.name;} 
                    (p1=parameter_list
                        {
                            Iterator i = paramMetaMathList.iterator();
                            while (i.hasNext()) {
                                String curQuan = (String)i.next();
                                if ($p1.paramlistMap.containsKey(curQuan)) {
                                    // Construct the class with regard to its binded primitive type.
                                    String bindedType = $p1.paramlistMap.get(curQuan);
                                    String code = ctrMetaMathClass(curQuan, bindedType);
                                    
                                    paramMetaMathCodeList.add(code);
                                }
                            }
                        }
                    )? 
                    {
                        $CPPScope::metamathMap.clear();
                    } 
                    ^(BLOCK (s1+=statement)*)
                )
           )
          {
                Set sets = $CPPScope::metamathMap.entrySet();
                Iterator i = sets.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> varMap = (Map<String, String>)entry.getValue();
                    
                    if (varMap.containsKey("quantity")) {
                        String curQuan = "class " + varMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                }
                
          }
          
          -> funcdefGen(template={$th1.st}, type={returnType}, name={$v1.st}, paramlist={$p1.st}, 
                        body={$s1}, metamath={metamathList}, paramMetaMath={paramMetaMathCodeList})
          
    |     
          ^(FUNCDEF (th2=template_head)? ^(FUNCDEFBODY td2=type_declaration v2=qualified_id {$CPPScope::name = $v2.name;} (p2=parameter_list)? ^(BLOCK (s2+=statement)*)))
    
          {
                Set sets = $CPPScope::metamathMap.entrySet();
                Iterator i = sets.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> varMap = (Map<String, String>)entry.getValue();
                    
                    if (varMap.containsKey("quantity")) {
                        String curQuan = "class " + varMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                }
          } 
          ->  funcdefGen(template={$th2.st}, type={$td2.prefixTags + $td2.typeName}, name={$v2.st}, paramlist={$p2.st}, body={$s2}, metamath={metamathList})
    ;

parameter_list returns [Map<String, String> paramlistMap]
@init {
    boolean isVoid = false;
    $paramlistMap = new LinkedHashMap<String, String>();
    List<Object> paramDeclList = new ArrayList<Object>();
}
    :     ^(ARGS 'void'{isVoid = true;})    ->   paramListGen(isVoid={isVoid})
    |   
          ^(ARGS (p=parameter_decl 
                     {  
                         
                            $paramlistMap.put($p.metamathQuan, $p.typeName);
                            paramDeclList.add(p.getTemplate());
                      } 
                      
                  )+
            )    
                ->    paramListGen(params={paramDeclList}, isVoid={isVoid})
    ;

parameter_decl returns [String typeName, String metamathQuan]
@init {
    boolean metamathFlag = false;
    boolean suffixsqFlag = false;
    $metamathQuan = "";
    $typeName = "";
}
    :     ^(ARG (td1=type_declaration) (v=qualified_id) (^(SUFFIXSQ (e1=expr) {suffixsqFlag = true;}))?)     
          {
                // Get the type name.
                $typeName = $td1.typeName;
                
                // Get the associated Meta-Math quantity if exists.
                if ($CPPScope::metamathMap.containsKey($v.name)) {
                    Map<String, String> varMap = $CPPScope::metamathMap.get($v.name);
                    if (varMap.containsKey("quantity")) {
                        $metamathQuan = varMap.get("quantity");
                        metamathFlag = true;
                    }
                    
                }
                
          }
    
          ->    paramGen(metamathflag={metamathFlag}, prefix={$td1.prefixTags}, type={$td1.typeName}, name={$v.st}, 
                         suffix_expr={$e1.st}, metamath={$metamathQuan}, suffixsqflag={suffixsqFlag})
    |
          ^(ARG (td2=type_declaration) ^(POINTER ptr_operator (q=qualified_id)) (^(SUFFIXSQ (e2=expr) {suffixsqFlag = true;}))?)
           {
                // Get the type name.
                $typeName = $td2.prefixTags + $td2.typeName;
                
                // Get the associated Meta-Math quantity if exists.
                if ($CPPScope::metamathMap.containsKey($q.name)) {
                    Map<String, String> varMap = $CPPScope::metamathMap.get($q.name);
                    if (varMap.containsKey("quantity")) {
                        $metamathQuan = varMap.get("quantity");
                        metamathFlag = true;
                    }
                }
           }                      
          ->    paramGen(metamathflag={metamathFlag}, prefix={$td2.prefixTags}, type={$td2.typeName}, name={"(" + $ptr_operator.name + $q.name + ")"}, 
                          suffix_expr={$e2.st}, metamath={$metamathQuan}, suffixsqflag={suffixsqFlag})
            
    ;


prefix_declaration_specifier returns [String name]
    :
    (
        'typedef'  {$name = "typedef";}
    |
        'friend'    {$name = "friend";}
    |
        storage_class_specifier   {$name = $storage_class_specifier.name;}
    |
        type_qualifier  {$name = $type_qualifier.name;}
    |
        function_specifier    {$name = $function_specifier.name;}
    |
        ^(declspec ID)    {$name = $declspec.name + "[" + $ID.text + "]";}
    )
    ;
    
////////////////////////////////////////////////////////
//////////////// Statement /////////////////////////////
///////////////////////////////////////////////////////
    
    
statement
    :     simple_declaration[true]    ->    {$simple_declaration.st}
    |     jump_statement    -> {$jump_statement.st}
    |     metamath_block  
    |     compound_statement  ->  {$compound_statement.st}
    |     expression      ->    expressionStmtGen(content={$expression.st})
    |     selection_statement    ->   {$selection_statement.st}
    |     case_statement    ->    {$case_statement.st}
    |     default_statement   ->    {$default_statement.st}
    |     iteration_statement   ->    {$iteration_statement.st}
    ;
    
compound_statement
scope CPPScope;
@init {
    $CPPScope::metamathMap = new LinkedHashMap<String, Map<String, String>>();
    List<String> metamathList = new ArrayList<String>();
    $CPPScope::name = "" + $CPPScope.size();
}
    :     ^(BLOCK (s+=statement)*)
          {
                Set sets = $CPPScope::metamathMap.entrySet();
                Iterator i = sets.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    Map<String, String> varMap = (Map<String, String>)entry.getValue();
                    
                    if (varMap.containsKey("quantity")) {
                        String curQuan = "class " + (String)varMap.get("quantity") + " {};";
                        metamathList.add(curQuan);
                    }
                    
                }
                
          }
          
          -> bodyGen(content={$s}, metamath={metamathList})
    ;
    
jump_statement
    :     
          ^('goto' ID)   ->   gotoGen(id={$ID.text})
    |
          'continue'  ->   constStmtGen(content={"continue"})
    |
          'break'      ->  constStmtGen(content={"break"})
    |
          ^('return' (
                          ID (e1=expression)?    -> returnGen(id={$ID.text}, expr={$e1.st})
                     |
                          e2=expression  ->  returnGen(expr={$e2.st})
                     |
                          -> returnGen()
                      ))
    ;

selection_statement
@init {
  boolean hasElse = false;
}
    :     ^('if' e1=expression s1=statement (s2=statement {hasElse = true;})?)
          ->    ifGen(cond={$e1.st}, thenBranch={$s1.st}, elseBranch={$s2.st}, flag={hasElse})
    |     ^('switch' e2=expression s3=statement)
          ->    switchGen(cond={$e2.st}, content={$s3.st})
    ;

case_statement
    :     ^('case' expr statement)
          ->    caseGen(cond={$expr.st}, content={$statement.st})
    ;
    
default_statement
    :   ^('default' statement)
          ->    defaultGen(content={$statement.st})
    ;
    
iteration_statement
    :
          ^('for' ^(FORINIT f1=forcontrolClause) ^(FOREND  e6+=expression?) ^(FORUPDATE f2=forcontrolClause) s4=statement)
          ->  forGen(forinit={$f1.st}, forend={$e6}, forupdate={$f2.st}, content={$s4.st})
    |
          ^('while' e1=expression s1=statement)
          ->    whileGen(cond={$e1.st}, content={$s1.st})
    |
          ^('do' s2=statement e2=expression)
          ->    dowhileGen(content={$s2.st}, cond={$e2.st})
    ;

forcontrolClause
    :     simple_declaration[false]     ->  forinitGen(content={$simple_declaration.st})
    |     expression       ->  forinitGen(content={$expression.st})
    |     ->  forinitGen()
    ;
    
using_statement
    :     ^(USINGNSPS qualified_id)   -> usingGen(name={$qualified_id.st}, flag={1})
    |     ^(USINGTYPE qualified_id)   -> usingGen(name={$qualified_id.st}, flag={0})
    ;

//////////////////////////////////////
/////////// expression ///////////////
/////////////////////////////////////

expression
    :     ^((EXPR | ELIST) (e+=expr)+)      ->    expressionlistGen(content={$e})
    ;

expr
    :     ^(assign_operator p1=primary_expr e1=expr)   ->  assignExprGen(op={$start.token}, left={$p1.st}, right={$e1.st})
    |     postfix_expression    ->    {$postfix_expression.st}
    |     unary_expression      ->    {$unary_expression.st}
    |     cast_expression     ->      {$cast_expression.st}
    |     ^(QUESTIONMARK eq1=expr eq2=expression eq3=expr)    ->  topGen(left={$eq1.st}, middle={$eq2.st}, right={$eq3.st})
    |     ^(binary_operator er1=expr er2=expr)     ->  bopGen(op={$start.token}, left={$er1.st}, right={$er2.st})
    ;

cast_expression
    :     ^(CAST (td1=type_declaration) unary_expression)    ->   castExprGen(type={$td1.prefixTags + $td1.typeName}, expr={$unary_expression.st})
    |     ^(CAST td2=type_declaration e=cast_expression)   ->   castExprGen(type={$td2.prefixTags + $td2.typeName}, expr={$e.st})
    ;

type_declaration returns [String prefixTags, String typeName]
@init {
  $prefixTags = "";
}
    :     ^(DECLSPECIFIERS (prefix_declaration_specifier {$prefixTags += $prefix_declaration_specifier.name; $prefixTags += " ";})* 
                           ^(TYPE simple_type_specifier {$typeName = $simple_type_specifier.name;})
           )
    ;

postfix_expression
    :     primary_expr      ->   {$primary_expr.st}
    |     call      ->  {$call.st}
    |     member    ->  {$member.st}
    |     postop_expr   ->   {$postop_expr.st}
    ;
    
postop_expr
    :     ^(POSTOP p1=postfix_expression (  PLUSPLUS    ->  postopExprGen(expr={$p1.st}, op={"++"})
                                         |  MINUSMINUS  ->  postopExprGen(expr={$p1.st}, op={"--"})
                                         )
            )   
    ;
    
call
    :     ^(CALL postfix_expression (expression)?  )     ->    callGen(name={$postfix_expression.st}, args={$expression.st})
    ;

member
    :     ^(DOT p1=postfix_expression ('template')? q1=qualified_id)    ->  memberDotGen(name={$p1.st}, member={$q1.st})
    |     ^(POINTERTO p2=postfix_expression ('template')? q2=qualified_id)  ->  memberPointerGen(name={$p2.st}, member={$q2.st})
    ;
    
array
    :     ^(ARRAY postfix_expression expression)   ->  arrayGen(name={$postfix_expression.st}, expr={$expression.st})
    ;

unary_expression
@init {
  boolean isConst = false;
  boolean isPtr = false;
}
    :     ^(unary_operator u=unary_expression)   ->  uopGen(op={$start.token}, oprand={$u.st})
    |     ^(cast_operator ('const' {isConst = true;})? ^(TYPE ts1=simple_type_specifier) (po=ptr_operator {isPtr = true;})? expression)
          ->    castOpGen(op={$cast_operator.name}, isConst={isConst}, type={$ts1.name}, isPtr={isPtr}, ptrop={$po.name}, expr={$expression.st})
    |     primary_expr    ->    {$primary_expr.st}
    ;

ptr_operator returns [String name]
    :     AMPERSAND {$name = "&";}
    |     ('_cdecl' {$name = "_cdecl";} | '__cdecl' {$name = "__cdecl";})
    |     ('_near' {$name= "_near";} | '__near' {$name = "__near";})
    |     ('_far' {$name = "_far";} | '__far' {$name = "__far";})
    |     '__interrupt' {$name = "__interrupt";}
    |     ('pascal' {$name = "pascal";} | '_pascal' {$name = "_pascal";} | '__pascal' {$name = "__pascal";})
    |     ('_stdcall' {$name = "_stdcall";} | '__stdcall' {$name = "__stdcall";})
    //|     (scope_override STAR cv_qualifier_seq)  =>  scope_override STAR cv_qualifier_seq
    |     STAR  {$name = "*";}
    ;

cast_operator returns [String name]
    :   'dynamic_cast' {$name = "dynamic_cast";}
    |   'static_cast'  {$name = "static_cast";}
    |   'reinterpret_cast'  {$name = "reinterpret_cast";}
    |   'const_cast'  {$name = "const_cast";}
    ;

unary_operator
    :     AMPERSAND | STAR | MINUS | TILDE | NOT | PLUSPLUS | MINUSMINUS
    ;

binary_operator
    :     OR | AND | BITWISEOR | BITWISEXOR | AMPERSAND | NOTEQUAL | EQUAL | LESSTHAN | GREATERTHAN | LESSTHANOREQUALTO | GREATERTHANOREQUALTO
        | SHIFTLEFT | SHIFTRIGHT | PLUS | MINUS | STAR | DIVIDE | MOD | DOTMBR | POINTERTOMBR
    ;  

assign_operator
    :   (ASSIGNEQUAL | TIMESEQUAL | DIVIDEEQUAL | MINUSEQUAL | PLUSEQUAL | MODEQUAL | SHIFTLEFTEQUAL | SHIFTRIGHTEQUAL | BITWISEANDEQUAL
              |  BITWISEXOREQUAL | BITWISEOREQUAL)
    ;
    
primary_expr
    :     qualified_id   ->  varGen(name={$qualified_id.st})
    |     literal   ->    varGen(name={$literal.value})
    |     'this'   ->  varGen(name={"this"})
    |     expression    ->   parenExpressionGen(content={$expression.st})
    |     array   ->    {$array.st}
    |     member    ->    {$member.st}
    ;
    
literal returns [String value]
@init {
    $value = "";
}
    :     (Number   {$value = $Number.text;}
          | CharLiteral   {$value = $CharLiteral.text;}
          | WCharLiteral   {$value = $WCharLiteral.text;}
          | StringLiteral {$value += $StringLiteral.text;} 
          | WStringLiteral {$value += $WStringLiteral.text;} 
          | FLOATONE   {$value = $FLOATONE.text;}
          | FLOATTWO   {$value = $FLOATTWO.text;}
          | 'true'   {$value = "true";}
          |'false'   {$value = "false";}
          )  
    ;


simple_type_specifier returns [String name]
    :   
    (
          (a=qualified_type)  {$name = $a.name;}
    |
          ( 'typename'  {$name = "typename";} 
          | 'enum'  {$name = "enum";}
          | class_specifier   {$name = $class_specifier.name;}
          ) 
          
          (b=qualified_type) {$name += $b.name;}
    |
          {$name = "";}
          (
              'char'   {$name += "char ";}
          |   'wchar_t' {$name += "wchar_t ";}
          |   'bool'  {$name += "bool ";}
          |   'short'  {$name += "short ";}
          |   'int'  {$name += "int ";}
          |   ('_int8' {$name += "_int8 ";} | '__int8' {$name += "__int8 ";})
          |   ('_int16' {$name += "_int16 ";} | '__int16'  {$name += "__int16 ";})
          |   ('_int32' {$name += "_int32 ";} | '__int32'  {$name += "__int32 ";})
          |   ('_int64' {$name += "_int64 ";} | '__int64'  {$name += "__int64 ";})
          |   ('_w64' {$name += "_w64 ";} | '__w64'  {$name += "__w64 ";})
          |   'long' {$name += "long ";}
          |   'signed' {$name += "signed ";}
          |   'unsigned' {$name += "unsigned ";}
          |   'float' {$name += "float ";}
          |   'double' {$name += "double ";}
          |   'void'  {$name += "void ";}
          )+
          
          {$name = $name.substring(0, $name.length()-1);}
    )
    ;
    
class_specifier returns [String name]
    :     ('class' {$name = "class";} | 'struct' {$name = "struct";} | 'union'  {$name = "union";})
    ;

scope_override returns [Stack scopeStack]
@init {
    $scopeStack = new Stack();
    boolean isTemplate = false;
}
    :     NOSCOPETAG   
    |     SCOPETAG  {$scopeStack.add("global");}      ->  scopeTagGen(isTemplate = {isTemplate})
    |     ^(SCOPETAG so=scope_override ID
                    (
                          {$scopeStack.push(new String($ID.text));}
                        ->    scopeTagGen(content={$ID.text}, isTemplate={isTemplate}, remain={$so.st})
                    |
                        (tpl=template_parameter_list) {isTemplate = true;} 
                        {$scopeStack.push(new String("template " + $ID.text));}
                        ->    scopeTagGen(content={$ID.text}, template={$tpl.st}, isTemplate={isTemplate}, remain={$so.st})
                    )
           )
    ;   
    
qualified_id returns [String name, Stack scopeStack]
    :     ^(ID scope_override)  {$name = $ID.text; $scopeStack = $scope_override.scopeStack;}   ->  qualifiedIdGen(name={$ID.text}, scopeTag={$scope_override.st})
    ;