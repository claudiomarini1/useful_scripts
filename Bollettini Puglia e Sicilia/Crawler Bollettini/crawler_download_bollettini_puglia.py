import math
import re
import os
import sys
from time import sleep
from htmldom import htmldom
import urllib.request
import configparser

configPath = ""


# Print iterations progress
def printProgress (iteration, total, prefix = '', suffix = '', decimals = 1, barLength = 100):
    """
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : number of decimals in percent complete (Int)
        barLength   - Optional  : character length of bar (Int)
    """
    filledLength    = int(round(barLength * iteration / float(total)))
    percents        = round(100.00 * (iteration / float(total)), decimals)
    bar             = '█' * filledLength + '-' * (barLength - filledLength)
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

dir ="./"



for anno in range(int(min_year),int(max_year)+1):
	link = "http://beta.regione.puglia.it/bollettino-ufficiale?p_p_id=burpsearch_WAR_GestioneBurpportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_burpsearch_WAR_GestioneBurpportlet_jspPage=%2Fhtml%2Fburpsearch%2Fview.jsp&_burpsearch_WAR_GestioneBurpportlet_opz=sezioni&_burpsearch_WAR_GestioneBurpportlet_anno="+str(anno)+"&_burpsearch_WAR_GestioneBurpportlet_keywords=&_burpsearch_WAR_GestioneBurpportlet_advancedSearch=false&_burpsearch_WAR_GestioneBurpportlet_andOperator=true&_burpsearch_WAR_GestioneBurpportlet_orderByCol=numero&_burpsearch_WAR_GestioneBurpportlet_orderByType=desc&_burpsearch_WAR_GestioneBurpportlet_resetCur=false&_burpsearch_WAR_GestioneBurpportlet_delta=1000"
	basic_link="http://beta.regione.puglia.it/bollettino-ufficiale?p_p_id=burpsearch_WAR_GestioneBurpportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_burpsearch_WAR_GestioneBurpportlet_jspPage=%2Fhtml%2Fburpsearch%2Fview.jsp&_burpsearch_WAR_GestioneBurpportlet_opz=sezioni&_burpsearch_WAR_GestioneBurpportlet_anno="+str(anno)+"&_burpsearch_WAR_GestioneBurpportlet_keywords=&_burpsearch_WAR_GestioneBurpportlet_advancedSearch=false&_burpsearch_WAR_GestioneBurpportlet_andOperator=true&_burpsearch_WAR_GestioneBurpportlet_orderByCol=numero&_burpsearch_WAR_GestioneBurpportlet_orderByType=desc&_burpsearch_WAR_GestioneBurpportlet_resetCur=false&_burpsearch_WAR_GestioneBurpportlet_delta=1000"
	print("Retrieving Year "+str(anno))
	directory=dir+str(anno)+"/"
	if not os.path.exists(directory):
		os.makedirs(directory)
	
	current_page =1
	while(True):
		dom = htmldom.HtmlDom(link).createDom()
		a = dom.find( "a[class='download-link']" )
		div = dom.find("div[class='nome-bollettino']");
		i = 0
		num_a = a.length()
		
		search_result = dom.find("div[class='search-results']")
		m = re.search("Risultati\s[0-9]{1,3}\s\-\s[0-9]{1,3}\ssu\s([0-9]{1,4})",search_result.text())
		pages = 0;
		if(m):
			pages = m.group(1)
		
		num_new_page = 0
		if(pages!=0):
			num_new_page = math.ceil( (int(pages)-(200*(int(current_page)-1)))/200)
			print (str(num_new_page))
		
		print("Download for page "+str(current_page))
		for link in a:
			#file = open(directory+div[i].text()+"."+extension, 'w+')
			urllib.request.urlretrieve(link.attr("href"), directory+div[i].text()+"."+extension)
			#file.write(link.attr("href"))
			i = i+1;
			#file.close();
			printProgress(i, num_a, prefix = 'Progress:', suffix = 'Complete', barLength = 50)
			
			
		if(num_new_page<=1):
			break;
		else:
			current_page = current_page + 1
			other_page = "_burpsearch_WAR_GestioneBurpportlet_cur="+str(current_page)
			link = basic_link+"&"+other_page
		
		
		
		
		#for link in a:
			#file = open(directory+div[i].text()+"."+extension, 'w+')
		#	urllib.request.urlretrieve(link.attr("href"), directory+div[i].text()+"."+extension)
			#file.write(link.attr("href"))
			#print(div[i].text()+" - "+ link.attr( "href" ))
		#	i = i+1;
		#	printProgress(i, num_a, prefix = 'Progress:', suffix = 'Complete', barLength = 50)

		
		
		
		
		
