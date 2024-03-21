/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.petcaredev;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ChoreoWebSubHubSubscriber {

    public void subscribeToWebSubHub(String hubUrl, String topicUrl, String callbackUrl) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            Map<Object, Object> data = new HashMap<>();
            data.put("hub.mode", "subscribe");
            data.put("hub.topic", topicUrl);
            data.put("hub.callback", callbackUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(hubUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(buildFormDataFromMap(data))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print status code for debugging
            System.out.println("Connection to the hub: " + hubUrl + " returned status: " + response.statusCode());

            // Print headers for debugging
            HttpHeaders headers = response.headers();
            headers.map().forEach((k,v) -> System.out.println(k + ":" + v));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}

