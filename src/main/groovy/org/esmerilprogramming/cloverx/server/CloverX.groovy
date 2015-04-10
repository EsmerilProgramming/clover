package org.esmerilprogramming.cloverx.server

import io.undertow.Undertow
import java.io.IOException
import javax.servlet.ServletException

import org.esmerilprogramming.cloverx.server.handlers.StartupHandler
import org.esmerilprogramming.cloverx.server.handlers.StartupHandlerImpl
import org.jboss.logging.Logger

final class CloverX {

  private static final Logger LOGGER = Logger.getLogger(CloverX)
  private Undertow server

  CloverX(CloverXConfiguration configuration) {
    try {
      start(configuration)
    } catch(RuntimeException e) {
      LOGGER.error("Error on startup")
      LOGGER.error(e.getMessage())
      e.printStackTrace()
    }
  }

  CloverX() {
    this(new ConfigurationBuilder().defaultConfiguration())
  }

  private void start(CloverXConfiguration config) throws RuntimeException {
    LOGGER.info("ignition...")
    try {
      server = buildServer(config)
    } catch (any) {
      e.printStackTrace()
    }
    server.start()
    LOGGER.info("Enjoy it! http://" + config.host
        + ":" + config.port
        + "/" + config.appContext)
  }

  void stop() {
    server.stop()
  }

  private Undertow buildServer(CloverXConfiguration config) throws ServletException, IOException {
    StartupHandler startupHandler = new StartupHandlerImpl()
    return startupHandler.prepareBuild(config)
  }

  Undertow getServer() {
    server
  }

  static main(args) {
    new CloverX(new ConfigurationBuilder()
      .withPackageToScan("org.esmerilprogramming.cloverx.management").shouldRunManagement(true)
      .withHost("0.0.0.0")
      .withPort(8080)
      .withMaxSessionTime(1)
      .build())
  }

}
