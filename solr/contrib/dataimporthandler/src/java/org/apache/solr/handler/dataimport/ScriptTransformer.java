begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|Invocable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngine
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngineManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptException
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link Transformer} instance capable of executing functions written in scripting  * languages as a {@link Transformer} instance.  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|ScriptTransformer
specifier|public
class|class
name|ScriptTransformer
extends|extends
name|Transformer
block|{
DECL|field|engine
specifier|private
name|Invocable
name|engine
decl_stmt|;
DECL|field|functionName
specifier|private
name|String
name|functionName
decl_stmt|;
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
name|initEngine
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
return|return
name|row
return|;
return|return
name|engine
operator|.
name|invokeFunction
argument_list|(
name|functionName
argument_list|,
operator|new
name|Object
index|[]
block|{
name|row
block|,
name|context
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Error invoking script for entity "
operator|+
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//will not reach here
return|return
literal|null
return|;
block|}
DECL|method|initEngine
specifier|private
name|void
name|initEngine
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|String
name|scriptText
init|=
name|context
operator|.
name|getScript
argument_list|()
decl_stmt|;
name|String
name|scriptLang
init|=
name|context
operator|.
name|getScriptLanguage
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptText
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"<script> tag is not present under<dataConfig>"
argument_list|)
throw|;
block|}
name|ScriptEngineManager
name|scriptEngineMgr
init|=
operator|new
name|ScriptEngineManager
argument_list|()
decl_stmt|;
name|ScriptEngine
name|scriptEngine
init|=
name|scriptEngineMgr
operator|.
name|getEngineByName
argument_list|(
name|scriptLang
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptEngine
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Cannot load Script Engine for language: "
operator|+
name|scriptLang
argument_list|)
throw|;
block|}
if|if
condition|(
name|scriptEngine
operator|instanceof
name|Invocable
condition|)
block|{
name|engine
operator|=
operator|(
name|Invocable
operator|)
name|scriptEngine
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"The installed ScriptEngine for: "
operator|+
name|scriptLang
operator|+
literal|" does not implement Invocable.  Class is "
operator|+
name|scriptEngine
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
try|try
block|{
name|scriptEngine
operator|.
name|eval
argument_list|(
name|scriptText
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ScriptException
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"'eval' failed with language: "
operator|+
name|scriptLang
operator|+
literal|" and script: \n"
operator|+
name|scriptText
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setFunctionName
specifier|public
name|void
name|setFunctionName
parameter_list|(
name|String
name|methodName
parameter_list|)
block|{
name|this
operator|.
name|functionName
operator|=
name|methodName
expr_stmt|;
block|}
DECL|method|getFunctionName
specifier|public
name|String
name|getFunctionName
parameter_list|()
block|{
return|return
name|functionName
return|;
block|}
block|}
end_class

end_unit

