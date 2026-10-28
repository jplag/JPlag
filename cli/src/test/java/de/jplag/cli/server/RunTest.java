package de.jplag.cli.server;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RunTest {



    @Test
    void testRun() throws IOException {

        ConfigurationSever server = new ConfigurationSever(1996);
        server.start();
        System.in.read();
        server.stop();
    }
}
