package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class CustomRoleKubernetesV2ServiceFactory {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ArtifactService artifactService;

  @Autowired
  Yaml yamlParser;

  @Autowired
  HalconfigDirectoryStructure halconfigDirectoryStructure;

  public <T> CustomRoleKubernetesV2Service<T> newInstance(SpinnakerService<T> baseService, String role, String roleProfileOutputDirectoryPath, String roleProfileContents) {
    return new CustomRoleKubernetesV2Service(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, roleProfileOutputDirectoryPath, roleProfileContents);
  }
}
