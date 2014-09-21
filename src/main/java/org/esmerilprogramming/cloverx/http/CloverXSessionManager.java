package org.esmerilprogramming.cloverx.http;

import org.esmerilprogramming.cloverx.scanner.ScannerResult;
import org.esmerilprogramming.cloverx.server.CloverXConfiguration;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;

public class CloverXSessionManager {
  
  private final InMemorySessionManager sessionManager;
  private final SessionConfig sessionConfig;
  
  private static CloverXSessionManager manager;
  
  public static CloverXSessionManager getInstance(){
    if( manager == null){
      manager = new CloverXSessionManager();
    }
    return manager;
  }
  
  private CloverXSessionManager() {
    sessionManager = new InMemorySessionManager("CLOVERX");
    sessionConfig = new SessionCookieConfig();
  }
  
  private Session getUndertowSession(HttpServerExchange exchange){
    return getSessionManager().getSession(exchange, getSessionConfig() );
  }
  
  public CloverXSession createNewSession(HttpServerExchange exchange ){
    Session undertowSession = getUndertowSession(exchange);
    if(undertowSession != null ){
      undertowSession.invalidate(exchange);
    }
    return getSession(exchange);
  }
  
  public CloverXSession getSession(HttpServerExchange exchange){
    Session session = getUndertowSession(exchange);
    if(session == null){
      session = getSessionManager().createSession(exchange, getSessionConfig() ); 
    }
    return new CloverXSession(exchange, session);
  }
  
  //TODO
  //Needs exchange to operate in the session
  public CloverXSession getSessionById(String id , HttpServerExchange exchange ){
    Session session = getSessionManager().getSession(id);
    if(session != null){
      return new CloverXSession( exchange , session );
    }else{
      return null;
    }
  }

  public SessionConfig getSessionConfig() {
    return sessionConfig;
  }

  public InMemorySessionManager getSessionManager() {
    return sessionManager;
  }
  
}
