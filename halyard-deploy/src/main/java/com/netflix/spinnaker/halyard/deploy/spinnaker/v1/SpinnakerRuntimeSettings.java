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
import com.netflix.spinnaker.halyard.core.problem.v1.Problem.Severity;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService.Type;
import java.util.Collections;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SpinnakerRuntimeSettings {
  protected Services services = new Services();

  // For serialization
  public SpinnakerRuntimeSettings() {}

  public static class Services extends HashMap<Type,ServiceSettings> {
    @Override
    public ServiceSettings get(Object key) {
      if (!Type.class.isInstance(key)) {
        throw new HalException(Severity.FATAL, "Key must be of type SpinnakerService.Type");
      }
      if (!this.containsKey(key)) {
        throw new HalException(Severity.FATAL, "Service " + Type.class.cast(key).getCanonicalName() + " does not exist");
      }
      return super.get(key);
    }
  }

  @JsonIgnore
  public Map<Type, ServiceSettings> getAllServiceSettings() {
    return Collections.unmodifiableMap(services);
  }

  public void setServiceSettings(Type type, ServiceSettings settings) {
    services.put(type, settings);
  }

  public ServiceSettings getServiceSettings(SpinnakerService service) {
    return getServiceSettings(service.getType());
  }

  private ServiceSettings getServiceSettings(Type type) {
    return services.get(type);
  }
}
