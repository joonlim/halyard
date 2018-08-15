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

import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ExtraProfileSpinnakerServiceWrapper;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerMonitoringDaemonService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.DeployPriority;
import java.util.function.BiFunction;

/**
 * Extend a KubernetesV2Service to add an extra profile.
 */
public class ExtraProfileKubernetesV2ServiceWrapper<T> extends
    ExtraProfileSpinnakerServiceWrapper<T> implements KubernetesV2Service<T> {
  private final KubernetesV2Service<T> baseKubernetesV2Service;

  public ExtraProfileKubernetesV2ServiceWrapper(SpinnakerService<T> baseService,  BiFunction<DeploymentConfiguration, SpinnakerRuntimeSettings, Profile> generateExtraProfile) {
    super(baseService, generateExtraProfile);

    if (!KubernetesV2Service.class.isInstance(baseService)) {
      throw new RuntimeException("Wrapped service must be a KubernetesV2Services instance");
    }

    this.baseKubernetesV2Service = KubernetesV2Service.class.cast(baseService);
  }

  @Override
  public String getArtifactId(String deploymentName) {
    return baseKubernetesV2Service.getArtifactId(deploymentName);
  }

  @Override
  public String getDockerRegistry(String deploymentName, SpinnakerArtifact artifact) {
    return baseKubernetesV2Service.getDockerRegistry(deploymentName, artifact);
  }

  @Override
  public ServiceSettings defaultServiceSettings() {
    return baseKubernetesV2Service.defaultServiceSettings();
  }

  @Override
  public SpinnakerMonitoringDaemonService getMonitoringDaemonService() {
    return baseKubernetesV2Service.getMonitoringDaemonService();
  }

  @Override
  public DeployPriority getDeployPriority() {
    return baseKubernetesV2Service.getDeployPriority();
  }

  @Override
  public boolean isEnabled(DeploymentConfiguration deploymentConfiguration) {
    return baseKubernetesV2Service.isEnabled(deploymentConfiguration);
  }

  // TODO(joonlim): Need to override this so that gate's address will be localhost
  // however, this is making it so that clouddriver-ro is pointing at clouddriver, not ro.
  @Override
  public ServiceSettings buildServiceSettings(DeploymentConfiguration deploymentConfiguration) {
    return baseKubernetesV2Service.buildServiceSettings(deploymentConfiguration);
  }
}
