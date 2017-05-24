import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Paragrafi {

	private String nomeBollettino;
	private String numeroBollettino;
	private String dataBollettino;
	private String chiaveParagrafo;
	private String paragrafo;
	
	public Paragrafi(String nomeBollettino, String numBolletino, String databollettino, String chiaveParagrafo, String paragrafo){
		this.nomeBollettino = nomeBollettino;
		this.numeroBollettino = numBolletino;
		this.dataBollettino = databollettino;
		this.chiaveParagrafo = chiaveParagrafo;
		this.paragrafo = paragrafo;
		this.creaFileParagrafo(this.nomeBollettino,this.dataBollettino,this.paragrafo);
	}

	public String getNomeBollettino() {
		return nomeBollettino;
	}

	public void setNomeBollettino(String nomeBollettino) {
		this.nomeBollettino = nomeBollettino;
	}

	public String getNumeroBollettino() {
		return numeroBollettino;
	}

	public void setNumeroBollettino(String numeroBollettino) {
		this.numeroBollettino = numeroBollettino;
	}

	public String getDataBollettino() {
		return dataBollettino;
	}

	public void setDataBollettino(String dataBollettino) {
		this.dataBollettino = dataBollettino;
	}

	public String getChiaveParagrafo() {
		return chiaveParagrafo;
	}

	public void setChiaveParagrafo(String chiaveParagrafo) {
		this.chiaveParagrafo = chiaveParagrafo;
	}

	public String getParagrafo() {
		return paragrafo;
	}

	public void setParagrafo(String paragrafo) {
		this.paragrafo = paragrafo;
	}
	public void creaFileParagrafo(String nomebollettino,String databollettino,String paragrafo){
		File f1 = new File("paragrafi/");
		 boolean bool = false;
		 if(!f1.exists())
			 bool = f1.mkdir();
		 
		 
		 PrintWriter writer;
		try {
			writer = new PrintWriter("paragrafi/"+nomebollettino+".bollettino", "UTF-8");
			writer.println(paragrafo);
			writer.close(); 
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	}
	
	
	
}
