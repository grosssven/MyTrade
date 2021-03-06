package formBeans;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import model.Aktie;
import model.KonstantenSession;
import dao.AktieDAO;
import dao.AuftragDAO;
import error.Meldungen;

/**
 * @date 25.6.2015 - 3.7.2015
 * @author sacha weiersmueller, siro duschletta und sven gross
 */
@ManagedBean
@SessionScoped
public class AktieErfassenFormBean {

	private String 	name;
	private String 	symbol;
	private double 	preis;
	private double 	dividende;
	private int		stueck;
	private Aktie 	neueAktie;
	
	private ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
	private Map<String, Object> sessionMap = externalContext.getSessionMap();
	
	public String backToAdministration() {

		return "administration?faces-redirect=true";
	}

	public String vorschau() {

		neueAktie = new Aktie(name, symbol, preis, dividende, stueck);		
		
		return "neueAktieVorschau?faces-redirect=true";

	}
	
	public String backToAktieErfassen() {
		
		return "aktieErfassen?faces-redirect=true";
		
	}
	
public String save() {
		
		AktieDAO neuesAktienDAO = new AktieDAO();
		AuftragDAO neuesAuftragDAO = new AuftragDAO();
		if (neuesAktienDAO.addAktie(neueAktie)) {
			sessionMap.put(KonstantenSession.MELDUNG, Meldungen.AKTIE_ERSTELLEN + neueAktie.getName());
			
			neuesAuftragDAO.auftragErfassen(neuesAktienDAO.checkIfStockTypeAlreadyExists(symbol), stueck, preis, 2);
			
			name      = "";
			symbol    = "";
			preis     = 0.0;
			dividende = 0.0;
			stueck    = 0;
			return "administration?faces-redirect=true";
		}else{
			
			return "aktieErfassen?faces-redirect=true";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getPreis() {
		return preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public double getDividende() {
		return dividende;
	}

	public void setDividende(double dividende) {
		this.dividende = dividende;
	}

	public int getStueck() {
		return stueck;
	}

	public void setStueck(int stueck) {
		this.stueck = stueck;
	}

	public Aktie getNeueAktie() {
		return neueAktie;
	}

	public void setNeueAktie(Aktie neueAktie) {
		this.neueAktie = neueAktie;
	}
}