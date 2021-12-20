package fr.craftandconquest.warofsquirrels.utils;

import lombok.Getter;
import lombok.Setter;

public class Pair<K, V> {
    @Getter
    @Setter
    private K key;
    @Getter
    @Setter
    private V value;

    public Pair(K _key, V _value) {
        key = _key;
        value = _value;
    }
}
