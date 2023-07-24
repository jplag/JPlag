package de.jplag.llvmir;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import de.jplag.llvmir.grammar.LLVMIRBaseListener;
import de.jplag.llvmir.grammar.LLVMIRParser;

public class JPlagLLVMIRListener extends LLVMIRBaseListener {

    private final LLVMIRParserAdapter parserAdapter;

    public JPlagLLVMIRListener(LLVMIRParserAdapter parserAdapter) {
        this.parserAdapter = parserAdapter;
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the grammar's token given by token.
     * @param tokenType the custom token type that occurred.
     * @param token the corresponding grammar's token
     */
    private void transformToken(LLVMIRTokenType tokenType, Token token) {
        parserAdapter.addToken(tokenType, token.getLine(), token.getCharPositionInLine() + 1, token.getText().length());
    }

    /**
     * Passes a token of the given tokenType to the parserAdapter, representing the current grammatical context given by
     * start and end.
     * @param tokenType the custom token type that occurred.
     * @param start the first Token of the context
     * @param end the last Token of the context
     */
    private void transformToken(LLVMIRTokenType tokenType, Token start, Token end) {
        parserAdapter.addToken(tokenType, start.getLine(), start.getCharPositionInLine() + 1, end.getStopIndex() - start.getStartIndex() + 1);
    }

    @Override
    public void enterSourceFilename(LLVMIRParser.SourceFilenameContext ctx) {
        transformToken(LLVMIRTokenType.FILENAME, ctx.getStart(), ctx.getStop());
        super.enterSourceFilename(ctx);
    }

    @Override
    public void enterModuleAsm(LLVMIRParser.ModuleAsmContext ctx) {
        transformToken(LLVMIRTokenType.ASSEMBLY, ctx.getStart(), ctx.getStop());
        super.enterModuleAsm(ctx);
    }

    @Override
    public void enterTypeDef(LLVMIRParser.TypeDefContext ctx) {
        transformToken(LLVMIRTokenType.TYPE_DEFINITION, ctx.getStart(), ctx.getStop());
        super.enterTypeDef(ctx);
    }

    @Override
    public void enterGlobalDecl(LLVMIRParser.GlobalDeclContext ctx) {
        transformToken(LLVMIRTokenType.GLOBAL_VARIABLE, ctx.getStart(), ctx.getStop());
        super.enterGlobalDecl(ctx);
    }

    @Override
    public void enterGlobalDef(LLVMIRParser.GlobalDefContext ctx) {
        transformToken(LLVMIRTokenType.GLOBAL_VARIABLE, ctx.getStart(), ctx.getStop());
        super.enterGlobalDef(ctx);
    }

    @Override
    public void enterIndirectSymbolDef(LLVMIRParser.IndirectSymbolDefContext ctx) {
    }

    @Override
    public void exitIndirectSymbolDef(LLVMIRParser.IndirectSymbolDefContext ctx) {
    }

    @Override
    public void enterFuncDecl(LLVMIRParser.FuncDeclContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_DECL, ctx.getStart(), ctx.getStop());
        super.enterFuncDecl(ctx);
    }

