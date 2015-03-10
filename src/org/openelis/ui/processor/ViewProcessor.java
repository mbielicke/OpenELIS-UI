package org.openelis.ui.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Field;
import org.openelis.ui.annotation.Focus;
import org.openelis.ui.annotation.Handler;
import org.openelis.ui.annotation.Meta;
import org.openelis.ui.annotation.Queryable;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.annotation.Tab;
import org.openelis.ui.annotation.Validate;
import org.openelis.ui.annotation.View;
import org.openelis.ui.screen.Permission;
import org.openelis.ui.screen.State;

@SupportedAnnotationTypes("org.openelis.ui.annotation.View")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ViewProcessor extends Processor {

    Set<String> fieldNames;
    List<VariableElement> fields;
    String template,presenterClass,superName,className,packageName;
    TypeElement presenter;
	
	protected void process(TypeElement element) {
		try {
			initialize(element);
			writePackage(packageName);
			writeImports();
			writeClassDeclaration();
			indent();
			writeTemplate();
			println();
			writeConstructor();
			writeInitialize();
			writeSetState();
			writeTabs();
			writeGetQueryFields();
			writeValidate();
			writeClearErrors();
			if(presenter != null)
				writeHandlers();
			writeGetters();
			writeDebugIds();
			outdent();
			println("}");
			writer.flush();
			writer.close();
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
	
	private void initialize(TypeElement element) throws Exception {
		template = element.getAnnotation(View.class).template();
		presenterClass = "";
		presenter = null;
		try {
			element.getAnnotation(View.class).presenter();
		} catch (MirroredTypeException e) {
			presenterClass = e.getTypeMirror().toString();
			presenter = processingEnv.getElementUtils().getTypeElement(e.getTypeMirror().toString());
		}
		superName = element.getSimpleName().toString(); 
		className = element.getSimpleName().toString()+"Impl";
		packageName = element.getQualifiedName().toString().replace("."+element.getSimpleName().toString(), "");
		
		fields = getUiFields(ElementFilter.fieldsIn(processingEnv.getElementUtils().getAllMembers(element)));
		fieldNames = new HashSet<String>();
		for(VariableElement field : fields) {
			fieldNames.add(field.getSimpleName().toString());
		}
		try {
			setWriter(element,packageName+"."+className);
		} catch (Exception e) {
			
		}
	}
		
	private void writeImports() {
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
		println();		
	}
	
	private void writeClassDeclaration() {
		println("@Generated(\"org.openelis.ui.processor.ViewProcessor\")");
		println("public class {0} extends {1} { ",className,superName);
		println();
	}
			
	private void writeTemplate() {
		println("@UiTemplate(\"{0}\")",template);
		println("interface ViewUiBinder extends UiBinder<Widget, {0}>{};",className);
        println("protected static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);");
        println();
	}
		
	private void writeConstructor() {
		println("public {0}() {",className);
		indent();
		println("super();");
		println("initWidget(uiBinder.createAndBindUi(this));");
		println("initialize();");
		outdent();
		println("}");
		println();
	}
	
	private void writeInitialize() {
		println("protected void initialize() {");
		indent();
		writeShortcuts();
		outdent();
		println("}");
		println();
	}
	
	private void writeShortcuts() {
		String shortcut;
		for (VariableElement field : fields) {
			shortcut = getShortcutValue(field);
			if (shortcut != null && !"".equals(shortcut)) {
				println("addShortcut({0},'{1}', ShortKeys.CTRL);",field.getSimpleName(),shortcut);
			}
		}
	}
	
	private String getShortcutValue(VariableElement field) {
		if (field.getAnnotation(Shortcut.class) != null) {
			return field.getAnnotation(Shortcut.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).shortcut();
		} 
		return null;
	}
	
	private void writeSetState() {
		State[] states,focus;
		Permission permission;
		
		println("public void setState(State state) {");
		indent();
		for (VariableElement field : fields) {
			states = getStatesValue(field);
			if (states != null) {
				print("this.{0}.setEnabled(isState(",field.getSimpleName());
				for (int i = 0; i < states.length; i++) {
					if(i > 0)
						print(",");
					print("State."+states[i].toString());
				}
				print(").contains(state)");
				permission = getPermissionValue(field);
				if (permission != null) {
					print(" && presenter.permissions().has");
					switch (permission) {
						case SELECT :
							print("Select");
							break;
						case UPDATE :
							print("Update");
							break;
						case DELETE :
							print("Delete");
							break;
						case ADD :
							print("Add");
							break;
					}
					print("Permission()");
				}
				println(");");
			}
		    if (getQueryableValue(field)) {
		    	println("this.{0}.setQueryMode(state == State.QUERY);",field.getSimpleName());
		    }
		    focus = getFocusValue(field);
		    if (getFocusValue(field) != null) {
		    	print("if (isState(");
		    	for (int i = 0; i < focus.length; i++) {
					if(i > 0)
						print(",");
					print("State."+focus[i].toString());
				}
				println(").contains(state)) {");
				indent();
				println("this.{0}.setFocus(true);",field.getSimpleName());
				outdent();
				println("}");
		    }
		}
		println("super.setState(state);");
		outdent();
		println("}");
		println();
	}
	
	private Permission getPermissionValue(VariableElement field) {
		Permission val = null;
		
		if (field.getAnnotation(org.openelis.ui.annotation.Permission.class) != null) {
			val = field.getAnnotation(org.openelis.ui.annotation.Permission.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			val = field.getAnnotation(Field.class).permission();
		}
		return val != Permission.NONE ? val : null;
	}
	
	private State[] getFocusValue(VariableElement field) {
		State[] ret = null;
		
		if (field.getAnnotation(Focus.class) != null) {
			ret = field.getAnnotation(Focus.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			ret = field.getAnnotation(Field.class).focus();
		}
		return ret != null && ret.length > 0 ? ret : null;
	}
	
	private State[] getStatesValue(VariableElement field) {
		if (field.getAnnotation(Enable.class) != null) {
			return field.getAnnotation(Enable.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).enable();
		} 
		return null;
	}
	
	private boolean getQueryableValue(VariableElement field) {
		if (field.getAnnotation(Queryable.class) != null) {
			return true;
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).queryable();
		} 
		return false;
	}
	
	private void writeTabs() {
		String[] tab;
		
		println("protected Focusable getNextWidget(Focusable widget, boolean forward) {");
		indent();
		for (VariableElement field : fields) {
			tab = getTabValue(field);
			validateTab(field);
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
	
	private void validateTab(VariableElement field) {
		String[] tabs;
		
		tabs = getTabValue(field);
		
		if(tabs == null)
			return;
		
		for (String tab : tabs) {
			if (!fieldNames.contains(tab)) {
				processingEnv.getMessager().printMessage(Kind.ERROR, "View does not contain field : "+tab+" for tab value",field);
			}
		}
	}
	
	private String[] getTabValue(VariableElement field) {
		if (field.getAnnotation(Tab.class) != null) {
			return field.getAnnotation(Tab.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).tab();
		} 
		return null;
	}
	
	private void writeGetQueryFields() {
		String meta;
		boolean queryable;
		
		println("public ArrayList<QueryData> getQueryFields() {");
		indent();
		println("ArrayList<QueryData> list = new ArrayList<>();");
		for (VariableElement field : fields) {
			meta = getMetaValue(field);
			queryable= getQueryableValue(field);
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
	
	private String getMetaValue(VariableElement field) {
		if (field.getAnnotation(Meta.class) != null) {
			return field.getAnnotation(Meta.class).value();
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).meta();
		} 
		return "";
	}
	
	private void writeValidate() {
		println("public Validation validate() {");
		indent();
		println("Validation validation = new Validation();");
		for (VariableElement field : fields) {			
		    if (getValidateValue(field)) {
				println("isValid({0},validation);",field.getSimpleName());
			}
		}
		println("super.validate(validation);");
		println("return validation;");
		outdent();
		println("}");
		println();
	}
	
	private boolean getValidateValue(VariableElement field) {
		if (field.getAnnotation(Validate.class) != null) {
			return true;
		} else if (field.getAnnotation(Field.class) != null) {
			return field.getAnnotation(Field.class).validate();
		} 
		return false;
	}
	
	private void writeClearErrors() {
		println("public void clearErrors() {");
		indent();
		for (VariableElement field : fields) {
		    if (getValidateValue(field)) {
				println("{0}.clearExceptions();",field.getSimpleName());
			}
		}
		println("super.clearErrors();");
		outdent();
		println("}");
		println();
	}
	
	private void writeHandlers() {
		Handler handler;
		
		int i = 0;
		for (ExecutableElement method : ElementFilter.methodsIn(processingEnv.getElementUtils().getAllMembers(presenter)))  {
			handler = method.getAnnotation(Handler.class);
			if (handler != null) {
				if (!fieldNames.contains(handler.value()[0])) {
					processingEnv.getMessager().printMessage(Kind.ERROR, "View does not contain field : "+handler.value()[0], method,method.getAnnotationMirrors().get(0));
					continue;
				}
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
	
	private void writeGetters() {
		for (VariableElement field : fields) {
			println("public {0} {1}() {",field.asType().toString(),formatGetterName(field.getSimpleName().toString()));
			indent();
			println("return {0};",field.getSimpleName());
			outdent();
			println("}");
			println();
		}
	}
	
	private void writeDebugIds() {
		String meta;
		
		println("public void onEnsureDebugId(String baseId) {");
		indent();
		for (VariableElement field : fields) {
			print("{0}.ensureDebugId(baseId + \".",field.getSimpleName());
			meta = getMetaValue(field);
			if (!"".equals(meta) && meta != null) {
				println(meta+"\");");
			} else {
				println(field.getSimpleName()+"\");");
			}
		}
		outdent();
		println("}");
		println();
	}
	
	private List<VariableElement> getUiFields(List<VariableElement> members) {
		ArrayList<VariableElement> uiFields = new ArrayList<>();
		for (VariableElement field : members) {
			for(AnnotationMirror mirror : field.getAnnotationMirrors()) {
				if (mirror.getAnnotationType().asElement().getSimpleName().toString().contains("UiField")) {
					uiFields.add(field);
				}
			}
		}
		return uiFields;
	}
	
	private String formatGetterName(String name) {
		return name = "get" + name.substring(0,1).toUpperCase() + name.substring(1);
	}

}
