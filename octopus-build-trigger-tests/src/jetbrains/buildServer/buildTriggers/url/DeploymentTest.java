/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.buildTriggers.url;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Test
public class DeploymentTest {
  public void isLatestDeploymentOlderThenReturnsTrueWhenNewerDatePassed() {
    //new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime(
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 10).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), true);
  }

  public void isLatestDeploymentOlderThenReturnsFalseWhenOlderDatePassed() {
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), false);
  }

  public void isLatestDeploymentOlderThenReturnsFalseWhenSameDatePassed() {
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), false);
  }

  public void isLatestDeploymentOlderThenComparesAgainstLatestDeploymentDate() {
    Date testDate = new GregorianCalendar(2015, 12, 10).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, 12, 9).getTime(), new GregorianCalendar(2015, 12, 12).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), true);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsTrueWhenNewerDatePassed() {
    //new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime(
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 10).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), true);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenOlderDatePassed() {
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 8).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), false);
  }

  public void isLatestSuccessfulDeploymentOlderThenReturnsFalseWhenSameDatePassed() {
    Date testDate = new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 9).getTime());
    Assert.assertEquals(deployment.isLatestDeploymentOlderThen(testDate), false);
  }

  public void isLatestSuccessfulDeploymentOlderThenComparesAgainstLatestDeploymentDate() {
    Date testDate = new GregorianCalendar(2015, 12, 10).getTime();
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, 12, 12).getTime(), new GregorianCalendar(2015, 12, 9).getTime());
    Assert.assertEquals(deployment.isLatestSuccessfulDeploymentOlderThen(testDate), true);
  }

  public void toStringFormatsCorrectly() {
    Deployment deployment = new Deployment("env", new GregorianCalendar(2015, Calendar.DECEMBER, 9, 14, 10, 11).getTime(), new GregorianCalendar(2015, Calendar.DECEMBER, 12, 9, 4, 3).getTime());
    Assert.assertEquals(deployment.toString(), "env;2015-12-09T14:10:11.000Z;2015-12-12T09:04:03.000Z");
  }
}
