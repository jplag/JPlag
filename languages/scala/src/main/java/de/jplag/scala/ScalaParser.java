package de.jplag.scala;

import static de.jplag.scala.ScalaTokenType.APPLY;
import static de.jplag.scala.ScalaTokenType.ARGUMENT;
import static de.jplag.scala.ScalaTokenType.ASSIGN;
import static de.jplag.scala.ScalaTokenType.BLOCK_END;
import static de.jplag.scala.ScalaTokenType.BLOCK_START;
import static de.jplag.scala.ScalaTokenType.CASE_BEGIN;
import static de.jplag.scala.ScalaTokenType.CASE_END;
import static de.jplag.scala.ScalaTokenType.CASE_STATEMENT;
import static de.jplag.scala.ScalaTokenType.CATCH_BEGIN;
import static de.jplag.scala.ScalaTokenType.CATCH_END;
import static de.jplag.scala.ScalaTokenType.CLASS_BEGIN;
import static de.jplag.scala.ScalaTokenType.CLASS_END;
import static de.jplag.scala.ScalaTokenType.CONSTRUCTOR_BEGIN;
import static de.jplag.scala.ScalaTokenType.CONSTRUCTOR_END;
import static de.jplag.scala.ScalaTokenType.DO_BODY_BEGIN;
import static de.jplag.scala.ScalaTokenType.DO_BODY_END;
import static de.jplag.scala.ScalaTokenType.DO_WHILE;
import static de.jplag.scala.ScalaTokenType.DO_WHILE_END;
import static de.jplag.scala.ScalaTokenType.ELSE;
import static de.jplag.scala.ScalaTokenType.ELSE_BEGIN;
import static de.jplag.scala.ScalaTokenType.ELSE_END;
import static de.jplag.scala.ScalaTokenType.ENUM_GENERATOR;
import static de.jplag.scala.ScalaTokenType.FINALLY;
import static de.jplag.scala.ScalaTokenType.FOR;
import static de.jplag.scala.ScalaTokenType.FOR_BODY_BEGIN;
import static de.jplag.scala.ScalaTokenType.FOR_BODY_END;
import static de.jplag.scala.ScalaTokenType.FUNCTION_BEGIN;
import static de.jplag.scala.ScalaTokenType.FUNCTION_END;
import static de.jplag.scala.ScalaTokenType.GUARD;
import static de.jplag.scala.ScalaTokenType.IF;
import static de.jplag.scala.ScalaTokenType.IF_BEGIN;
import static de.jplag.scala.ScalaTokenType.IF_END;
import static de.jplag.scala.ScalaTokenType.IMPORT;
import static de.jplag.scala.ScalaTokenType.MACRO;
import static de.jplag.scala.ScalaTokenType.MACRO_BEGIN;
import static de.jplag.scala.ScalaTokenType.MACRO_END;
import static de.jplag.scala.ScalaTokenType.MATCH_BEGIN;
import static de.jplag.scala.ScalaTokenType.MATCH_END;
import static de.jplag.scala.ScalaTokenType.MEMBER;
import static de.jplag.scala.ScalaTokenType.METHOD_BEGIN;
import static de.jplag.scala.ScalaTokenType.METHOD_DEF;
import static de.jplag.scala.ScalaTokenType.METHOD_END;
import static de.jplag.scala.ScalaTokenType.NEW_CREATION_BEGIN;
import static de.jplag.scala.ScalaTokenType.NEW_CREATION_END;
import static de.jplag.scala.ScalaTokenType.NEW_OBJECT;
import static de.jplag.scala.ScalaTokenType.OBJECT_BEGIN;
import static de.jplag.scala.ScalaTokenType.OBJECT_END;
import static de.jplag.scala.ScalaTokenType.PACKAGE;
import static de.jplag.scala.ScalaTokenType.PARAMETER;
import static de.jplag.scala.ScalaTokenType.PARTIAL_FUNCTION_BEGIN;
import static de.jplag.scala.ScalaTokenType.PARTIAL_FUNCTION_END;
import static de.jplag.scala.ScalaTokenType.RETURN;
import static de.jplag.scala.ScalaTokenType.SELF_TYPE;
import static de.jplag.scala.ScalaTokenType.THROW;
import static de.jplag.scala.ScalaTokenType.TRAIT_BEGIN;
import static de.jplag.scala.ScalaTokenType.TRAIT_END;
import static de.jplag.scala.ScalaTokenType.TRY_BEGIN;
import static de.jplag.scala.ScalaTokenType.TYPE;
import static de.jplag.scala.ScalaTokenType.TYPE_ARGUMENT;
import static de.jplag.scala.ScalaTokenType.TYPE_PARAMETER;
import static de.jplag.scala.ScalaTokenType.VARIABLE_DEFINITION;
import static de.jplag.scala.ScalaTokenType.WHILE;
import static de.jplag.scala.ScalaTokenType.WHILE_BODY_BEGIN;
import static de.jplag.scala.ScalaTokenType.WHILE_BODY_END;
import static de.jplag.scala.ScalaTokenType.YIELD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.jplag.ParsingException;
import de.jplag.Token;
import de.jplag.util.FileUtils;

