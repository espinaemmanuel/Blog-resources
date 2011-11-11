/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.handler.dataimport;

import static org.apache.solr.handler.dataimport.DataImportHandlerException.wrapAndThrow;
import static org.apache.solr.handler.dataimport.DataImportHandlerException.SEVERE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * <p>
 * A {@link Transformer} instance capable of executing functions written in scripting
 * languages as a {@link Transformer} instance.
 * </p>
 * <p/>
 * <p>
 * Refer to <a
 * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>
 * for more details.
 * </p>
 * <p/>
 * <b>This API is experimental and may change in the future.</b>
 *
 * @since solr 1.3
 */
public class ScriptTransformer extends Transformer {
  private ScriptEngine engine;
  private String functionName;  
  private Invocable invocable;

  @Override
  public Object transformRow(Map<String, Object> row, Context context) {
    try {
      if (engine == null)
        initEngine(context);
      if (engine == null)
        return row;
      return invocable.invokeFunction(functionName, new Object[]{row, context});
    } catch (DataImportHandlerException e) {
      throw e;
    } catch (Exception e) {
      wrapAndThrow(SEVERE,e, "Error invoking script for entity " + context.getEntityAttribute("name"));
    }
    return null;
  }

  private void initEngine(Context context) {
    try {
      String scriptText = context.getScript();
      String scriptLang = context.getScriptLanguage();
      if(scriptText == null ){
        throw new DataImportHandlerException(SEVERE,
              "<script> tag is not present under <dataConfig>");
      }
      
      ScriptEngineManager em = new ScriptEngineManager();
      engine = em.getEngineByName(scriptLang);
      engine.eval(scriptText);
      
      invocable = (Invocable) engine;
      
    } catch (Exception e) {
      wrapAndThrow(SEVERE,e, "<script> can be used only in java 6 or above");
    }
  }

  public void setFunctionName(String methodName) {
    this.functionName = methodName;
  }

  public String getFunctionName() {
    return functionName;
  }

}
