grammar InferenceRulesGrammar;

options {
    // Otuput Language
    language = Java;

}

//Entry parser rule. A rule must have an information section (about rule),almost one premisie and almost one  conclusion
parseInferenceRule: (new_rule)+;

new_rule : (rule_information) ((premise))+ (filter)* ((conclusion))+;     
//New rule token


//Rule information. Each rule must have a name and a id identfication
rule_information : TYPE TYPE_RULE RULE_NAME LetterAndSymbol  RULE_ID NUMBER ;

//Information rule tokens
RULE_NAME : 'name : ' |'name: ';  
RULE_ID : 'id: ' | 'id : ';
TYPE : 'type: ' | 'type : ';
TYPE_RULE : NEW_RULE | INCONSITENCY_RULE;
INCONSITENCY_RULE : 'new inconsistency rule';  
NEW_RULE : 'new rule';  
           
//Premise parser rule. Eache premise must have a triple.
premise : (START_PREMISE triple);
//Start premise token
START_PREMISE : 'premise : ' | 'premise: ';

//Conclusion parser rule. Each concluisone must have a triple.
conclusion : (START_CONCLUSION triple ) | (START_CONCLUSION FALSE );
FALSE : 'false';
//Start conclcusion token
START_CONCLUSION : 'conclusion: ' | 'conclusion : ';

//Triple parse rule. Each triple is composed by subject,object and predicate with relative values
triple: ('subject: '|'subject : ')(value)( 'predicate : ' |'predicate: ')(value)('object: '|'object : ')(value);


//Filter expression

filter: FILTER condition;
FILTER: 'filter: ' | 'filter : ';
condition: expression | (complex_condition)* ;
complex_condition : (complex_espression_left BOOLEAN complex_espression_right);
complex_espression_left: complex_espression;
complex_espression_right: complex_espression;
complex_espression: expression || '(' expression_left BOOLEAN expression_right ')';
expression_left : expression;
expression_right: expression;
BOOLEAN: ' && ' | ' || ';

expression: var LOGIC_OPERATOR var ;
LOGIC_OPERATOR: ' != ' | ' < ' | ' <= ' | ' > ' | ' >= ' | ' = ';


value
:
var |
iri |
blankNode |
singleValue
;

singleValue: '"' NUMBER '"^^'iri;



var
:
VAR1
;

VAR1
:
'?' VARNAME
;

fragment
VARNAME
:
 LETTER LETTER_OR_DIGIT*
;

fragment
LETTER
:
'a'..'z' | 'A'..'Z' | '_'
;

fragment
LETTER_OR_DIGIT
:
LETTER | '0'..'9'
;


iri
:
IRIREF|
prefixedName
;

IRIREF
:
'<' (~('<' | '>' | '"' | '{' | '}' | '|' | '^' | '`' | '\u0000'..'\u0020'))* '>'
;

prefixedName
:
PNAME_LN|
PNAME_NS
;




PNAME_NS
:
PN_PREFIX? ':'
;

PNAME_LN
:
PNAME_NS PN_LOCAL
;

BLANK_NODE_LABEL
:
// '_:' PN_LOCAL( PN_CHARS_U | '0'..'9' ) ((PN_CHARS|'.')* PN_CHARS)?
'_:' PN_LOCAL
;

LANGTAG
:
'@' ('a'..'z'|'A'..'Z')+ ('-' ('a'..'z'|'A'..'Z'|'0'..'9')+)*
;

fragment
PN_LOCAL
:
(PN_CHARS_U | ':' | '0'..'9' | PLX ) ((PN_CHARS | '.' | ':' | PLX)* (PN_CHARS | ':' | PLX) )?
;

fragment
PLX
:
PERCENT | PN_LOCAL_ESC
;

fragment
PERCENT
:
'%' HEX HEX
;

fragment
HEX
:
'0'..'9' | 'A'..'F' | 'a'..'f'
;

fragment
PN_LOCAL_ESC
:
'\\' ( '_' | '~' | '.' | '-' | '!' | '$' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | ';' | '=' | '/' | '?'
| '#' | '@' | '%' )
;
fragment
PN_PREFIX
:
PN_CHARS_BASE ((PN_CHARS/*|'.'*/)* PN_CHARS)? // Dot removed since it causes a bug in the generated Lexer
;

fragment
PN_CHARS_BASE
:
'A'..'Z' | 'a'..'z'| '\u00C0'..'\u00D6' | '\u00D8'..'\u00F6'| '\u00F8'..'\u02FF' | '\u0370'..'\u037D'|
'\u037F'..'\u1FFF' | '\u200C'..'\u200D'| '\u2070'..'\u218F' | '\u2C00'..'\u2FEF' | '\u3001'..'\uD7FF' |
'\uF900'..'\uFDCF' | '\uFDF0'..'\uFFFD' 
;

fragment
PN_CHARS_U
:
PN_CHARS_BASE | '_'
;

fragment
PN_CHARS
:
PN_CHARS_U | '-' | '0'..'9' | '\u00B7' | '\u0300'..'\u036F' | '\u203F'..'\u2040'
;

literal
:
'literal'
;

blankNode
:
BLANK_NODE_LABEL
;



LetterAndSymbol : (Letter | Symbol)*Letter (Letter|Symbol)*;
fragment Letter: 'A'..'Z' | 'a'..'z';
fragment Symbol : '_'|':'|'/'|'&'|'#'|'.';

NUMBER  : ('0'..'9')+ ;

//Skip thi charcter
WS : [ \r\t\n]+ -> skip ; // skip spaces, tabs, newlines


SL_COMMENT
: '//' .*? '\n' -> skip
;
