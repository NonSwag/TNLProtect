package net.nonswag.tnl.protect.api.flag.bounds;

import javax.annotation.Nonnull;

public interface ClassBound<C> {
    boolean test(@Nonnull C clazz);
}
