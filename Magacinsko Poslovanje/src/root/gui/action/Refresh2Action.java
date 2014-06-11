package root.gui.action;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import root.gui.form.GenericForm;
import root.gui.tablemodel.GenericTableModel;
import root.util.Constants;

public class Refresh2Action extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private GenericForm standardForm;

	public Refresh2Action(GenericForm standardForm) {
		putValue(SMALL_ICON, new ImageIcon("img/refresh.gif"));
		putValue(SHORT_DESCRIPTION, "Refresh");
		this.standardForm = standardForm;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String customQuery = "";
		if (standardForm.getChildWhere().equals("")) {
			customQuery = "SELECT id_prometnog_dokumenta, Poslovna_godina.id_poslovne_godine, Magacin1.id_jedinice, Vrsta_prometa.id_prometa,Magacin2.id_jedinice, Poslovni_partner.id_poslovnog_partnera, broj_prometnog_dokumenta, datum_prometnog, datum_knjizenja_prometnog, status_prometnog, Poslovna_godina.godina, Magacin1.naziv_jedinice, Vrsta_prometa.sifra_prometa, Magacin2.naziv_jedinice, Poslovni_partner.naziv_poslovnog_partnera, prometni_version FROM Prometni_dokument JOIN Poslovna_godina ON Prometni_dokument.id_poslovne_godine = Poslovna_godina.id_poslovne_godine JOIN Organizaciona_jedinica Magacin1 ON Prometni_dokument.id_jedinice = Magacin1.id_jedinice JOIN Vrsta_prometa ON Prometni_dokument.id_prometa = Vrsta_prometa.id_prometa LEFT JOIN Organizaciona_jedinica Magacin2 ON Prometni_dokument.Org_id_jedinice = Magacin2.id_jedinice LEFT JOIN Poslovni_partner ON Prometni_dokument.id_poslovnog_partnera = Poslovni_partner.id_poslovnog_partnera WHERE Prometni_dokument.id_poslovne_godine = "
					+ Constants.idGodine + " ORDER BY broj_prometnog_dokumenta";
		} else {
			customQuery = "SELECT id_prometnog_dokumenta, Poslovna_godina.id_poslovne_godine, Organizaciona_jedinica.id_jedinice, Vrsta_prometa.id_prometa,Magacin2.id_jedinice, Poslovni_partner.id_poslovnog_partnera, broj_prometnog_dokumenta, datum_prometnog, datum_knjizenja_prometnog, status_prometnog, Poslovna_godina.godina, Organizaciona_jedinica.naziv_jedinice, Vrsta_prometa.sifra_prometa, Magacin2.naziv_jedinice, Poslovni_partner.naziv_poslovnog_partnera, prometni_version FROM Prometni_dokument JOIN Poslovna_godina ON Prometni_dokument.id_poslovne_godine = Poslovna_godina.id_poslovne_godine JOIN Organizaciona_jedinica ON Prometni_dokument.id_jedinice = Organizaciona_jedinica.id_jedinice JOIN Vrsta_prometa ON Prometni_dokument.id_prometa = Vrsta_prometa.id_prometa LEFT JOIN Organizaciona_jedinica Magacin2 ON Prometni_dokument.Org_id_jedinice = Magacin2.id_jedinice LEFT JOIN Poslovni_partner ON Prometni_dokument.id_poslovnog_partnera = Poslovni_partner.id_poslovnog_partnera"
					+ standardForm.getChildWhere()
					+ " AND Prometni_dokument.id_poslovne_godine = "
					+ Constants.idGodine + " ORDER BY broj_prometnog_dokumenta";
		}
		try {
			((GenericTableModel) standardForm.getTblGrid().getModel()).fillData(customQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
