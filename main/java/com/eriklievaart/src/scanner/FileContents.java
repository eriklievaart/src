package com.eriklievaart.src.scanner;

import java.util.List;
import java.util.stream.Collectors;

import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;

public class FileContents {

	private final SystemFile file;
	private final List<JavaLine> lines;

	public FileContents(SystemFile file) {
		this.file = file;
		this.lines = getJavaLines(file.getContent().readLines());
	}

	public JavaLine getPackageDeclaration() {
		for (JavaLine line : lines) {
			if (line.getText().startsWith("package")) {
				return line;
			}
		}
		throw new FormattedException("Unable to find package declaration for file: " + file);
	}

	public List<JavaLine> getImports() {
		List<JavaLine> result = NewCollection.list();

		for (JavaLine line : lines) {
			String text = line.getText();

			if (text.startsWith("import")) {
				result.add(line);
			}
			if (text.startsWith("public") || text.startsWith("protected") || text.startsWith("private")) {
				return result;
			}
		}
		return result;
	}

	private static List<JavaLine> getJavaLines(List<String> raw) {
		List<JavaLine> result = NewCollection.list();
		ListTool.iterate(raw, (index, line) -> {
			result.add(new JavaLine(index, raw));
		});
		return result;
	}

	public SystemFile getFile() {
		return file;
	}

	public void writeback() {
		if (isChanged()) {
			writeback(file);
		}
	}

	public void writeback(SystemFile destination) {
		destination.getContent().writeLines(lines.stream().map(JavaLine::getText).collect(Collectors.toList()));
	}

	private boolean isChanged() {
		for (JavaLine line : lines) {
			if (line.isChanged()) {
				return true;
			}
		}
		return false;
	}
}
