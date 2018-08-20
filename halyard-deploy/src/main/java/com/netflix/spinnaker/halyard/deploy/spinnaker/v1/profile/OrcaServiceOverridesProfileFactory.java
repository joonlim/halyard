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

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile;

import com.netflix.spinnaker.halyard.config.model.v1.ha.HaServices;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import org.springframework.stereotype.Component;

@Component
public class OrcaServiceOverridesProfileFactory extends ServiceOverridesProfileFactory {
  @Override
  protected ServiceOverridesConfig generateServiceOverrides(DeploymentConfiguration deploymentConfiguration) {
    ServiceOverridesConfig config = new ServiceOverridesConfig();

    HaServices haServices = deploymentConfiguration.getDeploymentEnvironment().getHaServices();
    if (haServices.getClouddriver().isEnabled()) {
      ClouddriverConfig clouddriverConfig = new ClouddriverConfig();
      clouddriverConfig.setBaseUrl("${services.clouddriver-ro.baseUrl:http://localhost:7002}");
      config.getServices().setClouddriver(clouddriverConfig);
    }
    if (haServices.getEcho().isEnabled()) {
      EchoConfig echoConfig = new EchoConfig();
      echoConfig.setBaseUrl("${services.echo-slave.baseUrl:http://localhost:8089}");
      config.getServices().setEcho(echoConfig);
    }
    return config;
  }

  @Override
  public SpinnakerArtifact getArtifact() {
    return SpinnakerArtifact.ORCA;
  }
}
