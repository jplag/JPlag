

header 
{
package jplag.csharp.grammar;

}

options 
{	
	language = "Java";
	//namespace = "DDW.CSharp";
}

class CSharpParser extends Parser;

options 
{
	k = 2;                           	// two token lookahead
	defaultErrorHandler = false;		// don't generate parser error handlers
	buildAST = false;   
 	exportVocab=CSharp;
    classHeaderSuffix = "jplag.csharp.CSharpTokenConstants";
}


tokens 
{
		
// ===== Tokens =====

	CompileUnit;
	UsingNode;

// ======== DECLARATIONS =========================

	TypeNode;				
	NamespaceNode;
	ClassNode;
	InterfaceNode;
	StructNode;
	EnumNode;
	DelegateNode;
	BaseTypes;
	
	BooleanLiteral;
	IntegerLiteral;
	RealLiteral;
	CharLiteral;
	StringLiteral;
	NullLiteral;

// ======== MEMBERS ==============================

	Types;
	Members;
	TypeMemberNode;
	MethodNode;
	FieldNode;
	PropertyNode;
	EventNode;
	ConstantNode;
	IndexerNode;
	OperatorNode;
	ConstructorNode;
	DestructorNode;
	AccessorNode;
																		  
// ======== REFERENCES ===========================						  

	Ident;
	QualIdent;
	TypeRef;
	Args;
	Arg;
	ArgDirection;
	Op;
																	
// ======== STATEMENTS ===========================					
																	
	Statements;
	Statement;
	ExprStmt;
	AssignStmt;
	Comment;
	TryCatchFinallyStmt;
	 TryStmt;
	 CatchClause;
	 FinallyStmt;
	ConditionStmt;
	 SwitchSection;
	 TrueSection;
	 FalseSection;
	IterationStmt;
	 InitStmt;
	 IncStmt;
	 TestExpr;
	 
	GotoStmt;
	ReturnStmt;
	BreakStmt;
	ContinueStmt;
	ThrowStmt;
	
	CheckedStmt;
	UncheckedStmt;
	LockStmt;
	UsingStmt;
	
	LabeledStmt;
	VariableDeclStmt;

// ======== EXPRESSIONS ==========================

	Expressions;
	Expression;
	SubExpression;
	
	TypeRefExpr;			
	PrimitiveExpr;
	CastExpr;
	ThisRefExpr;
	BaseRefExpr;
	MemberAccessExpr;
	UnaryExpr;
	
	ArrayCreateExpr;
	ObjectCreateExpr;
	TypeOfExpr;
	PostfixExpr;
	CheckedExpr;
	UncheckedExpr;
	
	InvokeExpr;
	IndexerExpr;
	ArrayRankExpr;
	ArrayInitExpr;

// ======== MISC =================================

	AttributeNode;
	GlobalAttributeNode;
	CustomAttributeNode;
	ModifierAttributes;
	LinePragma;
}
	// Emeric 22.01.05 feld jplag.java.Parser parser
	
	
// extended consume routine:
{
public jplag.csharp.Parser parser ;
    private Token lastConsumedToken = null;
    public void consume() {
        try {
            lastConsumedToken = LT(1);
        	super.consume();
        } catch(antlr.TokenStreamException e) {
            lastConsumedToken = null;
        }
    }

    public Token getLastConsumedToken() {
        return lastConsumedToken;
    }
}



identifier
	:	IDENTIFIER
	;

// ***** A.1.8 Literals *****

literal
	:	boolean_literal
	|	INTEGER_LITERAL
	|	HEXADECIMAL_INTEGER_LITERAL
	|	REAL_LITERAL				
	|	CHARACTER_LITERAL			
	|	STRING_LITERAL				
	|	null_literal				
	;
	
boolean_literal
	:	TRUE
	|	FALSE
	;
	
null_literal
	:	NULL
	;
	

// ***** A.2.1 Basic concepts *****
namespace_name
	:	namespace_or_type_name
	;
type_name
	:	namespace_or_type_name
	;
namespace_or_type_name
	:	simple_name (DOT simple_name)*
	;
	

// ***** A.2.2 Types *****

type
	:	{ LA(2)!=LBRACK }?	// guard against array	
		value_type
	|	{ LA(2)!=LBRACK }?	
		type_name			// may be struct, enum, class, interface, array, or delegate type
	|	{ LA(2)==STAR }? pointer_type
	|	reference_type
	;

value_type
	:	struct_type
//	|	enum_type	// enum can only be a type_name
	;

struct_type
	:	simple_type
	;

simple_type
	:	numeric_type
	|	BOOL
	;

numeric_type
	:	integral_type
	|	floating_point_type
	|	DECIMAL
	;

integral_type
	:	SBYTE 
	|	BYTE 
	|	SHORT 
	|	USHORT 
	|	INT 
	|	UINT 
	|	LONG 
	|	ULONG 
	|	CHAR
	;

floating_point_type
	:	FLOAT 
	|	DOUBLE
	;

	
reference_type
	:	(class_type)=>class_type
	|	(array_type)=>array_type
//	|	interface_type	// interface_type can only be a type_name
//	|	delegate_type	// delegate_type  can only be a type_name
	;

pointer_type
	:	(type_name | struct_type | VOID) (STAR)+
	;
	
unmanaged_type
	:	(type_name | struct_type | VOID) (STAR)*
	;

class_type
	:	/*{LA(2)==LTHAN}? type_name LTHAN class_type (COMMA class_type)* GTHAN
	|	*/type_name
	|	OBJECT
	|	STRING 
	;	
	
array_type
	:	non_array_type rank_specifiers
	;
	
interface_type
	:	type_name
	;
	
delegate_type
	:	type_name
	;
	
non_array_type
	:	class_type
	|	value_type
//	|	type_name
//	|	interface_type // determine interface and delegates later
//	|	delegate_type
	;
	
rank_specifiers
	:	(options{greedy=true;}:LBRACK (COMMA)* RBRACK)+
	;
// ***** A.2.3 Variables *****

variable_reference
	:	expression
	;
	
	
// ***** A.2.4 Expressions *****

argument_list
	:	argument (COMMA argument)*
	;
		
argument
	:	expression
	|	parameter_direction variable_reference
	;

simple_name
	:	identifier
	;
parenthesized_expression
	:	LPAREN expression RPAREN
	;
primary_start
	:	literal	
	|	simple_name	
	|	parenthesized_expression
	|	this_access	
	|	base_access	
	|	(array_creation_expression)=>array_creation_expression
	|	object_creation_expression
	|	typeof_expression
	|	sizeof_expression
	|	checked_expression
	|	unchecked_expression
	|	predefined_type_access
	;
primary_expression
	:   primary_start 
		(	options {greedy=true;}:	
			unchecked_expression
		|	checked_expression			
		|	sizeof_expression
		|	typeof_expression
		|	object_creation_expression	
		|	postfix_expression			
		|	base_access	
		|	this_access	
		|	element_access				
//		|	pointer_element_access			// same as element_access
		|	(invocation_expression)=>invocation_expression
		|	member_access 
		|	pointer_member_access
		|	parenthesized_expression
		|	simple_name			
		|	literal			
		)*
	;		

