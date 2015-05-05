package org.esmerilprogramming.overtownacceptance.controllers;

import io.undertow.io.Sender;
import org.esmerilprogramming.overtown.annotation.Controller;
import org.esmerilprogramming.overtown.annotation.path.Get;
import org.esmerilprogramming.overtown.annotation.path.Post;
import org.esmerilprogramming.overtown.annotation.path.Put;
import org.esmerilprogramming.overtown.http.OvertownRequest;

/**
 * Created by efraimgentil<efraimgentil@gmail.com> on 14/03/15.
 */
@Controller
public class IndexController {

  @Get
  public void index(OvertownRequest request){
    Sender sender = request.getExchange().getResponseSender();
    sender.send("GET - index/index");
  }

  @Get(template = "index.ftl")
  public void indexWithTemplate(OvertownRequest request){

  }

  @Get(template = "/index.ftl")
  public void indexWithRootTemplate(OvertownRequest request){

  }

  @Post
  public void index(String name , OvertownRequest request){
    Sender sender = request.getExchange().getResponseSender();
    sender.send("POST - index/index - nome:" + name);
  }

  @Put
  public void put(String name , OvertownRequest request){
    Sender sender = request.getExchange().getResponseSender();
    sender.send("PUT - index/put - nome:" + name);
  }


}
