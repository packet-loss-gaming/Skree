package com.skelril.skree.service.internal.market;

import com.skelril.nitro.Clause;
import com.skelril.skree.db.SQLHandle;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.deducer.DeducerOfSimpleType;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.skelril.skree.db.schema.tables.ItemAliases.ITEM_ALIASES;
import static com.skelril.skree.db.schema.tables.ItemAliasesPrimary.ITEM_ALIASES_PRIMARY;
import static com.skelril.skree.db.schema.tables.ItemId.ITEM_ID;
import static com.skelril.skree.db.schema.tables.ItemValues.ITEM_VALUES;

public class MarketServiceImpl implements MarketService {
    @Override
    public ItemStack getItem(String alias) {
        return null;
    }

    @Override
    public BigDecimal getPrice(String alias) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            Record1<BigDecimal> result = create.select(ITEM_VALUES.PRICE).from(ITEM_VALUES).where(
                    ITEM_VALUES.ID.equal(
                            create.select(ITEM_ALIASES.ID)
                                    .from(ITEM_ALIASES)
                                    .where(ITEM_ALIASES.ALIAS.equal(alias.toLowerCase()))
                    )
            ).fetchOne();
            return result.getValue(ITEM_VALUES.PRICE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getPrice(ItemStack stack) {
        try (Connection con = SQLHandle.getConnection()) {
            Clause<String, String> idVariant = getIDVariant(stack);

            DSLContext create = DSL.using(con);
            Record1<BigDecimal> result = create.select(ITEM_VALUES.PRICE).from(ITEM_VALUES).where(
                    ITEM_VALUES.ID.equal(
                            create.select(ITEM_ID.ID)
                                    .from(ITEM_ID)
                                    .where(ITEM_ID.MC_ID.equal(idVariant.getKey())
                                            .and(ITEM_ID.VARIANT.equal(idVariant.getValue()))
                                    )
                    )
            ).fetchOne();
            return result.getValue(ITEM_VALUES.PRICE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setPrice(String alias, BigDecimal price) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            int updated = create.insertInto(ITEM_VALUES)
                    .columns(ITEM_VALUES.ITEM_ID, ITEM_VALUES.PRICE).select(
                            create.select(ITEM_ALIASES.ID, DSL.val(price))
                                    .from(ITEM_ALIASES)
                                    .where(ITEM_ALIASES.ALIAS.equal(alias.toLowerCase()))
                    ).execute();

            return updated == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addItem(ItemStack stack) {
        try (Connection con = SQLHandle.getConnection()) {
            Clause<String, String> idVariant = getIDVariant(stack);

            DSLContext create = DSL.using(con);
            create.insertInto(ITEM_ID)
                    .columns(ITEM_ID.MC_ID, ITEM_ID.VARIANT)
                    .values(idVariant.getKey(), idVariant.getValue())
                    .onDuplicateKeyIgnore()
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPrimaryAlias(String alias) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            create.insertInto(ITEM_ALIASES_PRIMARY).columns(ITEM_ALIASES_PRIMARY.ALIAS)
                    .select(
                            create.select(ITEM_ALIASES.ID)
                                    .from(ITEM_ALIASES)
                                    .where(ITEM_ALIASES.ALIAS.equal(alias))
                    ).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addAlias(String alias, ItemStack stack) {
        try (Connection con = SQLHandle.getConnection()) {
            Clause<String, String> idVariant = getIDVariant(stack);

            DSLContext create = DSL.using(con);
            int created = create.insertInto(ITEM_ALIASES)
                    .columns(ITEM_ALIASES.ITEM_ID, ITEM_ALIASES.ALIAS)
                    .select(create.select(ITEM_ID.ID, DSL.val(alias.toLowerCase()))
                            .from(ITEM_ID)
                            .where(ITEM_ID.MC_ID.equal(idVariant.getKey())
                                            .and(ITEM_ID.VARIANT.equal(idVariant.getValue()))
                            )
                    ).execute();
            return created == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getAlias(ItemStack stack) {
        try (Connection con = SQLHandle.getConnection()) {
            Clause<String, String> idVariant = getIDVariant(stack);

            DSLContext create = DSL.using(con);
            Record1<String> result = create.select(ITEM_ALIASES.ALIAS).from(ITEM_ALIASES).where(
                    ITEM_ALIASES.ITEM_ID.equal(
                            create.select(ITEM_ID.ID)
                                    .from(ITEM_ID)
                                    .where(ITEM_ID.MC_ID
                                                    .equal(idVariant.getKey())
                                                    .and(ITEM_ID.VARIANT
                                                            .equal(idVariant.getValue()))
                                    )
                    ).and(ITEM_ALIASES.ID.equal(DSL.any(create.select(ITEM_ALIASES_PRIMARY.ALIAS))))
            ).fetchOne();
            return result.value1();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Clause<String, BigDecimal>> getPrices() {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            Result<Record2<String, BigDecimal>> result = create.select(ITEM_ALIASES.ALIAS, ITEM_VALUES.PRICE)
                    .from(ITEM_ALIASES)
                    .innerJoin(ITEM_VALUES).on(ITEM_VALUES.ITEM_ID.equal(ITEM_ALIASES.ITEM_ID))
                    .innerJoin(ITEM_ALIASES_PRIMARY).on(ITEM_ALIASES.ID.equal(ITEM_ALIASES_PRIMARY.ALIAS))
                    .fetch();

            return result.stream().map(entry -> new Clause<>(entry.value1(), entry.value2())).collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, TypeDeducer> varientResolutionMap = new HashMap<>();

    private TypeDeducer getDeducer(String type) {
        return varientResolutionMap.getOrDefault(type, new DeducerOfSimpleType());
    }

    private ItemStack getItemStack(Clause<String, String> idVariant) {
        return getDeducer(idVariant.getKey()).getItemStack(idVariant);
    }

    private Clause<String, String> getIDVariant(ItemStack stack) {
        return getDeducer(stack.getItem().getId()).getVariant(stack);
    }
}
