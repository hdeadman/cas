#!/usr/bin/env bash
set -e
set -m

STRAPI_FOLDER=${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/strapi
PROJECT=getstarted
if [[ ! -d $STRAPI_FOLDER/$PROJECT ]] ; then
  mkdir -p $STRAPI_FOLDER
  cd $STRAPI_FOLDER
  yarn create strapi-app $PROJECT --quickstart --no-run
  cd -
fi

# copy server.js with URL for strapi defined
cp ${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/strapi-custom/server.js $STRAPI_FOLDER/$PROJECT/config/server.js
# copy SSO provider bootstrap.js with CAS defaults pre-configured for this test deployment
mkdir -p $STRAPI_FOLDER/$PROJECT/extensions/users-permissions/config/functions
cp ${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/strapi-custom/bootstrap.js $STRAPI_FOLDER/$PROJECT/extensions/users-permissions/config/functions

# install node modules for strapi app
cd $STRAPI_FOLDER/$PROJECT
yarn install

# copy over user-permissions-actions.js from installed app b/c bootstrap.js references it by relative path
cp $STRAPI_FOLDER/$PROJECT/node_modules/strapi-plugin-users-permissions/config/users-permissions-actions.js $STRAPI_FOLDER/$PROJECT/extensions/users-permissions/config

# run without SSL verification in order to call CAS via https and not worry about SSL trust
NODE_TLS_REJECT_UNAUTHORIZED=0 yarn develop &
pid=$!
if [[ "$CI" != "true" ]]; then
  fg 1
else
  echo "Waiting for Strapi start up"
  until curl -k -L --output /dev/null --silent --fail http://localhost:1337; do
    echo -n '.'
    sleep 1
  done
  echo "Strapi Ready - PID: $pid"
fi

if [[ "$SKIP_REGISTRATION" != "true" ]]; then
  # register strapi administrator user
  DATA='{"email":"admin@example.org","password":"P@ssw0rd","firstname":"Strapi","lastname":"Admin"}'
  echo $DATA > .admin.txt
  cat .admin.txt
  curl -X POST -H "Content-Type: application/json" http://localhost:1337/admin/register-admin --data @./.admin.txt
  rm .admin.txt
fi

cd -