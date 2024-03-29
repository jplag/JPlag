package de.jplag.llvmir;

import static de.jplag.llvmir.LLVMIRTokenType.ADDITION;
import static de.jplag.llvmir.LLVMIRTokenType.ALLOCATION;
import static de.jplag.llvmir.LLVMIRTokenType.AND;
import static de.jplag.llvmir.LLVMIRTokenType.ARRAY;
import static de.jplag.llvmir.LLVMIRTokenType.ASSEMBLY;
import static de.jplag.llvmir.LLVMIRTokenType.ATOMIC_ORDERING;
import static de.jplag.llvmir.LLVMIRTokenType.ATOMIC_READ_MODIFY_WRITE;
import static de.jplag.llvmir.LLVMIRTokenType.BASIC_BLOCK_BEGIN;
import static de.jplag.llvmir.LLVMIRTokenType.BASIC_BLOCK_END;
import static de.jplag.llvmir.LLVMIRTokenType.BITCAST;
import static de.jplag.llvmir.LLVMIRTokenType.BRANCH;
import static de.jplag.llvmir.LLVMIRTokenType.CALL;
import static de.jplag.llvmir.LLVMIRTokenType.CALL_BRANCH;
import static de.jplag.llvmir.LLVMIRTokenType.CASE;
import static de.jplag.llvmir.LLVMIRTokenType.CATCH_PAD;
import static de.jplag.llvmir.LLVMIRTokenType.CATCH_RETURN;
import static de.jplag.llvmir.LLVMIRTokenType.CATCH_SWITCH;
import static de.jplag.llvmir.LLVMIRTokenType.CLAUSE;
import static de.jplag.llvmir.LLVMIRTokenType.CLEAN_UP_PAD;
import static de.jplag.llvmir.LLVMIRTokenType.CLEAN_UP_RETURN;
import static de.jplag.llvmir.LLVMIRTokenType.COMPARE_EXCHANGE;
import static de.jplag.llvmir.LLVMIRTokenType.COMPARISON;
import static de.jplag.llvmir.LLVMIRTokenType.CONDITIONAL_BRANCH;
import static de.jplag.llvmir.LLVMIRTokenType.CONVERSION;
import static de.jplag.llvmir.LLVMIRTokenType.DIVISION;
import static de.jplag.llvmir.LLVMIRTokenType.EXTRACT_ELEMENT;
import static de.jplag.llvmir.LLVMIRTokenType.EXTRACT_VALUE;
import static de.jplag.llvmir.LLVMIRTokenType.FENCE;
import static de.jplag.llvmir.LLVMIRTokenType.FILENAME;
import static de.jplag.llvmir.LLVMIRTokenType.FUNCTION_BODY_BEGIN;
import static de.jplag.llvmir.LLVMIRTokenType.FUNCTION_BODY_END;
import static de.jplag.llvmir.LLVMIRTokenType.FUNCTION_DECLARATION;
import static de.jplag.llvmir.LLVMIRTokenType.FUNCTION_DEFINITION;
import static de.jplag.llvmir.LLVMIRTokenType.GET_ELEMENT_POINTER;
import static de.jplag.llvmir.LLVMIRTokenType.GLOBAL_VARIABLE;
import static de.jplag.llvmir.LLVMIRTokenType.INSERT_ELEMENT;
import static de.jplag.llvmir.LLVMIRTokenType.INSERT_VALUE;
import static de.jplag.llvmir.LLVMIRTokenType.INVOKE;
import static de.jplag.llvmir.LLVMIRTokenType.LANDING_PAD;
import static de.jplag.llvmir.LLVMIRTokenType.LOAD;
import static de.jplag.llvmir.LLVMIRTokenType.MULTIPLICATION;
import static de.jplag.llvmir.LLVMIRTokenType.OR;
import static de.jplag.llvmir.LLVMIRTokenType.PHI;
import static de.jplag.llvmir.LLVMIRTokenType.REMAINDER;
import static de.jplag.llvmir.LLVMIRTokenType.RESUME;
import static de.jplag.llvmir.LLVMIRTokenType.RETURN;
import static de.jplag.llvmir.LLVMIRTokenType.SELECT;
import static de.jplag.llvmir.LLVMIRTokenType.SHIFT;
import static de.jplag.llvmir.LLVMIRTokenType.SHUFFLE_VECTOR;
import static de.jplag.llvmir.LLVMIRTokenType.STORE;
import static de.jplag.llvmir.LLVMIRTokenType.STRUCTURE;
import static de.jplag.llvmir.LLVMIRTokenType.SUBTRACTION;
import static de.jplag.llvmir.LLVMIRTokenType.SWITCH;
import static de.jplag.llvmir.LLVMIRTokenType.TYPE_DEFINITION;
import static de.jplag.llvmir.LLVMIRTokenType.VARIABLE_ARGUMENT;
import static de.jplag.llvmir.LLVMIRTokenType.VECTOR;
import static de.jplag.llvmir.LLVMIRTokenType.XOR;

