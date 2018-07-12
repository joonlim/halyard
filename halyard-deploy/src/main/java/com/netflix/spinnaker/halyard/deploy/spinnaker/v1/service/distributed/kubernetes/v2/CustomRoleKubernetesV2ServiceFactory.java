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
  KubernetesV2ClouddriverService clouddriverService;

  @Autowired
  KubernetesV2DeckService deckService;

  @Autowired
  KubernetesV2EchoService echoService;

  @Autowired
  KubernetesV2FiatService fiatService;

  @Autowired
  KubernetesV2Front50Service front50Service;

  @Autowired
  KubernetesV2GateService gateService;

  @Autowired
  KubernetesV2IgorService igorService;

  @Autowired
  KubernetesV2KayentaService kayentaService;

  @Autowired
  KubernetesV2MonitoringDaemonService monitoringDaemonService;

  @Autowired
  KubernetesV2OrcaService orcaService;

  @Autowired
  KubernetesV2RedisService redisService;

  @Autowired
  KubernetesV2RoscoService roscoService;




  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ArtifactService artifactService;

  @Autowired
  Yaml yamlParser;

  @Autowired
  HalconfigDirectoryStructure halconfigDirectoryStructure;

  public <T> CustomRoleKubernetesV2Service<T> newInstance(SpinnakerService.Type type, String role, String customProfile, String customProfileOutputDirectoryPath, String customProfileContents) {
    SpinnakerService baseService;
    switch (type) {
      case CLOUDDRIVER:
        baseService = clouddriverService;
        break;
      case DECK:
        baseService = deckService;
        break;
      case ECHO:
        baseService = echoService;
        break;
      case FIAT:
        baseService = fiatService;
        break;
      case FRONT50:
        baseService = front50Service;
        break;
      case GATE:
        baseService = gateService;
        break;
      case IGOR:
        baseService = igorService;
        break;
      case KAYENTA:
        baseService = kayentaService;
        break;
      case MONITORING_DAEMON:
        baseService = monitoringDaemonService;
        break;
      case ORCA:
        baseService = orcaService;
        break;
      case REDIS:
        baseService = redisService;
        break;
      case ROSCO:
        baseService = roscoService;
        break;
      default:
        throw new RuntimeException();
    }
    return new CustomRoleKubernetesV2Service(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, baseService, role, customProfile, customProfileOutputDirectoryPath, customProfileContents);
  }


  public <T> CustomRoleKubernetesV2Service<T> newInstance(SpinnakerService.Type type, String role) {
    return newInstance(type, role, null, null, null);
  }
}
