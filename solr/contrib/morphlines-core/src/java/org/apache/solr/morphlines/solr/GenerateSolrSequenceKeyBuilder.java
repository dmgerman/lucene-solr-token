begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Random
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|CommandBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|MorphlineRuntimeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|Record
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|AbstractCommand
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Fields
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|Notifications
import|;
end_import

begin_import
import|import
name|com
operator|.
name|typesafe
operator|.
name|config
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * A command that assigns a record unique key that is the concatenation of the given  *<code>baseIdField</code> record field, followed by a running count of the record number within  * the current session. The count is reset to zero whenever a "startSession" notification is  * received.  *<p>  * For example, assume a CSV file containing multiple records but no unique ids, and the  *<code>baseIdField</code> field is the filesystem path of the file. Now this command can be used  * to assign the following record values to Solr's unique key field:  *<code>$path#0, $path#1, ... $path#N</code>.  *<p>  * The name of the unique key field is fetched from Solr's schema.xml file, as directed by the  *<code>solrLocator</code> configuration parameter.  */
end_comment

begin_class
DECL|class|GenerateSolrSequenceKeyBuilder
specifier|public
specifier|final
class|class
name|GenerateSolrSequenceKeyBuilder
implements|implements
name|CommandBuilder
block|{
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
literal|"generateSolrSequenceKey"
argument_list|,
literal|"sanitizeUniqueSolrKey"
comment|// old name (retained for backwards compatibility)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Command
name|build
parameter_list|(
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|GenerateSolrSequenceKey
argument_list|(
name|this
argument_list|,
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|GenerateSolrSequenceKey
specifier|private
specifier|static
specifier|final
class|class
name|GenerateSolrSequenceKey
extends|extends
name|AbstractCommand
block|{
DECL|field|preserveExisting
specifier|private
specifier|final
name|boolean
name|preserveExisting
decl_stmt|;
DECL|field|baseIdFieldName
specifier|private
specifier|final
name|String
name|baseIdFieldName
decl_stmt|;
DECL|field|uniqueKeyName
specifier|private
specifier|final
name|String
name|uniqueKeyName
decl_stmt|;
DECL|field|recordCounter
specifier|private
name|long
name|recordCounter
init|=
literal|0
decl_stmt|;
DECL|field|idPrefix
specifier|private
specifier|final
name|String
name|idPrefix
decl_stmt|;
comment|// for load testing only; enables adding same document many times with a different unique key
DECL|field|randomIdPrefix
specifier|private
specifier|final
name|Random
name|randomIdPrefix
decl_stmt|;
comment|// for load testing only; enables adding same document many times with a different unique key
DECL|method|GenerateSolrSequenceKey
specifier|public
name|GenerateSolrSequenceKey
parameter_list|(
name|CommandBuilder
name|builder
parameter_list|,
name|Config
name|config
parameter_list|,
name|Command
name|parent
parameter_list|,
name|Command
name|child
parameter_list|,
name|MorphlineContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|,
name|config
argument_list|,
name|parent
argument_list|,
name|child
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseIdFieldName
operator|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"baseIdField"
argument_list|,
name|Fields
operator|.
name|BASE_ID
argument_list|)
expr_stmt|;
name|this
operator|.
name|preserveExisting
operator|=
name|getConfigs
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|config
argument_list|,
literal|"preserveExisting"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Config
name|solrLocatorConfig
init|=
name|getConfigs
argument_list|()
operator|.
name|getConfig
argument_list|(
name|config
argument_list|,
literal|"solrLocator"
argument_list|)
decl_stmt|;
name|SolrLocator
name|locator
init|=
operator|new
name|SolrLocator
argument_list|(
name|solrLocatorConfig
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"solrLocator: {}"
argument_list|,
name|locator
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|locator
operator|.
name|getIndexSchema
argument_list|()
decl_stmt|;
name|SchemaField
name|uniqueKey
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|uniqueKeyName
operator|=
name|uniqueKey
operator|==
literal|null
condition|?
literal|null
else|:
name|uniqueKey
operator|.
name|getName
argument_list|()
expr_stmt|;
name|String
name|tmpIdPrefix
init|=
name|getConfigs
argument_list|()
operator|.
name|getString
argument_list|(
name|config
argument_list|,
literal|"idPrefix"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// for load testing only
name|Random
name|tmpRandomIdPrefx
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"random"
operator|.
name|equals
argument_list|(
name|tmpIdPrefix
argument_list|)
condition|)
block|{
comment|// for load testing only
name|tmpRandomIdPrefx
operator|=
operator|new
name|Random
argument_list|(
operator|new
name|SecureRandom
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|tmpIdPrefix
operator|=
literal|null
expr_stmt|;
block|}
name|idPrefix
operator|=
name|tmpIdPrefix
expr_stmt|;
name|randomIdPrefix
operator|=
name|tmpRandomIdPrefx
expr_stmt|;
name|validateArguments
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doProcess
specifier|protected
name|boolean
name|doProcess
parameter_list|(
name|Record
name|doc
parameter_list|)
block|{
name|long
name|num
init|=
name|recordCounter
operator|++
decl_stmt|;
comment|// LOG.debug("record #{} id before sanitizing doc: {}", num, doc);
if|if
condition|(
name|uniqueKeyName
operator|==
literal|null
operator|||
operator|(
name|preserveExisting
operator|&&
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|uniqueKeyName
argument_list|)
operator|)
condition|)
block|{
empty_stmt|;
comment|// we must preserve the existing id
block|}
else|else
block|{
name|Object
name|baseId
init|=
name|doc
operator|.
name|getFirstValue
argument_list|(
name|baseIdFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MorphlineRuntimeException
argument_list|(
literal|"Record field "
operator|+
name|baseIdFieldName
operator|+
literal|" must not be null as it is needed as a basis for a unique key for solr doc: "
operator|+
name|doc
argument_list|)
throw|;
block|}
name|doc
operator|.
name|replaceValues
argument_list|(
name|uniqueKeyName
argument_list|,
name|baseId
operator|.
name|toString
argument_list|()
operator|+
literal|"#"
operator|+
name|num
argument_list|)
expr_stmt|;
block|}
comment|// for load testing only; enables adding same document many times with a different unique key
if|if
condition|(
name|idPrefix
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|doc
operator|.
name|getFirstValue
argument_list|(
name|uniqueKeyName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|id
operator|=
name|idPrefix
operator|+
name|id
expr_stmt|;
name|doc
operator|.
name|replaceValues
argument_list|(
name|uniqueKeyName
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|randomIdPrefix
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|doc
operator|.
name|getFirstValue
argument_list|(
name|uniqueKeyName
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|id
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|randomIdPrefix
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
operator|+
literal|"#"
operator|+
name|id
expr_stmt|;
name|doc
operator|.
name|replaceValues
argument_list|(
name|uniqueKeyName
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"record #{} unique key sanitized to this: {}"
argument_list|,
name|num
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|doProcess
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doNotify
specifier|protected
name|void
name|doNotify
parameter_list|(
name|Record
name|notification
parameter_list|)
block|{
if|if
condition|(
name|Notifications
operator|.
name|containsLifecycleEvent
argument_list|(
name|notification
argument_list|,
name|Notifications
operator|.
name|LifecycleEvent
operator|.
name|START_SESSION
argument_list|)
condition|)
block|{
name|recordCounter
operator|=
literal|0
expr_stmt|;
comment|// reset
block|}
name|super
operator|.
name|doNotify
argument_list|(
name|notification
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

