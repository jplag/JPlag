package de.jplag.c.experimental;

import static de.jplag.SharedTokenAttribute.FILE_END;
import static de.jplag.c.CTokenAttribute.C_BLOCK_BEGIN;
import static de.jplag.c.CTokenAttribute.C_BLOCK_END;
import static de.jplag.c.CTokenAttribute.C_BREAK;
import static de.jplag.c.CTokenAttribute.C_CASE;
import static de.jplag.c.CTokenAttribute.C_CONTINUE;
import static de.jplag.c.CTokenAttribute.C_FOR;
import static de.jplag.c.CTokenAttribute.C_GOTO;
import static de.jplag.c.CTokenAttribute.C_IF;
import static de.jplag.c.CTokenAttribute.C_RETURN;
import static de.jplag.c.CTokenAttribute.C_THROW;
import static de.jplag.c.CTokenAttribute.C_WHILE;

import java.util.List;
import java.util.ListIterator;

import de.jplag.Token;
import de.jplag.TokenType;

/**
 * Contains a basic algorithm for detecting tokens contained in unreachable code.
 */
public final class UnreachableCodeFilter {

    private UnreachableCodeFilter() {
    }

    /**
     * Applies the filtering on the provided token list.
     * @param tokenList The list that will be filtered. The contents of this parameter will be modified.
     */
    public static void applyTo(List<Token> tokenList) {
        TokenFilterState stateMachine = TokenFilterState.STATE_DEFAULT;

        ListIterator<Token> iterator = tokenList.listIterator();
        while (iterator.hasNext()) {
            var token = iterator.next();

            stateMachine = stateMachine.nextState(token.getType());

            if (stateMachine.shouldTokenBeDeleted()) {
                iterator.remove();
            }
        }
    }

    /**
     * Represents the state of a simple state machine for C++ tokens.
     */
    private enum TokenFilterState {
        STATE_DEFAULT {
            @Override
            TokenFilterState nextState(TokenType nextType) {
                if (isBlockStartToken(nextType)) {
                    return STATE_BLOCK_BEGINNING;
                }
                if (isJumpToken(nextType)) {
                    return STATE_DEAD_BLOCK_BEGINNING;
                }
                if (nextType.matches(C_CASE)) {
                    return STATE_CASE_BLOCK;
                }
                return STATE_DEFAULT;
            }
        },
        STATE_BLOCK_BEGINNING {
            @Override
            TokenFilterState nextState(TokenType nextType) {
                if (isBlockEndToken(nextType) || nextType.matches(C_BLOCK_BEGIN)) {
                    return STATE_DEFAULT;
                }
                return STATE_BLOCK_BEGINNING;
            }
        },
        STATE_DEAD_BLOCK {
            @Override
            TokenFilterState nextState(TokenType nextType) {
                if (isBlockEndToken(nextType)) {
                    return STATE_DEFAULT;
                }
                if (nextType.matches(C_CASE)) {
                    return STATE_CASE_BLOCK;
                }
                return STATE_DEAD_BLOCK;
            }

            @Override
            public boolean shouldTokenBeDeleted() {
                return true;
            }
        },
        // the current token starts a dead block, so everything afterwards should be deleted, until the dead block is closed.
        STATE_DEAD_BLOCK_BEGINNING {
            @Override
            TokenFilterState nextState(TokenType nextType) {
                if (isBlockEndToken(nextType)) {
                    return STATE_DEFAULT;
                }
                if (nextType.matches(C_CASE)) {
                    return STATE_CASE_BLOCK;
                }
                return STATE_DEAD_BLOCK;
            }
        },
        // case blocks don't use braces, but the end of a case block is easy to recognize
        STATE_CASE_BLOCK {
            @Override
            TokenFilterState nextState(TokenType nextType) {
                if (isBlockEndToken(nextType)) {
                    return STATE_DEFAULT;
                }
                if (isJumpToken(nextType)) {
                    return STATE_DEAD_BLOCK_BEGINNING;
                }
                return STATE_CASE_BLOCK;
            }
        };

        private static boolean isBlockStartToken(TokenType token) {
            return token.matches(C_WHILE) || token.matches(C_IF) || token.matches(C_FOR);
        }

        private static boolean isBlockEndToken(TokenType token) {
            return token.matches(C_BLOCK_END) || token.matches(FILE_END);
        }

        // jump tokens are tokens that force a code execution jump in EVERY case and therefore indicate unreachable code.
        private static boolean isJumpToken(TokenType token) {
            return token.matches(C_RETURN) || token.matches(C_BREAK) || token.matches(C_CONTINUE) || token.matches(C_THROW) || token.matches(C_GOTO);
        }

        /**
         * Determine if the current token should be deleted, because it is located in dead or unreachable code.
         * @return true if the token corresponding to the current state is located in dead or unreachable code, false otherwise.
         */
        public boolean shouldTokenBeDeleted() {
            return false;
        }

        /**
         * Determine the next state depending on the current state and the next token type.
         * @param nextType The type of the next token in the token list.
         * @return the new state corresponding to the next token
         */
        abstract TokenFilterState nextState(TokenType nextType);
    }
}
