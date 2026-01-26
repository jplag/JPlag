package de.jplag.antlr;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes error messages from ANTLR to a logger.
 */
public class AntlrLoggerErrorListener extends BaseErrorListener {
    private static final Logger logger = LoggerFactory.getLogger(AntlrLoggerErrorListener.class);
    private static final String ERROR_TEMPLATE = "ANTLR error - in {} line {}:{} {}";

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg,
            RecognitionException e) {
        logger.error(ERROR_TEMPLATE, recognizer.getInputStream().getSourceName(), line, charPositionInLine, msg);
    }
}
