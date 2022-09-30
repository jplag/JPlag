package de.jplag.cli.logger;

import org.kohsuke.MetaInfServices;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

@MetaInfServices(SLF4JServiceProvider.class)
public class CollectedLoggerServiceProvider implements SLF4JServiceProvider {
    public static final String REQUESTED_API_VERSION = "2.0.99";
    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize() {
        this.loggerFactory = new CollectedLoggerFactory();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new NOPMDCAdapter();
    }
}
