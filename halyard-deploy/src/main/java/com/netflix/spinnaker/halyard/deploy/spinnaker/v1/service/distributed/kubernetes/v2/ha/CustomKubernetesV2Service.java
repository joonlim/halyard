package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerMonitoringDaemonService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2Service;
import java.util.Optional;
import org.yaml.snakeyaml.Yaml;

public class CustomKubernetesV2Service<T> extends CustomSpinnakerService<T> implements
    KubernetesV2Service<T>  {
  KubernetesV2Service<T> baseKubernetesV2Service;

  public CustomKubernetesV2Service(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String customModeName, String customProfileContents) {
    super(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, customModeName, customProfileContents);

    if (!KubernetesV2Service.class.isInstance(baseService)) {
      throw new RuntimeException();
    }

    baseKubernetesV2Service = (KubernetesV2Service<T>) baseService;
  }

  public CustomKubernetesV2Service(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String customModeName) {
    this(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, customModeName, null);
  }

  // TODO: this doesn't apply to non-spring services
  @Override
  protected Optional<String> customProfileOutputPath(String profileName) {
    // TODO: fixme look at SpringService
    return Optional.empty();
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

  // @Override
  // public String getArtifactId(String deploymentName) {
  //   return baseKubernetesv2Service.getArtifactId(deploymentName);
  // }
}
