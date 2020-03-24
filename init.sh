export SRV_USERNAME=$(cat /serviceuser/srvsyfooprest-sbs/username)
export SRV_PASSWORD=$(cat /serviceuser/srvsyfooprest-sbs/password)

export SRV_FSS_USERNAME=$(cat /serviceuser/srvsyfooprest/username)
export SRV_FSS_PASSWORD=$(cat /serviceuser/srvsyfooprest/password)

export SYFOOPREST_API_STSTOKEN_APIKEY_PASSWORD=$(cat /apigw/securitytokenservicetoken/x-nav-apiKey)

export SYFOOPREST_API_PDLAPI_APIKEY_PASSWORD=$(cat /apigw/pdlapi/x-nav-apiKey)
