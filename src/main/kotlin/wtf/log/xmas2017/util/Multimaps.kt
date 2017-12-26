package wtf.log.xmas2017.util

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap

inline fun <K, V> ImmutableMultimap<K, V>.alter(block: Multimap<K, V>.() -> Unit): ImmutableMultimap<K, V> {
    return ImmutableMultimap.copyOf(HashMultimap.create(this).apply(block))
}
