import requests
from random import randint
from time import sleep
from datetime import datetime
from lxml import html
import pandas as pd
import urllib
import os
import threading
import sys

withThread= sys.argv[1]


def download(path,link):
	urllib.urlretrieve(link, path)

def createNewDownloadThread(link, filelocation):
	download_thread = threading.Thread(target=download, args=(filelocation,link))
	print "Start thread downloader "+filelocation
	download_thread.start()
	
	
# Path cartella di input
path = './'

today = datetime.now()
today = today.strftime('%Y')

#start_anno = 2010
start_anno = 2012

for anno in range(int(start_anno), int(today)+1):

	page = requests.get("http://bur.regione.emilia-romagna.it/anno?y="+str(anno))

	#while True:
	# Costruisci l'albero DOM dalla pagina HTML risultante
	tree = html.fromstring(page.content)
	
	xpath_text = '//*[@id="content"]/div/p/a[1]/text()'
	xpath_link = '//*[@id="content"]/div/p/a[2]/@href'
	
	titolo_bollettini = tree.xpath(xpath_text)
	link_bollettini = tree.xpath(xpath_link)
	print str(anno) + "  Numero bollettini : " +str(len(titolo_bollettini))
	if not os.path.exists(str(anno)):
		os.makedirs(str(anno))
	for i in range(0,len(titolo_bollettini)):
		print "  Bollettino : " +str((titolo_bollettini[i]))
		titolo = titolo_bollettini[i].replace("/",'')
		titolo = titolo.replace(' ' , '')
		titolo = titolo.replace('<' , '')
		titolo = titolo.replace('>' , '')
		titolo = titolo.replace('.' , ' ')
		"""if withThread:
			createNewDownloadThread(link_bollettini[i],str(anno)+"/"+titolo+".pdf")
			if(len(titolo_bollettini)%10 == 0):
				print "Aspetto"
				sleep(10)
		else:
		"""
		download(str(anno)+"/"+titolo+".pdf",link_bollettini[i])


	