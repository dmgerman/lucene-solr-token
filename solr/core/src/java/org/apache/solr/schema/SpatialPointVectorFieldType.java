begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|legacy
operator|.
name|LegacyFieldType
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
name|legacy
operator|.
name|PointVectorStrategy
import|;
end_import

begin_comment
comment|/**  * @see PointVectorStrategy  * @deprecated use {@link LatLonPointSpatialField} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SpatialPointVectorFieldType
specifier|public
class|class
name|SpatialPointVectorFieldType
extends|extends
name|AbstractSpatialFieldType
argument_list|<
name|PointVectorStrategy
argument_list|>
implements|implements
name|SchemaAware
block|{
DECL|field|numberFieldName
specifier|protected
name|String
name|numberFieldName
init|=
literal|"tdouble"
decl_stmt|;
comment|//in example schema defaults to non-zero precision step -- a good choice
DECL|field|precisionStep
specifier|private
name|int
name|precisionStep
decl_stmt|;
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
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"numberType"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|numberFieldName
operator|=
name|v
expr_stmt|;
block|}
block|}
comment|/**    * Adds X and Y fields to the given schema for each field with this class as its field type.    *     * {@inheritDoc}    *     * @param schema {@inheritDoc}    *    */
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|numberFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not find number field: "
operator|+
name|numberFieldName
argument_list|)
throw|;
block|}
comment|//TODO support other numeric types in the future
if|if
condition|(
operator|!
operator|(
name|fieldType
operator|instanceof
name|TrieDoubleField
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field type must be TrieDoubleField: "
operator|+
name|fieldType
argument_list|)
throw|;
block|}
name|precisionStep
operator|=
operator|(
operator|(
name|TrieField
operator|)
name|fieldType
operator|)
operator|.
name|getPrecisionStep
argument_list|()
expr_stmt|;
comment|//Just set these, delegate everything else to the field type
specifier|final
name|int
name|p
init|=
operator|(
name|INDEXED
operator||
name|TOKENIZED
operator||
name|OMIT_NORMS
operator||
name|OMIT_TF_POSITIONS
operator|)
decl_stmt|;
name|List
argument_list|<
name|SchemaField
argument_list|>
name|newFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchemaField
name|sf
range|:
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|==
name|this
condition|)
block|{
name|String
name|name
init|=
name|sf
operator|.
name|getName
argument_list|()
decl_stmt|;
name|newFields
operator|.
name|add
argument_list|(
operator|new
name|SchemaField
argument_list|(
name|name
operator|+
name|PointVectorStrategy
operator|.
name|SUFFIX_X
argument_list|,
name|fieldType
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|newFields
operator|.
name|add
argument_list|(
operator|new
name|SchemaField
argument_list|(
name|name
operator|+
name|PointVectorStrategy
operator|.
name|SUFFIX_Y
argument_list|,
name|fieldType
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|SchemaField
name|newField
range|:
name|newFields
control|)
block|{
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|newField
operator|.
name|getName
argument_list|()
argument_list|,
name|newField
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumberType
specifier|public
name|NumberType
name|getNumberType
parameter_list|()
block|{
return|return
name|NumberType
operator|.
name|DOUBLE
return|;
block|}
annotation|@
name|Override
DECL|method|newSpatialStrategy
specifier|protected
name|PointVectorStrategy
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|// TODO update to how BBoxField does things
if|if
condition|(
name|this
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// create strategy based on legacy numerics
comment|// todo remove in 7.0
name|LegacyFieldType
name|fieldType
init|=
operator|new
name|LegacyFieldType
argument_list|(
name|PointVectorStrategy
operator|.
name|LEGACY_FIELDTYPE
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|setNumericPrecisionStep
argument_list|(
name|precisionStep
argument_list|)
expr_stmt|;
return|return
operator|new
name|PointVectorStrategy
argument_list|(
name|ctx
argument_list|,
name|fieldName
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|PointVectorStrategy
operator|.
name|newInstance
argument_list|(
name|ctx
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

