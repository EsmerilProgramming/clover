package org.esmerilprogramming.cloverx.scanner;

import static org.junit.Assert.*;
import io.undertow.server.HttpHandler;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.esmerilprogramming.cloverx.scanner.PackageScanner;
import org.esmerilprogramming.cloverx.scanner.ScannerResult;
import org.esmerilprogramming.cloverx.scanner.exception.PackageNotFoundException;
import org.esmerilprogramming.cloverx.scanner.testpackage.First;
import org.esmerilprogramming.cloverx.scanner.testpackage.Second;
import org.esmerilprogramming.cloverx.scanner.testpackage.Third;
import org.esmerilprogramming.cloverx.scanner.testpackage.subpack.AnotherSeverEndpoint;
import org.esmerilprogramming.cloverx.scanner.testpackage.subpack.Fifth;
import org.esmerilprogramming.cloverx.scanner.testpackage.subpack.Fourth;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PackageScannerTest {

  private PackageScanner scanner;

  @Before
  public void setUp() {
    scanner = new PackageScanner();
  }

  @Test(expected = PackageNotFoundException.class)
  public void givenANonExistentPackageShouldThrowException() throws PackageNotFoundException,
      IOException {
    ClassLoader classLoader = PackageScanner.class.getClassLoader();

    scanner.scan("com.wrong.package", classLoader);
  }
  
  @Test
  public void givenAPackagedShouldFindAllServerEndpointAnnotatedClassesInThisPackageAndSubPackages()
      throws PackageNotFoundException, IOException {
    ClassLoader classLoader = PackageScanner.class.getClassLoader();

    ScannerResult pageClasses =
        scanner.scan("org.esmerilprogramming.cloverx.scanner.testpackage", classLoader);

    List<Class<?>> handlers = pageClasses.getServerEndpoints();
    assertSame(2, handlers.size());
    assertTrue("Should have found the Second.class", handlers.contains(Second.class));
    assertTrue("Should have found the AnotherSeverEndpoint.class", handlers.contains(AnotherSeverEndpoint.class));
  }
  
  @Test
  public void givenAPackagedShouldFindAllControllerAnnotatedClassesInThisPackageAndSubPackages()
      throws PackageNotFoundException, IOException {
    ClassLoader classLoader = PackageScanner.class.getClassLoader();

    ScannerResult pageClasses =
        scanner.scan("org.esmerilprogramming.cloverx.scanner.testpackage", classLoader);

    List<Class<?>> handlers = pageClasses.getHandlers();
    assertSame(2, handlers.size());
    assertTrue("Should have found the First.class", handlers.contains(First.class));
    assertTrue("Should have found the Fifth.class", handlers.contains(Fifth.class));
  }

  @Test
  public void givenAPackagedShouldFindAllHttpServletClassesInThisPackageAndSubPackages()
      throws PackageNotFoundException, IOException {
    ClassLoader classLoader = PackageScanner.class.getClassLoader();

    ScannerResult pageClasses =
        scanner.scan("org.esmerilprogramming.cloverx.scanner.testpackage", classLoader);

    List<Class<? extends HttpServlet>> servlets = pageClasses.getServlets();
    assertSame(2, servlets.size());
    assertTrue("Should have found the Third.class", servlets.contains(Third.class));
    assertTrue("Should have found the Fourth.class", servlets.contains(Fourth.class));
  }

}
