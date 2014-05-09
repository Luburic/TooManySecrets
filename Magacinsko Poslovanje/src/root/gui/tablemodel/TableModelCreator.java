package root.gui.tablemodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import root.util.Constants;
import rs.mgifos.mosquito.IMetaLoader;
import rs.mgifos.mosquito.LoadingException;
import rs.mgifos.mosquito.impl.pdm.PDMetaLoader;
import rs.mgifos.mosquito.model.MetaColumn;
import rs.mgifos.mosquito.model.MetaModel;
import rs.mgifos.mosquito.model.MetaTable;

public class TableModelCreator {
	/**
	 * Putem ove metode se može jednostavno formirati tabela iz meta podataka koji su u modelu. Ukoliko je potrebno da
	 * tabela sadrži neke kolone iz spoja, trebaju se sve kolone proslediti kao argument za colNames. Takođe je potrebno
	 * u tom slučaju proslediti custom SQL naredbu table modelu, jer podrazumevano ponašanje je da izvuče samo atribute
	 * iz te tabele.
	 */
	@SuppressWarnings("unchecked")
	public static GenericTableModel createTableModel(String tableName, String[] colNames) {
		IMetaLoader metaLoader = new PDMetaLoader();

		Properties properties = new Properties();
		properties.put(PDMetaLoader.FILENAME, Constants.MODEL_LOCATION);

		Collection<MetaColumn> columns;
		String[] columnNames = colNames;
		String tableCode;

		try {
			MetaModel model = metaLoader.getMetaModel(properties);
			for (MetaTable table : model) {
				if (table.getName().equals(tableName)) {
					tableCode = table.getCode();
					columns = table.cColumns();
					if (colNames == null) {
						columnNames = new String[columns.size()];
						Iterator<MetaColumn> iter = columns.iterator();
						for (int i = 0; i < columns.size(); i++) {
							columnNames[i] = iter.next().getName();
						}
					}
					return new GenericTableModel(tableCode, columnNames, columns);
				}
			}
		} catch (LoadingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
