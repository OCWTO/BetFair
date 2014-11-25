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
#login code above this

if success == True:
	url="https://api.betfair.com/exchange/betting/json-rpc/v1"
	header = { 'X-Application' : delayedKey, 'X-Authentication' : sessionToken ,'content-type' : 'application/json' }
	
	jsonrpc_req = '{"jsonrpc": "2.0","method": "SportsAPING/v1.0/listMarketCatalogue","params": {"filter": {"eventIds": ["27297679"]},"maxResults": "200","marketProjection": ["COMPETITION","EVENT","EVENT_TYPE","RUNNER_DESCRIPTION","RUNNER_METADATA","MARKET_START_TIME"]},"id": 1}'
 
	response = requests.post(url, data=jsonrpc_req, headers=header)
	 
	print json.dumps(json.loads(response.text), indent=3)
else:
	print "Second request failed."


if success == True:
	url="https://api.betfair.com/betting/json-rpc/"
	header = { 'X-Application' : delayedKey, 'X-Authentication' : sessionToken ,'content-type' : 'application/json' }
	
	jsonrpc_req= '{"jsonrpc": "2.0","method": "SportsAPING/v1.0/listMarketBook", "params": {"marketIds" : ["1.107514698" , "1.107702438"],"priceProjection" : ["EX_BEST_OFFERS"] }, "id": 1}'



	response = requests.post(url, data=jsonrpc_req, headers=header)
	
	print json.dumps(json.loads(response.text), indent=3)
