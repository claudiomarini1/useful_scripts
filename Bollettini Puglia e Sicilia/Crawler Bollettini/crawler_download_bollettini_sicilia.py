# -*- coding: utf-8 -*-

import urllib2
from bs4 import BeautifulSoup
import math
import urllib
import sys
import os
import re
import configparser



# Print iterations progress
def printProgress (iteration, total, prefix = '', suffix = '', decimals = 1, barLength = 100):

    filledLength    = int(round(barLength * iteration / float(total)))
    percents        = round(100.00 * (iteration / float(total)), decimals)
    bar             = 'â–ˆ' * filledLength + '-' * (barLength - filledLength)
    sys.stdout.write('\r%s |%s| %s%s %s' % (prefix, bar, percents, '%', suffix)),
    sys.stdout.flush()
    if iteration == total:
        sys.stdout.write('\n')
        sys.stdout.flush()



if (len(sys.argv)==1):
	print ("USAGE: python "+__file__+" <config_file>")
	sys.exit()
else:
	print ("read config from "+sys.argv[1])
	configPath = sys.argv[1]

#Read Config file
config = configparser.ConfigParser()
config.read(configPath)
min_year = config["Config"]["min_year"]
max_year = config["Config"]["max_year"]

extension = config["OutExt"]["ext"]



bollettinon = "Bollettino n. "
pubblicatoil="Pubblicato il"

baselink="http://www.gurs.regione.sicilia.it/Gazzette/"


for anno in range(int(min_year),int(max_year)+1):
	dir="files/"
	directory=dir+str(anno)+"/"
	if not os.path.exists(directory):
		os.makedirs(directory)
	
  
	print("ANNO "+str(anno))
	x = str(anno)
	if(anno == 2016): #int(max_year)):
		link = "http://www.gurs.regione.sicilia.it/Gazzette/XGURS.HTM"
	else:
		link = "http://www.gurs.regione.sicilia.it/Gazzette/XGURS"+x[-2:]+".HTM"

	openwebsite = urllib2.urlopen(link)
	soup = BeautifulSoup(openwebsite, "html.parser")
   	i = 0
   	for link in soup.findAll('a'):
		if(link.get('target') == "_parent"):
      			numerobollettino = re.search('.*N\.\s*([0-9]{1,3})\-\s*.*?\s*([0-9]{2}|[0-9]{1})\s([a-zA-Z]{1}[a-z]{4,8})\s([0-9]{4})', link.getText())
      			#print(link.getText())
			#print(numerobollettino.group(1))
			#print(numerobollettino.group(2))
			#print(numerobollettino.group(3))
			#print(numerobollettino.group(4))
			#numerobollettino = re.search('.*N\.\s*([0-9]{1,3})\-\s*\S+\s([0-9]{2}|[0-9]{1})\s([a-zA-Z]{1}[a-z]{4,8})\s([0-9]{4})', link.getText())
      			nomedelfile=str(i)+"_"+bollettinon+" "+numerobollettino.group(1)+" "+pubblicatoil+" "+numerobollettino.group(2)+" "+numerobollettino.group(3)+" "+numerobollettino.group(4)+" "+link.getText()
      		
     			bollettinolink = link.get('href')
         	 	bollettinolink =urllib.quote(bollettinolink.encode('utf8'), ':/')
			print(bollettinolink+"  -  "+nomedelfile)
      			urllib.urlretrieve(baselink+bollettinolink,directory+nomedelfile+bollettinolink.replace("/","-"))
      			i = i+1;
			#printProgress(i, num_a, prefix = 'Progress:', suffix = 'Complete', barLength = 50)
	
