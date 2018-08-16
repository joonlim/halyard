/*
 * Copyright 2017 Google, Inc.
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
 */

package com.netflix.spinnaker.halyard.deploy.spinnaker.v1.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
abstract public class SpringServiceSettings extends ServiceSettings {
  private static final String SPRING_PROFILES_ACTIVE_KEY = "SPRING_PROFILES_ACTIVE";

  protected void setProfiles(List<String> profiles) {
    if (profiles == null || profiles.isEmpty()) {
      return;
    }

    String val = profiles.stream().collect(Collectors.joining(","));
    getEnv().put(SPRING_PROFILES_ACTIVE_KEY, val);
  }

  public void addProfiles(List<String> profiles) {
    if (profiles == null || profiles.isEmpty()) {
      return;
    }

    Set<String> allProfiles = new HashSet<>(profiles);
    if (getEnv().containsKey(SPRING_PROFILES_ACTIVE_KEY)) {
      List<String> existingProfiles = Arrays.asList(getEnv().get(SPRING_PROFILES_ACTIVE_KEY).split(","));
      allProfiles.addAll(existingProfiles);
    }

    getEnv().put(SPRING_PROFILES_ACTIVE_KEY, allProfiles.stream().collect(Collectors.joining(",")));
  }

  SpringServiceSettings() {}

  public void enableAuth() {
    setBasicAuthEnabled(true);
    setUsername(RandomStringUtils.random(10));
    setPassword(RandomStringUtils.random(10));
  }
}
