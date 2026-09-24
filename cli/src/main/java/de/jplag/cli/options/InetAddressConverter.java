package de.jplag.cli.options;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.jplag.cli.CliException;

import picocli.CommandLine;

/**
 * Converts a CLI string argument to an {@link InetAddress}.
 */
public class InetAddressConverter implements CommandLine.ITypeConverter<InetAddress> {
    @Override
    public InetAddress convert(String value) throws Exception {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new CliException("Invalid bind address: " + value, e);
        }
    }
}
