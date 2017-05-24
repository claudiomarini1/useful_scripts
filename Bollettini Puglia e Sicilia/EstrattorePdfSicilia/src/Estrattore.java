import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.Match;
import org.apache.sis.internal.util.X364;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.mchange.v1.util.ArrayUtils;


public class Estrattore {
	 static double cfile = 0;
	public static String result = "";
	public static String result2="";
	public static String separator ="|";
	public static final String header = "P.IVA"+separator+"Società"+separator+"Regione"+separator+"Provincia"+separator+"Comune"+separator+"Potenza [kW]"+separator+"Rif.File"+separator+"Bollettino num"+separator+"Data Bollettino"+separator+"Determinazione num"+separator+"Data Determinazione"+separator+"Mancato accoglimento"+separator+"Autorizzazione"+separator+"Annullamento"+separator+"Decadenza"+separator+"Revoca"+separator+"Rigetto"+separator+"Escussione"+separator+"Rif det num"+separator+"Rif data det"+separator+"Proroga"+separator+"Voltura"+separator+"In favore di"+separator+"Tipo Energia"+separator+"Rif Aut Unica\n"/*+separator+"Impianto\n"*/;

	public static ArrayList<Paragrafi> paragrafiTot = new ArrayList<Paragrafi>();
	
	public static ArrayList<Record> recordsTot = new ArrayList<Record>();
	
	public static ArrayList<String> toUpdate = new ArrayList<String>();
	
