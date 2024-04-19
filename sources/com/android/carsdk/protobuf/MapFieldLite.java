package com.android.carsdk.protobuf;

import com.android.carsdk.protobuf.Internal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public final class MapFieldLite<K, V> implements MutabilityOracle {
    private static final MapFieldLite EMPTY_MAP_FIELD;
    private boolean isMutable = true;
    private MutatabilityAwareMap<K, V> mapData;

    private MapFieldLite(Map<K, V> map) {
        this.mapData = new MutatabilityAwareMap<>(this, map);
    }

    static {
        MapFieldLite mapFieldLite = new MapFieldLite(Collections.emptyMap());
        EMPTY_MAP_FIELD = mapFieldLite;
        mapFieldLite.makeImmutable();
    }

    public static <K, V> MapFieldLite<K, V> emptyMapField() {
        return EMPTY_MAP_FIELD;
    }

    public static <K, V> MapFieldLite<K, V> newMapField() {
        return new MapFieldLite<>(new LinkedHashMap());
    }

    public Map<K, V> getMap() {
        return Collections.unmodifiableMap(this.mapData);
    }

    public Map<K, V> getMutableMap() {
        return this.mapData;
    }

    public void mergeFrom(MapFieldLite<K, V> mapFieldLite) {
        this.mapData.putAll(copy((Map) mapFieldLite.mapData));
    }

    public void clear() {
        this.mapData.clear();
    }

    private static boolean equals(Object obj, Object obj2) {
        if ((obj instanceof byte[]) && (obj2 instanceof byte[])) {
            return Arrays.equals((byte[]) obj, (byte[]) obj2);
        }
        return obj.equals(obj2);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static <K, V> boolean equals(java.util.Map<K, V> r4, java.util.Map<K, V> r5) {
        /*
            r0 = 1
            if (r4 != r5) goto L4
            return r0
        L4:
            int r1 = r4.size()
            int r2 = r5.size()
            r3 = 0
            if (r1 == r2) goto L10
            return r3
        L10:
            java.util.Set r4 = r4.entrySet()
            java.util.Iterator r4 = r4.iterator()
        L18:
            boolean r1 = r4.hasNext()
            if (r1 == 0) goto L42
            java.lang.Object r1 = r4.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r2 = r1.getKey()
            boolean r2 = r5.containsKey(r2)
            if (r2 != 0) goto L2f
            return r3
        L2f:
            java.lang.Object r2 = r1.getValue()
            java.lang.Object r1 = r1.getKey()
            java.lang.Object r1 = r5.get(r1)
            boolean r1 = equals(r2, r1)
            if (r1 != 0) goto L18
            return r3
        L42:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.carsdk.protobuf.MapFieldLite.equals(java.util.Map, java.util.Map):boolean");
    }

    public boolean equals(Object obj) {
        if (obj instanceof MapFieldLite) {
            return equals((Map) this.mapData, (Map) ((MapFieldLite) obj).mapData);
        }
        return false;
    }

    private static int calculateHashCodeForObject(Object obj) {
        if (obj instanceof byte[]) {
            return Internal.hashCode((byte[]) obj);
        }
        if (obj instanceof Internal.EnumLite) {
            throw new UnsupportedOperationException();
        }
        return obj.hashCode();
    }

    static <K, V> int calculateHashCodeForMap(Map<K, V> map) {
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            i += calculateHashCodeForObject(entry.getValue()) ^ calculateHashCodeForObject(entry.getKey());
        }
        return i;
    }

    public int hashCode() {
        return calculateHashCodeForMap(this.mapData);
    }

    private static Object copy(Object obj) {
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            return Arrays.copyOf(bArr, bArr.length);
        }
        return obj;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static <K, V> Map<K, V> copy(Map<K, V> map) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            linkedHashMap.put(entry.getKey(), copy(entry.getValue()));
        }
        return linkedHashMap;
    }

    public MapFieldLite<K, V> copy() {
        return new MapFieldLite<>(copy((Map) this.mapData));
    }

    public void makeImmutable() {
        this.isMutable = false;
    }

    public boolean isMutable() {
        return this.isMutable;
    }

    @Override // com.android.carsdk.protobuf.MutabilityOracle
    public void ensureMutable() {
        if (!isMutable()) {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: classes.dex */
    static class MutatabilityAwareMap<K, V> implements Map<K, V> {
        private final Map<K, V> delegate;
        private final MutabilityOracle mutabilityOracle;

        MutatabilityAwareMap(MutabilityOracle mutabilityOracle, Map<K, V> map) {
            this.mutabilityOracle = mutabilityOracle;
            this.delegate = map;
        }

        @Override // java.util.Map
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override // java.util.Map
        public boolean containsKey(Object obj) {
            return this.delegate.containsKey(obj);
        }

        @Override // java.util.Map
        public boolean containsValue(Object obj) {
            return this.delegate.containsValue(obj);
        }

        @Override // java.util.Map
        public V get(Object obj) {
            return this.delegate.get(obj);
        }

        @Override // java.util.Map
        public V put(K k, V v) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.put(k, v);
        }

        @Override // java.util.Map
        public V remove(Object obj) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(obj);
        }

        @Override // java.util.Map
        public void putAll(Map<? extends K, ? extends V> map) {
            this.mutabilityOracle.ensureMutable();
            this.delegate.putAll(map);
        }

        @Override // java.util.Map
        public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
        }

        @Override // java.util.Map
        public Set<K> keySet() {
            return new MutatabilityAwareSet(this.mutabilityOracle, this.delegate.keySet());
        }

        @Override // java.util.Map
        public Collection<V> values() {
            return new MutatabilityAwareCollection(this.mutabilityOracle, this.delegate.values());
        }

        @Override // java.util.Map
        public Set<Map.Entry<K, V>> entrySet() {
            return new MutatabilityAwareSet(this.mutabilityOracle, this.delegate.entrySet());
        }

        @Override // java.util.Map
        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        @Override // java.util.Map
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    /* loaded from: classes.dex */
    private static class MutatabilityAwareCollection<E> implements Collection<E> {
        private final Collection<E> delegate;
        private final MutabilityOracle mutabilityOracle;

        MutatabilityAwareCollection(MutabilityOracle mutabilityOracle, Collection<E> collection) {
            this.mutabilityOracle = mutabilityOracle;
            this.delegate = collection;
        }

        @Override // java.util.Collection
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override // java.util.Collection
        public boolean contains(Object obj) {
            return this.delegate.contains(obj);
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return new MutatabilityAwareIterator(this.mutabilityOracle, this.delegate.iterator());
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            return this.delegate.toArray();
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.delegate.toArray(tArr);
        }

        @Override // java.util.Collection
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean remove(Object obj) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(obj);
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            return this.delegate.containsAll(collection);
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.removeAll(collection);
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.retainAll(collection);
        }

        @Override // java.util.Collection
        public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
        }

        @Override // java.util.Collection
        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    /* loaded from: classes.dex */
    private static class MutatabilityAwareSet<E> implements Set<E> {
        private final Set<E> delegate;
        private final MutabilityOracle mutabilityOracle;

        MutatabilityAwareSet(MutabilityOracle mutabilityOracle, Set<E> set) {
            this.mutabilityOracle = mutabilityOracle;
            this.delegate = set;
        }

        @Override // java.util.Set, java.util.Collection
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object obj) {
            return this.delegate.contains(obj);
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<E> iterator() {
            return new MutatabilityAwareIterator(this.mutabilityOracle, this.delegate.iterator());
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            return this.delegate.toArray();
        }

        @Override // java.util.Set, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.delegate.toArray(tArr);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(E e) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.add(e);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object obj) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(obj);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> collection) {
            return this.delegate.containsAll(collection);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends E> collection) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.addAll(collection);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> collection) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.retainAll(collection);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> collection) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.removeAll(collection);
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }
    }

    /* loaded from: classes.dex */
    private static class MutatabilityAwareIterator<E> implements Iterator<E> {
        private final Iterator<E> delegate;
        private final MutabilityOracle mutabilityOracle;

        MutatabilityAwareIterator(MutabilityOracle mutabilityOracle, Iterator<E> it) {
            this.mutabilityOracle = mutabilityOracle;
            this.delegate = it;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override // java.util.Iterator
        public E next() {
            return this.delegate.next();
        }

        @Override // java.util.Iterator
        public void remove() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.remove();
        }

        public boolean equals(Object obj) {
            return this.delegate.equals(obj);
        }

        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }
    }
}
