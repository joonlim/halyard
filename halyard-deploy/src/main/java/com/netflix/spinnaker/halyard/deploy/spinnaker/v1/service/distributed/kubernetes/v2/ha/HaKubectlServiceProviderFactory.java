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
import com.netflix.spinnaker.halyard.config.model.v1.ha.HaServices;
import com.netflix.spinnaker.halyard.config.model.v1.providers.kubernetes.KubernetesAccount;
import com.netflix.spinnaker.halyard.deploy.deployment.v1.AccountDeploymentDetails;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService.Type;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerServiceProvider;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubectlServiceProvider;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ClouddriverCachingService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ClouddriverRoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ClouddriverRwService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ClouddriverService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2DeckService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2EchoSchedulerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2EchoService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2EchoSlaveService;
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
  KubernetesV2ClouddriverCachingService clouddriverCachingService;

  @Autowired
  KubernetesV2ClouddriverRoService clouddriverRoService;

  @Autowired
  KubernetesV2ClouddriverRwService clouddriverRwService;

  @Autowired
  KubernetesV2DeckService deckService;

  @Autowired
  KubernetesV2EchoService echoService;

  @Autowired
  KubernetesV2EchoSchedulerService echoSchedulerService;

  @Autowired
  KubernetesV2EchoSlaveService echoSlaveService;

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

  public SpinnakerServiceProvider<AccountDeploymentDetails<KubernetesAccount>> create(HaServices haServices) {
    Map<Type,KubernetesV2Service> services = new HashMap<>();

    if (haServices.getClouddriver().isEnabled()) {
      services.put(Type.CLOUDDRIVER_CACHING, clouddriverCachingService);
      services.put(Type.CLOUDDRIVER_RO, clouddriverRoService);
      services.put(Type.CLOUDDRIVER_RW, clouddriverRwService);
    } else {
      services.put(Type.CLOUDDRIVER, clouddriverService);
    }
    services.put(Type.DECK, deckService);
    if (haServices.getEcho().isEnabled()) {
      services.put(Type.ECHO_SCHEDULER, echoSchedulerService);
      services.put(Type.ECHO_SLAVE, echoSlaveService);
    } else {
      services.put(Type.ECHO, echoService);
    }
    services.put(Type.FIAT, fiatService);
    services.put(Type.FRONT50, front50Service);
    services.put(Type.GATE, gateService);
    services.put(Type.IGOR, igorService);
    services.put(Type.KAYENTA, kayentaService);
    services.put(Type.MONITORING_DAEMON, monitoringDaemonService);
    services.put(Type.ORCA, orcaService);
    services.put(Type.REDIS, redisService);
    services.put(Type.ROSCO, roscoService);

    return new MapBackedKubectlServiceProvider(services);
  }

  private static class MapBackedKubectlServiceProvider extends KubectlServiceProvider {

    private final Map<Type,KubernetesV2Service> services;

    public MapBackedKubectlServiceProvider(Map<Type,KubernetesV2Service> services) {
      this.services = new HashMap<>(services);
    }

    @Override
    public List<SpinnakerService> getServices() {
      return services.values().stream()
          .map(s -> SpinnakerService.class.cast(s))
          .collect(Collectors.toList());
    }

    @Override
    public List<KubernetesV2Service> getServicesByPriority(List<Type> serviceTypes) {
      List<KubernetesV2Service> result = services.values().stream()
          .filter(d -> serviceTypes.contains(d.getService().getType()))
          .sorted((d1, d2) -> d2.getDeployPriority().compareTo(d1.getDeployPriority()))
          .collect(Collectors.toList());
      return result;
    }

    public KubernetesV2Service getService(Type type) {
      return services.get(type);
    }

    public <S> KubernetesV2Service getService(Type type, Class<S> clazz) {
      return getService(type);
    }
  }
}
