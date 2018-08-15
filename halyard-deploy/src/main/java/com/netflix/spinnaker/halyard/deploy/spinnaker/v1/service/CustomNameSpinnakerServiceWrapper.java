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

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service;

import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Extend a SpinnakerService to set a custom name.
 */
public abstract class CustomNameSpinnakerServiceWrapper<T> extends SpinnakerService<T> {
  private final SpinnakerService<T> baseService;
  private final String customName;
  private final Function<String, Optional<String>> customProfileOutputPath;

  public CustomNameSpinnakerServiceWrapper(SpinnakerService<T> baseService, String customNameSuffix, Function<String, Optional<String>> customProfileOutputPath) {
    super(baseService.getObjectMapper(), baseService.getArtifactService(), baseService.getYamlParser(), baseService.getHalconfigDirectoryStructure());
    this.baseService = baseService;
    this.customName = baseService.getCanonicalName() + "-" + customNameSuffix;
    this.customProfileOutputPath = customProfileOutputPath;
  }

  @Override
  public String getCanonicalName() {
    return customName;
  }

  @Override
  public String getServiceName() {
    return "spin-" + customName;
  }

  @Override
  protected Optional<String> customProfileOutputPath(String profileName) {
    return customProfileOutputPath.apply(profileName);
  }

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration, SpinnakerRuntimeSettings endpoints) {
    return baseService.getProfiles(deploymentConfiguration, endpoints);
  }

  @Override
  public Type getType() {
    return baseService.getType();
  }

  @Override
  public Class<T> getEndpointClass() {
    return baseService.getEndpointClass();
  }

  @Override
  public SpinnakerArtifact getArtifact() {
    return baseService.getArtifact();
  }
}
