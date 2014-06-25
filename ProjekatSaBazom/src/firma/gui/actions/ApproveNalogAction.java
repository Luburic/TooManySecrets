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
import beans.nalog.Nalog;
import client.firma.NalogClient;
import firma.gui.MainFrame;
import firma.gui.dialogs.AbstractViewDialog;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ApproveNalogAction extends AbstractAction {
	private AbstractViewDialog dialog;

	public ApproveNalogAction(AbstractViewDialog dialog) {
		this.dialog = dialog;
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY, ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Odobri nalog");
		putValue(NAME, "Odobri nalog");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int i = dialog.getTable().getSelectedRow();
		if (i != -1) {
			Nalog nalog = (Nalog) ((ListTableModel) dialog.getTable().getModel()).getRow(i).get(0);
			((ListTableModel) dialog.getTable().getModel()).removeRows(i);

			MainFrame.getInstance().getBaza().getNaloziZaSefa().getNalog().remove(nalog);
			MainFrame.getInstance().getBaza().getNaloziZaDirektora().getNalog().remove(nalog);
			FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
					"http://localhost:8081/BaseX75/rest/firmaa");

			// TODO: Kod za slanje naloga
			try {
				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper("nalog"));
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.marshal(nalog, new File("./NalogTest/NalogTemp.xml"));

				Document doc = Validation.buildDocumentWithoutValidation("./NalogTest/NalogTemp.xml");
				Element eNalog = (Element) doc.getElementsByTagName("nalog").item(0);
				eNalog.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				SecurityClass sc = new SecurityClass();
				sc.saveDocument(doc, "./NalogTest/NalogTemp.xml");

				NalogClient nc = new NalogClient();
				nc.testIt("firmaA", "bankaa", "cerbankaa", "./NalogTest/NalogTemp.xml");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
