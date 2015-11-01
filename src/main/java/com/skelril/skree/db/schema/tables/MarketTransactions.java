/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema.tables;


import com.skelril.skree.db.schema.Keys;
import com.skelril.skree.db.schema.McDb;
import com.skelril.skree.db.schema.tables.records.MarketTransactionsRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.sql.Timestamp;
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
public class MarketTransactions extends TableImpl<MarketTransactionsRecord> {

	private static final long serialVersionUID = -1394171449;

	/**
	 * The reference instance of <code>mc_db.market_transactions</code>
	 */
	public static final MarketTransactions MARKET_TRANSACTIONS = new MarketTransactions();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MarketTransactionsRecord> getRecordType() {
		return MarketTransactionsRecord.class;
	}

	/**
	 * The column <code>mc_db.market_transactions.id</code>.
	 */
	public final TableField<MarketTransactionsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mc_db.market_transactions.player_id</code>.
	 */
	public final TableField<MarketTransactionsRecord, Integer> PLAYER_ID = createField("player_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mc_db.market_transactions.item_id</code>.
	 */
	public final TableField<MarketTransactionsRecord, Integer> ITEM_ID = createField("item_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mc_db.market_transactions.quantity</code>.
	 */
	public final TableField<MarketTransactionsRecord, Integer> QUANTITY = createField("quantity", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mc_db.market_transactions.time</code>.
	 */
	public final TableField<MarketTransactionsRecord, Timestamp> TIME = createField("time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false), this, "");

	/**
	 * Create a <code>mc_db.market_transactions</code> table reference
	 */
	public MarketTransactions() {
		this("market_transactions", null);
	}

	/**
	 * Create an aliased <code>mc_db.market_transactions</code> table reference
	 */
	public MarketTransactions(String alias) {
		this(alias, MARKET_TRANSACTIONS);
	}

	private MarketTransactions(String alias, Table<MarketTransactionsRecord> aliased) {
		this(alias, aliased, null);
	}

	private MarketTransactions(String alias, Table<MarketTransactionsRecord> aliased, Field<?>[] parameters) {
		super(alias, McDb.MC_DB, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<MarketTransactionsRecord, Integer> getIdentity() {
		return Keys.IDENTITY_MARKET_TRANSACTIONS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MarketTransactionsRecord> getPrimaryKey() {
		return Keys.KEY_MARKET_TRANSACTIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MarketTransactionsRecord>> getKeys() {
		return Arrays.<UniqueKey<MarketTransactionsRecord>>asList(Keys.KEY_MARKET_TRANSACTIONS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<MarketTransactionsRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<MarketTransactionsRecord, ?>>asList(Keys.PLAYER_KEY, Keys.ITEM_KEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MarketTransactions as(String alias) {
		return new MarketTransactions(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MarketTransactions rename(String name) {
		return new MarketTransactions(name, null);
	}
}