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
import com.netflix.spinnaker.halyard.config.model.v1.providers.kubernetes.KubernetesAccount;
import com.netflix.spinnaker.halyard.deploy.deployment.v1.AccountDeploymentDetails;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HaKubectlServiceProviderFactory {
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
  KubernetesV2PersistentRedisForGateService redisForGateService;

  @Autowired
  KubernetesV2RoscoService roscoService;

  public SpinnakerServiceProvider<AccountDeploymentDetails<KubernetesAccount>> create(List<HaServiceType> haServices) {
    Map<Type, KubernetesV2Service> services = new HashMap<>();

    addClouddriverServices(services, haServices);
    addDeckServices(services, haServices);
    addEchoServices(services, haServices);
    addFiatServices(services, haServices);
    addFront50Services(services, haServices);
    addGateServices(services, haServices);
    addIgorServices(services, haServices);
    addKayentaServices(services, haServices);
    addMonitoringDaemonServices(services, haServices);
    addOrcaServices(services, haServices);
    addRedisServices(services, haServices);
    addRoscoServices(services, haServices);

    return new MapBackedKubectlServiceProvider(services);
  }

  private void addClouddriverServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Clouddriver
    services.put(Type.CLOUDDRIVER, clouddriverService);
  }

  private void addDeckServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(Type.DECK, deckService);
  }

  private void addEchoServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Echo
    services.put(Type.ECHO, echoService);
  }

  private void addFiatServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Fiat
    // Additional Fiat profile for HA Clouddriver - point to ClouddriverRo
    services.put(Type.FIAT, fiatService);
  }

  private void addFront50Services(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Front50
    services.put(Type.FRONT50, front50Service);
  }

  private void addGateServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    String gateProfileContents = "";
    if (haServices.contains(HaServiceType.GATE)) {
      services.put(Type.REDIS_FOR_GATE, redisForGateService);
      gateProfileContents = gateProfileContents.concat("\n"
          + "redis:\n"
          + "  connection: ${services.redisForGate.baseUrl}\n");
    }
    if (haServices.contains(HaServiceType.CLOUDDRIVER)) {
      gateProfileContents = gateProfileContents.concat("\n"
          + "clouddriver:\n"
          + "  baseUrl: ${services.clouddriverRo.baseUrl}\n");
    }

    if (gateProfileContents.isEmpty()) {
      services.put(Type.GATE, gateService);
    } else {
      services.put(Type.GATE, gateService.withAdditionalProfile("ha", gateProfileContents));
    }
  }

  private void addIgorServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Igor
    // Additional Igor profile for HA Echo - point to EchoSlave
    services.put(Type.IGOR, igorService);
  }

  private void addKayentaServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Kayenta
    services.put(Type.KAYENTA, kayentaService);
  }

  private void addMonitoringDaemonServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    services.put(Type.MONITORING_DAEMON, monitoringDaemonService);
  }

  private void addOrcaServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Orca
    // Additional Igor profile for HA Echo - point to EchoSlave
    // Additional Igor profile for HA Clouddriver - point to ClouddriverRw
    services.put(Type.IGOR, igorService);
  }

  private void addRedisServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    if (!haServices.containsAll(Arrays.asList(HaServiceType.values()))) {
      services.put(Type.REDIS, redisService);
    }
    // TODO(joonlim): Test no Redis
  }

  private void addRoscoServices(Map<Type, KubernetesV2Service> services, List<HaServiceType> haServices) {
    // TODO(joonlim): Issue 2934 - Implement HA Rosco
    services.put(Type.ROSCO, roscoService);
  }

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
      List<KubernetesV2Service> result = services.values().stream()
          .filter(d -> serviceTypes.contains(d.getService().getType()))
          .collect(Collectors.toList());

      result.sort((d1, d2) -> d2.getDeployPriority().compareTo(d1.getDeployPriority()));
      return result;
    }

    @Override
    public <S> KubernetesV2Service getService(SpinnakerService.Type type, Class<S> clazz) {
      return services.get(type);
    }
  }
}