import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.Seq;
import scala.meta.Case;
import scala.meta.Ctor;
import scala.meta.Decl;
import scala.meta.Defn;
import scala.meta.Dialect;
import scala.meta.Enumerator;
import scala.meta.Import;
import scala.meta.Init;
import scala.meta.Lit;
import scala.meta.Member;
import scala.meta.Pat;
import scala.meta.Pkg;
import scala.meta.Self;
import scala.meta.Source;
import scala.meta.Term;
import scala.meta.Tree;
import scala.meta.Type;
import scala.meta.common.Convert;
import scala.meta.inputs.Input;
import scala.meta.inputs.Position;
import scala.meta.parsers.Api;
import scala.meta.parsers.Parse;

/**
 * Parser for Scala code.
 */
public class ScalaParser {
    private static final Set<String> OPERATORS = Set.of("+", "-", "*", "/", "%", "**", "==", "!=", ">", "<", ">=", "<=", "&&", "||", "!", "=", "+=",
            "-=", "*=", "/=", "%=", "**=", "<<=", ">>=", ">>>=", "&=", "^=", "|=", "&", "|", "^", "<<", ">>", "~", ">>>", "++", "::", ":::", "<:",
            ">:", "#");

    private File currentFile;
    private List<Token> tokens;

    private void handleDefinitionPattern(Pat pattern, Option<Term> optionalValue) {
        if (pattern instanceof Pat.Tuple tuple) {
            if (optionalValue.isDefined()) {
                if (optionalValue.get() instanceof Term.Tuple optionalValueTuple) {
                    // The java compiler is unable to infer the type without the explicit cast
                    ((scala.collection.immutable.List<Tuple2<Pat, Term>>) tuple.args().zip(optionalValueTuple.args())).foreach(it -> {
                        handleDefinitionPattern(it._1(), new Some<>(it._2()));
                        return null;
                    });
                } else {
                    tuple.args().foreach(arg -> {
                        handleDefinitionPattern(arg, Option.empty());
                        return null;
                    });
                    addToken(ASSIGN, optionalValue.get(), false);
                }
            } else {
                tuple.args().foreach(arg -> {
                    handleDefinitionPattern(arg, Option.empty());
                    return null;
                });
            }
        } else { // single variable
            addToken(VARIABLE_DEFINITION, pattern, false);
            visit(pattern);
            addTokenAndVisitIfPresent(optionalValue, ASSIGN);
        }
    }

