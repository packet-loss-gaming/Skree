/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema.tables.records;


import com.skelril.skree.db.schema.tables.Players;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.sql.Timestamp;


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
public class PlayersRecord extends UpdatableRecordImpl<PlayersRecord> implements Record6<Integer, String, Timestamp, Timestamp, Integer, Integer> {

	private static final long serialVersionUID = -1664178209;

	/**
	 * Setter for <code>mc_db.players.id</code>.
	 */
	public void setId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>mc_db.players.id</code>.
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mc_db.players.uuid</code>.
	 */
	public void setUuid(String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>mc_db.players.uuid</code>.
	 */
	public String getUuid() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mc_db.players.first_login</code>.
	 */
	public void setFirstLogin(Timestamp value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>mc_db.players.first_login</code>.
	 */
	public Timestamp getFirstLogin() {
		return (Timestamp) getValue(2);
	}

	/**
	 * Setter for <code>mc_db.players.last_login</code>.
	 */
	public void setLastLogin(Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>mc_db.players.last_login</code>.
	 */
	public Timestamp getLastLogin() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>mc_db.players.times_played</code>.
	 */
	public void setTimesPlayed(Integer value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>mc_db.players.times_played</code>.
	 */
	public Integer getTimesPlayed() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mc_db.players.seconds_played</code>.
	 */
	public void setSecondsPlayed(Integer value) {
		setValue(5, value);
	}

	/**
	 * Getter for <code>mc_db.players.seconds_played</code>.
	 */
	public Integer getSecondsPlayed() {
		return (Integer) getValue(5);
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
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, String, Timestamp, Timestamp, Integer, Integer> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<Integer, String, Timestamp, Timestamp, Integer, Integer> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return Players.PLAYERS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return Players.PLAYERS.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field3() {
		return Players.PLAYERS.FIRST_LOGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return Players.PLAYERS.LAST_LOGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return Players.PLAYERS.TIMES_PLAYED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field6() {
		return Players.PLAYERS.SECONDS_PLAYED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value3() {
		return getFirstLogin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getLastLogin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getTimesPlayed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value6() {
		return getSecondsPlayed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value2(String value) {
		setUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value3(Timestamp value) {
		setFirstLogin(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value4(Timestamp value) {
		setLastLogin(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value5(Integer value) {
		setTimesPlayed(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord value6(Integer value) {
		setSecondsPlayed(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlayersRecord values(Integer value1, String value2, Timestamp value3, Timestamp value4, Integer value5, Integer value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PlayersRecord
	 */
	public PlayersRecord() {
		super(Players.PLAYERS);
	}

	/**
	 * Create a detached, initialised PlayersRecord
	 */
	public PlayersRecord(Integer id, String uuid, Timestamp firstLogin, Timestamp lastLogin, Integer timesPlayed, Integer secondsPlayed) {
		super(Players.PLAYERS);

		setValue(0, id);
		setValue(1, uuid);
		setValue(2, firstLogin);
		setValue(3, lastLogin);
		setValue(4, timesPlayed);
		setValue(5, secondsPlayed);
	}
}