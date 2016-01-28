/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema.tables.records;


import com.skelril.skree.db.schema.tables.RegionMembers;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
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
public class RegionMembersRecord extends UpdatableRecordImpl<RegionMembersRecord> implements Record3<Integer, Integer, Integer> {

	private static final long serialVersionUID = -452520818;

	/**
	 * Setter for <code>mc_db.region_members.id</code>.
	 */
	public void setId(Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>mc_db.region_members.id</code>.
	 */
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mc_db.region_members.region_id</code>.
	 */
	public void setRegionId(Integer value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>mc_db.region_members.region_id</code>.
	 */
	public Integer getRegionId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mc_db.region_members.player_id</code>.
	 */
	public void setPlayerId(Integer value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>mc_db.region_members.player_id</code>.
	 */
	public Integer getPlayerId() {
		return (Integer) getValue(2);
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
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Integer> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, Integer, Integer> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return RegionMembers.REGION_MEMBERS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return RegionMembers.REGION_MEMBERS.REGION_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return RegionMembers.REGION_MEMBERS.PLAYER_ID;
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
	public Integer value2() {
		return getRegionId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getPlayerId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegionMembersRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegionMembersRecord value2(Integer value) {
		setRegionId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegionMembersRecord value3(Integer value) {
		setPlayerId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RegionMembersRecord values(Integer value1, Integer value2, Integer value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached RegionMembersRecord
	 */
	public RegionMembersRecord() {
		super(RegionMembers.REGION_MEMBERS);
	}

	/**
	 * Create a detached, initialised RegionMembersRecord
	 */
	public RegionMembersRecord(Integer id, Integer regionId, Integer playerId) {
		super(RegionMembers.REGION_MEMBERS);

		setValue(0, id);
		setValue(1, regionId);
		setValue(2, playerId);
	}
}