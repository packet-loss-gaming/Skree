package com.skelril.skree.service.internal.market.deducer;


import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemStackFactory;
import com.skelril.skree.service.internal.market.TypeDeducer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class DeducerOfSimpleType implements TypeDeducer {
    @Override
    public ItemStack getItemStack(Clause<String, String> idVariant) {
        Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, idVariant.getKey());
        if (!idVariant.getValue().equals("default")) {
            throw new IllegalArgumentException("Complex item passed, Simple Type Deducer supports only simple types");
        }
        return ItemStackFactory.newItemStack(type.get());
    }

    @Override
    public Clause<String, String> getVariant(ItemStack stack) {
        return new Clause<>(stack.getItem().getId(), "default");
    }
}
