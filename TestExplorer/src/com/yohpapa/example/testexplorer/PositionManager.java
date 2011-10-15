package com.yohpapa.example.testexplorer;

import java.util.HashMap;

/**
 * 各表示階層のラスト表示位置を管理する
 * @author yohpapa
 *
 */
public class PositionManager {
	private HashMap<String, Integer> _positions = new HashMap<String, Integer>();
	
	/**
	 * 表示階層と表示位置を対で保持する
	 * @param path
	 * @param position
	 */
	public void append(String path, int position) {
		_positions.put(path, position);
	}
	
	/**
	 * 表示階層と表示位置の対を削除する
	 * @param path
	 */
	public void remove(String path) {
		if(_positions.containsKey(path)) {
			_positions.remove(path);
		}
	}
	
	/**
	 * 全ての表示位置を削除する
	 */
	public void removeAll() {
		_positions.clear();
	}
	
	/**
	 * 指定されたパスの表示位置を取得する
	 * キャッシュされていない場合は0を返す
	 * @param path
	 * @return
	 */
	public int getPosition(String path) {
		if(_positions.containsKey(path)) {
			return _positions.get(path);
		}
		return 0;
	}
}