    @Override
    public void enterFuncDef(LLVMIRParser.FuncDefContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_DEF, ctx.getStart(), ctx.getStop());
        super.enterFuncDef(ctx);
    }

    @Override
    public void enterIndirectSymbol(LLVMIRParser.IndirectSymbolContext ctx) {
    }

    @Override
    public void exitIndirectSymbol(LLVMIRParser.IndirectSymbolContext ctx) {
    }

    @Override
    public void enterFuncBody(LLVMIRParser.FuncBodyContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_BODY_BEGIN, ctx.getStart());
        super.enterFuncBody(ctx);
    }

    @Override
    public void exitFuncBody(LLVMIRParser.FuncBodyContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_BODY_END, ctx.getStop());
        super.exitFuncBody(ctx);
    }

    @Override
    public void enterRetTerm(LLVMIRParser.RetTermContext ctx) {
        transformToken(LLVMIRTokenType.RETURN, ctx.getStart(), ctx.getStop());
        super.enterRetTerm(ctx);
    }

    @Override
    public void enterBrTerm(LLVMIRParser.BrTermContext ctx) {
        transformToken(LLVMIRTokenType.BRANCH, ctx.getStart(), ctx.getStop());
        super.enterBrTerm(ctx);
    }

    @Override
    public void enterCondBrTerm(LLVMIRParser.CondBrTermContext ctx) {
        transformToken(LLVMIRTokenType.CONDITIONAL_BRANCH, ctx.getStart(), ctx.getStop());
        super.enterCondBrTerm(ctx);
    }

    @Override
    public void enterSwitchTerm(LLVMIRParser.SwitchTermContext ctx) {
        transformToken(LLVMIRTokenType.SWITCH, ctx.getStart(), ctx.getStop());
        super.enterSwitchTerm(ctx);
    }

    @Override
    public void enterIndirectBrTerm(LLVMIRParser.IndirectBrTermContext ctx) {
        transformToken(LLVMIRTokenType.BRANCH, ctx.getStart(), ctx.getStop());
        super.enterIndirectBrTerm(ctx);
    }

    @Override
    public void enterResumeTerm(LLVMIRParser.ResumeTermContext ctx) {
        transformToken(LLVMIRTokenType.RESUME, ctx.getStart(), ctx.getStop());
        super.enterResumeTerm(ctx);
    }

    @Override
    public void enterCatchRetTerm(LLVMIRParser.CatchRetTermContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_RETURN, ctx.getStart(), ctx.getStop());
        super.enterCatchRetTerm(ctx);
    }

    @Override
    public void enterCleanupRetTerm(LLVMIRParser.CleanupRetTermContext ctx) {
        transformToken(LLVMIRTokenType.CLEAN_UP_RETURN, ctx.getStart(), ctx.getStop());
        super.enterCleanupRetTerm(ctx);
    }

    @Override
    public void enterInvokeTerm(LLVMIRParser.InvokeTermContext ctx) {
        transformToken(LLVMIRTokenType.INVOKE, ctx.getStart(), ctx.getStop());
        super.enterInvokeTerm(ctx);
    }

    @Override
    public void enterCallBrTerm(LLVMIRParser.CallBrTermContext ctx) {
        transformToken(LLVMIRTokenType.CALL_BRANCH, ctx.getStart(), ctx.getStop());
        super.enterCallBrTerm(ctx);
    }

    @Override
    public void enterCatchSwitchTerm(LLVMIRParser.CatchSwitchTermContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_SWITCH, ctx.getStart(), ctx.getStop());
        super.enterCatchSwitchTerm(ctx);
    }

    @Override
    public void enterCase(LLVMIRParser.CaseContext ctx) {
        transformToken(LLVMIRTokenType.CASE, ctx.getStart(), ctx.getStop());
        super.enterCase(ctx);
    }

    @Override
    public void enterStructConst(LLVMIRParser.StructConstContext ctx) {
        transformToken(LLVMIRTokenType.STRUCTURE, ctx.getStart(), ctx.getStop());
        super.enterStructConst(ctx);
    }

    @Override
    public void enterArrayConst(LLVMIRParser.ArrayConstContext ctx) {
        transformToken(LLVMIRTokenType.ARRAY, ctx.getStart(), ctx.getStop());
        super.enterArrayConst(ctx);
    }

    @Override
    public void enterVectorConst(LLVMIRParser.VectorConstContext ctx) {
        transformToken(LLVMIRTokenType.VECTOR, ctx.getStart(), ctx.getStop());
        super.enterVectorConst(ctx);
    }

    @Override
    public void enterInlineAsm(LLVMIRParser.InlineAsmContext ctx) {
        transformToken(LLVMIRTokenType.ASSEMBLY, ctx.getStart(), ctx.getStop());
        super.enterInlineAsm(ctx);
    }

    @Override
    public void enterVectorType(LLVMIRParser.VectorTypeContext ctx) {
    }

    @Override
    public void enterArrayType(LLVMIRParser.ArrayTypeContext ctx) {
    }

    @Override
    public void enterStructType(LLVMIRParser.StructTypeContext ctx) {
    }

    @Override
    public void enterBitCastExpr(LLVMIRParser.BitCastExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterBitCastExpr(ctx);
    }

    @Override
    public void enterGetElementPtrExpr(LLVMIRParser.GetElementPtrExprContext ctx) {
        transformToken(LLVMIRTokenType.GET_ELEMENT_POINTER, ctx.getStart(), ctx.getStop());
        super.enterGetElementPtrExpr(ctx);
    }

    @Override
    public void enterAddrSpaceCastExpr(LLVMIRParser.AddrSpaceCastExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterAddrSpaceCastExpr(ctx);
    }

    @Override
    public void enterIntToPtrExpr(LLVMIRParser.IntToPtrExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterIntToPtrExpr(ctx);
    }

    @Override
    public void enterICmpExpr(LLVMIRParser.ICmpExprContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterICmpExpr(ctx);
    }

    @Override
    public void enterFCmpExpr(LLVMIRParser.FCmpExprContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterFCmpExpr(ctx);
    }

    @Override
    public void enterSelectExpr(LLVMIRParser.SelectExprContext ctx) {
        transformToken(LLVMIRTokenType.SELECT, ctx.getStart(), ctx.getStop());
        super.enterSelectExpr(ctx);
    }

    @Override
    public void enterTruncExpr(LLVMIRParser.TruncExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterTruncExpr(ctx);
    }

    @Override
    public void enterZExtExpr(LLVMIRParser.ZExtExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterZExtExpr(ctx);
    }

    @Override
    public void enterSExtExpr(LLVMIRParser.SExtExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSExtExpr(ctx);
    }

    @Override
    public void enterFpTruncExpr(LLVMIRParser.FpTruncExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpTruncExpr(ctx);
    }

    @Override
    public void enterFpExtExpr(LLVMIRParser.FpExtExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpExtExpr(ctx);
    }

    @Override
    public void enterFpToUiExpr(LLVMIRParser.FpToUiExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToUiExpr(ctx);
    }

    @Override
    public void enterFpToSiExpr(LLVMIRParser.FpToSiExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToSiExpr(ctx);
    }

    @Override
    public void enterUiToFpExpr(LLVMIRParser.UiToFpExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterUiToFpExpr(ctx);
    }

    @Override
    public void enterSiToFpExpr(LLVMIRParser.SiToFpExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSiToFpExpr(ctx);
    }

    @Override
    public void enterPtrToIntExpr(LLVMIRParser.PtrToIntExprContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterPtrToIntExpr(ctx);
    }

    @Override
    public void enterExtractElementExpr(LLVMIRParser.ExtractElementExprContext ctx) {
        transformToken(LLVMIRTokenType.EXTRACT_ELEM, ctx.getStart(), ctx.getStop());
        super.enterExtractElementExpr(ctx);
    }

    @Override
    public void enterInsertElementExpr(LLVMIRParser.InsertElementExprContext ctx) {
        transformToken(LLVMIRTokenType.INSERT_ELEM, ctx.getStart(), ctx.getStop());
        super.enterInsertElementExpr(ctx);
    }

    @Override
    public void enterShuffleVectorExpr(LLVMIRParser.ShuffleVectorExprContext ctx) {
        transformToken(LLVMIRTokenType.SHUFFLE_VEC, ctx.getStart(), ctx.getStop());
        super.enterShuffleVectorExpr(ctx);
    }

    @Override
    public void enterShlExpr(LLVMIRParser.ShlExprContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterShlExpr(ctx);
    }

    @Override
    public void enterLShrExpr(LLVMIRParser.LShrExprContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterLShrExpr(ctx);
    }

    @Override
    public void enterAShrExpr(LLVMIRParser.AShrExprContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterAShrExpr(ctx);
    }

    @Override
    public void enterAndExpr(LLVMIRParser.AndExprContext ctx) {
        transformToken(LLVMIRTokenType.AND, ctx.getStart(), ctx.getStop());
        super.enterAndExpr(ctx);
    }

    @Override
    public void enterOrExpr(LLVMIRParser.OrExprContext ctx) {
        transformToken(LLVMIRTokenType.OR, ctx.getStart(), ctx.getStop());
        super.enterOrExpr(ctx);
    }

    @Override
    public void enterXorExpr(LLVMIRParser.XorExprContext ctx) {
        transformToken(LLVMIRTokenType.XOR, ctx.getStart(), ctx.getStop());
        super.enterXorExpr(ctx);
    }

    @Override
    public void enterAddExpr(LLVMIRParser.AddExprContext ctx) {
        transformToken(LLVMIRTokenType.ADD, ctx.getStart(), ctx.getStop());
        super.enterAddExpr(ctx);
    }

    @Override
    public void enterSubExpr(LLVMIRParser.SubExprContext ctx) {
        transformToken(LLVMIRTokenType.SUB, ctx.getStart(), ctx.getStop());
        super.enterSubExpr(ctx);
    }

    @Override
    public void enterMulExpr(LLVMIRParser.MulExprContext ctx) {
        transformToken(LLVMIRTokenType.MUL, ctx.getStart(), ctx.getStop());
        super.enterMulExpr(ctx);
    }

    @Override
    public void enterFNegExpr(LLVMIRParser.FNegExprContext ctx) {
    }

    @Override
    public void enterStoreInst(LLVMIRParser.StoreInstContext ctx) {
        transformToken(LLVMIRTokenType.STORE, ctx.getStart(), ctx.getStop());
        super.enterStoreInst(ctx);
    }

    @Override
    public void enterFenceInst(LLVMIRParser.FenceInstContext ctx) {
        transformToken(LLVMIRTokenType.FENCE, ctx.getStart(), ctx.getStop());
        super.enterFenceInst(ctx);
    }

    @Override
    public void enterFNegInst(LLVMIRParser.FNegInstContext ctx) {
    }

    @Override
    public void enterAddInst(LLVMIRParser.AddInstContext ctx) {
        transformToken(LLVMIRTokenType.ADD, ctx.getStart(), ctx.getStop());
        super.enterAddInst(ctx);
    }

    @Override
    public void enterFAddInst(LLVMIRParser.FAddInstContext ctx) {
        transformToken(LLVMIRTokenType.ADD, ctx.getStart(), ctx.getStop());
        super.enterFAddInst(ctx);
    }

    @Override
    public void enterSubInst(LLVMIRParser.SubInstContext ctx) {
        transformToken(LLVMIRTokenType.SUB, ctx.getStart(), ctx.getStop());
        super.enterSubInst(ctx);
    }

    @Override
    public void enterFSubInst(LLVMIRParser.FSubInstContext ctx) {
        transformToken(LLVMIRTokenType.SUB, ctx.getStart(), ctx.getStop());
        super.enterFSubInst(ctx);
    }

    @Override
    public void enterMulInst(LLVMIRParser.MulInstContext ctx) {
        transformToken(LLVMIRTokenType.MUL, ctx.getStart(), ctx.getStop());
        super.enterMulInst(ctx);
    }

    @Override
    public void enterFMulInst(LLVMIRParser.FMulInstContext ctx) {
        transformToken(LLVMIRTokenType.MUL, ctx.getStart(), ctx.getStop());
        super.enterFMulInst(ctx);
    }

    @Override
    public void enterUDivInst(LLVMIRParser.UDivInstContext ctx) {
        transformToken(LLVMIRTokenType.DIV, ctx.getStart(), ctx.getStop());
        super.enterUDivInst(ctx);
    }

    @Override
    public void enterSDivInst(LLVMIRParser.SDivInstContext ctx) {
        transformToken(LLVMIRTokenType.DIV, ctx.getStart(), ctx.getStop());
        super.enterSDivInst(ctx);
    }

    @Override
    public void enterFDivInst(LLVMIRParser.FDivInstContext ctx) {
        transformToken(LLVMIRTokenType.DIV, ctx.getStart(), ctx.getStop());
        super.enterFDivInst(ctx);
    }

    @Override
    public void enterURemInst(LLVMIRParser.URemInstContext ctx) {
        transformToken(LLVMIRTokenType.REM, ctx.getStart(), ctx.getStop());
        super.enterURemInst(ctx);
    }

    @Override
    public void enterSRemInst(LLVMIRParser.SRemInstContext ctx) {
        transformToken(LLVMIRTokenType.REM, ctx.getStart(), ctx.getStop());
        super.enterSRemInst(ctx);
    }

    @Override
    public void enterFRemInst(LLVMIRParser.FRemInstContext ctx) {
        transformToken(LLVMIRTokenType.REM, ctx.getStart(), ctx.getStop());
        super.enterFRemInst(ctx);
    }

    @Override
    public void enterShlInst(LLVMIRParser.ShlInstContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterShlInst(ctx);
    }

    @Override
    public void enterLShrInst(LLVMIRParser.LShrInstContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterLShrInst(ctx);
    }

    @Override
    public void enterAShrInst(LLVMIRParser.AShrInstContext ctx) {
        transformToken(LLVMIRTokenType.SHIFT, ctx.getStart(), ctx.getStop());
        super.enterAShrInst(ctx);
    }

    @Override
    public void enterAndInst(LLVMIRParser.AndInstContext ctx) {
        transformToken(LLVMIRTokenType.AND, ctx.getStart(), ctx.getStop());
        super.enterAndInst(ctx);
    }

    @Override
    public void enterOrInst(LLVMIRParser.OrInstContext ctx) {
        transformToken(LLVMIRTokenType.OR, ctx.getStart(), ctx.getStop());
        super.enterOrInst(ctx);
    }

    @Override
    public void enterXorInst(LLVMIRParser.XorInstContext ctx) {
        transformToken(LLVMIRTokenType.XOR, ctx.getStart(), ctx.getStop());
        super.enterXorInst(ctx);
    }

    @Override
    public void enterExtractElementInst(LLVMIRParser.ExtractElementInstContext ctx) {
        transformToken(LLVMIRTokenType.EXTRACT_ELEM, ctx.getStart(), ctx.getStop());
        super.enterExtractElementInst(ctx);
    }

    @Override
    public void enterInsertElementInst(LLVMIRParser.InsertElementInstContext ctx) {
        transformToken(LLVMIRTokenType.INSERT_ELEM, ctx.getStart(), ctx.getStop());
        super.enterInsertElementInst(ctx);
    }

    @Override
    public void enterShuffleVectorInst(LLVMIRParser.ShuffleVectorInstContext ctx) {
        transformToken(LLVMIRTokenType.SHUFFLE_VEC, ctx.getStart(), ctx.getStop());
        super.enterShuffleVectorInst(ctx);
    }

    @Override
    public void enterExtractValueInst(LLVMIRParser.ExtractValueInstContext ctx) {
        transformToken(LLVMIRTokenType.EXTRACT_VAL, ctx.getStart(), ctx.getStop());
        super.enterExtractValueInst(ctx);
    }

    @Override
    public void enterInsertValueInst(LLVMIRParser.InsertValueInstContext ctx) {
        transformToken(LLVMIRTokenType.INSERT_VAL, ctx.getStart(), ctx.getStop());
        super.enterInsertValueInst(ctx);
    }

    @Override
    public void enterAllocaInst(LLVMIRParser.AllocaInstContext ctx) {
        transformToken(LLVMIRTokenType.ALLOCATION, ctx.getStart(), ctx.getStop());
        super.enterAllocaInst(ctx);
    }

    @Override
    public void enterLoadInst(LLVMIRParser.LoadInstContext ctx) {
        transformToken(LLVMIRTokenType.LOAD, ctx.getStart(), ctx.getStop());
        super.enterLoadInst(ctx);
    }

    @Override
    public void enterCmpXchgInst(LLVMIRParser.CmpXchgInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARE_EXCHANGE, ctx.getStart(), ctx.getStop());
        super.enterCmpXchgInst(ctx);
    }

    @Override
    public void enterAtomicRMWInst(LLVMIRParser.AtomicRMWInstContext ctx) {
        transformToken(LLVMIRTokenType.ATOMIC_READ_MODIFY_WRITE, ctx.getStart(), ctx.getStop());
        super.enterAtomicRMWInst(ctx);
    }

    @Override
    public void enterGetElementPtrInst(LLVMIRParser.GetElementPtrInstContext ctx) {
        transformToken(LLVMIRTokenType.GET_ELEMENT_POINTER, ctx.getStart(), ctx.getStop());
        super.enterGetElementPtrInst(ctx);
    }

    @Override
    public void enterTruncInst(LLVMIRParser.TruncInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterTruncInst(ctx);
    }

    @Override
    public void enterZExtInst(LLVMIRParser.ZExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterZExtInst(ctx);
    }

    @Override
    public void enterSExtInst(LLVMIRParser.SExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSExtInst(ctx);
    }

    @Override
    public void enterFpTruncInst(LLVMIRParser.FpTruncInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpTruncInst(ctx);
    }

    @Override
    public void enterFpExtInst(LLVMIRParser.FpExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpExtInst(ctx);
    }

    @Override
    public void enterFpToUiInst(LLVMIRParser.FpToUiInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToUiInst(ctx);
    }

    @Override
    public void enterFpToSiInst(LLVMIRParser.FpToSiInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToSiInst(ctx);
    }

    @Override
    public void enterUiToFpInst(LLVMIRParser.UiToFpInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterUiToFpInst(ctx);
    }

    @Override
    public void enterSiToFpInst(LLVMIRParser.SiToFpInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSiToFpInst(ctx);
    }

    @Override
    public void enterPtrToIntInst(LLVMIRParser.PtrToIntInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterPtrToIntInst(ctx);
    }

    @Override
    public void enterIntToPtrInst(LLVMIRParser.IntToPtrInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterIntToPtrInst(ctx);
    }

    @Override
    public void enterBitCastInst(LLVMIRParser.BitCastInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterBitCastInst(ctx);
    }

    @Override
    public void enterAddrSpaceCastInst(LLVMIRParser.AddrSpaceCastInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterAddrSpaceCastInst(ctx);
    }

    @Override
    public void enterICmpInst(LLVMIRParser.ICmpInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterICmpInst(ctx);
    }

    @Override
    public void enterFCmpInst(LLVMIRParser.FCmpInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterFCmpInst(ctx);
    }

    @Override
    public void enterPhiInst(LLVMIRParser.PhiInstContext ctx) {
        transformToken(LLVMIRTokenType.PHI, ctx.getStart(), ctx.getStop());
        super.enterPhiInst(ctx);
    }

    @Override
    public void enterSelectInst(LLVMIRParser.SelectInstContext ctx) {
        transformToken(LLVMIRTokenType.SELECT, ctx.getStart(), ctx.getStop());
        super.enterSelectInst(ctx);
    }

    @Override
    public void enterCallInst(LLVMIRParser.CallInstContext ctx) {
        transformToken(LLVMIRTokenType.CALL, ctx.getStart(), ctx.getStop());
        super.enterCallInst(ctx);
    }

    @Override
    public void enterLandingPadInst(LLVMIRParser.LandingPadInstContext ctx) {
        transformToken(LLVMIRTokenType.LANDING_PAD, ctx.getStart(), ctx.getStop());
        super.enterLandingPadInst(ctx);
    }

    @Override
    public void enterCatchPadInst(LLVMIRParser.CatchPadInstContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_PAD, ctx.getStart(), ctx.getStop());
        super.enterCatchPadInst(ctx);
    }

    @Override
    public void enterCleanupPadInst(LLVMIRParser.CleanupPadInstContext ctx) {
        transformToken(LLVMIRTokenType.CLEAN_UP_PAD, ctx.getStart(), ctx.getStop());
        super.enterCleanupPadInst(ctx);
    }

    @Override
    public void enterClause(LLVMIRParser.ClauseContext ctx) {
    }

    @Override
    public void enterAtomicOrdering(LLVMIRParser.AtomicOrderingContext ctx) {
    }

    @Override
    public void exitAtomicOrdering(LLVMIRParser.AtomicOrderingContext ctx) {
    }

    @Override
    public void enterAtomicOp(LLVMIRParser.AtomicOpContext ctx) {
    }

    @Override
    public void exitAtomicOp(LLVMIRParser.AtomicOpContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

}
