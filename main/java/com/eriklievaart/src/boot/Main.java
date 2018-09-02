package com.eriklievaart.src.boot;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eriklievaart.src.scanner.JavaFile;
import com.eriklievaart.src.scanner.JavaLine;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.vfs.api.file.SystemFile;
import com.eriklievaart.toolkit.vfs.api.file.VirtualFile;

public class Main {

	public static void main(String[] args) {

		File root = new File("/home/erikl/Development/git/toolkit");
		File java = new File(root, "main/java");
		File lievaart = new File(java, "com/eriklievaart/toolkit");
		for (VirtualFile bundle : new SystemFile(lievaart).getChildrenAlphabeticallyDirectoriesFirst()) {
			System.out.println(bundle.getName());
			scanImports((SystemFile) bundle);
			System.out.println();
		}

	}

	private static void scanImports(SystemFile bundle) {
		Set<String> imports = new HashSet<>();
		for (VirtualFile file : bundle.scan("java")) {
			for (JavaLine scanned : new JavaFile((SystemFile) file).scanImports()) {
				imports.add(Str.sub("$ ($)", scanned.getText(), file.getName()));
			}
		}
		List<String> sorted = ListTool.sortedCopy(imports);
		for (String imp : sorted) {
			if (imp.startsWith("com.eriklievaart") && !imp.contains("toolkit." + bundle.getName())) {
				System.out.println("\t=> " + imp);
			}
		}
	}
}
