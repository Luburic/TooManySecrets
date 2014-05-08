package root.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ColumnList {

	private List<Column> columns;

	public ColumnList() {
		columns = new ArrayList<Column>();
	}

	public ColumnList(List<Column> columns) {
		this();
		this.columns.addAll(columns);
	}

	public Column getColumn(String name) {
		Iterator<Column> it = columns.iterator();
		while (it.hasNext()) {
			Column c = it.next();
			if (c.getName().equals(name))
				return c;
		}
		return null;
	}

	public Object getValue(String name) {
		Column c = getColumn(name);
		if (c == null)
			return null;
		else
			return c.getValue();
	}

	public String getWhereClause() {
		StringBuffer retVal = new StringBuffer();
		Iterator<Column> it = columns.iterator();
		while (it.hasNext()) {
			Column c = it.next();
			if (retVal.length() > 0)
				retVal.append(" AND");
			retVal.append(' ');
			retVal.append(c.getName());
			retVal.append('=');
			if (c.getValue() instanceof Number) {
				retVal.append(c.getValue().toString());
			} else if (c.getValue() instanceof String) {
				retVal.append("'");
				retVal.append(c.getValue());
				retVal.append("'");
			} else if (c.getValue() instanceof Date) {
				retVal.append("'");
				retVal.append(sdf.format((Date) c.getValue()));
				retVal.append("'");
			} else if (c.getValue() instanceof BigDecimal) {
				retVal.append(c.getValue());
			}
		}
		return retVal.toString();
	}

	public int getColumnCount() {
		return columns.size();
	}

	public Column getColumn(int index) {
		if (index >= 0 && index < columns.size())
			return columns.get(index);
		else
			return null;
	}

	public void add(Column c) {
		columns.add(c);
	}

	public void remove(Column c) {
		columns.remove(c);
	}

	public Iterator<Column> iterator() {
		return columns.iterator();
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) {
		Column c1 = new Column("DR_DRZAVA", "SCG");
		Column c2 = new Column("NM_PTTBR", new Integer(21000));
		Column c3 = new Column("DAT_DATUM", new Date());
		Column c4 = new Column("BIG_DEC", new BigDecimal(12.5f));
		ColumnList cl = new ColumnList();
		cl.add(c1);
		cl.add(c2);
		cl.add(c3);
		cl.add(c4);
		System.out.println(cl.getWhereClause());
	}
}