/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice.mysql.aggressive;

import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.*;
import com.skelril.skree.service.internal.market.builder.MarketItemBuilderImpl;
import com.skelril.skree.service.internal.market.buy.BuyOffer;
import com.skelril.skree.service.internal.market.sell.SellOffer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AggressiveMySQLFixedPriceMarketServiceImpl implements MarketService {

    private MarketItemBuilder builder = new MarketItemBuilderImpl();

    protected MarketOfferSnapshot createDeal(UUID offerer, MarketOfferType type, MarketItem item, BigDecimal price, MarketOfferStatus status, int total) {
        if (countOffers(offerer) > getMaxOffers(offerer)) {
            status = MarketOfferStatus.REJECTED_NO_SPACE;
        }
        MarketOfferSnapshot snapshot = new MarketOfferSnapshotImpl(UUID.randomUUID(), offerer, type, item, price, status, 0, status == MarketOfferStatus.COMPLETE ? total : 0, total);

        UpdateOfferStatement statement = new UpdateOfferStatement(snapshot);
        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MarketOfferSnapshot offer(BuyOffer offer) {
        // The minimum we're going to sell it for
        BigDecimal marketMin = getPrice(offer.getItem()).getMaxAdvisory();
        // How most they're going to buy it for
        BigDecimal userMax = offer.getPrice();
        // They want to spend less than the market is willing to take, reject it
        if (userMax.compareTo(marketMin) == -1) {
            return createBuyDeal(
                    offer.getOfferer(),
                    offer.getItem(),
                    offer.getPrice(),
                    MarketOfferStatus.REJECTED_NO_DEAL,
                    offer.getQuantity()
            );
        }
        return createBuyDeal(
                offer.getOfferer(),
                offer.getItem(),
                offer.getPrice(),
                MarketOfferStatus.COMPLETE,
                offer.getQuantity()
        );
    }

    protected MarketOfferSnapshot createBuyDeal(UUID offerer, MarketItem item, BigDecimal price, MarketOfferStatus status, int total) {
        return createDeal(offerer, MarketOfferType.BUY, item, price, status, total);
    }

    @Override
    public MarketOfferSnapshot offer(SellOffer offer) {
        // The maximum we're going to buy it for
        BigDecimal marketMax = getPrice(offer.getItem()).getMinAdvisory();
        // How much they want us to pay
        BigDecimal userMin = offer.getPrice();
        // They require more than the market is willing to spend, reject it
        if (userMin.compareTo(marketMax) == 1) {
            return createSellDeal(
                    offer.getOfferer(),
                    offer.getItem(),
                    offer.getPrice(),
                    MarketOfferStatus.REJECTED_NO_DEAL,
                    offer.getQuantity()
            );
        }
        return createSellDeal(
                offer.getOfferer(),
                offer.getItem(),
                offer.getPrice(),
                MarketOfferStatus.COMPLETE,
                offer.getQuantity()
        );
    }

    protected MarketOfferSnapshot createSellDeal(UUID offerer, MarketItem item, BigDecimal price, MarketOfferStatus status, int total) {
        return createDeal(offerer, MarketOfferType.SELL, item, price, status, total);
    }

    @Override
    public void offer(PriceUpdate update) {

    }

    @Override
    public MarketWithdrawSnapshot offer(MarketWithdraw offer) {
        MarketOfferSnapshot snapshot = getOffer(offer.getOfferID());
        if (snapshot == null) {
            return new MarketWithdrawSnapshotImpl(null, MarketWithdrawStatus.INVALID_OFFER_ID);
        }

        int newTaken = snapshot.getTakenQuantity() + offer.getRequestedQuantity();
        if (newTaken > snapshot.getCompletedQuantity()) {
            return new MarketWithdrawSnapshotImpl(null, MarketWithdrawStatus.CANCELLED_NOT_ENOUGH_ITEMS);
        }

        MarketOfferSnapshot success = new MarketOfferSnapshotImpl(
                snapshot.getOfferID(),
                snapshot.getOfferer(),
                snapshot.getType(),
                snapshot.getItem(),
                snapshot.getPrice(),
                snapshot.getStatus(),
                newTaken,
                snapshot.getCompletedQuantity(),
                snapshot.getTotalQuantity()
        );

        UpdateOfferStatement statement = new UpdateOfferStatement(success);
        try {
            return new MarketWithdrawSnapshotImpl(statement.executeStatement());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MarketItem getItem(UUID ID) {
        FetchItemByIDStatement statement = new FetchItemByIDStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MarketItem getItem(String name) {
        FetchItemByAliasStatement statement = new FetchItemByAliasStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MarketItem getItem(ItemStack itemStack) {
        return builder.fromItemStack(itemStack);
    }

    @Override
    public PriceSnapshot getPrice(MarketItem type) {
        FetchPriceSnapshotStatement statement = new FetchPriceSnapshotStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MarketOfferSnapshot getOffer(UUID ID) {
        FetchOfferStatement statement = new FetchOfferStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int countOffers(UUID user) {
        CountAllOfferStatement statement = new CountAllOfferStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getMaxOffers(UUID user) {
        return 9;
    }

    @Override
    public List<MarketOfferSnapshot> getOffers(UUID user) {
        FetchAllOfferStatement statement = new FetchAllOfferStatement();

        try {
            return statement.executeStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
