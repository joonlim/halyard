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
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.OrcaServiceOverridesProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.OrcaService;
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
public class KubernetesV2OrcaService extends OrcaService implements KubernetesV2Service<OrcaService.Orca> {
  final DeployPriority deployPriority = new DeployPriority(1);

  @Delegate
  @Autowired
  KubernetesV2ServiceDelegate serviceDelegate;

  @Autowired
  OrcaServiceOverridesProfileFactory orcaServiceOverridesProfileFactory;

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration, SpinnakerRuntimeSettings endpoints) {
    List<Profile> profiles = super.getProfiles(deploymentConfiguration, endpoints);
    if (hasServiceOverrides(deploymentConfiguration)) {
      String filename = "orca-service-overrides.yml";
      String path = Paths.get(getConfigOutputPath(), filename).toString();
      profiles.add(orcaServiceOverridesProfileFactory.getProfile(filename, path, deploymentConfiguration, endpoints));
    }
    return profiles;
  }

  @Override
  public ServiceSettings defaultServiceSettings(DeploymentConfiguration deploymentConfiguration) {
    if (hasServiceOverrides(deploymentConfiguration)) {
      List<String> profiles = new ArrayList<>();
      profiles.add("service-overrides");
      profiles.add("test");
      profiles.add("local");
      return new Settings(profiles);
    }
    return new Settings();
  }

  private boolean hasServiceOverrides(DeploymentConfiguration deploymentConfiguration) {
    HaServices haServices = deploymentConfiguration.getDeploymentEnvironment().getHaServices();
    return haServices.getClouddriver().isEnabled() || haServices.getEcho().isEnabled();
  }
}
