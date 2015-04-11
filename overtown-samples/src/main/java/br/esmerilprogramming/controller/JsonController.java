package br.esmerilprogramming.controller;

import br.esmerilprogramming.model.Car;
import br.esmerilprogramming.model.Contact;
import org.esmerilprogramming.overtown.annotation.Controller;
import org.esmerilprogramming.overtown.annotation.JSONResponse;
import org.esmerilprogramming.overtown.annotation.Page;
import org.esmerilprogramming.overtown.http.CloverXRequest;
import org.esmerilprogramming.overtown.http.JsonResponse;

/**
 * Created by efraimgentil on 19/01/15.
 */
@Controller
public class JsonController {

  @Page(value = "form",responseTemplate = "json.ftl")
  public void form(){

  }

  @JSONResponse(rootAttribute = "")
  @Page(value="toJson")
  public void toJson(Contact contact , CloverXRequest request){
    System.out.println(contact);
    request.addAttribute("contact" , contact );
  }

  @Page("car")
  public void json(JsonResponse response){
    Car car = new Car(2004 , "Honda Fit");
    response.addAttribute("car" , car );
  }

}
