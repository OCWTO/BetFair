#!/usr/bin/env python
 
import requests
import json
#openssl x509 -x509toreq -in certificate.crt -out CSR.csr -signkey privateKey.key
 
liveKey = "ztgZ1aJPu2lvvW6a"
delayedKey = "scQ6H11vdb6C4s7t"
footballId = 1
americanFootballID = 6423

payload = 'username=0ocwto0&password=@Cracker93'
headers = {'X-Application': delayedKey, 'Content-Type': 'application/x-www-form-urlencoded'}
 
resp = requests.post('https://identitysso.betfair.com/api/certlogin', data=payload, cert=("C:\Users\Craig\Desktop\eclipse_workspace\Project\certs\client-2048.crt", "C:\Users\Craig\Desktop\eclipse_workspace\Project\certs\client-2048.key"), headers=headers)
 
if resp.status_code == 200:
  resp_json = resp.json()
  success = True
  sessionToken = resp_json['sessionToken']
  print "Login attempt reply:"
  print "STATUS: " + resp_json['loginStatus']
  print "TOKEN: " + resp_json['sessionToken']
else:
  print "First request failed."

if success == True:
	url="https://api.betfair.com/exchange/betting/json-rpc/v1"
	header = { 'X-Application' : delayedKey, 'X-Authentication' : sessionToken ,'content-type' : 'application/json' }
	
	jsonrpc_req = '{"jsonrpc": "2.0", "method": "SportsAPING/v1.0/listEvents","params": {"filter": {"eventTypeIds": ["6423"],"marketStartTime": {"from": "2014-11-16T00:00:00Z","to": "2014-11-16T23:59:00Z"}}},"id": 1}'
 
	response = requests.post(url, data=jsonrpc_req, headers=header)
	 
	print json.dumps(json.loads(response.text), indent=3)
else:
	print "Second request failed."
