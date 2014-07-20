package org.esmerilprogramming.cloverx.server;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.esmerilprogramming.cloverx.scanner.PackageScanner;
import org.esmerilprogramming.cloverx.scanner.ScannerResult;
import org.esmerilprogramming.cloverx.scanner.exception.PackageNotFoundException;
import org.jboss.logging.Logger;

public class CloverX {

  private static final Logger LOGGER = Logger.getLogger(CloverX.class);
  
  private String host = "localhost";
  private int port = 8080;

  public CloverX() {

  }

  public CloverX(int port) {
    this.port = port;
  }

  public CloverX(String host) {
    this.host = host;
  }

  public CloverX(int port, String host) {
    this.port = port;
    this.host = host;
  }

  private Undertow server;

  public void start() throws PackageNotFoundException, IOException, NoSuchMethodException,
      SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    ScannerResult scan = new PackageScanner().scan("", classLoader);

    Builder builder = Undertow.builder();
    builder.addHttpListener(port, host);

    if (!scan.getHandlers().isEmpty()) {
      PathHandlerMounter mounter = new PathHandlerMounter();
      builder.setHandler(mounter.mount(scan.getHandlers()));
    }

    server = builder.build();
    server.start();
    
    LOGGER.info("Enjoy it! http://" + host + ":" + port);
  }

  public Undertow getServer() {
    return server;
  }

  public void setServer(Undertow server) {
    this.server = server;
  }

  public static void main(String[] args) throws Exception {
    new CloverX().start();
  }

}
