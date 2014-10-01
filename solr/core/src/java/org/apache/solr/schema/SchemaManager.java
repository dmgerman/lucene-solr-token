begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|ZkSolrResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|BaseSolrResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Collection
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|EMPTY_LIST
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|EMPTY_MAP
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
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
name|schema
operator|.
name|FieldType
operator|.
name|CLASS_NAME
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
name|schema
operator|.
name|IndexSchema
operator|.
name|DESTINATION
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
name|schema
operator|.
name|IndexSchema
operator|.
name|NAME
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
name|schema
operator|.
name|IndexSchema
operator|.
name|SOURCE
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
name|schema
operator|.
name|IndexSchema
operator|.
name|TYPE
import|;
end_import

begin_comment
comment|/**A utility class to manipulate schema using the bulk mode.  * This class takes in all the commands and process them completely. It is an all or none  * operation  */
end_comment

begin_class
DECL|class|SchemaManager
specifier|public
class|class
name|SchemaManager
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
name|SchemaManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|req
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|managedIndexSchema
name|ManagedIndexSchema
name|managedIndexSchema
decl_stmt|;
DECL|field|ADD_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ADD_FIELD
init|=
literal|"add-field"
decl_stmt|;
DECL|field|ADD_COPY_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ADD_COPY_FIELD
init|=
literal|"add-copy-field"
decl_stmt|;
DECL|field|ADD_DYNAMIC_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|ADD_DYNAMIC_FIELD
init|=
literal|"add-dynamic-field"
decl_stmt|;
DECL|field|ADD_FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|ADD_FIELD_TYPE
init|=
literal|"add-field-type"
decl_stmt|;
DECL|field|KNOWN_OPS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|KNOWN_OPS
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|KNOWN_OPS
operator|.
name|add
argument_list|(
name|ADD_COPY_FIELD
argument_list|)
expr_stmt|;
name|KNOWN_OPS
operator|.
name|add
argument_list|(
name|ADD_FIELD
argument_list|)
expr_stmt|;
name|KNOWN_OPS
operator|.
name|add
argument_list|(
name|ADD_DYNAMIC_FIELD
argument_list|)
expr_stmt|;
name|KNOWN_OPS
operator|.
name|add
argument_list|(
name|ADD_FIELD_TYPE
argument_list|)
expr_stmt|;
block|}
DECL|method|SchemaManager
specifier|public
name|SchemaManager
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
block|}
comment|/**Take in a JSON command set and execute them . It tries to capture as many errors    * as possible instead of failing at the frst error it encounters    * @param rdr The input as a Reader    * @return Lis of errors . If the List is empty then the operation is successful.    */
DECL|method|performOperations
specifier|public
name|List
name|performOperations
parameter_list|(
name|Reader
name|rdr
parameter_list|)
block|{
name|List
argument_list|<
name|Operation
argument_list|>
name|ops
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ops
operator|=
name|SchemaManager
operator|.
name|parse
argument_list|(
name|rdr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error parsing schema operations "
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|singletonMap
argument_list|(
name|ERR_MSGS
argument_list|,
name|msg
operator|+
literal|":"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|List
name|errs
init|=
name|captureErrors
argument_list|(
name|ops
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|errs
return|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|schema
operator|instanceof
name|ManagedIndexSchema
operator|)
condition|)
block|{
return|return
name|singletonList
argument_list|(
name|singletonMap
argument_list|(
name|ERR_MSGS
argument_list|,
literal|"schema is not editable"
argument_list|)
argument_list|)
return|;
block|}
synchronized|synchronized
init|(
name|schema
operator|.
name|getSchemaUpdateLock
argument_list|()
init|)
block|{
return|return
name|doOperations
argument_list|(
name|ops
argument_list|)
return|;
block|}
block|}
DECL|method|doOperations
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|doOperations
parameter_list|(
name|List
argument_list|<
name|Operation
argument_list|>
name|operations
parameter_list|)
block|{
name|int
name|timeout
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
name|BaseSolrResource
operator|.
name|UPDATE_TIMEOUT_SECS
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|endTime
init|=
name|timeout
operator|>
literal|0
condition|?
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
operator|(
name|timeout
operator|*
literal|1000
operator|*
literal|1000
operator|)
else|:
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|endTime
condition|;
control|)
block|{
name|managedIndexSchema
operator|=
operator|(
name|ManagedIndexSchema
operator|)
name|core
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
for|for
control|(
name|Operation
name|op
range|:
name|operations
control|)
block|{
if|if
condition|(
name|ADD_FIELD
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
operator|||
name|ADD_DYNAMIC_FIELD
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
name|applyAddField
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ADD_COPY_FIELD
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
name|applyAddCopyField
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ADD_FIELD_TYPE
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
name|applyAddType
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|op
operator|.
name|addError
argument_list|(
literal|"No such operation : "
operator|+
name|op
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|List
name|errs
init|=
name|captureErrors
argument_list|(
name|operations
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|errs
return|;
try|try
block|{
name|managedIndexSchema
operator|.
name|persistManagedSchema
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|core
operator|.
name|setLatestSchema
argument_list|(
name|managedIndexSchema
argument_list|)
expr_stmt|;
name|waitForOtherReplicasToUpdate
argument_list|(
name|timeout
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
return|return
name|EMPTY_LIST
return|;
block|}
catch|catch
parameter_list|(
name|ManagedIndexSchema
operator|.
name|SchemaChangedInZkException
name|e
parameter_list|)
block|{
name|String
name|s
init|=
literal|"Failed to update schema because schema is modified"
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|s
init|=
literal|"Exception persisting schema"
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|singletonList
argument_list|(
name|s
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|singletonList
argument_list|(
literal|"Unable to persist schema"
argument_list|)
return|;
block|}
DECL|method|waitForOtherReplicasToUpdate
specifier|private
name|void
name|waitForOtherReplicasToUpdate
parameter_list|(
name|int
name|timeout
parameter_list|,
name|long
name|startTime
parameter_list|)
block|{
if|if
condition|(
name|timeout
operator|>
literal|0
operator|&&
name|managedIndexSchema
operator|.
name|getResourceLoader
argument_list|()
operator|instanceof
name|ZkSolrResourceLoader
condition|)
block|{
name|CoreDescriptor
name|cd
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|String
name|collection
init|=
name|cd
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|ZkSolrResourceLoader
name|zkLoader
init|=
operator|(
name|ZkSolrResourceLoader
operator|)
name|managedIndexSchema
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|long
name|timeLeftSecs
init|=
name|timeout
operator|-
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeLeftSecs
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Not enough time left to update replicas. However the schema is updated already"
argument_list|)
throw|;
name|ManagedIndexSchema
operator|.
name|waitForSchemaZkVersionAgreement
argument_list|(
name|collection
argument_list|,
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|,
operator|(
name|managedIndexSchema
operator|)
operator|.
name|getSchemaZkVersion
argument_list|()
argument_list|,
name|zkLoader
operator|.
name|getZkController
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|timeLeftSecs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|applyAddType
specifier|private
name|boolean
name|applyAddType
parameter_list|(
name|Operation
name|op
parameter_list|)
block|{
name|String
name|name
init|=
name|op
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|String
name|clz
init|=
name|op
operator|.
name|getStr
argument_list|(
name|CLASS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
literal|false
return|;
try|try
block|{
name|FieldType
name|fieldType
init|=
name|managedIndexSchema
operator|.
name|newFieldType
argument_list|(
name|name
argument_list|,
name|clz
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|op
operator|.
name|commandData
argument_list|)
decl_stmt|;
name|managedIndexSchema
operator|=
name|managedIndexSchema
operator|.
name|addFieldTypes
argument_list|(
name|singletonList
argument_list|(
name|fieldType
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|getErrorStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|getErrorStr
specifier|private
name|String
name|getErrorStr
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Throwable
name|cause
init|=
name|e
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|cause
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cause
operator|.
name|getCause
argument_list|()
operator|==
literal|null
operator|||
name|cause
operator|.
name|getCause
argument_list|()
operator|==
name|cause
condition|)
break|break;
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|applyAddCopyField
specifier|private
name|boolean
name|applyAddCopyField
parameter_list|(
name|Operation
name|op
parameter_list|)
block|{
name|String
name|src
init|=
name|op
operator|.
name|getStr
argument_list|(
name|SOURCE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dest
init|=
name|op
operator|.
name|getStrs
argument_list|(
name|DESTINATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
literal|false
return|;
try|try
block|{
name|managedIndexSchema
operator|=
name|managedIndexSchema
operator|.
name|addCopyFields
argument_list|(
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
operator|>
name|singletonMap
argument_list|(
name|src
argument_list|,
name|dest
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|getErrorStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|applyAddField
specifier|private
name|boolean
name|applyAddField
parameter_list|(
name|Operation
name|op
parameter_list|)
block|{
name|String
name|name
init|=
name|op
operator|.
name|getStr
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|op
operator|.
name|getStr
argument_list|(
name|TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldType
name|ft
init|=
name|managedIndexSchema
operator|.
name|getFieldTypeByName
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|op
operator|.
name|addError
argument_list|(
literal|"No such field type '"
operator|+
name|type
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
try|try
block|{
if|if
condition|(
name|ADD_DYNAMIC_FIELD
operator|.
name|equals
argument_list|(
name|op
operator|.
name|name
argument_list|)
condition|)
block|{
name|managedIndexSchema
operator|=
name|managedIndexSchema
operator|.
name|addDynamicFields
argument_list|(
name|singletonList
argument_list|(
name|SchemaField
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|op
operator|.
name|getValuesExcluding
argument_list|(
name|NAME
argument_list|,
name|TYPE
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|EMPTY_MAP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|managedIndexSchema
operator|=
name|managedIndexSchema
operator|.
name|addFields
argument_list|(
name|singletonList
argument_list|(
name|SchemaField
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|op
operator|.
name|getValuesExcluding
argument_list|(
name|NAME
argument_list|,
name|TYPE
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|EMPTY_MAP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|op
operator|.
name|addError
argument_list|(
name|getErrorStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|class|Operation
specifier|public
specifier|static
class|class
name|Operation
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|commandData
specifier|private
name|Object
name|commandData
decl_stmt|;
comment|//this is most often a map
DECL|field|errors
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Operation
name|Operation
parameter_list|(
name|String
name|operationName
parameter_list|,
name|Object
name|metaData
parameter_list|)
block|{
name|commandData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|operationName
expr_stmt|;
if|if
condition|(
operator|!
name|KNOWN_OPS
operator|.
name|contains
argument_list|(
name|this
operator|.
name|name
argument_list|)
condition|)
name|errors
operator|.
name|add
argument_list|(
literal|"Unknown Operation :"
operator|+
name|this
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|s
init|=
operator|(
name|String
operator|)
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|def
else|:
name|s
return|;
block|}
DECL|method|getMapVal
specifier|private
name|Object
name|getMapVal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|metaData
init|=
operator|(
name|Map
operator|)
name|commandData
decl_stmt|;
return|return
name|metaData
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|" value has to be an object for operation :"
operator|+
name|name
decl_stmt|;
if|if
condition|(
operator|!
name|errors
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
name|errors
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|val
init|=
name|getStrs
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
literal|"'"
operator|+
name|key
operator|+
literal|"' is a required field"
argument_list|)
expr_stmt|;
return|return
name|val
return|;
block|}
comment|/**Get collection of values for a key. If only one val is present a      * single value collection is returned      */
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|def
parameter_list|)
block|{
name|Object
name|v
init|=
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|def
return|;
block|}
else|else
block|{
if|if
condition|(
name|v
operator|instanceof
name|List
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|List
operator|)
name|v
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|def
return|;
return|return
name|l
return|;
block|}
else|else
block|{
return|return
name|singletonList
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**Get a required field. If missing it adds to the errors      */
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|s
init|=
name|getStr
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
literal|"'"
operator|+
name|key
operator|+
literal|"' is a required field"
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|errorDetails
specifier|private
name|Map
name|errorDetails
parameter_list|()
block|{
return|return
name|makeMap
argument_list|(
name|name
argument_list|,
name|commandData
argument_list|,
name|ERR_MSGS
argument_list|,
name|errors
argument_list|)
return|;
block|}
DECL|method|hasError
specifier|public
name|boolean
name|hasError
parameter_list|()
block|{
return|return
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**Get all the values from the metadata for the command      * without the specified keys      */
DECL|method|getValuesExcluding
specifier|public
name|Map
name|getValuesExcluding
parameter_list|(
name|String
modifier|...
name|keys
parameter_list|)
block|{
name|getMapVal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasError
argument_list|()
condition|)
return|return
name|emptyMap
argument_list|()
return|;
comment|//just to verify the type is Map
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cp
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|commandData
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
return|return
name|cp
return|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|cp
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|cp
return|;
block|}
DECL|method|getErrors
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
block|}
comment|/**Parse the command operations into command objects    */
DECL|method|parse
specifier|static
name|List
argument_list|<
name|Operation
argument_list|>
name|parse
parameter_list|(
name|Reader
name|rdr
parameter_list|)
throws|throws
name|IOException
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|rdr
argument_list|)
decl_stmt|;
name|ObjectBuilder
name|ob
init|=
operator|new
name|ObjectBuilder
argument_list|(
name|parser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|lastEvent
argument_list|()
operator|!=
name|JSONParser
operator|.
name|OBJECT_START
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The JSON must be an Object of the form {\"command\": {...},..."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Operation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
return|return
name|operations
return|;
name|Object
name|key
init|=
name|ob
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|Object
name|val
init|=
name|ob
operator|.
name|getVal
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|Operation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|Operation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|captureErrors
specifier|static
name|List
argument_list|<
name|Map
argument_list|>
name|captureErrors
parameter_list|(
name|List
argument_list|<
name|Operation
argument_list|>
name|ops
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchemaManager
operator|.
name|Operation
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|op
operator|.
name|errorDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|errors
return|;
block|}
DECL|field|ERR_MSGS
specifier|public
specifier|static
specifier|final
name|String
name|ERR_MSGS
init|=
literal|"errorMessages"
decl_stmt|;
block|}
end_class

end_unit