	public String[] getParagrafo(String pathToFile) throws IOException, SAXException, TikaException{
		BodyContentHandler handler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File(pathToFile));
		ParseContext pcontext = new ParseContext();
		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);

		String x = handler.toString();
		
		
		/*Espressioni regolari per prendere la parte di interesse*/
		String assessoratoEnergia="(as(\\-)*ses(\\-)*so(\\-)*ra(\\-)*to\\sdel(\\-)*l.{1}e(\\-)*ner(\\-)*gi(\\-)*a)";
		String termineParagrafo = "(ASSESSORATO\\sDELL.ISTRUZIONE|ASSESSORATO\\sDELLA\\sSALUTE|ASSESSORATO\\sDEL\\sTURISMO|ASSESSORATO\\s*DEL\\sTERRITORIO\\sE\\sDELL.AMBIENTE|ASSESSORATO\\s*DELLE\\sINFRASTRUTTURE\\sE\\sDELLA\\sMOBILIT.|ASSESSORATO\\sDELLA\\sFAMIGLIA)";//(as(\\-)*ses(\\-)*so(\\-)*ra(\\-)*to\\s*del)";
		Pattern r = Pattern.compile(assessoratoEnergia,Pattern.CASE_INSENSITIVE );
		String []token = r.split(x);
		
		//System.out.println("Trovati "+token.length);
		
		r=Pattern.compile(termineParagrafo,Pattern.CASE_INSENSITIVE);
		
		if(token.length==1){
			return null;
		}
		
		String[] paragrafointeresse = r.split(token[token.length-1]);
		
		if(token.length==5){
			paragrafointeresse = new String[1];
			String[] tmp1=r.split(token[3]);
			String[] tmp2 = r.split(token[4]);
			//System.out.println("1jjjjj"+tmp1[0].replaceAll("\n", ""));
			//System.out.println("2jjjjj"+tmp2[0].replaceAll("\n", ""));
			paragrafointeresse [0]=tmp1[0]+tmp2[0];
		}
		if(token.length==7){
			paragrafointeresse = new String[1];
			String p = token[3]+token[4]+token[5]+token[6];
			
			/*System.out.println("1jjjjj"+token[3].replaceAll("\n", ""));
			System.out.println("2jjjjj"+token[4].replaceAll("\n", ""));
			System.out.println("3jjjjj"+token[5].replaceAll("\n", ""));
			System.out.println("4jjjjj"+token[6].replaceAll("\n", ""));
*/
			paragrafointeresse[0]=r.split(p)[0];
	//		System.out.println("PARAGRADFO DI INTERERSEE "+paragrafointeresse[0]);
		}
		
		String patterSuddividiParagrafo ="(\\([0-9]{4}\\.\\s*[0-9]{1,2}\\.\\s*[0-9]{2,4}\\)\\s*[0-9]{3})";
		r = Pattern.compile(patterSuddividiParagrafo,Pattern.CASE_INSENSITIVE);
		Matcher tmp = r.matcher(paragrafointeresse[0]);
		while(tmp.find()){
			//System.out.println("paragrafi individuati "+tmp.group());
		}
		String [] paragrafi = r.split(paragrafointeresse[0]);
		System.out.println(paragrafi.length);
		
		return paragrafi;
	}
	
	public String parserSicilia(String pathToFile,String filename) throws IOException, SAXException, TikaException{
		//System.out.println("\t"+pathToFile+" - "+filename);
		
		String[] par = getParagrafo(pathToFile);
		String ret = elaboraParagrafi(par, filename);

		return  ret;
		
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
	
	
	
	public String elaboraParagrafi(String[] par,String filename){
		String ret="";
		String pi="",soc="",prov="",com="",pot="",file="",bnum="",bdata="",manacc="",aut="",dec="",ann="",rev="",rig="",esc="",pror="",volt="",infavdi="",tipoener="",detNum="",dataDet="";
		String rif="";
		
		String autorizzazioneUnica="(a(\\-|\\s)*u(\\-|\\s)*t(\\-|\\s)*o(\\-|\\s)*r(\\-|\\s)*i(\\-|\\s)*z(\\-|\\s)*z(\\-|\\s)*a(\\-|\\s)*z(\\-|\\s)*i(\\-|\\s)*o(\\-|\\s)*n(\\-|\\s)*e(\\-|\\s)*u(\\-|\\s)*n(\\-|\\s)*i(\\-|\\s)*c(\\-|\\s)*a(\\-|\\s)*)";
		String decadenza ="(de(\\-)*ca(\\-)*den(\\-)*za*)|(de(\\-)*ca(\\-)*du(\\-)*ta)|(de(\\-)*ca(\\-)*de)";
		String revoca = "(re(\\-)*vo(\\-)*ca)";
		String annullamento="(an(\\-)*nul(\\-)*la(\\-)*men(\\-)*to)";
		String proroga = "(pro(\\-)*ro(\\-)*ga)";
		String voltura = "(\\S*)(vol(\\-|\\‐)*tu(\\-|\\‐)*ra)(\\s|\\,)*";
		String patternIFD = "(\\S*)((((in|a)\\s*fa(\\-|\\‐)*vo(\\-|\\‐)*re\\s*d(i|el(\\-|\\‐)*la))|(al(\\-|\\‐)*la\\s*(s|S)o(\\-|\\‐)*cie(\\-|\\‐)*tà))(\\:*)(\\s*)(\\“*)(.*?))(\\,|\\,\\s|\\”|con|\\-|\\.\\s*\\.)";
		String escussione = "(\\S*)((e(\\-|\\‐)*s(\\-|\\‐)*cus(\\-|\\‐)*sio(\\-|\\‐)*ne))(\\s|\\,)"; 
		String societa = "((so(\\-)*cie(\\-)*tà\\s*)|dit(\\-)*ta\\s*|\\sd*al\\-*la\\s*|as\\-*soc\\-*ia\\-*zio\\-*ne\\s*)(.+)\\s*\\n*(.*((a|s)\\.*\\s*(r|p)\\.*\\s*(l|a)\\.*)*\\W|\\,)";
		//String piva = "(p.\\siva\\s*([0-9]{11}))";
		String piva = "((P(.|artita)\\s{0,1}I\\.*V*\\.*A*\\.*)|cod\\.*\\s*fisc\\.*\\s*|co(\\-)*di(\\-)*ce(\\-)*\\s*fi(\\-)*sca(\\-)*le|(\\s*C\\.F\\.\\s*|n\\.\\s*))\\s*(e\\s*C\\.F\\.\\s*|n\\.\\s*)*.*([A-Z]{0,2}[0-9]{11})(\\s|\\.)*.*?";
		String mancatoaccoglimento="(man(\\-)*ca(\\-)*to(\\-)*\\s*ac(\\-)*co(\\-)*gli(\\-)*men(\\-)*to)";

		String condecreto="(n\\.\\s*([0-9]{1,4})\\s*del(\\-)*(l.)*\\s*([0-9]{1,2}\\s*.{5,10}\\s*[0-9]{4}))";
		
		String potenza ="(([0-9]{0,3}(\\.*|\\,*|\\’*)){1,2}[0-9]{0,3}(\\.*|\\,*|\\’*)[0-9]{1,4})\\s*(kw|mw|w)";
		String potenza2 ="(kw|mw|w).\\s*([0-9]{0,4}(\\.*|\\,*)[0-9]{0,4}(\\.*|\\,*)[0-9]{1,4})";
		
		String tipoimpianto ="(fo(\\-)*\\n*to(\\-)*\\n*vol(\\-)*\\n*ta(\\-)*\\n*ic.|eo(\\-)*\\n*li(\\-)*\\n*c.|bio(\\-)*\\n*mas(\\-)*\\n*s.|com(\\-)*\\n*pos(\\-)*\\n*tag(\\-)*\\n*gio)";
		String comunedi="(co(\\-|\\‐)*mu(\\-|\\‐)*n(e|i)\\s*di|lo(\\-|\\‐)*ca(\\-|\\‐)*li(\\-|\\‐)*t.)\\s*\\n*(((\\S+\\s*){1,6})\\s*(\\(|\\-|\\.|\\,))";
				//"co(\\-)*mu(\\-)*n(e|i)\\s*di\\s*((\\S+\\s*){1,4}\\(|\\-|\\.)";
		//"co(\\-)*mu(\\-)*n(e|i)\\s*di\\s*((.+)\\n(.+))\\W|\\n";
		
		
		//String provincia="\\(([A-Za-z]{1}[A-Za-z]{1})\\)\\,*";
		String provincia = "\\(([a-zA-Z]{2})\\)(\\s|\\,|\\.|\\“|\\;)*.*?";
		
		
		String patternNBol = "(Bollettino\\s(.*)\\s([0-9]{1,5})(\\s+(s|\\-\\sv|p)))";
	//	String patternNBol
		String patternData = "il\\s([0-9]{1,2}\\s*\\-*[A-Za-z]{4,9}\\s*\\-*[0-9]{4})"; //"il\\s*([0-9]{1,2}\\-*.*\\-*[0-9]{4})";		
		
		
		//String riferimentoAutirizzazione= "con\\s(D\\.R\\.S\\.|decreto)\\sn\\.\\s([0-9]{1,3})\\sdel\\s([0-9]{1,2}\\s[a-z|A-Z]{5,9}\\s[0-9]{4})";
		String riferimentoAutirizzazione="\\s(D\\.R\\.S\\.|decreto|D\\.lgs\\.)\\sn\\.\\s([0-9]{1,3}\\/[0-9]{1,4}|[0-9]{1,3})(\\sdel\\s([0-9]{1,2}\\s*[a-z|A-Z]{5,9}\\s*[0-9]{4})){0,1}";
		
		file = filename;
		
		Pattern pattern;
		Matcher mat ;
		pattern = Pattern.compile(patternNBol,Pattern.CASE_INSENSITIVE);
		mat = pattern.matcher(filename);
		if(mat.find()){
			bnum=mat.group(3).replaceAll("\n", "");
			//System.out.println("bollettino numero "+bnum);
		}
		
		pattern = Pattern.compile(patternData,Pattern.CASE_INSENSITIVE);
		mat = pattern.matcher(filename);
		if(mat.find()){
			bdata=mat.group(1).replaceAll("\n", "");
			//System.out.println("data numero "+mat.group(1));
		}
		
		
		
		
		
		
		/*String patternAut = "(\\S*)(((a|A){1}(\\-|\\‐)*u(\\-|\\‐)*to(\\-|\\‐)*riz(\\-|\\‐)*za(\\-|\\‐)*zio(\\-|\\‐)*ne\\s*(u|U)(\\-|\\‐)*ni(\\-|\\‐)*ca))(\\s|\\,)*";
		String patternDec = "(\\S*)((de(\\-|\\‐)*ca(\\-|\\‐)*den(\\-|\\‐)*za)|(an(\\-|\\‐)*nul(\\-|\\‐)*la(\\-|\\‐)*men(\\-|\\‐)*to)|(ri(\\-|\\‐)*get(\\-|\\‐)*ta(\\-|\\‐)*t[a-z]{1})|(re(\\-|\\‐)*vo(\\-|\\‐)*ca))(\\s|\\,)";
		String patternPror = "(\\S*)(pro(\\-|\\‐)*ro(\\-|\\‐)*ga)(\\s|\\,)*";
		String patternVol = "(\\S*)(vol(\\-|\\‐)*tu(\\-|\\‐)*ra)(\\s|\\,)*";
		String patternPiva = "((P(.|artita)\\s{0,1}I\\.*V*\\.*A*\\.*)|(\\s*C\\.F\\.\\s*|n\\.\\s*))\\s*(e\\s*C\\.F\\.\\s*|n\\.\\s*)*.*([A-Z]{0,2}[0-9]{10,11})(\\s|\\.)*.*?";
		String patternSoc = "(\\S*)(((pro(\\-|\\‐)*po(\\-|\\‐)*nen(\\-|\\‐)*te)|(S|s)(o(\\-|\\‐)*cie(\\-|\\‐)*tà\\spro(\\-|\\‐)*po(\\-|\\‐)*nen(\\-|\\‐)*te|o(\\-|\\‐)*cie(\\-|\\‐)*tà|oc\\.*))(\\:*)(\\s*)(\\“*)(.*?))($|\\,|\\,\\s|\\”|(v|V)ol(\\-|\\‐)*tu(\\-|\\‐)*ra|con|\\-|\\(|\\)|\\.\\s*\\.)";
		String patternPro = "\\(([a-zA-Z]{2})\\)(\\s|\\,|\\.|\\“|\\;)*.*?";
		String patternCom = "((C|c)omun[a-z]{1}\\s*di\\s*(.+)(\\s*))(\\([a-zA-Z]{2}\\))*";
		String patternPot = "((\\d{1,3}(\\,|\\.){0,1}\\d{0,5}\\s*MW)|((\\d{1,3}\\.)*\\d{1,3}\\,{0,1}\\d{0,5}\\s*(k|K)W)).*";
		String patternTip = "(\\S*|\\()(fo(\\-|\\‐)*to(\\-|\\‐)*vol(\\-|\\‐)*ta(\\-|\\‐)*i(\\-|\\‐)*c[a-z]{1}|e(\\-|\\‐)*o(\\-|\\‐)*li(\\-|\\‐)*c[a-z]{1}|bio(\\-|\\‐)*mas(\\-|\\‐)*s[a-z]{1}|bio(\\-|\\‐)*gas|oli(\\-|\\‐)*[a-z]{0,1}(\\-|\\‐)*ve(\\-|\\‐)*ge(\\-|\\‐)*ta(\\-|\\‐)*l[a-z]{0,1})(\\s|\\,|\\))*";
		String patternIFD = "(\\S*)((((in|a)\\s*fa(\\-|\\‐)*vo(\\-|\\‐)*re\\s*d(i|el(\\-|\\‐)*la))|(al(\\-|\\‐)*la\\s*(s|S)o(\\-|\\‐)*cie(\\-|\\‐)*tà))(\\:*)(\\s*)(\\“*)(.*?))(\\,|\\,\\s|\\”|con|\\-|\\.\\s*\\.)";
		*/
		
		
		
		boolean autuni = false;
		if(par!=null){
			for(int i=0;i<par.length;i++){
				
				String chiaveparagrafo = filename+"#"+i+1;
				paragrafiTot.add(new Paragrafi(filename, bnum, bdata, chiaveparagrafo, par[i]));
				
				//System.out.println("##############\n"+par[i]+"\n******\n");
				boolean cerca_riferimento = false;
				pi="";soc="";prov="";com="";pot="";manacc="";aut="";dec="";ann="";rev="";rig="";esc="";;pror="";volt="";infavdi="";tipoener="";detNum="";dataDet="";
				autuni = false;
				pattern = Pattern.compile(autorizzazioneUnica,Pattern.CASE_INSENSITIVE);
				mat = pattern.matcher(par[i]);
				if(mat.find()){
					aut = "Autorizzazione unica";
				//	System.out.println("Trovo autorizzazione unica "+mat.group(1));
					creaFileParagrafo(chiaveparagrafo.substring(chiaveparagrafo.lastIndexOf("\\")+1),"neno",par[i]);
					/*PrintWriter writer;
					try {
						writer = new PrintWriter(filename+".txt", "UTF-8");
						writer.println(par[i]);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
					autuni=true;
				}
				
				pattern = Pattern.compile(mancatoaccoglimento,Pattern.CASE_INSENSITIVE);
				mat = pattern.matcher(par[i]);
				if(mat.find()){
					manacc = "Mancato accoglimento";
					//System.out.println("Trovo mancato accoglimento "+mat.group(1));
					//autuni=true;
				}
				
				
				pattern = Pattern.compile(escussione,Pattern.CASE_INSENSITIVE);
				mat = pattern.matcher(par[i]);
				if(mat.find()){
					esc = "Escussione";
					//System.out.println("Trovo mancato accoglimento "+mat.group(1));
					//autuni=true;
				}
				
				if(autuni){
					boolean volturato=false;
					pattern = Pattern.compile(voltura,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						volturato=true;
						volt="Voltura";
						cerca_riferimento = true;
						//System.out.println("\tE' una decadenza");
					}
					
					if(volturato){
						pattern = Pattern.compile(patternIFD,Pattern.CASE_INSENSITIVE);
						mat = pattern.matcher(par[i]);
						if(mat.find()){
							infavdi=mat.group(18).replaceAll("\n", "");
							//System.out.println("\tE' una decadenza");
						}
					}
					
					
					pattern = Pattern.compile(decadenza,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						dec="Decadenza";
						cerca_riferimento = true;

						//System.out.println("\tE' una decadenza");
					}
					
					
					pattern = Pattern.compile(revoca,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						rev = "Revoca";
						cerca_riferimento = true;

						//	System.out.println("\tE' una revoca");
					}
					
					pattern = Pattern.compile(annullamento,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						ann="Annullamento";
						cerca_riferimento = true;

						//System.out.println("\tE' un annullamento");
					}
					
					pattern = Pattern.compile(proroga,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						pror="Proroga";
						cerca_riferimento = true;

						//System.out.println("\tE' una proroga");
					}
					
					pattern = Pattern.compile(societa,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						soc=mat.group(6).replace("\n", "")+" "+mat.group(7).replace("\n", "");
						soc = soc.substring(0,soc.indexOf(",")>=0?soc.indexOf(","):soc.length());
						//System.out.println(soc);
						if(soc.indexOf("con sede")>0)
							soc = soc.substring(0,soc.indexOf("con sede"));
						if(soc.indexOf("per la")>0)
							soc = soc.substring(0,soc.indexOf("per la"));
						if(soc.indexOf("per gli")>0)
							soc = soc.substring(0,soc.indexOf("per gli"));
						if(soc.indexOf("con il")>0)
							soc = soc.substring(0,soc.indexOf("con il"));
						if(soc.indexOf("società")>=0)
							soc = soc.replaceAll("società\\s","");
						if(soc.indexOf("ditta")>=0)
							soc = soc.replaceAll("ditta\\s","");
						if(soc.indexOf("soc.")>=0)
							soc = soc.replaceAll("soc.\\s","");
						
					}
					
					pattern = Pattern.compile(piva,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						pi=mat.group(11).replace("\n", "");
						//System.out.println("\tla partita iva è :"+mat.group(6));
					}
					
					
					if(cerca_riferimento){

						pattern = Pattern.compile(riferimentoAutirizzazione,Pattern.CASE_INSENSITIVE);
						mat = pattern.matcher(par[i]);
						String num = "";
						String datanum="";
						rif="";
						boolean first = true;
						while(mat.find()){
							if(mat.group(4)!=null){
								/*if(first)
									System.out.println("con DRS numero "+mat.group(2).replaceAll("\n", "")+" del "+mat.group(4).replaceAll("\n", "") );
								else
									System.out.println("e con DRS numero "+mat.group(2).replaceAll("\n", "")+" del "+mat.group(4).replaceAll("\n", "") );*/
								num= mat.group(2).replaceAll("\n", "");
								datanum= mat.group(4).replaceAll("\n", "");
								rif = mat.group(2).replaceAll("\n", "")+" del "+mat.group(4).replaceAll("\n", "");
								
							}
							else{
								rif = mat.group(2).replaceAll("\n", "");
							}
						}
						
						if(!toUpdate.contains(num+"#"+datanum+"#"+rif)){toUpdate.add(num+"#"+datanum+"#"+rif);}
						

					}
					else{
						rif="";
					}
					
					boolean pot_meausure_unit = false;
					boolean pot_valida=false;
					pattern = Pattern.compile(potenza,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					String potenzatmp="";
					if(mat.find()){
						pot_meausure_unit = true;
						pot_valida=true;
						pot=mat.group(1).replaceAll("\n", "")+" "+mat.group(5).replaceAll("\n", "");
						//System.out.println(pot +" è questa");
						potenzatmp=mat.group(1).replaceAll("\n", "");
						//System.out.println("\tla potenza è :"+mat.group(1)+" "+mat.group(4));
					}
					
					if(potenzatmp.equals("")){
						//System.out.println("non la trovo e provo al contrario");
						pattern = Pattern.compile(potenza2,Pattern.CASE_INSENSITIVE);
						mat = pattern.matcher(par[i]);
						if(mat.find()){
							pot_valida=true;
							//System.out.println("Ora la trovo al contrario");
							pot=mat.group(2).replaceAll("\n", "")+" "+mat.group(1).replaceAll("\n", "");
							//System.out.println("\tla potenza2 è :"+mat.group(2)+" "+mat.group(1));
						}
					}
					
					pot = pot.replace(" ", "").toUpperCase();
					
					pot = pot.replace("’", "").toUpperCase();

					if(pot_valida){
						
						if(!pot.contains("MW") && pot.contains("KW")) 
							pot = pot.substring(0,(pot.indexOf("W")-1));
						else {
							pot = pot.replace(".", "");
							if(StringUtils.countMatches(pot, ",")>1 ){
								pot = pot.replace(",", "");
							}
							if(pot.contains("MW")){
								Double val = Double.parseDouble(pot.substring(0, pot.indexOf("MW")).replace(",", "."));
								val = val * 1000;
								pot = ""+ val;
								pot=pot.replace(".", ",");
							}
							else{
								Double val = Double.parseDouble(pot.substring(0, pot.indexOf("W")).replace(",", "."));
								val = val / 1000;
								pot = ""+ val;
								pot=pot.replace(".", ",");
							}
							
						}
					}
					/*else if(!pot_meausure_unit && pot_valida){
						System.out.println("é giusto");
						if(!pot.contains("MW") && pot.contains("KW")) 
							pot = pot.substring(potenza.indexOf("W")+1);
						else {
							pot = pot.replace(".", "");
							System.out.println("AAAAA"+pot);
							if(pot.contains("MW")){
								Double val = Double.parseDouble(pot.substring(pot.indexOf("W")+1).replace(",", "."));
								val = val * 1000;
								pot = ""+ val;
								pot=pot.replace(".", ",");
							}
							else{
								Double val = Double.parseDouble(pot.substring(pot.indexOf("W")+1).replace(",", "."));
								val = val / 1000;
								pot = ""+ val;
								pot=pot.replace(".", ",");
							}
						}
					}*/
					
					
					pattern = Pattern.compile(tipoimpianto,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						tipoener=mat.group(1).replaceAll("\n", "");
						//System.out.println("\tla il tipo impianto è :"+mat.group(1));
					}
					
					pattern = Pattern.compile(comunedi,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i].replaceAll("\n",""));
					if(mat.find()){
						com=mat.group(9).replace("\n", "");
						com = com.substring(0,com.indexOf(".")>=0?com.indexOf("."):com.length());
						com = com.substring(0,com.indexOf(",")>=0?com.indexOf(","):com.length());
						com = com.replace("-", "");
						//System.out.println("\tcomune di :"+mat.group(4));
					}
					
					pattern = Pattern.compile(provincia,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					while(mat.find()){
						prov=mat.group(1).replaceAll("\n", "");
						//System.out.println("\tprovincia di :"+mat.group());
					}
					
					
					pattern = Pattern.compile(condecreto,Pattern.CASE_INSENSITIVE);
					mat = pattern.matcher(par[i]);
					if(mat.find()){
						detNum=mat.group(2).replaceAll("\n", ""); 	
						dataDet = mat.group(5).replaceAll("\n", "");
					}
					
					String reg="SICILIA";
					recordsTot.add(new Record(pi, soc, reg, prov, com, pot, file, bnum, bdata, detNum, dataDet, manacc, aut, ann, dec, rev, rig, esc, ""/*escDetNum*/, ""/*escDataDet*/, pror, volt, infavdi, tipoener, rif, chiaveparagrafo));
					
					ret+=pi+separator+soc+separator+prov+separator+com+separator+pot+separator+file+separator+bnum+separator+bdata+separator+detNum+separator+dataDet+separator+manacc+separator+aut+separator+ann+separator+dec+separator+rev+separator+rig+separator+esc+separator+pror+separator+volt+separator+infavdi+separator+tipoener+separator+rif+"\n";
				}
				else{
					//System.out.println("Non ci sta niente");
				}
				
			}
		}
		return ret;
	}
	
	
	
	
	public String parserHTML(String filepath) throws IOException{
		int trovati = 0;
		File input = new File(filepath);
		Document doc = Jsoup.parse(input, "UTF-8", "");
		Element body = doc.body();
		String baselink = "http://www.gurs.regione.sicilia.it/Gazzette/";
		String[] paragrafi= null;
		ArrayList<String> para = new ArrayList<>();
		System.out.println("elaboro file "+filepath);
		if(body.text().contains("Autorizzazione unica")){
			System.out.println("trovo autorizzazione unica");
			Elements links =doc.select("a");
			for (Element link : links){
				if(link.attr("href").startsWith("g")){
					String att = link.attr("href");
					String url = baselink + att.substring(0,att.lastIndexOf("-"))+"/"+att;
					System.out.println(url);
					Document document = Jsoup.parse(new URL(url), 10000);
					Element corpo = document.body();
					if(/*corpo.text().contains("Autorizzazione unica")&& */corpo.text().toUpperCase().contains("ASSESSORATO DELL'INDUSTRIA")){
						String bd = corpo.getElementsByTag("dd").get(0).text();
						//System.out.println("\n#####PARAGRAFO\n"+bd);
						para.add(bd);
					}
				}
			}
		}
		
		
		paragrafi = new String[para.size()];
		paragrafi = para.toArray(paragrafi);
		
		return elaboraParagrafi(paragrafi, filepath);
	}
	
	public void makeCSV(String outputNameFile,String header, String strToWrite) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputNameFile));
		if(header!=null){
			writer.write(header);
		}
		writer.write(strToWrite);
		writer.close();
	}
	
	public void makeText(String outputNameFile, String strToWrite) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputNameFile));
		writer.write(strToWrite);
		writer.close();
	}
	
	
	
	
	static void updateProgress(double progressPercentage) {
	    final int width = 11; // progress bar width in chars
	    System.out.print("\r[");
	    int i = 0;
	    for (; i <= (int)(progressPercentage*width); i++) {
	      System.out.print(".");
	    }
	    for (; i < width; i++) {
	      System.out.print(" ");
	    }
	    System.out.print("]");
	}
	
	
	public void sendElastic(String pathToFile,String filename) throws IOException, SAXException, TikaException{
		//System.out.println("\t"+pathToFile+" - "+filename);
		
		String[] par = getParagrafo(pathToFile);
		if(par!=null){
			for(int i=0;i<par.length;i++){
				System.out.println(par[i]);
				PrintWriter writer = new PrintWriter(filename+".txt", "UTF-8");
				writer.println(par[i]);
				writer.close();
//				httpPut, filename);	
			}
		}
	}
	
	
	public void postElab(){
		String detnum;
		String datadet;
		String rif;
		String temp;
		String matchNum, matchData;
		boolean slash=false;
		for(int i=0; i<toUpdate.size(); i++){
			temp = toUpdate.get(i);
			
			slash=false;
			
			detnum = temp.substring(0,temp.indexOf("#"));
			datadet = temp.substring(temp.indexOf("#")+1,temp.lastIndexOf("#"));
			
			if(temp.contains("/")){
				slash=true;
				detnum = temp.substring(temp.lastIndexOf("#")+1,temp.indexOf("/"));
				datadet = temp.substring(temp.indexOf("/")+1);
			}
			if(datadet.startsWith("0"))
				datadet =  datadet.substring(1);
			datadet = datadet.replace(" ", "").toLowerCase();
			rif = temp.substring(temp.lastIndexOf("#")+1);
			System.out.println("to update "+i+" = "+temp);
			if(i==16){
				System.out.println("Slash = "+slash + " num "+detnum + " data "+datadet+ " rif "+rif);
			}
			for(int j=0; j<recordsTot.size(); j++){
				matchNum=recordsTot.get(j).getDeterminazioneNum();
				matchData=recordsTot.get(j).getDataDeterminazione();
				if(matchData.startsWith("0"))
					matchData =  matchData.substring(1);
				matchData = matchData.replace(" ", "").toLowerCase();				
				//System.out.println("\tcerco nel record "+j+" "+recordsTot.get(j).getDeterminazioneNum()+" "+recordsTot.get(j).getDataDeterminazione());
				if(!slash){
					if(matchNum.equals(detnum) && matchData.equals(datadet)){
						recordsTot.get(j).setRifAutorizzazioneUnica(rif);
						System.out.println("\t\tOK inserisco "+rif);
					}
				}
				else{
					if(matchNum.equals(detnum) && matchData.endsWith(datadet)){
						recordsTot.get(j).setRifAutorizzazioneUnica(rif);
						System.out.println("\t\tOK inserisco "+rif);
					}
				}
			}
		}
	}
	
	public void httpPut(String par,String filename) throws IOException{
		/*Random random = new Random();
        URL url = new URL("http://cdh-hub.westeurope.cloudapp.azure.com:9200/rassegna/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        Gson gson = new Gson();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());*/
        long millis = System.currentTimeMillis() % 1000;
        /*String payload = gson.toJson(String.format("{'user': 'cdh-user','timestamp': "+millis+" , 'message':"+par+",'bollettino': "+filename+"}"));
        
        osw.write(payload);
        osw.flush();
        osw.close();
        */
        
        
        URL myURL = new URL("http://cdh-hub.westeurope.cloudapp.azure.com:9200/rassegna");
        HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
        myURLConnection.setRequestMethod("PUT");
        myURLConnection.setRequestProperty("Content-Type", "application/json");
        myURLConnection.setUseCaches(false);
        myURLConnection.setDoInput(true);
        myURLConnection.setDoOutput(true);
        myURLConnection.connect();

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("user", "cdh-user");
        jsonParam.put("timestamp", millis);
        jsonParam.put("message",par);
        jsonParam.put("bollettino", filename);

        OutputStreamWriter os = new OutputStreamWriter(myURLConnection.getOutputStream());
        os.write(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
        os.close();
        
        
        System.err.println(myURLConnection.getResponseCode());
	}
	
	public static void main(final String[] args) throws IOException,TikaException, SAXException {
		
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
	   
	   
	   Estrattore est = new Estrattore();
	   
	
	 /*  Files.walk(Paths.get("Sicilia/2016")).forEach(filePath -> {	
				    if (Files.isRegularFile(filePath)) {
				    	cfile++;
				    	
		   try {
		   	//System.out.println("TOT File elaborati "+cfile);
		   	//System.out.println("\n#######Elaborazione "+ filePath.getFileName());
		   	if(filePath.getFileName().toString().endsWith("pdf") || filePath.getFileName().toString().endsWith("PDF")){
		   		est.sendElastic(filePath.toString(),filePath.getFileName().toString());
		   	}
		   	
		   	//System.out.println("#######\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		});

	   
	   try {
		   System.out.println("SORNMO UCCIDIMI");
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	   
	   
	Files.walk(Paths.get("Sicilia")).forEach(filePath -> {	
					    if (Files.isRegularFile(filePath)) {
					    	cfile++;
					    	
		        try {
		        	//System.out.println("TOT File elaborati "+cfile);
		        	//System.out.println("\n#######Elaborazione "+ filePath.getFileName());
		        	if(filePath.getFileName().toString().endsWith("pdf") || filePath.getFileName().toString().endsWith("PDF")){
		        		result +=  est.parserSicilia(filePath.toString(),filePath.getFileName().toString());
		        	}
		        	else{
		        		//System.out.println("apro il file non PDF");*/
		        		result += est.parserHTML(filePath.toString());

		        		//System.out.println("File HTML, tag "+est.parserHTML(filePath.toString()));
		        	}
		        	//System.out.println("#######\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TikaException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		    }

		});
	   est.postElab();
	   
	   result = "";
	   for(int j=0; j<recordsTot.size(); j++){
			result += recordsTot.get(j).recordToString();
		}
		est.makeCSV("outputSicilia.csv", Estrattore.header, result);
	   //est.print();
		
	   est.makeText("outputNewVersion.csv", header+result);
   }
}