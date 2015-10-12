package com.skelril.skree.service.internal.market.deducer;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.market.TypeDeducer;
import org.spongepowered.api.item.inventory.ItemStack;

public class DeducerOfSimpleType implements TypeDeducer {
    @Override
    public Clause<String, String> getVariant(ItemStack stack) {
        return new Clause<>(stack.getItem().getId(), "default");
    }
}
