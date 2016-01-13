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

package matt_richardson.teamCity.buildTriggers.octopusDeploy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Deployment {
  final String environmentId;
  Date latestDeployment;
  Date latestSuccessfulDeployment;

  public Deployment(String environmentId, Date latestDeployment, Date latestSuccessfulDeployment) {
    this.environmentId = environmentId;
    this.latestDeployment = latestDeployment;
    this.latestSuccessfulDeployment = latestSuccessfulDeployment;
  }

  public boolean isLatestDeploymentOlderThen(Date compareDate) {
    return this.latestDeployment.compareTo(compareDate) < 0;
  }

  public boolean isLatestSuccessfulDeploymentOlderThen(Date compareDate) {
    return this.latestSuccessfulDeployment.compareTo(compareDate) < 0;
  }

  public boolean isSuccessful() {
    Date testDate = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();

    return this.latestSuccessfulDeployment.compareTo(testDate) > 0;
  }

  @Override
  public String toString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(OctopusDeploymentsProvider.OCTOPUS_DATE_FORMAT);
    return String.format("%s;%s;%s", environmentId, dateFormat.format(latestDeployment), dateFormat.format(latestSuccessfulDeployment));
  }
}
