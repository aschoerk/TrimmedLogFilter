package net.oneandone.loganalyzer;


/* JFlex example: partial Java language lexer specification */
import net.oneandone.loganalyzer.helpers.Symbol;
import net.oneandone.loganalyzer.helpers.Symbol.Sym;

/**
 * This class is a simple example lexer.
 */


%%

%unicode

%class LogAnalyzerLexer

%line

%public

%type Symbol

%{


  StringBuilder sentence = new StringBuilder();

  String lastLine = null;

  StringBuilder line = new StringBuilder();

  private void printRule(String s) {
      System.out.println("\n**** Rule: " + s + " ****\n   +++++ " + yytext() + " +++++\n");
  }


  private Symbol symbol(Sym type) {
    return new Symbol(type, yyline, yytext());
  }

  private Symbol symbol(Sym type, StringBuilder text) {
      return new Symbol(type, yyline, text.toString());
  }

  private Symbol symbol(Sym type, String text) {
      return new Symbol(type, yyline, text);
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

ExceptionClassOrPackagePath = {Word}(\.{Word})*\:{WhiteSpace}.*

ExceptionLine = at\ {ClassOrPackagePath}((\({ClassOrPackagePath}:[0-9]*\))|(\(Native\ Method\)))

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

  ^{WhiteSpace}+{ExceptionLine}
     { printRule("exc ws excline"); line.append(yytext());}

  ^Caused\ by:\ .*$
     { printRule("exc caused by"); line.append(yytext());}

  ^{WhiteSpace}+\.\.\.\ [0-9]+\ more$
     { printRule("exc more"); line.append(yytext());}

  ({WhiteSpace}[\[][^\]]+[\]])$ {
    printRule("artifact extension");
    line.append(yytext());
  }

  {LineTerminator} { printRule("exc ws"); line.append(yytext()); }

  [^] {
        printRule("exc ws else");
        lastLine = line.toString();
        line.setLength(0);
        yypushback(yytext().length());
        yybegin(YYINITIAL);
        return symbol(Sym.EXCEPTION, lastLine);
      }

}


<YYINITIAL> {

  \[{Level}\]
  | {Level}   { printRule("ini level"); line.append(yytext()); return symbol(Sym.LEVEL); }

  ^U+00E4*\]  { printRule("ini Y"); line.append(yytext()); return symbol(Sym.TIMESTAMP); }

  {Word} { printRule("ini word"); line.append(yytext()); sentence = new StringBuilder(yytext()); yybegin(SENTENCE); }

  {OrderedDate}   { printRule("ini date"); line.append(yytext()); return symbol(Sym.DATE); }

  {OrderedTime}   { printRule("ini time"); line.append(yytext()); return symbol(Sym.TIME); }

  {Timestamp}  { printRule("ini timestamp"); line.append(yytext()); return symbol(Sym.TIMESTAMP); }



  {Guid} { printRule("ini level"); line.append(yytext()); return symbol(Sym.GUID); }


  ^{ExceptionClassOrPackagePath}$ {
         printRule("ini exc by word");
         line.setLength(0);
         line.append(yytext());
         yybegin(EXCEPTION);
     }

   ^{WhiteSpace}+{ExceptionLine} {
         printRule("ini exc by at");
         line.setLength(0);
         line.append(yytext());
         yybegin(EXCEPTION);
     }

  {ClassOrPackagePath} { printRule("ini path"); line.append(yytext()); return symbol(Sym.PATH); }




  // {DecIntegerLiteral}            {} // { return symbol(Sym.DEC_INTEGER_LITERAL); }
  // {BinIntegerLiteral}            {} // { return symbol(Sym.BIN_INTEGER_LITERAL); }
  // {HexIntegerLiteral}            {} // { return symbol(Sym.HEX_INTEGER_LITERAL); }


  {LineTerminator} {
      printRule("ini terminator");
      if (line.length() > 0) {
        line.append(yytext());
        lastLine = line.toString();
              line.setLength(0);
            }
      }

  {WhiteSpace}                   { printRule("ini ws"); line.append(yytext()); }
}

<SENTENCE> {

   {Word} { printRule("sen Word"); line.append(yytext()); sentence.append(yytext()); }

   ({MiddleOfSentenceSeparator} | [ \t\f])+ { printRule("sen middle"); line.append(yytext()); sentence.append(yytext()); }

    {LineTerminator} {
            printRule("sen eol");
            lastLine = line.toString();
            line.setLength(0);
            yypushback(yytext().length());
            yybegin(YYINITIAL);
            return symbol(Sym.SENTENCE, sentence);
    }


   [^]
   ({EndOfSentenceSeparator})? {
       printRule("sen else, endofsentence");
            // don't append here to line, is pushed back. Otherwise double append would happen!!!
            yybegin(YYINITIAL);
            yypushback(yytext().length());
            return symbol(Sym.SENTENCE, sentence);
        }

}

/* error fallback */
[^]                              {
    printRule("ini else ");
    line.append(yytext());
}