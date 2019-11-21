package jplag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
    private InputStream is;
    private String res;

    public String getRes() {
        return res;
    }

    public StreamGobbler(InputStream is) {
        this.is = is;
        this.res = null;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        this.res = null;

        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int len = 0;

            while ((len = br.read(buffer)) != -1) {
                builder.append(buffer, 0, len);
            }

            this.res = builder.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                try {
                    if (isr != null) {
                        isr.close();
                    }
                } finally {
                    if (br != null) {
                        br.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
