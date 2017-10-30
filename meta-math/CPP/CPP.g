grammar CPP;

options {
  language = Java;
  output = AST;
  ASTLabelType = CommonTree;
}


tokens {
  OPERATOR = 'operator';
  FILE; HEADER; FUNCDEF; VOID; ARGS; ARG; DEFAULT; TYPE; USINGNSPS; USINGTYPE; INITLIST; DECL;
  METAMATH; METAMATHINFO; METAMATHENTRY; VAR; POINTER; VARLIST; BLOCK; EXPR; TRIPLE_EXPR; NAMESPACE;
  CLASS; CLASSDECL; CLASSDEF; BASECLAUSE; CAST; FORINIT; FOREND; FORUPDATE; ELIST; ARRAY; CALL;
  POSTOP; TPLIST; SUFFIXSQ; DECLSPECIFIERS; FUNCDEFBODY; SCOPETAG; NOSCOPETAG; TALIST; CTOR;
  CTORHEAD; CTORDECL; CTORINIT; CTORINITITEM; CTORBODY; CTORDECLSPEC;
} 



translation_unit
    :   (header_declarations)?  (external_declaration)*   EOF   ->  ^(FILE  (header_declarations)? (external_declaration)*)
    ;


    
header_declarations
    :   (PreProcDirective)+     ->    ^(HEADER (PreProcDirective)+)
    ;


external_declaration
    :
    
          // Template explicit specialisation
          ('template' LESSTHAN GREATERTHAN) => 'template' LESSTHAN GREATERTHAN external_declaration
    |
          // All typedefs
          ('typedef') => 
          (
                ('typedef' 'enum') => 'typedef' enum_specifier (init_declarator_list)? SEMICOLON 
          |     
                (declaration_specifiers function_declarator SEMICOLON) =>  declaration
          |
                (declaration_specifiers (init_declarator_list)? SEMICOLON) =>  declaration
          |
                ('typedef' class_specifier) => 'typedef' class_decl_or_def (init_declarator_list)? SEMICOLON 
          )
    |
          // Class template declaration or definition
          (template_head (function_specifier)* class_specifier) => template_head (function_specifier)* class_decl_or_def (init_declarator_list)? SEMICOLON
    |
          // Templated functions and constructors matched here.
          template_head
          (
              // templated forward class decl, init/decl of static member in template
              (declaration_specifiers (init_declarator_list)? SEMICOLON)  => declaration_specifiers (init_declarator_list)? SEMICOLON
          |
              // Templated function declaration
              (declaration_specifiers function_declarator SEMICOLON)  =>  declaration
          |
              // Templated function definition
              (declaration_specifiers function_declarator LCURLY) => function_definition     ->   ^(FUNCDEF template_head function_definition)
          |
              // Templated constructor definition
                // JEL 4/3/96 Added predicate that works once the
                // restriction is added that ctor cannot be virtual
              ( ctor_decl_spec* 
              )  =>  ctor_definition
          )
    |
          // Enum definition (don't want to backtrack over this in other alts)
          ('enum' (ID)? LCURLY) =>  enum_specifier (init_declarator_list)? SEMICOLON!
    |
          // Destructor definition (templated or non-templated)
          ((template_head)? dtor_head[1] LCURLY) => (template_head)? dtor_head[1] dtor_body
    |
          // Constructor definition (non-templated)
          // JEL 4/3/96 Added predicate that works, once the
          // restriction is added that ctor cannot be virtual
          // and ctor_declarator uses a more restrictive id
          (ctor_decl_spec*) => ctor_definition
    |
          // User-defined type cast
          (('inline')? scope_override  conversion_function_decl_or_def)  =>  ('inline')? s1 = scope_override conversion_function_decl_or_def
    |
          // Function declaration
          (declaration_specifiers function_declarator SEMICOLON) =>  declaration_specifiers function_declarator SEMICOLON!
    |
          // Function definition
          ( declaration_specifiers function_declarator LCURLY) =>  function_definition    ->   ^(FUNCDEF function_definition)
    |
          // Function definition with int return assumed
          (function_declarator LCURLY) => function_definition
    |   
          // K & R Function definition
          (declaration_specifiers function_declarator declaration)  =>  function_definition     ->   ^(FUNCDEF function_definition)
    |
          // K & R Function definition with int return assumed
          (function_declarator declaration)    =>    function_definition        ->   ^(FUNCDEF function_definition)
    |
          // Class declaration or definition
          (('extern')? function_specifier* class_specifier)  =>  class_whole_block
    |
          // Copied from member_declaration 31/05/07
          (declaration_specifiers (init_declarator_list)? SEMICOLON) =>  declaration
    |
          // Namespace definition
          //'namespace' namespace_definition
          namespace_definition   
    |
          // Meta-Math Comment Declaration
          metamath_block
    |
          // Semicolon
          SEMICOLON
    | 
          // Anything else
          declaration
    
    ;

class_whole_block
    :     ('extern')? (function_specifier)* class_decl_or_def (init_declarator_list)? SEMICOLON
          ->      ^(CLASS ('extern')? (function_specifier)* class_decl_or_def (init_declarator_list)?)
    ;

