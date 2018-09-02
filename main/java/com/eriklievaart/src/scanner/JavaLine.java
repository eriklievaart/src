package com.eriklievaart.src.scanner;

import java.util.List;

public class JavaLine {

	private int index;
	private List<String> lines;
	private boolean changed = false;

	public JavaLine(int index, List<String> lines) {
		this.index = index;
		this.lines = lines;
	}

	public String getText() {
		return lines.get(index);
	}

	public void replace(String line) {
		if (lines.get(index).equals(line)) {
			return;
		}
		lines.remove(index);
		lines.add(index, line);
		changed = true;
	}

	public boolean isChanged() {
		return changed;
	}

}