import de.jplag.antlr.AbstractAntlrListener;
import de.jplag.llvmir.grammar.LLVMIRParser.*;

/**
 * Extracts tokens from the ANTLR parse tree. The token abstraction includes nesting tokens for functions and basic
 * blocks and separate tokens for different elements. These include binary and bitwise instructions, memory operations,
 * terminator instructions, conversions, global variables, type definitions, constants, and others.
 */
class LLVMIRListener extends AbstractAntlrListener {

    LLVMIRListener() {
        visit(SourceFilenameContext.class).map(FILENAME);
        visit(ModuleAsmContext.class).map(ASSEMBLY);
        visit(TypeDefContext.class).map(TYPE_DEFINITION);
        visit(GlobalDeclContext.class).map(GLOBAL_VARIABLE);
        visit(GlobalDefContext.class).map(GLOBAL_VARIABLE);
        visit(FuncDeclContext.class).map(FUNCTION_DECLARATION);
        visit(FuncDefContext.class).map(FUNCTION_DEFINITION);
        visit(FuncBodyContext.class).map(FUNCTION_BODY_BEGIN, FUNCTION_BODY_END);
        visit(BasicBlockContext.class).map(BASIC_BLOCK_BEGIN, BASIC_BLOCK_END);
        visit(RetTermContext.class).map(RETURN);
        visit(BrTermContext.class).map(BRANCH);
        visit(CondBrTermContext.class).map(CONDITIONAL_BRANCH);
        visit(SwitchTermContext.class).map(SWITCH);
        visit(IndirectBrTermContext.class).map(BRANCH);
        visit(ResumeTermContext.class).map(RESUME);
        visit(CatchRetTermContext.class).map(CATCH_RETURN);
        visit(CleanupRetTermContext.class).map(CLEAN_UP_RETURN);
        visit(InvokeTermContext.class).map(INVOKE);
        visit(CallBrTermContext.class).map(CALL_BRANCH);
        visit(CatchSwitchTermContext.class).map(CATCH_SWITCH);
        visit(Case_Context.class).map(CASE);
        visit(StructConstContext.class).map(STRUCTURE);
        visit(ArrayConstContext.class).map(ARRAY);
        visit(VectorConstContext.class).map(VECTOR);
        visit(InlineAsmContext.class).map(ASSEMBLY);
        visit(BitCastExprContext.class).map(BITCAST);
        visit(GetElementPtrExprContext.class).map(GET_ELEMENT_POINTER);
        visit(AddrSpaceCastExprContext.class).map(CONVERSION);
        visit(IntToPtrExprContext.class).map(CONVERSION);
        visit(ICmpExprContext.class).map(COMPARISON);
        visit(FCmpExprContext.class).map(COMPARISON);
        visit(SelectExprContext.class).map(SELECT);
        visit(TruncExprContext.class).map(CONVERSION);
        visit(ZExtExprContext.class).map(CONVERSION);
        visit(SExtExprContext.class).map(CONVERSION);
        visit(FpTruncExprContext.class).map(CONVERSION);
        visit(FpExtExprContext.class).map(CONVERSION);
        visit(FpToUiExprContext.class).map(CONVERSION);
        visit(FpToSiExprContext.class).map(CONVERSION);
        visit(UiToFpExprContext.class).map(CONVERSION);
        visit(SiToFpExprContext.class).map(CONVERSION);
        visit(PtrToIntExprContext.class).map(CONVERSION);
        visit(ExtractElementExprContext.class).map(EXTRACT_ELEMENT);
        visit(InsertElementExprContext.class).map(INSERT_ELEMENT);
        visit(ShuffleVectorExprContext.class).map(SHUFFLE_VECTOR);
        visit(ShlExprContext.class).map(SHIFT);
        visit(LShrExprContext.class).map(SHIFT);
        visit(AShrExprContext.class).map(SHIFT);
        visit(AndExprContext.class).map(AND);
        visit(OrExprContext.class).map(OR);
        visit(XorExprContext.class).map(XOR);
        visit(AddExprContext.class).map(ADDITION);
        visit(SubExprContext.class).map(SUBTRACTION);
        visit(MulExprContext.class).map(MULTIPLICATION);
        visit(StoreInstContext.class).map(STORE);
        visit(FenceInstContext.class).map(FENCE);
        visit(AddInstContext.class).map(ADDITION);
        visit(FAddInstContext.class).map(ADDITION);
        visit(SubInstContext.class).map(SUBTRACTION);
        visit(FSubInstContext.class).map(SUBTRACTION);
        visit(MulInstContext.class).map(MULTIPLICATION);
        visit(FMulInstContext.class).map(MULTIPLICATION);
        visit(UDivInstContext.class).map(DIVISION);
        visit(SDivInstContext.class).map(DIVISION);
        visit(FDivInstContext.class).map(DIVISION);
        visit(URemInstContext.class).map(REMAINDER);
        visit(SRemInstContext.class).map(REMAINDER);
        visit(FRemInstContext.class).map(REMAINDER);
        visit(ShlInstContext.class).map(SHIFT);
        visit(LShrInstContext.class).map(SHIFT);
        visit(AShrInstContext.class).map(SHIFT);
        visit(AndInstContext.class).map(AND);
        visit(OrInstContext.class).map(OR);
        visit(XorInstContext.class).map(XOR);
        visit(ExtractElementInstContext.class).map(EXTRACT_ELEMENT);
        visit(InsertElementInstContext.class).map(INSERT_ELEMENT);
        visit(ShuffleVectorInstContext.class).map(SHUFFLE_VECTOR);
        visit(ExtractValueInstContext.class).map(EXTRACT_VALUE);
        visit(InsertValueInstContext.class).map(INSERT_VALUE);
        visit(AllocaInstContext.class).map(ALLOCATION);
        visit(LoadInstContext.class).map(LOAD);
        visit(CmpXchgInstContext.class).map(COMPARE_EXCHANGE);
        visit(AtomicRMWInstContext.class).map(ATOMIC_READ_MODIFY_WRITE);
        visit(GetElementPtrInstContext.class).map(GET_ELEMENT_POINTER);
        visit(TruncInstContext.class).map(CONVERSION);
        visit(ZExtInstContext.class).map(CONVERSION);
        visit(SExtInstContext.class).map(CONVERSION);
        visit(FpTruncInstContext.class).map(CONVERSION);
        visit(FpExtInstContext.class).map(CONVERSION);
        visit(FpToUiInstContext.class).map(CONVERSION);
        visit(FpToSiInstContext.class).map(CONVERSION);
        visit(UiToFpInstContext.class).map(CONVERSION);
        visit(SiToFpInstContext.class).map(CONVERSION);
        visit(PtrToIntInstContext.class).map(CONVERSION);
        visit(IntToPtrInstContext.class).map(CONVERSION);
        visit(BitCastInstContext.class).map(BITCAST);
        visit(AddrSpaceCastInstContext.class).map(CONVERSION);
        visit(ICmpInstContext.class).map(COMPARISON);
        visit(FCmpInstContext.class).map(COMPARISON);
        visit(PhiInstContext.class).map(PHI);
        visit(SelectInstContext.class).map(SELECT);
        visit(CallInstContext.class).map(CALL);
        visit(VaargInstContext.class).map(VARIABLE_ARGUMENT);
        visit(LandingPadInstContext.class).map(LANDING_PAD);
        visit(CatchPadInstContext.class).map(CATCH_PAD);
        visit(CleanupPadInstContext.class).map(CLEAN_UP_PAD);
        visit(ClauseContext.class).map(CLAUSE);
        visit(AtomicOrderingContext.class).map(ATOMIC_ORDERING);
    }
}
