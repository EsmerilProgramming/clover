package org.esmerilprogramming.overtownacceptance;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.esmerilprogramming.overtownacceptance.main.MainApp;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by efraimgentil<efraimgentil@gmail.com> on 15/03/15.
 */
public class PutRoutesAcceptance {

  WebDriver webDriver;
  static MainApp mainApp;

  @BeforeClass
  public static void initClass(){
    mainApp = new MainApp();
    mainApp.start();
  }

  @AfterClass
  public static void finish(){
    mainApp.stop();
  }

  @Before
  public void initTest(){
    webDriver = new FirefoxDriver();
  }

  @After
  public void endTest(){
    webDriver.quit();
  }

  @Test
  public void doesCallPutAndSendGetRequestWithHiddenMethodInformation(){
    webDriver.get("localhost:8080/acceptance/index/indexWithTemplate");
    WebElement name = webDriver.findElement(By.id("putName"));
    name.sendKeys("efraim");
    webDriver.findElement(By.id("putSubmit")).click();

    String pageSource = webDriver.getPageSource();
    System.out.println( pageSource );
    assertTrue(  pageSource.contains("PUT - index/put - nome:efraim") );
  }

  @Test
  public void doesCallPutAndSendPostRequestWithHiddenMethodInformation(){
    webDriver.get("localhost:8080/acceptance/index/indexWithTemplate");
    WebElement name = webDriver.findElement(By.id("putPostName"));
    name.sendKeys("efraim");
    webDriver.findElement(By.id("putPostSubmit")).click();

    String pageSource = webDriver.getPageSource();
    System.out.println( pageSource );
    assertTrue(  pageSource.contains("PUT - index/put - nome:efraim") );
  }

  @Test
  public void doesCorrectRespondToARequestUsingPutMethod() throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut("http://localhost:8080/acceptance/index/put");
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add( new BasicNameValuePair("name", "efraim") );
    httpPut.setEntity( new UrlEncodedFormEntity(nvps , HTTP.UTF_8 ) );

    HttpResponse response = client.execute(httpPut);
    BufferedReader br = new BufferedReader(
            new InputStreamReader((response.getEntity().getContent())));
    String output;
    StringBuilder sb = new StringBuilder();
    while ((output = br.readLine()) != null) {
      sb.append( output );
    }
    assertTrue( sb.toString().contains("PUT - index/put - nome:efraim")  );
    client.close();
  }

}
