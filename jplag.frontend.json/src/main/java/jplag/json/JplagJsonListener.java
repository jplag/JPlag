package jplag.json;

import jplag.json.grammar.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JplagJsonListener implements JsonListener, JsonTokenConstants {

    private jplag.json.Parser jplagParser;

    public JplagJsonListener(jplag.json.Parser jplag) {
        jplagParser = jplag;
    }

    @Override
    public void enterCompilationUnit(JsonParser.CompilationUnitContext ctx) {

    }

    @Override
    public void exitCompilationUnit(JsonParser.CompilationUnitContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void enterArray(JsonParser.ArrayContext ctx) {
        jplagParser.add(JsonTokenConstants.ARRAY_START, ctx.getStart());
    }

    @Override
    public void exitArray(JsonParser.ArrayContext ctx) {
        jplagParser.addEnd(JsonTokenConstants.ARRAY_END, ctx.getStart());
    }

    @Override
    public void enterObj(JsonParser.ObjContext ctx) {
        jplagParser.add(JsonTokenConstants.OBJECT_START, ctx.getStart());

    }

    @Override
    public void exitObj(JsonParser.ObjContext ctx) {
        jplagParser.addEnd(JsonTokenConstants.OBJECT_END, ctx.getStart());
    }

    @Override
    public void enterJson(JsonParser.JsonContext ctx) {

    }

    @Override
    public void exitJson(JsonParser.JsonContext ctx) {

    }

    @Override
    public void enterValue(JsonParser.ValueContext ctx) {
        if (ctx.STRING() == null) {
            jplagParser.add(JsonTokenConstants.NUMBER, ctx.getStart());
        } else {
            jplagParser.add(ctx.getText(), ctx.getStart());
        }
    }

    @Override
    public void exitValue(JsonParser.ValueContext ctx) {

    }

    @Override
    public void enterPair(JsonParser.PairContext ctx) {
        jplagParser.add(ctx.STRING().getText(), ctx.getStart());
    }

    @Override
    public void exitPair(JsonParser.PairContext ctx) {

    }
}
