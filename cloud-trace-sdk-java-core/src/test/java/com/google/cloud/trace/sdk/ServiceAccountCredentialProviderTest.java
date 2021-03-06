// Copyright 2015 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;

/**
 * Tests for the {@link ServiceAccountCredentialProvider} class.
 */
@RunWith(JUnit4.class)
public class ServiceAccountCredentialProviderTest {

  private ServiceAccountCredentialProvider provider;

  @Test
  public void testInitFromProperties() throws Exception {
    provider = new ServiceAccountCredentialProvider();
    Properties props = new Properties();
    props.setProperty(ServiceAccountCredentialProvider.class.getName() + ".emailAddress",
        "a@b.com");
    props.setProperty(ServiceAccountCredentialProvider.class.getName() + ".p12FileName",
        "/a/b/c.txt");
    provider.initFromProperties(props);
    assertEquals("a@b.com", provider.getEmailAddress());
    assertEquals("/a/b/c.txt", provider.getP12File().getAbsolutePath());
  }
}
