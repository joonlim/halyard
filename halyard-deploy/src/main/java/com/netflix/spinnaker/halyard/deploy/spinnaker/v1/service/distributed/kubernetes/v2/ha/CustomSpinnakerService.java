package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.ha;

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

// TODO: move to better package
public abstract class CustomSpinnakerService<T> extends SpinnakerService<T> {
  // Backed by a SpinnakerService delegate
  // Add custom profile and profile name
  // make sure profile name is: not empty, trimmed, not "local"

  // Allows services of the same type
  // e.g., clouddriver/ro


  // HaServiceFactory sees which services we want in HA mode (e.g., gate and clouddriver) and it'll create custom spinnaker services based on that.

  SpinnakerService<T> baseService;

  String customModeName;
  String customProfileContents;

  public CustomSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String customModeName, String customProfileContents) {
    super(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure);
    this.baseService = baseService;
    this.customModeName = customModeName;
    this.customProfileContents = customProfileContents;
  }

  public CustomSpinnakerService(ObjectMapper objectMapper, ArtifactService artifactService, Yaml yamlParser, HalconfigDirectoryStructure halconfigDirectoryStructure, SpinnakerService<T> baseService, String customModeName) {
    this(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, customModeName, null);
  }

  // TODO: should this be separate for deck and redis?
  @Override
  public List<Profile> getProfiles(DeploymentConfiguration deploymentConfiguration,
      SpinnakerRuntimeSettings endpoints) {
    List<Profile> profiles = baseService.getProfiles(deploymentConfiguration, endpoints);

    if (this.customProfileContents != null) {
      Optional<Profile> mainProfile = profiles.stream()
          .filter(p -> p.getName().equals(baseService.getCanonicalName() + ".yml"))
          .findFirst();

      if (mainProfile.isPresent()) {
        mainProfile.get()
            .appendContents("# custom mode service\n")
            .appendContents(customProfileContents);
      }
    }
    //
    // // Add role's profile
    // if (customProfileName != null && customProfileOutputDirectoryPath != null && customProfileContents != null) {
    //   Profile customProfile = new Profile(getCustomProfileName(),
    //       getArtifactService().getArtifactVersion(deploymentConfiguration.getName(), getArtifact()),
    //       Paths.get(customProfileOutputDirectoryPath, getCustomProfileName()).toString(),
    //       customProfileContents);
    //   customProfile.appendContents(customProfile.getBaseContents());
    //   customProfile.getEnv().put("SPRING_PROFILES_ACTIVE", customProfileName);
    //
    //   profiles.add(customProfile);
    // }

    return profiles;
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
