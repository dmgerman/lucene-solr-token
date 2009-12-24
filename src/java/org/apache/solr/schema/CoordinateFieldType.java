begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|search
operator|.
name|QParser
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
name|search
operator|.
name|function
operator|.
name|ValueSource
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|MapSolrParams
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
name|List
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
name|HashMap
import|;
end_import

begin_comment
comment|/**  * A CoordinateFieldType is the base class for {@link org.apache.solr.schema.FieldType}s that have semantics  * related to items in a coordinate system.  *<br/>  * Implementations depend on a delegating work to a sub {@link org.apache.solr.schema.FieldType}, specified by  * either the {@link #SUB_FIELD_SUFFIX} or the {@link #SUB_FIELD_TYPE} (the latter is used if both are defined.  *<br/>  * Example:  *<pre>&lt;fieldType name="xy" class="solr.PointType" dimension="2" subFieldType="double"/&gt;  *</pre>  * In theory, classes deriving from this should be able to do things like represent a point, a polygon, a line, etc.  *<br/>  * NOTE: There can only be one sub Field Type.  *  */
end_comment

begin_class
DECL|class|CoordinateFieldType
specifier|public
specifier|abstract
class|class
name|CoordinateFieldType
extends|extends
name|FieldType
implements|implements
name|SchemaAware
block|{
comment|/**    * The dimension of the coordinate system    */
DECL|field|dimension
specifier|protected
name|int
name|dimension
decl_stmt|;
DECL|field|subType
specifier|protected
name|FieldType
name|subType
decl_stmt|;
DECL|field|SUB_FIELD_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|SUB_FIELD_SUFFIX
init|=
literal|"subFieldSuffix"
decl_stmt|;
DECL|field|SUB_FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|SUB_FIELD_TYPE
init|=
literal|"subFieldType"
decl_stmt|;
DECL|field|suffix
specifier|private
name|String
name|suffix
decl_stmt|;
comment|//need to keep this around between init and inform, since dynamic fields aren't created until before inform
DECL|field|dynFieldProps
specifier|protected
name|int
name|dynFieldProps
decl_stmt|;
DECL|method|getDimension
specifier|public
name|int
name|getDimension
parameter_list|()
block|{
return|return
name|dimension
return|;
block|}
DECL|method|getSubType
specifier|public
name|FieldType
name|getSubType
parameter_list|()
block|{
return|return
name|subType
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
comment|//it's not a first class citizen for the IndexSchema
name|SolrParams
name|p
init|=
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|String
name|subFT
init|=
name|p
operator|.
name|get
argument_list|(
name|SUB_FIELD_TYPE
argument_list|)
decl_stmt|;
name|String
name|subSuffix
init|=
name|p
operator|.
name|get
argument_list|(
name|SUB_FIELD_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|subFT
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
name|SUB_FIELD_TYPE
argument_list|)
expr_stmt|;
name|subType
operator|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|subFT
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subSuffix
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
name|SUB_FIELD_SUFFIX
argument_list|)
expr_stmt|;
name|suffix
operator|=
name|subSuffix
expr_stmt|;
block|}
else|else
block|{
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
literal|"The field type: "
operator|+
name|typeName
operator|+
literal|" must specify the "
operator|+
name|SUB_FIELD_TYPE
operator|+
literal|" attribute or the "
operator|+
name|SUB_FIELD_SUFFIX
operator|+
literal|" attribute."
argument_list|)
throw|;
block|}
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
comment|//Can't do this until here b/c the Dynamic Fields are not initialized until here.
if|if
condition|(
name|suffix
operator|!=
literal|null
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|suffix
argument_list|)
decl_stmt|;
name|subType
operator|=
name|sf
operator|.
name|getType
argument_list|()
expr_stmt|;
comment|//this means it is already registered
name|dynFieldProps
operator|=
name|sf
operator|.
name|getProperties
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subType
operator|!=
literal|null
condition|)
block|{
name|SchemaField
name|proto
init|=
name|registerPolyFieldDynamicPrototype
argument_list|(
name|schema
argument_list|,
name|subType
argument_list|)
decl_stmt|;
name|dynFieldProps
operator|=
name|proto
operator|.
name|getProperties
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
literal|"The field type: "
operator|+
name|typeName
operator|+
literal|" must specify the "
operator|+
name|SUB_FIELD_TYPE
operator|+
literal|" attribute or the "
operator|+
name|SUB_FIELD_SUFFIX
operator|+
literal|" attribute."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper method for creating a dynamic field SchemaField prototype.  Returns a {@link org.apache.solr.schema.SchemaField} with    * the {@link org.apache.solr.schema.FieldType} given and a name of "*" + {@link org.apache.solr.schema.FieldType#POLY_FIELD_SEPARATOR} + {@link org.apache.solr.schema.FieldType#typeName}    * and props of indexed=true, stored=false.    * @param schema the IndexSchema    * @param type The {@link org.apache.solr.schema.FieldType} of the prototype.    * @return The {@link org.apache.solr.schema.SchemaField}    */
DECL|method|registerPolyFieldDynamicPrototype
specifier|static
name|SchemaField
name|registerPolyFieldDynamicPrototype
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|String
name|name
init|=
literal|"*"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
name|type
operator|.
name|typeName
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//Just set these, delegate everything else to the field type
name|props
operator|.
name|put
argument_list|(
literal|"indexed"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"stored"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|int
name|p
init|=
name|SchemaField
operator|.
name|calcProps
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|SchemaField
name|proto
init|=
name|SchemaField
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|registerDynamicField
argument_list|(
name|proto
argument_list|)
expr_stmt|;
return|return
name|proto
return|;
block|}
comment|/**    * Throws UnsupportedOperationException()    */
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

