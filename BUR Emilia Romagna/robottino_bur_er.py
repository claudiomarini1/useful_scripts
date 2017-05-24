import requests
from datetime import datetime
from lxml import html
import pandas as pd

# Path cartella di output
path = './'

# Data di oggi come limite superior nella ricerca dei BUR
today = datetime.now()
today = today.strftime('%d/%m/%Y')

# Dati della form da inviare nella richiesta POST
payload = {
			'key': 'autorizzazione unica',
			'dal': '01/01/2010',
			'al': today,
			'testo': 'Cerca nel Testo'
		}

# Invia richiesta POST e prendi i risultati
page = requests.post("http://bur.regione.emilia-romagna.it/risultati", data=payload)

estrazioni = []
first = True
while True:
	
	# Costruisci l'albero DOM dalla pagina HTML risultante
	tree = html.fromstring(page.content)

	# XPath che contiene tutti i link da estrarre nella pagina
	xpath_links = '//*[@id="content"]/div/div[2]/div/h3/a/@href'
	
	# XPath che mostra il numero dei link ottenuti
	num_of_links = '//*[@id="content"]/div/div[2]/p[3]/strong/text()'
	
	#Stampa quantita' risultati ottenuti
	if first:
		print "num of results :"+ str(tree.xpath(num_of_links))
		first = False
	# Siti web contenenti i paragrafi da estrarre
	web_pages = list(tree.xpath(xpath_links))

	for web_page in web_pages:

		page = requests.get(web_page)
		tree_content_page = html.fromstring(page.content)
		xpath_titolo_bollettino = '//*[@id="content"]/div/h1/text()'
		xpath_titolo = '//*[@id="content"]/div/div/h1/text()'
		xpath_contenuto = '//*[@id="content"]/div/div/div/p/text()'

		
		titolo_bollettini = tree_content_page.xpath(xpath_titolo_bollettino)
		titolo_bollettini = titolo_bollettini[0] if len(titolo_bollettini) > 0 else ''
		
		titolo = tree_content_page.xpath(xpath_titolo)
		titolo = titolo[0] if len(titolo) > 0 else ''
		
		contenuto = tree_content_page.xpath(xpath_contenuto)
		contenuto = contenuto[0] if len(contenuto) > 0 else ''
		
		estrazioni.append([titolo_bollettini.strip(), titolo.strip(), contenuto.strip()])
		
	xpath_next_link = '//*[@id="content"]/div/div[2]/div[21]/span[@class="next"]/a/@href'
	
	next_page = tree.xpath(xpath_next_link)
	if len(next_page) == 0:
		break;
	
	next_page = next_page[0]
	page = requests.get(next_page)
	
	print 'Extracting data from %s' % next_page

 
estrazioni = pd.DataFrame(estrazioni, columns=['Titolo Bollettino', 'Titolo', 'Contenuto'])	
estrazioni.to_csv(path+'estrazioni_bur.csv', sep=';', index=False, encoding='utf-8') 