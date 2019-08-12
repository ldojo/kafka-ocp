```
#creates a new template named apache-kafka, that can launch a kafka pod with kafka and zookeper containers in the pod
oc create -f resources.yaml
oc new-app apache-kafka