member_declaration  
    :
    (
          // Template explicit specialization
          ('template' LESSTHAN GREATERTHAN)  =>  'template' LESSTHAN GREATERTHAN member_declaration
    |
          // All typedefs
          ('typedef')  =>  
          (
                ('typedef' 'enum')  =>  'typedef' enum_specifier (init_declarator_list)? SEMICOLON
          |
                (declaration_specifiers function_declarator SEMICOLON)  =>  declaration
          |
                (declaration_specifiers (init_declarator_list)? SEMICOLON)  =>  declaration
          |
                ('typedef' class_specifier)  =>  'typedef' class_decl_or_def (init_declarator_list)? SEMICOLON
          )
    |
          // Templated class definition or declaration
          (template_head (function_specifier)* class_specifier)  =>  template_head (function_specifier)* class_decl_or_def (init_declarator_list)? SEMICOLON
    |
          // Templated functions and constructors matched here.
          template_head
          (
                // templated forward class decl, init/decl of static member in template
                (declaration_specifiers (init_declarator_list)? SEMICOLON)  =>  declaration_specifiers (init_declarator_list)? SEMICOLON
          |
                // Templated function declaration
                (declaration_specifiers function_declarator SEMICOLON)  =>  declaration
          |
                // Templated function definition
                (declaration_specifiers function_declarator LCURLY)  =>  function_definition  ->  ^(FUNCDEF function_definition)
          |
                // Templated constructor declarator
                (ctor_decl_spec*  ctor_declarator[0] SEMICOLON)  =>  ctor_decl_spec* ctor_declarator[0] SEMICOLON
          |
                // Templated constructor definition
                (ctor_decl_spec* )  =>  ctor_definition
          |
                // Templated operator function
                conversion_function_decl_or_def
          |
                // Templated class definition
                class_head declaration_specifiers (init_declarator_list)? SEMICOLON
          )
    |
          // Enum definition (don't want to backtrack over this in other alts)
          ('enum' (ID)? LCURLY)  =>  enum_specifier (init_declarator_list)? SEMICOLON
    |
          // Constructor declaration
          (ctor_decl_spec*  ctor_declarator[0] SEMICOLON)  =>  ctor_decl_spec* ctor_declarator[0] SEMICOLON
    |
          // JEL Predicate to distinguish ctor from function
          // This works now that ctor cannot have VIRTUAL
          // It unfortunately matches A::A where A is not enclosing
          // class -- this will have to be checked semantically
          // Constructor definition
          ( ctor_decl_spec* 
            ctor_declarator[1]
            (COLON    // DEFINITION: ctor_initializer
            | LCURLY  // DEFINITION: (compound statement)?
            )  
          )  =>   ctor_definition   ->  ctor_definition
    |
          // No template_head allowed for dtor member
          // Backtrack if not a dtor (no TILDE)
          // Destructor definition
          (dtor_head[1] LCURLY)  =>  dtor_head[1] dtor_body
    |
          // Function declaration
          (declaration_specifiers function_declarator SEMICOLON)  =>  declaration_specifiers function_declarator SEMICOLON
    |
          // Access specifier
          access_specifier COLON   ->  access_specifier
    |
          // Funciton definition
          ((declaration_specifiers function_declarator LCURLY)  =>  function_definition   ->  ^(FUNCDEF function_definition))
    |
          // Class declaration or definition
          (('friend')? (function_specifier)* class_specifier)  =>  ('friend')? (function_specifier)* class_decl_or_def (init_declarator_list)? SEMICOLON
    |
          // Variable definition
          (declaration_specifiers (init_declarator_list)? SEMICOLON) => declaration    ->  declaration
    |
          // Member without a type (I guess it can only be a function declaration or definition)
          ((function_specifier)* function_declarator SEMICOLON)  =>  (function_specifier)* function_declarator SEMICOLON
    |
          // Member without a type (I guess it can only be a function definition)
          ((function_specifier)* function_declarator LCURLY)  =>  (function_specifier)* function_declarator compound_statement
    |
          // User-defined type cast
          (('inline')? conversion_function_decl_or_def)  =>  ('inline')? conversion_function_decl_or_def
    |
          // Hack to handle decls like "superclass::member",
          // to redefine access to private base class public members
          // Qualified identifier
          (qualified_id SEMICOLON)  =>   qualified_id  SEMICOLON
    |
          // Meta-Math Comment Declaration
          metamath_block
    |
          // Semicolon 
          SEMICOLON
    )
    ;
    
namespace_definition
    :     'namespace' (ID)? LCURLY (external_declaration)* RCURLY   ->  ^(NAMESPACE (ID)? (external_declaration)*)
    ;
    
namespace_alias_definition
    :     'namespace' ID ASSIGNEQUAL qualified_id SEMICOLON
    ;
    
function_definition
    :
    (     
          // {( !(LA(1)==SCOPE||LA(1)==ID) || qualifiedItemIsOneOf(qiType|qiCtor,0) )}? 
          declaration_specifiers function_declarator
          (
                (declaration)  =>  (declaration)*     // Possible for K & R definition
          )?
          
          compound_statement   ->   ^(FUNCDEFBODY  declaration_specifiers function_declarator (declaration)* compound_statement)
    |
          function_declarator
          (
                (declaration)  =>  (declaration)*
          )?
          compound_statement  ->  ^(FUNCDEFBODY VOID function_declarator (declaration)* compound_statement)
    )
    ;
    
declaration
    :     ('extern' StringLiteral)  =>  linkage_specification
    |     simple_declaration
    |     using_statement
    ;
    
linkage_specification
    :     'extern' StringLiteral (LCURLY (external_declaration)* RCURLY  |  declaration)
    ;
    
class_head
    :     // Used only by predicates
          ('struct'
          |'union'
          |'class'
          )
          (ID
                (LESSTHAN template_argument_list GREATERTHAN)?
                (base_clause)?
          )?
          LCURLY
    ;
    
declaration_specifiers
    :  
    (
          (prefix_declaration_specifier)*
          type_specifier
          (type_qualifier)*
    )   ->    ^(DECLSPECIFIERS (prefix_declaration_specifier)* type_specifier (type_qualifier)*)
    ;
    
prefix_declaration_specifier
    :
    (
        'typedef'
    |
        'friend'
    |
        storage_class_specifier
    |
        type_qualifier
    |
        function_specifier
    |
        declspec^ LPAREN! ID RPAREN!
    )
    ;
    
storage_class_specifier
    :     'auto'  |  'register'  |  'static'  |  'extern'  |  'mutable'
    ;
    
function_specifier
    :     ('inline' | '_inline' | '__inline') 
    |     'virtual'  
    |     'explicit'  
    ;

type_specifier
    :   simple_type_specifier   ->  ^(TYPE simple_type_specifier)
    ;
    
simple_type_specifier
    :   
    (
          //{qualifiedItemIsOneOf(qiType|qiCtor, 0)}?
          qualified_type
    |
          ('typename' | 'enum' | class_specifier) qualified_type
    |
          (
              'char'
          |   'wchar_t'
          |   'bool'
          |   'short'
          |   'int'
          |   ('_int8' | '__int8')
          |   ('_int16' | '__int16')
          |   ('_int32' | '__int32')
          |   ('_int64' | '__int64')
          |   ('_w64' | '__w64')
          |   'long'
          |   'signed'
          |   'unsigned'
          |   'float'
          |   'double'
          |   'void'
          )+
    )
    ;

