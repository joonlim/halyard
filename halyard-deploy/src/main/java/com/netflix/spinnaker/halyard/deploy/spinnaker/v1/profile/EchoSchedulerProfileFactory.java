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

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.profile;

import com.netflix.spinnaker.halyard.config.model.v1.node.DeploymentConfiguration;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerArtifact;
import com.netflix.spinnaker.halyard.deploy.spinnaker.v1.SpinnakerRuntimeSettings;
import org.springframework.stereotype.Component;

@Component
public class EchoSchedulerProfileFactory extends StringBackedProfileFactory {
  @Override
  protected String getRawBaseProfile() { return ""; }

  @Override
  protected void setProfile(Profile profile, DeploymentConfiguration deploymentConfiguration,
      SpinnakerRuntimeSettings endpoints) {
    String contents = ""
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
        + "    timezone: ${global.spinnaker.timezone:America/Los_Angeles}\n";
    profile.appendContents(contents);
  }

  @Override
  public SpinnakerArtifact getArtifact() { return SpinnakerArtifact.ECHO; }

  @Override
  protected String commentPrefix() { return "## "; }
}
