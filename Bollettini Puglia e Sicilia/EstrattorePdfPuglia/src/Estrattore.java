import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sis.internal.jaxb.referencing.CC_OperationMethod;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

public class Estrattore {
	
	public static final String separator = "|";
	
	//public static final String anno = "2015";
	
	public static final String regione = "Puglia";
	
	public static final int numInterval = 2;
	
	public static final String headerFind = "To Find"+separator+"File"+separator+"Num Bollettino"+separator+"Occorrenze\n";

	public static final String header = "P.IVA"+separator+"Società"+separator+"Regione"+separator+"Provincia"+separator+"Comune"+separator+"Potenza [kW]"+separator+"Rif.File"+separator+"Bollettino num"+separator+"Data Bollettino"+separator+"Determinazione num"+separator+"Data Determinazione"+separator+"Mancato accoglimento"+separator+"Autorizzazione"+separator+"Annullamento"+separator+"Decadenza"+separator+"Revoca"+separator+"Rigetto"+separator+"Escussione"+separator+"Rif det num"+separator+"Rif data det"+separator+"Proroga"+separator+"Voltura"+separator+"In favore di"+separator+"Tipo Energia"+separator+"Rif Aut Unica\n"/*+separator+"Impianto\n"*/;

	public static ArrayList<Paragrafi> paragrafiTot = new ArrayList<Paragrafi>();
	
	public static ArrayList<Record> recordsTot = new ArrayList<Record>();
	
	public static ArrayList<String> toUpdate = new ArrayList<String>();
	