qualified_type
    :     scope_override ID^ (LESSTHAN! template_argument_list GREATERTHAN!)?
    ;
    
class_specifier
    :     ('class' | 'struct' | 'union')
    ;
    
type_qualifier
    :     (
            'const' | 'volatile'
          )
    ;
    
class_decl_or_def
    :     class_specifier
          (declspec LPAREN expression RPAREN)*
          (
                qualified_id
                (
                      SEMICOLON       ->      ^(CLASSDECL class_specifier (declspec expression)* qualified_id)
                | 
                      member_declarator   ->   ^(CLASSDECL class_specifier (declspec expression)* qualified_id member_declarator)
                |
                      (base_clause)? LCURLY (member_declaration)* RCURLY   
                      ->  ^(CLASSDEF class_specifier (declspec expression)* qualified_id (base_clause)? ^(BLOCK (member_declaration)*))
                )
          |
                LCURLY (member_declaration)* RCURLY     ->      ^(CLASSDEF class_specifier (declspec expression)* ^(BLOCK (member_declaration)*))
          )
    ;
    
declspec
    :   ('_declspec' | '__declspec')
    ;
   
base_clause
    :     COLON base_specifier (COMMA base_specifier)*    ->  ^(BASECLAUSE base_specifier+)
    ;
    
base_specifier
    :     
    (
          'virtual' (access_specifier)? qualified_type 
    |
          access_specifier ('virtual')? qualified_type
    |
          qualified_type
    )
    ;
    
access_specifier:     'public' | 'protected' | 'private'
    ;
    
enum_specifier
    :     'enum'
          (
                LCURLY enumerator_list RCURLY
          |
                qualified_id (LCURLY enumerator_list RCURLY)?
          )
    ;
    
enumerator_list
    :     enumerator (COMMA (enumerator)?)*
    ;
    
enumerator
    :     ID (ASSIGNEQUAL constant_expression)?
    ;
    
// This matches a generic qualified identifier ::T::B::foo
 // (including OPERATOR).
 // It might be a good idea to put T::~dtor in here
 // as well, but id_expression in expr.g puts it in manually.
 // Maybe not, 'cause many people use this assuming only A::B.
 // How about a 'qualified_complex_id'?
//
qualified_id
    :      scope_override
          (
                ID^  ((LESSTHAN template_argument_list GREATERTHAN) =>  LESSTHAN template_argument_list GREATERTHAN)? 
                
          |
                OPERATOR optor^
          |
                TILDE^ id_expression
          )
          
    ;
    
typeID
    :     
          ID
    ;
    
init_declarator_list
    :     member_declarator (COMMA member_declarator)*   ->  ^(VARLIST member_declarator+)
    ;
    
member_declarator
    :     ((ID)? COLON constant_expression)  =>  (ID)? COLON constant_expression    -> ^(VAR ID? ^(EXPR constant_expression))
    |     declarator
          (
                (ASSIGNEQUAL OCTALINT SEMICOLON)  =>  ASSIGNEQUAL OCTALINT      ->    ^(VAR declarator ^(EXPR OCTALINT))
          |
                ASSIGNEQUAL initializer     ->      ^(VAR declarator ^(EXPR initializer))
          |
                LPAREN  expression_list RPAREN     ->      ^(VAR declarator expression_list)
          |
                ->    ^(VAR declarator)
          )
    ;
    
initializer
    :     remainder_expression 
    |     LCURLY initializer (COMMA (initializer)?)* RCURLY
    ;
    
declarator
    :     (ptr_operator)  =>  ptr_operator declarator     ->      ^(POINTER ptr_operator declarator)
    |     direct_declarator
    ;
    
direct_declarator
    :     (qualified_id LPAREN (RPAREN | declaration_specifiers))  =>  qualified_id LPAREN (parameter_list)? RPAREN (type_qualifier)* (exception_specification)?
    |     (qualified_id LPAREN qualified_id)  =>  qualified_id LPAREN expression_list RPAREN
    |     (qualified_id LSQUARE)  =>  qualified_id (LSQUARE (constant_expression)? RSQUARE)+
    |     (qualified_id RPAREN LPAREN)  =>  qualified_id    // Must be function declaration (see function_direct_declarator)
    |     qualified_id   
    |     LPAREN! declarator RPAREN! (declarator_suffix)?
    ;
    
declarator_suffix
    :     
    (
          (LSQUARE (constant_expression)? RSQUARE   ->   ^(SUFFIXSQ (constant_expression)?) )+
    |
          LPAREN (parameter_list)? RPAREN (type_qualifier)* (exception_specification)?
    )
    ;
    
conversion_function_decl_or_def
    :     OPERATOR declaration_specifiers (STAR | AMPERSAND)?
          (LESSTHAN template_parameter_list GREATERTHAN)?
          LPAREN (parameter_list)? RPAREN
          (type_qualifier)* (exception_specification)? 
          (
            compound_statement
          | SEMICOLON
          )
    ;

function_declarator
    :     (ptr_operator)  =>  ptr_operator function_declarator
    |     function_direct_declarator
    ;
    
function_direct_declarator
    :
    (
          LPAREN! declarator RPAREN!
    |     qualified_id
    )
    LPAREN! (parameter_list)? RPAREN! (type_qualifier)* (ASSIGNEQUAL OCTALINT)? (exception_specification)?
    ;
    
ctor_definition
    :     ctor_head ctor_body   ->   ^(CTOR ctor_head ctor_body)
    ;
    
ctor_head
    :     ctor_decl_spec* ctor_declarator[1]    ->   ^(CTORHEAD ^(CTORDECLSPEC ctor_decl_spec*) ctor_declarator)
    ;
    
ctor_decl_spec
    :     (('inline' | '_inline' | '__inline') | 'explicit')
    ;
    
ctor_declarator [int definition]
    :     qualified_ctor_id LPAREN (parameter_list)? RPAREN (exception_specification)?   ->   ^(CTORDECL qualified_ctor_id (parameter_list)? (exception_specification)?)
    ;
    
qualified_ctor_id
    :     scope_override ID^ 
    ;
    
