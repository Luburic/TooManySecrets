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
	@SuppressWarnings("unchecked")
	public static GenericTableModel createTableModel(String tableName) {
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
					columnNames = new String[columns.size()];
					Iterator<MetaColumn> iter = columns.iterator();
					for (int i = 0; i < columns.size(); i++) {
						columnNames[i] = iter.next().getName();
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
