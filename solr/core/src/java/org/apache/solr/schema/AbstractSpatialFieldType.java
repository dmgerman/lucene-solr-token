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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContextFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|StoredField
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
name|StorableField
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
name|FunctionQuery
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
name|search
operator|.
name|Filter
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
name|FilteredQuery
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
name|SortField
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
name|SpatialStrategy
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
name|query
operator|.
name|SpatialArgsParser
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
name|SpatialOperation
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
name|response
operator|.
name|TextResponseWriter
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
name|util
operator|.
name|MapListener
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
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
DECL|class|AbstractSpatialFieldType
specifier|public
specifier|abstract
class|class
name|AbstractSpatialFieldType
parameter_list|<
name|T
extends|extends
name|SpatialStrategy
parameter_list|>
extends|extends
name|FieldType
block|{
comment|/** A local-param with one of "none" (default), "distance", or "recipDistance". */
DECL|field|SCORE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_PARAM
init|=
literal|"score"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|argsParser
specifier|protected
name|SpatialArgsParser
name|argsParser
decl_stmt|;
DECL|field|fieldStrategyMap
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|fieldStrategyMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
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
comment|//Solr expects us to remove the parameters we've used.
name|MapListener
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|argsWrap
init|=
operator|new
name|MapListener
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|ctx
operator|=
name|SpatialContextFactory
operator|.
name|makeSpatialContext
argument_list|(
name|argsWrap
argument_list|,
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|argsWrap
operator|.
name|getSeenKeys
argument_list|()
argument_list|)
expr_stmt|;
name|argsParser
operator|=
operator|new
name|SpatialArgsParser
argument_list|()
expr_stmt|;
comment|//might make pluggable some day?
block|}
comment|//--------------------------------------------------------------
comment|// Indexing
comment|//--------------------------------------------------------------
annotation|@
name|Override
DECL|method|createField
specifier|public
specifier|final
name|StorableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"should be calling createFields because isPolyField() is true"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
specifier|final
name|StorableField
index|[]
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|String
name|shapeStr
init|=
literal|null
decl_stmt|;
name|Shape
name|shape
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Shape
condition|)
block|{
name|shape
operator|=
operator|(
operator|(
name|Shape
operator|)
name|val
operator|)
expr_stmt|;
block|}
else|else
block|{
name|shapeStr
operator|=
name|val
operator|.
name|toString
argument_list|()
expr_stmt|;
name|shape
operator|=
name|ctx
operator|.
name|readShape
argument_list|(
name|shapeStr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Field {}: null shape for input: {}"
argument_list|,
name|field
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Field
index|[]
name|indexableFields
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|T
name|strategy
init|=
name|getStrategy
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|indexableFields
operator|=
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
expr_stmt|;
block|}
name|StoredField
name|storedField
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
if|if
condition|(
name|shapeStr
operator|==
literal|null
condition|)
name|shapeStr
operator|=
name|shapeToString
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|storedField
operator|=
operator|new
name|StoredField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|shapeStr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexableFields
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|storedField
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|Field
index|[]
block|{
name|storedField
block|}
return|;
block|}
else|else
block|{
if|if
condition|(
name|storedField
operator|==
literal|null
condition|)
return|return
name|indexableFields
return|;
name|Field
index|[]
name|result
init|=
operator|new
name|Field
index|[
name|indexableFields
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|indexableFields
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|indexableFields
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
index|[
name|result
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|storedField
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|method|shapeToString
specifier|protected
name|String
name|shapeToString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
return|;
block|}
comment|/** Called from {@link #getStrategy(String)} upon first use by fieldName. } */
DECL|method|newSpatialStrategy
specifier|protected
specifier|abstract
name|T
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|isPolyField
specifier|public
specifier|final
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|//--------------------------------------------------------------
comment|// Query Support
comment|//--------------------------------------------------------------
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
if|if
condition|(
operator|!
name|minInclusive
operator|||
operator|!
name|maxInclusive
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Both sides of spatial range query must be inclusive: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|Shape
name|shape1
init|=
name|ctx
operator|.
name|readShape
argument_list|(
name|part1
argument_list|)
decl_stmt|;
name|Shape
name|shape2
init|=
name|ctx
operator|.
name|readShape
argument_list|(
name|part2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|shape1
operator|instanceof
name|Point
operator|)
operator|||
operator|!
operator|(
name|shape2
operator|instanceof
name|Point
operator|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Both sides of spatial range query must be points: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|Point
name|p1
init|=
operator|(
name|Point
operator|)
name|shape1
decl_stmt|;
name|Point
name|p2
init|=
operator|(
name|Point
operator|)
name|shape2
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
decl_stmt|;
name|SpatialArgs
name|spatialArgs
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|bbox
argument_list|)
decl_stmt|;
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|spatialArgs
argument_list|)
return|;
comment|//won't score by default
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
comment|//This is different from Solr 3 LatLonType's approach which uses the MultiValueSource concept to directly expose
comment|// the an x& y pair of FieldCache value sources.
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"A ValueSource isn't directly available from this field. Instead try a query using the distance as the score."
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|argsParser
operator|.
name|parse
argument_list|(
name|externalVal
argument_list|,
name|ctx
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getQueryFromSpatialArgs
specifier|private
name|Query
name|getQueryFromSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|SpatialArgs
name|spatialArgs
parameter_list|)
block|{
name|T
name|strategy
init|=
name|getStrategy
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|SolrParams
name|localParams
init|=
name|parser
operator|.
name|getLocalParams
argument_list|()
decl_stmt|;
name|String
name|score
init|=
operator|(
name|localParams
operator|==
literal|null
condition|?
literal|null
else|:
name|localParams
operator|.
name|get
argument_list|(
name|SCORE_PARAM
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|score
operator|==
literal|null
operator|||
literal|"none"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
block|{
comment|//FYI Solr FieldType doesn't have a getFilter(). We'll always grab
comment|// getQuery() but it's possible a strategy has a more efficient getFilter
comment|// that could be wrapped -- no way to know.
comment|//See SOLR-2883 needScore
return|return
name|strategy
operator|.
name|makeQuery
argument_list|(
name|spatialArgs
argument_list|)
return|;
comment|//ConstantScoreQuery
block|}
comment|//We get the valueSource for the score then the filter and combine them.
name|ValueSource
name|valueSource
decl_stmt|;
if|if
condition|(
literal|"distance"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
name|valueSource
operator|=
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|spatialArgs
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|"recipDistance"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
name|valueSource
operator|=
name|strategy
operator|.
name|makeRecipDistanceValueSource
argument_list|(
name|spatialArgs
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"'score' local-param must be one of 'none', 'distance', or 'recipDistance'"
argument_list|)
throw|;
name|Filter
name|filter
init|=
name|strategy
operator|.
name|makeFilter
argument_list|(
name|spatialArgs
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/**    * Gets the cached strategy for this field, creating it if necessary    * via {@link #newSpatialStrategy(String)}.    * @param fieldName Mandatory reference to the field name    * @return Non-null.    */
DECL|method|getStrategy
specifier|public
name|T
name|getStrategy
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
name|T
name|strategy
init|=
name|fieldStrategyMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|//double-checked locking idiom
if|if
condition|(
name|strategy
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|fieldStrategyMap
init|)
block|{
name|strategy
operator|=
name|fieldStrategyMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
if|if
condition|(
name|strategy
operator|==
literal|null
condition|)
block|{
name|strategy
operator|=
name|newSpatialStrategy
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|fieldStrategyMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|strategy
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Sorting not supported on SpatialField: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

