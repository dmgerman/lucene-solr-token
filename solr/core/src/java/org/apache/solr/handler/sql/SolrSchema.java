begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|sql
package|;
end_package

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
name|util
operator|.
name|EnumSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelProtoDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeFactoryImpl
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|LukeRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|LukeResponse
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
name|luke
operator|.
name|FieldFlag
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|SolrSchema
class|class
name|SolrSchema
extends|extends
name|AbstractSchema
block|{
DECL|field|cloudSolrClient
specifier|final
name|CloudSolrClient
name|cloudSolrClient
decl_stmt|;
DECL|method|SolrSchema
name|SolrSchema
parameter_list|(
name|String
name|zk
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|cloudSolrClient
operator|=
operator|new
name|CloudSolrClient
argument_list|(
name|zk
argument_list|)
expr_stmt|;
name|this
operator|.
name|cloudSolrClient
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
name|this
operator|.
name|cloudSolrClient
operator|.
name|connect
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|collections
init|=
name|this
operator|.
name|cloudSolrClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollections
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|collection
range|:
name|collections
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|collection
argument_list|,
operator|new
name|SolrTable
argument_list|(
name|this
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFieldInfo
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|LukeResponse
operator|.
name|FieldInfo
argument_list|>
name|getFieldInfo
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|LukeRequest
name|lukeRequest
init|=
operator|new
name|LukeRequest
argument_list|()
decl_stmt|;
name|lukeRequest
operator|.
name|setNumTerms
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LukeResponse
name|lukeResponse
decl_stmt|;
try|try
block|{
name|lukeResponse
operator|=
name|lukeRequest
operator|.
name|process
argument_list|(
name|cloudSolrClient
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|lukeResponse
operator|.
name|getFieldInfo
argument_list|()
return|;
block|}
DECL|method|getRelDataType
name|RelProtoDataType
name|getRelDataType
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
comment|// Temporary type factory, just for the duration of this method. Allowable
comment|// because we're creating a proto-type, not a type; before being used, the
comment|// proto-type will be copied into a real type factory.
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
name|fieldInfo
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LukeResponse
operator|.
name|FieldInfo
argument_list|>
name|luceneFieldInfoMap
init|=
name|getFieldInfo
argument_list|(
name|collection
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|LukeResponse
operator|.
name|FieldInfo
argument_list|>
name|entry
range|:
name|luceneFieldInfoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LukeResponse
operator|.
name|FieldInfo
name|luceneFieldInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|RelDataType
name|type
decl_stmt|;
switch|switch
condition|(
name|luceneFieldInfo
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
literal|"string"
case|:
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"int"
case|:
case|case
literal|"long"
case|:
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
default|default:
name|type
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|EnumSet
argument_list|<
name|FieldFlag
argument_list|>
name|flags
init|=
name|luceneFieldInfo
operator|.
name|getFlags
argument_list|()
decl_stmt|;
if|if
condition|(
name|flags
operator|!=
literal|null
operator|&&
name|flags
operator|.
name|contains
argument_list|(
name|FieldFlag
operator|.
name|MULTI_VALUED
argument_list|)
condition|)
block|{
name|type
operator|=
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|fieldInfo
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|type
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|fieldInfo
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

