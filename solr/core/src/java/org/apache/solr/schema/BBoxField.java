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
name|Arrays
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
name|lucene
operator|.
name|index
operator|.
name|DocValuesType
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
name|lucene
operator|.
name|queries
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
name|lucene
operator|.
name|spatial
operator|.
name|bbox
operator|.
name|BBoxOverlapRatioValueSource
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
name|spatial
operator|.
name|bbox
operator|.
name|BBoxStrategy
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|util
operator|.
name|ShapeAreaValueSource
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
name|search
operator|.
name|QParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_class
DECL|class|BBoxField
specifier|public
class|class
name|BBoxField
extends|extends
name|AbstractSpatialFieldType
argument_list|<
name|BBoxStrategy
argument_list|>
implements|implements
name|SchemaAware
block|{
DECL|field|PARAM_QUERY_TARGET_PROPORTION
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_QUERY_TARGET_PROPORTION
init|=
literal|"queryTargetProportion"
decl_stmt|;
DECL|field|PARAM_MIN_SIDE_LENGTH
specifier|private
specifier|static
specifier|final
name|String
name|PARAM_MIN_SIDE_LENGTH
init|=
literal|"minSideLength"
decl_stmt|;
comment|//score modes:
DECL|field|OVERLAP_RATIO
specifier|private
specifier|static
specifier|final
name|String
name|OVERLAP_RATIO
init|=
literal|"overlapRatio"
decl_stmt|;
DECL|field|AREA
specifier|private
specifier|static
specifier|final
name|String
name|AREA
init|=
literal|"area"
decl_stmt|;
DECL|field|AREA2D
specifier|private
specifier|static
specifier|final
name|String
name|AREA2D
init|=
literal|"area2D"
decl_stmt|;
DECL|field|numberTypeName
specifier|private
name|String
name|numberTypeName
decl_stmt|;
comment|//required
DECL|field|booleanTypeName
specifier|private
name|String
name|booleanTypeName
init|=
literal|"boolean"
decl_stmt|;
DECL|field|storeSubFields
specifier|private
name|boolean
name|storeSubFields
init|=
literal|false
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|BBoxField
specifier|public
name|BBoxField
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|OVERLAP_RATIO
argument_list|,
name|AREA
argument_list|,
name|AREA2D
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
operator|==
literal|null
condition|)
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
literal|" must specify the numberType attribute."
argument_list|)
throw|;
block|}
name|numberTypeName
operator|=
name|v
expr_stmt|;
name|v
operator|=
name|args
operator|.
name|remove
argument_list|(
literal|"booleanType"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|booleanTypeName
operator|=
name|v
expr_stmt|;
block|}
name|v
operator|=
name|args
operator|.
name|remove
argument_list|(
literal|"storeSubFields"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|storeSubFields
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
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
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|FieldType
name|numberType
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|numberTypeName
argument_list|)
decl_stmt|;
name|FieldType
name|booleanType
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|booleanTypeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|numberType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot find number fieldType: "
operator|+
name|numberTypeName
argument_list|)
throw|;
block|}
if|if
condition|(
name|booleanType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot find boolean fieldType: "
operator|+
name|booleanTypeName
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|booleanType
operator|instanceof
name|BoolField
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must be a BoolField: "
operator|+
name|booleanType
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|numberType
operator|instanceof
name|TrieDoubleField
operator|)
condition|)
block|{
comment|// TODO support TrieField (any trie) once BBoxStrategy does
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must be TrieDoubleField: "
operator|+
name|numberType
argument_list|)
throw|;
block|}
comment|//note: this only works for explicit fields, not dynamic fields
name|List
argument_list|<
name|SchemaField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
comment|//copy, because we modify during iteration
for|for
control|(
name|SchemaField
name|sf
range|:
name|fields
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
name|registerSubFields
argument_list|(
name|schema
argument_list|,
name|name
argument_list|,
name|numberType
argument_list|,
name|booleanType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|registerSubFields
specifier|private
name|void
name|registerSubFields
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|FieldType
name|numberType
parameter_list|,
name|FieldType
name|booleanType
parameter_list|)
block|{
name|register
argument_list|(
name|schema
argument_list|,
name|name
operator|+
name|BBoxStrategy
operator|.
name|SUFFIX_MINX
argument_list|,
name|numberType
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|schema
argument_list|,
name|name
operator|+
name|BBoxStrategy
operator|.
name|SUFFIX_MAXX
argument_list|,
name|numberType
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|schema
argument_list|,
name|name
operator|+
name|BBoxStrategy
operator|.
name|SUFFIX_MINY
argument_list|,
name|numberType
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|schema
argument_list|,
name|name
operator|+
name|BBoxStrategy
operator|.
name|SUFFIX_MAXY
argument_list|,
name|numberType
argument_list|)
expr_stmt|;
name|register
argument_list|(
name|schema
argument_list|,
name|name
operator|+
name|BBoxStrategy
operator|.
name|SUFFIX_XDL
argument_list|,
name|booleanType
argument_list|)
expr_stmt|;
block|}
comment|// note: Registering the field is probably optional; it makes it show up in the schema browser and may have other
comment|//  benefits.
DECL|method|register
specifier|private
name|void
name|register
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
name|int
name|props
init|=
name|fieldType
operator|.
name|properties
decl_stmt|;
if|if
condition|(
name|storeSubFields
condition|)
block|{
name|props
operator||=
name|STORED
expr_stmt|;
block|}
else|else
block|{
name|props
operator|&=
operator|~
name|STORED
expr_stmt|;
block|}
name|SchemaField
name|sf
init|=
operator|new
name|SchemaField
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|props
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|put
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|sf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSpatialStrategy
specifier|protected
name|BBoxStrategy
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
comment|//if it's a dynamic field, we register the sub-fields now.
name|FieldType
name|numberType
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|numberTypeName
argument_list|)
decl_stmt|;
name|FieldType
name|booleanType
init|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|booleanTypeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|.
name|isDynamicField
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|registerSubFields
argument_list|(
name|schema
argument_list|,
name|fieldName
argument_list|,
name|numberType
argument_list|,
name|booleanType
argument_list|)
expr_stmt|;
block|}
comment|//Solr's FieldType ought to expose Lucene FieldType. Instead as a hack we create a Field with a dummy value.
specifier|final
name|SchemaField
name|solrNumField
init|=
operator|new
name|SchemaField
argument_list|(
literal|"_"
argument_list|,
name|numberType
argument_list|)
decl_stmt|;
comment|//dummy temp
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldType
name|luceneType
init|=
operator|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldType
operator|)
name|solrNumField
operator|.
name|createField
argument_list|(
literal|0.0
argument_list|,
literal|1.0f
argument_list|)
operator|.
name|fieldType
argument_list|()
decl_stmt|;
name|luceneType
operator|.
name|setStored
argument_list|(
name|storeSubFields
argument_list|)
expr_stmt|;
comment|//and annoyingly this Field isn't going to have a docValues format because Solr uses a separate Field for that
if|if
condition|(
name|solrNumField
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
if|if
condition|(
name|luceneType
operator|instanceof
name|LegacyFieldType
condition|)
block|{
name|luceneType
operator|=
operator|new
name|LegacyFieldType
argument_list|(
operator|(
name|LegacyFieldType
operator|)
name|luceneType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|luceneType
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FieldType
argument_list|(
name|luceneType
argument_list|)
expr_stmt|;
block|}
name|luceneType
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BBoxStrategy
argument_list|(
name|ctx
argument_list|,
name|fieldName
argument_list|,
name|luceneType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSourceFromSpatialArgs
specifier|protected
name|ValueSource
name|getValueSourceFromSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|SpatialArgs
name|spatialArgs
parameter_list|,
name|String
name|scoreParam
parameter_list|,
name|BBoxStrategy
name|strategy
parameter_list|)
block|{
if|if
condition|(
name|scoreParam
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|scoreParam
condition|)
block|{
comment|//TODO move these to superclass after LUCENE-5804 ?
case|case
name|OVERLAP_RATIO
case|:
name|double
name|queryTargetProportion
init|=
literal|0.25
decl_stmt|;
comment|//Suggested default; weights towards target area
name|String
name|v
init|=
name|parser
operator|.
name|getParam
argument_list|(
name|PARAM_QUERY_TARGET_PROPORTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|queryTargetProportion
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|double
name|minSideLength
init|=
literal|0.0
decl_stmt|;
name|v
operator|=
name|parser
operator|.
name|getParam
argument_list|(
name|PARAM_MIN_SIDE_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|minSideLength
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|v
argument_list|)
expr_stmt|;
return|return
operator|new
name|BBoxOverlapRatioValueSource
argument_list|(
name|strategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|ctx
operator|.
name|isGeo
argument_list|()
argument_list|,
operator|(
name|Rectangle
operator|)
name|spatialArgs
operator|.
name|getShape
argument_list|()
argument_list|,
name|queryTargetProportion
argument_list|,
name|minSideLength
argument_list|)
return|;
case|case
name|AREA
case|:
return|return
operator|new
name|ShapeAreaValueSource
argument_list|(
name|strategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|ctx
argument_list|,
name|ctx
operator|.
name|isGeo
argument_list|()
argument_list|,
name|distanceUnits
operator|.
name|multiplierFromDegreesToThisUnit
argument_list|()
operator|*
name|distanceUnits
operator|.
name|multiplierFromDegreesToThisUnit
argument_list|()
argument_list|)
return|;
case|case
name|AREA2D
case|:
return|return
operator|new
name|ShapeAreaValueSource
argument_list|(
name|strategy
operator|.
name|makeShapeValueSource
argument_list|()
argument_list|,
name|ctx
argument_list|,
literal|false
argument_list|,
name|distanceUnits
operator|.
name|multiplierFromDegreesToThisUnit
argument_list|()
operator|*
name|distanceUnits
operator|.
name|multiplierFromDegreesToThisUnit
argument_list|()
argument_list|)
return|;
default|default:
return|return
name|super
operator|.
name|getValueSourceFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|spatialArgs
argument_list|,
name|scoreParam
argument_list|,
name|strategy
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

