package mapred;

import java.util.List;

import util.Pair;

public interface Mapper<K1, V1, K2, V2> {
	public void map(K1 k, V1 v, List<Pair<K2, V2>> output);
}
