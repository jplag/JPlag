package jplag;

import java.io.IOException;

public class CommandExecutor {
    public static Tuple3<Integer, String, String> execute(String ... args) {
        Process exec;
        try {
            exec = new ProcessBuilder().command(args).start();
        } catch (IOException e) {
            e.printStackTrace();
            return new Tuple3<>(-1, "", "");
        }


        StreamGobbler stderr = new StreamGobbler(exec.getErrorStream());
        StreamGobbler stdout = new StreamGobbler(exec.getInputStream());

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

        return new Tuple3<>(exec.exitValue(), stdout.getRes(), stderr.getRes());
    }
}
