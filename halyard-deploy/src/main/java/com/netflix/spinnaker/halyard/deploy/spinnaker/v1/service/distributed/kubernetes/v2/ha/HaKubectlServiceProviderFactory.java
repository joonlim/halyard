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

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.ha;

import com.netflix.spinnaker.halyard.config.model.v1.ha.HaService.HaServiceType;
import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.config.model.v1.providers.kubernetes.KubernetesAccount;
import com.netflix.spinnaker.halyard.deploy.deployment.v1.AccountDeploymentDetails;
import com.netflix.spinnaker.halyard.deploy.services.v1.ArtifactService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.Profile;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.ProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile.StringBackedProfileFactory;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ClouddriverService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.EchoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.FiatService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.GateService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.IgorService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.OrcaService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.RoscoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerServiceProvider;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.CustomNameKubernetesV2ServiceWrapper;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.ExtraProfileKubernetesV2ServiceWrapper;
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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HaKubectlServiceProviderFactory {
  private static final String SPRING_CONFIG_OUTPUT_PATH = "/opt/spinnaker/config/";
  private static final String STRING_ACTIVE_PROFILES_KEY = "SPRING_PROFILES_ACTIVE";

  @Autowired
  KubernetesV2ClouddriverService baseClouddriverService;

  @Autowired
  KubernetesV2DeckService baseDeckService;

  @Autowired
  KubernetesV2EchoService baseEchoService;

  @Autowired
  KubernetesV2FiatService baseFiatService;

  @Autowired
  KubernetesV2Front50Service baseFront50Service;

  @Autowired
  KubernetesV2GateService baseGateService;

  @Autowired
  KubernetesV2IgorService baseIgorService;

  @Autowired
  KubernetesV2KayentaService baseKayentaService;

  @Autowired
  KubernetesV2MonitoringDaemonService baseMonitoringDaemonService;

  @Autowired
  KubernetesV2OrcaService baseOrcaService;

  @Autowired
  KubernetesV2RedisService baseRedisService;

  @Autowired
  KubernetesV2RoscoService baseRoscoService;

  public SpinnakerServiceProvider<AccountDeploymentDetails<KubernetesAccount>> create(List<HaServiceType> haServices) {
    Map<String,KubernetesV2Service> services = new HashMap<>();

    addClouddriver(services, haServices);
    addDeck(services, haServices);
    addEcho(services, haServices);
    addFiat(services, haServices);
    addFront50(services, haServices);
    addGate(services, haServices);
    addIgor(services, haServices);
    addKayenta(services, haServices);
    addMonitoringDaemon(services, haServices);
    addOrca(services, haServices);
    addRedis(services, haServices);
    addRosco(services, haServices);

    return new MapBackedKubectlServiceProvider(services);
  }

  private void addClouddriver(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER)) {
      services.put(baseClouddriverService.getCanonicalName(), baseClouddriverService);
      return;
    }

    String clouddriverRoExtraProfileContents = ""
        + "server:\n"
        + "  port: ${services.clouddriver-ro.port:7002}\n"
        + "  address: ${services.clouddriver-ro.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis.baseUrl:redis://localhost:6379}\n" // TODO(joonlim): Update to services.redis-slave-clouddriver.baseUrl
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: false\n"
        + "  writeEnabled: false\n";
    KubernetesV2Service<ClouddriverService.Clouddriver> clouddriverRoService = serviceWithExtraProfileAndCustomName(baseClouddriverService, "ro", clouddriverRoExtraProfileContents);
    services.put(clouddriverRoService.getCanonicalName(), clouddriverRoService);

    String clouddriverRwExtraProfileContents = ""
        + "server:\n"
        + "  port: ${services.clouddriver-rw.port:7002}\n"
        + "  address: ${services.clouddriver-rw.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis.baseUrl:redis://localhost:6379}\n" // TODO(joonlim): Update to services.redis-master-clouddriver.baseUrl
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: false\n"
        + "  writeEnabled: false\n";
    KubernetesV2Service<ClouddriverService.Clouddriver> clouddriverRwService = serviceWithExtraProfileAndCustomName(baseClouddriverService, "rw", clouddriverRwExtraProfileContents);
    services.put(clouddriverRwService.getCanonicalName(), clouddriverRwService);

    String clouddriverCachingExtraProfileContents = ""
        + "server:\n"
        + "  port: ${services.clouddriver-caching.port:7002}\n"
        + "  address: ${services.clouddriver-caching.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis.baseUrl:redis://localhost:6379}\n" // TODO(joonlim): Update to services.redis-master-clouddriver.baseUrl
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: true\n"
        + "  writeEnabled: true\n";
    KubernetesV2Service<ClouddriverService.Clouddriver> clouddriverCachingService = serviceWithExtraProfileAndCustomName(baseClouddriverService, "caching", clouddriverCachingExtraProfileContents);
    services.put(clouddriverCachingService.getCanonicalName(), clouddriverCachingService);

    // TODO(joonlim): Setup redis-master-clouddriver and redis-slave-clouddriver
  }

  private void addDeck(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(baseDeckService.getCanonicalName(), baseDeckService);
  }

  private void addEcho(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.ECHO)) {
      services.put(baseEchoService.getCanonicalName(), baseEchoService);
      return;
    }

    String echoSchedulerExtraProfileContents = ""
        + "server:\n"
        + "  port: ${services.echo-scheduler.port:8089}\n"
        + "  address: ${services.echo-scheduler.host:localhost}\n"
        + "\n"
        + "scheduler:\n"
        + "  enabled: true\n"
        + "  threadPoolSize: 20\n"
        + "  triggeringEnabled: true\n"
        + "  pipelineConfigsPoller:\n"
        + "    enabled: true\n"
        + "    pollingIntervalMs: 30000\n"
        + "  cron:\n"
        + "    timezone: ${global.spinnaker.timezone:America/Los_Angeles}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis.baseUrl:redis://localhost:6379}\n"; // TODO(joonlim): Update to services.redis-echo-scheduler.baseUrl
    KubernetesV2Service<EchoService.Echo> echoSchedulerService = serviceWithExtraProfileAndCustomName(baseEchoService, "scheduler", echoSchedulerExtraProfileContents);
    services.put(echoSchedulerService.getCanonicalName(), echoSchedulerService);

    String echoSlaveExtraProfileContents = ""
        + "server:\n"
        + "  port: ${services.echo-slave.port:8089}\n"
        + "  address: ${services.echo-slave.host:localhost}\n"
        + "\n"
        + "scheduler:\n"
        + "  enabled: false\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis.baseUrl:redis://localhost:6379}\n"; // TODO(joonlim): Update to services.redis-echo-slave.baseUrl
    KubernetesV2Service<EchoService.Echo> echoSlaveService = serviceWithExtraProfileAndCustomName(baseEchoService, "slave", echoSlaveExtraProfileContents);
    services.put(echoSlaveService.getCanonicalName(), echoSlaveService);

    // TODO(joonlim): Setup redis-echo-scheduler and redis-echo-slave
  }

  private void addFiat(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER)) {
      services.put(baseFiatService.getCanonicalName(), baseFiatService);
      return;
    }

    String fiatExtraProfileContents = ""
        + "services:\n"
        + "  clouddriver:\n"
        + "    baseUrl: ${services.clouddriver-ro.baseUrl:http://localhost:7002}\n"; //TODO why this not working
    KubernetesV2Service<FiatService.Fiat> fiatService = serviceWithExtraProfile(baseFiatService, "ha", fiatExtraProfileContents);
    services.put(fiatService.getCanonicalName(), fiatService);
  }

  private void addFront50(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(baseFront50Service.getCanonicalName(), baseFront50Service);
  }

  private void addGate(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER)) {
      services.put(baseGateService.getCanonicalName(), baseGateService);
      return;
    }

    String gateExtraProfileContents = ""
        + "services:\n"
        + "  clouddriver:\n"
        + "    baseUrl: ${services.clouddriver-ro.baseUrl:http://localhost:7002}\n";
    KubernetesV2Service<GateService.Gate> gateService = serviceWithExtraProfile(baseGateService, "ha", gateExtraProfileContents);
    services.put(gateService.getCanonicalName(), gateService);
  }

  private void addIgor(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER) && !haServices.contains(HaServiceType.ECHO)) {
      services.put(baseIgorService.getCanonicalName(), baseIgorService);
      return;
    }

    String igorExtraProfileContents = ""
        + "services:\n";
    if (haServices.contains(HaServiceType.CLOUDDRIVER)) {
      igorExtraProfileContents += ""
          + "  clouddriver:\n"
          + "    baseUrl: ${services.clouddriver-ro.baseUrl:http://localhost:7002}\n";
    }
    if (haServices.contains(HaServiceType.ECHO)) {
      igorExtraProfileContents += ""
          + "  echo:\n"
          + "    baseUrl: ${services.echo-slave.baseUrl:http://localhost:8089}\n";
    }

    KubernetesV2Service<IgorService.Igor> igorService = serviceWithExtraProfile(baseIgorService, "ha", igorExtraProfileContents);
    services.put(igorService.getCanonicalName(), igorService);
  }

  private void addKayenta(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(baseKayentaService.getCanonicalName(), baseKayentaService);
  }

  private void addMonitoringDaemon(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(baseMonitoringDaemonService.getCanonicalName(), baseMonitoringDaemonService);
  }

  private void addOrca(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER) && !haServices.contains(HaServiceType.ECHO)) {
      services.put(baseOrcaService.getCanonicalName(), baseOrcaService);
      return;
    }

    String orcaExtraProfileContents = "";
    if (haServices.contains(HaServiceType.CLOUDDRIVER)) {
      orcaExtraProfileContents += ""
          + "oort:\n"
          + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n"
          + "\n"
          + "mort:\n"
          + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n"
          + "\n"
          + "kato:\n"
          + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n";
    }
    if (haServices.contains(HaServiceType.ECHO)) {
      orcaExtraProfileContents += ""
          + "echo:\n"
          + "  baseUrl: ${services.echo-slave.baseUrl:http://localhost:8089}\n";
    }

    KubernetesV2Service<OrcaService.Orca> orcaService = serviceWithExtraProfile(baseOrcaService, "ha", orcaExtraProfileContents);
    services.put(orcaService.getCanonicalName(), orcaService);
  }

  private void addRedis(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(baseRedisService.getCanonicalName(), baseRedisService);
  }

  private void addRosco(Map<String,KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.contains(HaServiceType.CLOUDDRIVER)) {
      services.put(baseRoscoService.getCanonicalName(), baseRoscoService);
      return;
    }

    String roscoExtraProfileContents = ""
        + "services:\n"
        + "  clouddriver:\n"
        + "    baseUrl: ${services.clouddriver-ro.baseUrl:http://localhost:7002}\n";
    KubernetesV2Service<RoscoService.Rosco> roscoService = serviceWithExtraProfile(baseRoscoService, "ha", roscoExtraProfileContents);
    services.put(roscoService.getCanonicalName(), roscoService);
  }

  private <T> KubernetesV2Service<T> serviceWithExtraProfile(SpinnakerService<T> baseService, String extraProfileName, String extraProfileContents) {
    String extraProfileFileName = baseService.getArtifact().getName() + "-" + extraProfileName + ".yml";
    ProfileFactory extraProfileFactory = new StringBackedProfileFactory() {
      @Override
      protected void setProfile(Profile profile, DeploymentConfiguration deploymentConfiguration, SpinnakerRuntimeSettings endpoints) {
        profile.appendContents(profile.getBaseContents());
      }

      @Override
      protected ArtifactService getArtifactService() { return baseService.getArtifactService(); }

      @Override
      public SpinnakerArtifact getArtifact() { return baseService.getArtifact(); }

      @Override
      protected String commentPrefix() { return "## "; }

      @Override
      protected String getRawBaseProfile() { return extraProfileContents; }
    };

    return new ExtraProfileKubernetesV2ServiceWrapper<>(baseService, (deploymentConfiguration, endpoints) -> {
      Profile extraProfile = extraProfileFactory.getProfile(
          extraProfileFileName,
          Paths.get(SPRING_CONFIG_OUTPUT_PATH, extraProfileFileName).toString(),
          deploymentConfiguration,
          endpoints);
      extraProfile.getEnv()
          .put(STRING_ACTIVE_PROFILES_KEY,
              String.join(",",
                  fullProfileName(baseService, "local"),
                  fullProfileName(baseService, "test"),
                  extraProfileName));
      return extraProfile;
    });
  }

  private <T> KubernetesV2Service<T> serviceWithExtraProfileAndCustomName(SpinnakerService<T> baseService, String extraProfileNameAndCustomNameSuffix, String extraProfileContents) {
    String extraProfileName = extraProfileNameAndCustomNameSuffix;
    String customNameSuffix = extraProfileNameAndCustomNameSuffix;

    String customName = baseService.getArtifact().getName() + "-" + customNameSuffix;
    String extraProfileFileName = customName + ".yml";

    return serviceWithExtraProfile(new CustomNameKubernetesV2ServiceWrapper<>(baseService, customNameSuffix, profileName -> {
      if (profileName.equals(extraProfileFileName) || profileName.startsWith(customName + "-") || profileName.startsWith("spinnaker")) {
        return Optional.of(Paths.get(SPRING_CONFIG_OUTPUT_PATH, profileName).toString());
      } else {
        return Optional.empty();
      }
    }), extraProfileName, extraProfileContents);
  }

  private <T> KubernetesV2Service<T> serviceWithCustomName(SpinnakerService<T> baseService, String customNameSuffix) {
    // return new CustomNameKubernetesV2ServiceWrapper<T>(baseService, customNameSuffix, baseService::customProfileOutputPath);
    return new CustomNameKubernetesV2ServiceWrapper<>(baseService, customNameSuffix, profileName -> Optional.empty());
  }
  /**
   * E.g.,
   * If canonical name is "clouddriver" and profileName is "test", result will be "test".
   * If canonical name is "clouddriver-ro" and profileName is "test", result will be "ro-test".
   */
  private <T> String fullProfileName(SpinnakerService<T> service, String profileName) {
    return service.getCanonicalName().equals(service.getArtifact().getName()) ?
        profileName :
        service.getCanonicalName().substring(1 + service.getArtifact().getName().length()) + "-" + profileName;

  }
  private static class MapBackedKubectlServiceProvider extends KubectlServiceProvider {
    private final Map<String, KubernetesV2Service> services;

    public MapBackedKubectlServiceProvider(Map<String, KubernetesV2Service> services) {
      this.services = new HashMap<>(services);
    }

    @Override
    public List<SpinnakerService> getServices() {
      return services.values().stream()
          .map(s -> SpinnakerService.class.cast(s))
          .collect(Collectors.toList());
    }

    @Override
    public List<KubernetesV2Service> getServicesByPriority(List<String> serviceNames) {
      List<KubernetesV2Service> result = services.values().stream()
          .filter(d -> serviceNames.contains(d.getCanonicalName()))
          .collect(Collectors.toList());
      result.sort((d1, d2) -> d2.getDeployPriority().compareTo(d1.getDeployPriority()));
      return result;
    }

    @Override
    public <S> KubernetesV2Service getService(String serviceName, Class<S> clazz) {
      return services.get(serviceName);
    }
  }
}