    /**
     * Parses an ast node and returns the appropriate token record.
     * @param tree The ast node to parse
     * @return The token record
     */
    private TraverserRecord parse(Tree tree) {
        return switch (tree) {
            case Term.Do doBlock -> new TraverserRecord(DO_WHILE, DO_WHILE_END).traverse(() -> {
                encloseAndAppy(doBlock.body(), new TraverserRecord(DO_BODY_BEGIN, DO_BODY_END));
                visit(doBlock.cond());
            });
            case Term.Assign ignored -> new TraverserRecord(ASSIGN);
            case Term.While whileBlock -> new TraverserRecord(WHILE).traverse(() -> {
                visit(whileBlock.cond());
                encloseAndAppy(whileBlock.body(), new TraverserRecord(WHILE_BODY_BEGIN, WHILE_BODY_END));
            });
            case Term.For forBlock -> new TraverserRecord(FOR).traverse(() -> {
                visit(forBlock.enumsBlock());
                encloseAndAppy(forBlock.body(), new TraverserRecord(FOR_BODY_BEGIN, FOR_BODY_END));
            });
            case Term.Try tryBlock -> new TraverserRecord(TRY_BEGIN).traverse(() -> {
                visit(tryBlock.expr());
                encloseAndAppy(tryBlock.catchClause().get(), new TraverserRecord(CATCH_BEGIN, CATCH_END));
                addTokenAndVisitIfPresent(tryBlock.finallyp(), FINALLY);
            });
            case Term.TryWithHandler tryWithHandler -> new TraverserRecord(TRY_BEGIN).traverse(() -> {
                visit(tryWithHandler.expr());
                encloseAndAppy(tryWithHandler.catchClause().get(), new TraverserRecord(CATCH_BEGIN, CATCH_END));
                addTokenAndVisitIfPresent(tryWithHandler.finallyp(), FINALLY);
            });
            case Term.Apply call when !isStandardOperator(getMethodIdentifier(call.fun())) -> new TraverserRecord().traverse(() -> {
                encloseAndAppy(call.fun(), new TraverserRecord(APPLY));
                call.argClause().values().foreach(argument -> {
                    addToken(ARGUMENT, argument, false);
                    if (argument instanceof Term.Assign assignment) {
                        visit(assignment.lhs());
                        visit(assignment.rhs());
                    } else {
                        visit(argument);
                    }
                    return null;
                });
            });
            case Term.NewAnonymous ignored -> new TraverserRecord(NEW_CREATION_BEGIN, NEW_CREATION_END);
            case Term.Return ignored -> new TraverserRecord(RETURN);
            case Term.Match match -> new TraverserRecord(MATCH_BEGIN, MATCH_END).traverse(() -> {
                visit(match.expr());
                visit(match.casesBlock());
            });
            case Term.Throw ignored -> new TraverserRecord(THROW);
            case Term.Function ignored -> new TraverserRecord(FUNCTION_BEGIN, FUNCTION_END);
            case Term.PartialFunction ignored -> new TraverserRecord(PARTIAL_FUNCTION_BEGIN, PARTIAL_FUNCTION_END);
            case Term.ForYield forYield -> new TraverserRecord().traverse(() -> {
                visit(forYield.enumsBlock());
                addToken(FOR_BODY_BEGIN, forYield.body(), false);
                encloseAndAppy(forYield.body(), new TraverserRecord(YIELD, FOR_BODY_END));
            });
            case Term.If ifTerm -> new TraverserRecord().traverse(() -> {
                addToken(IF, ifTerm, false);
                visit(ifTerm.cond());
                encloseAndAppy(ifTerm.thenp(), new TraverserRecord(IF_BEGIN, IF_END));

                if (ifTerm.elsep() instanceof Lit.Unit) {
                    visit(ifTerm.elsep());
                } else {
                    int elseStart = ifTerm.pos().text().indexOf("else", ifTerm.thenp().pos().end() - ifTerm.pos().start());
                    Position.Range elsePosition = new Position.Range(ifTerm.pos().input(), ifTerm.pos().start() + elseStart,
                            ifTerm.pos().start() + elseStart + 4);
                    addToken(ELSE, elsePosition.startLine() + 1, elsePosition.startColumn() + 1, elsePosition.text().length());
                    encloseAndAppy(ifTerm.elsep(), new TraverserRecord(ELSE_BEGIN, ELSE_END));
                }
            });

            case Pkg ignored -> new TraverserRecord(PACKAGE);
            case Import ignored -> new TraverserRecord(IMPORT);

            case Defn.Def definition -> new TraverserRecord().traverse(() -> {
                visitAll(definition.mods());
                addToken(METHOD_DEF, definition.name(), false);
                addTokenForAll(getTParams(definition.paramClauseGroups()), TYPE_PARAMETER, false);
                addTokenForAll(getPParamsLists(definition.paramClauseGroups()), PARAMETER, false);

                encloseAndAppy(definition.body(), new TraverserRecord(METHOD_BEGIN, METHOD_END));
            });
            case Defn.Macro macroDef -> new TraverserRecord(MACRO).traverse(() -> {
                visitAll(macroDef.mods());
                visitAll(getTParams(macroDef.paramClauseGroups()));
                visitAll(getPParamsLists(macroDef.paramClauseGroups()));
                encloseAndAppy(macroDef.body(), new TraverserRecord(MACRO_BEGIN, MACRO_END));
            });
            case Defn.Class ignored -> new TraverserRecord(CLASS_BEGIN, CLASS_END);
            case Defn.Object ignored -> new TraverserRecord(OBJECT_BEGIN, OBJECT_END);
            case Defn.Trait ignored -> new TraverserRecord(TRAIT_BEGIN, TRAIT_END);
            case Defn.Type ignored -> new TraverserRecord(TYPE);
            case Defn.Var varDef -> new TraverserRecord().traverse(() -> {
                visitAll(varDef.mods());
                varDef.pats().foreach(pattern -> {
                    handleDefinitionPattern(pattern, new Some<>(varDef.body()));
                    return null;
                });
                if (varDef.decltpe().isDefined()) {
                    visit(varDef.decltpe().get());
                }
            });
            case Defn.Val definition -> new TraverserRecord().traverse(() -> {
                visitAll(definition.mods());
                definition.pats().foreach(pattern -> {
                    handleDefinitionPattern(pattern, new Some<>(definition.rhs()));
                    return null;
                });
            });

            case Decl.Var ignored -> new TraverserRecord(VARIABLE_DEFINITION);
            case Decl.Val ignored -> new TraverserRecord(VARIABLE_DEFINITION);
            case Decl.Def ignored -> new TraverserRecord(METHOD_BEGIN, METHOD_END);
            case Decl.Type ignored -> new TraverserRecord(TYPE);

            case Ctor.Secondary ignored -> new TraverserRecord(CONSTRUCTOR_BEGIN, CONSTRUCTOR_END);

            case Init init -> new TraverserRecord().traverse(() -> addTokenForAll(getArgList(init.argClauses()), ARGUMENT, true));
            case Enumerator.Guard ignored -> new TraverserRecord(GUARD);

            case Term.Param ignored -> new TraverserRecord().traverse(() -> addToken(PARAMETER, tree, false));
            case Term.ApplyInfix term when term.op().value().contains("=") && !List.of("==", "!=").contains(term.op().value()) -> new TraverserRecord(
                    ASSIGN);
            case Term.ApplyInfix term when !isStandardOperator(term.op().value()) -> new TraverserRecord().traverse(() -> {
                addToken(APPLY, tree, false);
                visit(term.lhs());
                addTokenForAll(term.targClause().values(), TYPE_ARGUMENT, true);
                addTokenForAll(term.argClause().values(), ARGUMENT, true);
            });
            case Term.Select select -> new TraverserRecord().traverse(() -> {
                visit(select.qual());
                if (!isStandardOperator(select.name().value())) {
                    addToken(MEMBER, select.name(), false);
                }
                visit(select.name());
            });
            case Term.ApplyType term -> new TraverserRecord().traverse(() -> {
                addToken(APPLY, term, false);
                addTokenForAll(term.targClause().values(), TYPE_ARGUMENT, false);
            });
            case Term.New ignored -> new TraverserRecord(NEW_OBJECT);
            case Self ignored -> new TraverserRecord(SELF_TYPE);
            case Term.Block block -> {
                if (block.parent().get().parent() instanceof Some<?> && ((Some<?>) block.parent().get().parent()).get() instanceof Term.Apply) {
                    yield new TraverserRecord(BLOCK_START, BLOCK_END);
                } else {
                    yield new TraverserRecord();
                }
            }
            case Enumerator.Generator ignored -> new TraverserRecord(ENUM_GENERATOR);
            case Type.Param ignored -> new TraverserRecord(TYPE_PARAMETER);

            case Case caseBlock -> new TraverserRecord(CASE_STATEMENT).traverse(() -> {
                visit(caseBlock.pat());
                if (caseBlock.cond().isDefined()) {
                    visit(caseBlock.cond().get());
                }
                encloseAndAppy(caseBlock.body(), new TraverserRecord(CASE_BEGIN, CASE_END));
            });
            default -> new TraverserRecord();
        };
    }