ctor_body
    :     (ctor_initializer)? compound_statement    ->   ^(CTORBODY (ctor_initializer)? compound_statement)
    ;
    
ctor_initializer
    :     COLON superclass_init (COMMA superclass_init)*    ->   ^(CTORINIT superclass_init+)
    ;
    
superclass_init
    :     qualified_id LPAREN (expression_list)? RPAREN   ->    ^(CTORINITITEM qualified_id (expression_list)?)
    ;
    
dtor_head [int definition]
    :     dtor_decl_spec dtor_declarator[definition]
    ;
    
dtor_decl_spec
    :     (('inline' | '_inline' | '__inline' | 'virtual')*)
    ;
    
dtor_declarator [int definition]
    :     scope_override TILDE ID LPAREN ('void')? RPAREN (exception_specification)?
    ;
    
dtor_body
    :     compound_statement
    ;
    
parameter_list
    :     parameter_declaration_list (ELLIPSIS!)?
    ;
    
parameter_declaration_list
    :     'void'      ->    ^(ARGS 'void')  
    |   
          (parameter_declaration (COMMA parameter_declaration)*)    -> ^(ARGS parameter_declaration+)
    ;
    
parameter_declaration
    :     (
              /*{!((LA(1)==SCOPE) && (LA(2)==STAR||LA(2)==OPERATOR)) &&
                (!(LA(1)==SCOPE||LA(1)==ID) }?*/
              declaration_specifiers
              (
                  (declarator)  =>  declarator (ASSIGNEQUAL remainder_expression)?  ->   ^(ARG declaration_specifiers declarator (^(DEFAULT remainder_expression))?)
              |
                  abstract_declarator (ASSIGNEQUAL remainder_expression)?   ->    ^(ARG declaration_specifiers abstract_declarator (^(DEFAULT remainder_expression))?)
              )
              
          |
              (declarator)  =>  declarator (ASSIGNEQUAL remainder_expression)?    ->  ^(ARG declarator (^(DEFAULT remainder_expression))?)
          |
              ELLIPSIS (ASSIGNEQUAL remainder_expression)?    ->    ^(ARG ELLIPSIS (^(DEFAULT remainder_expression))? )
          )         
    ;
    
type_id
    :     declaration_specifiers abstract_declarator
    ;
    
abstract_declarator
    :     ptr_operator abstract_declarator
    |     (LPAREN abstract_declarator RPAREN (LSQUARE | LPAREN))  =>  LPAREN abstract_declarator RPAREN (abstract_declarator_suffix)
    |     (abstract_declarator_suffix)?
    ;
    
abstract_declarator_suffix
    :     (LSQUARE (constant_expression)? RSQUARE)+
    |     LPAREN (parameter_list)? RPAREN cv_qualifier_seq (exception_specification)?
    ;
    
exception_specification
    :     'throw' LPAREN ((scope_override ID (COMMA scope_override ID)*)? | ELLIPSIS) RPAREN
    ;
    
template_head
    :     'template'^ LESSTHAN! template_parameter_list GREATERTHAN!
    ;
    
template_parameter_list
    :     template_parameter (COMMA template_parameter)*   -> ^(TPLIST template_parameter+)
    ;
    
template_parameter
    :     
    (     type_parameter
    |     (parameter_declaration)  => parameter_declaration
    |     template_parameter_declaration
    )
    ;
    
type_parameter
    :     
    (
          ('class' | 'typename')^ (ID (ASSIGNEQUAL! assigned_type_name)?)?
    |
          template_head 'class' (ID (ASSIGNEQUAL assigned_type_name)?)?
    )
    ;
    
assigned_type_name
    :     
    (
          qualified_type abstract_declarator
    |
          simple_type_specifier abstract_declarator
    )
    ;
    
template_parameter_declaration
    :     (
                /*{!((LA(1)==SCOPE) && (LA(2)==STAR||LA(2)==OPERATOR)) &&
                  (!(LA(1)==SCOPE||LA(1)==ID)}?*/
                declaration_specifiers 
                (
                    (declarator)  =>  declarator
                |
                    abstract_declarator
                )
          |
                (declarator)  =>  declarator
          |
                ELLIPSIS
          )
          (ASSIGNEQUAL additive_expression)?
    ;
    
template_id
    :     ID LESSTHAN template_argument_list GREATERTHAN
    ;
    
template_argument_list
    :     template_argument (COMMA template_argument)*     ->  ^(TALIST template_argument+)
    ;
    
template_argument
    :     /*{( !(LA(1)==SCOPE||LA(1)==ID) }?*/
          type_id
    | 
          shift_expression // failed in iosfwd
    ;
    
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
//////////////////////////////  STATEMENTS ////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////


statement_list
    :     (statement)+
    ;
    
statement
    :     
    (
        ('namespace' | 'using')  =>  block_declaration
    |
        (('typedef')? class_specifier (qualified_id)? LCURLY)  =>  member_declaration
    |
        (declaration_specifiers ((ptr_operator)  =>  ptr_operator)? qualified_id)  =>    member_declaration
    |
        (labeled_statement)  =>  labeled_statement
    |
        case_statement
    | 
        default_statement
    |
        expression SEMICOLON!
    |
        compound_statement
    |
        selection_statement
    |
        iteration_statement
    |
        jump_statement
    |
        SEMICOLON!
    |
        try_block
    |
        throw_statement
    |
        metamath_block
    )
    ;

metamath_block
    :   
          MetaMathCommentTag MetaMathCommandTag 'param' qualified_id (metamath_rest)? SEMICOLON
          
          ->    ^(METAMATH 'param' qualified_id ^(METAMATHINFO metamath_rest?))
    |
          MetaMathCommentTag MetaMathCommandTag 'retval' (metamath_rest)? SEMICOLON
          ->    ^(METAMATH 'retval' ^(METAMATHINFO metamath_rest?))
    ;
    
metamath_rest
    :
          metamath_assign (COMMA! metamath_assign)*
    ;
    
metamath_assign
    :
          metamath_attribute ASSIGNEQUAL metamath_content   ->    ^(METAMATHENTRY metamath_attribute metamath_content)
    ;
    
metamath_attribute
    :     'quantity'
    |     'unit'
    ;

metamath_content
    :     ID
    |     metamath_unit
    ;
    
