package org.openelis.ui.processor;

import java.util.List;

import javax.annotation.processing.FilerException;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;


@SupportedAnnotationTypes("org.openelis.ui.annotation.Service")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ServiceProcessor extends Processor {
	String className,packageName,syncInt,asyncInt;
	List<ExecutableElement> methods;

	protected void process(TypeElement element) {
		syncInt = element.getSimpleName().toString();
		className = element.getSimpleName().toString()+"Impl";
		asyncInt = syncInt +"Async";
		packageName = element.getQualifiedName().toString().replace("."+element.getSimpleName().toString(), "");
		methods = ElementFilter.methodsIn(element.getEnclosedElements());
		createAsyncInt(element);
		createService(element);
	}
	
	private void createAsyncInt(TypeElement element) {
		try {
			setWriter(element,packageName+"."+asyncInt);
			writePackage(packageName);
			println();
			println("import com.google.gwt.user.client.rpc.AsyncCallback;");
			println();
			println("public interface {0} {",asyncInt);
			indent();
			writeAsyncMethods();
			outdent();
			println("}");
			writer.flush();
			writer.close();
		} catch (FilerException e) {
			processingEnv.getMessager().printMessage(Kind.NOTE, "By Passing "+element.asType().toString());
		} catch (Exception e) {
			e.printStackTrace();
			processingEnv.getMessager().printMessage(Kind.ERROR, "Error "+e.toString(),element);
			writer.flush();
			writer.close();
		}	
	}
	
	private void writeAsyncMethods() {
		for(ExecutableElement method : methods) {
			print("public void {0}(",method.getSimpleName());
			for (VariableElement param : method.getParameters()) {
				print("{0} {1}, ",param.asType().toString(),param.getSimpleName());
			}
			String returnType = method.getReturnType().toString();
			if (returnType.equals("void")) {
				returnType = "Void";
			}
			println("AsyncCallback<{0}> callback);",returnType);
		}
	}
	
	private void createService(TypeElement element) {
		try {
			setWriter(element,packageName+"."+className);
			writePackage(packageName);
			writeImports();
			writeClassDeclaration();
			indent();
			writeMembers();
			writeConstructor();
			writeMethods();
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
	
	private void writeImports() {
		println("import org.openelis.ui.screen.Callback;");
		println("import org.openelis.ui.services.TokenService;");
		println("import com.google.gwt.core.client.GWT;");
		println("import com.google.gwt.user.client.rpc.AsyncCallback;");
		println("import com.google.gwt.user.client.rpc.HasRpcToken;");
		println();
	}
	
	private void writeClassDeclaration() {
		println("public enum {0} implements {1}, {2} {",className,syncInt,asyncInt);
	}
	
	private void writeMembers() {
	    println("INSTANCE;");
	    println();
	    println("{0} service;",asyncInt);
	    println();
	}
	
	private void writeConstructor() {
	    println("private {0}() {",className);
	    indent();
	    println("service = ({0})GWT.create({1}.class);",asyncInt,syncInt);
	    println("((HasRpcToken)service).setRpcToken(TokenService.getToken());");
	    outdent();
	    println("}");
	    println();
	}
	
	private void writeMethods() {
		for(ExecutableElement method : methods) {
			writeAsyncMethod(method);
			writeSyncMethod(method);
		}
	}
	
	private void writeAsyncMethod(ExecutableElement method) {
		print("public void {0}(",method.getSimpleName());
		for (VariableElement param : method.getParameters()) {			
			print("{0} {1}, ",param.asType().toString(),param.getSimpleName());
		}
		String returnType = method.getReturnType().toString();
		if (returnType.equals("void")) {
			returnType = "Void";
		}
		println("AsyncCallback<{0}> callback) {",returnType);
		indent();
		print("service.{0}(",method.getSimpleName());
		for (VariableElement param : method.getParameters()) {
			print("{0}, ",param.getSimpleName());
		}
		println("callback);");
		outdent();
		println("}");
		println();
	}
	
	private void writeSyncMethod(ExecutableElement method) {
		print("public {0} {1}(",method.getReturnType().toString(),method.getSimpleName());
		for (int i = 0; i < method.getParameters().size(); i++) {
			VariableElement param = method.getParameters().get(i);
			if (i > 0) {
				print(",");
			}
			if (param.asType().getKind() == TypeKind.ARRAY) {
				if (method.isVarArgs() && method.getParameters().indexOf(param) == method.getParameters().size()-1) {
					String type = param.asType().toString();
					type = type.substring(0, type.length()-2);
					print("{0}... {1}",type,param.getSimpleName());
				} else {
					print("{0} {1} ",param.asType().toString(),param.getSimpleName());
				}
			} else {
				print("{0} {1} ",param.asType().toString(),param.getSimpleName());
			}
		}
		println(") throws Exception {");
		indent();
		String returnType = method.getReturnType().toString();
		if (returnType.equals("void")) {
			returnType = "Void";
		}
		println("Callback<{0}> callback = new Callback<{0}>();",returnType);
		print("service.{0}(",method.getSimpleName());
		for (int i = 0; i < method.getParameters().size(); i++) {
			VariableElement param = method.getParameters().get(i);
			print("{0}, ",param.getSimpleName());
		}
		println("callback);");
		if (returnType.equals("Void")) {
			println("callback.getResult();");
		} else {
			println("return callback.getResult();");
		}
		outdent();
		println("}");
		println();
	}
}
