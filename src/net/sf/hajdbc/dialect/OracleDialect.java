/*
 * Copyright (c) 2004-2007, Identity Theft 911, LLC.  All rights reserved.
 */
package net.sf.hajdbc.dialect;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.hajdbc.QualifiedName;

/**
 * Dialect for Oracle (commercial).
 * @author Paul Ferraro
 */
@SuppressWarnings("nls")
public class OracleDialect extends StandardDialect
{
	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#dummyTable()
	 */
	@Override
	protected String dummyTable()
	{
		return "DUAL";
	}

	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#getSequences(java.sql.DatabaseMetaData)
	 */
	@Override
	public Collection<QualifiedName> getSequences(DatabaseMetaData metaData) throws SQLException
	{
		List<QualifiedName> sequenceList = new LinkedList<QualifiedName>();
		
		Statement statement = metaData.getConnection().createStatement();
		
		ResultSet resultSet = statement.executeQuery("SELECT SEQUENCE_NAME FROM USER_SEQUENCES");
		
		while (resultSet.next())
		{
			sequenceList.add(new QualifiedName(resultSet.getString(1)));
		}
		
		statement.close();
		
		return sequenceList;
	}

	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#parseInsertTable(java.lang.String)
	 */
	@Override
	public String parseInsertTable(String sql)
	{
		return null;
	}

	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#truncateTableFormat()
	 */
	@Override
	protected String truncateTableFormat()
	{
		return "TRUNCATE TABLE {0}";
	}
	
	/**
	 * ON UPDATE and deferrability clauses are not supported.
	 * @see net.sf.hajdbc.dialect.StandardDialect#createForeignKeyConstraintFormat()
	 */
	@Override
	protected String createForeignKeyConstraintFormat()
	{
		return "ALTER TABLE {1} ADD CONSTRAINT {0} FOREIGN KEY ({2}) REFERENCES {3} ({4}) ON DELETE {5,choice,0#CASCADE|1#RESTRICT|2#SET NULL|3#NO ACTION|4#SET DEFAULT}";
	}

	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#sequencePattern()
	 */
	@Override
	protected String sequencePattern()
	{
		return "'?(\\w+)'?\\.(?:CURR|NEXT)VAL";
	}

	/**
	 * @see net.sf.hajdbc.dialect.StandardDialect#nextSequenceValueFormat()
	 */
	@Override
	protected String nextSequenceValueFormat()
	{
		return "{0}.NEXTVAL";
	}
}