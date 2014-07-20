package org.esmerilprogramming.cloverx.http.converter;

import org.esmerilprogramming.cloverx.http.CloverXRequest;

/**
 * 
 * @author efraimgentil (efraim.gentil@gmail.com)
 */
public class StringConverter implements ParameterConverter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T translate(Class<T> clazz, String parameterName,
			CloverXRequest cloverRequest) {
		Object attribute = cloverRequest.getParameter(parameterName);
		if(attribute == null)
			return null;
		
		return (T) String.valueOf(attribute);
	}
	
}