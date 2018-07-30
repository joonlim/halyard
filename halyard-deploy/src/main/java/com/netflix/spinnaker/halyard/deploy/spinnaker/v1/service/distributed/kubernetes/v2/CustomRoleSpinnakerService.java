package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.yaml.snakeyaml.Yaml;

public abstract class CustomRoleSpinnakerService<T> extends SpinnakerService<T> {
  // @Delegate(excludes = ExcludeDelegate.class)
  SpinnakerService<T> baseService;

  String role;
  String customProfileName;
  String customProfileOutputDirectoryPath;
  String customProfileContents;

  public CustomRoleSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role, String customProfileName, String customProfileOutputDirectoryPath, String customProfileContents) {
    super(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure);
    this.baseService = baseService;
    this.role = role == null ? DEFAULT_ROLE : role;
    this.customProfileName = customProfileName;
    this.customProfileOutputDirectoryPath = customProfileOutputDirectoryPath;
    this.customProfileContents = customProfileContents;
  }

  public CustomRoleSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role) {
    this(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, null, null, null);
  }

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration,
      SpinnakerRuntimeSettings endpoints) {
    List<Profile> profiles = baseService.getProfiles(deploymentConfiguration, endpoints);

    // Add role's profile
    if (customProfileName != null && customProfileOutputDirectoryPath != null && customProfileContents != null) {
      Profile customProfile = new Profile(getCustomProfileName(),
          getArtifactService().getArtifactVersion(deploymentConfiguration.getName(), getArtifact()),
          Paths.get(customProfileOutputDirectoryPath, getCustomProfileName()).toString(),
          customProfileContents);
      customProfile.appendContents(customProfile.getBaseContents());
      customProfile.getEnv().put("SPRING_PROFILES_ACTIVE", customProfileName);

      profiles.add(customProfile);
    }

    return profiles;
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public Type getType() {
    return baseService.getType();
  }

  @Override
  public Class<T> getEndpointClass() {
    return baseService.getEndpointClass();
  }

  private String getCustomProfileName() {
    if (customProfileName == null) {
      throw new RuntimeException();
    }
    return this.getType().getCanonicalName() + "-" + customProfileName + ".yml";
  }

  @Override
  protected Optional<String> customProfileOutputPath(String profileName) {
    return Optional.empty();
  }

  @Override
  public SpinnakerArtifact getArtifact() {
    return baseService.getArtifact();
  }


  // Should delegate


  // private interface ExcludeDelegate {
  //   List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration,
  //       SpinnakerRuntimeSettings endpoints);
  //
  //   String getRole();
  // }

}
