package root.gui.tablemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import root.util.Constants;
import root.util.MetaSurogateDisplay;
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
	public static GenericTableModel createTableModel(String tableName, List<MetaSurogateDisplay> additionalColumns) {
		IMetaLoader metaLoader = new PDMetaLoader();

		Properties properties = new Properties();
		properties.put(PDMetaLoader.FILENAME, Constants.MODEL_LOCATION);

		Collection<MetaColumn> columns;
		String[] columnNames;
		String tableCode;

		try {
			MetaModel model = metaLoader.getMetaModel(properties);
			for (MetaTable table : model) {
				if (table.getName().equals(tableName)) {
					tableCode = table.getCode();
					columns = table.cColumns();
					int moreColumns = 0;
					List<String> moreColumnNames = new ArrayList<String>();
					if (additionalColumns != null) {
						Iterator<MetaSurogateDisplay> additionalIterator = additionalColumns.iterator();
						while (additionalIterator.hasNext()) {
							MetaSurogateDisplay msd = additionalIterator.next();
							moreColumns += msd.getDisplayColumnName().size();
							moreColumnNames.addAll(msd.getDisplayColumnName());
						}
					}
					columnNames = new String[columns.size() + moreColumns];
					Iterator<MetaColumn> iter = columns.iterator();
					for (int i = 0; i < columns.size() - 1; i++) {
						columnNames[i] = iter.next().getName();
					}
					for (int i = columns.size() - 1, j = 0; i < columnNames.length - 1; i++, j++) {
						columnNames[i] = moreColumnNames.get(j);
					}
					columnNames[columnNames.length - 1] = iter.next().getName();
					GenericTableModel retVal = new GenericTableModel(tableCode, columnNames, columns);
					retVal.setOutsideColumns(additionalColumns);
					return retVal;
				}
			}
		} catch (LoadingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