    /**
     * Adds the given token before the node and visits the node if it is present.
     * @param tree The ast node
     * @param tokenType The token type to add
     */
    private void addTokenAndVisitIfPresent(Option<? extends Tree> tree, ScalaTokenType tokenType) {
        if (tree.isDefined()) {
            encloseAndAppy(tree.get(), new TraverserRecord(tokenType));
        }
    }

    /**
     * Visits the given node. Adds tokens and traverses into children.
     * @param tree The node
     */
    public void visit(Tree tree) {
        TraverserRecord traverserRecord = parse(tree);
        traverserRecord.before().ifPresent(token -> addToken(token, tree, false));
        traverserRecord.traverse().accept(tree, this);
        traverserRecord.after().ifPresent(token -> addToken(token, tree, true));
    }

    /**
     * Visits all nodes in the list.
     * @param items The list of nodes
     */
    public void visitAll(scala.collection.immutable.List<? extends Tree> items) {
        items.foreach(tree -> {
            visit(tree);
            return null;
        });
    }

    /**
     * Adds a token for each node in the list. Also visits the nodes if traverse is true.
     * @param items The list of nodes
     * @param tokenType The type of token to add
     * @param traverse Decides if the nodes are visited
     */
    public void addTokenForAll(scala.collection.immutable.List<?> items, ScalaTokenType tokenType, boolean traverse) {
        items.foreach(element -> {
            if (element instanceof Tree tree) {
                addToken(tokenType, tree, false);
                if (traverse) {
                    visit(tree);
                }
            }
            if (element instanceof scala.collection.immutable.List<?> list) {
                addTokenForAll(list, tokenType, traverse);
            }
            return null;
        });
    }