member_access
	:	DOT identifier 
	;
predefined_type_access
	:(	BOOL	|	BYTE	|	CHAR	|	DECIMAL	|	DOUBLE	
	|	FLOAT	|	INT		|	LONG	|	OBJECT	|	SBYTE	
	|	SHORT	|	STRING	|	UINT	|	ULONG	|	USHORT)
	DOT  identifier
	;

invocation_expression
	:	LPAREN { parser.add(_INVOCATION, this); }
        (argument_list)? RPAREN
	;	
element_access
	:	LBRACK expression_list RBRACK
	;
expression_list
	:	expression (COMMA expression)*
	;
this_access
	:	THIS
	;
base_access
	:	BASE DOT identifier
	|	BASE element_access										
	;																		
postfix_expression															
	:	postfix_op													
	;	
postfix_op
	:	INC { parser.add(_ASSIGNMENT, this); }
	|	DEC { parser.add(_ASSIGNMENT, this); }
	;
object_creation_expression
	:	NEW { parser.add(_OBJECT_CREATION, this); }
        type LPAREN (argument_list)? RPAREN
	;
typeof_expression
	:	TYPEOF LPAREN
		typeof_types
		RPAREN
	;	
typeof_types
	:	({LA(2)!=STAR}? VOID | type)
	;
checked_expression
	:	CHECKED LPAREN expression RPAREN
	;

unchecked_expression
	:	UNCHECKED LPAREN expression RPAREN
	;
array_creation_expression
	:	(NEW array_type array_initializer)=>
		 NEW  { parser.add(_ARRAY_CREATION, this); } 
		 array_type 
		 array_initializer
		 
	|	NEW  { parser.add(_ARRAY_CREATION, this); }
		non_array_type 
		LBRACK 
		expression_list 
		RBRACK 
		(options{greedy=true;}:rank_specifiers )? 
		(array_initializer)?
		
	;
// until types are discovered, delegate and object creation are the same
//delegate_creation_expression
//	:	NEW delegate_type LPAREN expression RPAREN 
//	;


pointer_member_access
	:	ARROW identifier
	;

/* same as element_access
pointer_element_access
	:	LBRACK expression RBRACK
	;
*/

addressof_expression
	:	BAND unary_expression
	;

sizeof_expression
	:	SIZEOF LPAREN unmanaged_type RPAREN
	;

// ============================

unary_expression
	:	(cast_expression unary_expression) => cast_expression unary_expression
	|	primary_expression 
	|	unary_op unary_expression
//	|	pointer_indirection_expression
//	|	addressof_expression
	;
unary_op	// includes operators for unsafe (* and &)
	: PLUS | MINUS | LNOT | BNOT | STAR | BAND
	| INC { parser.add(_ASSIGNMENT, this); }
	| DEC { parser.add(_ASSIGNMENT, this); }
	;
	
pre_increment_expression
	:	INC  { parser.add(_ASSIGNMENT, this); }  unary_expression
	;

pre_decrement_expression
	:	DEC  { parser.add(_ASSIGNMENT, this); }  unary_expression
	;

cast_expression
	:	LPAREN type RPAREN 
	;

// ============================

sub_expression
	:	unary_expression
	;

multiplicative_expression
	:	sub_expression
	(	STAR^ sub_expression
	|	DIV^ sub_expression
	|	MOD^ sub_expression
	)*
	;

additive_expression
	:	multiplicative_expression
	(	PLUS^ multiplicative_expression
	|	MINUS^ multiplicative_expression
	)*
	;

shift_expression
	:	additive_expression
	(	SL^ additive_expression
	|	SR^ additive_expression
	)*
	;

relational_expression
	:	shift_expression
	(	LTHAN^ shift_expression
	|	GTHAN^ shift_expression
	|	LE^ shift_expression
	|	GE^ shift_expression
	|	IS^ type
	|	AS^ type
	)*
	;

equality_expression
	:	relational_expression
	(	EQUAL^ relational_expression
	|	NOT_EQUAL^ relational_expression
	)*
	;

and_expression
	:	equality_expression
		(BAND^ equality_expression)*
	;

exclusive_or_expression
	:	and_expression
		(BXOR^ and_expression)*
	;

inclusive_or_expression
	:	exclusive_or_expression
		(BOR^ exclusive_or_expression)*
	;
	
conditional_and_expression
	:	inclusive_or_expression
		(LAND^ inclusive_or_expression)*
	;

conditional_or_expression
	:	conditional_and_expression
		(LOR^ conditional_and_expression)*
	;

conditional_expression
	:	conditional_or_expression
		(QUESTION^ expression COLON^ expression)?
	;
// ============================

assignment
	:	unary_expression
        assignment_operator  { parser.add(_ASSIGNMENT, this); }
        expression
	;

assignment_operator
	:	ASSIGN | PLUS_ASN | MINUS_ASN | STAR_ASN | DIV_ASN
    |	MOD_ASN | BAND_ASN | BOR_ASN | BXOR_ASN | SL_ASN
    |	SR_ASN
        
	;

expression
	:	(conditional_expression) => conditional_expression
	|	assignment
	;

constant_expression
	:	expression
	;

boolean_expression
	:	expression
	;



// ***** A.2.5 Statements *****

statement
	:	labeled_statement
	|	(declaration_statement) => declaration_statement
	|	embedded_statement
	;

embedded_statement
	:	block
	|	empty_statement
	|	expression_statement
	|	selection_statement
	|	iteration_statement
	|	jump_statement
	|	try_statement
	|	checked_statement
	|	unchecked_statement
	|	lock_statement
	|	using_statement
	|	unsafe_statement
	|	fixed_statement
	;

block
	:	LBRACE  { parser.add(_L_BRACE, this); }
        statement_list RBRACE { parser.add(_R_BRACE, this); }
	;
	
statement_list
	:	(statement)*
	;

empty_statement
	:	SEMI
	; 

labeled_statement
	:	identifier COLON statement
	;
	
declaration_statement
	:	local_variable_declaration SEMI
	|	local_constant_declaration SEMI 
	;

local_variable_declaration
	:	type local_variable_declarators
	;

local_variable_declarators
	:	local_variable_declarator (COMMA local_variable_declarator)*
	;

local_variable_declarator
	:	identifier  { parser.add(_DECLARE_VAR, this); }
        (ASSIGN local_variable_initializer)?
	;

local_variable_initializer
	:	expression
	|	array_initializer
	|	stackalloc_initializer
	;

stackalloc_initializer
	:	STACKALLOC unmanaged_type LBRACK expression RBRACK
	;

local_constant_declaration
	:	CONST type constant_declarators
	;
	
