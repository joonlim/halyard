/*
 * Copyright 2018 Google, Inc.
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
 *
 *
 */

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.netflix.spinnaker.halyard.config.model.v1.ha.HaServices;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.GateServiceOverridesProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.GateService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.DeployPriority;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
@EqualsAndHashCode(callSuper = true)
public class KubernetesV2GateService extends GateService implements KubernetesV2Service<GateService.Gate> {
  final DeployPriority deployPriority = new DeployPriority(0);

  @Delegate
  @Autowired
  KubernetesV2ServiceDelegate serviceDelegate;

  @Autowired
  GateServiceOverridesProfileFactory gateServiceOverridesProfileFactory;

  @Override
  public ServiceSettings defaultServiceSettings(DeploymentConfiguration deploymentConfiguration) {
    if (hasServiceOverrides(deploymentConfiguration)) {
      List<String> profiles = new ArrayList<>();
      profiles.add("service-overrides");
      profiles.add("test");
      profiles.add("local");
      return new Settings(deploymentConfiguration.getSecurity().getApiSecurity(), profiles);
    }
    return new Settings(deploymentConfiguration.getSecurity().getApiSecurity());
  }

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration, SpinnakerRuntimeSettings endpoints) {
    List<Profile> profiles = super.getProfiles(deploymentConfiguration, endpoints);
    if (hasServiceOverrides(deploymentConfiguration)) {
      String filename = "gate-service-overrides.yml";
      String path = Paths.get(getConfigOutputPath(), filename).toString();
      profiles.add(gateServiceOverridesProfileFactory.getProfile(filename, path, deploymentConfiguration, endpoints));
    }
    return profiles;
  }

  private boolean hasServiceOverrides(DeploymentConfiguration deploymentConfiguration) {
    HaServices haServices = deploymentConfiguration.getDeploymentEnvironment().getHaServices();
    return haServices.getClouddriver().isEnabled() || haServices.getEcho().isEnabled();
  }
}
