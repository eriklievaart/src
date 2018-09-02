package com.eriklievaart.src.boot;

import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.src.scanner.FileContents;
import com.eriklievaart.src.scanner.JavaFile;
import com.eriklievaart.src.scanner.JavaLine;
import com.eriklievaart.src.structure.Project;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class ImportFix {

	public static void main(String[] args) {
		String source = "toolkit";
		String fix = "javalightning";
		String filter = "import com.eriklievaart.toolkit.*";

		Map<String, String> simpleToFull = indexSimpleToFullClasses(source);

		Project project = new Project(fix);
		for (JavaFile file : project.getAllJavaFiles()) {

			FileContents contents = file.getContents();
			for (JavaLine imp : contents.getImports()) {
				fixImport(imp, filter, simpleToFull);
			}
			contents.writeback();
		}
	}

	private static void fixImport(JavaLine imp, String filter, Map<String, String> simpleToFull) {
		String original = imp.getText();
		if (original.matches(filter)) {
			String simple = original.replaceFirst(".*[.]([^.;]++);", "$1");
			if (simpleToFull.containsKey(simple)) {
				String replacement = simpleToFull.get(simple);
				imp.replace(Str.sub("import $;", replacement));
				System.out.println(original + "\n    => " + replacement);
				System.out.println();
			}
		}
	}

	private static Map<String, String> indexSimpleToFullClasses(String source) {
		Map<String, String> simpleToFull = new Hashtable<>();
		Project p = new Project(source);
		for (JavaFile file : p.getMainJavaFiles()) {
			simpleToFull.put(file.getSimpleName(), file.getFullyQualifiedName());
		}
		return simpleToFull;
	}
}