constant_declarators
	:	constant_declarator (COMMA constant_declarator)*
	;

constant_declarator
	:	identifier  { parser.add(_DECLARE_CONST, this); }
        ASSIGN constant_expression
	;
	
expression_statement
	:	statement_expression SEMI
	;
	
statement_expression
	:	(assignment)=> assignment	// TODO: this pe can only be	
	|	primary_expression			// a) invocation_expression 
	|	pre_increment_expression	// b) object_creation_expression
	|	pre_decrement_expression	// c) postfix_expression
	;																
	
selection_statement
	:	if_statement
	|	switch_statement
	;
	
// 'if' is just a subset of switch...
if_statement
	:	IF { parser.add(_IF, this); }
        LPAREN boolean_expression RPAREN embedded_statement
		(	options {warnWhenFollowAmbig = false;} // dangling else
		:	ELSE  { parser.add(_ELSE, this); }
            embedded_statement
		)?        { parser.add(_END_IF, this); }
	;

switch_statement
	:	SWITCH  { parser.add(_SWITCH_BEGIN, this); }
        LPAREN expression RPAREN switch_block
	;
	
switch_block
	:	LBRACE ( (switch_section)+ )?
        RBRACE  { parser.add(_SWITCH_END, this); }
	;
	
switch_section
	:	(	options {warnWhenFollowAmbig=false;}: switch_label COLON )+ 
		statement_list
	;	
switch_label
	:	CASE  { parser.add(_CASE, this); }
        constant_expression
	|	DEFAULT	 { parser.add(_CASE, this); }
	;
	
iteration_statement
	:	while_statement
	|	do_statement
	|	for_statement
	|	foreach_statement
	;

while_statement
	:	WHILE  { parser.add(_WHILE, this); }
        LPAREN boolean_expression RPAREN embedded_statement
	;

do_statement
	:	DO  { parser.add(_DO, this); }
        embedded_statement WHILE LPAREN boolean_expression RPAREN SEMI 
	;
for_statement
	:	FOR  { parser.add(_FOR, this); }
        LPAREN 
		(for_initializer)? SEMI 
		(for_condition)? 	SEMI 
		(for_iterator)? 	RPAREN 
        embedded_statement
    ;

for_initializer
	:	(local_variable_declaration) => local_variable_declaration
	|	statement_expression_list
	;

for_condition
	:	boolean_expression
	;

for_iterator
	:	statement_expression_list
	;

statement_expression_list
	:	statement_expression (COMMA statement_expression)*
	;

foreach_statement
	:	FOREACH  { parser.add(_FOREACH, this); }
        LPAREN 
        type 
        identifier 	IN 
        expression 	RPAREN 
        embedded_statement
	;
jump_statement
	:	break_statement
	|	continue_statement
	|	goto_statement
	|	return_statement
	|	throw_statement
	;

break_statement	
	:	BREAK { parser.add(_BREAK, this); } SEMI		
	;
continue_statement
	:	CONTINUE { parser.add(_CONTINUE, this); } SEMI
	;
goto_statement
	:	GOTO { parser.add(_GOTO, this); } identifier SEMI
	|	GOTO { parser.add(_GOTO, this); } CASE constant_expression SEMI
	|	GOTO { parser.add(_GOTO, this); } DEFAULT SEMI
	;
return_statement
	:	RETURN { parser.add(_RETURN, this); } (expression)? SEMI 
	; 	
throw_statement
	:	THROW { parser.add(_THROW, this); } (expression)? SEMI 
	;	
	
checked_statement
	:	CHECKED { parser.add(_CHECKED, this); } block
	;
unchecked_statement
	:	UNCHECKED { parser.add(_UNCHECKED, this); } block
	;
lock_statement
	:	LOCK { parser.add(_LOCK, this); }
        LPAREN expression RPAREN embedded_statement
	;
using_statement
	:	USING { parser.add(_USING, this); }
        LPAREN resource_acquisition RPAREN embedded_statement
	;
resource_acquisition
	:	(local_variable_declaration) => local_variable_declaration
	|	expression
	;
try_statement
	:	TRY { parser.add(_TRY, this); }
        block catch_clauses (finally_clause)?
	;
catch_clauses
	:	(catch_clause)*
	;
catch_clause
	:	CATCH { parser.add(_CATCH, this); }
        (LPAREN class_type (identifier)? RPAREN)? block
	;
finally_clause
	:	FINALLY { parser.add(_FINALLY, this); } block
	;

unsafe_statement
	:	UNSAFE { parser.add(_UNSAFE, this); } block
	;

fixed_statement
	:	FIXED { parser.add(_FIXED, this); }
		LPAREN pointer_type fixed_pointer_declarators RPAREN embedded_statement
	;

fixed_pointer_declarators
	:	fixed_pointer_declarator (COMMA fixed_pointer_declarator)*
	;

fixed_pointer_declarator
	:	identifier { parser.add(_DECLARE_VAR, this); }
		ASSIGN fixed_pointer_initializer
	;

fixed_pointer_initializer
	:	{LA(1)==BAND}? BAND variable_reference
	|	expression
	;

// ***** A.2.5b Namespaces *****	

compilation_unit
	:	(using_directive)*  (global_attributes)*  (namespace_member_declaration)* 
	;

namespace_declaration
	:	NAMESPACE   { parser.add(_NAMESPACE_BEGIN, this); }
		qualified_identifier  
		namespace_body   
		(SEMI)?
	;
	
qualified_identifier
	:	identifier (DOT identifier)*
	;
namespace_body
	:	LBRACE   (using_directive)*  (namespace_member_declaration)* r:RBRACE
        { parser.add(_NAMESPACE_END, this); }
	;
using_directive
	:	USING   { parser.add(_USING_DIRECTIVE, this); }
		(	identifier   ASSIGN   namespace_or_type_name   SEMI
		|	namespace_name	SEMI
		)
		
	;
namespace_member_declaration
	:	namespace_declaration | type_declaration
	;

type_declaration
	:	(attributes)?
		(	(class_declaration) => class_declaration
		|	(struct_declaration) => struct_declaration 
		|	(interface_declaration) => interface_declaration
		|	(enum_declaration) => enum_declaration
		|	(delegate_declaration SEMI) => delegate_declaration SEMI
		) 
		(SEMI)?
	;
	
// ***** Generics stuff *****

type_parameter_list
	:	LTHAN type_parameters GTHAN
	;
	
type_parameters
	:	type_parameter (COMMA type_parameter)*
	;
	
type_parameter
	:	identifier
	;

type_parameter_constraints_clauses
	:	(type_parameter_constraints_clause)+
	;

type_parameter_constraints_clause
	:	{ LT(1).getText().equals("where") }? identifier type_parameter COLON type_parameter_constraints
	;

type_parameter_constraints
	:	primary_constraint (COMMA
		(
			secondary_constraints (COMMA constructor_constraint)?
		|
			constructor_constraint
		))?