    /**
     * Adds the given tokens around the node and visits it.
     * @param tree The ast node
     * @param traverserRecord The tokens to add
     */
    private void encloseAndAppy(Tree tree, TraverserRecord traverserRecord) {
        traverserRecord.before().ifPresent(token -> addToken(token, tree, false));
        visit(tree);
        traverserRecord.after().ifPresent(token -> addToken(token, tree, true));
    }

    /**
     * Adds the given token type for the node.
     * @param tokenType The type of token to add
     * @param node The node to add the token for
     * @param fromEnd if true, the token is added to the end. Otherwise, it is added to the beginning of the node
     */
    private void addToken(ScalaTokenType tokenType, Tree node, boolean fromEnd) {
        if (!node.pos().text().isEmpty()) {
            if (fromEnd) {
                tokens.add(new Token(tokenType, currentFile, node.pos().endLine() + 1, node.pos().endColumn() + 1, 0));
            } else {
                tokens.add(new Token(tokenType, currentFile, node.pos().startLine() + 1, node.pos().startColumn() + 1, node.pos().text().length()));
            }
        }
    }

    /**
     * Adds a token with the given data.
     * @param tokenType The type of token
     * @param line The start line
     * @param column The start column
     * @param length The length of the token
     */
    private void addToken(ScalaTokenType tokenType, int line, int column, int length) {
        tokens.add(new Token(tokenType, currentFile, line, column, length));
    }

    /**
     * Parses the given files.
     * @param files The files to parse
     * @return The list of tokens
     * @throws ParsingException If the parsing fails
     */
    public List<Token> parse(Set<File> files) throws ParsingException {
        this.tokens = new ArrayList<>();
        for (File file : files) {
            parseFile(file);
        }
        return tokens;
    }

    /**
     * Parses a single file.
     * @param file The file to parse
     * @throws ParsingException If the parsing fails
     */
    private void parseFile(File file) throws ParsingException {
        currentFile = file;

        try {
            String text = FileUtils.readFileContent(file, true);
            Input.VirtualFile input = new Input.VirtualFile(file.getPath(), text);
            Source source = new MyApi().parse(input);
            visit(source);
            tokens.add(Token.fileEnd(file));
        } catch (IOException e) {
            throw new ParsingException(file, e);
        }
    }

    /**
     * Wrapper to call the scala methods from java.
     */
    private static class MyApi implements Api {
        public Source parse(Input.VirtualFile virtualFile) {
            // All the casts are necessary to make the java compiler accept this line. There is no way to make this more concise, as
            // it is a scala method
            return new XtensionParseInputLike<>(virtualFile)
                    .parse((Convert<Input.VirtualFile, Input>) (Object) Convert.trivial(), Parse.parseSource(), Dialect.current()).get();
        }
    }

    private boolean isStandardOperator(String operator) {
        return OPERATORS.contains(operator);
    }

    private String getMethodIdentifier(Term function) {
        String[] parts = function.toString().split("\\.");
        return parts[parts.length - 1];
    }

    private scala.collection.immutable.List<Type.Param> getTParams(scala.collection.immutable.List<Member.ParamClauseGroup> groups) {
        return groups.flatMap(it -> it.tparamClause().values());
    }

    private scala.collection.immutable.List<Term.Param> getPParamsLists(scala.collection.immutable.List<Member.ParamClauseGroup> groups) {
        return groups.flatMap(it -> it.paramClauses().flatMap(Term.ParamClause::values));
    }

    private scala.collection.immutable.List<Term.ArgClause> getArgList(Seq<Term.ArgClause> arguments) {
        return arguments.map(Term.ArgClause::values).toList();
    }
}
