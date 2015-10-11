/**
 * This class is generated by jOOQ
 */
package com.skelril.skree.db.schema;


import com.skelril.skree.db.schema.tables.Modifiers;
import com.skelril.skree.db.schema.tables.records.ModifiersRecord;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import javax.annotation.Generated;


/**
 * A class modelling foreign key relationships between tables of the <code>mc_db</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.0"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<ModifiersRecord> KEY_MODIFIERS_PRIMARY = UniqueKeys0.KEY_MODIFIERS_PRIMARY;
	public static final UniqueKey<ModifiersRecord> KEY_MODIFIERS_NAME_UNIQUE = UniqueKeys0.KEY_MODIFIERS_NAME_UNIQUE;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<ModifiersRecord> KEY_MODIFIERS_PRIMARY = createUniqueKey(Modifiers.MODIFIERS, Modifiers.MODIFIERS.ID);
		public static final UniqueKey<ModifiersRecord> KEY_MODIFIERS_NAME_UNIQUE = createUniqueKey(Modifiers.MODIFIERS, Modifiers.MODIFIERS.NAME);
	}
}