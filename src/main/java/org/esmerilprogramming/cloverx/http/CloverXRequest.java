package org.esmerilprogramming.cloverx.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;

import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.esmerilprogramming.cloverx.http.converter.GenericConverter;
import org.esmerilprogramming.cloverx.http.converter.ParameterConverter;
import org.esmerilprogramming.cloverx.view.ViewAttributes;
import org.jboss.logging.Logger;

/**
 * @author efraimgentil (efraim.gentil@gmail.com)
 */
public class CloverXRequest {
  
  private static final Logger LOGGER = Logger.getLogger(CloverXRequest.class);

  private HttpServerExchange exchange;
  private CloverXSessionManager sessionManager;
  private Map<String, Deque<String>> queryParameters;
  private Map<String, GenericConverter<?>> parameterConverters;
  private FormData formData;
  private ViewAttributes viewAttributes;
  private Response response;

  public CloverXRequest() {}

  public CloverXRequest(HttpServerExchange exchange) {
    this.exchange = exchange;
    this.queryParameters = exchange.getQueryParameters();
    this.parameterConverters = new HashMap<>();
    this.viewAttributes = new ViewAttributes();
    this.sessionManager = CloverXSessionManager.getInstance(); 
    if (isPostRequest()) {
      FormDataParser create = new FormEncodedDataDefinition().create(exchange);
      try {
        formData =  create != null ? create.parseBlocking() : null;
      } catch (IOException ioe) {
        LOGGER.error(ioe.getMessage());
      }
    }
  }

  public CloverXSession getSession(){
    return sessionManager.getSession( exchange ); 
  }
  
  public CloverXSession createSession(){
    return sessionManager.createNewSession(exchange);
  }
  
  public <T> void addAttribute(String name , T value ){
    viewAttributes.add(name, value);
  }
  
  public ViewAttributes getViewAttributes(){
    return viewAttributes;
  }
  
  public Object getParameter(String name) {
    Object value = null;
    if (isPostRequest()) {
      value = getFromFormData(name);
    }
    if (value == null) {
      value = getFromParameters(name);
    }
    return value;
  }

  protected Object getFromFormData(String name) {
    if (formData != null) {
      Deque<FormValue> dequeVal = formData.get(name);
      if (dequeVal != null) {
        return dequeVal.getLast().getValue();
      }
    }
    return null;
  }

  protected Object getFromParameters(String parameterName) {
    Deque<String> deque = queryParameters.get(parameterName);
    if (deque != null) {
      return deque.getLast();
    }
    return null;
  }

  public boolean containsAttributeStartingWith(String parameterName) {
    boolean contains = false;
    Set<String> keySet = queryParameters.keySet();
    String parameterPrefix = parameterName + ".";
    
    if (keySet.contains(parameterPrefix)) {
      contains = true;
    }
    if (isPostRequest() && contains == false && formData != null) {
      Iterator<String> iterator = formData.iterator();
      while (iterator.hasNext()) {
        String next = iterator.next();
        if (next.contains(parameterPrefix)) {
          contains = true;
          break;
        }
      }
    }
    return contains;

  }

  protected boolean isPostRequest() {
    return "POST".equalsIgnoreCase(exchange.getRequestMethod().toString())
            || "PUT".equalsIgnoreCase(exchange.getRequestMethod().toString())
            || "DELETE".equalsIgnoreCase(exchange.getRequestMethod().toString());
  }

  public HttpServerExchange getExchange() {
    return exchange;
  }

  public void addConverter(String parameterName , GenericConverter<?> converter){
    parameterConverters.put(parameterName, converter);
  }
  
  public void addConverters( Map<String, GenericConverter<?>> converterMaps ){
    parameterConverters.putAll(converterMaps);
  }
  
  public void setConverters( Map<String, GenericConverter<?>> converterMaps ){
    parameterConverters = converterMaps;
  }

  public boolean shouldConvertParameter(String parameterName) {
    return parameterConverters.containsKey(parameterName);
  }

  public ParameterConverter getTranslator(String parameterName) {
    return parameterConverters.get(parameterName);
  }

  protected void setQueryParameters(Map<String, Deque<String>> queryParameters) {
    this.queryParameters = queryParameters;
  }

  public void respondAsHttp() {
    if(response == null || !(response instanceof HttpResponse ) ){
      response = new HttpResponse( exchange , viewAttributes );
    }
  }
  
  public void respondAsJson() {
    if(response == null || !(response instanceof JsonResponse )){
      response = new JsonResponse( exchange , viewAttributes );
    }
  }

  public Response getResponse() {
    if(response == null){
      respondAsHttp();
    }
    return response;
  }
  
  public JsonResponse getJsonResponse(){
    if(response == null || !(response instanceof JsonResponse )){
      respondAsJson();
    }
    return (JsonResponse) response;
  }

  public Object getHttpMethod() {
    return exchange.getRequestMethod().toString();
  }
}