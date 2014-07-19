package org.esmerilprogramming.clover.http.converter;

import org.esmerilprogramming.clover.http.CloverRequest;

/**
 * @author efraimgentil (efraim.gentil@gmail.com)
 */
public class EmptyParamConverter implements ParameterConverter {
	
	@Override
	public <T> T translate(Class<T> clazz, String parameterName,
			CloverRequest cloverRequest) {
		return null;
	}

}
