package org.esmerilprogramming.cloverx.server;


import static io.undertow.Handlers.path;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;

import java.io.IOException;

import org.esmerilprogramming.cloverx.scanner.PackageScanner;
import org.esmerilprogramming.cloverx.scanner.ScannerResult;
import org.esmerilprogramming.cloverx.scanner.exception.PackageNotFoundException;
import org.jboss.logging.Logger;

public final class CloverX {

  private static final Logger LOGGER = Logger.getLogger(CloverX.class);

  private Undertow server;
  private static final InMemorySessionManager sessionManager;
  private static final SessionConfig sessionConfig;
  
  static {
    sessionManager = new InMemorySessionManager("CLOVERX");
    sessionConfig = new SessionCookieConfig();
  }
  
  public CloverX( CloverXConfiguration configuration ){
    start(configuration);
  }

  public CloverX() {
    this( new ConfigurationBuilder().defaultConfiguration() );
  }

  private void start( CloverXConfiguration configuration ) {
    LOGGER.info("ignition...");
    server = buildServer( configuration );
    server.start();
    LOGGER.info("Enjoy it! http://" + configuration.getHost()
        + ":" + configuration.getPort()
        + "/" + configuration.getAppContext() );
  }

  private Undertow buildServer( CloverXConfiguration configuration ) {
    
    return Undertow.builder()
        .addHttpListener( configuration.getPort() ,  configuration.getHost() )
        .setHandler(
            path()
            .addPrefixPath("/" + configuration.getAppContext() , createHandler())
            .addPrefixPath("/" + configuration.getStaticRootPath() , new ResourceHandlerMounter()
            .mount()))
        .build();
  }

  private PathHandler createHandler() {
    ScannerResult scan = scanPackagesForHandlers();
    if (!scan.getHandlers().isEmpty()) {
      PathHandlerMounter mounter = new PathHandlerMounter();
      return mounter.mount(scan.getHandlers());
    }
    return null;
  }

  private ScannerResult scanPackagesForHandlers() {
    ClassLoader classLoader = this.getClass().getClassLoader();
    ScannerResult scan = null;
    try {
      scan = new PackageScanner().scan("", classLoader);
    } catch (PackageNotFoundException | IOException e) {
      LOGGER.error(e.getMessage());
    }
    return scan;
  }

  public Undertow getServer() {
    return server;
  }

  public static void main(String[] args) {
    new CloverX(new ConfigurationBuilder()
    .withHost("127.0.0.1")
    .withPort(8080)
    .build());
  }

  public static InMemorySessionManager getSessionmanager() {
    return sessionManager;
  }

  public static SessionConfig getSessionconfig() {
    return sessionConfig;
  }

}
