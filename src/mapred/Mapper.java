package mapred;

import util.Pair;

public interface Mapper<K1, V1, K2, V2> {
	public Pair<K2, V2> map(K1 k, V1 v);
}
