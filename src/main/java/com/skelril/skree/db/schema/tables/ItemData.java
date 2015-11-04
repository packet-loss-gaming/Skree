/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema.tables;


import com.skelril.skree.db.schema.Keys;
import com.skelril.skree.db.schema.McDb;
import com.skelril.skree.db.schema.tables.records.ItemDataRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ItemData extends TableImpl<ItemDataRecord> {

	private static final long serialVersionUID = 1701639668;

	/**
	 * The reference instance of <code>mc_db.item_data</code>
	 */
	public static final ItemData ITEM_DATA = new ItemData();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ItemDataRecord> getRecordType() {
		return ItemDataRecord.class;
	}

	/**
	 * The column <code>mc_db.item_data.id</code>.
	 */
	public final TableField<ItemDataRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mc_db.item_data.mc_id</code>.
	 */
	public final TableField<ItemDataRecord, String> MC_ID = createField("mc_id", org.jooq.impl.SQLDataType.VARCHAR.length(45).nullable(false), this, "");

	/**
	 * The column <code>mc_db.item_data.variant</code>.
	 */
	public final TableField<ItemDataRecord, String> VARIANT = createField("variant", org.jooq.impl.SQLDataType.VARCHAR.length(45).nullable(false), this, "");

	/**
	 * The column <code>mc_db.item_data.value</code>.
	 */
	public final TableField<ItemDataRecord, BigDecimal> VALUE = createField("value", org.jooq.impl.SQLDataType.DECIMAL.precision(22, 2).nullable(false), this, "");

	/**
	 * The column <code>mc_db.item_data.primary alias</code>.
	 */
	public final TableField<ItemDataRecord, Integer> PRIMARY_ALIAS = createField("primary alias", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mc_db.item_data</code> table reference
	 */
	public ItemData() {
		this("item_data", null);
	}

	/**
	 * Create an aliased <code>mc_db.item_data</code> table reference
	 */
	public ItemData(String alias) {
		this(alias, ITEM_DATA);
	}

	private ItemData(String alias, Table<ItemDataRecord> aliased) {
		this(alias, aliased, null);
	}

	private ItemData(String alias, Table<ItemDataRecord> aliased, Field<?>[] parameters) {
		super(alias, McDb.MC_DB, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<ItemDataRecord, Integer> getIdentity() {
		return Keys.IDENTITY_ITEM_DATA;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ItemDataRecord> getPrimaryKey() {
		return Keys.KEY_ITEM_DATA_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ItemDataRecord>> getKeys() {
		return Arrays.<UniqueKey<ItemDataRecord>>asList(Keys.KEY_ITEM_DATA_PRIMARY, Keys.KEY_ITEM_DATA_ITEM, Keys.KEY_ITEM_DATA_PRIMARY_ALIAS_UNIQUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<ItemDataRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<ItemDataRecord, ?>>asList(Keys.FK_ITEM_ID_1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemData as(String alias) {
		return new ItemData(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ItemData rename(String name) {
		return new ItemData(name, null);
	}
}
