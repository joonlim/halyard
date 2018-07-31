package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.spinnaker.halyard.config.config.v1.HalconfigDirectoryStructure;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.config.model.v1.providers.kubernetes.KubernetesAccount;
import com.netflix.spinnaker.halyard.core.RemoteAction;
import com.netflix.spinnaker.halyard.core.error.v1.HalException;
import com.netflix.spinnaker.halyard.core.problem.v1.Problem;
import com.netflix.spinnaker.halyard.core.problem.v1.Problem.Severity;
import com.netflix.spinnaker.halyard.deploy.deployment.v1.AccountDeploymentDetails;
import com.netflix.spinnaker.halyard.deploy.deployment.v1.DeploymentDetails;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService.Type;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerServiceProvider;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubectlServiceProvider;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ClouddriverService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2DeckService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2EchoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2FiatService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2Front50Service;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2GateService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2IgorService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2KayentaService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2MonitoringDaemonService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2OrcaService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2RedisService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2RoscoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2Service;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class HaKubectlServiceProviderFactory {

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  ArtifactService artifactService;
  @Autowired
  Yaml yamlParser;
  @Autowired
  HalconfigDirectoryStructure halconfigDirectoryStructure;

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

  public SpinnakerServiceProvider<AccountDeploymentDetails<KubernetesAccount>> create(List<Type> haServices, DeploymentConfiguration deploymentConfiguration) {
    Map<Type, KubernetesV2Service> services = new HashMap<>();

    // Clouddriver TODO
    services.put(Type.CLOUDDRIVER, clouddriverService);

    // Deck TODO
    services.put(Type.DECK, deckService);

    // Echo TODO
    services.put(Type.ECHO, echoService);

    // Fiat TODO
    services.put(Type.FIAT, fiatService);

    // Front50 TODO
    services.put(Type.FRONT50, front50Service);

    // Gate
    addGateService(services, haServices, deploymentConfiguration));

    // Igor


  }

  private KubernetesV2Service addGateService(Map<Type, KubernetesV2Service> services, List<Type> haServices, DeploymentConfiguration deploymentConfiguration) {
    String gateCustomProfile = "";
    if (haServices.contains(Type.GATE)) {
      KubernetesV2Service redisGateService = newCustomRedisService("gate");
      services.pu

      gateCustomProfile.concat("\n"
          + "redis:\n"
          + "  connection: ${services.redisGate.baseUrl}\n");
    }
    if (haServices.contains(Type.CLOUDDRIVER)) {
      gateCustomProfile.concat("\n"
          + "clouddriver:\n"
          + "  baseUrl: ${services.clouddriverRo.baseUrl}\n");
    }
    if ()
  }

  private KubernetesV2Service newCustomRedisService(String customModeName) {
    return new CustomKubernetesV2Service(objectMapper, artifactService, yamlParser, halconfigDirectoryStructure, redisService, customModeName);
  }

  @Slf4j
  private static class MapBackedKubectlServiceProvider extends KubectlServiceProvider {
    private final Map<Type, KubernetesV2Service> services;

    public MapBackedKubectlServiceProvider(Map<Type, KubernetesV2Service> services) {
      this.services = new HashMap<>(services);
    }

    @Override
    public List<SpinnakerService> getServices() {
      return services.values().stream()
          .map(s -> SpinnakerService.class.cast(s))
          .collect(Collectors.toList());
    }

    @Override
    public List<KubernetesV2Service> getServicesByPriority(List<SpinnakerService.Type> serviceTypes) {
      List<KubernetesV2Service> result = new ArrayList<>();
      for (SpinnakerService.Type type : serviceTypes) {
        if (services.containsKey(type)) {
          result.add(services.get(type));
        }
      }
      result.sort((a, b) -> {
        // Prioritize Redis services
        if (a.getService().getArtifact() == SpinnakerArtifact.REDIS) {
          return -1;
        }
        if (b.getService().getArtifact() == SpinnakerArtifact.REDIS) {
          return 1;
        }
        return 0;
      });
      return result;
    }

    @Override
    public <S> KubernetesV2Service getService(SpinnakerService.Type type, Class<S> clazz) {
      return services.get(type);
    }
  }

}
