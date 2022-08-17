package de.jplag.csharp.grammar;

import java.util.Stack;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

/**
 * This class was taken from <a href="https://github.com/antlr/grammars-v4/tree/master/csharp">antlr/grammars-v4</a>. It
 * was originally written by Ken Domino. Note that this class is licensed under Eclipse Public License - v 1.0.
 */
abstract class CSharpLexerBase extends Lexer {
    protected CSharpLexerBase(CharStream input) {
        super(input);
    }

    protected int interpolatedStringLevel;
    protected Stack<Boolean> interpolatedVerbatiums = new Stack<>();
    protected Stack<Integer> curlyLevels = new Stack<>();
    protected boolean verbatium;

    protected void onInterpolatedRegularStringStart() {
        interpolatedStringLevel++;
        interpolatedVerbatiums.push(false);
        verbatium = false;
    }

    protected void onInterpolatedVerbatiumStringStart() {
        interpolatedStringLevel++;
        interpolatedVerbatiums.push(true);
        verbatium = true;
    }

    protected void onOpenBrace() {
        if (interpolatedStringLevel > 0) {
            curlyLevels.push(curlyLevels.pop() + 1);
        }
    }

    protected void onCloseBrace() {

        if (interpolatedStringLevel > 0) {
            curlyLevels.push(curlyLevels.pop() - 1);
            if (curlyLevels.peek() == 0) {
                curlyLevels.pop();
                skip();
                popMode();
            }
        }
    }

    protected void onColon() {
        if (interpolatedStringLevel > 0) {
            int ind = 1;
            boolean switchToFormatString = true;
            while ((char) _input.LA(ind) != '}') {
                if (_input.LA(ind) == ':' || _input.LA(ind) == ')') {
                    switchToFormatString = false;
                    break;
                }
                ind++;
            }
            if (switchToFormatString) {
                mode(CSharpLexer.INTERPOLATION_FORMAT);
            }
        }
    }

    protected void openBraceInside() {
        curlyLevels.push(1);
    }

    protected void onDoubleQuoteInside() {
        interpolatedStringLevel--;
        interpolatedVerbatiums.pop();
        verbatium = (interpolatedVerbatiums.size() > 0 ? interpolatedVerbatiums.peek() : false);
    }

    protected void onCloseBraceInside() {
        curlyLevels.pop();
    }

    protected boolean isRegularCharInside() {
        return !verbatium;
    }

    protected boolean isVerbatiumDoubleQuoteInside() {
        return verbatium;
    }
}
