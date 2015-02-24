package org.openelis.ui.processor;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Field;
import org.openelis.ui.annotation.Handler;
import org.openelis.ui.annotation.Meta;
import org.openelis.ui.annotation.Queryable;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.annotation.Tab;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.screen.State;

@SupportedAnnotationTypes("org.openelis.ui.annotation.View")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ViewProcessor extends AbstractProcessor {

    private PrintWriter writer;
    String indent = "";
    boolean applyIndent = true;
    JavaFileObject file;

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)  {
		if (!roundEnv.processingOver()) {
			for (TypeElement element : annotations) {
				for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(element)) {
					processingEnv.getMessager().printMessage(Kind.NOTE, "View Processing = "+annotatedElement.asType().toString());
					process((TypeElement)annotatedElement);
				}
			}
		}
		return true;
	}
	
	protected void process(TypeElement element) {
		try {
			//compileDummy(element);
			setWriter(element);
			String template = element.getAnnotation(View.class).template();
			String presenterClass = "";// = element.getAnnotation(View.class).presenter();
			TypeElement presenter = null;
			try {
				element.getAnnotation(View.class).presenter();
			} catch (MirroredTypeException e) {
				presenterClass = e.getTypeMirror().toString();
				processingEnv.getMessager().printMessage(Kind.NOTE,"PresenterClass = "+presenterClass);
				presenter = processingEnv.getElementUtils().getTypeElement(e.getTypeMirror().toString());
			}
			String superName = element.getSimpleName().toString(); 
			String className = element.getSimpleName().toString()+"Impl";
			
			List<VariableElement> fields = getUiFields(ElementFilter.fieldsIn(processingEnv.getElementUtils().getAllMembers(element)));
			
			writePackage(element);
			writeImports(fields);
			writeClassDeclaration(className,superName);
			indent();
			writeTemplate(template,className);
			//writeFieldsName(fields);
			//println("{0} presenter;",presenterClass);
			println();
			writeConstructor(className,presenterClass);
			writeInitialize(fields);
			writeSetState(fields);
			writeTabs(fields);
			writeGetQueryFields(fields);
			writeValidate(fields);
			writeClearErrors(fields);
			if(presenter != null)
				writeHandlers(presenter);
			writeGetters(fields);
			writeDebugIds(fields);
			outdent();
			println("}");
			writer.flush();
			writer.close();
			//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			//processingEnv.getFiler().getResource(StandardLocation., pkg, relativeName)
			//compiler.run(null, null, null, file.toUri().getPath());
			processingEnv.getMessager().printMessage(Kind.NOTE, "Processed "+element.asType().toString());
		} catch (FilerException e) {
			processingEnv.getMessager().printMessage(Kind.NOTE, "By Passing "+element.asType().toString());
		} catch (Exception e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Kind.ERROR, "Error "+e.toString(),element);
			writer.flush();
			writer.close();
		}
		
		
	}
	
	private void writePackage(TypeElement element) {
		println("package {0};",getPackageName(element));
		println();
	}
	
	private String getPackageName(TypeElement element) {
		return element.getQualifiedName().toString().replace("."+element.getSimpleName().toString(), "");
	}
	
	private void writeImports(List<VariableElement> fields) {
		println("import com.google.gwt.uibinder.client.UiHandler;");
		println("import com.google.gwt.uibinder.client.UiTemplate;");
		println("import com.google.gwt.uibinder.client.UiBinder;");
		println("import com.google.gwt.core.client.GWT;");
		println("import com.google.gwt.user.client.ui.Widget;");
		println("import com.google.gwt.user.client.ui.Focusable;");
		println("import org.openelis.ui.screen.State;");
		println("import org.openelis.ui.common.data.QueryData;");
		println("import java.util.ArrayList;");
		println("import javax.inject.Inject;");
		println("import javax.annotation.Generated;");
		HashSet<String> imports = new HashSet<>();
		for (VariableElement field : fields) {
			if (!imports.contains(field.asType().toString())) {
				println("import {0};",getTypeForImport(field));
				imports.add(field.asType().toString());
			}
		}
		println();
	}
	
	private String getTypeForImport(VariableElement field) {
		String imp = field.asType().toString();
		return imp.substring(0,imp.indexOf("<") > 0 ? imp.indexOf("<") : imp.length());
	}
	
	private void writeClassDeclaration(String className, String superName) {
		println("@Generated(\"org.openelis.ui.processor.ViewProcessor\")");
		println("public class {0} extends {1} { ",className,superName);
		println();
	}
	
	private void compileDummy(TypeElement element) throws Exception {
		Writer sourceWriter;
		file = processingEnv
				.getFiler()
				.createSourceFile(
						element.getQualifiedName().toString() + "Impl", element);
		
				sourceWriter = file.openWriter();

		BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
		writer = new PrintWriter(bufferedWriter);
		writePackage(element);
		println("public class {0} {",element.getSimpleName().toString()+"Impl");
		println("}");
		writer.flush();
		writer.close();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, file.toUri().getPath());
	}
	
	private void setWriter(TypeElement element) throws Exception {
		Writer sourceWriter;
		file = processingEnv
				.getFiler()
				.createSourceFile(
						element.getQualifiedName().toString() + "Impl", element);
		
				sourceWriter = file.openWriter();

		BufferedWriter bufferedWriter = new BufferedWriter(sourceWriter);
		writer = new PrintWriter(bufferedWriter);
	}
		
	private String getShortTypeName(VariableElement field) {
		return getClassType(field.asType().toString());
	}
	
	private void writeTemplate(String template,String className) {
		println("@UiTemplate(\"{0}\")",template);
		println("interface ViewUiBinder extends UiBinder<Widget, {0}>{};",className);
        println("protected static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);");
        println();
	}
	
	private void writeFieldsName(List<VariableElement> fields) {
		println("public static final String ");
		indent();
		for (int i = 0; i < fields.size(); i++) {
			print("{0}_NAME = \"{1}\"",getStaticName(fields.get(i)),fields.get(i).getSimpleName());
			if (i < fields.size()-1) {
				println(",");
			}
		}
		println(";");
		println();
		outdent();
	}
	
	private String getStaticName(VariableElement field) {
		return field.getSimpleName().toString().toUpperCase();
	}
	
	private void writeConstructor(String className, String presenter) {
		println("@Inject");
		println("public {0}() {",className);
		indent();
		println("super();");
		println("initWidget(uiBinder.createAndBindUi(this));");
		println("initialize();");
		outdent();
		println("}");
		println();
	}
	
	private void writeInitialize(List<VariableElement> fields) {
		println("protected void initialize() {");
		indent();
		writeShortcuts(fields);
		//writeEnableHandler(fields);
		outdent();
		println("}");
		println();
	}
	
	private void writeShortcuts(List<VariableElement> fields) {
		for (VariableElement field : fields) {
			String shortcut = null;
			if (field.getAnnotation(Field.class) != null) {
				shortcut = field.getAnnotation(Field.class).shortcut();
			} else if (field.getAnnotation(Shortcut.class) != null) {
				shortcut = field.getAnnotation(Shortcut.class).value();
			}
			if (shortcut != null && !"".equals(shortcut)) {
				println("addShortcut({0},'{1}', ShortKeys.CTRL);",field.getSimpleName(),shortcut);
			}
		}
	}
	
	private void writeSetState(List<VariableElement> fields) {
		println("public void setState(State state) {");
		indent();
		for (VariableElement field : fields) {
			State[] states = null;
			if (field.getAnnotation(Field.class) != null) {
				states = field.getAnnotation(Field.class).enable();
			} else if (field.getAnnotation(Enable.class) != null) {
				states = field.getAnnotation(Enable.class).value();
			}
			if (states != null) {
				print("this.{0}.setEnabled(isState(",field.getSimpleName());
				for (int i = 0; i < states.length; i++) {
					if(i > 0)
						print(",");
					print("State."+states[i].toString());
				}
				println(").contains(state));");
			}
			boolean queryable = false;
			if (field.getAnnotation(Field.class) != null) {
				queryable = field.getAnnotation(Field.class).queryable();
			} else if (field.getAnnotation(Queryable.class) != null) {
				queryable = true;
			}
		    if (queryable) {
		    	println("this.{0}.setQueryMode(state == State.QUERY);",field.getSimpleName());
		    }
		}
		println("super.setState(state);");
		outdent();
		println("}");
		println();
	}
	
	private void writeTabs(List<VariableElement> fields) {
		println("protected Focusable getNextWidget(Focusable widget, boolean forward) {");
		indent();
		for (VariableElement field : fields) {
			String[] tab = null;
			if (field.getAnnotation(Field.class) != null) {
				tab = field.getAnnotation(Field.class).tab();
			} else if (field.getAnnotation(Tab.class) != null) {
				tab = field.getAnnotation(Tab.class).value();
			}
			if(tab != null && tab.length > 0) {
			    println("if ({0}.equals(widget)) {",field.getSimpleName());
			    indent();
			    if (!"".equals(tab[0])) {
				    print("return forward ? {0} : ",tab[0]);
			    }
			    if (tab.length > 1 && !"".equals(tab[1])) {
   				    println(tab[1]+";");
			    } else {
				    println("null;");
			    }
			    outdent();
			    println("}");
			}
		}
		println("return super.getNextWidget(widget,forward);");
		outdent();
		println("}");
		println();
	}
	
	private void writeGetQueryFields(List<VariableElement> fields) {
		println("public ArrayList<QueryData> getQueryFields() {");
		indent();
		println("ArrayList<QueryData> list = new ArrayList<>();");
		for (VariableElement field : fields) {
			boolean queryable = false;
			if (field.getAnnotation(Field.class) != null) {
				queryable = field.getAnnotation(Field.class).queryable();
			} else if (field.getAnnotation(Queryable.class) != null) {
				queryable = true;
			}
			String meta = "";
			if (field.getAnnotation(Field.class) != null) {
				meta = field.getAnnotation(Field.class).meta();
			} else if (field.getAnnotation(Meta.class) != null) {
 				meta = field.getAnnotation(Meta.class).value();
			}
			if(queryable && "".equals(meta)) {
				processingEnv.getMessager().printMessage(Kind.ERROR, "Meta must be defined for queryable field", field);
			}
		    if (queryable) {
				println("getQuery(list,{0}.getQuery(),\"{1}\");",field.getSimpleName(),meta);
			}
		}
		println("super.getQueryFields(list);");
		println("return list;");
		outdent();
		println("}");
		println();
	}
	
	private void writeValidate(List<VariableElement> fields) {
		println("public Validation validate() {");
		indent();
		println("Validation validation = new Validation();");
		for (VariableElement field : fields) {
			boolean validate = false;
			if (field.getAnnotation(Field.class) != null) {
				validate = field.getAnnotation(Field.class).validate();
			} else if (field.getAnnotation(Validate.class) != null) {
				validate = true;
			}
		    if (validate) {
				println("isValid({0},validation);",field.getSimpleName());
			}
		}
		println("super.validate(validation);");
		println("return validation;");
		outdent();
		println("}");
		println();
	}
	
	private void writeClearErrors(List<VariableElement> fields) {
		println("public void clearErrors() {");
		indent();
		for (VariableElement field : fields) {
			boolean validate = false;
			if (field.getAnnotation(Field.class) != null) {
				validate = field.getAnnotation(Field.class).validate();
			} else if (field.getAnnotation(Validate.class) != null) {
				validate = true;
			}
		    if (validate) {
				println("{0}.clearExceptions();",field.getSimpleName());
			}
		}
		println("super.clearErrors();");
		outdent();
		println("}");
		println();
	}
	
	private void writeHandlers(TypeElement presenter) {
		int i = 0;
		for (ExecutableElement method : ElementFilter.methodsIn(processingEnv.getElementUtils().getAllMembers(presenter)))  {
			if (method.getAnnotation(Handler.class) != null) {
				Handler handler = method.getAnnotation(Handler.class);
				print("@UiHandler({");
				for(int j = 0; j < handler.value().length; j++) {
					if (j > 0) {
						print(",");
					}
					print("\""+handler.value()[j]+"\"");
				}
				println("})");
				println("protected void handler{0}({1} arg) {",i++,method.getParameters().get(0).asType().toString());
				indent();
				println("(({0})presenter).{1}(arg);",presenter.asType().toString(),method.getSimpleName());
				outdent();
				println("}");
				println();
			}
		}
	}
	
	private void writeGetters(List<VariableElement> fields) {
		for (VariableElement field : fields) {
			println("public {0} {1}() {",getShortTypeName(field),formatGetterName(field.getSimpleName().toString()));
			indent();
			println("return {0};",field.getSimpleName());
			outdent();
			println("}");
			println();
		}
	}
	
	private void writeDebugIds(List<VariableElement> fields) {
		println("public void onEnsureDebugId(String baseId) {");
		indent();
		for (VariableElement field : fields) {
			print("{0}.ensureDebugId(baseId + \".",field.getSimpleName());
			if (field.getAnnotation(Meta.class) != null) {
				println(field.getAnnotation(Meta.class).value()+"\");");
			} else {
				println(field.getSimpleName()+"\");");
			}
		}
		outdent();
		println("}");
		println();
	}
	
	private List<VariableElement> getUiFields(List<VariableElement> fields) {
		ArrayList<VariableElement> uiFields = new ArrayList<>();
		for (VariableElement field : fields) {
			for(AnnotationMirror mirror : field.getAnnotationMirrors()) {
				if (mirror.getAnnotationType().asElement().getSimpleName().toString().contains("UiField")) {
					uiFields.add(field);
				}
			}
		}
		return uiFields;
	}
	
	private String getClassType(String full) {
		return full.substring(full.lastIndexOf(".",full.indexOf("<") > 0 ? full.indexOf("<") : full.length())+1,full.length());
	}

	private String formatGetterName(String name) {
		return name = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
	}
	
	private void println() {
		writer.println();
		
		applyIndent = true;
	}
	
	private void println(String target,Object... parameters) {
		writer.println((applyIndent ? indent : "") + replaceParameters(target, parameters));
		applyIndent = true;
	}
	
	private void print(String target,Object... parameters) {
		writer.print((applyIndent ? indent : "") + replaceParameters(target,parameters));
		applyIndent = false;
	}
	
	private void indent() {
		indent += "    ";
	}
	
	private void outdent() {
		if(indent.length() > 0)
			indent = indent.substring(0,indent.length()-4);
	}
    
	private String replaceParameters(String target, Object... parameters) {
        String result = target;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                result = result.replace("{" + i + "}", String.valueOf(parameters[i]));
            }
        }
        return result;
    }

}
