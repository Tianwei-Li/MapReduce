package util;

public class Pair<K, V> {
	K k;
	V v;
	public Pair(K k1, V v1) {
		k = k1;
		v = v1;
	}
	@Override
	public String toString() {
		return "Pair [k=" + k + ", v=" + v + "]";
	}
}
