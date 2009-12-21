begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|EntityProcessorBase
operator|.
name|*
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
name|EntityProcessorBase
operator|.
name|SKIP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * A Wrapper over EntityProcessor instance which performs transforms and handles multi-row outputs correctly.  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|EntityProcessorWrapper
specifier|public
class|class
name|EntityProcessorWrapper
extends|extends
name|EntityProcessor
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EntityProcessorWrapper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|delegate
specifier|private
name|EntityProcessor
name|delegate
decl_stmt|;
DECL|field|docBuilder
specifier|private
name|DocBuilder
name|docBuilder
decl_stmt|;
DECL|field|onError
specifier|private
name|String
name|onError
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|field|resolver
specifier|private
name|VariableResolverImpl
name|resolver
decl_stmt|;
DECL|field|entityName
specifier|private
name|String
name|entityName
decl_stmt|;
DECL|field|transformers
specifier|protected
name|List
argument_list|<
name|Transformer
argument_list|>
name|transformers
decl_stmt|;
DECL|field|rowcache
specifier|protected
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rowcache
decl_stmt|;
DECL|field|contextCopy
specifier|private
name|Context
name|contextCopy
decl_stmt|;
DECL|method|EntityProcessorWrapper
specifier|public
name|EntityProcessorWrapper
parameter_list|(
name|EntityProcessor
name|delegate
parameter_list|,
name|DocBuilder
name|docBuilder
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|docBuilder
operator|=
name|docBuilder
expr_stmt|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|rowcache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|resolver
operator|=
operator|(
name|VariableResolverImpl
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
expr_stmt|;
comment|//context has to be set correctly . keep the copy of the old one so that it can be restored in destroy
name|contextCopy
operator|=
name|resolver
operator|.
name|context
expr_stmt|;
name|resolver
operator|.
name|context
operator|=
name|context
expr_stmt|;
if|if
condition|(
name|entityName
operator|==
literal|null
condition|)
block|{
name|onError
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|ON_ERROR
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|onError
operator|==
literal|null
condition|)
name|onError
operator|=
name|ABORT
expr_stmt|;
name|entityName
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DataConfig
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|delegate
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|loadTransformers
name|void
name|loadTransformers
parameter_list|()
block|{
name|String
name|transClasses
init|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|TRANSFORMER
argument_list|)
decl_stmt|;
if|if
condition|(
name|transClasses
operator|==
literal|null
condition|)
block|{
name|transformers
operator|=
name|Collections
operator|.
name|EMPTY_LIST
expr_stmt|;
return|return;
block|}
name|String
index|[]
name|transArr
init|=
name|transClasses
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|transformers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Transformer
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|add
parameter_list|(
name|Transformer
name|transformer
parameter_list|)
block|{
if|if
condition|(
name|docBuilder
operator|!=
literal|null
operator|&&
name|docBuilder
operator|.
name|verboseDebug
condition|)
block|{
name|transformer
operator|=
name|docBuilder
operator|.
name|writer
operator|.
name|getDebugLogger
argument_list|()
operator|.
name|wrapTransformer
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|add
argument_list|(
name|transformer
argument_list|)
return|;
block|}
block|}
expr_stmt|;
for|for
control|(
name|String
name|aTransArr
range|:
name|transArr
control|)
block|{
name|String
name|trans
init|=
name|aTransArr
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|trans
operator|.
name|startsWith
argument_list|(
literal|"script:"
argument_list|)
condition|)
block|{
name|String
name|functionName
init|=
name|trans
operator|.
name|substring
argument_list|(
literal|"script:"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|ScriptTransformer
name|scriptTransformer
init|=
operator|new
name|ScriptTransformer
argument_list|()
decl_stmt|;
name|scriptTransformer
operator|.
name|setFunctionName
argument_list|(
name|functionName
argument_list|)
expr_stmt|;
name|transformers
operator|.
name|add
argument_list|(
name|scriptTransformer
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|Class
name|clazz
init|=
name|DocBuilder
operator|.
name|loadClass
argument_list|(
name|trans
argument_list|,
name|context
operator|.
name|getSolrCore
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Transformer
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|transformers
operator|.
name|add
argument_list|(
operator|(
name|Transformer
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Method
name|meth
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
name|TRANSFORM_ROW
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|transformers
operator|.
name|add
argument_list|(
operator|new
name|ReflectionTransformer
argument_list|(
name|meth
argument_list|,
name|clazz
argument_list|,
name|trans
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Transformer :"
operator|+
name|trans
operator|+
literal|"does not implement Transformer interface or does not have a transformRow(Map<String.Object> m)method"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|nsme
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to load Transformer: "
operator|+
name|aTransArr
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to load Transformer: "
operator|+
name|trans
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|ReflectionTransformer
specifier|static
class|class
name|ReflectionTransformer
extends|extends
name|Transformer
block|{
DECL|field|meth
specifier|final
name|Method
name|meth
decl_stmt|;
DECL|field|clazz
specifier|final
name|Class
name|clazz
decl_stmt|;
DECL|field|trans
specifier|final
name|String
name|trans
decl_stmt|;
DECL|field|o
specifier|final
name|Object
name|o
decl_stmt|;
DECL|method|ReflectionTransformer
specifier|public
name|ReflectionTransformer
parameter_list|(
name|Method
name|meth
parameter_list|,
name|Class
name|clazz
parameter_list|,
name|String
name|trans
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|meth
operator|=
name|meth
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|trans
operator|=
name|trans
expr_stmt|;
name|o
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
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
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
try|try
block|{
return|return
name|meth
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
name|aRow
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"method invocation failed on transformer : "
operator|+
name|trans
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|WARN
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getFromRowCache
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFromRowCache
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|rowcache
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowcache
operator|.
name|isEmpty
argument_list|()
condition|)
name|rowcache
operator|=
literal|null
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applyTransformer
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|applyTransformer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|)
block|{
if|if
condition|(
name|row
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|transformers
operator|==
literal|null
condition|)
name|loadTransformers
argument_list|()
expr_stmt|;
if|if
condition|(
name|transformers
operator|==
name|Collections
operator|.
name|EMPTY_LIST
condition|)
return|return
name|row
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transformedRow
init|=
name|row
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
literal|null
decl_stmt|;
name|boolean
name|stopTransform
init|=
name|checkStopTransform
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|resolver
init|=
operator|(
name|VariableResolverImpl
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
decl_stmt|;
for|for
control|(
name|Transformer
name|t
range|:
name|transformers
control|)
block|{
if|if
condition|(
name|stopTransform
condition|)
break|break;
try|try
block|{
if|if
condition|(
name|rows
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|tmpRows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|rows
control|)
block|{
name|resolver
operator|.
name|addNamespace
argument_list|(
name|entityName
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|t
operator|.
name|transformRow
argument_list|(
name|map
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|oMap
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|stopTransform
operator|=
name|checkStopTransform
argument_list|(
name|oMap
argument_list|)
expr_stmt|;
name|tmpRows
operator|.
name|add
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|tmpRows
operator|.
name|addAll
argument_list|(
operator|(
name|List
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Transformer must return Map<String, Object> or a List<Map<String, Object>>"
argument_list|)
expr_stmt|;
block|}
block|}
name|rows
operator|=
name|tmpRows
expr_stmt|;
block|}
else|else
block|{
name|resolver
operator|.
name|addNamespace
argument_list|(
name|entityName
argument_list|,
name|transformedRow
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|t
operator|.
name|transformRow
argument_list|(
name|transformedRow
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|oMap
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|stopTransform
operator|=
name|checkStopTransform
argument_list|(
name|oMap
argument_list|)
expr_stmt|;
name|transformedRow
operator|=
operator|(
name|Map
operator|)
name|o
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|rows
operator|=
operator|(
name|List
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Transformer must return Map<String, Object> or a List<Map<String, Object>>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"transformer threw error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SKIP
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|DataImportHandlerException
operator|.
name|SKIP
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// onError = continue
block|}
block|}
if|if
condition|(
name|rows
operator|==
literal|null
condition|)
block|{
return|return
name|transformedRow
return|;
block|}
else|else
block|{
name|rowcache
operator|=
name|rows
expr_stmt|;
return|return
name|getFromRowCache
argument_list|()
return|;
block|}
block|}
DECL|method|checkStopTransform
specifier|private
name|boolean
name|checkStopTransform
parameter_list|(
name|Map
name|oMap
parameter_list|)
block|{
return|return
name|oMap
operator|.
name|get
argument_list|(
literal|"$stopTransform"
argument_list|)
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|oMap
operator|.
name|get
argument_list|(
literal|"$stopTransform"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|rowcache
operator|!=
literal|null
condition|)
block|{
return|return
name|getFromRowCache
argument_list|()
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|arow
init|=
literal|null
decl_stmt|;
try|try
block|{
name|arow
operator|=
name|delegate
operator|.
name|nextRow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//SKIP is not really possible. If this calls the nextRow() again the Entityprocessor would be in an inconisttent state
name|log
operator|.
name|error
argument_list|(
literal|"Exception in entity : "
operator|+
name|entityName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|arow
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|arow
operator|=
name|applyTransformer
argument_list|(
name|arow
argument_list|)
expr_stmt|;
if|if
condition|(
name|arow
operator|!=
literal|null
condition|)
block|{
name|delegate
operator|.
name|postTransform
argument_list|(
name|arow
argument_list|)
expr_stmt|;
return|return
name|arow
return|;
block|}
block|}
block|}
block|}
DECL|method|nextModifiedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedRowKey
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|delegate
operator|.
name|nextModifiedRowKey
argument_list|()
decl_stmt|;
name|row
operator|=
name|applyTransformer
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|rowcache
operator|=
literal|null
expr_stmt|;
return|return
name|row
return|;
block|}
DECL|method|nextDeletedRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextDeletedRowKey
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
name|delegate
operator|.
name|nextDeletedRowKey
argument_list|()
decl_stmt|;
name|row
operator|=
name|applyTransformer
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|rowcache
operator|=
literal|null
expr_stmt|;
return|return
name|row
return|;
block|}
DECL|method|nextModifiedParentRowKey
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextModifiedParentRowKey
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|nextModifiedParentRowKey
argument_list|()
return|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|delegate
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|resolver
operator|.
name|context
operator|=
name|contextCopy
expr_stmt|;
name|contextCopy
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getVariableResolver
specifier|public
name|VariableResolverImpl
name|getVariableResolver
parameter_list|()
block|{
return|return
operator|(
name|VariableResolverImpl
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
return|;
block|}
DECL|method|getContext
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