metamath_unit
    :     ID BITWISEXOR '0..9' (metamath_unit)?
    |     ID BITWISEXOR LCURLY (MINUS)? Number RCURLY (metamath_unit)?
    ;
    
block_declaration
    :     simple_declaration 
    |     namespace_alias_definition
    |     using_statement
    ;
    
// For global variables
simple_declaration
    :     declaration_specifiers (init_declarator_list)? SEMICOLON     ->   ^(DECL  declaration_specifiers  init_declarator_list?)
    ;
    
labeled_statement
    :     ID COLON statement
    ;
    
case_statement
    :     'case'^ constant_expression COLON! statement
    ;
    
default_statement
    :     'default'^ COLON! statement
    ;
    
compound_statement
    :     LCURLY (statement_list)? RCURLY    ->    ^(BLOCK (statement_list)?)
    ;
    
selection_statement
    :     'if'^ LPAREN! condition RPAREN! statement ('else'! statement)?
    |     'switch'^ LPAREN! condition RPAREN! statement
    ;
    
iteration_statement
    :     'while'^ LPAREN! condition RPAREN! statement   
    |     'do'^ statement 'while'! LPAREN! condition RPAREN! SEMICOLON! 
    |     'for' LPAREN 
          forInit
          (condition)? SEMICOLON
          (expression)?
          RPAREN statement      ->  ^('for' forInit ^(FOREND (condition)?) ^(FORUPDATE (expression)?) statement)   
    ;
    
forInit
    :   (
              (declaration)  =>  declaration   ->  ^(FORINIT declaration) 
          |
              (expression)? SEMICOLON          ->  ^(FORINIT (expression)?)
          )
    ;
    
condition
    :     
    (
          (declaration_specifiers declarator ASSIGNEQUAL)  =>  declaration_specifiers declarator ASSIGNEQUAL remainder_expression
    |
          expression
    )
    ;
    
jump_statement
    :     
    (
          'goto'^ ID SEMICOLON!  
    |
          'continue'^ SEMICOLON!
    |
          'break'^ SEMICOLON!
    |
          'return'^ 
          (
              (LPAREN  ID RPAREN)  =>  LPAREN! ID RPAREN! (expression)? SEMICOLON!  
          |
              expression SEMICOLON!   
          |   SEMICOLON!    
          ) 
          
    )
    ;
    
try_block
    :     'try' compound_statement (handler)*
    ;
    
handler
    :     'catch' LPAREN exception_declaration RPAREN compound_statement
    ;
    
exception_declaration
    :     parameter_declaration_list
    ;
    
throw_statement
    :     'throw' (assignment_expression)? SEMICOLON
    ;
    
using_statement
    :     'using' 
          (  'namespace' qualified_id SEMICOLON    ->     ^(USINGNSPS qualified_id)
          |  ('typename')? qualified_id SEMICOLON   ->    ^(USINGTYPE qualified_id)
          )  
    ;
    
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
//////////////////////////////  EXPRESSIONS ///////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

expression
    :       assignment_expression (COMMA assignment_expression)*    ->    ^(EXPR (assignment_expression)*)
    ;
    
// right-to-left for assignment op
assignment_expression
    :     conditional_expression   
          (
              assign_operator^ remainder_expression
          )?
    ;
    
assign_operator
    :   (ASSIGNEQUAL | TIMESEQUAL | DIVIDEEQUAL | MINUSEQUAL | PLUSEQUAL | MODEQUAL | SHIFTLEFTEQUAL | SHIFTRIGHTEQUAL | BITWISEANDEQUAL
              |  BITWISEXOREQUAL | BITWISEOREQUAL)
    ;

remainder_expression
    :     
    assignment_expression
    ;
    
conditional_expression
    :     logical_or_expression (
                                    QUESTIONMARK^ expression COLON! conditional_expression
                                )?
    ;
    
constant_expression
    :     conditional_expression
    ;
    
logical_or_expression
    :     logical_and_expression ((OR^ logical_and_expression))*
    ;
    
logical_and_expression
    :     inclusive_or_expression (AND^ inclusive_or_expression)*
    ;
    
inclusive_or_expression
    :     exclusive_or_expression (BITWISEOR^ exclusive_or_expression)*
    ;
    
exclusive_or_expression
    :     and_expression (BITWISEXOR^ and_expression)*
    ;
    
and_expression
    :     equality_expression (AMPERSAND^ equality_expression)*
    ;
    
equality_expression
    :     relational_expression ((NOTEQUAL^ | EQUAL^) relational_expression)*
    ;
    
relational_expression
    :     shift_expression
          (options{backtrack=true;}: ((LESSTHAN^ | GREATERTHAN^ | LESSTHANOREQUALTO^ | GREATERTHANOREQUALTO^)) shift_expression)?
    ;
    
shift_expression
    :     additive_expression ((SHIFTLEFT^ | SHIFTRIGHT^) additive_expression)*
    ;
    
additive_expression
    :     multiplicative_expression ((PLUS^ | MINUS^) multiplicative_expression)*
    ;
    
// ANTLR has trouble dealing with the analysis of the confusing unary/binary
// operators such as STAR, AMPERSAND, PLUS, etc...  
// With the #pragma (now "(options{warnWhenFollowAmbig = false;}:" etc.)
// we simply tell ANTLR to use the "quick-to-analyze" approximate lookahead
// as full LL(k) lookahead will not resolve the ambiguity anyway.  Might
// as well not bother.  This has the side-benefit that ANTLR doesn't go
// off to lunch here (take infinite time to read grammar).

multiplicative_expression
    :     pm_expression ((STAR^ | DIVIDE^ | MOD^) pm_expression)*
    ;
    
pm_expression
    :     cast_expression ((DOTMBR^ | POINTERTOMBR^) cast_expression)*
    ;
    
