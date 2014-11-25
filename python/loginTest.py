#!/usr/bin/env python
 
import requests
 
#openssl x509 -x509toreq -in certificate.crt -out CSR.csr -signkey privateKey.key
 
 
payload = 'username=0ocwto0&password=@Cracker93'
headers = {'X-Application': 'ztgZ1aJPu2lvvW6a', 'Content-Type': 'application/x-www-form-urlencoded'}
 
resp = requests.post('https://identitysso.betfair.com/api/certlogin', data=payload, cert=('C:\Users\Craig\Desktop\eclipse_workspace\Project\certs\client-2048.crt', 'C:\Users\Craig\Desktop\eclipse_workspace\Project\certs\client-2048.key'), headers=headers)
 
if resp.status_code == 200:
  resp_json = resp.json()
  print resp_json['loginStatus']
  print resp_json['sessionToken']
else:
  print "Request failed."