	public String parserRidottoPuglia(String fileName, String pathToFile, String folderName) throws IOException, SAXException, TikaException{
		
		String anno = folderName;
		String patternToken = "((A)tti(\\sdi)*\\s(o|O)rgani\\s(monocratici|democratici)\\s(r|R)egionali|(A)tti\\s*(r|R)egionali)";
		BodyContentHandler handler = new BodyContentHandler(-1);
		
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(pathToFile));
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);

		String x = handler.toString();

		Pattern perToken = Pattern.compile(patternToken);
		String[] token = perToken.split(x);
		
		int counter=0;
		String result ="";
		//System.out.println("\tnum token atti regionali = "+token.length+"\tprendo token num "+Math.floorDiv(token.length, 2));
		if((!anno.equalsIgnoreCase("2012") && token.length>=numInterval) || (anno.equalsIgnoreCase("2012") && token.length>numInterval)){ 
			String seconda_parte = token[/*numInterval*/Math.floorDiv(token.length, 2)];
			if(/*anno.equalsIgnoreCase("2011") || */anno.equalsIgnoreCase("2012")){
				seconda_parte = token[numInterval];
			}
			/*if(anno.equalsIgnoreCase("2016") && token.length==3){
				seconda_parte = token[0];
				//System.out.println("#####################\n\n"+seconda_parte+"\n\n###########################\n\n");
				BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));
				writer.write("FILE : "+fileName+"\n");
				for(int p=0; p<token.length; p++){
					writer.write("Token "+p+" :\n");
					writer.write(token[p]);
					writer.write("\n#####################\n\n");
				}
				writer.close();
			}*/
			/*if(token.length==(numInterval+1)){
				seconda_parte = token[numInterval-1];
			}
			else{
				seconda_parte = token[numInterval];
			}*/
			/*if(token.length==2 && fileName.contains("131")) {
				System.out.println("\t\t---->cambio!");
				seconda_parte = token[0];
			}*/
			
			patternToken = "((Atti\\se\\scomunicazioni\\sdegli\\sEnti\\sLocali)|(Appalti(\\s-\\sBandi)*(\\,\\sConcorsi)*(\\se)*(\\sAvvisi)*))";
			perToken = Pattern.compile(patternToken, Pattern.CASE_INSENSITIVE);
			String[] secondo_token = perToken.split(seconda_parte);
			
			String paragrafi = secondo_token[0];
			patternToken = "(Pag\\.\\s[0-9]{1,5})";
			perToken = Pattern.compile(patternToken, Pattern.CASE_INSENSITIVE);
			String[] terzo_token = perToken.split(paragrafi);
			
			
			String patternAut = "(\\S*)(((a|A){1}(\\-|\\‐)*u(\\-|\\‐)*to(\\-|\\‐)*riz(\\-|\\‐)*za(\\-|\\‐)*zio(\\-|\\‐)*ne\\s*(u|U)(\\-|\\‐)*ni(\\-|\\‐)*ca))(\\s|\\,)*";
			Pattern perTokenAut = Pattern.compile(patternAut, Pattern.CASE_INSENSITIVE);
			
			String patternAnn = "(\\S*)((an(\\-|\\‐)*nul(\\-|\\‐)*la(\\-|\\‐)*men(\\-|\\‐)*to)|(an(\\-|\\‐)*nul(\\-|\\‐)*la(\\-|\\‐)*t[a-z]{1}))(\\s|\\,)";
			Pattern perTokenAnn = Pattern.compile(patternAnn, Pattern.CASE_INSENSITIVE);
			
			String patternMAc = "(\\S*)((man(\\-|\\‐)*ca(\\-|\\‐)*to(\\-|\\‐)*\\s*ac(\\-|\\‐)*co(\\-|\\‐)*gli(\\-|\\‐)*men(\\-|\\‐)*to))(\\s|\\,)";
			Pattern perTokenMAc = Pattern.compile(patternMAc, Pattern.CASE_INSENSITIVE);
			
			String patternDec = "(\\S*)((de(\\-|\\‐)*ca(\\-|\\‐)*den(\\-|\\‐)*za)|de(\\-|\\‐)*ca(\\-|\\‐)*du(\\-|\\‐)*ta|de(\\-|\\‐)*ca(\\-|\\‐)*de)(\\s|\\,)";
			Pattern perTokenDec = Pattern.compile(patternDec, Pattern.CASE_INSENSITIVE);
			
			String patternRev = "(\\S*)((re(\\-|\\‐)*vo(\\-|\\‐)*ca))(\\s|\\,)";
			Pattern perTokenRev = Pattern.compile(patternRev, Pattern.CASE_INSENSITIVE);
						
			String patternEsc = "(\\S*)((e(\\-|\\‐)*s(\\-|\\‐)*cus(\\-|\\‐)*sio(\\-|\\‐)*ne))(\\s|\\,)";
			Pattern perTokenEsc = Pattern.compile(patternEsc, Pattern.CASE_INSENSITIVE);
			
			String patternEscDet = "(n\\.\\s*([0-9]{1,4})\\s*del(l.){0,1}\\s*(\\d{1,2}.\\d{2}.\\d{4}))";
			Pattern perTokenEscDet = Pattern.compile(patternEscDet, Pattern.CASE_INSENSITIVE);
						
			String patternRig = "(\\S*)((ri(\\-|\\‐)*get(\\-|\\‐)*ta(\\-|\\‐)*t[a-z]{1})|(ri(\\-|\\‐)*get(\\-|\\‐)*t[a-z]{1}))(\\s|\\,)";
			Pattern perTokenRig = Pattern.compile(patternRig, Pattern.CASE_INSENSITIVE);
			
			String patternPror = "(\\S*)(pro(\\-|\\‐)*ro(\\-|\\‐)*ga)(\\s|\\,)*";
			Pattern perTokenPror = Pattern.compile(patternPror, Pattern.CASE_INSENSITIVE);
			
			String patternVol = "(\\S*)(vol(\\-|\\‐)*tu(\\-|\\‐)*ra)(\\s|\\,)*";
			Pattern perTokenVol = Pattern.compile(patternVol, Pattern.CASE_INSENSITIVE);
			
			String patternPiva = "((P(.|artita)\\s{0,1}I\\.*V*\\.*A*\\.*)|(\\s*C\\.F\\.\\s*|n\\.\\s*))\\s*(e\\s*C\\.F\\.\\s*|n\\.\\s*)*.*([A-Z]{0,2}[0-9]{10,11})(\\s|\\.)*.*?";
			Pattern perTokenPiva = Pattern.compile(patternPiva, Pattern.CASE_INSENSITIVE);
			
			String patternSoc = "(\\S*)(((pro(\\-|\\‐)*po(\\-|\\‐)*nen(\\-|\\‐)*te)|(S|s)(o(\\-|\\‐)*cie(\\-|\\‐)*tà\\spro(\\-|\\‐)*po(\\-|\\‐)*nen(\\-|\\‐)*te|o(\\-|\\‐)*cie(\\-|\\‐)*tà|oc\\.*))(\\:*)(\\s*)(\\“*)(.*?))($|\\,|\\,\\s|\\”|(v|V)ol(\\-|\\‐)*tu(\\-|\\‐)*ra|con|\\-|\\(|\\)|\\.\\s*\\.)";
			Pattern perTokenSoc = Pattern.compile(patternSoc, Pattern.CASE_INSENSITIVE);
			
			String patternPro = "\\(([a-zA-Z]{2,3})\\)(\\s|\\,|\\.|\\“|\\;)*.*?";
			Pattern perTokenPro = Pattern.compile(patternPro, Pattern.CASE_INSENSITIVE);
			
			String patternDet = "((\\d{1,2}\\s*.{5,10}\\s*\\d{4})\\s*\\,*\\s*n\\.\\s*([0-9]{1,4})\\s*\\w+)";
			Pattern perTokenDet = Pattern.compile(patternDet, Pattern.CASE_INSENSITIVE);
			
			String patternCom = "((C|c)omun[a-z]{1}\\s*di\\s*(.+)(\\s*))(\\([a-zA-Z]{2}\\))*";
			Pattern perTokenCom = Pattern.compile(patternCom, Pattern.CASE_INSENSITIVE);
			
			String patternPot = "((\\d{1,3}(\\,|\\.){0,1}\\d{0,5}\\s*MW)|((\\d{1,3}\\.)*\\d{1,3}\\,{0,1}\\d{0,5}\\s*(k|K)W)).*";
			Pattern perTokenPot = Pattern.compile(patternPot, Pattern.CASE_INSENSITIVE);
			
			String patternTip = "(\\S*|\\()(fo(\\-|\\‐)*to(\\-|\\‐)*vol(\\-|\\‐)*ta(\\-|\\‐)*i(\\-|\\‐)*c[a-z]{1}|e(\\-|\\‐)*o(\\-|\\‐)*li(\\-|\\‐)*c[a-z]{1}|bio(\\-|\\‐)*mas(\\-|\\‐)*s[a-z]{1}|bio(\\-|\\‐)*gas|oli(\\-|\\‐)*[a-z]{0,1}(\\-|\\‐)*ve(\\-|\\‐)*ge(\\-|\\‐)*ta(\\-|\\‐)*l[a-z]{0,1})(\\s|\\,|\\))*";
			Pattern perTokenTip = Pattern.compile(patternTip, Pattern.CASE_INSENSITIVE);
			
			String patternIFD = "(\\S*)((((in|a)\\s*fa(\\-|\\‐)*vo(\\-|\\‐)*re\\s*d(i|el(\\-|\\‐)*la))|(al(\\-|\\‐)*la\\s*(s|S)o(\\-|\\‐)*cie(\\-|\\‐)*tà))(\\:*)(\\s*)(\\“*)(.*?))(\\,|\\,\\s|\\”|con|\\-|\\.\\s*\\.)";
			Pattern perTokenIFD = Pattern.compile(patternIFD, Pattern.CASE_INSENSITIVE);
			
			String patternRifAnn = "(n\\.\\s*([0-9]{1,4})\\s*(e\\s*[0-9]{1,4}\\s*\\,*\\s*)*del\\s*([0-9]{1,2}(°)*\\s*(\\w{5,10}|\\.[0-9]{1,2}\\.)\\s*[0-9]{4}))";
			Pattern perTokenRifAnn = Pattern.compile(patternRifAnn, Pattern.CASE_INSENSITIVE);
			/*String patternImp = "(\\S*)(im(\\-|\\‐)*pian(\\-|\\‐)*to)(\\s|\\,)*";
			Pattern perTokenImp = Pattern.compile(patternImp, Pattern.CASE_INSENSITIVE);*/
			
			
			
			String paragrafo = "";
			String autorizzazione;
			String decadenza;
			String voltura;
			String partitaIva;
			String societa;
			String provincia;
			String comune;
			String potenza;
			String nome_file = fileName;
			String inFavoreDi;
			String proroga;
			String annullamento;
			String revoca;
			String rigetto;
			String escussione;
			String dataDet ; 
			String numDet ;
			String mancatoAcc;
			String escDetNum; 
			String escDataDet;
			//int impianti=0;
			String paragrafoMultiplo;
			Boolean multiplo;
			String rifAnn;
			
			String patternNBol = "(Bollettino\\s(.*)\\s([0-9]{1,5})(\\s+(s|\\-\\sv|p)))";
			Pattern perTokenNBol = Pattern.compile(patternNBol, Pattern.CASE_INSENSITIVE);
			Matcher m = perTokenNBol.matcher(fileName);
			String nBollettino = "";
			if(m.find()){
				 nBollettino = m.group(3);
			}
			
			if(anno.equals("2016") && Integer.parseInt(nBollettino)>11){
				patternToken = "(\\.\\s*){2}([0-9]{4,5})";
				perToken = Pattern.compile(patternToken, Pattern.CASE_INSENSITIVE);
				terzo_token = perToken.split(paragrafi);
			}
			
			
			
			String patternData = "([0-9]{2}\\-[0-9]{2}\\-[0-9]{4})";		
			Pattern perTokenData = Pattern.compile(patternData, Pattern.CASE_INSENSITIVE);
			m = perTokenData.matcher(fileName);
			String dataBollettino = "";
			if(m.find()){
				dataBollettino = m.group(1);		
			}
			
			String tipoEnergia;
			
			String chiaveParagrafo;
			
			//ArrayList<Record> records = new ArrayList<Record>();
			for(int i=0; i<terzo_token.length; i++){
				
				chiaveParagrafo = nome_file+"#"+(i+1);
				
				autorizzazione = "";
				decadenza = "";
				voltura = "";
				partitaIva = "";
				societa = "";
				provincia = "";
				comune = "";
				potenza = "";
				tipoEnergia = "";
				inFavoreDi = "";
				proroga="";
				annullamento ="";
				revoca = "";
				rigetto = "";
				escussione = "";
				dataDet = ""; 
				numDet = "";
				mancatoAcc = "";
				escDetNum = ""; 
				escDataDet = "";
				//impianti=0;
				paragrafoMultiplo = "";
				multiplo = false;
				rifAnn="";
						
				paragrafoMultiplo = terzo_token[i];
				String patternMultiplo = "(\\n[a-z]\\))";
				Pattern perTokenMultiplo = Pattern.compile(patternMultiplo, Pattern.CASE_INSENSITIVE);
				String[] tokenMultiplo = perTokenMultiplo.split(paragrafoMultiplo);
				
				if(tokenMultiplo.length>1){ 
					multiplo=true;
					for(int tl=0; tl<tokenMultiplo.length; tl++){
						tokenMultiplo[tl] = tokenMultiplo[tl].replaceAll("\n", "");
					}
				}
				
				
				paragrafo = terzo_token[i].replaceAll("\n", "");
				m = perTokenAut.matcher(paragrafo);
				if(m.find()) autorizzazione = m.group(2);
				
				m = perTokenAnn.matcher(paragrafo);
				if(m.find()) annullamento = m.group(2);
											
				m = perTokenMAc.matcher(paragrafo);
				if(m.find()) mancatoAcc = m.group(2);
				
				m = perTokenDec.matcher(paragrafo);
				if(m.find()) decadenza = m.group(2);
				
				m = perTokenRev.matcher(paragrafo);
				if(m.find()) revoca = m.group(2);
				
				m = perTokenEsc.matcher(paragrafo);
				if(m.find()) escussione = m.group(2);
				
				m = perTokenRig.matcher(paragrafo);
				if(m.find()) rigetto = m.group(2);
				
				m = perTokenVol.matcher(paragrafo);
				if(m.find()) voltura = m.group(2);
				
				m = perTokenPror.matcher(paragrafo);
				if(m.find()) proroga = m.group(2);
				
				/*m = perTokenImp.matcher(paragrafo);
				while(m.find()) impianti++;*/
				
				
				
				if(!autorizzazione.equals("") || !escussione.equals("")){
					paragrafiTot.add(new Paragrafi(nome_file, nBollettino, dataBollettino, chiaveParagrafo, terzo_token[i]));
					//creaFileParagrafo(nome_file,dataBollettino,terzo_token[i]);
					if(!multiplo){
						counter++;
						m = perTokenPiva.matcher(paragrafo);
						if(m.find()) partitaIva = m.group(6);
						
						m = perTokenSoc.matcher(paragrafo);
						if(!voltura.equals("")){						
							if(m.find()) societa = m.group(20);
						}
						else{
							while(m.find()){ if(!m.group(20).equals("") && !m.group(20).equals(" ")) societa = m.group(20);}
						}
						
						m = perTokenPro.matcher(paragrafo);
						if(m.find()) provincia = m.group(1);
						
						m = perTokenDet.matcher(paragrafo);
						if(m.find()){ dataDet = m.group(2).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", ""); numDet = m.group(3);}
						
						m = perTokenCom.matcher(paragrafo);
						if(m.find()) comune = m.group(3);
						if(comune.contains("(")){
							comune = comune.substring(0,comune.indexOf("("));
						}
						if(comune.contains(".")){
							comune = comune.substring(0,comune.indexOf("."));
						}
						if(comune.contains(",")){
							comune = comune.substring(0,comune.indexOf(","));
						}
						if(comune.contains(";")){
							comune = comune.substring(0,comune.indexOf(";"));
						}
						
						/*if(comune.contains("ocalit")){
							provincia = "";
						}*/
						
						m = perTokenPot.matcher(paragrafo);
						if(m.find()) {
							
							potenza = m.group(1);
							potenza = potenza.replace(" ", "").toUpperCase();
							
							if(!potenza.contains("MW")) potenza = potenza.substring(0,(potenza.indexOf("W")-1));
							else {
								Double val = Double.parseDouble(potenza.substring(0, potenza.indexOf("MW")).replace(",", "."));
								val = val * 1000;
								potenza = ""+ val;
								potenza=potenza.replace(".", ",");
							};
						}
						
						if(!voltura.equals("")){
							m = perTokenIFD.matcher(paragrafo);
							while(m.find()) inFavoreDi = m.group(18);
						}
						
						if(!escussione.equals("")){
							m = perTokenEscDet.matcher(paragrafo);
							boolean temp = false;
							while(m.find()){ temp = true; rifAnn = m.group(1); escDetNum = m.group(2); escDataDet = m.group(4).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", "");}
							if(temp) {
								if(escDataDet.replace(" ", "").matches("[0-9]+")){
									//System.out.println("#"+escDataDet+"##");
									escDataDet = escDataDet.substring(0,escDataDet.indexOf(" "))+
											new DateFormatSymbols().getMonths()[Integer.parseInt(escDataDet.substring(escDataDet.indexOf(" ")+1, escDataDet.lastIndexOf(" ")))-1]+
											escDataDet.substring(escDataDet.lastIndexOf(" "));
								}
								if(!toUpdate.contains(escDetNum+"#"+escDataDet+"#"+rifAnn))toUpdate.add(escDetNum+"#"+escDataDet+"#"+rifAnn);
							}
						}
						
						if(!annullamento.equals("") || !mancatoAcc.equals("") || !decadenza.equals("") || !revoca.equals("") || !rigetto.equals("")  || !proroga.equals("") || !voltura.equals("")){
							m = perTokenRifAnn.matcher(paragrafo);
							if(m.find()){
								rifAnn = m.group(1);
								escDetNum = m.group(2); 
								escDataDet = m.group(4).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", "").replace("°", "").toLowerCase();
								if(escDataDet.replace(" ", "").matches("[0-9]+")){
									escDataDet = escDataDet.substring(0,escDataDet.indexOf(" "))+new DateFormatSymbols().getMonths()[Integer.parseInt(escDataDet.substring(escDataDet.indexOf(" ")+1, escDataDet.lastIndexOf(" ")))-1]+escDataDet.substring(escDataDet.lastIndexOf(" "));
								}
								//System.out.println("########################\n\n\n\n"+m.group(4)+"\n\n\n"+escDataDet+"\n\n\n#################################");
								if(!toUpdate.contains(escDetNum+"#"+escDataDet+"#"+rifAnn))toUpdate.add(escDetNum+"#"+escDataDet+"#"+rifAnn);
							}
							
						}
						
						m = perTokenTip.matcher(paragrafo);
						if(m.find()) tipoEnergia = m.group(2);
						
						creaFileParagrafo(chiaveParagrafo, dataBollettino,  terzo_token[i]);
						recordsTot.add(new Record(partitaIva, societa,regione, provincia, comune, potenza, nome_file, nBollettino, dataBollettino, numDet, dataDet, mancatoAcc, autorizzazione, annullamento, decadenza, revoca, rigetto, escussione, escDetNum, escDataDet, proroga, voltura, inFavoreDi, tipoEnergia, rifAnn, chiaveParagrafo));
						result+=partitaIva+separator+societa+separator+provincia+separator+comune+separator+potenza+separator+nome_file+separator+nBollettino+separator+dataBollettino+separator+numDet+separator+dataDet+separator+mancatoAcc+separator+autorizzazione+separator+annullamento+separator+decadenza+separator+revoca+separator+rigetto+separator+escussione+separator+escDetNum+separator+escDataDet+separator+proroga+separator+voltura+separator+inFavoreDi+separator+tipoEnergia+separator+rifAnn+/*separator+impianti+*/"\n";
					}
					else{
						for(int tl=0; tl<tokenMultiplo.length; tl++){
							if(tokenMultiplo[tl].toUpperCase().contains("IMPIANTO")){
								partitaIva = "";
								societa = "";
								provincia = "";
								comune = "";
								potenza = "";
								tipoEnergia = "";
								inFavoreDi = "";
								proroga="";
								dataDet = ""; 
								numDet = "";
								mancatoAcc = "";
								escDetNum = ""; 
								escDataDet = "";
								counter++;
								m = perTokenPiva.matcher(tokenMultiplo[tl]);
								if(m.find()) partitaIva = m.group(6);
								
								if(partitaIva.equals("")){
									m = perTokenPiva.matcher(paragrafo);
									if(m.find()) partitaIva = m.group(6);
								}
								
								m = perTokenSoc.matcher(tokenMultiplo[tl]);
								if(!voltura.equals("")){						
									if(m.find()) societa = m.group(20);
								}
								else{
									while(m.find()){ if(!m.group(20).equals("") && !m.group(20).equals(" ")) societa = m.group(20);}
								}
								
								if(societa.equals("")){
									m = perTokenSoc.matcher(paragrafo);
									if(!voltura.equals("")){						
										if(m.find()) societa = m.group(20);
									}
									else{
										while(m.find()){ if(!m.group(20).equals("") && !m.group(20).equals(" ")) societa = m.group(20);}
									}
								}
								
								m = perTokenPro.matcher(tokenMultiplo[tl]);
								if(m.find()) provincia = m.group(1);
								
								m = perTokenDet.matcher(paragrafo);
								if(m.find()){ dataDet = m.group(2).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", ""); numDet = m.group(3);}
								
								m = perTokenCom.matcher(tokenMultiplo[tl]);
								if(m.find()) comune = m.group(3);
								if(comune.contains("(")){
									comune = comune.substring(0,comune.indexOf("("));
								}
								if(comune.contains(".")){
									comune = comune.substring(0,comune.indexOf("."));
								}
								if(comune.contains(",")){
									comune = comune.substring(0,comune.indexOf(","));
								}
								if(comune.contains(";")){
									comune = comune.substring(0,comune.indexOf(";"));
								}
								
								/*if(comune.contains("ocalit")){
									provincia = "";
								}*/
								
								m = perTokenPot.matcher(tokenMultiplo[tl]);
								if(m.find()) {
									
									potenza = m.group(1);
									potenza = potenza.replace(" ", "").toUpperCase();
									
									if(!potenza.contains("MW")) potenza = potenza.substring(0,(potenza.indexOf("W")-1));
									else {
										Double val = Double.parseDouble(potenza.substring(0, potenza.indexOf("MW")).replace(",", "."));
										val = val * 1000;
										potenza = ""+ val;
										potenza=potenza.replace(".", ",");
									};
								}
								
								if(!voltura.equals("")){
									m = perTokenIFD.matcher(tokenMultiplo[tl]);
									while(m.find()) inFavoreDi = m.group(18);
								}
								
								if(!escussione.equals("")){
									m = perTokenEscDet.matcher(tokenMultiplo[tl]);
									boolean temp = false;
									while(m.find()){temp = true; rifAnn=m.group(1); escDetNum = m.group(2); escDataDet = m.group(4).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", "");}
									if(temp)  {
										if(escDataDet.replace(" ", "").matches("[0-9]+")){
											escDataDet = escDataDet.substring(0,escDataDet.indexOf(" "))+new DateFormatSymbols().getMonths()[Integer.parseInt(escDataDet.substring(escDataDet.indexOf(" ")+1, escDataDet.lastIndexOf(" ")))-1]+escDataDet.substring(escDataDet.lastIndexOf(" "));
										}
										if(!toUpdate.contains(escDetNum+"#"+escDataDet+"#"+rifAnn))toUpdate.add(escDetNum+"#"+escDataDet+"#"+rifAnn);
									}

								}
								
								if(!annullamento.equals("") || !mancatoAcc.equals("") || !decadenza.equals("") || !revoca.equals("") || !rigetto.equals("")  || !proroga.equals("") || !voltura.equals("")){
									m = perTokenRifAnn.matcher(paragrafo);
									if(m.find()){
										rifAnn = m.group(1);
										escDetNum = m.group(2); 
										escDataDet = m.group(4).replace(".", " ").replace("/", " ").replace("-", "").replace("‐", "").replace("°", "").toLowerCase();
										//System.out.println("########################\n\n\n\n"+m.group(4)+"\n\n\n"+escDataDet+"\n\n\n#################################");
										if(escDataDet.replace(" ", "").matches("[0-9]+")){
											escDataDet = escDataDet.substring(0,escDataDet.indexOf(" "))+new DateFormatSymbols().getMonths()[Integer.parseInt(escDataDet.substring(escDataDet.indexOf(" ")+1, escDataDet.lastIndexOf(" ")))-1]+escDataDet.substring(escDataDet.lastIndexOf(" "));
										}
										if(!toUpdate.contains(escDetNum+"#"+escDataDet+"#"+rifAnn))toUpdate.add(escDetNum+"#"+escDataDet+"#"+rifAnn);
									}
									
								}
								
								m = perTokenTip.matcher(tokenMultiplo[tl]);
								if(m.find()) tipoEnergia = m.group(2);
								creaFileParagrafo(chiaveParagrafo, dataBollettino,  terzo_token[i]);
								recordsTot.add(new Record(partitaIva, societa,regione, provincia, comune, potenza, nome_file, nBollettino, dataBollettino, numDet, dataDet, mancatoAcc, autorizzazione, annullamento, decadenza, revoca, rigetto, escussione, escDetNum, escDataDet, proroga, voltura, inFavoreDi, tipoEnergia, rifAnn, chiaveParagrafo));
								result+=partitaIva+separator+societa+separator+provincia+separator+comune+separator+potenza+separator+nome_file+separator+nBollettino+separator+dataBollettino+separator+numDet+separator+dataDet+separator+mancatoAcc+separator+autorizzazione+separator+annullamento+separator+decadenza+separator+revoca+separator+rigetto+separator+escussione+separator+escDetNum+separator+escDataDet+separator+proroga+separator+voltura+separator+inFavoreDi+separator+tipoEnergia+separator+rifAnn+/*separator+impianti+*/"\n";
							}
						}
					}
				}
				
				
			}
			System.out.println("\t"+paragrafiTot.size()+" record inseriti");
		}
		
		
		return result;
	}
	
	
	public void creaFileParagrafo(String nomebollettino,String databollettino,String paragrafo){
		File f1 = new File("tutti/");
		 boolean bool = false;
		 if(!f1.exists())
			 bool = f1.mkdir();
		 PrintWriter writer;
		try {
			writer = new PrintWriter("tutti/"+nomebollettino+".bollettino", "UTF-8");
			writer.println(paragrafo);
			writer.close(); 
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	 
	}
	
	
	public String findFile(String fileName, String pathToFile) throws IOException, SAXException, TikaException{
		
		//String[] toFind = {/*"TUTURANO","ALERION ENERGIE RINNOVABILI",*/"ALTRATENSIONE",/*,"ANDRIA ENERGIA","BARDO","CERIGNOLA APOLLO 3","CERIGNOLA SUNLIGHT 1","EC SOLAR P1","EC SOLAR P2","EC SOLAR P3","ENERPOOL","ERA SRL","ERGYCA SIX","ERGYCA FIVE","ERGYCA SOLARE","ERGYCA THREE","FOTOSTAR 1","FOTOSTAR 2","MWP GIOIA","RENEWABLE ENERGY SOURCES","RENEWABLE ENERGY SOURCES","ALFA SOLAR","SIBA SHIPS","SOLAR ENERGY & PARTNERS","SOLARE DI MINERVINO","SOLON","SR12","SV FOGGIA","TETI"*/};
		
		
		
		BodyContentHandler handler = new BodyContentHandler(-1);
		
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(pathToFile));
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);

		String x = handler.toString();
		x = x.toUpperCase();
		
		ArrayList<Matcher> matchers = new ArrayList<Matcher>();
		
		String altraTensione = "((al(\\-|\\‐)*tra\\s*(\\-|\\‐)*ten(\\-|\\‐)*sio(\\-|\\‐)*ne)|0(\\-|\\‐)*6(\\-|\\‐)*9(\\-|\\‐)*2(\\-|\\‐)*1(\\-|\\‐)*6(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*2(\\-|\\‐)*0)";
		Pattern perTokenat = Pattern.compile(altraTensione, Pattern.CASE_INSENSITIVE);
		Matcher mat = perTokenat.matcher(x);
		matchers.add(mat);
		
		String tuturano = "((Tu(\\-|\\‐)*tu(\\-|\\‐)*ra(\\-|\\‐)*no)|(0(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*1(\\-|\\‐)*9(\\-|\\‐)*3(\\-|\\‐)*3(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*7))";
		Pattern perTokentut = Pattern.compile(tuturano, Pattern.CASE_INSENSITIVE);
		Matcher mTut = perTokentut.matcher(x);
		matchers.add(mTut);
		
		String alerion = "((A(\\-|\\‐)*le(\\-|\\‐)*ri(\\-|\\‐)*on(\\-|\\‐)*\\s*E(\\-|\\‐)*ner(\\-|\\‐)*gi(\\-|\\‐)*e(\\-|\\‐)*\\s*Rin(\\-|\\‐)*no(\\-|\\‐)*va(\\-|\\‐)*bi(\\-|\\‐)*li))";
		Pattern perTokenAle = Pattern.compile(alerion, Pattern.CASE_INSENSITIVE);
		Matcher mAle = perTokenAle.matcher(x);
		matchers.add(mAle);
		
		String andria = "((An(\\-|\\‐)*dri(\\-|\\‐)*a(\\-|\\‐)*\\s*E(\\-|\\‐)*ner(\\-|\\‐)*gi(\\-|\\‐)*a)|(0(\\-|\\‐)*2(\\-|\\‐)*3(\\-|\\‐)*0(\\-|\\‐)*4(\\-|\\‐)*9(\\-|\\‐)*2(\\-|\\‐)*0(\\-|\\‐)*6(\\-|\\‐)*9(\\-|\\‐)*3))";
		Pattern perTokenAnd = Pattern.compile(andria, Pattern.CASE_INSENSITIVE);
		Matcher mAnd = perTokenAnd.matcher(x);
		matchers.add(mAnd);
		
		String bardo = "((Bar(\\-|\\‐)*do))";
		Pattern perTokenBar = Pattern.compile(bardo, Pattern.CASE_INSENSITIVE);
		Matcher mBar = perTokenBar.matcher(x);
		matchers.add(mBar);
		
		String cerignolaA3 = "((Ce(\\-|\\‐)*ri(\\-|\\‐)*gno(\\-|\\‐)*la(\\-|\\‐)*\\s*A(\\-|\\‐)*pol(\\-|\\‐)*lo(\\-|\\‐)*\\s*3)|(1(\\-|\\‐)*0(\\-|\\‐)*8(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*7(\\-|\\‐)*8(\\-|\\‐)*1(\\-|\\‐)*0(\\-|\\‐)*0(\\-|\\‐)*5))";
		Pattern perTokenCA3 = Pattern.compile(cerignolaA3, Pattern.CASE_INSENSITIVE);
		Matcher mCA3 = perTokenCA3.matcher(x);
		matchers.add(mCA3);
		
		String cerignolaS1 = "((Ce(\\-|\\‐)*ri(\\-|\\‐)*gno(\\-|\\‐)*la(\\-|\\‐)*\\s*Sun(\\-|\\‐)*light(\\-|\\‐)*\\s*1)|(1(\\-|\\‐)*0(\\-|\\‐)*8(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*7(\\-|\\‐)*8(\\-|\\‐)*1(\\-|\\‐)*0(\\-|\\‐)*0(\\-|\\‐)*5))";
		Pattern perTokenCS1 = Pattern.compile(cerignolaS1, Pattern.CASE_INSENSITIVE);
		Matcher mCS1 = perTokenCS1.matcher(x);
		matchers.add(mCS1);
		
		String ecsolarp1 = "((Ec(\\-|\\‐)*\\s*So(\\-|\\‐)*lar(\\-|\\‐)*\\s*P1))";
		Pattern perTokenEcp1 = Pattern.compile(ecsolarp1, Pattern.CASE_INSENSITIVE);
		Matcher mEcp1 = perTokenEcp1.matcher(x);
		matchers.add(mEcp1);
		
		String ecsolarp2 = "((Ec(\\-|\\‐)*\\s*So(\\-|\\‐)*lar(\\-|\\‐)*\\s*P2))";
		Pattern perTokenEcp2 = Pattern.compile(ecsolarp2, Pattern.CASE_INSENSITIVE);
		Matcher mEcp2 = perTokenEcp2.matcher(x);
		matchers.add(mEcp2);
		
		String ecsolarp3 = "((Ec(\\-|\\‐)*\\s*So(\\-|\\‐)*lar(\\-|\\‐)*\\s*P3))";
		Pattern perTokenEcp3 = Pattern.compile(ecsolarp3, Pattern.CASE_INSENSITIVE);
		Matcher mEcp3 = perTokenEcp3.matcher(x);
		matchers.add(mEcp3);
		
		String enerpool = "((E(\\-|\\‐)*ner(\\-|\\‐)*pool)|(0(\\-|\\‐)*2(\\-|\\‐)*3(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*3(\\-|\\‐)*9(\\-|\\‐)*0(\\-|\\‐)*1(\\-|\\‐)*8(\\-|\\‐)*3))";
		Pattern perTokenEner = Pattern.compile(enerpool, Pattern.CASE_INSENSITIVE);
		Matcher mEner = perTokenEner.matcher(x);
		matchers.add(mEner);
		
		String era = "((E(\\-|\\‐)*ra(\\-|\\‐)*\\s*S(\\.)*r(\\.)*l(\\.)*))";
		Pattern perTokenEra = Pattern.compile(era, Pattern.CASE_INSENSITIVE);
		Matcher mEra = perTokenEra.matcher(x);
		matchers.add(mEra);
		
		String ergicaP = "((Er(\\-|\\‐)*gi(\\-|\\‐)*ca(\\-|\\‐)*\\s*Po(\\-|\\‐)*wer))";
		Pattern perTokenErgP = Pattern.compile(ergicaP, Pattern.CASE_INSENSITIVE);
		Matcher mErgp = perTokenErgP.matcher(x);
		matchers.add(mErgp);
		
		String ergycasun = "((Er(\\-|\\‐)*gy(\\-|\\‐)*ca(\\-|\\‐)*\\s*Sun))";
		Pattern perTokenErgSun = Pattern.compile(ergycasun, Pattern.CASE_INSENSITIVE);
		Matcher mesun = perTokenErgSun.matcher(x);
		matchers.add(mesun);
		
		String ergyca6 = "((Er(\\-|\\‐)*gy(\\-|\\‐)*ca(\\-|\\‐)*\\s*Six))";
		Pattern perTokenEr6 = Pattern.compile(ergyca6, Pattern.CASE_INSENSITIVE);
		Matcher mEr6 = perTokenEr6.matcher(x);
		matchers.add(mEr6);
		
		String ergyca5 = "((Er(\\-|\\‐)*gy(\\-|\\‐)*ca(\\-|\\‐)*\\s*Fi(\\-|\\‐)*ve)|(0(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*2(\\-|\\‐)*1(\\-|\\‐)*8(\\-|\\‐)*4(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*1))";
		Pattern perTokenEr5 = Pattern.compile(ergyca5, Pattern.CASE_INSENSITIVE);
		Matcher mEr5 = perTokenEr5.matcher(x);
		matchers.add(mEr5);
		
		String ergyca3 = "((Er(\\-|\\‐)*gy(\\-|\\‐)*ca(\\-|\\‐)*\\s*Three)|(0(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*2(\\-|\\‐)*1(\\-|\\‐)*8(\\-|\\‐)*2(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*3))";
		Pattern perTokenEr3 = Pattern.compile(ergyca3, Pattern.CASE_INSENSITIVE);
		Matcher mEr3 = perTokenEr3.matcher(x);
		matchers.add(mEr3);
		
		String ergycasol = "((Er(\\-|\\‐)*gy(\\-|\\‐)*ca(\\-|\\‐)*\\s*So(\\-|\\‐)*la(\\-|\\‐)*re)|(0(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*2(\\-|\\‐)*1(\\-|\\‐)*8(\\-|\\‐)*4(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*1))";
		Pattern perTokenEsol = Pattern.compile(ergycasol, Pattern.CASE_INSENSITIVE);
		Matcher mEsol = perTokenEsol.matcher(x);
		matchers.add(mEsol);
		
		String fotostar1 = "((Fo(\\-|\\‐)*to(\\-|\\‐)*star(\\-|\\‐)*\\s*1))";
		Pattern perTokenFot1 = Pattern.compile(fotostar1, Pattern.CASE_INSENSITIVE);
		Matcher mFot1 = perTokenFot1.matcher(x);
		matchers.add(mFot1);
		
		String fotostar2 = "((Fo(\\-|\\‐)*to(\\-|\\‐)*star(\\-|\\‐)*\\s*2))";
		Pattern perTokenFot2 = Pattern.compile(fotostar2, Pattern.CASE_INSENSITIVE);
		Matcher mFot2 = perTokenFot2.matcher(x);
		matchers.add(mFot2);
		
		String gioia = "((MWP(\\-|\\‐)*\\s*Gio(\\-|\\‐)*i(\\-|\\‐)*a)|(0(\\-|\\‐)*2(\\-|\\‐)*7(\\-|\\‐)*9(\\-|\\‐)*3(\\-|\\‐)*6(\\-|\\‐)*5(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*3(\\-|\\‐)*6))";
		Pattern perTokenGioia = Pattern.compile(gioia, Pattern.CASE_INSENSITIVE);
		Matcher mGioia = perTokenGioia.matcher(x);
		matchers.add(mGioia);
		
		String satel = "((Sa(\\-|\\‐)*tel(\\-|\\‐)*\\s*Re(\\-|\\‐)*ne(\\-|\\‐)*wa(\\-|\\‐)*ble)|(0(\\-|\\‐)*3(\\-|\\‐)*6(\\-|\\‐)*1(\\-|\\‐)*3(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*1(\\-|\\‐)*5))";
		Pattern perTokenSatel = Pattern.compile(satel, Pattern.CASE_INSENSITIVE);
		Matcher mSatel = perTokenSatel.matcher(x);
		matchers.add(mSatel);
		
		String renewable = "((Re(\\-|\\‐)*ne(\\-|\\‐)*wa(\\-|\\‐)*ble(\\-|\\‐)*\\s*E(\\-|\\‐)*ner(\\-|\\‐)*gy(\\-|\\‐)*\\s*Sour(\\-|\\‐)*ces)|(0(\\-|\\‐)*2(\\-|\\‐)*2(\\-|\\‐)*3(\\-|\\‐)*3(\\-|\\‐)*8(\\-|\\‐)*1(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*2))";
		Pattern perTokenRenew = Pattern.compile(renewable, Pattern.CASE_INSENSITIVE);
		Matcher mRenew = perTokenRenew.matcher(x);
		matchers.add(mRenew);
		
		String alfa = "((Al(\\-|\\‐)*fa(\\-|\\‐)*\\s*So(\\-|\\‐)*lar)|(0(\\-|\\‐)*3(\\-|\\‐)*6(\\-|\\‐)*1(\\-|\\‐)*3(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*1(\\-|\\‐)*5))";
		Pattern perTokenAlfa = Pattern.compile(alfa, Pattern.CASE_INSENSITIVE);
		Matcher mAlfa = perTokenAlfa.matcher(x);
		matchers.add(mAlfa);
		
		String siba = "((SI(\\-|\\‐)*BA(\\-|\\‐)*\\s*SHIPS))";
		Pattern perTokenSiba = Pattern.compile(siba, Pattern.CASE_INSENSITIVE);
		Matcher mSiba = perTokenSiba.matcher(x);
		matchers.add(mSiba);
		
		String SolarEP = "((So(\\-|\\‐)*lar(\\-|\\‐)*\\s*E(\\-|\\‐)*ner(\\-|\\‐)*gy(\\-|\\‐)*\\s*&\\s*Part(\\-|\\‐)*ners)|(0(\\-|\\‐)*2(\\-|\\‐)*2(\\-|\\‐)*5(\\-|\\‐)*7(\\-|\\‐)*2(\\-|\\‐)*8(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*9))";
		Pattern perTokenSEP = Pattern.compile(SolarEP, Pattern.CASE_INSENSITIVE);
		Matcher mSEP = perTokenSEP.matcher(x);
		matchers.add(mSEP);
		
		String solareM = "((So(\\-|\\‐)*la(\\-|\\‐)*re(\\-|\\‐)*\\s*di\\s*Mi(\\-|\\‐)*ner(\\-|\\‐)*vi(\\-|\\‐)*no))";
		Pattern perTokenSolarem = Pattern.compile(solareM, Pattern.CASE_INSENSITIVE);
		Matcher mSolareM = perTokenSolarem.matcher(x);
		matchers.add(mSolareM);
		
		String solon = "((So(\\-|\\‐)*lon))";
		Pattern perTokenSolon = Pattern.compile(solon, Pattern.CASE_INSENSITIVE);
		Matcher mSolon = perTokenSolon.matcher(x);
		matchers.add(mSolon);
		
		String sr12 = "((SR12)|(0(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*2(\\-|\\‐)*7(\\-|\\‐)*4(\\-|\\‐)*0(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*0))";
		Pattern perTokensr12 = Pattern.compile(sr12, Pattern.CASE_INSENSITIVE);
		Matcher msr12 = perTokensr12.matcher(x);
		matchers.add(msr12);
		
		String svfoggia = "((SV(\\-|\\‐)*\\s*Fog(\\-|\\‐)*gia)|(0(\\-|\\‐)*7(\\-|\\‐)*2(\\-|\\‐)*7(\\-|\\‐)*7(\\-|\\‐)*5(\\-|\\‐)*7(\\-|\\‐)*0(\\-|\\‐)*9(\\-|\\‐)*6(\\-|\\‐)*1))";
		Pattern perTokenSVF = Pattern.compile(svfoggia, Pattern.CASE_INSENSITIVE);
		Matcher mSVF = perTokenSVF.matcher(x);
		matchers.add(mSVF);
		
		String teti = "((Te(\\-|\\‐)*ti(\\-|\\‐)*\\s*S(\\.)*r(\\.)*l(\\.)*)|(0(\\-|\\‐)*6(\\-|\\‐)*8(\\-|\\‐)*8(\\-|\\‐)*5(\\-|\\‐)*8(\\-|\\‐)*3(\\-|\\‐)*0(\\-|\\‐)*7(\\-|\\‐)*2(\\-|\\‐)*6))";
		Pattern perTokenTeti = Pattern.compile(teti, Pattern.CASE_INSENSITIVE);
		Matcher mTeti = perTokenTeti.matcher(x);
		matchers.add(mTeti);
		
		
		String patternNBol = "(Bollettino\\s(.*)\\s([0-9]{1,5})(\\s+(s|\\-\\sv|p)))";
		Pattern perTokenNBol = Pattern.compile(patternNBol, Pattern.CASE_INSENSITIVE);
		Matcher m = perTokenNBol.matcher(fileName);
		String nBollettino = "";
		if(m.find()){
			 nBollettino = m.group(3);
		}		
		
		String result ="";
		for(int i=0; i<matchers.size(); i++){
			int count=0;
			String letto="";
			while(matchers.get(i).find()){
				count++;
				letto +=matchers.get(i).group(1)+",";
			}
			if(count>0) {
				//System.out.println(letto.replace("\n", "")+separator+fileName+separator+nBollettino+separator+count);
				result += letto.replace("\n", "")+separator+fileName+separator+nBollettino+separator+count+"\n";
			}
		
		}
		return result;
	}
	
	public void makeCSV(String outputNameFile,String header, String strToWrite) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputNameFile));
		if(header!=null){
			writer.write(header);
		}
		writer.write(strToWrite);
		writer.close();
	}
	
	public void disableLogWarn(){
		String[] loggers = { "org.apache.pdfbox.util.PDFStreamEngine",
	            "org.apache.pdfbox.pdmodel.font.PDSimpleFont",
	            "org.apache.pdfbox.pdmodel.font.PDFont",
	            "org.apache.pdfbox.pdmodel.font.PDTrueTypeFont",
	            "org.apache.pdfbox.pdmodel.font.PDType0Font",
	            "org.apache.pdfbox.pdmodel.font.PDType1Font",
	            "org.apache.pdfbox.pdmodel.font.PDType2Font",
	            "org.apache.pdfbox.pdmodel.font.PDType3Font",
	            "org.apache.pdfbox.pdmodel.font.FontManager",
	            "org.apache.pdfbox.pdfparser.PDFObjectStreamParser" };
	        for (String logger : loggers) {
	          org.apache.log4j.Logger logpdfengine = org.apache.log4j.Logger
	              .getLogger(logger);
	          logpdfengine.setLevel(org.apache.log4j.Level.OFF);
	        }
	}
	
	public void postElab(){
		String detnum;
		String datadet;
		String rif;
		String temp;
		ArrayList<String> numdet = new ArrayList<String>();
		numdet.add("10");
		numdet.add("19");
		numdet.add("20");
		numdet.add("203");
		numdet.add("209");
		numdet.add("224");
		numdet.add("23");
		numdet.add("246");
		numdet.add("247");
		numdet.add("249");
		numdet.add("25");
		numdet.add("250");
		numdet.add("257");
		numdet.add("28");
		numdet.add("285");
		numdet.add("286");
		numdet.add("287");
		numdet.add("308");
		numdet.add("37");
		numdet.add("440");
		numdet.add("613");
		numdet.add("82");
		numdet.add("83");
		numdet.add("85");
		numdet.add("137");
		numdet.add("84");
		
		String recDetNum;
		String recDataDet;
		
		for(int i=0; i<toUpdate.size(); i++){
			temp = toUpdate.get(i);
			detnum = temp.substring(0,temp.indexOf("#"));
			datadet = temp.substring(temp.indexOf("#")+1,temp.lastIndexOf("#"));
			if(datadet.startsWith("0")){
				datadet = datadet.substring(1);
			}
			datadet = datadet.replace(" ", "").toLowerCase();
			rif = temp.substring(temp.lastIndexOf("#")+1);
			System.out.println("to update "+i+" = "+temp);
			for(int j=0; j<recordsTot.size(); j++){
				recDetNum = recordsTot.get(j).getDeterminazioneNum();
				recDataDet = recordsTot.get(j).getDataDeterminazione();
				if(recDataDet.startsWith("0")){
					recDataDet = recDataDet.substring(1);
				}
				recDataDet = recDataDet.replace(" ", "").toLowerCase();
				if(numdet.contains(detnum) && recDetNum.equals(detnum)){
					System.out.println("\t-->\tcerco nel record "+j+" "+recDetNum+" - "+recDataDet);
				}
				//System.out.println("\tcerco nel record "+j+" "+recordsTot.get(j).getDeterminazioneNum()+" "+recordsTot.get(j).getDataDeterminazione());
				if(recDetNum.equals(detnum) && recDataDet.equals(datadet)){
					recordsTot.get(j).setRifAutorizzazioneUnica(rif);
					System.out.println("\t\tOK inserisco "+rif);
				}
			}
		}
	}
	
	public void print(){
		for(int j=0; j<recordsTot.size(); j++){
			System.out.println(recordsTot.get(j).recordToString());
		}
	}
	
   public static void main(final String[] args) throws IOException,TikaException, SAXException {
		
	   
	   Estrattore est = new Estrattore();
	   est.disableLogWarn();
	   String result = "";
	   File folderRegione = new File(regione);
	   File[] listOfYear = folderRegione.listFiles();
	   for(int j=0; j<listOfYear.length; j++){
		   //if(listOfYear[j].getName().equals("2012") || listOfYear[j].getName().equals("2016")){
		   File folder = new File(regione+"/"+/*anno*/listOfYear[j].getName());
		   File[] listOfFiles = folder.listFiles();
		   
			System.out.println("Analizzo cartella '"+folder.getName()+"', contiene " + listOfFiles.length + " file");
			for (int i = 0; i < listOfFiles.length; i++) {
				//System.out.println("Analizzo file '" + listOfFiles[i].getName()+"'");
				if (listOfFiles[i].isFile()) {
					result += est.parserRidottoPuglia(listOfFiles[i].getName(), listOfFiles[i].getAbsolutePath(),folder.getName());
				}
			}
		   //}
	   }	
	   
	   /*
	   for(int j=0; j<paragrafiTot.size(); j++){
		   System.out.println("CREO FILE");
		   est.creaFileParagrafo(paragrafiTot.get(j).getNomeBollettino(), paragrafiTot.get(j).getDataBollettino(),  paragrafiTot.get(j).getParagrafo());
		}*/
	   est.postElab();
	   result = "";
	   for(int j=0; j<recordsTot.size(); j++){
			result += recordsTot.get(j).recordToString();
		}
		est.makeCSV("outputPuglia.csv", Estrattore.header, result);
	   //est.print();
	   
		/*File folderRegione = new File(regione);
		File[] listOfYear = folderRegione.listFiles();
	    for(int j=0; j<listOfYear.length; j++){
		   File folder = new File(regione+"/"+listOfYear[j].getName());
		   File[] listOfFiles = folder.listFiles();
			System.out.println("Analizzo cartella "+listOfYear[j].getName()+", contiene " + listOfFiles.length + " file");
			for (int i = 0; i < listOfFiles.length; i++) {
				//System.out.println("\tAnalizzo file '" + listOfFiles[i].getName()+"'");
				if (listOfFiles[i].isFile()) {
					result += est.findFile(listOfFiles[i].getName(), listOfFiles[i].getAbsolutePath());
				}
			}
	    }
		est.makeCSV("outputPuglia.csv", Estrattore.headerFind, result);*/
		System.out.println("FINITO");

	   
   }
}