// The string "( ID" can be either the start of a cast or
///the start of a unary_expression.  However, the ID must
// be a type name for it to be a cast.  Since ANTLR can only hoist
// semantic predicates that are visible without consuming a token,
// the semantic predicate in rule type_name is not hoisted--hence, the
// rule is reported to be ambiguous.  I am manually putting in the
// correctly hoisted predicate.
//
// Ack! Actually "( ID" might be the start of "(T(expr))" which makes
// the first parens just an ordinary expression grouping.  The solution
// is to look at what follows the type, T.  Note, this could be a
// qualified type.  Yucko.  I believe that "(T(" can only imply
// function-style type cast in an expression (...) grouping.
//
// We DO NOT handle the following situation correctly at the moment:
// Suppose you have
//    struct rusage rusage;
//    return (rusage.fp);
//    return (rusage*)p;
// Now essentially there is an ambiguity here. If rusage is followed by any
// postix operators then it is an identifier else it is a type name. This
// problem does not occur in C because, unless the tag struct is attached,
// rusage is not a type name. However in C++ that restriction is removed.
// No *real* programmer would do this, but it's in the C++ standard just for
// fun..
//
// Another fun one (from an LL standpoint):
//
//   (A::B::T *)v;      // that's a cast of v to type A::B::T
//   (A::B::foo);    // that's a simple member access
//
// The qualifiedItemIs(1) function scans ahead to what follows the
// final "::" and returns qiType if the item is a type.  The offset of
// '1' makes it ignore the initial LPAREN; normally, the offset is 0.
 //
   
cast_expression
    :     (LPAREN type_id RPAREN unary_expression)  =>  LPAREN type_id RPAREN unary_expression    ->  ^(CAST type_id unary_expression)
    |     (LPAREN type_id RPAREN cast_expression)  =>  LPAREN type_id RPAREN cast_expression    ->    ^(CAST type_id cast_expression)
    |     unary_expression
    ;
    
unary_expression
    :   
    (
          (postfix_expression)  =>   postfix_expression
    |
          PLUSPLUS^ unary_expression
    |
          MINUSMINUS^ unary_expression
    |
          unary_operator^ cast_expression
    |
          ('sizeof'^ | '__alignof__'^) ( (unary_expression)  =>  unary_expression   |   LPAREN! type_id RPAREN!)
    |
          (SCOPE)? (new_expression | delete_expression)
    )
    ;
    
// Yiming: the first alternative is disabled in order to diminish its conflict with the normal function invocations.
// Yiming: I don't quite understand the syntax that the first alternative matches. Is it like
// Yiming:                              int(a, b, c)
// Yiming: or something else?
postfix_expression
    :   //  (simple_type_specifier LPAREN)  =>  simple_type_specifier LPAREN (RPAREN LPAREN (expression_list)? RPAREN  
        //                                                                   |  (expression_list)? RPAREN (DOT postfix_expression)?) 
    //|
          
          (primary_expression -> primary_expression) 
           (
                (
                    LSQUARE expression RSQUARE        ->  ^(ARRAY $postfix_expression expression)
                | LPAREN (expression_list)? RPAREN         ->  ^(CALL["CALL"] $postfix_expression (expression_list)?)
                | DOT ('template')? id_expression        ->  ^(DOT $postfix_expression ('template')? id_expression)
                | POINTERTO ('template')? id_expression   ->  ^(POINTERTO $postfix_expression ('template')? id_expression)
                | PLUSPLUS      ->  ^(POSTOP $postfix_expression PLUSPLUS)
                | MINUSMINUS    ->  ^(POSTOP $postfix_expression MINUSMINUS)
                )*
           )
    |
          ('dynamic_cast'^ | 'static_cast'^ | 'reinterpret_cast'^ | 'const_cast'^) LESSTHAN! ('const')? type_specifier (ptr_operator)? GREATERTHAN! 
          LPAREN! expression RPAREN!
    |
          'typeid'^ LPAREN! ((type_id)  =>  type_id  |  expression) RPAREN! ((DOT | POINTERTO) postfix_expression)?
    ;
    
primary_expression
    :     id_expression 
    |     literal
    |     'this'
    |     LPAREN! expression RPAREN!  
    ;
    
id_expression
    :     qualified_id
    ;
    
literal
    :     (Number | CharLiteral | WCharLiteral | (StringLiteral | WStringLiteral)+ | FLOATONE | FLOATTWO | 'true' |'false')
    ;
    
unary_operator
    :     AMPERSAND | STAR | MINUS | TILDE | NOT
    ;
    
// JEL The first ()? is used to resolve "new (expr) (type)" because both
// (expr) and (type) look identical until you've seen the whole thing.
//
// new_initializer appears to be conflicting with function arguments as
// function arguments can follow a primary_expression.  [This is a full
// LL(k) versus LALL(k) problem.  Enhancing context by duplication of
// some rules might handle this.]
///

new_expression
    :     
    (
          'new' ( (LPAREN expression_list RPAREN)  => expression_list RPAREN)?
          ( (LPAREN type_id RPAREN)  =>  LPAREN type_id RPAREN  |  new_type_id   )
          ((new_initializer)  =>  new_initializer)?
    )
    ;
    
new_initializer
    :     LPAREN (expression_list)? RPAREN
    ;
    
new_type_id
    :     declaration_specifiers (new_declarator)?
    ;
    
new_declarator
    :     ptr_operator (new_declarator)?
    |     direct_new_declarator
    ;
    
// The "[expression]" construct conflicts with the "new []" construct
// (and possibly others).  We used approximate lookahead for the "new []"
// construct so that it would not try to compute full LL(2) lookahead.
// Here, we use #pragma approx again because anytime we see a [ followed
// by token that can begin an expression, we always want to loop.
// Approximate lookahead handles this correctly.  In fact, approximate
// lookahead is the same as full lookahead when all but the last lookahead
// depth are singleton sets; e.g., {"["} followed by FIRST(expression).
///
direct_new_declarator
    :     (LSQUARE expression RSQUARE)+
    ;
    
ptr_operator
    :     AMPERSAND 
    |     ('_cdecl' | '__cdecl')
    |     ('_near' | '__near')
    |     ('_far' | '__far')
    |     '__interrupt'
    |     ('pascal' | '_pascal' | '__pascal')
    |     ('_stdcall' | '__stdcall')
    |     (scope_override STAR cv_qualifier_seq)  =>  scope_override STAR cv_qualifier_seq
    ;
    
ptr_to_member
    :     scope_override STAR cv_qualifier_seq
    ;
    
// JEL note:  does not use (const|volatile)* to avoid lookahead problems
cv_qualifier_seq
    :     (type_qualifier)*
    ;
    