//	|	secondary_constraints (COMMA constructor_constraint)?
	|	constructor_constraint
	;

primary_constraint
	:	class_type
	|	CLASS
	|	STRUCT
	;
	
secondary_constraints
	:	interface_type ({LA(1) == COMMA}? COMMA secondary_constraints)?
//	|	type_parameter (COMMA secondary_constraints)?
	;

constructor_constraint
	:	NEW LPAREN RPAREN
	;
	
// ***** A.2.6 Classes *****

class_declaration
	:	(class_modifiers)*
		(partial_modifier)?
		CLASS
        identifier  { parser.add(_CLASS_BEGIN, this); }
        (type_parameter_list)?
		(class_base)? 
		(type_parameter_constraints_clauses)?
        class_body
                        { parser.add(_CLASS_END, this); }
	;
	
	
class_modifiers
	:	NEW 	|	PUBLIC	 |	PROTECTED  |	INTERNAL 
	|	PRIVATE |	ABSTRACT |	SEALED     |    UNSAFE
	;

partial_modifier
	:	{ LT(1).getText().equals("partial")}? identifier		
	;

class_base
	:	COLON 
		(	class_type (COMMA class_type)*
//		|	interface_type_list
//		|	class_type COMMA interface_type_list
		)
	;
//interface_type_list
//	:	interface_type (COMMA interface_type)*
//	;
class_body
	:	LBRACE (class_member_declaration)* RBRACE
	;

class_member_declaration
	:	(constant_declaration) 		=> constant_declaration
	|	(field_declaration) 		=> field_declaration 
	|	(method_declaration) 		=> method_declaration 
	|	(property_declaration) 		=> property_declaration
	|	(event_declaration) 		=> event_declaration
	|	(indexer_declaration) 		=> indexer_declaration
	|	(operator_declaration) 		=> operator_declaration
	|	(constructor_declaration) 	=> constructor_declaration 
	|	(destructor_declaration) 	=> destructor_declaration
	|	(static_constructor_declaration) => static_constructor_declaration
	|	type_declaration
	;
	
	
constant_declaration
	:	(attributes)? 
		constant_modifiers 
		CONST  { parser.add(_DECLARE_CONST, this); }
		type 
		constant_declarators // consts can declare more than one field
		SEMI
	;
constant_modifiers
	:	(NEW
	|	PUBLIC
	|	PROTECTED 
	|	INTERNAL
	|	PRIVATE)*
	;
field_declaration
	:	(attributes)? 
		field_modifiers 
		type 
		variable_declarators
		SEMI
	;
field_modifiers
	 : (NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE | STATIC | READONLY | VOLATILE | UNSAFE)*
	;
variable_declarators
	:	variable_declarator (COMMA variable_declarator)* 
	;
variable_declarator
	:	identifier { parser.add(_DECLARE_VAR, this); }
	|	identifier { parser.add(_DECLARE_VAR, this); }
        ASSIGN variable_initializer
	;
variable_initializer
	:	expression 	
	|	array_initializer
	;
	
method_declaration
	:	method_header 
		method_body
	;
method_header
	:	(attributes)? 
		method_modifiers 
		return_type 
		member_name  { parser.add(_METHOD, this); }
		LPAREN (formal_parameter_list)? RPAREN
	;
method_modifiers
	:	(NEW	|	PUBLIC	|	PROTECTED	|	INTERNAL
	|	PRIVATE |	STATIC	|	VIRTUAL		|	SEALED	
	|	OVERRIDE|	ABSTRACT|	EXTERN | UNSAFE)*
	;
return_type
	:	{LA(2)!=STAR}? VOID
	|	type
	;
member_name
	:	type_name	// identifier
					// interface_type DOT identifier 
	;
method_body
	:	block
	|	SEMI
	;
	
// TODO: insure later that PARAMS is last one and single dimension
formal_parameter_list
	:	method_parameter
		(COMMA method_parameter)*
	;
method_parameter
		// fixed_parameter
	:	((attributes)? (parameter_direction)? type identifier) =>
		 (attributes)? 
		 (parameter_direction)? 
		  type 
		  identifier
		//  parameter_array
	|	((attributes)? PARAMS array_type identifier) =>
		 (attributes)? 
		  PARAMS 
		  array_type 
		  identifier
	;
parameter_direction
	:	REF
	| 	OUT
	;
	
property_declaration
	:	(attributes)? 
        property_modifiers 
        type 
        type_name    { parser.add(_PROPERTY, this); }
		LBRACE
        accessor_declarations 
		RBRACE
	;
property_modifiers
 : 	(	NEW		|	PUBLIC	|	PROTECTED	|	INTERNAL	|	PRIVATE	|	STATIC	
	|	VIRTUAL	|	SEALED	|	OVERRIDE	|	ABSTRACT	|	EXTERN | UNSAFE
 	)*
	;
// TODO: can't be two gets etc.
accessor_declarations
	:	 accessor_declaration (accessor_declaration)?
	;
accessor_declaration
	:	(attributes)? accessor_modifier (block | SEMI)
	;
accessor_modifier
	:	{ LT(1).getText().equals("get") || LT(1).getText().equals("set")}? 
		identifier		
	;
event_declaration
	:	(attributes)? 
        event_modifiers 
		EVENT  { parser.add(_EVENT, this); }
        type 
		(	(variable_declarator)+ SEMI
		|	member_name LBRACE event_accessor_declarations RBRACE
		)
	;
event_modifiers
	:	(NEW 	 |	PUBLIC 	 |	PROTECTED |	INTERNAL 
	|	PRIVATE	 |	STATIC 	 |	VIRTUAL   |	SEALED 
	|	OVERRIDE |	ABSTRACT |	EXTERN | UNSAFE)*
	;
// TODO: insure not two PLUS_ASN's etc
event_accessor_declarations
	:	 event_accessor_declaration	(event_accessor_declaration)?
	;
event_accessor_declaration
	:	(attributes)? event_accessor_modifiers block
	;
event_accessor_modifiers
	:	{ LT(1).getText().equals("add") || LT(1).getText().equals("remove")}? 
		identifier		
	;
	
indexer_declaration
	:	(attributes)? 
		 indexer_modifiers 
		 indexer_declarator 
		LBRACE accessor_declarations RBRACE 
	;
indexer_modifiers
	:	(NEW 	 |	PUBLIC  |	PROTECTED |	INTERNAL  
	|	PRIVATE  |	VIRTUAL |	SEALED    |	OVERRIDE  
	|	ABSTRACT |	EXTERN | UNSAFE)*
	;
indexer_declarator
	:	type 
		(simple_name DOT)?
		THIS 	{ parser.add(_INDEXER, this); }
		LBRACK (formal_parameter_list)? RBRACK
	;
operator_declaration
	:	(attributes)? operator_modifiers operator_declarator operator_body 
	;
operator_modifiers
	:	(PUBLIC 	|	STATIC 	|	EXTERN  | UNSAFE)*
	;
