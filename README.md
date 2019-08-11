# kafka-ocp

## Install Spring Boot/Kafka end to end example on AMQ Streams

1. Create OCP project named "kafka". All OCP YAML we'll deploy has namespace "kafka" declared in it.
2. Install the AMQ Streams cluster Operator

```
cd GIT_HOME/redhat-amq-streams/installation/
oc project kafka
#set the namespace to 'kafka' for the relevant YAML we'll deploy
sed -i 's/namespace: .*/namespace: kafka/' install/cluster-operator/*RoleBinding*.yaml
oc apply -f install/cluster-operator -n kafka
oc apply -f examples/templates/cluster-operator -n kafka
oc apply -f examples/kafka/kafka-ephemeral.yaml
```

3. wait for the strimzi-cluster-operator deployment, and the zookeeper and kafka StatefulSets, and the entity-operator deployment to come up.

4. Deploy the Spring Boot application. It will be capable of pushing/pulling from a kafka topic.

```
cd spring-boot/boot-kafka-example/
oc import-image java:8 --from=registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift --confirm
#deploy spring boot application
oc new-app --name boot-kafka-example 'java:8~https://github.com/ldojo/kafka-ocp.git' --context-dir=spring-boot/boot-kafka-example
#wait for application to come up
oc logs -f bc/boot-kafka-example
#add a route
oc expose svc/boot-kafka-example
#if all is successful, you should be able to go to this address in your browser:
echo "http://$(oc get route boot-kafka-example -o jsonpath='{.spec.host}{"\n"}')"
```

5. In the Webapp, type in some message, and view the corresponding OCP pod logs
If you type in a string like "msg1", you should then see logs like this in the pod:
```
INFO: Sent message=[msg1] with offset=[3]
INFO: Received Messasge in kafka group group1: msg1
```

That confims that the Spring Boot code is both pushing the message onto a Kafka topic, as well as receiving it, confirming communication with the Kafka cluster installed via AMQ Streams