scope_override
    :     (SCOPE    ->    SCOPETAG
           |        ->    NOSCOPETAG
          )
          (
                (ID LESSTHAN template_argument_list GREATERTHAN SCOPE)  =>  
                ID LESSTHAN template_argument_list GREATERTHAN SCOPE ('template')?
                ->    ^(SCOPETAG $scope_override ID template_argument_list)
          |
                (ID SCOPE)  =>  ID SCOPE  ('template')?
                ->    ^(SCOPETAG $scope_override ID)
          )*
    ;
    
delete_expression
    :     'delete' (LSQUARE RSQUARE)? cast_expression
    ;
    
expression_list
    :       assignment_expression  (COMMA assignment_expression)*   ->   ^(ELIST (assignment_expression)+)
    ;
    
optor
    :     // NOTE: you may need to add backtracking depending on the C++ standard specifications used...
          // but for now V3-Author has decided not to add that and go for the default alternative 1 selected
          // during antlr code generation...
          (
                'new' (LSQUARE RSQUARE )?
          |
                'delete' (LSQUARE RSQUARE)?
          |
                LPAREN RPAREN
          |
                LSQUARE RSQUARE
          |
                optor_simple_tokclass
          |
                type_specifier LPAREN RPAREN
          )
    ;
    
// optor_simple_tokclass
optor_simple_tokclass
  :
       PLUS|MINUS|STAR|DIVIDE|MOD|BITWISEXOR|AMPERSAND|BITWISEOR|TILDE|NOT|
   SHIFTLEFT|SHIFTRIGHT|
   ASSIGNEQUAL|TIMESEQUAL|DIVIDEEQUAL|MODEQUAL|PLUSEQUAL|MINUSEQUAL|
   SHIFTLEFTEQUAL|SHIFTRIGHTEQUAL|BITWISEANDEQUAL|BITWISEXOREQUAL|BITWISEOREQUAL|
   EQUAL|NOTEQUAL|LESSTHAN|GREATERTHAN|LESSTHANOREQUALTO|GREATERTHANOREQUALTO|OR|AND|
   PLUSPLUS|MINUSMINUS|COMMA|POINTERTO|POINTERTOMBR
  
  
  ;  
    
/*
*   Lexer
*/

// Operators:

// Operators:

ASSIGNEQUAL     : '=' ;
COLON           : ':' ;
COMMA           : ',' ;
QUESTIONMARK    : '?' ;
SEMICOLON       : ';' ;
POINTERTO       : '->' ;

// DOT & ELLIPSIS are commented out since they are generated as part of
// the Number rule below due to some bizarre lexical ambiguity shme.
 DOT  :       '.' ;
 ELLIPSIS      : '...' ;

LPAREN          : '(' ;
RPAREN          : ')' ;
LSQUARE         : '[' ;
RSQUARE         : ']' ;
LCURLY          : '{' ;
RCURLY          : '}' ;

EQUAL           : '==' ;
NOTEQUAL        : '!=' ;
LESSTHANOREQUALTO     : '<=' ;
LESSTHAN              : '<' ;
GREATERTHANOREQUALTO  : '>=' ;
GREATERTHAN           : '>' ;

DIVIDE          : '/' ;
DIVIDEEQUAL     : '/=' ;
PLUS            : '+' ;
PLUSEQUAL       : '+=' ;
PLUSPLUS        : '++' ;
MINUS           : '-' ;
MINUSEQUAL      : '-=' ;
MINUSMINUS      : '--' ;
STAR            : '*' ;
TIMESEQUAL      : '*=' ;
MOD             : '%' ;
MODEQUAL        : '%=' ;
SHIFTRIGHT      : '>>' ;
SHIFTRIGHTEQUAL : '>>=' ;
SHIFTLEFT       : '<<' ;
SHIFTLEFTEQUAL  : '<<=' ;

AND            : '&&' ;
NOT            : '!' ;
OR             : '||' ;

AMPERSAND       : '&' ;
BITWISEANDEQUAL : '&=' ;
TILDE           : '~' ;
BITWISEOR       : '|' ;
BITWISEOREQUAL  : '|=' ;
BITWISEXOR      : '^' ;
BITWISEXOREQUAL : '^=' ;

//Zuo: the following tokens are come from cplusplus.g

POINTERTOMBR    : '->*' ;
DOTMBR          : '.*'  ;

SCOPE           : '::'  ;

Whitespace  
  : 
    ( // Ignore space
      Space
    | // handle newlines
      ( '\r' '\n' // MS
      | '\r'    // Mac
      | '\n'    // Unix 
      ) 
    | // handle continuation lines
      ( '\\' '\r' '\n'  // MS
      | '\\' '\r'   // Mac
      | '\\' '\n'   // Unix 
      ) 
    ) 
    { $channel=HIDDEN;  }
  ;

MetaMathCommentTag
  : '//$'
  ;

MetaMathCommandTag
  : '@'
  ;
  


Comment  
  : 
    '/*'   
    ( /*{LA(2) != '/'}?=> */'*'
    | EndOfLine 
    | ~('*'| '\r' | '\n')
    )*
    '*/' { $channel=HIDDEN;  }
  ;

CPPComment
  : 
    '//' (~'$') (~('\n' | '\r'))* EndOfLine
    { $channel=HIDDEN;}                     
  ;

PREPROC_DIRECTIVE
  : 
    '#' LineDirective
    { $channel=HIDDEN; } 
  ;

fragment 
LineDirective
  :
    ('line')?  // this would be for if the directive started '#line'
    (Space)+
    n=Decimal
    (Space)+
    (sl=StringLiteral)
    ((Space)+ Decimal)* // To support cpp flags (GNU)
    
    EndOfLine?
  ;

fragment  
Space
  : 
      (' '|'\t' |'\f')
  ;

Pragma
  : 
    ('#' 'pragma' ( (EndOfContinuedLine)=>EndOfContinuedLine
            | ~('\r' | '\n')
            )*
    EndOfLine?
    )
    { $channel=HIDDEN; }
  ;

Error
  : 
    ('#' 'error'  ( (EndOfContinuedLine)=>EndOfContinuedLine
            | ~('\r' | '\n')
            )* EndOfLine
    EndOfLine?
    )
    { $channel=HIDDEN; }
  ;


// Added by V3-Author to eliminate the need for a preprocessor

