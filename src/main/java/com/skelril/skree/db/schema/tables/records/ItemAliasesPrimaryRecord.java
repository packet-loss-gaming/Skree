/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema.tables.records;


import com.skelril.skree.db.schema.tables.ItemAliasesPrimary;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Row1;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;


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
public class ItemAliasesPrimaryRecord extends UpdatableRecordImpl<ItemAliasesPrimaryRecord> implements Record1<Integer> {

	private static final long serialVersionUID = 487575421;

	/**
	 * Setter for <code>mc_db.item_aliases_primary.alias</code>.
	 */
	public void setAlias(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>mc_db.item_aliases_primary.alias</code>.
	 */
	public Integer getAlias() {
		return (Integer) getValue(0);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record1 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row1<Integer> fieldsRow() {
		return (Row1) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row1<Integer> valuesRow() {
		return (Row1) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return ItemAliasesPrimary.ITEM_ALIASES_PRIMARY.ALIAS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getAlias();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemAliasesPrimaryRecord value1(Integer value) {
		setAlias(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemAliasesPrimaryRecord values(Integer value1) {
		value1(value1);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ItemAliasesPrimaryRecord
	 */
	public ItemAliasesPrimaryRecord() {
		super(ItemAliasesPrimary.ITEM_ALIASES_PRIMARY);
	}

	/**
	 * Create a detached, initialised ItemAliasesPrimaryRecord
	 */
	public ItemAliasesPrimaryRecord(Integer alias) {
		super(ItemAliasesPrimary.ITEM_ALIASES_PRIMARY);

		setValue(0, alias);
	}
}