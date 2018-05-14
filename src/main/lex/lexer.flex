package net.oneandone.loganalyzer;


/* JFlex example: partial Java language lexer specification */
import net.oneandone.loganalyzer.helpers.Symbol;
import net.oneandone.loganalyzer.helpers.Symbol.Sym;

/**
 * This class is a simple example lexer.
 */


%%

%class LogAnalyzerLexer
%unicode
%line
%column
%public

%type Symbol

%{


  StringBuffer sentence = new StringBuffer();


  private Symbol symbol(Sym type) {
    return new Symbol(type, yyline, yycolumn, yytext());
  }

  private Symbol symbol(Sym type, StringBuffer text) {
      return new Symbol(type, yyline, yycolumn, text.toString());
  }
%}



Level = ERROR | FAILURE | WARNING | INFO | DEBUG | TRACE

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
EndOfSentenceSeparator = [.!?]
ParenthesesExpression = \({InputCharacter}*\) | \[{InputCharacter}*\]
QuotedExpression = \"{InputCharacter}*\"
MiddleOfSentenceSeparator     = [,;:]

Word = [:jletter:]([-]?[:jletterdigit:])*

ClassOrPackagePath = {Word}(\.{Word})*


Sentence = {Word} (({MiddleOfSentenceSeparator} | {WhiteSpace})+ {Word})+ ({EndOfSentenceSeparator}|LineTerminator)?

OrderedDate = [12][0-9]{3}-[01][0-9]-[0-3][0-9]
OrderedTime = [01][0-9]:[0-5][0-9]:[0-5][0-9]([.,][0-9]{3})?

OrderedTimestamp = {OrderedDate}{WhiteSpace}{OrderedTime}

StandardTimestamp = {OrderedDate}T{OrderedTime}([+-][01][0-9]:[0-9][0-9]|Z)?

Timestamp = {OrderedTimestamp} | {StandardTimestamp}

DecIntegerLiteral = 0 | [1-9][0-9]*
BinIntegerLiteral = 0 | [1][0-1]*
HexIntegerLiteral = 0 | [1-9a-fA-F][0-9a-fA-F]*

Guid = [a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}


%state YYINITIAL SENTENCE

%%


<YYINITIAL> {

  \[{Level}\]
  | {Level}   { return symbol(Sym.LEVEL); }

  {Word} { sentence = new StringBuffer(yytext()); yybegin(SENTENCE); }

  {OrderedDate}   { return symbol(Sym.DATE); }

  {OrderedTime}   { return symbol(Sym.TIME); }

  {Timestamp}  { return symbol(Sym.TIMESTAMP); }


  {Guid} { return symbol(Sym.GUID); }


  {ClassOrPackagePath} { return symbol(Sym.PATH); }


  // {DecIntegerLiteral}            {} // { return symbol(Sym.DEC_INTEGER_LITERAL); }
  // {BinIntegerLiteral}            {} // { return symbol(Sym.BIN_INTEGER_LITERAL); }
  // {HexIntegerLiteral}            {} // { return symbol(Sym.HEX_INTEGER_LITERAL); }

  {WhiteSpace}                   { /* ignore */ }
}

<SENTENCE> {

   {Word} { sentence.append(yytext()); }

   ({MiddleOfSentenceSeparator} | [ \t\f])+ { sentence.append(yytext()); }

   [^]
   ({EndOfSentenceSeparator}|LineTerminator)? {
            yybegin(YYINITIAL);
            yypushback(yytext().length());
            return symbol(Sym.SENTENCE, sentence);
        }



}

/* error fallback */
[^]                              { System.out.print(yytext()); }