operator_declarator
	:	(unary_operator_declarator) 	 => unary_operator_declarator
	|	(binary_operator_declarator) 	 => binary_operator_declarator
	|	(conversion_operator_declarator) => conversion_operator_declarator
	;
unary_operator_declarator
	:	type 
		OPERATOR  { parser.add(_OPERATOR, this); }
		overloadable_unary_operator 
		LPAREN 
		type 
		identifier 
		RPAREN 
	;	
overloadable_unary_operator
	:	PLUS	|	MINUS	|	LNOT	|	BNOT	|	STAR 
	|	INC		|	DEC		|	TRUE	|	FALSE
	;	
binary_operator_declarator
	:	type 
		OPERATOR  { parser.add(_OPERATOR, this); }
		overloadable_binary_operator 
		LPAREN 
		type 
		identifier 
		COMMA 
		type 
		identifier 
		RPAREN
	;
	
overloadable_binary_operator
	:	PLUS	|	MINUS	|	STAR	|	DIV	|	MOD 
	|	BAND	|	BOR		|	BXOR	|	SL	|	SR
	|	EQUAL	|	NOT_EQUAL			
	|	GTHAN	|	LTHAN
	|	GE		|	LE
	;
conversion_operator_declarator
	:	conversion_classification 
		OPERATOR   { parser.add(_OPERATOR, this); }
		type 
		LPAREN 
		type 		
		identifier 
		RPAREN
	;
conversion_classification
	:	IMPLICIT | EXPLICIT
	;
operator_body
	:	block
	|	SEMI
	; 
	
constructor_declaration
	:	(attributes)? 
		 constructor_modifiers 
		 constructor_declarator 
		 constructor_body
	;
		
constructor_modifiers
	:	(PUBLIC  |	PROTECTED |	INTERNAL |	PRIVATE  |	EXTERN | UNSAFE)*
	;
constructor_declarator
	:	identifier  { parser.add(_CONSTRUCTOR, this); }
		LPAREN 
		(formal_parameter_list)? 
		RPAREN 
		(constructor_initializer)?
	;
constructor_initializer
	:	COLON (BASE^ | THIS^ ) LPAREN (argument_list)? RPAREN 
	;
constructor_body
	:	block
	|	SEMI
	; 
static_constructor_declaration
	:	(attributes)? 
		 static_constructor_modifiers 
		 identifier { parser.add(_STATIC_CONSTR, this); } LPAREN RPAREN 
		 static_constructor_body
	;
static_constructor_modifiers
	:	(EXTERN | STATIC | UNSAFE)*
	;

static_constructor_body
	:	block
	|	SEMI
	;

destructor_declaration
	:	(attributes)? 
		(EXTERN UNSAFE | UNSAFE EXTERN)? 
		BNOT
        identifier { parser.add(_DESTRUCTOR, this); } LPAREN RPAREN  
        destructor_body
	;
destructor_body
	:	block
	|	SEMI
	;


// ***** A.2.7 Structs *****

struct_declaration
	:	struct_modifiers
		(partial_modifier)?
		STRUCT   { parser.add(_STRUCT_BEGIN, this); }
		identifier 
		(struct_interfaces)? 
        struct_body		
	;
struct_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE | UNSAFE)*
	;

struct_interfaces
	:	COLON class_type (COMMA class_type)*
	;

struct_body
	:	LBRACE (struct_member_declaration)* RBRACE
        { parser.add(_STRUCT_END, this); }
	;
	
struct_member_declaration	
	:	(constant_declaration) 		=> constant_declaration
	|	(field_declaration) 		=> field_declaration
	|	(method_declaration) 		=> method_declaration 
	|	(property_declaration) 		=> property_declaration
	|	(event_declaration) 		=> event_declaration
	|	(indexer_declaration) 		=> indexer_declaration
	|	(operator_declaration) 		=> operator_declaration
	|	(constructor_declaration) 	=> constructor_declaration 
	|	(static_constructor_declaration) => static_constructor_declaration
	|	 type_declaration
	;


// ***** A.2.8 Arrays *****

// array_type
// non_array_type
// rank_specifiers

array_initializer
	:	LBRACE 
		(
			variable_initializer
			(
				options {greedy=true;}: {LA(1)==COMMA && LA(2)!=RBRACE}?
			 	COMMA variable_initializer
			)*
			({LA(1)==COMMA && LA(2)==RBRACE}? COMMA)?
		)?
		RBRACE
	;


// ***** A.2.9 Interfaces *****

interface_declaration
	:	interface_modifiers 
		(partial_modifier)?
		INTERFACE   { parser.add(_INTERFACE_BEGIN, this); }
		identifier 
		(interface_base)? 
        interface_body
	;
	
interface_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE | UNSAFE)*
	;
interface_base
	:	COLON class_type (COMMA class_type)* //interface_type_list
	;
interface_body
	:	LBRACE (interface_member_declaration)* RBRACE
        { parser.add(_INTERFACE_END, this); }
	;
interface_member_declaration
	:	(interface_method_declaration)	=> interface_method_declaration
	|	(interface_property_declaration)=> interface_property_declaration
	|	(interface_event_declaration)	=> interface_event_declaration
	|	(interface_indexer_declaration)	=> interface_indexer_declaration
	;

interface_method_declaration
	:	(attributes)? 
		(NEW)? 
		return_type 
		identifier  { parser.add(_METHOD, this); }
		LPAREN (formal_parameter_list)? RPAREN SEMI
	;
interface_property_declaration
	:	(attributes)? 
		(NEW)? 
		type 
		identifier  { parser.add(_PROPERTY, this); }
		LBRACE interface_accessors RBRACE
	;
interface_accessors
	:	 (attributes)? accessor_modifier SEMI
		((attributes)? accessor_modifier SEMI)?
	;
interface_event_declaration
	:	(attributes)? (NEW)? EVENT { parser.add(_EVENT, this); }
        type identifier SEMI 
	;
interface_indexer_declaration
	:	(attributes)? (NEW)? type THIS { parser.add(_INDEXER, this); }
		LBRACK (formal_parameter_list)? RBRACK 
		LBRACE interface_accessors RBRACE 
	;

	
// ***** A.2.10 Enums *****

enum_declaration
	:	 enum_modifiers 
		ENUM    { parser.add(_ENUM, this); }
		 identifier 
		(enum_base)? 
		 enum_body	
	;
	
enum_base
	:	COLON integral_type
	;

enum_body
	:	(LBRACE (enum_member_declaration  (COMMA enum_member_declaration)* )? COMMA RBRACE) =>
		 LBRACE (enum_member_declaration (COMMA enum_member_declaration)* )? COMMA RBRACE
	|	 LBRACE (enum_member_declaration (COMMA enum_member_declaration)* )? RBRACE
	;
	
enum_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE)*
	;

enum_member_declaration
	:	(attributes)? identifier (ASSIGN constant_expression)?  
	;


// ***** A.2.11 Delegates *****

