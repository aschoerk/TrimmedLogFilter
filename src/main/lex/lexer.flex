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
%caseless

%type Symbol

%{


  StringBuilder sentence = new StringBuilder();

  String lastLine = null;

  StringBuilder line = new StringBuilder();


  private Symbol symbol(Sym type) {
    return new Symbol(type, yyline, yycolumn, yytext());
  }

  private Symbol symbol(Sym type, StringBuilder text) {
      return new Symbol(type, yyline, yycolumn, text.toString());
  }

  private Symbol symbol(Sym type, String text) {
      return new Symbol(type, yyline, yycolumn, text);
  }
%}



Level = ERROR | FAILURE | WARNING | INFO | DEBUG | TRACE

LineTerminator = [ä] // \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
EndOfSentenceSeparator = [.!?]
ParenthesesExpression = \({InputCharacter}*\) | \[{InputCharacter}*\]
QuotedExpression = \"{InputCharacter}*\"
MiddleOfSentenceSeparator     = [,;:]

Word = [:jletter:]([-]?[:jletterdigit:])*

ClassOrPackagePath = {Word}(\.{Word})*

ExceptionClassOrPackagePath = {Word}(\.{Word})*\:{WhiteSpace}.*

ExceptionLine = [a][t]{WhiteSpace}{ClassOrPackagePath}((\({ClassOrPackagePath}:[0-9]*\))|(\(Native\ Method\)))

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


%state YYINITIAL SENTENCE EXCEPTION

%%

<EXCEPTION> {


  {WhiteSpace} { line.append(yytext()); }


  {WhiteSpace}+{ExceptionLine} { line.append(yytext());}

  [^] {
        lastLine = line.toString();
        line.setLength(0);
        yypushback(yytext().length());
        yybegin(YYINITIAL);
        return symbol(Sym.EXCEPTION, lastLine);
      }

}


<YYINITIAL> {

  \[{Level}\]
  | {Level}   { line.append(yytext()); return symbol(Sym.LEVEL); }

  {Word} { line.append(yytext()); sentence = new StringBuilder(yytext()); yybegin(SENTENCE); }

  {OrderedDate}   { line.append(yytext()); return symbol(Sym.DATE); }

  {OrderedTime}   { line.append(yytext()); return symbol(Sym.TIME); }

  {Timestamp}  { line.append(yytext()); return symbol(Sym.TIMESTAMP); }


  {Guid} { line.append(yytext()); return symbol(Sym.GUID); }


  {Word}(\.{Word})+\:.{5}  {
         line.setLength(0);
         line.append(yytext());
         yybegin(EXCEPTION);
     }

  {ClassOrPackagePath} { line.append(yytext()); return symbol(Sym.PATH); }




  // {DecIntegerLiteral}            {} // { return symbol(Sym.DEC_INTEGER_LITERAL); }
  // {BinIntegerLiteral}            {} // { return symbol(Sym.BIN_INTEGER_LITERAL); }
  // {HexIntegerLiteral}            {} // { return symbol(Sym.HEX_INTEGER_LITERAL); }


  {LineTerminator} {
      if (line.length() > 0) {
        line.append(yytext());
        lastLine = line.toString();
              line.setLength(0);
            }
      }

  {WhiteSpace}                   { line.append(yytext()); }
}

<SENTENCE> {

   {Word} { line.append(yytext()); sentence.append(yytext()); }

   ({MiddleOfSentenceSeparator} | [ \t\f])+ { line.append(yytext()); sentence.append(yytext()); }

    {LineTerminator} {
            lastLine = line.toString();
            line.setLength(0);
            yypushback(yytext().length());
            yybegin(YYINITIAL);
            return symbol(Sym.SENTENCE, sentence);
    }


   [^]
   ({EndOfSentenceSeparator})? {
            // don't append here to line, is pushed back. Otherwise double append would happen!!!
            yybegin(YYINITIAL);
            yypushback(yytext().length());
            return symbol(Sym.SENTENCE, sentence);
        }

}

/* error fallback */
[^]                              {
    line.append(yytext());
}