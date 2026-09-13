package de.jplag.x86;

import de.jplag.TokenType;

public enum X86_64AssemblerTokenType implements TokenType {
    SECTION_MARKER("sec", false),
    LABEL("lab", false),
    EXTERN("extern", false),

    OPERAND_REGISTER("reg", false),
    OPERAND_ADDRESS("add", false),
    OPERAND_VALUE("val", false),

    // OP-Codes

    // Basic data type declarations
    DB("db", true),     // Define Byte (8-bit)
    DW("dw", true),     // Define Word (16-bit)
    DD("dd", true),     // Define Doubleword (32-bit)
    DQ("dq", true),     // Define Quadword (64-bit)
    DT("dt", true),     // Define Ten Bytes (80-bit floating point)
    EQU("equ", true),

    // Reservation operations
    RESB("resb", true), // Reserve Byte
    RESW("resw", true), // Reserve Word
    RESD("resd", true), // Reserve Doubleword
    RESQ("resq", true), // Reserve Quadword

    // String/text declarations
    ASCIZ("asciz", true),  // Null-terminated string
    ASCIIZ("asciiz", true),// Alternative name for null-terminated string
    ASCII("ascii", true),  // String without null termination

    // Floating point declarations
    REAL4("real4", true),  // 32-bit floating point
    REAL8("real8", true),  // 64-bit floating point
    REAL10("real10", true),// 80-bit floating point

    GLOBAL("global", true), // Declare a symbol as globally visible

    // Alignment declarations
    ALIGN("align", true),  // Align next data to specified boundary

    // SIMD and extended declarations
    TIMES("times", true),   // Repeat declaration multiple times

    // Data Movement Instructions
    MOV_REG_REG("mov", true),
    MOV_REG_MEM("mov", true),
    MOV_MEM_REG("mov", true),
    LEA("lea", true),
    XCHG("xchg", true),

    // Arithmetic Instructions
    ADD("add", true),
    SUB("sub", true),
    MUL("mul", true),
    IMUL("imul", true),
    DIV("div", true),
    IDIV("idiv", true),
    INC("inc", true),
    DEC("dec", true),
    NEG("neg", true),
    ADC("adc", true),
    SBB("sbb", true),

    // Decimal Adjust Instructions
    AAA("aaa", true),
    AAD("aad", true),
    AAM("aam", true),
    AAS("aas", true),

    // Logical and Bit Manipulation
    AND("and", true),
    OR("or", true),
    XOR("xor", true),
    NOT("not", true),

    // Shift Operations
    SHL("shl", true),
    SHR("shr", true),
    SAL("sal", true),
    SAR("sar", true),
    ROL("rol", true),
    ROR("ror", true),
    RCL("rcl", true),
    RCR("rcr", true),

    // Comparison Instructions
    CMP("cmp", true),
    TEST("test", true),

    // Conditional Jump Instructions
    JE("je", true),
    JNE("jne", true),
    JG("jg", true),
    JGE("jge", true),
    JL("jl", true),
    JLE("jle", true),
    JZ("jz", true),
    JNZ("jnz", true),
    JC("jc", true),
    JNC("jnc", true),

    // Unconditional Jump
    JMP("jmp", true),

    // Conditional Move Instructions
    CMOVE("cmove", true),
    CMOVNE("cmovne", true),
    CMOVG("cmovg", true),
    CMOVGE("cmovge", true),
    CMOVL("cmovl", true),
    CMOVLE("cmovle", true),

    // Stack Operations
    PUSH("push", true),
    POP("pop", true),
    CALL("call", true),
    RET("ret", true),
    ENTER("enter", true),
    LEAVE("leave", true),

    // String Instructions
    MOVS("movs", true),
    CMPS("cmps", true),
    SCAS("scas", true),
    STOS("stos", true),
    LODS("lods", true),

    // System and Control Instructions
    INT("int", true),
    SYSCALL("syscall", true),
    SYSRET("sysret", true),
    CLI("cli", true),
    STI("sti", true),
    CLD("cld", true),
    STD("std", true),
    NOP("nop", true),

    // Special Instructions
    PAUSE("pause", true),
    LOCK("lock", true),
    REP("rep", true),
    REPE("repe", true),

    // Floating Point Instructions
    FADD("fadd", true),
    FSUB("fsub", true),
    FMUL("fmul", true),
    FDIV("fdiv", true),
    FLD("fld", true),

    // SIMD Instructions
    MOVAPS("movaps", true),
    ADDPS("addps", true),
    MULPS("mulps", true),

    // Additional Floating Point Instructions
    FSTP("fstp", true),      // Store Floating-Point Value and Pop
    FLDZ("fldz", true),      // Load +0.0
    FLD1("fld1", true),      // Load +1.0

    // Additional Bit and Decimal Adjust Instructions
    BSF("bsf", true),        // Bit Scan Forward
    BSR("bsr", true),        // Bit Scan Reverse

    // Additional System and Control Instructions
    CPUID("cpuid", true),    // CPU Identification
    RDTSC("rdtsc", true),    // Read Time-Stamp Counter

    // Additional Comparison and Conditional Instructions
    FCOMI("fcomi", true),    // Compare Floating-Point Values
    FUCOMI("fucomi", true),  // Compare Floating-Point Values (Unordered)

    // SIMD and Advanced Instructions
    MOVDQU("movdqu", true),  // Move Unaligned Double Quadword
    PADDB("paddb", true),    // Add Packed Bytes
    PADDW("paddw", true),    // Add Packed Words
    PADDD("paddd", true),    // Add Packed Doublewords

    // Cryptographic and Special Instructions
    RDRAND("rdrand", true),  // Read Random Number
    AESENC("aesenc", true),  // AES Encryption

    // Bit Manipulation Instructions
    BEXTR("bextr", true),    // Bit Extract
    BLSI("blsi", true),      // Isolate Lowest Set Bit
    BLSMSK("blsmsk", true),  // Get Mask of Lowest Set Bit
    BLSR("blsr", true),      // Reset Lowest Set Bit

    // String Instructions
    MOVSQ("movsq", true),    // Move Quadword
    STOSQ("stosq", true);    // Store Quadword
    ;

    private String description;
    private boolean isOpCode;

    X86_64AssemblerTokenType(String description, boolean isOpCode) {
        this.description = description;
        this.isOpCode = isOpCode;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public boolean isOpCode() {
        return isOpCode;
    }
}
