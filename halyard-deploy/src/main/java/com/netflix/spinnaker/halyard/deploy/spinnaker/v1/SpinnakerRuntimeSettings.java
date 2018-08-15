/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.CaseFormat;
import com.netflix.spinnaker.halyard.core.error.v1.HalException;
import com.netflix.spinnaker.halyard.core.problem.v1.Problem;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import java.util.Collections;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SpinnakerRuntimeSettings {
  protected Map<String, ServiceSettings> services = new HashMap<>();

  @JsonIgnore
  public Map<String, ServiceSettings> getAllServiceSettings() {
    return Collections.unmodifiableMap(services);
  }

  public void setServiceSettings(String serviceName, ServiceSettings settings) {
    services.put(snakeCaseToCamelCase(serviceName), settings);
  }

  public ServiceSettings getServiceSettings(SpinnakerService service) {
    return getServiceSettings(service.getCanonicalName());
  }

  public ServiceSettings getServiceSettings(SpinnakerService.Type serviceType) {
    return getServiceSettings(serviceType.getCanonicalName());
  }

  public ServiceSettings getServiceSettings(String serviceName) {
    String reducedName = snakeCaseToCamelCase(serviceName);
    if (!services.containsKey(reducedName)) {
      throw new HalException(Problem.Severity.FATAL, "Service " + serviceName + " does not exist");
    }
    return services.get(reducedName);
  }

  private static String snakeCaseToCamelCase(String string) {
    return string; // TODO
    // return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string.replaceAll("-", "_"));
  }
}
