package org.esmerilprogramming.cloverx.server.injection;

import org.esmerilprogramming.cloverx.http.CloverXRequest;

public class ViewAttributesInjector implements CoreInjector {

  @SuppressWarnings("unchecked")
  @Override
  public <T> T inject(Class<T> clazz, String parameterName, CloverXRequest cloverRequest) {
    return (T) cloverRequest.getViewAttributes();
  }

}
