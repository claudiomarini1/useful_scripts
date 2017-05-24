# -*- coding: utf-8 -*-
"""
Created on Tue May 23 15:18:52 2017

@author: claudiomarini
"""

import json
import sys
import requests
import pandas as pd
import time
import random


input_path = sys.argv[1]
output = {}

def add_result (line,lat, lng):
    output[line]= str(lat)+","+str(lng)

def print_result ():
    df = pd.DataFrame.from_dict(output,orient="index")
    df.to_csv ("output.csv")


if __name__=="__main__":
    
    with  open(input_path) as f:
        for line in f:
            print "GeoLocating "+line
            url = 'http://maps.googleapis.com/maps/api/geocode/json?address='+str(line)
            try:
                #time.sleep(random.random() + random.randint(1,10))
                
                rispostaGet = requests.get (url)
    
                jsonparsed = json.loads(rispostaGet.content)
        
                if (jsonparsed['status'] == 'OK'):
                    add_result(line,jsonparsed['results'][0]['geometry']['location']['lat'],jsonparsed['results'][0]['geometry']['location']['lng'])
                    print jsonparsed['results'][0]['geometry']['location']['lat']
                    print jsonparsed['results'][0]['geometry']['location']['lng']
            except requests.ConnectionError:
                print "error "+ str(line)
                
    print_result()