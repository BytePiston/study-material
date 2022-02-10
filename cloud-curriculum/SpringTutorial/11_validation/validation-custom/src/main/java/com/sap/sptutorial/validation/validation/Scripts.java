package com.sap.sptutorial.validation.validation;

import java.io.InputStream;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Scripts {

	@Cacheable(value = "scripts", unless = "#result == null")
	public CompiledScript getValidationScript(String scriptFileName) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(scriptFileName);) {
			String script = IOUtils.toString(input);
			CompiledScript compiledScript = ((Compilable) engine).compile(script);
			return compiledScript;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
