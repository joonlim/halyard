package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerMonitoringDaemonService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import lombok.experimental.Delegate;
import org.yaml.snakeyaml.Yaml;

public class CustomRoleKubernetesV2Service<T> extends CustomRoleSpinnakerService<T> implements KubernetesV2Service<T> {
  KubernetesV2Service<T> baseKubernetesv2Service;

  public CustomRoleKubernetesV2Service(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role, String customProfileName, String customProfileOutputDirectoryPath, String customProfileContents) {
    super(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, customProfileName, customProfileOutputDirectoryPath, customProfileContents);

    if (!KubernetesV2Service.class.isInstance(baseService)) {
      throw new RuntimeException();
    }

    baseKubernetesv2Service = (KubernetesV2Service<T>) baseService;
  }

  public CustomRoleKubernetesV2Service(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role) {
    this(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, null, null, null);
  }

  @Override
  public String getDockerRegistry(String deploymentName, SpinnakerArtifact artifact) {
    return baseKubernetesv2Service.getDockerRegistry(deploymentName, artifact);
  }

  @Override
  public ServiceSettings defaultServiceSettings() {
    return baseKubernetesv2Service.defaultServiceSettings();
  }

  @Override
  public SpinnakerMonitoringDaemonService getMonitoringDaemonService() {
    return baseKubernetesv2Service.getMonitoringDaemonService();
  }

  @Override
  public String getArtifactId(String deploymentName) {
    return baseKubernetesv2Service.getArtifactId(deploymentName);
  }
}
