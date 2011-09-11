package com.nakaikensuke.slidepazzle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		if(args.length != 1)
			return;
		
		String path = args[0];
		FileReader file = null;
		try {
			file = new FileReader(path);
			BufferedReader reader = new BufferedReader(file);
			
			// 移動量の制約読み込み
			String   line  = reader.readLine();
			String[] lines =line.split(" ");
			int left  = Integer.parseInt(lines[0]);
			int right = Integer.parseInt(lines[1]);
			int up    = Integer.parseInt(lines[2]);
			int down  = Integer.parseInt(lines[3]);
			
			// ボードの数読み込み
			line = reader.readLine();
			int numBoards = Integer.parseInt(line);
			
			System.err.println("ボード情報");
			System.err.println("左: " + left + ", 右: " + right + ", 上: " + up + ", 下" + down);
			System.err.println("ボード数: " + numBoards);
			
			SlidePazzle pazzle = new SlidePazzle(up, down, left, right, reader);
			pazzle.solve();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
