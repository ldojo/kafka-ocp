/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.snowdrop.example.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Path("/greeting")
@Component
public class GreetingEndpoint {
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
    @GET
    @Produces("application/json")
    public Greeting greeting(@QueryParam("name") @DefaultValue("World") String name) {
        final String message = String.format(Greeting.FORMAT, name);
        return new Greeting(message);
    }
    
    @GET
    @Path("/send")
    public void sendMessage(@QueryParam("message") String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("baeldung", message);
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
     
            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message + 
                  "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                  + message + "] due to : " + ex.getMessage());
            }
        });
    }
}
