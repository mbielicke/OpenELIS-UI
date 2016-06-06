package org.openelis.ui.processor;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.Diagnostic.Kind;

public abstract class Processor extends AbstractProcessor {
	
    protected PrintWriter writer;
    String indent = "";
    boolean applyIndent = true;
    
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)  {
		if (!roundEnv.processingOver()) {
			for (TypeElement element : annotations) {
				for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(element)) {
					if(!wasGeneratedByThis(annotatedElement)) {
						processingEnv.getMessager().printMessage(Kind.NOTE, "Processing View : "+annotatedElement.asType().toString());
						process((TypeElement)annotatedElement);
					}
				}
			}
		}
		return true;
	}
	
	protected abstract void process(TypeElement element);
	
	protected boolean wasGeneratedByThis(Element element) {
		Generated gen = element.getAnnotation(Generated.class);
		return gen != null;
	}
	
	protected void setWriter(TypeElement element, String className) throws Exception {

		Writer sourceWriter;
		FileObject file = processingEnv
				.getFiler()
				.createSourceFile(className, element);
		
				sourceWriter = file.openWriter();

		BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
		writer = new PrintWriter(bufferedWriter);
	}
	
	protected void writePackage(String packageName) {
		println("package {0};",packageName);
		println();
	}
    
	protected void println() {
		writer.println();
		
		applyIndent = true;
	}
	
	protected void println(String target,Object... parameters) {
		writer.println((applyIndent ? indent : "") + replaceParameters(target, parameters));
		applyIndent = true;
	}
	
	protected void print(String target,Object... parameters) {
		writer.print((applyIndent ? indent : "") + replaceParameters(target,parameters));
		applyIndent = false;
	}
	
	protected void indent() {
		indent += "    ";
	}
	
	protected void outdent() {
		if(indent.length() > 0)
			indent = indent.substring(0,indent.length()-4);
	}
    
	protected String replaceParameters(String target, Object... parameters) {
        String result = target;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                result = result.replace("{" + i + "}", String.valueOf(parameters[i]));
            }
        }
        return result;
    }

}