delegate_declaration
	:	delegate_modifiers
		DELEGATE    { parser.add(_DELEGATE, this); }
		return_type  // note: this must include VOID (vs C# spec, 'type')
		identifier 
		LPAREN (formal_parameter_list)?	RPAREN
	;
		 
delegate_modifiers
	:	(NEW | PUBLIC | PROTECTED | INTERNAL | PRIVATE | UNSAFE)*
	;



// ***** A.2.12 Attributes *****

global_attributes
	:	LBRACK "assembly" COLON attribute_list RBRACK
	;

attributes
	:	(attribute_section)+
		//{ #attributes = #( [ATTRIBUTE_DEF], as); }
	;
	
attribute_section
	:	LBRACK (attribute_target COLON)? attribute_list RBRACK  
	;

attribute_target
	:	FIELD
	|	EVENT
	|	METHOD
	|	MODULE
	|	PARAM
	|	PROPERTY
	|	RETURN 
	|	TYPE 
	;

attribute_list
	:	(attribute (COMMA attribute)* COMMA) => 
		 attribute (COMMA attribute)* COMMA
	|	 attribute (COMMA attribute)*
	;

attribute
	:	type_name { parser.add(_ATTRIBUTE, this); }
        (attribute_arguments)?
	;

// args must be positional, then named - not mixed
// (x=5) can be positional, but not x=5 
// TODO: must enforce (2,4,x=5,y=6) order 
attribute_arguments
	:	LPAREN (attribute_argument_list)? RPAREN
	;
	
 // an expression can be an expression_list
attribute_argument_list
	:	expression (COMMA expression)* 
	;
	
/* // unused for now
positional_argument_list
	:	expression (COMMA expression)* 
		{#positional_argument_list = #( #[Args], #positional_argument_list);}
	;

// unused for now
named_argument_list
	:	identifier ASSIGN expression (COMMA identifier ASSIGN expression)*
	;
*/	


// ***** A.3 Grammar extensions for unsafe code *****
// added where needed
	
	
// =======================================================
// == LEXER ==============================================
// =======================================================
	
class CSharpLexer extends Lexer;

options 
{
	k=4;                       // four characters of lookahead
	// Allow any char but \uFFFF (16 bit -1 == CharParser.EOF .....)
	charVocabulary='\u0003'..'\uFFFE';
	exportVocab=CSharp;
	testLiterals=false;
}

	
// ***** A.1.7 Keywords *****
tokens
{
	ABSTRACT	=	"abstract";			LONG		=	"long";
	AS			=	"as";				NAMESPACE	=	"namespace";
	BASE		=	"base";				NEW			=	"new";
	BOOL		=	"bool";				NULL		=	"null";
	BREAK		=	"break";			OBJECT		=	"object";
	BYTE		=	"byte";				OPERATOR	=	"operator";
	CASE		=	"case";				OUT			=	"out";
	CATCH		=	"catch";			OVERRIDE	=	"override";
	CHAR		=	"char";				PARAMS		=	"params";
	CHECKED		=	"checked";			PRIVATE		=	"private";
	CLASS		=	"class";			PROTECTED	=	"protected";
	CONST		=	"const";			PUBLIC		=	"public";
	CONTINUE	=	"continue";			READONLY	=	"readonly";
	DECIMAL		=	"decimal";			REF			=	"ref";
	DEFAULT		=	"default";			RETURN		=	"return";
	DELEGATE	=	"delegate";			SBYTE		=	"sbyte";
	DO			=	"do";				SEALED		=	"sealed";
	DOUBLE		=	"double";			SHORT		=	"short";
	ELSE		=	"else";				SIZEOF		=	"sizeof";
	ENUM		=	"enum";				STACKALLOC	=	"stackalloc";
	EVENT		=	"event";			STATIC		=	"static";
	EXPLICIT	=	"explicit";			STRING		=	"string";
	EXTERN		=	"extern";			STRUCT		=	"struct";
	FALSE		=	"false";			SWITCH		=	"switch";
	FINALLY		=	"finally";			THIS		=	"this";
	FIXED		=	"fixed";			THROW		=	"throw";
	FLOAT		=	"float";			TRUE		=	"true";
	FOR			=	"for";				TRY			=	"try";
	FOREACH		=	"foreach";			TYPEOF		=	"typeof";
	GOTO		=	"goto";				UINT		=	"uint";
	IF			=	"if";				ULONG		=	"ulong";
	IMPLICIT	=	"implicit";			UNCHECKED	=	"unchecked";
	IN			=	"in";				UNSAFE		=	"unsafe";
	INT			=	"int";				USHORT		=	"ushort";
	INTERFACE	=	"interface";		USING		=	"using";
	INTERNAL	=	"internal";			VIRTUAL		=	"virtual";
	IS			=	"is";				VOID		=	"void";
	LOCK		=	"lock";				WHILE		=	"while";
//	GET			=	"get";				SET			=	"set";
//	ADD			=	"add";				REMOVE		=	"remove";
}


// ***** Lexical Grammar *****
/*
Input
	:	(Input_section)*
	;
Input_section
	:	(Input_element)*   New_line
	;
Input_element
	:	Whitespace
	|	Comment
	|	Token
	;
*/
// ***** A.1.1 LINE TERMINATORS *****
protected
NEW_LINE
	:	(	// carriage return character followed by possible line feed character	
			{ LA(2)=='\n' }? '\r' '\n'			
		|	'\r'			// line feed character							
		|	'\n'			// line feed character							
		|	'\u2028'			// line separator character
		|	'\u2029'			// paragraph separator character 
		)
		{newline();}
	;
	
protected
NEW_LINE_CHARACTER
	:	('\r' | '\n' | '\u2028' | '\u2029')
	;
	
protected
NOT_NEW_LINE
	:	~( '\r' | '\n' | '\u2028' | '\u2029')
	;
	
	
// ***** A.1.2 WHITESPACE *****
WHITESPACE
	:	(	' '
		|	'\u0009' // horizontal tab character
		|	'\u000B' // vertical tab character
		|	'\u000C' // form feed character 
		|	NEW_LINE 
		)+
		{ _ttype = Token.SKIP; }
	;	
	
	
// ***** A.1.3 COMMENTS *****
SINGLE_LINE_COMMENT
	:	"//" 
		(NOT_NEW_LINE)* 
		(NEW_LINE)?
		{_ttype = Token.SKIP;}
	;
	
DELIMITED_COMMENT
	:	"/*"  
		(	{ LA(2)!='/' }? '*'
		|	NEW_LINE		
		|	~('*'|'\r'|'\n'|'\u2028'|'\u2029')
		)*
		"*/"
		{ _ttype = Token.SKIP; }
	;	

/*	
// ***** A.1.4 TOKENS *****
TOKEN
	:	identifier
	|	KEYWORD
	|	INTEGER_LITERAL
	|	REAL_LITERAL
	|	CHARACTER_LITERAL
	|	STRING_LITERAL
	|	OPERATOR_OR_PUNCTUATOR
	;
*/	
	
