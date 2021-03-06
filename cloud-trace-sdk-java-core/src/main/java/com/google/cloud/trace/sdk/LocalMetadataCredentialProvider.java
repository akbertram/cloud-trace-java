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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.IOException;
import java.util.List;

/**
 * Authorizes using the local metadata stored in a GCE instance. This will
 * fail on anything other than GCE.
 */
public class LocalMetadataCredentialProvider implements CredentialProvider {

  @Override
  public Credential getCredential(List<String> scopes) throws CloudTraceException {
    try {
      return GoogleCredential.getApplicationDefault();
    } catch (IOException e) {
      throw new CloudTraceException("Failed to get default GoogleCredential", e);
    }
  }
}
