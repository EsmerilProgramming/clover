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

import org.esmerilprogramming.cloverx.http.converter.ParameterConverter;
import org.esmerilprogramming.cloverx.view.ViewAttributes;
import org.jboss.logging.Logger;

/**
 * @author efraimgentil (efraim.gentil@gmail.com)
 */
public class CloverXRequest {
  
  private static final Logger LOGGER = Logger.getLogger(CloverXRequest.class);

  private HttpServerExchange exchange;
  private Map<String, Deque<String>> queryParameters;
  private Map<String, ParameterConverter> parameterConverters;
  private FormData formData;
  private ViewAttributes viewAttributes;

  public CloverXRequest() {}

  public CloverXRequest(HttpServerExchange exchange) {
    this.exchange = exchange;
    this.queryParameters = exchange.getQueryParameters();
    this.parameterConverters = new HashMap<>();
    this.viewAttributes = new ViewAttributes();
    if (isPostRequest()) {
      FormDataParser create = new FormEncodedDataDefinition().create(exchange);
      try {
        formData = create.parseBlocking();
      } catch (IOException ioe) {
        LOGGER.error(ioe.getMessage());
      }
    }
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

  public boolean containsAttributeStartingWith(String parameterName) {
    boolean contains = false;
    Set<String> keySet = queryParameters.keySet();
    String parameterPrefix = parameterName + ".";
    
    if (keySet.contains(parameterPrefix)) {
      contains = true;
    }
    if (isPostRequest() && contains == false) {
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
    return "POST".equalsIgnoreCase(exchange.getRequestMethod().toString());
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

  public HttpServerExchange getExchange() {
    return exchange;
  }

  public void addConverter(String parameterName , ParameterConverter converter){
    parameterConverters.put(parameterName, converter);
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

}