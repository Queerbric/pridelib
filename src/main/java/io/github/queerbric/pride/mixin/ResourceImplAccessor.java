package io.github.queerbric.pride.mixin;

import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResourceImpl.class)
public interface ResourceImplAccessor {

    @Accessor
    Identifier getId();
}
