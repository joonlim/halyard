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
  String roleProfileOutputDirectoryPath;
  String roleProfileContents;

  public CustomRoleSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role, String roleProfileOutputDirectoryPath, String roleProfileContents) {
    super(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure);
    this.baseService = baseService;
    this.role = role;
    this.roleProfileOutputDirectoryPath = roleProfileOutputDirectoryPath;
    this.roleProfileContents = roleProfileContents;
  }

  public CustomRoleSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String role) {
    this(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, null, null);
  }

  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration,
      SpinnakerRuntimeSettings endpoints) {
    List<Profile> profiles = baseService.getProfiles(deploymentConfiguration, endpoints);

    // Add role's profile
    if (roleProfileOutputDirectoryPath != null && roleProfileContents != null) {
      Profile roleProfile = new Profile(getRoleProfileName(),
          getArtifactService().getArtifactVersion(deploymentConfiguration.getName(), getArtifact()),
          Paths.get(roleProfileOutputDirectoryPath, getRoleProfileName()).toString(),
          roleProfileContents);
      roleProfile.appendContents(roleProfile.getBaseContents());
      roleProfile.getEnv().put("SPRING_PROFILES_ACTIVE", role);

      profiles.add(roleProfile);
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

  private String getRoleProfileName() {
    return this.getCanonicalName() + ".yml";
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
