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

import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.RedisService;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.ServiceSettings;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.DeployPriority;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.StatefulSetResource;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2Service;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service.distributed.kubernetes.v2.KubernetesV2ServiceDelegate;
import lombok.Data;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class KubernetesV2PersistentRedisService extends RedisService implements
    KubernetesV2Service<Jedis> {
  final DeployPriority deployPriority = new DeployPriority(1);

  @Delegate
  @Autowired
  KubernetesV2ServiceDelegate serviceDelegate;

  public String getArtifactId(String deploymentName) {
    return "gcr.io/kubernetes-spinnaker/redis-cluster:v2";
  }

  @Override
  public ServiceSettings defaultServiceSettings() {
    return new Settings();
  }

  @Override
  public DeployPriority getDeployPriority() { return this.deployPriority; }

  @Override
  public boolean isStatefulSet() { return true; }
}
