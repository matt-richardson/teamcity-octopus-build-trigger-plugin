package jetbrains.buildServer.buildTriggers.url;

import jetbrains.buildServer.BaseTestCase;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Date;

/**
 * @author Victory.Bedrosova
 * 11/22/13.
 */
@Test
public class DefaultResourceHashProviderTest extends BaseTestCase {
  public void testConnectionTimeout() throws Exception {
    final long before = new Date().getTime();
    try {
      createDefaultHashProvider().getResourceHash(TriggerParameters.create("http://www.google.com:81", "admin", "admin", 10));
    } catch (Throwable e) {
      final Collection<Throwable> causes = ResourceHashProviderTestUtil.getCauses(e);
      for (Throwable t : causes) {
        if (t instanceof SocketTimeoutException) {
          // expected
          return;
        }
      }
      System.out.println(causes);
      fail();
    }
    assertTrue(new Date().getTime() - before < 1000);
  }

  public void testHandleExe() throws Exception {
    assertEquals("2579df066d38a15be8142954a2633e7f", createDefaultHashProvider().getResourceHash(TriggerParameters.create("http://live.sysinternals.com/handle.exe")));
  }

  @NotNull
  private DefaultResourceHashProvider createDefaultHashProvider() {
    return new DefaultResourceHashProvider();
  }
}
