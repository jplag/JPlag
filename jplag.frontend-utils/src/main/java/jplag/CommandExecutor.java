package jplag;

import java.io.*;

public class CommandExecutor {
    public static Tuple3<Integer, String, String> execute(final InputStream input, String ... args) {
        Process exec;
        try {
            exec = new ProcessBuilder().command(args).start();
        } catch (IOException e) {
            e.printStackTrace();
            return new Tuple3<>(-1, "", "");
        }

        final OutputStream stdin = exec.getOutputStream();
        StreamGobbler stderr = new StreamGobbler(exec.getErrorStream());
        StreamGobbler stdout = new StreamGobbler(exec.getInputStream());
        final Thread writer = new Thread() {
            @Override
            public void run() {
                byte[] buffer = new byte[8192];
                int len = 0;
                try {
                    while ((len = input.read(buffer)) >= 0) {
                        stdin.write(buffer, 0, len);
                    }
                    stdin.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stdin.flush();
                        stdin.close();
                    } catch (IOException e) {}
                }
            }
        };


        writer.start();
        stderr.start();
        stdout.start();

        while (true) {
            try {
                exec.waitFor();
                break;
            } catch (InterruptedException e) {
            }
        }

        while (true) {
            try {
                stderr.join();
                break;
            } catch (InterruptedException e) {
            }
        }
        while (true) {
            try {
                stdout.join();
                break;
            } catch (InterruptedException e) {
            }
        }
        while (true) {
            try {
                writer.join();
                break;
            } catch (InterruptedException e) {
            }
        }

        return new Tuple3<>(exec.exitValue(), stdout.getRes(), stderr.getRes());
    }

    public static Tuple3<Integer, String, String> execute(String ... args) {
        return execute(new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        }, args);
    }
}