// ***** A.1.5 UNICODE character escape sequences *****
UNICODE_ESCAPE_SEQUENCE
	:	('\\' 'u'   HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT)
	|	('\\' 'U'   HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT  
					HEX_DIGIT   HEX_DIGIT   HEX_DIGIT  HEX_DIGIT)
	;

// ***** A.1.6 IDENTIFIERS *****

IDENTIFIER
options { testLiterals=true; }
	:	IDENTIFIER_START_CHARACTER (IDENTIFIER_PART_CHARACTER)*
//	|	'@' (IDENTIFIER_PART_CHARACTER)*
	;
	
protected
IDENTIFIER_START_CHARACTER
	:	('a'..'z'|'A'..'Z'|'_'|'$') //|'@') //TODO: identifier literals can have starting @
	;
	
protected
IDENTIFIER_PART_CHARACTER
	:	('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$') 
	;
	
// ***** A.1.8 LITERALS *****

/* // move to parser - TODO: look into this...

BOOLEAN_LITERAL
	:	TRUE
	|	FALSE
	;
NULL_LITERAL
	:	NULL
	;
*/

NUMERIC_LITERAL

	// real
	:	('.' DECIMAL_DIGIT) =>
		 '.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
			
	|	((DECIMAL_DIGIT)+ '.' DECIMAL_DIGIT) =>
		 (DECIMAL_DIGIT)+ '.' (DECIMAL_DIGIT)+ (EXPONENT_PART)? (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
		
	|	((DECIMAL_DIGIT)+ (EXPONENT_PART)) =>
		 (DECIMAL_DIGIT)+ (EXPONENT_PART) (REAL_TYPE_SUFFIX)?
		{$setType(REAL_LITERAL);}
		
	|	((DECIMAL_DIGIT)+ (REAL_TYPE_SUFFIX)) =>
		 (DECIMAL_DIGIT)+ (REAL_TYPE_SUFFIX)		
		{$setType(REAL_LITERAL);}
		 
	// integer
	|	 (DECIMAL_DIGIT)+ (INTEGER_TYPE_SUFFIX)?	
		{$setType(INTEGER_LITERAL);}
	
	// just a dot
	| '.'{$setType(DOT);}
	;

	
HEXADECIMAL_INTEGER_LITERAL
	:	"0x"   (HEX_DIGIT)+   (INTEGER_TYPE_SUFFIX)?
	|	"0X"   (HEX_DIGIT)+   (INTEGER_TYPE_SUFFIX)?
	;

CHARACTER_LITERAL
	:	"'"!   CHARACTER   "'"!
	;	

STRING_LITERAL
	:	REGULAR_STRING_LITERAL
	|	VERBATIM_STRING_LITERAL
	;

	
// ===== literal (protected) helpers ============

// nums
protected
DECIMAL_DIGIT
	: 	'0'	|	'1'	|	'2'	|	'3'	|	'4'	|	'5'	|	'6'	|	'7'	|	'8'	|	'9'
	;
protected	
INTEGER_TYPE_SUFFIX
	:
	(	options {generateAmbigWarnings=false;}
		:	"UL"	| "LU" 	| "ul"	| "lu"
		|	"UL"	| "LU" 	| "uL"	| "lU"
		|	"U"		| "L"	| "u"	| "l"
	)
	;
		
protected
HEX_DIGIT
	:	'0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | 
		'A' | 'B' | 'C' | 'D' | 'E' | 'F'  |
		'a' | 'b' | 'c' | 'd' | 'e' | 'f'
	;	
	
protected	
EXPONENT_PART
	:	"e"  (SIGN)*  (DECIMAL_DIGIT)+
	|	"E"  (SIGN)*  (DECIMAL_DIGIT)+
	;	
	
protected
SIGN
	:	'+' | '-'
	;
	
protected
REAL_TYPE_SUFFIX
	: 'F' | 'f' | 'D' | 'd' | 'M' | 'm'
	;

// chars
protected
CHARACTER
	:	SINGLE_CHARACTER
	|	SIMPLE_ESCAPE_SEQUENCE
	|	HEXADECIMAL_ESCAPE_SEQUENCE
	|	UNICODE_ESCAPE_SEQUENCE
	;

protected
SINGLE_CHARACTER
	:	 ~( '\'' | '\\' | '\r' | '\n' | '\u2028' | '\u2029')
	;
	
protected
SIMPLE_ESCAPE_SEQUENCE
	:	"\\'" | "\\\"" | "\\\\" | "\\0" | "\\a"  
	|	"\\b" | "\\f"  | "\\n"  | "\\r" | "\\t" | "\\v"
	;
	
protected	
HEXADECIMAL_ESCAPE_SEQUENCE	
	:	('\\' 'x' HEX_DIGIT)
		( options {warnWhenFollowAmbig = false;}: 
		HEX_DIGIT 
			( options {warnWhenFollowAmbig = false;}:
			HEX_DIGIT 
				( options {warnWhenFollowAmbig = false;}:
				HEX_DIGIT
				)?
			)?
		)? // oh antlr syntax
	;

// strings
protected	
REGULAR_STRING_LITERAL
	:	'\"'!   
		(	rs:REGULAR_STRING_LITERAL_CHARACTER
		)*   
		'\"'!
	;
	
protected	
REGULAR_STRING_LITERAL_CHARACTER
	:	SINGLE_REGULAR_STRING_LITERAL_CHARACTER
	|	SIMPLE_ESCAPE_SEQUENCE
	|	HEXADECIMAL_ESCAPE_SEQUENCE
	|	UNICODE_ESCAPE_SEQUENCE
	;
	
protected	
SINGLE_REGULAR_STRING_LITERAL_CHARACTER
	:	 ~( '\"' | '\\' | '\r' | '\n' | '\u2028' | '\u2029')
	;
	
protected	
VERBATIM_STRING_LITERAL
{String s="";}
	:	 '@' "\""  	
		(	"\"\""				{s+=("\"");}
		|	"\\"				{s+=("\\\\");}
		|	ch:~('\"' | '\\')	{s+=(ch);}
		)* 
		"\""	
		{$setText(s);}
	;
			
	
// ***** A.1.9 Operators and punctuators *****
/*
Operator_or_punctuator
	:	'{'	|	'}'	|	'['	|	']'	|	'('	|	')'	|			','	|	':'	|	';'
	|	'+'	|	'-'	|	'*'	|	'/'	|	'%'	|	'&'	|	'|'	|	'^'	|	'!'	|	'~'
	|	'='	|	'<'	|	'>'	|	'?'	|	"++"|	"--"|	"&&"|	"||"|	"<<"|	">>"
	|	"=="|	"!="|	"<="|	">="|	"+="|	"-="|	"*="|	"/="|	"%="|	"&="
	|	"|="|	"^="| "<<="	| ">>=" |	"->"
	;
*/
LBRACE		:	'{'		;	RBRACE		:	'}'		;
LBRACK		:	'['		;	RBRACK		:	']'		;
LPAREN		:	'('		;	RPAREN		:	')'		;


PLUS		:	'+'		;	PLUS_ASN	:	"+="	;	
MINUS		:	'-'		;	MINUS_ASN	:	"-="	;	
STAR		:	'*'		;	STAR_ASN	:	"*="	;
DIV			:	'/'		;	DIV_ASN		:	"/="	;
MOD			:	'%'		;	MOD_ASN		:	"%="	;
INC			:	"++"	;	DEC			:	"--"	;

SL			:	"<<"	;	SL_ASN		:	"<<="	;
SR			:	">>"	;	SR_ASN		:	">>="	;
BSR			:	">>>"	;	BSR_ASN		:	">>>="	;

BAND		:	'&'		;	BAND_ASN	:	"&="	;	
BOR			:	'|'		;	BOR_ASN		:	"|="	;	
BXOR		:	'^'		;	BXOR_ASN	:	"^="	;
BNOT		:	'~'		;

ASSIGN		:	'='		;	EQUAL		:	"=="	;
LTHAN		:	'<'		;	LE			:	"<="	;
GTHAN		:	">"		;	GE			:	">="	;
LNOT		:	'!'		;	NOT_EQUAL	:	"!="	;
LOR			:	"||"	;	LAND		:	"&&"	;

COMMA		:	','		;	COLON		:	':'		;	
SEMI		:	';'		;
QUOTE		:	"\""    ;	QUESTION	:	'?'		;

ARROW		:	"->"	;


// ***** A.1.10 Pre_processing directives *****

// ignore all pp directives
PP_DIRECTIVE
	:	"#" (NOT_NEW_LINE)* (NEW_LINE)? { _ttype = Token.SKIP; }
	;

/*protected	
PP_NEW_LINE
	:	(SINGLE_LINE_COMMENT | NEW_LINE_CHARACTER)
	;
protected	
PP_WHITESPACE
	:	(	' '
		|	'\u0009' // horizontal tab character
		|	'\u000B' // vertical tab character
		|	'\u000C' // form feed character 
		)+
		{ _ttype = Token.SKIP; }
	;

PP_DIRECTIVE
	:	 HASH (PP_WHITESPACE)?
		(	dc:PP_DECLARATION//{System.out.println("===>decl: "+dc.getText());}
		| 	(PPT_REGION || PPT_END_REGION) => rg:PP_REGION	 //{System.out.println("===>regn: "+rg.getText());}
		|	PP_CONDITIONAL
//		|	PP_LINE
//		|	PP_DIAGNOSTIC
		)
	{ _ttype = Token.SKIP; }
	;
protected	
PP_DECLARATION
	:	(PPT_DEFINE | PPT_UNDEF) PP_WHITESPACE CONDITIONAL_SYMBOL (PP_WHITESPACE)? PP_NEW_LINE
	;
protected	
PP_REGION
	:	(PPT_REGION | PPT_END_REGION) PP_MESSAGE	
	;
protected
PP_MESSAGE
	:	(NOT_NEW_LINE)* NEW_LINE?
	;
protected
CONDITIONAL_SYMBOL
	:	IDENTIFIER_START_CHARACTER (IDENTIFIER_PART_CHARACTER)*
	;
protected
PPT_DEFINE
	:	"define"
	;
protected
PPT_UNDEF
	:	"undef"
	;
protected
PPT_REGION
	:	"region"
	;
protected
PPT_END_REGION
	:	"endregion"
	;

protected	
PP_CONDITIONAL
	:	PP_IF_SECTION
	|	PP_ELIF_SECTION
	|	PP_ELSE_SECTION
	|	PP_ENDIF
	;
protected	
PP_IF_SECTION
	:	PPT_IF PP_WHITESPACE PP_EXPRESSION PP_NEW_LINE // (CONDITIONAL_SECTION)?
	;
protected
PP_ELIF_SECTION
	:	PPT_ELIF PP_WHITESPACE PP_EXPRESSION PP_NEW_LINE // (CONDITIONAL_SECTION)?
	;
protected
PP_ELSE_SECTION
	:	PPT_ELSE (PP_WHITESPACE)? PP_NEW_LINE // (CONDITIONAL_SECTION)?
	;
protected
PP_ENDIF
	:	PPT_ENDIF (PP_WHITESPACE)? PP_NEW_LINE
	;
	
protected	
PP_EXPRESSION  
	:	PP_OR_EXPRESSION
	;

protected	
PP_OR_EXPRESSION
	:	PP_AND_EXPRESSION (LOR (PP_WHITESPACE)? PP_AND_EXPRESSION)*
	;
protected	
PP_AND_EXPRESSION
	:	PP_EQUALITY_EXPRESSION (LAND (PP_WHITESPACE)? PP_EQUALITY_EXPRESSION)*
	;
protected	
PP_EQUALITY_EXPRESSION
	:	PP_UNARY_EXPRESSION ((EQUAL | NOT_EQUAL) (PP_WHITESPACE)? PP_UNARY_EXPRESSION)*
	;
protected	
PP_UNARY_EXPRESSION
	:	//PP_PRIMARY_EXPRESSION (LNOT PP_PRIMARY_EXPRESSION)?
	(LNOT (PP_WHITESPACE)?)* PP_PRIMARY_EXPRESSION
	;

protected	
PP_PRIMARY_EXPRESSION
	:	((PPT_TRUE)=>PPT_TRUE 		{System.out.println("  ===>true ");}
	|	(PPT_FALSE)=>PPT_FALSE 		{System.out.println("  ===>false ");}
	|	LPAREN
		(PP_WHITESPACE)?
		ex:PP_EXPRESSION 
		RPAREN 						{System.out.println("  ===>expr "+ex.getText());}
	|	cs:CONDITIONAL_SYMBOL 		{System.out.println("  ===>symbol "+cs.getText());}
	) (PP_WHITESPACE)?
	;

	
protected
PPT_IF
	:	"if"
	;
protected
PPT_ELIF
	:	"elif"
	;
protected
PPT_ELSE
	:	"else"
	;
protected
PPT_ENDIF
	:	"endif"
	;
protected
PPT_TRUE
	:	"true"
	;
protected
PPT_FALSE
	:	"false"
	;*/

// =======================================================
// == TREE WALKER ========================================
// =======================================================

// TODO: treewalker: move AST to non antlr code dom structure (must be cls compliant).	
class CSharpTreeWalker extends TreeParser;
compilation_unit returns [String s]
{
//	string tabs = "\t\t\t";
	s = "";
}	
	:	((any:.) 
//		{	int tabCount = 3-((int)(any.getText().Length / 8)); 
//			s += "\n"+any.getText()+tabs.Substring(3-tabCount)+any.Type;}
		)*
	{return s;}
	;
	
	
	
	
