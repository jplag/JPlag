package de.jplag.cli.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

import com.google.auto.service.AutoService;

/**
 * SLF4J service provider for integrating the CollectedLogger with SLF4J API.
 */
@AutoService(SLF4JServiceProvider.class)
public class CollectedLoggerServiceProvider implements SLF4JServiceProvider {

    /**
     * SLF4J API version requested by this provider.
     */
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
