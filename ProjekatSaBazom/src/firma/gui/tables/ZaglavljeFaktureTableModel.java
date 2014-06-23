package firma.gui.tables;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import beans.faktura.Faktura.Zaglavlje;

@SuppressWarnings("serial")
public class ZaglavljeFaktureTableModel extends AbstractTableModel{

	static final public String[] colHeaders = new String []{
		"Id poruke",
		"Naziv dobavljaca", 
		"Adresa dobavljaca",
		"Pib dobavljaca",
		"Naziv kupca",
		"Adresa kupca",
		"Pib kupca",
		"Broj racuna",
		"Datum racuna",
		"Vrednost robe",
		"Vrednost usluga",
		"Ukupno roba/usluge",
		"Ukupan rabat",
		"Ukupan porez",
		"Oznaka valute",
		"Iznos za uplatu",
		"Uplata na racun",
		"Datum valute",
		};
	private ArrayList<Zaglavlje> zaglavlja;
	
	
	
	public ZaglavljeFaktureTableModel() {
		zaglavlja = new ArrayList<Zaglavlje>();
	}
	
	
	
	
	public void fillTable(ArrayList<Zaglavlje> zaglavlja){
	
			zaglavlja.clear();
			
			for(Zaglavlje z:zaglavlja) {
				Zaglavlje nz = new Zaglavlje();
				// nz.set mile majke
				zaglavlja.add(nz);
			}
	}
	
	
	
	
	@Override
	public int getRowCount() {
		return zaglavlja.size();
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
		
		Zaglavlje zaglavlje = zaglavlja.get(rowIndex);
		switch(columnIndex){
		
		/*case 0: return zaglavlje.getNazivRobeIliUsluge();
		case 1:	return zaglavlje.getKolicina();
		case 2: return zaglavlje.getJedinicaMere();
		case 3:	return zaglavlje.getJedinicnaCena();
		case 4: return zaglavlje.getVrednost();
		case 5:	return zaglavlje.getProcenatRabata();
		
		case 6: return zaglavlje.getIznosRabata();
		case 7:	return zaglavlje.getUmanjenoZaRabat();
		case 8: return zaglavlje.getUkupanPorez();
		case 9:	return zaglavlje.getRedniBroj();
		case 10:return zaglavlje.getProcenatRabata();
		case 11:return zaglavlje.getIznosRabata();
		
		case 12:return zaglavlje.getUmanjenoZaRabat();
		case 13:return zaglavlje.getUkupanPorez();
		case 14:return zaglavlje.getRedniBroj();
		case 15:return zaglavlje.getUmanjenoZaRabat();
		case 16:return zaglavlje.getUkupanPorez();
		case 17:return zaglavlje.getRedniBroj();*/
		
		}
		return "";	
	}

}
