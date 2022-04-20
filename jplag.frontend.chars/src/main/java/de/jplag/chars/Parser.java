package de.jplag.chars;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import de.jplag.AbstractParser;
import de.jplag.ErrorConsumer;
import de.jplag.TokenConstants;
import de.jplag.TokenList;

public class Parser extends AbstractParser {

    private TokenList tokens;

    /**
     * Creates the parser.
     * @param errorConsumer is the consumer for any occurring errors.
     */
    public Parser(ErrorConsumer errorConsumer) {
        super(errorConsumer);
    }

    public TokenList parse(File directory, String[] files) {
        tokens = new TokenList();
        errors = 0;
        for (String file : files) {
            getErrorConsumer().print(null, "Parsing file " + file);
            if (!parseFile(directory, file))
                errors++;
            tokens.addToken(new CharToken(TokenConstants.FILE_END, file, this));
        }
        if (errors == 0)
            errorConsumer.print(null, "OK");
        else
            errorConsumer.print(null, errors + " ERROR" + (errors > 1 ? "S" : ""));

        return tokens;
    }

    private boolean parseFile(File dir, String file) {
        char[] buffer = new char[4096];
        int type;
        int length;
        int offset = 0;

        try {
            FileReader reader = new FileReader(new File(dir, file), StandardCharsets.UTF_8);

            do {
                length = reader.read(buffer);

                for (int i = 0; i < length; i++) {
                    if (buffer[i] <= 127 && (type = mapping[buffer[i]]) > 1) {
                        tokens.addToken(new CharToken(type, file, offset + i, buffer[i], this));
                    }
                }
                offset += length;
            } while (length != -1);

            // close file
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private final int[] mapping = {-1, // 0 (nul)
            -1, // 1 (soh)
            -1, // 2 (stx)
            -1, // 3 (etx)
            -1, // 4 (eot)
            -1, // 5 (enq)
            -1, // 6 (ack)
            -1, // 7 (bel)
            -1, // 8 (bs)
            -1, // 9 (ht)
            -1, // 10 (nl)
            -1, // 11 (vt)
            -1, // 12 (np)
            -1, // 13 (cr)
            -1, // 14 (so)
            -1, // 15 (si)
            -1, // 16 (dle)
            -1, // 17 (dc1)
            -1, // 18 (dc2)
            -1, // 19 (dc3)
            -1, // 20 (dc4)
            -1, // 21 (nak)
            -1, // 22 (syn)
            -1, // 23 (etb)
            -1, // 24 (can)
            -1, // 25 (em)
            -1, // 26 (sub)
            -1, // 27 (esc)
            -1, // 28 (fs)
            -1, // 29 (gs)
            -1, // 30 (rs)
            -1, // 31 (us)
            -1, // 32 (sp)
            -1, // 33 !
            -1, // 34 "
            -1, // 35 #
            -1, // 36 $
            -1, // 37 %
            -1, // 38 &
            -1, // 39 '
            -1, // 40 (
            -1, // 41 )
            -1, // 42 *
            -1, // 43 +
            -1, // 44 ,
            -1, // 45 -
            -1, // 46 .
            -1, // 47 /
            27, // 48 0
            28, // 49 1
            29, // 50 2
            30, // 51 3
            31, // 52 4
            32, // 53 5
            33, // 54 6
            34, // 55 7
            35, // 56 8
            36, // 57 9
            -1, // 58 :
            -1, // 59 ;
            -1, // 60 <
            -1, // 61 =
            -1, // 62 >
            -1, // 63 ?
            -1, // 64 @
            2, // 65 A
            3, // 66 B
            4, // 67 C
            5, // 68 D
            6, // 69 E
            7, // 70 F
            8, // 71 G
            9, // 72 H
            10, // 73 I
            11, // 74 J
            12, // 75 K
            13, // 76 L
            14, // 77 M
            15, // 78 N
            16, // 79 O
            17, // 80 P
            18, // 81 Q
            19, // 82 R
            20, // 83 S
            21, // 84 T
            22, // 85 U
            23, // 86 V
            24, // 87 W
            25, // 88 X
            26, // 89 Y
            27, // 90 Z
            -1, // 91 [
            -1, // 92 \
            -1, // 93 ]
            -1, // 94 ^
            -1, // 95 _
            -1, // 96 `
            2, // 97 a
            3, // 98 b
            4, // 99 c
            5, // 100 d
            6, // 101 e
            7, // 102 f
            8, // 103 g
            9, // 104 h
            10, // 105 i
            11, // 106 j
            12, // 107 k
            13, // 108 l
            14, // 109 m
            15, // 110 n
            16, // 111 o
            17, // 112 p
            18, // 113 q
            19, // 114 r
            20, // 115 s
            21, // 116 t
            22, // 117 u
            23, // 118 v
            24, // 119 w
            25, // 120 x
            26, // 121 y
            27, // 122 z
            -1, // 123 {
            -1, // 124 |
            -1, // 125 }
            -1, // 126 ~
            -1, // 127 (del)
    };
}
