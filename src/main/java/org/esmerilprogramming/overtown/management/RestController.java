package org.esmerilprogramming.overtown.management;

import org.esmerilprogramming.overtown.annotation.Controller;
import org.esmerilprogramming.overtown.annotation.path.Get;
import org.esmerilprogramming.overtown.annotation.path.Path;
import org.esmerilprogramming.overtown.annotation.path.Post;
import org.esmerilprogramming.overtown.http.OvertownRequest;

@Controller
public class RestController {

  public RestController() {}

  @Get( value ="form/" ,template = "form.ftl")
  public void form(){
    System.out.println("With slash");
  }

  @Post( value ="form/" ,template = "form.ftl")
  public void postForm(){
    System.out.println("Post With slash");
  }

  /*@Get( value ="form" ,template = "rest/form.ftl")
  public void formM(){
    System.out.println("SAME BUT WHITOUT SLASH");
  }*/

  @Post(template = "result.ftl")
  public void form(String message , OvertownRequest request){
    request.addAttribute("message" , message);
  }

  @Path(template = "rest/form.ftl")
  public void getandpost(String message , OvertownRequest request){
    request.addAttribute("message" , message);
    System.out.println("message = " + message);
  }

}

