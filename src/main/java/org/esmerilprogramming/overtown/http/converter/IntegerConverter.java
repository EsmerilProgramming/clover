package org.esmerilprogramming.overtown.http.converter;

import org.esmerilprogramming.overtown.http.OvertownRequest;
import org.jboss.logging.Logger;


/**
 * 
 * @author efraimgentil (efraim.gentil@gmail.com)
 */
public class IntegerConverter implements ParameterConverter {

  private static final Logger LOGGER = Logger.getLogger(IntegerConverter.class);

  @SuppressWarnings("unchecked")
  @Override
  public <T> T translate(Class<T> clazz, String parameterName, OvertownRequest cloverRequest) {
    Object attribute = cloverRequest.getParameter(parameterName);
    if (attribute != null) {
      String strVal = String.valueOf(attribute);

      try {
        Integer i = Integer.parseInt(strVal);
        return (T) i;
      } catch (NumberFormatException nfe) {
        LOGGER.error(nfe.getMessage());
      }
    }
    return null;
  }

}
