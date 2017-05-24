
public class Record {

	public static final String separator = "|";
	private String partitaIva;
	private String azienda;
	private String regione;
	private String provincia;
	private String comune;
	private String potenza;
	private String rifFile;
	private String bollettinoNum;
	private String dataBollettino;
	private String determinazioneNum;
	private String dataDeterminazione;
	private String mancatoAccoglimento;
	private String autorizzazioneUnica;
	private String annullamento;
	private String decadenza;
	private String revoca;
	private String rigetto;
	private String escussione;
	private String escussioneDetNum;
	private String escussioneDataDet;
	private String proroga;
	private String voltura;
	private String inFavoreDi;
	private String tipoEnergia;
	private String rifAutorizzazioneUnica;
	private String chiaveParagrafo;
	
	public Record(String partitaIva, String azienda,String regione, String provincia, String comune, String potenza, String rifFile, String bollettinoNum, String dataBollettino, String determinazioneNum, String dataDeterminazione, String mancatoAccoglimento, String autorizzazioneUnica, String annullamento, String decadenza, String revoca, String rigetto, String escussione, String escussioneDetNum, String escussioneDataDet, String proroga, String voltura, String inFavoreDi, String tipoEnergia, String rifAutorizzazioneUnica, String chiaveParagrafo){
		this.partitaIva = partitaIva;
		this.azienda = azienda;
		this.regione = regione;
		this.provincia = provincia;
		this.comune = comune;
		this.potenza = potenza;
		this.rifFile = rifFile;
		this.bollettinoNum = bollettinoNum;
		this.dataBollettino = dataBollettino;
		this.determinazioneNum = determinazioneNum;
		this.dataDeterminazione = dataDeterminazione;
		this.mancatoAccoglimento = mancatoAccoglimento;
		this.autorizzazioneUnica = autorizzazioneUnica;
		this.annullamento = annullamento;
		this.decadenza = decadenza;
		this.revoca = revoca;
		this.rigetto = rigetto;
		this.escussione = escussione;
		this.escussioneDetNum = escussioneDetNum;
		this.escussioneDataDet = escussioneDataDet;
		this.proroga = proroga;
		this.voltura = voltura;
		this.inFavoreDi = inFavoreDi;
		this.tipoEnergia = tipoEnergia;
		this.rifAutorizzazioneUnica = rifAutorizzazioneUnica;
		this.chiaveParagrafo = chiaveParagrafo;
	}
	
	public String recordToString(){
		return this.partitaIva+separator+this.azienda+separator+this.regione+separator+this.provincia+separator+this.comune+separator+this.potenza+separator+this.rifFile+separator+this.bollettinoNum+separator+this.dataBollettino+separator+this.determinazioneNum+separator+this.dataDeterminazione+separator+this.mancatoAccoglimento+separator+this.autorizzazioneUnica+separator+this.annullamento+separator+this.decadenza+separator+this.revoca+separator+this.rigetto+separator+this.escussione+separator+this.escussioneDetNum+separator+this.escussioneDataDet+separator+this.proroga+separator+this.voltura+separator+this.inFavoreDi+separator+this.tipoEnergia+separator+this.rifAutorizzazioneUnica+"\n";

	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	public String getRegione() {
		return regione;
	}

	public void setRegione(String regione) {
		this.regione = regione;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getPotenza() {
		return potenza;
	}

	public void setPotenza(String potenza) {
		this.potenza = potenza;
	}

	public String getRifFile() {
		return rifFile;
	}

	public void setRifFile(String rifFile) {
		this.rifFile = rifFile;
	}

	public String getBollettinoNum() {
		return bollettinoNum;
	}

	public void setBollettinoNum(String bollettinoNum) {
		this.bollettinoNum = bollettinoNum;
	}

	public String getDataBollettino() {
		return dataBollettino;
	}

	public void setDataBollettino(String dataBollettino) {
		this.dataBollettino = dataBollettino;
	}

	public String getDeterminazioneNum() {
		return determinazioneNum;
	}

	public void setDeterminazioneNum(String determinazioneNum) {
		this.determinazioneNum = determinazioneNum;
	}

	public String getDataDeterminazione() {
		return dataDeterminazione;
	}

	public void setDataDeterminazione(String dataDeterminazione) {
		this.dataDeterminazione = dataDeterminazione;
	}

	public String getMancatoAccoglimento() {
		return mancatoAccoglimento;
	}

	public void setMancatoAccoglimento(String mancatoAccoglimento) {
		this.mancatoAccoglimento = mancatoAccoglimento;
	}

	public String getAutorizzazioneUnica() {
		return autorizzazioneUnica;
	}

	public void setAutorizzazioneUnica(String autorizzazioneUnica) {
		this.autorizzazioneUnica = autorizzazioneUnica;
	}

	public String getAnnullamento() {
		return annullamento;
	}

	public void setAnnullamento(String annullamento) {
		this.annullamento = annullamento;
	}

	public String getDecadenza() {
		return decadenza;
	}

	public void setDecadenza(String decadenza) {
		this.decadenza = decadenza;
	}

	public String getRevoca() {
		return revoca;
	}

	public void setRevoca(String revoca) {
		this.revoca = revoca;
	}

	public String getRigetto() {
		return rigetto;
	}

	public void setRigetto(String rigetto) {
		this.rigetto = rigetto;
	}

	public String getEscussione() {
		return escussione;
	}

	public void setEscussione(String escussione) {
		this.escussione = escussione;
	}

	public String getEscussioneDetNum() {
		return escussioneDetNum;
	}

	public void setEscussioneDetNum(String escussioneDetNum) {
		this.escussioneDetNum = escussioneDetNum;
	}

	public String getEscussioneDataDet() {
		return escussioneDataDet;
	}

	public void setEscussioneDataDet(String escussioneDataDet) {
		this.escussioneDataDet = escussioneDataDet;
	}

	public String getProroga() {
		return proroga;
	}

	public void setProroga(String proroga) {
		this.proroga = proroga;
	}

	public String getVoltura() {
		return voltura;
	}

	public void setVoltura(String voltura) {
		this.voltura = voltura;
	}

	public String getInFavoreDi() {
		return inFavoreDi;
	}

	public void setInFavoreDi(String inFavoreDi) {
		this.inFavoreDi = inFavoreDi;
	}

	public String getTipoEnergia() {
		return tipoEnergia;
	}

	public void setTipoEnergia(String tipoEnergia) {
		this.tipoEnergia = tipoEnergia;
	}

	public String getRifAutorizzazioneUnica() {
		return rifAutorizzazioneUnica;
	}

	public void setRifAutorizzazioneUnica(String rifAutorizzazioneUnica) {
		this.rifAutorizzazioneUnica = rifAutorizzazioneUnica;
	}

	public String getChiaveParagrafo() {
		return chiaveParagrafo;
	}

	public void setChiaveParagrafo(String chiaveParagrafo) {
		this.chiaveParagrafo = chiaveParagrafo;
	}
	
	
}
