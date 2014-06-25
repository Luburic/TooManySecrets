package firma.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.NSPrefixMapper;
import util.Validation;
import basexdb.util.FirmaDBUtil;
import beans.faktura.Faktura;
import client.firma.FakturaClient;
import firma.gui.MainFrame;
import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ApproveAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public ApproveAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Odobri fakturu");
		putValue(NAME, "Odobri fakturu");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			Faktura faktura = (Faktura) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);

			MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura().remove(faktura);
			MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura().remove(faktura);
			FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
					"http://localhost:8081/BaseX75/rest/firmaa");

			try {
				JAXBContext context = JAXBContext.newInstance("beans.faktura");
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper("faktura"));
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.marshal(faktura, new File("./FakturaTest/FakturaTemp.xml"));

				Document doc = Validation.buildDocumentWithoutValidation("./FakturaTest/FakturaTemp.xml");
				Element eFaktura = (Element) doc.getElementsByTagName("faktura").item(0);
				eFaktura.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				SecurityClass sc = new SecurityClass();
				sc.saveDocument(doc, "./FakturaTest/FakturaTemp.xml");

				FakturaClient fc = new FakturaClient();
				fc.testIt("firmaA", "firmab", "cerfirmab", "./FakturaTest/FakturaTemp.xml");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// TODO: Kod za slanje fakture
		}
	}

}
