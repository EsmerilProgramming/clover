package org.esmerilprogramming.cloverx.server;

import freemarker.template.TemplateException;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.esmerilprogramming.cloverx.annotation.BeforeTranslate;
import org.esmerilprogramming.cloverx.annotation.Controller;
import org.esmerilprogramming.cloverx.annotation.Converter;
import org.esmerilprogramming.cloverx.annotation.Page;
import org.esmerilprogramming.cloverx.http.CloverXRequest;
import org.esmerilprogramming.cloverx.http.converter.GenericConverter;
import org.esmerilprogramming.cloverx.http.converter.ParametersConverter;
import org.esmerilprogramming.cloverx.view.ViewParser;
import org.jboss.logging.Logger;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class PathHandlerMounter {

  private static final Logger LOGGER = Logger.getLogger(PathHandlerMounter.class);

  public PathHandler mount(List<Class<? extends HttpHandler>> handlers) {

    PathHandler pathHandler = Handlers.path();
    try {
      for (Class<?> handlerClass : handlers) {
        Controller controllerAnnotation = handlerClass.getAnnotation(Controller.class);
        if (controllerAnnotation != null) {
          Constructor<?> constructor = handlerClass.getConstructor();
          constructor.setAccessible(true);
          mountMethods(pathHandler, handlerClass);
        }
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }

    return pathHandler;
  }

  protected PathHandler mountMethods(PathHandler pathHandler, final Class<?> handlerClass) {
    Controller controllerAnnotation = handlerClass.getAnnotation(Controller.class);

    Method[] methods = handlerClass.getMethods();

    final List<Method> beforeTranslationMethods = identifyBeforeTranslationMethod(methods);
    for (final Method method : methods) {
      Page methodPagePath = method.getAnnotation(Page.class);
      if (methodPagePath != null) {

        Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
        final String[] parameterNames = paranamer.lookupParameterNames(method);
        final String responseTemplate = methodPagePath.responseTemplate();

        HttpHandler h = new HttpHandler() {
          @Override
          public void handleRequest(final HttpServerExchange exchange) throws IOException {
            Object newInstance;
            try {
              newInstance = handlerClass.getConstructor().newInstance();
              try {
                Class<?>[] parameterTypes = method.getParameterTypes();
                CloverXRequest request = new CloverXRequest(exchange);
                //Call before methods
                for (Method method : beforeTranslationMethods) {
                  method.invoke(newInstance, request);
                }
                //Verify @Converter annotations in the methods
                identifyParameterConverters(method, parameterNames, request);
                
                ParametersConverter translator = new ParametersConverter();
                Object[] parameters =
                    translator.translateAllParameters(parameterNames, parameterTypes, request);
                method.invoke(newInstance, parameters);

                if (!Page.NO_TEMPLATE.equals(responseTemplate)) {
                  try {
                    String parsedTemplate =
                        new ViewParser().parse(request.getViewAttributes(), responseTemplate);
                    exchange.getResponseSender().send(parsedTemplate);
                  } catch (TemplateException e) {
                    LOGGER.error(e.getMessage());
                  }
                }
              } catch (IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException e) {
                LOGGER.error(e.getMessage());

              }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
              LOGGER.error(e1.getMessage());
            }
          }

        };

        for (String pageRoot : controllerAnnotation.path()) {
          for (String methodRoot : methodPagePath.value()) {
            pathHandler.addExactPath(pageRoot + "/" + methodRoot, h);
          }
        }

      }
    }

    return pathHandler;
  }
  
  /**
   * Verify the method annotations and the parameter methods annotations to find any @Conveter
   * if found will add the custom converter to the CloverXRequest to be used in the parameter conversion 
   */
  protected void identifyParameterConverters(Method method, String[] parameterNames , CloverXRequest request) throws InstantiationException, IllegalAccessException{
    
    Annotation[] annotations = method.getAnnotations();
    for (Annotation annotation : annotations) {
      if(annotation instanceof Converter){
        Converter c = (Converter) annotation;
        String paramName = c.paramName();
        if( "".equals(paramName) ){
          LOGGER.warn("No paramName specified, the @Converter annotation will be ignored,"
              + " when using @Converter to annotating a method you should specify"
              + " the parameter name that will be converted");
        }else{
          Class<? extends GenericConverter<?>> converterClass = c.value();
          request.addConverter(paramName, converterClass.newInstance() );
        }
      }
    }
    
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    for( int i = 0 ; i < parameterNames.length ; i++){
      String parameterName = parameterNames[i];
      Annotation[] ann = parameterAnnotations[i];
      for (Annotation annotation : ann) {
        if(annotation instanceof Converter){
          Class<? extends GenericConverter<?>> value = ((Converter) annotation).value();
          request.addConverter( parameterName ,  value.newInstance() );
        }
      }
    }
  }
  
  protected List<Method> identifyBeforeTranslationMethod(Method[] methods) {
    List<Method> beforeTranslationMethods = new ArrayList<>();
    for (Method method : methods) {
      List<Annotation> asList = Arrays.asList(method.getAnnotations());
      for (Annotation annotation : asList) {
        if (annotation instanceof BeforeTranslate) {
          beforeTranslationMethods.add(method);
        }
      }
    }
    return beforeTranslationMethods;
  }

  @SuppressWarnings("unchecked")
  protected <T> T setParamater(Class<T> clazz, String parameterName, CloverXRequest request) {
    if (String.class.equals(clazz)) {
      return (T) request.getParameter(parameterName);
    }
    return null;
  }
}
