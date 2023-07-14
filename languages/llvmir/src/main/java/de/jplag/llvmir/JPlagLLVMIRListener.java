package de.jplag.llvmir;

import org.antlr.v4.runtime.ParserRuleContext;
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
    public void enterCompilationUnit(LLVMIRParser.CompilationUnitContext ctx) {
    }

    @Override
    public void exitCompilationUnit(LLVMIRParser.CompilationUnitContext ctx) {
    }

    @Override
    public void enterTargetDef(LLVMIRParser.TargetDefContext ctx) {
    }

    @Override
    public void exitTargetDef(LLVMIRParser.TargetDefContext ctx) {
    }

    @Override
    public void enterSourceFilename(LLVMIRParser.SourceFilenameContext ctx) {
    }

    @Override
    public void exitSourceFilename(LLVMIRParser.SourceFilenameContext ctx) {
    }

    @Override
    public void enterTargetDataLayout(LLVMIRParser.TargetDataLayoutContext ctx) {
    }

    @Override
    public void exitTargetDataLayout(LLVMIRParser.TargetDataLayoutContext ctx) {
    }

    @Override
    public void enterTargetTriple(LLVMIRParser.TargetTripleContext ctx) {
    }

    @Override
    public void exitTargetTriple(LLVMIRParser.TargetTripleContext ctx) {
    }

    @Override
    public void enterTopLevelEntity(LLVMIRParser.TopLevelEntityContext ctx) {
    }

    @Override
    public void exitTopLevelEntity(LLVMIRParser.TopLevelEntityContext ctx) {
    }

    @Override
    public void enterModuleAsm(LLVMIRParser.ModuleAsmContext ctx) {
    }

    @Override
    public void exitModuleAsm(LLVMIRParser.ModuleAsmContext ctx) {
    }

    @Override
    public void enterTypeDef(LLVMIRParser.TypeDefContext ctx) {
    }

    @Override
    public void exitTypeDef(LLVMIRParser.TypeDefContext ctx) {
    }

    @Override
    public void enterComdatDef(LLVMIRParser.ComdatDefContext ctx) {
    }

    @Override
    public void exitComdatDef(LLVMIRParser.ComdatDefContext ctx) {
    }

    @Override
    public void enterGlobalDecl(LLVMIRParser.GlobalDeclContext ctx) {
    }

    @Override
    public void exitGlobalDecl(LLVMIRParser.GlobalDeclContext ctx) {
    }

    @Override
    public void enterGlobalDef(LLVMIRParser.GlobalDefContext ctx) {
    }

    @Override
    public void exitGlobalDef(LLVMIRParser.GlobalDefContext ctx) {
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
    public void exitFuncDecl(LLVMIRParser.FuncDeclContext ctx) {
    }

    @Override
    public void enterFuncDef(LLVMIRParser.FuncDefContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_DEF, ctx.getStart(), ctx.getStop());
        super.enterFuncDef(ctx);
    }

    @Override
    public void exitFuncDef(LLVMIRParser.FuncDefContext ctx) {
    }

    @Override
    public void enterAttrGroupDef(LLVMIRParser.AttrGroupDefContext ctx) {
    }

    @Override
    public void exitAttrGroupDef(LLVMIRParser.AttrGroupDefContext ctx) {
    }

    @Override
    public void enterNamedMetadataDef(LLVMIRParser.NamedMetadataDefContext ctx) {
    }

    @Override
    public void exitNamedMetadataDef(LLVMIRParser.NamedMetadataDefContext ctx) {
    }

    @Override
    public void enterMetadataDef(LLVMIRParser.MetadataDefContext ctx) {
    }

    @Override
    public void exitMetadataDef(LLVMIRParser.MetadataDefContext ctx) {
    }

    @Override
    public void enterUseListOrder(LLVMIRParser.UseListOrderContext ctx) {
    }

    @Override
    public void exitUseListOrder(LLVMIRParser.UseListOrderContext ctx) {
    }

    @Override
    public void enterUseListOrderBB(LLVMIRParser.UseListOrderBBContext ctx) {
    }

    @Override
    public void exitUseListOrderBB(LLVMIRParser.UseListOrderBBContext ctx) {
    }

    @Override
    public void enterFuncHeader(LLVMIRParser.FuncHeaderContext ctx) {
    }

    @Override
    public void exitFuncHeader(LLVMIRParser.FuncHeaderContext ctx) {
    }

    @Override
    public void enterIndirectSymbol(LLVMIRParser.IndirectSymbolContext ctx) {
    }

    @Override
    public void exitIndirectSymbol(LLVMIRParser.IndirectSymbolContext ctx) {
    }

    @Override
    public void enterCallingConv(LLVMIRParser.CallingConvContext ctx) {
    }

    @Override
    public void exitCallingConv(LLVMIRParser.CallingConvContext ctx) {
    }

    @Override
    public void enterCallingConvInt(LLVMIRParser.CallingConvIntContext ctx) {
    }

    @Override
    public void exitCallingConvInt(LLVMIRParser.CallingConvIntContext ctx) {
    }

    @Override
    public void enterFuncHdrField(LLVMIRParser.FuncHdrFieldContext ctx) {
    }

    @Override
    public void exitFuncHdrField(LLVMIRParser.FuncHdrFieldContext ctx) {
    }

    @Override
    public void enterGc(LLVMIRParser.GcContext ctx) {
    }

    @Override
    public void exitGc(LLVMIRParser.GcContext ctx) {
    }

    @Override
    public void enterPrefix(LLVMIRParser.PrefixContext ctx) {
    }

    @Override
    public void exitPrefix(LLVMIRParser.PrefixContext ctx) {
    }

    @Override
    public void enterPrologue(LLVMIRParser.PrologueContext ctx) {
    }

    @Override
    public void exitPrologue(LLVMIRParser.PrologueContext ctx) {
    }

    @Override
    public void enterPersonality(LLVMIRParser.PersonalityContext ctx) {
    }

    @Override
    public void exitPersonality(LLVMIRParser.PersonalityContext ctx) {
    }

    @Override
    public void enterReturnAttribute(LLVMIRParser.ReturnAttributeContext ctx) {
    }

    @Override
    public void exitReturnAttribute(LLVMIRParser.ReturnAttributeContext ctx) {
    }

    @Override
    public void enterFuncBody(LLVMIRParser.FuncBodyContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_BODY_BEGIN, ctx.getStart(), ctx.getStop());
        super.enterFuncBody(ctx);
    }

    @Override
    public void exitFuncBody(LLVMIRParser.FuncBodyContext ctx) {
        transformToken(LLVMIRTokenType.FUNCTION_BODY_END, ctx.getStart(), ctx.getStop());
        super.exitFuncBody(ctx);
    }

    @Override
    public void enterBasicBlock(LLVMIRParser.BasicBlockContext ctx) {
    }

    @Override
    public void exitBasicBlock(LLVMIRParser.BasicBlockContext ctx) {
    }

    @Override
    public void enterInstruction(LLVMIRParser.InstructionContext ctx) {
    }

    @Override
    public void exitInstruction(LLVMIRParser.InstructionContext ctx) {
    }

    @Override
    public void enterTerminator(LLVMIRParser.TerminatorContext ctx) {
    }

    @Override
    public void exitTerminator(LLVMIRParser.TerminatorContext ctx) {
    }

    @Override
    public void enterLocalDefTerm(LLVMIRParser.LocalDefTermContext ctx) {
    }

    @Override
    public void exitLocalDefTerm(LLVMIRParser.LocalDefTermContext ctx) {
    }

    @Override
    public void enterValueTerminator(LLVMIRParser.ValueTerminatorContext ctx) {
    }

    @Override
    public void exitValueTerminator(LLVMIRParser.ValueTerminatorContext ctx) {
    }

    @Override
    public void enterRetTerm(LLVMIRParser.RetTermContext ctx) {
        transformToken(LLVMIRTokenType.RETURN, ctx.getStart(), ctx.getStop());
        super.enterRetTerm(ctx);
    }

    @Override
    public void exitRetTerm(LLVMIRParser.RetTermContext ctx) {
    }

    @Override
    public void enterBrTerm(LLVMIRParser.BrTermContext ctx) {
        transformToken(LLVMIRTokenType.BRANCH, ctx.getStart(), ctx.getStop());
        super.enterBrTerm(ctx);
    }

    @Override
    public void exitBrTerm(LLVMIRParser.BrTermContext ctx) {
    }

    @Override
    public void enterCondBrTerm(LLVMIRParser.CondBrTermContext ctx) {
        transformToken(LLVMIRTokenType.COND_BRANCH, ctx.getStart(), ctx.getStop());
        super.enterCondBrTerm(ctx);
    }

    @Override
    public void exitCondBrTerm(LLVMIRParser.CondBrTermContext ctx) {
    }

    @Override
    public void enterSwitchTerm(LLVMIRParser.SwitchTermContext ctx) {
        transformToken(LLVMIRTokenType.SWITCH, ctx.getStart(), ctx.getStop());
        super.enterSwitchTerm(ctx);
    }

    @Override
    public void exitSwitchTerm(LLVMIRParser.SwitchTermContext ctx) {
    }

    @Override
    public void enterIndirectBrTerm(LLVMIRParser.IndirectBrTermContext ctx) {
        transformToken(LLVMIRTokenType.INDIRECT_BRANCH, ctx.getStart(), ctx.getStop());
        super.enterIndirectBrTerm(ctx);
    }

    @Override
    public void exitIndirectBrTerm(LLVMIRParser.IndirectBrTermContext ctx) {
    }

    @Override
    public void enterResumeTerm(LLVMIRParser.ResumeTermContext ctx) {
        transformToken(LLVMIRTokenType.RESUME, ctx.getStart(), ctx.getStop());
        super.enterResumeTerm(ctx);
    }

    @Override
    public void exitResumeTerm(LLVMIRParser.ResumeTermContext ctx) {
    }

    @Override
    public void enterCatchRetTerm(LLVMIRParser.CatchRetTermContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_RETURN, ctx.getStart(), ctx.getStop());
        super.enterCatchRetTerm(ctx);
    }

    @Override
    public void exitCatchRetTerm(LLVMIRParser.CatchRetTermContext ctx) {
    }

    @Override
    public void enterCleanupRetTerm(LLVMIRParser.CleanupRetTermContext ctx) {
        transformToken(LLVMIRTokenType.CLEAN_UP_RETURN, ctx.getStart(), ctx.getStop());
        super.enterCleanupRetTerm(ctx);
    }

    @Override
    public void exitCleanupRetTerm(LLVMIRParser.CleanupRetTermContext ctx) {
    }

    @Override
    public void enterUnreachableTerm(LLVMIRParser.UnreachableTermContext ctx) {
    }

    @Override
    public void exitUnreachableTerm(LLVMIRParser.UnreachableTermContext ctx) {
    }

    @Override
    public void enterInvokeTerm(LLVMIRParser.InvokeTermContext ctx) {
        transformToken(LLVMIRTokenType.INVOKE, ctx.getStart(), ctx.getStop());
        super.enterInvokeTerm(ctx);
    }

    @Override
    public void exitInvokeTerm(LLVMIRParser.InvokeTermContext ctx) {
    }

    @Override
    public void enterCallBrTerm(LLVMIRParser.CallBrTermContext ctx) {
        transformToken(LLVMIRTokenType.CALL_BRANCH, ctx.getStart(), ctx.getStop());
        super.enterCallBrTerm(ctx);
    }

    @Override
    public void exitCallBrTerm(LLVMIRParser.CallBrTermContext ctx) {
    }

    @Override
    public void enterCatchSwitchTerm(LLVMIRParser.CatchSwitchTermContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_SWITCH, ctx.getStart(), ctx.getStop());
        super.enterCatchSwitchTerm(ctx);
    }

    @Override
    public void exitCatchSwitchTerm(LLVMIRParser.CatchSwitchTermContext ctx) {
    }

    @Override
    public void enterLabel(LLVMIRParser.LabelContext ctx) {
    }

    @Override
    public void exitLabel(LLVMIRParser.LabelContext ctx) {
    }

    @Override
    public void enterCase(LLVMIRParser.CaseContext ctx) {
    }

    @Override
    public void exitCase(LLVMIRParser.CaseContext ctx) {
    }

    @Override
    public void enterUnwindTarget(LLVMIRParser.UnwindTargetContext ctx) {
    }

    @Override
    public void exitUnwindTarget(LLVMIRParser.UnwindTargetContext ctx) {
    }

    @Override
    public void enterHandlers(LLVMIRParser.HandlersContext ctx) {
    }

    @Override
    public void exitHandlers(LLVMIRParser.HandlersContext ctx) {
    }

    @Override
    public void enterMetadataNode(LLVMIRParser.MetadataNodeContext ctx) {
    }

    @Override
    public void exitMetadataNode(LLVMIRParser.MetadataNodeContext ctx) {
    }

    @Override
    public void enterDiExpression(LLVMIRParser.DiExpressionContext ctx) {
    }

    @Override
    public void exitDiExpression(LLVMIRParser.DiExpressionContext ctx) {
    }

    @Override
    public void enterDiExpressionField(LLVMIRParser.DiExpressionFieldContext ctx) {
    }

    @Override
    public void exitDiExpressionField(LLVMIRParser.DiExpressionFieldContext ctx) {
    }

    @Override
    public void enterGlobalField(LLVMIRParser.GlobalFieldContext ctx) {
    }

    @Override
    public void exitGlobalField(LLVMIRParser.GlobalFieldContext ctx) {
    }

    @Override
    public void enterSection(LLVMIRParser.SectionContext ctx) {
    }

    @Override
    public void exitSection(LLVMIRParser.SectionContext ctx) {
    }

    @Override
    public void enterComdat(LLVMIRParser.ComdatContext ctx) {
    }

    @Override
    public void exitComdat(LLVMIRParser.ComdatContext ctx) {
    }

    @Override
    public void enterPartition(LLVMIRParser.PartitionContext ctx) {
    }

    @Override
    public void exitPartition(LLVMIRParser.PartitionContext ctx) {
    }

    @Override
    public void enterConstant(LLVMIRParser.ConstantContext ctx) {
    }

    @Override
    public void exitConstant(LLVMIRParser.ConstantContext ctx) {
    }

    @Override
    public void enterBoolConst(LLVMIRParser.BoolConstContext ctx) {
    }

    @Override
    public void exitBoolConst(LLVMIRParser.BoolConstContext ctx) {
    }

    @Override
    public void enterIntConst(LLVMIRParser.IntConstContext ctx) {
    }

    @Override
    public void exitIntConst(LLVMIRParser.IntConstContext ctx) {
    }

    @Override
    public void enterFloatConst(LLVMIRParser.FloatConstContext ctx) {
    }

    @Override
    public void exitFloatConst(LLVMIRParser.FloatConstContext ctx) {
    }

    @Override
    public void enterNullConst(LLVMIRParser.NullConstContext ctx) {
    }

    @Override
    public void exitNullConst(LLVMIRParser.NullConstContext ctx) {
    }

    @Override
    public void enterNoneConst(LLVMIRParser.NoneConstContext ctx) {
    }

    @Override
    public void exitNoneConst(LLVMIRParser.NoneConstContext ctx) {
    }

    @Override
    public void enterStructConst(LLVMIRParser.StructConstContext ctx) {
    }

    @Override
    public void exitStructConst(LLVMIRParser.StructConstContext ctx) {
    }

    @Override
    public void enterArrayConst(LLVMIRParser.ArrayConstContext ctx) {
    }

    @Override
    public void exitArrayConst(LLVMIRParser.ArrayConstContext ctx) {
    }

    @Override
    public void enterVectorConst(LLVMIRParser.VectorConstContext ctx) {
    }

    @Override
    public void exitVectorConst(LLVMIRParser.VectorConstContext ctx) {
    }

    @Override
    public void enterZeroInitializerConst(LLVMIRParser.ZeroInitializerConstContext ctx) {
    }

    @Override
    public void exitZeroInitializerConst(LLVMIRParser.ZeroInitializerConstContext ctx) {
    }

    @Override
    public void enterUndefConst(LLVMIRParser.UndefConstContext ctx) {
    }

    @Override
    public void exitUndefConst(LLVMIRParser.UndefConstContext ctx) {
    }

    @Override
    public void enterPoisonConst(LLVMIRParser.PoisonConstContext ctx) {
    }

    @Override
    public void exitPoisonConst(LLVMIRParser.PoisonConstContext ctx) {
    }

    @Override
    public void enterBlockAddressConst(LLVMIRParser.BlockAddressConstContext ctx) {
    }

    @Override
    public void exitBlockAddressConst(LLVMIRParser.BlockAddressConstContext ctx) {
    }

    @Override
    public void enterDsoLocalEquivalentConst(LLVMIRParser.DsoLocalEquivalentConstContext ctx) {
    }

    @Override
    public void exitDsoLocalEquivalentConst(LLVMIRParser.DsoLocalEquivalentConstContext ctx) {
    }

    @Override
    public void enterNoCFIConst(LLVMIRParser.NoCFIConstContext ctx) {
    }

    @Override
    public void exitNoCFIConst(LLVMIRParser.NoCFIConstContext ctx) {
    }

    @Override
    public void enterConstantExpr(LLVMIRParser.ConstantExprContext ctx) {
    }

    @Override
    public void exitConstantExpr(LLVMIRParser.ConstantExprContext ctx) {
    }

    @Override
    public void enterTypeConst(LLVMIRParser.TypeConstContext ctx) {
    }

    @Override
    public void exitTypeConst(LLVMIRParser.TypeConstContext ctx) {
    }

    @Override
    public void enterMetadataAttachment(LLVMIRParser.MetadataAttachmentContext ctx) {
    }

    @Override
    public void exitMetadataAttachment(LLVMIRParser.MetadataAttachmentContext ctx) {
    }

    @Override
    public void enterMdNode(LLVMIRParser.MdNodeContext ctx) {
    }

    @Override
    public void exitMdNode(LLVMIRParser.MdNodeContext ctx) {
    }

    @Override
    public void enterMdTuple(LLVMIRParser.MdTupleContext ctx) {
    }

    @Override
    public void exitMdTuple(LLVMIRParser.MdTupleContext ctx) {
    }

    @Override
    public void enterMetadata(LLVMIRParser.MetadataContext ctx) {
    }

    @Override
    public void exitMetadata(LLVMIRParser.MetadataContext ctx) {
    }

    @Override
    public void enterDiArgList(LLVMIRParser.DiArgListContext ctx) {
    }

    @Override
    public void exitDiArgList(LLVMIRParser.DiArgListContext ctx) {
    }

    @Override
    public void enterTypeValue(LLVMIRParser.TypeValueContext ctx) {
    }

    @Override
    public void exitTypeValue(LLVMIRParser.TypeValueContext ctx) {
    }

    @Override
    public void enterValue(LLVMIRParser.ValueContext ctx) {
    }

    @Override
    public void exitValue(LLVMIRParser.ValueContext ctx) {
    }

    @Override
    public void enterInlineAsm(LLVMIRParser.InlineAsmContext ctx) {
    }

    @Override
    public void exitInlineAsm(LLVMIRParser.InlineAsmContext ctx) {
    }

    @Override
    public void enterMdString(LLVMIRParser.MdStringContext ctx) {
    }

    @Override
    public void exitMdString(LLVMIRParser.MdStringContext ctx) {
    }

    @Override
    public void enterMdFieldOrInt(LLVMIRParser.MdFieldOrIntContext ctx) {
    }

    @Override
    public void exitMdFieldOrInt(LLVMIRParser.MdFieldOrIntContext ctx) {
    }

    @Override
    public void enterDiSPFlag(LLVMIRParser.DiSPFlagContext ctx) {
    }

    @Override
    public void exitDiSPFlag(LLVMIRParser.DiSPFlagContext ctx) {
    }

    @Override
    public void enterFuncAttribute(LLVMIRParser.FuncAttributeContext ctx) {
    }

    @Override
    public void exitFuncAttribute(LLVMIRParser.FuncAttributeContext ctx) {
    }

    @Override
    public void enterType(LLVMIRParser.TypeContext ctx) {
    }

    @Override
    public void exitType(LLVMIRParser.TypeContext ctx) {
    }

    @Override
    public void enterParams(LLVMIRParser.ParamsContext ctx) {
    }

    @Override
    public void exitParams(LLVMIRParser.ParamsContext ctx) {
    }

    @Override
    public void enterParam(LLVMIRParser.ParamContext ctx) {
    }

    @Override
    public void exitParam(LLVMIRParser.ParamContext ctx) {
    }

    @Override
    public void enterParamAttribute(LLVMIRParser.ParamAttributeContext ctx) {
    }

    @Override
    public void exitParamAttribute(LLVMIRParser.ParamAttributeContext ctx) {
    }

    @Override
    public void enterAttrString(LLVMIRParser.AttrStringContext ctx) {
    }

    @Override
    public void exitAttrString(LLVMIRParser.AttrStringContext ctx) {
    }

    @Override
    public void enterAttrPair(LLVMIRParser.AttrPairContext ctx) {
    }

    @Override
    public void exitAttrPair(LLVMIRParser.AttrPairContext ctx) {
    }

    @Override
    public void enterAlign(LLVMIRParser.AlignContext ctx) {
    }

    @Override
    public void exitAlign(LLVMIRParser.AlignContext ctx) {
    }

    @Override
    public void enterAlignPair(LLVMIRParser.AlignPairContext ctx) {
    }

    @Override
    public void exitAlignPair(LLVMIRParser.AlignPairContext ctx) {
    }

    @Override
    public void enterAlignStack(LLVMIRParser.AlignStackContext ctx) {
    }

    @Override
    public void exitAlignStack(LLVMIRParser.AlignStackContext ctx) {
    }

    @Override
    public void enterAlignStackPair(LLVMIRParser.AlignStackPairContext ctx) {
    }

    @Override
    public void exitAlignStackPair(LLVMIRParser.AlignStackPairContext ctx) {
    }

    @Override
    public void enterAllocKind(LLVMIRParser.AllocKindContext ctx) {
    }

    @Override
    public void exitAllocKind(LLVMIRParser.AllocKindContext ctx) {
    }

    @Override
    public void enterAllocSize(LLVMIRParser.AllocSizeContext ctx) {
    }

    @Override
    public void exitAllocSize(LLVMIRParser.AllocSizeContext ctx) {
    }

    @Override
    public void enterUnwindTable(LLVMIRParser.UnwindTableContext ctx) {
    }

    @Override
    public void exitUnwindTable(LLVMIRParser.UnwindTableContext ctx) {
    }

    @Override
    public void enterVectorScaleRange(LLVMIRParser.VectorScaleRangeContext ctx) {
    }

    @Override
    public void exitVectorScaleRange(LLVMIRParser.VectorScaleRangeContext ctx) {
    }

    @Override
    public void enterByRefAttr(LLVMIRParser.ByRefAttrContext ctx) {
    }

    @Override
    public void exitByRefAttr(LLVMIRParser.ByRefAttrContext ctx) {
    }

    @Override
    public void enterByval(LLVMIRParser.ByvalContext ctx) {
    }

    @Override
    public void exitByval(LLVMIRParser.ByvalContext ctx) {
    }

    @Override
    public void enterDereferenceable(LLVMIRParser.DereferenceableContext ctx) {
    }

    @Override
    public void exitDereferenceable(LLVMIRParser.DereferenceableContext ctx) {
    }

    @Override
    public void enterElementType(LLVMIRParser.ElementTypeContext ctx) {
    }

    @Override
    public void exitElementType(LLVMIRParser.ElementTypeContext ctx) {
    }

    @Override
    public void enterInAlloca(LLVMIRParser.InAllocaContext ctx) {
    }

    @Override
    public void exitInAlloca(LLVMIRParser.InAllocaContext ctx) {
    }

    @Override
    public void enterParamAttr(LLVMIRParser.ParamAttrContext ctx) {
    }

    @Override
    public void exitParamAttr(LLVMIRParser.ParamAttrContext ctx) {
    }

    @Override
    public void enterPreallocated(LLVMIRParser.PreallocatedContext ctx) {
    }

    @Override
    public void exitPreallocated(LLVMIRParser.PreallocatedContext ctx) {
    }

    @Override
    public void enterStructRetAttr(LLVMIRParser.StructRetAttrContext ctx) {
    }

    @Override
    public void exitStructRetAttr(LLVMIRParser.StructRetAttrContext ctx) {
    }

    @Override
    public void enterFirstClassType(LLVMIRParser.FirstClassTypeContext ctx) {
    }

    @Override
    public void exitFirstClassType(LLVMIRParser.FirstClassTypeContext ctx) {
    }

    @Override
    public void enterConcreteType(LLVMIRParser.ConcreteTypeContext ctx) {
    }

    @Override
    public void exitConcreteType(LLVMIRParser.ConcreteTypeContext ctx) {
    }

    @Override
    public void enterIntType(LLVMIRParser.IntTypeContext ctx) {
    }

    @Override
    public void exitIntType(LLVMIRParser.IntTypeContext ctx) {
    }

    @Override
    public void enterFloatType(LLVMIRParser.FloatTypeContext ctx) {
    }

    @Override
    public void exitFloatType(LLVMIRParser.FloatTypeContext ctx) {
    }

    @Override
    public void enterPointerType(LLVMIRParser.PointerTypeContext ctx) {
    }

    @Override
    public void exitPointerType(LLVMIRParser.PointerTypeContext ctx) {
    }

    @Override
    public void enterVectorType(LLVMIRParser.VectorTypeContext ctx) {
    }

    @Override
    public void exitVectorType(LLVMIRParser.VectorTypeContext ctx) {
    }

    @Override
    public void enterLabelType(LLVMIRParser.LabelTypeContext ctx) {
    }

    @Override
    public void exitLabelType(LLVMIRParser.LabelTypeContext ctx) {
    }

    @Override
    public void enterArrayType(LLVMIRParser.ArrayTypeContext ctx) {
    }

    @Override
    public void exitArrayType(LLVMIRParser.ArrayTypeContext ctx) {
    }

    @Override
    public void enterStructType(LLVMIRParser.StructTypeContext ctx) {
    }

    @Override
    public void exitStructType(LLVMIRParser.StructTypeContext ctx) {
    }

    @Override
    public void enterNamedType(LLVMIRParser.NamedTypeContext ctx) {
    }

    @Override
    public void exitNamedType(LLVMIRParser.NamedTypeContext ctx) {
    }

    @Override
    public void enterMmxType(LLVMIRParser.MmxTypeContext ctx) {
    }

    @Override
    public void exitMmxType(LLVMIRParser.MmxTypeContext ctx) {
    }

    @Override
    public void enterTokenType(LLVMIRParser.TokenTypeContext ctx) {
    }

    @Override
    public void exitTokenType(LLVMIRParser.TokenTypeContext ctx) {
    }

    @Override
    public void enterOpaquePointerType(LLVMIRParser.OpaquePointerTypeContext ctx) {
    }

    @Override
    public void exitOpaquePointerType(LLVMIRParser.OpaquePointerTypeContext ctx) {
    }

    @Override
    public void enterAddrSpace(LLVMIRParser.AddrSpaceContext ctx) {
    }

    @Override
    public void exitAddrSpace(LLVMIRParser.AddrSpaceContext ctx) {
    }

    @Override
    public void enterThreadLocal(LLVMIRParser.ThreadLocalContext ctx) {
    }

    @Override
    public void exitThreadLocal(LLVMIRParser.ThreadLocalContext ctx) {
    }

    @Override
    public void enterMetadataType(LLVMIRParser.MetadataTypeContext ctx) {
    }

    @Override
    public void exitMetadataType(LLVMIRParser.MetadataTypeContext ctx) {
    }

    @Override
    public void enterBitCastExpr(LLVMIRParser.BitCastExprContext ctx) {
    }

    @Override
    public void exitBitCastExpr(LLVMIRParser.BitCastExprContext ctx) {
    }

    @Override
    public void enterGetElementPtrExpr(LLVMIRParser.GetElementPtrExprContext ctx) {
    }

    @Override
    public void exitGetElementPtrExpr(LLVMIRParser.GetElementPtrExprContext ctx) {
    }

    @Override
    public void enterGepIndex(LLVMIRParser.GepIndexContext ctx) {
    }

    @Override
    public void exitGepIndex(LLVMIRParser.GepIndexContext ctx) {
    }

    @Override
    public void enterAddrSpaceCastExpr(LLVMIRParser.AddrSpaceCastExprContext ctx) {
    }

    @Override
    public void exitAddrSpaceCastExpr(LLVMIRParser.AddrSpaceCastExprContext ctx) {
    }

    @Override
    public void enterIntToPtrExpr(LLVMIRParser.IntToPtrExprContext ctx) {
    }

    @Override
    public void exitIntToPtrExpr(LLVMIRParser.IntToPtrExprContext ctx) {
    }

    @Override
    public void enterICmpExpr(LLVMIRParser.ICmpExprContext ctx) {
    }

    @Override
    public void exitICmpExpr(LLVMIRParser.ICmpExprContext ctx) {
    }

    @Override
    public void enterFCmpExpr(LLVMIRParser.FCmpExprContext ctx) {
    }

    @Override
    public void exitFCmpExpr(LLVMIRParser.FCmpExprContext ctx) {
    }

    @Override
    public void enterSelectExpr(LLVMIRParser.SelectExprContext ctx) {
    }

    @Override
    public void exitSelectExpr(LLVMIRParser.SelectExprContext ctx) {
    }

    @Override
    public void enterTruncExpr(LLVMIRParser.TruncExprContext ctx) {
    }

    @Override
    public void exitTruncExpr(LLVMIRParser.TruncExprContext ctx) {
    }

    @Override
    public void enterZExtExpr(LLVMIRParser.ZExtExprContext ctx) {
    }

    @Override
    public void exitZExtExpr(LLVMIRParser.ZExtExprContext ctx) {
    }

    @Override
    public void enterSExtExpr(LLVMIRParser.SExtExprContext ctx) {
    }

    @Override
    public void exitSExtExpr(LLVMIRParser.SExtExprContext ctx) {
    }

    @Override
    public void enterFpTruncExpr(LLVMIRParser.FpTruncExprContext ctx) {
    }

    @Override
    public void exitFpTruncExpr(LLVMIRParser.FpTruncExprContext ctx) {
    }

    @Override
    public void enterFpExtExpr(LLVMIRParser.FpExtExprContext ctx) {
    }

    @Override
    public void exitFpExtExpr(LLVMIRParser.FpExtExprContext ctx) {
    }

    @Override
    public void enterFpToUiExpr(LLVMIRParser.FpToUiExprContext ctx) {
    }

    @Override
    public void exitFpToUiExpr(LLVMIRParser.FpToUiExprContext ctx) {
    }

    @Override
    public void enterFpToSiExpr(LLVMIRParser.FpToSiExprContext ctx) {
    }

    @Override
    public void exitFpToSiExpr(LLVMIRParser.FpToSiExprContext ctx) {
    }

    @Override
    public void enterUiToFpExpr(LLVMIRParser.UiToFpExprContext ctx) {
    }

    @Override
    public void exitUiToFpExpr(LLVMIRParser.UiToFpExprContext ctx) {
    }

    @Override
    public void enterSiToFpExpr(LLVMIRParser.SiToFpExprContext ctx) {
    }

    @Override
    public void exitSiToFpExpr(LLVMIRParser.SiToFpExprContext ctx) {
    }

    @Override
    public void enterPtrToIntExpr(LLVMIRParser.PtrToIntExprContext ctx) {
    }

    @Override
    public void exitPtrToIntExpr(LLVMIRParser.PtrToIntExprContext ctx) {
    }

    @Override
    public void enterExtractElementExpr(LLVMIRParser.ExtractElementExprContext ctx) {
    }

    @Override
    public void exitExtractElementExpr(LLVMIRParser.ExtractElementExprContext ctx) {
    }

    @Override
    public void enterInsertElementExpr(LLVMIRParser.InsertElementExprContext ctx) {
    }

    @Override
    public void exitInsertElementExpr(LLVMIRParser.InsertElementExprContext ctx) {
    }

    @Override
    public void enterShuffleVectorExpr(LLVMIRParser.ShuffleVectorExprContext ctx) {
    }

    @Override
    public void exitShuffleVectorExpr(LLVMIRParser.ShuffleVectorExprContext ctx) {
    }

    @Override
    public void enterShlExpr(LLVMIRParser.ShlExprContext ctx) {
    }

    @Override
    public void exitShlExpr(LLVMIRParser.ShlExprContext ctx) {
    }

    @Override
    public void enterLShrExpr(LLVMIRParser.LShrExprContext ctx) {
    }

    @Override
    public void exitLShrExpr(LLVMIRParser.LShrExprContext ctx) {
    }

    @Override
    public void enterAShrExpr(LLVMIRParser.AShrExprContext ctx) {
    }

    @Override
    public void exitAShrExpr(LLVMIRParser.AShrExprContext ctx) {
    }

    @Override
    public void enterAndExpr(LLVMIRParser.AndExprContext ctx) {
    }

    @Override
    public void exitAndExpr(LLVMIRParser.AndExprContext ctx) {
    }

    @Override
    public void enterOrExpr(LLVMIRParser.OrExprContext ctx) {
    }

    @Override
    public void exitOrExpr(LLVMIRParser.OrExprContext ctx) {
    }

    @Override
    public void enterXorExpr(LLVMIRParser.XorExprContext ctx) {
    }

    @Override
    public void exitXorExpr(LLVMIRParser.XorExprContext ctx) {
    }

    @Override
    public void enterAddExpr(LLVMIRParser.AddExprContext ctx) {
    }

    @Override
    public void exitAddExpr(LLVMIRParser.AddExprContext ctx) {
    }

    @Override
    public void enterSubExpr(LLVMIRParser.SubExprContext ctx) {
    }

    @Override
    public void exitSubExpr(LLVMIRParser.SubExprContext ctx) {
    }

    @Override
    public void enterMulExpr(LLVMIRParser.MulExprContext ctx) {
    }

    @Override
    public void exitMulExpr(LLVMIRParser.MulExprContext ctx) {
    }

    @Override
    public void enterFNegExpr(LLVMIRParser.FNegExprContext ctx) {
    }

    @Override
    public void exitFNegExpr(LLVMIRParser.FNegExprContext ctx) {
    }

    @Override
    public void enterLocalDefInst(LLVMIRParser.LocalDefInstContext ctx) {
    }

    @Override
    public void exitLocalDefInst(LLVMIRParser.LocalDefInstContext ctx) {
    }

    @Override
    public void enterValueInstruction(LLVMIRParser.ValueInstructionContext ctx) {
    }

    @Override
    public void exitValueInstruction(LLVMIRParser.ValueInstructionContext ctx) {
    }

    @Override
    public void enterStoreInst(LLVMIRParser.StoreInstContext ctx) {
        transformToken(LLVMIRTokenType.STORE, ctx.getStart(), ctx.getStop());
        super.enterStoreInst(ctx);
    }

    @Override
    public void exitStoreInst(LLVMIRParser.StoreInstContext ctx) {
    }

    @Override
    public void enterSyncScope(LLVMIRParser.SyncScopeContext ctx) {
    }

    @Override
    public void exitSyncScope(LLVMIRParser.SyncScopeContext ctx) {
    }

    @Override
    public void enterFenceInst(LLVMIRParser.FenceInstContext ctx) {
        transformToken(LLVMIRTokenType.FENCE, ctx.getStart(), ctx.getStop());
        super.enterFenceInst(ctx);
    }

    @Override
    public void exitFenceInst(LLVMIRParser.FenceInstContext ctx) {
    }

    @Override
    public void enterFNegInst(LLVMIRParser.FNegInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFNegInst(ctx);
    }

    @Override
    public void exitFNegInst(LLVMIRParser.FNegInstContext ctx) {
    }

    @Override
    public void enterAddInst(LLVMIRParser.AddInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterAddInst(ctx);
    }

    @Override
    public void exitAddInst(LLVMIRParser.AddInstContext ctx) {
    }

    @Override
    public void enterFAddInst(LLVMIRParser.FAddInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFAddInst(ctx);
    }

    @Override
    public void exitFAddInst(LLVMIRParser.FAddInstContext ctx) {
    }

    @Override
    public void enterSubInst(LLVMIRParser.SubInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterSubInst(ctx);
    }

    @Override
    public void exitSubInst(LLVMIRParser.SubInstContext ctx) {
    }

    @Override
    public void enterFSubInst(LLVMIRParser.FSubInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFSubInst(ctx);
    }

    @Override
    public void exitFSubInst(LLVMIRParser.FSubInstContext ctx) {
    }

    @Override
    public void enterMulInst(LLVMIRParser.MulInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterMulInst(ctx);
    }

    @Override
    public void exitMulInst(LLVMIRParser.MulInstContext ctx) {
    }

    @Override
    public void enterFMulInst(LLVMIRParser.FMulInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFMulInst(ctx);
    }

    @Override
    public void exitFMulInst(LLVMIRParser.FMulInstContext ctx) {
    }

    @Override
    public void enterUDivInst(LLVMIRParser.UDivInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterUDivInst(ctx);
    }

    @Override
    public void exitUDivInst(LLVMIRParser.UDivInstContext ctx) {
    }

    @Override
    public void enterSDivInst(LLVMIRParser.SDivInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterSDivInst(ctx);
    }

    @Override
    public void exitSDivInst(LLVMIRParser.SDivInstContext ctx) {
    }

    @Override
    public void enterFDivInst(LLVMIRParser.FDivInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFDivInst(ctx);
    }

    @Override
    public void exitFDivInst(LLVMIRParser.FDivInstContext ctx) {
    }

    @Override
    public void enterURemInst(LLVMIRParser.URemInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterURemInst(ctx);
    }

    @Override
    public void exitURemInst(LLVMIRParser.URemInstContext ctx) {
    }

    @Override
    public void enterSRemInst(LLVMIRParser.SRemInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterSRemInst(ctx);
    }

    @Override
    public void exitSRemInst(LLVMIRParser.SRemInstContext ctx) {
    }

    @Override
    public void enterFRemInst(LLVMIRParser.FRemInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterFRemInst(ctx);
    }

    @Override
    public void exitFRemInst(LLVMIRParser.FRemInstContext ctx) {
    }

    @Override
    public void enterShlInst(LLVMIRParser.ShlInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterShlInst(ctx);
    }

    @Override
    public void exitShlInst(LLVMIRParser.ShlInstContext ctx) {
    }

    @Override
    public void enterLShrInst(LLVMIRParser.LShrInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterLShrInst(ctx);
    }

    @Override
    public void exitLShrInst(LLVMIRParser.LShrInstContext ctx) {
    }

    @Override
    public void enterAShrInst(LLVMIRParser.AShrInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterAShrInst(ctx);
    }

    @Override
    public void exitAShrInst(LLVMIRParser.AShrInstContext ctx) {
    }

    @Override
    public void enterAndInst(LLVMIRParser.AndInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterAndInst(ctx);
    }

    @Override
    public void exitAndInst(LLVMIRParser.AndInstContext ctx) {
    }

    @Override
    public void enterOrInst(LLVMIRParser.OrInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterOrInst(ctx);
    }

    @Override
    public void exitOrInst(LLVMIRParser.OrInstContext ctx) {
    }

    @Override
    public void enterXorInst(LLVMIRParser.XorInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterXorInst(ctx);
    }

    @Override
    public void exitXorInst(LLVMIRParser.XorInstContext ctx) {
    }

    @Override
    public void enterExtractElementInst(LLVMIRParser.ExtractElementInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterExtractElementInst(ctx);
    }

    @Override
    public void exitExtractElementInst(LLVMIRParser.ExtractElementInstContext ctx) {
    }

    @Override
    public void enterInsertElementInst(LLVMIRParser.InsertElementInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterInsertElementInst(ctx);
    }

    @Override
    public void exitInsertElementInst(LLVMIRParser.InsertElementInstContext ctx) {
    }

    @Override
    public void enterShuffleVectorInst(LLVMIRParser.ShuffleVectorInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterShuffleVectorInst(ctx);
    }

    @Override
    public void exitShuffleVectorInst(LLVMIRParser.ShuffleVectorInstContext ctx) {
    }

    @Override
    public void enterExtractValueInst(LLVMIRParser.ExtractValueInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterExtractValueInst(ctx);
    }

    @Override
    public void exitExtractValueInst(LLVMIRParser.ExtractValueInstContext ctx) {
    }

    @Override
    public void enterInsertValueInst(LLVMIRParser.InsertValueInstContext ctx) {
        transformToken(LLVMIRTokenType.OPERATION, ctx.getStart(), ctx.getStop());
        super.enterInsertValueInst(ctx);
    }

    @Override
    public void exitInsertValueInst(LLVMIRParser.InsertValueInstContext ctx) {
    }

    @Override
    public void enterAllocaInst(LLVMIRParser.AllocaInstContext ctx) {
        transformToken(LLVMIRTokenType.ALLOCATION, ctx.getStart(), ctx.getStop());
        super.enterAllocaInst(ctx);
    }

    @Override
    public void exitAllocaInst(LLVMIRParser.AllocaInstContext ctx) {
    }

    @Override
    public void enterLoadInst(LLVMIRParser.LoadInstContext ctx) {
        transformToken(LLVMIRTokenType.LOAD, ctx.getStart(), ctx.getStop());
        super.enterLoadInst(ctx);
    }

    @Override
    public void exitLoadInst(LLVMIRParser.LoadInstContext ctx) {
    }

    @Override
    public void enterCmpXchgInst(LLVMIRParser.CmpXchgInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARE_EXCHANGE, ctx.getStart(), ctx.getStop());
        super.enterCmpXchgInst(ctx);
    }

    @Override
    public void exitCmpXchgInst(LLVMIRParser.CmpXchgInstContext ctx) {
    }

    @Override
    public void enterAtomicRMWInst(LLVMIRParser.AtomicRMWInstContext ctx) {
        transformToken(LLVMIRTokenType.ATOMIC_CRMW, ctx.getStart(), ctx.getStop());
        super.enterAtomicRMWInst(ctx);
    }

    @Override
    public void exitAtomicRMWInst(LLVMIRParser.AtomicRMWInstContext ctx) {
    }

    @Override
    public void enterGetElementPtrInst(LLVMIRParser.GetElementPtrInstContext ctx) {
        transformToken(LLVMIRTokenType.GET_ELEMENT_POINTER, ctx.getStart(), ctx.getStop());
        super.enterGetElementPtrInst(ctx);
    }

    @Override
    public void exitGetElementPtrInst(LLVMIRParser.GetElementPtrInstContext ctx) {
    }

    @Override
    public void enterTruncInst(LLVMIRParser.TruncInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterTruncInst(ctx);
    }

    @Override
    public void exitTruncInst(LLVMIRParser.TruncInstContext ctx) {
    }

    @Override
    public void enterZExtInst(LLVMIRParser.ZExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterZExtInst(ctx);
    }

    @Override
    public void exitZExtInst(LLVMIRParser.ZExtInstContext ctx) {
    }

    @Override
    public void enterSExtInst(LLVMIRParser.SExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSExtInst(ctx);
    }

    @Override
    public void exitSExtInst(LLVMIRParser.SExtInstContext ctx) {
    }

    @Override
    public void enterFpTruncInst(LLVMIRParser.FpTruncInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpTruncInst(ctx);
    }

    @Override
    public void exitFpTruncInst(LLVMIRParser.FpTruncInstContext ctx) {
    }

    @Override
    public void enterFpExtInst(LLVMIRParser.FpExtInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpExtInst(ctx);
    }

    @Override
    public void exitFpExtInst(LLVMIRParser.FpExtInstContext ctx) {
    }

    @Override
    public void enterFpToUiInst(LLVMIRParser.FpToUiInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToUiInst(ctx);
    }

    @Override
    public void exitFpToUiInst(LLVMIRParser.FpToUiInstContext ctx) {
    }

    @Override
    public void enterFpToSiInst(LLVMIRParser.FpToSiInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterFpToSiInst(ctx);
    }

    @Override
    public void exitFpToSiInst(LLVMIRParser.FpToSiInstContext ctx) {
    }

    @Override
    public void enterUiToFpInst(LLVMIRParser.UiToFpInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterUiToFpInst(ctx);
    }

    @Override
    public void exitUiToFpInst(LLVMIRParser.UiToFpInstContext ctx) {
    }

    @Override
    public void enterSiToFpInst(LLVMIRParser.SiToFpInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterSiToFpInst(ctx);
    }

    @Override
    public void exitSiToFpInst(LLVMIRParser.SiToFpInstContext ctx) {
    }

    @Override
    public void enterPtrToIntInst(LLVMIRParser.PtrToIntInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterPtrToIntInst(ctx);
    }

    @Override
    public void exitPtrToIntInst(LLVMIRParser.PtrToIntInstContext ctx) {
    }

    @Override
    public void enterIntToPtrInst(LLVMIRParser.IntToPtrInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterIntToPtrInst(ctx);
    }

    @Override
    public void exitIntToPtrInst(LLVMIRParser.IntToPtrInstContext ctx) {
    }

    @Override
    public void enterBitCastInst(LLVMIRParser.BitCastInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterBitCastInst(ctx);
    }

    @Override
    public void exitBitCastInst(LLVMIRParser.BitCastInstContext ctx) {
    }

    @Override
    public void enterAddrSpaceCastInst(LLVMIRParser.AddrSpaceCastInstContext ctx) {
        transformToken(LLVMIRTokenType.CONVERSION, ctx.getStart(), ctx.getStop());
        super.enterAddrSpaceCastInst(ctx);
    }

    @Override
    public void exitAddrSpaceCastInst(LLVMIRParser.AddrSpaceCastInstContext ctx) {
    }

    @Override
    public void enterICmpInst(LLVMIRParser.ICmpInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterICmpInst(ctx);
    }

    @Override
    public void exitICmpInst(LLVMIRParser.ICmpInstContext ctx) {
    }

    @Override
    public void enterFCmpInst(LLVMIRParser.FCmpInstContext ctx) {
        transformToken(LLVMIRTokenType.COMPARISON, ctx.getStart(), ctx.getStop());
        super.enterFCmpInst(ctx);
    }

    @Override
    public void exitFCmpInst(LLVMIRParser.FCmpInstContext ctx) {
    }

    @Override
    public void enterPhiInst(LLVMIRParser.PhiInstContext ctx) {
        transformToken(LLVMIRTokenType.PHI, ctx.getStart(), ctx.getStop());
        super.enterPhiInst(ctx);
    }

    @Override
    public void exitPhiInst(LLVMIRParser.PhiInstContext ctx) {
    }

    @Override
    public void enterSelectInst(LLVMIRParser.SelectInstContext ctx) {
        transformToken(LLVMIRTokenType.SELECT, ctx.getStart(), ctx.getStop());
        super.enterSelectInst(ctx);
    }

    @Override
    public void exitSelectInst(LLVMIRParser.SelectInstContext ctx) {
    }

    @Override
    public void enterFreezeInst(LLVMIRParser.FreezeInstContext ctx) {
        transformToken(LLVMIRTokenType.FREEZE, ctx.getStart(), ctx.getStop());
        super.exitFreezeInst(ctx);
    }

    @Override
    public void exitFreezeInst(LLVMIRParser.FreezeInstContext ctx) {
    }

    @Override
    public void enterCallInst(LLVMIRParser.CallInstContext ctx) {
        transformToken(LLVMIRTokenType.CALL, ctx.getStart(), ctx.getStop());
        super.enterCallInst(ctx);
    }

    @Override
    public void exitCallInst(LLVMIRParser.CallInstContext ctx) {
    }

    @Override
    public void enterVaargInst(LLVMIRParser.VaargInstContext ctx) {
        transformToken(LLVMIRTokenType.VA_ARG, ctx.getStart(), ctx.getStop());
        super.enterVaargInst(ctx);
    }

    @Override
    public void exitVaargInst(LLVMIRParser.VaargInstContext ctx) {
    }

    @Override
    public void enterLandingPadInst(LLVMIRParser.LandingPadInstContext ctx) {
        transformToken(LLVMIRTokenType.LANDING_PAD, ctx.getStart(), ctx.getStop());
        super.enterLandingPadInst(ctx);
    }

    @Override
    public void exitLandingPadInst(LLVMIRParser.LandingPadInstContext ctx) {
    }

    @Override
    public void enterCatchPadInst(LLVMIRParser.CatchPadInstContext ctx) {
        transformToken(LLVMIRTokenType.CATCH_PAD, ctx.getStart(), ctx.getStop());
        super.enterCatchPadInst(ctx);
    }

    @Override
    public void exitCatchPadInst(LLVMIRParser.CatchPadInstContext ctx) {
    }

    @Override
    public void enterCleanupPadInst(LLVMIRParser.CleanupPadInstContext ctx) {
        transformToken(LLVMIRTokenType.CLEAN_UP_PAD, ctx.getStart(), ctx.getStop());
        super.enterCleanupPadInst(ctx);
    }

    @Override
    public void exitCleanupPadInst(LLVMIRParser.CleanupPadInstContext ctx) {
    }

    @Override
    public void enterInc(LLVMIRParser.IncContext ctx) {
    }

    @Override
    public void exitInc(LLVMIRParser.IncContext ctx) {
    }

    @Override
    public void enterOperandBundle(LLVMIRParser.OperandBundleContext ctx) {
    }

    @Override
    public void exitOperandBundle(LLVMIRParser.OperandBundleContext ctx) {
    }

    @Override
    public void enterClause(LLVMIRParser.ClauseContext ctx) {
    }

    @Override
    public void exitClause(LLVMIRParser.ClauseContext ctx) {
    }

    @Override
    public void enterArgs(LLVMIRParser.ArgsContext ctx) {
    }

    @Override
    public void exitArgs(LLVMIRParser.ArgsContext ctx) {
    }

    @Override
    public void enterArg(LLVMIRParser.ArgContext ctx) {
    }

    @Override
    public void exitArg(LLVMIRParser.ArgContext ctx) {
    }

    @Override
    public void enterExceptionArg(LLVMIRParser.ExceptionArgContext ctx) {
    }

    @Override
    public void exitExceptionArg(LLVMIRParser.ExceptionArgContext ctx) {
    }

    @Override
    public void enterExceptionPad(LLVMIRParser.ExceptionPadContext ctx) {
    }

    @Override
    public void exitExceptionPad(LLVMIRParser.ExceptionPadContext ctx) {
    }

    @Override
    public void enterExternalLinkage(LLVMIRParser.ExternalLinkageContext ctx) {
    }

    @Override
    public void exitExternalLinkage(LLVMIRParser.ExternalLinkageContext ctx) {
    }

    @Override
    public void enterInternalLinkage(LLVMIRParser.InternalLinkageContext ctx) {
    }

    @Override
    public void exitInternalLinkage(LLVMIRParser.InternalLinkageContext ctx) {
    }

    @Override
    public void enterLinkage(LLVMIRParser.LinkageContext ctx) {
    }

    @Override
    public void exitLinkage(LLVMIRParser.LinkageContext ctx) {
    }

    @Override
    public void enterPreemption(LLVMIRParser.PreemptionContext ctx) {
    }

    @Override
    public void exitPreemption(LLVMIRParser.PreemptionContext ctx) {
    }

    @Override
    public void enterVisibility(LLVMIRParser.VisibilityContext ctx) {
    }

    @Override
    public void exitVisibility(LLVMIRParser.VisibilityContext ctx) {
    }

    @Override
    public void enterDllStorageClass(LLVMIRParser.DllStorageClassContext ctx) {
    }

    @Override
    public void exitDllStorageClass(LLVMIRParser.DllStorageClassContext ctx) {
    }

    @Override
    public void enterTlsModel(LLVMIRParser.TlsModelContext ctx) {
    }

    @Override
    public void exitTlsModel(LLVMIRParser.TlsModelContext ctx) {
    }

    @Override
    public void enterUnnamedAddr(LLVMIRParser.UnnamedAddrContext ctx) {
    }

    @Override
    public void exitUnnamedAddr(LLVMIRParser.UnnamedAddrContext ctx) {
    }

    @Override
    public void enterExternallyInitialized(LLVMIRParser.ExternallyInitializedContext ctx) {
    }

    @Override
    public void exitExternallyInitialized(LLVMIRParser.ExternallyInitializedContext ctx) {
    }

    @Override
    public void enterImmutable(LLVMIRParser.ImmutableContext ctx) {
    }

    @Override
    public void exitImmutable(LLVMIRParser.ImmutableContext ctx) {
    }

    @Override
    public void enterFuncAttr(LLVMIRParser.FuncAttrContext ctx) {
    }

    @Override
    public void exitFuncAttr(LLVMIRParser.FuncAttrContext ctx) {
    }

    @Override
    public void enterDistinct(LLVMIRParser.DistinctContext ctx) {
    }

    @Override
    public void exitDistinct(LLVMIRParser.DistinctContext ctx) {
    }

    @Override
    public void enterInBounds(LLVMIRParser.InBoundsContext ctx) {
    }

    @Override
    public void exitInBounds(LLVMIRParser.InBoundsContext ctx) {
    }

    @Override
    public void enterReturnAttr(LLVMIRParser.ReturnAttrContext ctx) {
    }

    @Override
    public void exitReturnAttr(LLVMIRParser.ReturnAttrContext ctx) {
    }

    @Override
    public void enterOverflowFlag(LLVMIRParser.OverflowFlagContext ctx) {
    }

    @Override
    public void exitOverflowFlag(LLVMIRParser.OverflowFlagContext ctx) {
    }

    @Override
    public void enterIPred(LLVMIRParser.IPredContext ctx) {
    }

    @Override
    public void exitIPred(LLVMIRParser.IPredContext ctx) {
    }

    @Override
    public void enterFPred(LLVMIRParser.FPredContext ctx) {
    }

    @Override
    public void exitFPred(LLVMIRParser.FPredContext ctx) {
    }

    @Override
    public void enterAtomicOrdering(LLVMIRParser.AtomicOrderingContext ctx) {
    }

    @Override
    public void exitAtomicOrdering(LLVMIRParser.AtomicOrderingContext ctx) {
    }

    @Override
    public void enterCallingConvEnum(LLVMIRParser.CallingConvEnumContext ctx) {
    }

    @Override
    public void exitCallingConvEnum(LLVMIRParser.CallingConvEnumContext ctx) {
    }

    @Override
    public void enterFastMathFlag(LLVMIRParser.FastMathFlagContext ctx) {
    }

    @Override
    public void exitFastMathFlag(LLVMIRParser.FastMathFlagContext ctx) {
    }

    @Override
    public void enterAtomicOp(LLVMIRParser.AtomicOpContext ctx) {
    }

    @Override
    public void exitAtomicOp(LLVMIRParser.AtomicOpContext ctx) {
    }

    @Override
    public void enterFloatKind(LLVMIRParser.FloatKindContext ctx) {
    }

    @Override
    public void exitFloatKind(LLVMIRParser.FloatKindContext ctx) {
    }

    @Override
    public void enterSpecializedMDNode(LLVMIRParser.SpecializedMDNodeContext ctx) {
    }

    @Override
    public void exitSpecializedMDNode(LLVMIRParser.SpecializedMDNodeContext ctx) {
    }

    @Override
    public void enterDiBasicType(LLVMIRParser.DiBasicTypeContext ctx) {
    }

    @Override
    public void exitDiBasicType(LLVMIRParser.DiBasicTypeContext ctx) {
    }

    @Override
    public void enterDiCommonBlock(LLVMIRParser.DiCommonBlockContext ctx) {
    }

    @Override
    public void exitDiCommonBlock(LLVMIRParser.DiCommonBlockContext ctx) {
    }

    @Override
    public void enterDiCompileUnit(LLVMIRParser.DiCompileUnitContext ctx) {
    }

    @Override
    public void exitDiCompileUnit(LLVMIRParser.DiCompileUnitContext ctx) {
    }

    @Override
    public void enterDiCompositeType(LLVMIRParser.DiCompositeTypeContext ctx) {
    }

    @Override
    public void exitDiCompositeType(LLVMIRParser.DiCompositeTypeContext ctx) {
    }

    @Override
    public void enterDiCompositeTypeField(LLVMIRParser.DiCompositeTypeFieldContext ctx) {
    }

    @Override
    public void exitDiCompositeTypeField(LLVMIRParser.DiCompositeTypeFieldContext ctx) {
    }

    @Override
    public void enterDiDerivedType(LLVMIRParser.DiDerivedTypeContext ctx) {
    }

    @Override
    public void exitDiDerivedType(LLVMIRParser.DiDerivedTypeContext ctx) {
    }

    @Override
    public void enterDiDerivedTypeField(LLVMIRParser.DiDerivedTypeFieldContext ctx) {
    }

    @Override
    public void exitDiDerivedTypeField(LLVMIRParser.DiDerivedTypeFieldContext ctx) {
    }

    @Override
    public void enterDiEnumerator(LLVMIRParser.DiEnumeratorContext ctx) {
    }

    @Override
    public void exitDiEnumerator(LLVMIRParser.DiEnumeratorContext ctx) {
    }

    @Override
    public void enterDiEnumeratorField(LLVMIRParser.DiEnumeratorFieldContext ctx) {
    }

    @Override
    public void exitDiEnumeratorField(LLVMIRParser.DiEnumeratorFieldContext ctx) {
    }

    @Override
    public void enterDiFile(LLVMIRParser.DiFileContext ctx) {
    }

    @Override
    public void exitDiFile(LLVMIRParser.DiFileContext ctx) {
    }

    @Override
    public void enterDiFileField(LLVMIRParser.DiFileFieldContext ctx) {
    }

    @Override
    public void exitDiFileField(LLVMIRParser.DiFileFieldContext ctx) {
    }

    @Override
    public void enterDiGlobalVariable(LLVMIRParser.DiGlobalVariableContext ctx) {
    }

    @Override
    public void exitDiGlobalVariable(LLVMIRParser.DiGlobalVariableContext ctx) {
    }

    @Override
    public void enterDiGlobalVariableField(LLVMIRParser.DiGlobalVariableFieldContext ctx) {
    }

    @Override
    public void exitDiGlobalVariableField(LLVMIRParser.DiGlobalVariableFieldContext ctx) {
    }

    @Override
    public void enterDiGlobalVariableExpression(LLVMIRParser.DiGlobalVariableExpressionContext ctx) {
    }

    @Override
    public void exitDiGlobalVariableExpression(LLVMIRParser.DiGlobalVariableExpressionContext ctx) {
    }

    @Override
    public void enterDiGlobalVariableExpressionField(LLVMIRParser.DiGlobalVariableExpressionFieldContext ctx) {
    }

    @Override
    public void exitDiGlobalVariableExpressionField(LLVMIRParser.DiGlobalVariableExpressionFieldContext ctx) {
    }

    @Override
    public void enterDiImportedEntity(LLVMIRParser.DiImportedEntityContext ctx) {
    }

    @Override
    public void exitDiImportedEntity(LLVMIRParser.DiImportedEntityContext ctx) {
    }

    @Override
    public void enterDiImportedEntityField(LLVMIRParser.DiImportedEntityFieldContext ctx) {
    }

    @Override
    public void exitDiImportedEntityField(LLVMIRParser.DiImportedEntityFieldContext ctx) {
    }

    @Override
    public void enterDiLabel(LLVMIRParser.DiLabelContext ctx) {
    }

    @Override
    public void exitDiLabel(LLVMIRParser.DiLabelContext ctx) {
    }

    @Override
    public void enterDiLabelField(LLVMIRParser.DiLabelFieldContext ctx) {
    }

    @Override
    public void exitDiLabelField(LLVMIRParser.DiLabelFieldContext ctx) {
    }

    @Override
    public void enterDiLexicalBlock(LLVMIRParser.DiLexicalBlockContext ctx) {
    }

    @Override
    public void exitDiLexicalBlock(LLVMIRParser.DiLexicalBlockContext ctx) {
    }

    @Override
    public void enterDiLexicalBlockField(LLVMIRParser.DiLexicalBlockFieldContext ctx) {
    }

    @Override
    public void exitDiLexicalBlockField(LLVMIRParser.DiLexicalBlockFieldContext ctx) {
    }

    @Override
    public void enterDiLexicalBlockFile(LLVMIRParser.DiLexicalBlockFileContext ctx) {
    }

    @Override
    public void exitDiLexicalBlockFile(LLVMIRParser.DiLexicalBlockFileContext ctx) {
    }

    @Override
    public void enterDiLexicalBlockFileField(LLVMIRParser.DiLexicalBlockFileFieldContext ctx) {
    }

    @Override
    public void exitDiLexicalBlockFileField(LLVMIRParser.DiLexicalBlockFileFieldContext ctx) {
    }

    @Override
    public void enterDiLocalVariable(LLVMIRParser.DiLocalVariableContext ctx) {
    }

    @Override
    public void exitDiLocalVariable(LLVMIRParser.DiLocalVariableContext ctx) {
    }

    @Override
    public void enterDiLocalVariableField(LLVMIRParser.DiLocalVariableFieldContext ctx) {
    }

    @Override
    public void exitDiLocalVariableField(LLVMIRParser.DiLocalVariableFieldContext ctx) {
    }

    @Override
    public void enterDiLocation(LLVMIRParser.DiLocationContext ctx) {
    }

    @Override
    public void exitDiLocation(LLVMIRParser.DiLocationContext ctx) {
    }

    @Override
    public void enterDiLocationField(LLVMIRParser.DiLocationFieldContext ctx) {
    }

    @Override
    public void exitDiLocationField(LLVMIRParser.DiLocationFieldContext ctx) {
    }

    @Override
    public void enterDiMacro(LLVMIRParser.DiMacroContext ctx) {
    }

    @Override
    public void exitDiMacro(LLVMIRParser.DiMacroContext ctx) {
    }

    @Override
    public void enterDiMacroField(LLVMIRParser.DiMacroFieldContext ctx) {
    }

    @Override
    public void exitDiMacroField(LLVMIRParser.DiMacroFieldContext ctx) {
    }

    @Override
    public void enterDiMacroFile(LLVMIRParser.DiMacroFileContext ctx) {
    }

    @Override
    public void exitDiMacroFile(LLVMIRParser.DiMacroFileContext ctx) {
    }

    @Override
    public void enterDiMacroFileField(LLVMIRParser.DiMacroFileFieldContext ctx) {
    }

    @Override
    public void exitDiMacroFileField(LLVMIRParser.DiMacroFileFieldContext ctx) {
    }

    @Override
    public void enterDiModule(LLVMIRParser.DiModuleContext ctx) {
    }

    @Override
    public void exitDiModule(LLVMIRParser.DiModuleContext ctx) {
    }

    @Override
    public void enterDiModuleField(LLVMIRParser.DiModuleFieldContext ctx) {
    }

    @Override
    public void exitDiModuleField(LLVMIRParser.DiModuleFieldContext ctx) {
    }

    @Override
    public void enterDiNamespace(LLVMIRParser.DiNamespaceContext ctx) {
    }

    @Override
    public void exitDiNamespace(LLVMIRParser.DiNamespaceContext ctx) {
    }

    @Override
    public void enterDiNamespaceField(LLVMIRParser.DiNamespaceFieldContext ctx) {
    }

    @Override
    public void exitDiNamespaceField(LLVMIRParser.DiNamespaceFieldContext ctx) {
    }

    @Override
    public void enterDiObjCProperty(LLVMIRParser.DiObjCPropertyContext ctx) {
    }

    @Override
    public void exitDiObjCProperty(LLVMIRParser.DiObjCPropertyContext ctx) {
    }

    @Override
    public void enterDiObjCPropertyField(LLVMIRParser.DiObjCPropertyFieldContext ctx) {
    }

    @Override
    public void exitDiObjCPropertyField(LLVMIRParser.DiObjCPropertyFieldContext ctx) {
    }

    @Override
    public void enterDiStringType(LLVMIRParser.DiStringTypeContext ctx) {
    }

    @Override
    public void exitDiStringType(LLVMIRParser.DiStringTypeContext ctx) {
    }

    @Override
    public void enterDiStringTypeField(LLVMIRParser.DiStringTypeFieldContext ctx) {
    }

    @Override
    public void exitDiStringTypeField(LLVMIRParser.DiStringTypeFieldContext ctx) {
    }

    @Override
    public void enterDiSubprogram(LLVMIRParser.DiSubprogramContext ctx) {
    }

    @Override
    public void exitDiSubprogram(LLVMIRParser.DiSubprogramContext ctx) {
    }

    @Override
    public void enterDiSubprogramField(LLVMIRParser.DiSubprogramFieldContext ctx) {
    }

    @Override
    public void exitDiSubprogramField(LLVMIRParser.DiSubprogramFieldContext ctx) {
    }

    @Override
    public void enterDiSubrange(LLVMIRParser.DiSubrangeContext ctx) {
    }

    @Override
    public void exitDiSubrange(LLVMIRParser.DiSubrangeContext ctx) {
    }

    @Override
    public void enterDiSubrangeField(LLVMIRParser.DiSubrangeFieldContext ctx) {
    }

    @Override
    public void exitDiSubrangeField(LLVMIRParser.DiSubrangeFieldContext ctx) {
    }

    @Override
    public void enterDiSubroutineType(LLVMIRParser.DiSubroutineTypeContext ctx) {
    }

    @Override
    public void exitDiSubroutineType(LLVMIRParser.DiSubroutineTypeContext ctx) {
    }

    @Override
    public void enterDiTemplateTypeParameter(LLVMIRParser.DiTemplateTypeParameterContext ctx) {
    }

    @Override
    public void exitDiTemplateTypeParameter(LLVMIRParser.DiTemplateTypeParameterContext ctx) {
    }

    @Override
    public void enterDiTemplateValueParameter(LLVMIRParser.DiTemplateValueParameterContext ctx) {
    }

    @Override
    public void exitDiTemplateValueParameter(LLVMIRParser.DiTemplateValueParameterContext ctx) {
    }

    @Override
    public void enterGenericDiNode(LLVMIRParser.GenericDiNodeContext ctx) {
    }

    @Override
    public void exitGenericDiNode(LLVMIRParser.GenericDiNodeContext ctx) {
    }

    @Override
    public void enterDiTemplateTypeParameterField(LLVMIRParser.DiTemplateTypeParameterFieldContext ctx) {
    }

    @Override
    public void exitDiTemplateTypeParameterField(LLVMIRParser.DiTemplateTypeParameterFieldContext ctx) {
    }

    @Override
    public void enterDiCompileUnitField(LLVMIRParser.DiCompileUnitFieldContext ctx) {
    }

    @Override
    public void exitDiCompileUnitField(LLVMIRParser.DiCompileUnitFieldContext ctx) {
    }

    @Override
    public void enterDiCommonBlockField(LLVMIRParser.DiCommonBlockFieldContext ctx) {
    }

    @Override
    public void exitDiCommonBlockField(LLVMIRParser.DiCommonBlockFieldContext ctx) {
    }

    @Override
    public void enterDiBasicTypeField(LLVMIRParser.DiBasicTypeFieldContext ctx) {
    }

    @Override
    public void exitDiBasicTypeField(LLVMIRParser.DiBasicTypeFieldContext ctx) {
    }

    @Override
    public void enterGenericDINodeField(LLVMIRParser.GenericDINodeFieldContext ctx) {
    }

    @Override
    public void exitGenericDINodeField(LLVMIRParser.GenericDINodeFieldContext ctx) {
    }

    @Override
    public void enterTagField(LLVMIRParser.TagFieldContext ctx) {
    }

    @Override
    public void exitTagField(LLVMIRParser.TagFieldContext ctx) {
    }

    @Override
    public void enterHeaderField(LLVMIRParser.HeaderFieldContext ctx) {
    }

    @Override
    public void exitHeaderField(LLVMIRParser.HeaderFieldContext ctx) {
    }

    @Override
    public void enterOperandsField(LLVMIRParser.OperandsFieldContext ctx) {
    }

    @Override
    public void exitOperandsField(LLVMIRParser.OperandsFieldContext ctx) {
    }

    @Override
    public void enterDiTemplateValueParameterField(LLVMIRParser.DiTemplateValueParameterFieldContext ctx) {
    }

    @Override
    public void exitDiTemplateValueParameterField(LLVMIRParser.DiTemplateValueParameterFieldContext ctx) {
    }

    @Override
    public void enterNameField(LLVMIRParser.NameFieldContext ctx) {
    }

    @Override
    public void exitNameField(LLVMIRParser.NameFieldContext ctx) {
    }

    @Override
    public void enterTypeField(LLVMIRParser.TypeFieldContext ctx) {
    }

    @Override
    public void exitTypeField(LLVMIRParser.TypeFieldContext ctx) {
    }

    @Override
    public void enterDefaultedField(LLVMIRParser.DefaultedFieldContext ctx) {
    }

    @Override
    public void exitDefaultedField(LLVMIRParser.DefaultedFieldContext ctx) {
    }

    @Override
    public void enterValueField(LLVMIRParser.ValueFieldContext ctx) {
    }

    @Override
    public void exitValueField(LLVMIRParser.ValueFieldContext ctx) {
    }

    @Override
    public void enterMdField(LLVMIRParser.MdFieldContext ctx) {
    }

    @Override
    public void exitMdField(LLVMIRParser.MdFieldContext ctx) {
    }

    @Override
    public void enterDiSubroutineTypeField(LLVMIRParser.DiSubroutineTypeFieldContext ctx) {
    }

    @Override
    public void exitDiSubroutineTypeField(LLVMIRParser.DiSubroutineTypeFieldContext ctx) {
    }

    @Override
    public void enterFlagsField(LLVMIRParser.FlagsFieldContext ctx) {
    }

    @Override
    public void exitFlagsField(LLVMIRParser.FlagsFieldContext ctx) {
    }

    @Override
    public void enterDiFlags(LLVMIRParser.DiFlagsContext ctx) {
    }

    @Override
    public void exitDiFlags(LLVMIRParser.DiFlagsContext ctx) {
    }

    @Override
    public void enterCcField(LLVMIRParser.CcFieldContext ctx) {
    }

    @Override
    public void exitCcField(LLVMIRParser.CcFieldContext ctx) {
    }

    @Override
    public void enterAlignField(LLVMIRParser.AlignFieldContext ctx) {
    }

    @Override
    public void exitAlignField(LLVMIRParser.AlignFieldContext ctx) {
    }

    @Override
    public void enterAllocatedField(LLVMIRParser.AllocatedFieldContext ctx) {
    }

    @Override
    public void exitAllocatedField(LLVMIRParser.AllocatedFieldContext ctx) {
    }

    @Override
    public void enterAnnotationsField(LLVMIRParser.AnnotationsFieldContext ctx) {
    }

    @Override
    public void exitAnnotationsField(LLVMIRParser.AnnotationsFieldContext ctx) {
    }

    @Override
    public void enterArgField(LLVMIRParser.ArgFieldContext ctx) {
    }

    @Override
    public void exitArgField(LLVMIRParser.ArgFieldContext ctx) {
    }

    @Override
    public void enterAssociatedField(LLVMIRParser.AssociatedFieldContext ctx) {
    }

    @Override
    public void exitAssociatedField(LLVMIRParser.AssociatedFieldContext ctx) {
    }

    @Override
    public void enterAttributesField(LLVMIRParser.AttributesFieldContext ctx) {
    }

    @Override
    public void exitAttributesField(LLVMIRParser.AttributesFieldContext ctx) {
    }

    @Override
    public void enterBaseTypeField(LLVMIRParser.BaseTypeFieldContext ctx) {
    }

    @Override
    public void exitBaseTypeField(LLVMIRParser.BaseTypeFieldContext ctx) {
    }

    @Override
    public void enterChecksumField(LLVMIRParser.ChecksumFieldContext ctx) {
    }

    @Override
    public void exitChecksumField(LLVMIRParser.ChecksumFieldContext ctx) {
    }

    @Override
    public void enterChecksumkindField(LLVMIRParser.ChecksumkindFieldContext ctx) {
    }

    @Override
    public void exitChecksumkindField(LLVMIRParser.ChecksumkindFieldContext ctx) {
    }

    @Override
    public void enterColumnField(LLVMIRParser.ColumnFieldContext ctx) {
    }

    @Override
    public void exitColumnField(LLVMIRParser.ColumnFieldContext ctx) {
    }

    @Override
    public void enterConfigMacrosField(LLVMIRParser.ConfigMacrosFieldContext ctx) {
    }

    @Override
    public void exitConfigMacrosField(LLVMIRParser.ConfigMacrosFieldContext ctx) {
    }

    @Override
    public void enterContainingTypeField(LLVMIRParser.ContainingTypeFieldContext ctx) {
    }

    @Override
    public void exitContainingTypeField(LLVMIRParser.ContainingTypeFieldContext ctx) {
    }

    @Override
    public void enterCountField(LLVMIRParser.CountFieldContext ctx) {
    }

    @Override
    public void exitCountField(LLVMIRParser.CountFieldContext ctx) {
    }

    @Override
    public void enterDebugInfoForProfilingField(LLVMIRParser.DebugInfoForProfilingFieldContext ctx) {
    }

    @Override
    public void exitDebugInfoForProfilingField(LLVMIRParser.DebugInfoForProfilingFieldContext ctx) {
    }

    @Override
    public void enterDeclarationField(LLVMIRParser.DeclarationFieldContext ctx) {
    }

    @Override
    public void exitDeclarationField(LLVMIRParser.DeclarationFieldContext ctx) {
    }

    @Override
    public void enterDirectoryField(LLVMIRParser.DirectoryFieldContext ctx) {
    }

    @Override
    public void exitDirectoryField(LLVMIRParser.DirectoryFieldContext ctx) {
    }

    @Override
    public void enterDiscriminatorField(LLVMIRParser.DiscriminatorFieldContext ctx) {
    }

    @Override
    public void exitDiscriminatorField(LLVMIRParser.DiscriminatorFieldContext ctx) {
    }

    @Override
    public void enterDataLocationField(LLVMIRParser.DataLocationFieldContext ctx) {
    }

    @Override
    public void exitDataLocationField(LLVMIRParser.DataLocationFieldContext ctx) {
    }

    @Override
    public void enterDiscriminatorIntField(LLVMIRParser.DiscriminatorIntFieldContext ctx) {
    }

    @Override
    public void exitDiscriminatorIntField(LLVMIRParser.DiscriminatorIntFieldContext ctx) {
    }

    @Override
    public void enterDwarfAddressSpaceField(LLVMIRParser.DwarfAddressSpaceFieldContext ctx) {
    }

    @Override
    public void exitDwarfAddressSpaceField(LLVMIRParser.DwarfAddressSpaceFieldContext ctx) {
    }

    @Override
    public void enterDwoIdField(LLVMIRParser.DwoIdFieldContext ctx) {
    }

    @Override
    public void exitDwoIdField(LLVMIRParser.DwoIdFieldContext ctx) {
    }

    @Override
    public void enterElementsField(LLVMIRParser.ElementsFieldContext ctx) {
    }

    @Override
    public void exitElementsField(LLVMIRParser.ElementsFieldContext ctx) {
    }

    @Override
    public void enterEmissionKindField(LLVMIRParser.EmissionKindFieldContext ctx) {
    }

    @Override
    public void exitEmissionKindField(LLVMIRParser.EmissionKindFieldContext ctx) {
    }

    @Override
    public void enterEncodingField(LLVMIRParser.EncodingFieldContext ctx) {
    }

    @Override
    public void exitEncodingField(LLVMIRParser.EncodingFieldContext ctx) {
    }

    @Override
    public void enterEntityField(LLVMIRParser.EntityFieldContext ctx) {
    }

    @Override
    public void exitEntityField(LLVMIRParser.EntityFieldContext ctx) {
    }

    @Override
    public void enterEnumsField(LLVMIRParser.EnumsFieldContext ctx) {
    }

    @Override
    public void exitEnumsField(LLVMIRParser.EnumsFieldContext ctx) {
    }

    @Override
    public void enterExportSymbolsField(LLVMIRParser.ExportSymbolsFieldContext ctx) {
    }

    @Override
    public void exitExportSymbolsField(LLVMIRParser.ExportSymbolsFieldContext ctx) {
    }

    @Override
    public void enterExprField(LLVMIRParser.ExprFieldContext ctx) {
    }

    @Override
    public void exitExprField(LLVMIRParser.ExprFieldContext ctx) {
    }

    @Override
    public void enterExtraDataField(LLVMIRParser.ExtraDataFieldContext ctx) {
    }

    @Override
    public void exitExtraDataField(LLVMIRParser.ExtraDataFieldContext ctx) {
    }

    @Override
    public void enterFileField(LLVMIRParser.FileFieldContext ctx) {
    }

    @Override
    public void exitFileField(LLVMIRParser.FileFieldContext ctx) {
    }

    @Override
    public void enterFilenameField(LLVMIRParser.FilenameFieldContext ctx) {
    }

    @Override
    public void exitFilenameField(LLVMIRParser.FilenameFieldContext ctx) {
    }

    @Override
    public void enterFlagsStringField(LLVMIRParser.FlagsStringFieldContext ctx) {
    }

    @Override
    public void exitFlagsStringField(LLVMIRParser.FlagsStringFieldContext ctx) {
    }

    @Override
    public void enterGetterField(LLVMIRParser.GetterFieldContext ctx) {
    }

    @Override
    public void exitGetterField(LLVMIRParser.GetterFieldContext ctx) {
    }

    @Override
    public void enterGlobalsField(LLVMIRParser.GlobalsFieldContext ctx) {
    }

    @Override
    public void exitGlobalsField(LLVMIRParser.GlobalsFieldContext ctx) {
    }

    @Override
    public void enterIdentifierField(LLVMIRParser.IdentifierFieldContext ctx) {
    }

    @Override
    public void exitIdentifierField(LLVMIRParser.IdentifierFieldContext ctx) {
    }

    @Override
    public void enterImportsField(LLVMIRParser.ImportsFieldContext ctx) {
    }

    @Override
    public void exitImportsField(LLVMIRParser.ImportsFieldContext ctx) {
    }

    @Override
    public void enterIncludePathField(LLVMIRParser.IncludePathFieldContext ctx) {
    }

    @Override
    public void exitIncludePathField(LLVMIRParser.IncludePathFieldContext ctx) {
    }

    @Override
    public void enterInlinedAtField(LLVMIRParser.InlinedAtFieldContext ctx) {
    }

    @Override
    public void exitInlinedAtField(LLVMIRParser.InlinedAtFieldContext ctx) {
    }

    @Override
    public void enterIsDeclField(LLVMIRParser.IsDeclFieldContext ctx) {
    }

    @Override
    public void exitIsDeclField(LLVMIRParser.IsDeclFieldContext ctx) {
    }

    @Override
    public void enterIsDefinitionField(LLVMIRParser.IsDefinitionFieldContext ctx) {
    }

    @Override
    public void exitIsDefinitionField(LLVMIRParser.IsDefinitionFieldContext ctx) {
    }

    @Override
    public void enterIsImplicitCodeField(LLVMIRParser.IsImplicitCodeFieldContext ctx) {
    }

    @Override
    public void exitIsImplicitCodeField(LLVMIRParser.IsImplicitCodeFieldContext ctx) {
    }

    @Override
    public void enterIsLocalField(LLVMIRParser.IsLocalFieldContext ctx) {
    }

    @Override
    public void exitIsLocalField(LLVMIRParser.IsLocalFieldContext ctx) {
    }

    @Override
    public void enterIsOptimizedField(LLVMIRParser.IsOptimizedFieldContext ctx) {
    }

    @Override
    public void exitIsOptimizedField(LLVMIRParser.IsOptimizedFieldContext ctx) {
    }

    @Override
    public void enterIsUnsignedField(LLVMIRParser.IsUnsignedFieldContext ctx) {
    }

    @Override
    public void exitIsUnsignedField(LLVMIRParser.IsUnsignedFieldContext ctx) {
    }

    @Override
    public void enterApiNotesField(LLVMIRParser.ApiNotesFieldContext ctx) {
    }

    @Override
    public void exitApiNotesField(LLVMIRParser.ApiNotesFieldContext ctx) {
    }

    @Override
    public void enterLanguageField(LLVMIRParser.LanguageFieldContext ctx) {
    }

    @Override
    public void exitLanguageField(LLVMIRParser.LanguageFieldContext ctx) {
    }

    @Override
    public void enterLineField(LLVMIRParser.LineFieldContext ctx) {
    }

    @Override
    public void exitLineField(LLVMIRParser.LineFieldContext ctx) {
    }

    @Override
    public void enterLinkageNameField(LLVMIRParser.LinkageNameFieldContext ctx) {
    }

    @Override
    public void exitLinkageNameField(LLVMIRParser.LinkageNameFieldContext ctx) {
    }

    @Override
    public void enterLowerBoundField(LLVMIRParser.LowerBoundFieldContext ctx) {
    }

    @Override
    public void exitLowerBoundField(LLVMIRParser.LowerBoundFieldContext ctx) {
    }

    @Override
    public void enterMacrosField(LLVMIRParser.MacrosFieldContext ctx) {
    }

    @Override
    public void exitMacrosField(LLVMIRParser.MacrosFieldContext ctx) {
    }

    @Override
    public void enterNameTableKindField(LLVMIRParser.NameTableKindFieldContext ctx) {
    }

    @Override
    public void exitNameTableKindField(LLVMIRParser.NameTableKindFieldContext ctx) {
    }

    @Override
    public void enterNodesField(LLVMIRParser.NodesFieldContext ctx) {
    }

    @Override
    public void exitNodesField(LLVMIRParser.NodesFieldContext ctx) {
    }

    @Override
    public void enterOffsetField(LLVMIRParser.OffsetFieldContext ctx) {
    }

    @Override
    public void exitOffsetField(LLVMIRParser.OffsetFieldContext ctx) {
    }

    @Override
    public void enterProducerField(LLVMIRParser.ProducerFieldContext ctx) {
    }

    @Override
    public void exitProducerField(LLVMIRParser.ProducerFieldContext ctx) {
    }

    @Override
    public void enterRangesBaseAddressField(LLVMIRParser.RangesBaseAddressFieldContext ctx) {
    }

    @Override
    public void exitRangesBaseAddressField(LLVMIRParser.RangesBaseAddressFieldContext ctx) {
    }

    @Override
    public void enterRankField(LLVMIRParser.RankFieldContext ctx) {
    }

    @Override
    public void exitRankField(LLVMIRParser.RankFieldContext ctx) {
    }

    @Override
    public void enterRetainedNodesField(LLVMIRParser.RetainedNodesFieldContext ctx) {
    }

    @Override
    public void exitRetainedNodesField(LLVMIRParser.RetainedNodesFieldContext ctx) {
    }

    @Override
    public void enterRetainedTypesField(LLVMIRParser.RetainedTypesFieldContext ctx) {
    }

    @Override
    public void exitRetainedTypesField(LLVMIRParser.RetainedTypesFieldContext ctx) {
    }

    @Override
    public void enterRuntimeLangField(LLVMIRParser.RuntimeLangFieldContext ctx) {
    }

    @Override
    public void exitRuntimeLangField(LLVMIRParser.RuntimeLangFieldContext ctx) {
    }

    @Override
    public void enterRuntimeVersionField(LLVMIRParser.RuntimeVersionFieldContext ctx) {
    }

    @Override
    public void exitRuntimeVersionField(LLVMIRParser.RuntimeVersionFieldContext ctx) {
    }

    @Override
    public void enterScopeField(LLVMIRParser.ScopeFieldContext ctx) {
    }

    @Override
    public void exitScopeField(LLVMIRParser.ScopeFieldContext ctx) {
    }

    @Override
    public void enterScopeLineField(LLVMIRParser.ScopeLineFieldContext ctx) {
    }

    @Override
    public void exitScopeLineField(LLVMIRParser.ScopeLineFieldContext ctx) {
    }

    @Override
    public void enterSdkField(LLVMIRParser.SdkFieldContext ctx) {
    }

    @Override
    public void exitSdkField(LLVMIRParser.SdkFieldContext ctx) {
    }

    @Override
    public void enterSetterField(LLVMIRParser.SetterFieldContext ctx) {
    }

    @Override
    public void exitSetterField(LLVMIRParser.SetterFieldContext ctx) {
    }

    @Override
    public void enterSizeField(LLVMIRParser.SizeFieldContext ctx) {
    }

    @Override
    public void exitSizeField(LLVMIRParser.SizeFieldContext ctx) {
    }

    @Override
    public void enterSourceField(LLVMIRParser.SourceFieldContext ctx) {
    }

    @Override
    public void exitSourceField(LLVMIRParser.SourceFieldContext ctx) {
    }

    @Override
    public void enterSpFlagsField(LLVMIRParser.SpFlagsFieldContext ctx) {
    }

    @Override
    public void exitSpFlagsField(LLVMIRParser.SpFlagsFieldContext ctx) {
    }

    @Override
    public void enterSplitDebugFilenameField(LLVMIRParser.SplitDebugFilenameFieldContext ctx) {
    }

    @Override
    public void exitSplitDebugFilenameField(LLVMIRParser.SplitDebugFilenameFieldContext ctx) {
    }

    @Override
    public void enterSplitDebugInliningField(LLVMIRParser.SplitDebugInliningFieldContext ctx) {
    }

    @Override
    public void exitSplitDebugInliningField(LLVMIRParser.SplitDebugInliningFieldContext ctx) {
    }

    @Override
    public void enterStrideField(LLVMIRParser.StrideFieldContext ctx) {
    }

    @Override
    public void exitStrideField(LLVMIRParser.StrideFieldContext ctx) {
    }

    @Override
    public void enterStringLengthField(LLVMIRParser.StringLengthFieldContext ctx) {
    }

    @Override
    public void exitStringLengthField(LLVMIRParser.StringLengthFieldContext ctx) {
    }

    @Override
    public void enterStringLengthExpressionField(LLVMIRParser.StringLengthExpressionFieldContext ctx) {
    }

    @Override
    public void exitStringLengthExpressionField(LLVMIRParser.StringLengthExpressionFieldContext ctx) {
    }

    @Override
    public void enterStringLocationExpressionField(LLVMIRParser.StringLocationExpressionFieldContext ctx) {
    }

    @Override
    public void exitStringLocationExpressionField(LLVMIRParser.StringLocationExpressionFieldContext ctx) {
    }

    @Override
    public void enterSysrootField(LLVMIRParser.SysrootFieldContext ctx) {
    }

    @Override
    public void exitSysrootField(LLVMIRParser.SysrootFieldContext ctx) {
    }

    @Override
    public void enterTargetFuncNameField(LLVMIRParser.TargetFuncNameFieldContext ctx) {
    }

    @Override
    public void exitTargetFuncNameField(LLVMIRParser.TargetFuncNameFieldContext ctx) {
    }

    @Override
    public void enterTemplateParamsField(LLVMIRParser.TemplateParamsFieldContext ctx) {
    }

    @Override
    public void exitTemplateParamsField(LLVMIRParser.TemplateParamsFieldContext ctx) {
    }

    @Override
    public void enterThisAdjustmentField(LLVMIRParser.ThisAdjustmentFieldContext ctx) {
    }

    @Override
    public void exitThisAdjustmentField(LLVMIRParser.ThisAdjustmentFieldContext ctx) {
    }

    @Override
    public void enterThrownTypesField(LLVMIRParser.ThrownTypesFieldContext ctx) {
    }

    @Override
    public void exitThrownTypesField(LLVMIRParser.ThrownTypesFieldContext ctx) {
    }

    @Override
    public void enterTypeMacinfoField(LLVMIRParser.TypeMacinfoFieldContext ctx) {
    }

    @Override
    public void exitTypeMacinfoField(LLVMIRParser.TypeMacinfoFieldContext ctx) {
    }

    @Override
    public void enterTypesField(LLVMIRParser.TypesFieldContext ctx) {
    }

    @Override
    public void exitTypesField(LLVMIRParser.TypesFieldContext ctx) {
    }

    @Override
    public void enterUnitField(LLVMIRParser.UnitFieldContext ctx) {
    }

    @Override
    public void exitUnitField(LLVMIRParser.UnitFieldContext ctx) {
    }

    @Override
    public void enterUpperBoundField(LLVMIRParser.UpperBoundFieldContext ctx) {
    }

    @Override
    public void exitUpperBoundField(LLVMIRParser.UpperBoundFieldContext ctx) {
    }

    @Override
    public void enterValueIntField(LLVMIRParser.ValueIntFieldContext ctx) {
    }

    @Override
    public void exitValueIntField(LLVMIRParser.ValueIntFieldContext ctx) {
    }

    @Override
    public void enterValueStringField(LLVMIRParser.ValueStringFieldContext ctx) {
    }

    @Override
    public void exitValueStringField(LLVMIRParser.ValueStringFieldContext ctx) {
    }

    @Override
    public void enterVarField(LLVMIRParser.VarFieldContext ctx) {
    }

    @Override
    public void exitVarField(LLVMIRParser.VarFieldContext ctx) {
    }

    @Override
    public void enterVirtualIndexField(LLVMIRParser.VirtualIndexFieldContext ctx) {
    }

    @Override
    public void exitVirtualIndexField(LLVMIRParser.VirtualIndexFieldContext ctx) {
    }

    @Override
    public void enterVirtualityField(LLVMIRParser.VirtualityFieldContext ctx) {
    }

    @Override
    public void exitVirtualityField(LLVMIRParser.VirtualityFieldContext ctx) {
    }

    @Override
    public void enterVtableHolderField(LLVMIRParser.VtableHolderFieldContext ctx) {
    }

    @Override
    public void exitVtableHolderField(LLVMIRParser.VtableHolderFieldContext ctx) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

}