PreProcDirective
    :
      ('#' 
        (Space)=> Space*
        (
          ('ifdef')=> 'ifdef'     
        | ('ifndef')=> 'ifndef'     
        | ('if')=> 'if'         
        | ('elif')=> 'elif'       
        | ('else')=> 'else'       
        | ('endif')=> 'endif'       
        | ('undef')=> 'undef'    
        | ('define')=> 'define' 
        | ('exec_macro_expression')=> 'exec_macro_expression' 
        | ('include')=> 'include'
        | ('include_next')=> 'include_next' 
        | ('warning')=> 'warning' 
        ) 
        
        ( (EndOfContinuedLine)=>EndOfContinuedLine
          | ~('\r' | '\n')
        )*
      EndOfLine?
      )
    ;


// Literals:

/*
 * Note that we do NOT handle tri-graphs nor multi-byte sequences.
 */

/*
 * Note that we can't have empty character constants (even though we
 * can have empty strings :-).
 */
CharLiteral
  : 
    '\'' ( Escape | UniversalCharacterName | ~('\''|'\\'|'\n'|'\r') ) '\''
  ;

WCharLiteral
  :
    'L' CharLiteral
  ;

/*
 * Can't have raw imbedded newlines in string constants.  Strict reading of
 * the standard gives odd dichotomy between newlines & carriage returns.
 * Go figure.
 */
StringLiteral
  : 
    '"'
    ( Escape
    | UniversalCharacterName
    | ~('"'|'\\'|'\n'|'\r')
    )*
    '"'
  ;

WStringLiteral
  :
    'L' StringLiteral
  ;

fragment
EndOfContinuedLine
  :
    ( '\\' (Space)* '\r' '\n' // MS
    | '\\' (Space)* '\r'    // Mac
    | '\\' (Space)* '\n'    // Unix 
    )
  ;

fragment
EndOfLine
  : 
    (//{dummyVar}?=> //added by V3-Author //options{generateAmbigWarnings = false;}:
      '\r\n'  // MS
    | '\r'    // Mac
    | '\n'    // Unix
    )
  ;

/*
 * Handle the various escape sequences.
 *
 * Note carefully that these numeric escape *sequences* are *not* of the
 * same form as the C language numeric *constants*.
 *
 * There is no such thing as a binary numeric escape sequence.
 *
 * Octal escape sequences are either 1, 2, or 3 octal digits exactly.
 *
 * There is no such thing as a decimal escape sequence.
 *
 * Hexadecimal escape sequences are begun with a leading \x and continue
 * until a non-hexadecimal character is found.
 *
 * No real handling of tri-graph sequences, yet.
 */

fragment
Escape  
  : 
    '\\'
    ( 'a'
    | 'b'
    | 'f'
    | 'n'
    | 'r'
    | 't'
    | 'v'
    | '"'
    | '\''
    | '\\'
    | '?'
    | ('0'..'3') (Digit (Digit)? )?
    | ('4'..'7') (Digit)?
    | 'x' (HexadecimalDigit)+)
  ;

// Numeric Constants: 


fragment
Digit
  : 
    '0'..'9'
  ;

fragment
Decimal
  : 
    ('0'..'9')+
  ;

fragment
LongSuffix
  : 'l'
  | 'L'
  ;

fragment
UnsignedSuffix
  : 'u'
  | 'U'
  ;

fragment
FloatSuffix
  : 'f'
  | 'F'
  ;

fragment
Exponent
  : 
    ('e'|'E') ('+'|'-')? (Digit)+
  ;

fragment
UniversalCharacterName
  :
    '\\u' HexQuad
  | '\\U' HexQuad HexQuad
  ;

fragment
HexQuad
  :
    HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit 
  ;

fragment
HexadecimalDigit
  :
    ('0'..'9'|'a'..'f'|'A'..'F')
  ;

fragment
Vocabulary
  : 
    '\u0003'..'\u0377'
  ;

Number
  : 
    ( (Digit)+ ('.' | 'e' | 'E') )=> 
    (Digit)+
    ( '.' (Digit)* (Exponent)? //{$type = FLOATONE;} //Zuo 3/12/01
    | Exponent                 //{$type = FLOATTWO;} //Zuo 3/12/01
    )                          
    (FloatSuffix               
    |LongSuffix                
    )?
  | 
    ('...')=> '...'            //{$type = ELLIPSIS;}
  | 
    '.'                        //{$type = DOT;}
    ( (Digit)+ (Exponent)?   //{$type = FLOATONE;} //Zuo 3/12/01
                                   
      (FloatSuffix           
      |LongSuffix            
      )?
    )?
  | 
    '0' ('0'..'7')*            
    (LongSuffix                
    |UnsignedSuffix            
    )*                         //{$type = OCTALINT;}
  | 
    '1'..'9' (Digit)*          
    (LongSuffix                
    |UnsignedSuffix            
    )*                         //{$type = DECIMALINT; }  
  | 
    '0' ('x' | 'X') (HexadecimalDigit)+ //('a'..'f' | 'A'..'F' | Digit)+                                
    (LongSuffix                
    |UnsignedSuffix            
    )*                         //{$type = HEXADECIMALINT;}   
  ;

ID
  :
    (('asm'|'_asm'|'__asm') Whitespace )=>

    ('asm'|'_asm'|'__asm') 
      (EndOfLine 
      |Space
      )+
    (
      LPAREN
        ( EndOfLine  
        | ~(')' | '\r' | '\n')
        )*  
      RPAREN { $channel=HIDDEN; }
    |
      LCURLY
        ( EndOfLine  
        | ~('}' | '\r' | '\n')
        )*  
      RCURLY { $channel=HIDDEN; }
    |
      // Single line asm statement
      (~('(' | ')' | '{' | '}' | '\n' | '\r' | ' ' | '\t' | '\f'))
      (~('(' | ')' | '{' | '}' | '\n' | '\r'))* (EndOfLine )?
      { $channel=HIDDEN; }
    )
  | 
    ('a'..'z'|'A'..'Z'|'_')
    ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
  ;

fragment
OCTALINT:;

fragment
DECIMALINT:;

fragment
HEXADECIMALINT:;

fragment
FLOATONE:;

fragment
FLOATTWO:;
