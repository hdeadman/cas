#!/bin/bash

echo "Installing sonarqube in k3d"

set -e
SCENARIO_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

time ${PWD}/ci/tests/k3d/setup.sh
helm repo add sonarqube https://sonarsource.github.io/helm-chart-sonarqube || true
kubectl create namespace sonarqube || true
kubectl delete secret -n sonarqube cas-cert || true
sleep 2
# this chart lets you create a secret of certs that are added to java trust bundle on startup, needs to trust CAS for callbacks
kubectl create secret generic -n sonarqube cas-cert --from-file=cas-cert.crt=${CAS_CERT}
# adding date annotation to force re-start of pod since secret with cas cert probably changed due to it being deleted every time
helm upgrade sonarqube sonarqube/sonarqube -n sonarqube --install \
  --values ${SCENARIO_DIR}/sonarqube-test-values.yaml \
  --set annotations.releaseTime="$( date --rfc-3339=seconds)"
sleep 60
# we don't want to stop after this point so if it times out coming up, we can see status and logs
set +e
echo "Dumping logs before waiting"
RC=1
while [ $RC -n 0 ]
do
   kubectl get pods -n sonarqube  | grep sonarqube-sonarqube-0 | grep Running
   RC=$?
   kubectl get pods -n sonarqube  | grep sonarqube-sonarqube-0
   kubectl logs -n sonarqube sonarqube-sonarqube-0 --tail=100
   sleep 5
done
kubectl logs -n sonarqube sonarqube-sonarqube-0
echo $?
echo "Waiting for sonarqube pods to be ready"
kubectl wait --namespace sonarqube --for condition=ready pod --selector=statefulset.kubernetes.io/pod-name=sonarqube-sonarqube-0 --timeout=300s
echo "Showing sonarqube pods status"
kubectl get pods -n sonarqube
kubectl logs -n sonarqube sonarqube-sonarqube-0
kubectl get pods -A
curl -ksv -o /dev/null "https://host.k3d.internal"
