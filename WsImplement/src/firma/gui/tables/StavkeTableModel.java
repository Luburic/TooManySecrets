package firma.gui.tables;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import beans.faktura.Faktura.Stavka;

@SuppressWarnings("serial")
public class StavkeTableModel extends AbstractTableModel{

	static final public String[] colHeaders = new String []{
		"Redni broj",
		"Roba/Usluge", 
		"Kolicina",
		"Jedinica mere",
		"Jedinicna cena",
		"Vrednost",
		"Procenat rabata",
		"Iznos rabata",
		"Umanjeno(rabat)",
		"Ukupan porez",
		};
	
	private ArrayList<Stavka> stavke;
	
	public StavkeTableModel() {
		stavke = new ArrayList<Stavka>();
	}
	

	public void fillTable(ArrayList<Stavka> stavke){
		stavke.clear();
		
		for(Stavka s:stavke) {
			Stavka ns = new Stavka();
			
			ns.setIznosRabata(s.getIznosRabata());
			ns.setJedinicaMere(s.getJedinicaMere());
			ns.setJedinicnaCena(s.getJedinicnaCena());
			ns.setKolicina(s.getKolicina());
			ns.setNazivRobeIliUsluge(s.getNazivRobeIliUsluge());
			ns.setProcenatRabata(s.getProcenatRabata());
			ns.setRedniBroj(s.getRedniBroj());
			ns.setUkupanPorez(s.getUkupanPorez());
			ns.setUmanjenoZaRabat(s.getUmanjenoZaRabat());
			ns.setVrednost(s.getVrednost());
			
			stavke.add(ns);
		}
	}
	
	
	@Override
	public int getRowCount() {
		return stavke.size();
	}

	@Override
	public int getColumnCount() {
		return colHeaders.length;
	}

	public String getColumnName(int i) {
		return colHeaders[i];
	}
	
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if( rowIndex<0 || rowIndex >= getRowCount())
			return "";
		
		Stavka stavka = stavke.get(rowIndex);
		
		switch(columnIndex){
		case 0:	return stavka.getRedniBroj();
		case 1: return stavka.getNazivRobeIliUsluge();
		case 2:	return stavka.getKolicina();
		case 3: return stavka.getJedinicaMere();
		case 4:	return stavka.getJedinicnaCena();
		case 5: return stavka.getVrednost();
		case 6:	return stavka.getProcenatRabata();
		case 7: return stavka.getIznosRabata();
		case 8:	return stavka.getUmanjenoZaRabat();
		case 9: return stavka.getUkupanPorez();
		

		}
		return "";	
	}
	
}
