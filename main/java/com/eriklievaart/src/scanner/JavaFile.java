package com.eriklievaart.src.scanner;

import java.util.List;

import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class JavaFile {

	private SystemFile file;

	public JavaFile(VirtualFile file) {
		this.file = (SystemFile) file;
	}

	public List<JavaLine> scanImports() {
		return getContents().getImports();
	}

	public String getSimpleName() {
		return file.getBaseName();
	}

	public String getDeclaredPackage() {
		List<String> lines = file.getContent().readLines();
		for (String line : lines) {
			if (line.startsWith("package")) {
				return line.replaceFirst("package ([^;]++);.*", "$1");
			}
		}
		throw new FormattedException("Missing package declaration: %", file.getPath());
	}

	public String getFullyQualifiedName() {
		return getDeclaredPackage() + "." + getSimpleName();
	}

	public FileContents getContents() {
		return new FileContents(file);
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", file.getPath());
	}
}
