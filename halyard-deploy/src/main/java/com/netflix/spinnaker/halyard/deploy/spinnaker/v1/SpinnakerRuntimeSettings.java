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
import com.netflix.spinnaker.halyard.core.error.v1.HalException;
import com.netflix.spinnaker.halyard.core.problem.v1.Problem;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import java.util.Collections;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class SpinnakerRuntimeSettings {
  // protected Services services = new Services();
  protected Map<SpinnakerService.TypeAndRole, ServiceSettings> services = new HashMap<>();

  // For serialization
  public SpinnakerRuntimeSettings() {}

  @JsonIgnore
  public Map<SpinnakerService.TypeAndRole, ServiceSettings> getAllServiceSettings() {
    return Collections.unmodifiableMap(services);
  }

  public void setServiceSettings(SpinnakerService.TypeAndRole typeAndRole, ServiceSettings settings) {
    services.put(typeAndRole, settings);
  }

  public ServiceSettings getServiceSettings(SpinnakerService.TypeAndRole serviceTypeAndRole) {
    if (!services.containsKey(serviceTypeAndRole)) {
      throw new HalException(Problem.Severity.FATAL, "Service with type and role does not exist"); // TODO
    }
    return services.get(serviceTypeAndRole);
  }

  public ServiceSettings getServiceSettings(SpinnakerService service) {
    return getServiceSettings(service.getTypeAndRole());
  }
  //
  // private ServiceSettings getServiceSettings(String name) {
  //   Field serviceField = getServiceField(name);
  //   serviceField.setAccessible(true);
  //   try {
  //     return (ServiceSettings) serviceField.get(services);
  //   } catch (IllegalAccessException e) {
  //     throw new HalException(Problem.Severity.FATAL, "Can't access service field for " + name + ": " + e.getMessage());
  //   } finally {
  //     serviceField.setAccessible(false);
  //   }
  // }

  // private Field getServiceField(String name) {
  //   String reducedName = name.replace("-", "").replace("_", "");
  //
  //   Optional<Field> matchingField = Arrays.stream(Services.class.getDeclaredFields())
  //       .filter(f -> f.getName().equalsIgnoreCase(reducedName))
  //       .findFirst();
  //
  //   return matchingField.orElseThrow(() -> new HalException(Problem.Severity.FATAL, "Unknown service " + reducedName));
  // }
}
