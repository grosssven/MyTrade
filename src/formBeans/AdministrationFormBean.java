package formBeans;

import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import berechnungen.Dividendenaenderung;
import model.Aktie;
import model.Benutzer;
import model.KonstantenSession;
import dao.AktieDAO;
import dao.BenutzerDAO;
import error.Meldungen;

/**
 * @date 25.6.2015 - 3.7.2015
 * @author sacha weiersmueller, siro duschletta und sven gross
 */
@ManagedBean
@SessionScoped
public class AdministrationFormBean {

	public String aktieErfassen() {
		return "aktieErfassen.xhtml";
	}

	public String benutzerErfassen() {
		return "benutzerErfassen.xhtml";
	}

	public void dividendeAusschuetten() {

		BenutzerDAO benutzerDao = new BenutzerDAO();
		AktieDAO aktieDao = new AktieDAO();

		ArrayList<Benutzer> alleBenutzer = benutzerDao.getAllUsers();
		ArrayList<Aktie> alleAktien = aktieDao.selectAlleAktien();

		/*
		 * Hier wird f�r jede Aktie einzeln eine 
		 * neue Dividende ausgerechnet und 
		 * in der DB eingetragen
		 */
		if (alleAktien != null) {

			for (Aktie aktie : alleAktien) {

				int neueDividende = Dividendenaenderung.neueDividende(
						aktie.getDividende(),
						Dividendenaenderung.MITTLERE_STREEUNG, 10, 100);
				System.out.println("Aktie: " + aktie.getName()
						+ "    Neue Dividende: " + neueDividende);

				aktieDao.dividendeAktualisieren(neueDividende,
						aktie.getAktienID());
				System.out.println("Aktie: " + aktie.getName()
						+ " Dividende wurde ausbezahlt.");
			}
		}
		
		System.out.println("----------- �bergang zu Benutzer");
		
		/*
		 * Hier wird �ber jeden Benutzer iteriert und
		 * seine ganze Aktienliste von der DB abgefragt
		 * und ihm dann die neue Dividende * Anzahl Aktien ausbezahlt
		 * und dem aktuellen Kontostand gutgeschrieben
		 */
		if (alleBenutzer != null) {

			for (Benutzer benutzer : alleBenutzer) {

				if (!benutzer.isAdministrator()) {

					double neuerKontostand = benutzer.getKontostand();
					System.out.println("Benutzer: "
							+ benutzer.getBenutzername() + " Kontostand: "
							+ neuerKontostand);

					neuerKontostand = benutzer.getKontostand();
					ArrayList<Aktie> aktienVonBenutzer = aktieDao
							.selectAlleAktienVonBenutzer(benutzer
									.getBenutzerIDAsInt());

					if (aktienVonBenutzer != null) {

						for (Aktie aktie : aktienVonBenutzer) {

							double gesamteDividendeDieserAktie = aktie
									.getDividende() * aktie.getStueck();
							System.out
									.println("Aktie: " + aktie.getName()
											+ " Dividende: "
											+ aktie.getDividende()
											+ " Gesamt: "
											+ gesamteDividendeDieserAktie);
							neuerKontostand = neuerKontostand
									+ gesamteDividendeDieserAktie;
						}
					}

					benutzerDao.kontostandAktualisieren(neuerKontostand,
							benutzer.getBenutzerIDAsInt());
				}

			}
		}

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		Map<String, Object> sessionMap = externalContext.getSessionMap();

		sessionMap.put(KonstantenSession.MELDUNG,
				Meldungen.DIVIDENDE_AUSSCHUETTEN);
	}
}
