package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2;

import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpinnakerService.Type;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.SpringService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HaKubectlServiceProvider extends KubectlServiceProvider {
  List<KubernetesV2Service> services = new ArrayList<>();

  @Autowired
  CustomRoleKubernetesV2ServiceFactory customRoleServiceFactory;

  @PostConstruct
  public void postConstruct() throws Exception {
    String contents = "\n"
        + "server:\n"
        + "  port: ${services.clouddriver-caching.port:7002}\n"
        + "  address: ${services.clouddriver-caching.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-clouddriver.baseUrl:redis://localhost:6379}\n"
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: true\n"
        + "  writeEnabled: true\n";
    services.add(customRoleServiceFactory
        .newInstance(Type.CLOUDDRIVER, "caching", "caching", SpringService.getSpringServiceConfigOutputPath(), contents));

    contents = "\n"
        + "server:\n"
        + "  port: ${services.clouddriver-ro.port:7002}\n"
        + "  address: ${services.clouddriver-ro.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-clouddriver-slave.baseUrl:redis://localhost:6379}\n"
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: false\n"
        + "  writeEnabled: false\n";
    services.add(customRoleServiceFactory.newInstance(Type.CLOUDDRIVER, "ro", "ro", SpringService.getSpringServiceConfigOutputPath(), contents));

    contents = "\n"
        + "server:\n"
        + "  port: ${services.clouddriver-rw.port:7002}\n"
        + "  address: ${services.clouddriver-rw.host:localhost}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-clouddriver.baseUrl:redis://localhost:6379}\n"
        + "\n"
        + "caching:\n"
        + "  redis:\n"
        + "    hashingEnabled: false\n"
        + "  writeEnabled: false\n";
    services.add(customRoleServiceFactory.newInstance(Type.CLOUDDRIVER, "rw", "rw", SpringService.getSpringServiceConfigOutputPath(), contents));

    services.add(customRoleServiceFactory.newInstance(Type.DECK, SpinnakerService.DEFAULT_ROLE));

    contents = "server:\n"
        + "  port: ${services.echo-cron.port:8089}\n"
        + "  address: ${services.echo-cron.host:localhost}\n"
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
        + "  connection: ${services.redis-echo-cron.baseUrl:redis://localhost:6379}\n";
    services.add(customRoleServiceFactory.newInstance(Type.ECHO, "cron", "cron", SpringService.getSpringServiceConfigOutputPath(), contents));

    contents = "\n"
        + "server:\n"
        + "  port: ${services.echo-no-cron.port:8089}\n"
        + "  address: ${services.echo-no-cron.host:localhost}\n"
        + "\n"
        + "scheduler:\n"
        + "  enabled: false\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-echo-no-cron.baseUrl:redis://localhost:6379}\n";
    services.add(customRoleServiceFactory.newInstance(Type.ECHO, "no-cron", "no-cron", SpringService.getSpringServiceConfigOutputPath(), contents));

    services.add(front50Service);

    contents = "clouddriver:\n"
        + "  baseUrl: ${services.clouddriver-ro.baseUrl:http://localhost:7002}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-gate.baseUrl:redis://localhost:6379}\n";
    services.add(customRoleServiceFactory
        .newInstance(Type.GATE, SpinnakerService.DEFAULT_ROLE, "ha", SpringService.getSpringServiceConfigOutputPath(), contents));

    // fiat todo
    // igor todo
    // kayenta todo

    contents = "\n"
        + "oort:\n"
        + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n"
        + "\n"
        + "mort:\n"
        + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n"
        + "\n"
        + "kato:\n"
        + "  baseUrl: ${services.clouddriver-rw.baseUrl:http://localhost:7002}\n"
        + "\n"
        + "echo:\n"
        + "  enabled: true\n"
        + "  baseUrl: ${services.echo-no-cron.baseUrl:http://localhost:8089}\n"
        + "\n"
        + "redis:\n"
        + "  connection: ${services.redis-orca.baseUrl:redis://localhost:6379}\n";
    services.add(customRoleServiceFactory
        .newInstance(Type.ORCA, SpinnakerService.DEFAULT_ROLE, "ha", SpringService.getSpringServiceConfigOutputPath(), contents));

    contents = "\n"
        + "redis:\n"
        + "  connection: ${services.redis-rosco.baseUrl:redis://localhost:6379}\n";
    services.add(customRoleServiceFactory
        .newInstance(Type.ROSCO, SpinnakerService.DEFAULT_ROLE, "ha", SpringService.getSpringServiceConfigOutputPath(), contents));

    // TODO: add new type for REDIS_MASTER
    // TODO: add new type for REDIS_SLAVE
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "clouddriver"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "clouddriver-slave"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "echo-cron"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "echo-no-cron"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "front50"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "gate"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "igor"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "orca"));
    services.add(customRoleServiceFactory.newInstance(Type.REDIS, "rosco"));
    services.add(monitoringDaemonService);
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
