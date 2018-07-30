package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class CustomKubectlServiceProvider extends KubectlServiceProvider {
  @Data
  @EqualsAndHashCode(callSuper = false)
  static class CustomServices {
    List<CustomService> customServices;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  static class CustomService {
    String baseService;
    String role;
    CustomProfile customProfile;
  }


  @Data
  @EqualsAndHashCode(callSuper = false)
  static class CustomProfile {
    String name;
    String outputPath;
    String contents;
  }

  List<KubernetesV2Service> services = new ArrayList<>();

  @Autowired
  CustomRoleKubernetesV2ServiceFactory customRoleServiceFactory;

  @Autowired
  private HalconfigDirectoryStructure halconfigDirectoryStructure;

  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @PostConstruct
  public void postConstruct() throws Exception {
    Path customServicesPath = halconfigDirectoryStructure.getCustomServicesPath("default");
    Path haPath = customServicesPath.resolve("distributedha.yml");

    // TODO(): this should be done with config node logic

    CustomServices customServices = mapper.readValue(haPath.toFile(), CustomServices.class);

    for (CustomService customService : customServices.getCustomServices()) {
      SpinnakerService.Type type = SpinnakerService.Type.fromCanonicalName(customService.baseService);
      if (customService.getCustomProfile() != null) {
        if (type.isSidecar()) {
          ...
        } else {
          // check to make sure this service doesn't already exist in map
          services.add(customRoleServiceFactory.newInstance(type, customService.getRole(), customService.getCustomProfile().getName(), customService.getCustomProfile().getOutputPath(), customService.getCustomProfile().getContents()));

        }
      } else {
        if (type.isSidecar()) {
          ...
        } else {
          // check to make sure this service doesn't already exist in map
          services.add(customRoleServiceFactory.newInstance(type, customService.getRole()));
        }
      }
    }
  }

  @Override
  public List<SpinnakerService> getServices() {
    List<SpinnakerService> result = services.stream().map(s -> (SpinnakerService) s).collect(
        Collectors.toList());
    return result;
  }

  public List<KubernetesV2Service> getServicesByPriority(List<SpinnakerService.TypeAndRole> serviceTypesAndRoles) {
    List<KubernetesV2Service> result = new ArrayList<>(services);

    result.sort((a, b) -> {
      if (a.getService().getType() == SpinnakerService.Type.REDIS) {
        return -1;
      }
      if (b.getService().getType() == SpinnakerService.Type.REDIS) {
        return 1;
      }
      return 0;
    });

    return result;
  }

  public KubernetesV2Service getService(SpinnakerService.TypeAndRole typeAndRole) {
    // return getService(typeAndRole, Object.class);
    return services
        .stream()
        .filter(s -> s != null && typeAndRole.equals(s.getService().getTypeAndRole()))
        .findFirst()
        .get(); // TODO: check and throw if empty or > 1
  }
}
