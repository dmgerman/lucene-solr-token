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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|Arrays
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
name|TreeSet
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
name|Callable
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
name|ExecutionException
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
name|IndexableField
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|lucene
operator|.
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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
name|SolrException
operator|.
name|ErrorCode
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
name|search
operator|.
name|SpatialOptions
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
name|DistanceUnits
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|SpatialUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
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
name|context
operator|.
name|SpatialContext
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
name|context
operator|.
name|SpatialContextFactory
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
name|distance
operator|.
name|DistanceUtils
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
name|io
operator|.
name|ShapeReader
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
name|io
operator|.
name|ShapeWriter
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
name|io
operator|.
name|SupportedFormats
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
name|Point
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
name|Shape
import|;
end_import

begin_comment
comment|/**  * Abstract base class for Solr FieldTypes based on a Lucene 4 {@link SpatialStrategy}.  *  * @lucene.experimental  */
end_comment

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
implements|implements
name|SpatialQueryable
block|{
comment|/** A local-param with one of "none" (default), "distance", "recipDistance" or supported values in ({@link DistanceUnits#getSupportedUnits()}. */
DECL|field|SCORE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_PARAM
init|=
literal|"score"
decl_stmt|;
comment|/** A local-param boolean that can be set to false to only return the    * FunctionQuery (score), and thus not do filtering.    */
DECL|field|FILTER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|FILTER_PARAM
init|=
literal|"filter"
decl_stmt|;
comment|//score param values:
DECL|field|DISTANCE
specifier|public
specifier|static
specifier|final
name|String
name|DISTANCE
init|=
literal|"distance"
decl_stmt|;
DECL|field|RECIP_DISTANCE
specifier|public
specifier|static
specifier|final
name|String
name|RECIP_DISTANCE
init|=
literal|"recipDistance"
decl_stmt|;
DECL|field|NONE
specifier|public
specifier|static
specifier|final
name|String
name|NONE
init|=
literal|"none"
decl_stmt|;
comment|/** Optional param to pick the string conversion */
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT
init|=
literal|"format"
decl_stmt|;
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
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
DECL|field|shapeWriter
specifier|protected
name|ShapeWriter
name|shapeWriter
decl_stmt|;
DECL|field|shapeReader
specifier|protected
name|ShapeReader
name|shapeReader
decl_stmt|;
DECL|field|fieldStrategyCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|fieldStrategyCache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|distanceUnits
specifier|protected
name|DistanceUnits
name|distanceUnits
decl_stmt|;
DECL|field|supportedScoreModes
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|supportedScoreModes
decl_stmt|;
DECL|method|AbstractSpatialFieldType
specifier|protected
name|AbstractSpatialFieldType
parameter_list|()
block|{
name|this
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractSpatialFieldType
specifier|protected
name|AbstractSpatialFieldType
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|moreScoreModes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|//sorted for consistent display order
name|set
operator|.
name|add
argument_list|(
name|NONE
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|DISTANCE
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|RECIP_DISTANCE
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|DistanceUnits
operator|.
name|getSupportedUnits
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|moreScoreModes
argument_list|)
expr_stmt|;
name|supportedScoreModes
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|set
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
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
comment|// subclass can set this directly
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
argument_list|<>
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
block|}
specifier|final
name|String
name|distanceUnitsStr
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"distanceUnits"
argument_list|)
decl_stmt|;
if|if
condition|(
name|distanceUnitsStr
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|distanceUnits
operator|=
name|ctx
operator|.
name|isGeo
argument_list|()
condition|?
name|DistanceUnits
operator|.
name|KILOMETERS
else|:
name|DistanceUnits
operator|.
name|DEGREES
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|distanceUnits
operator|=
name|parseDistanceUnits
argument_list|(
name|distanceUnitsStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|distanceUnits
operator|==
literal|null
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
literal|"Must specify distanceUnits as one of "
operator|+
name|DistanceUnits
operator|.
name|getSupportedUnits
argument_list|()
operator|+
literal|" on field types with class "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|SupportedFormats
name|fmts
init|=
name|ctx
operator|.
name|getFormats
argument_list|()
decl_stmt|;
specifier|final
name|String
name|format
init|=
name|args
operator|.
name|remove
argument_list|(
name|FORMAT
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|shapeWriter
operator|=
name|fmts
operator|.
name|getWriter
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|shapeReader
operator|=
name|fmts
operator|.
name|getReader
argument_list|(
name|format
argument_list|)
expr_stmt|;
if|if
condition|(
name|shapeWriter
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
literal|"Unknown Shape Format: "
operator|+
name|format
argument_list|)
throw|;
block|}
if|if
condition|(
name|shapeReader
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
literal|"Unknown Shape Format: "
operator|+
name|format
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// Otherwise, pick the first supported reader/writer
name|shapeWriter
operator|=
name|fmts
operator|.
name|getWriters
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|shapeReader
operator|=
name|fmts
operator|.
name|getReaders
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|argsParser
operator|=
name|newSpatialArgsParser
argument_list|()
expr_stmt|;
block|}
comment|/** if {@code str} is non-null, returns {@link org.apache.solr.util.DistanceUnits#valueOf(String)}    * (which will return null if not found),    * else returns {@link #distanceUnits} (only null before initialized in {@code init()}.    * @param str maybe null    * @return maybe null    */
DECL|method|parseDistanceUnits
specifier|public
name|DistanceUnits
name|parseDistanceUnits
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|distanceUnits
return|;
block|}
else|else
block|{
return|return
name|DistanceUnits
operator|.
name|valueOf
argument_list|(
name|str
argument_list|)
return|;
block|}
block|}
DECL|method|newSpatialArgsParser
specifier|protected
name|SpatialArgsParser
name|newSpatialArgsParser
parameter_list|()
block|{
return|return
operator|new
name|SpatialArgsParser
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|AbstractSpatialFieldType
operator|.
name|this
operator|.
name|parseShape
argument_list|(
name|str
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|//--------------------------------------------------------------
comment|// Indexing
comment|//--------------------------------------------------------------
annotation|@
name|Override
DECL|method|createField
specifier|public
specifier|final
name|Field
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
literal|"instead call createFields() because isPolyField() is true"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|IndexableField
argument_list|>
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
name|parseShape
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
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|IndexableField
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|result
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|getStoredValue
argument_list|(
name|shape
argument_list|,
name|shapeStr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** Called by {@link #createFields(SchemaField, Object, float)} to get the stored value. */
DECL|method|getStoredValue
specifier|protected
name|String
name|getStoredValue
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|String
name|shapeStr
parameter_list|)
block|{
return|return
operator|(
name|shapeStr
operator|==
literal|null
operator|)
condition|?
name|shapeToString
argument_list|(
name|shape
argument_list|)
else|:
name|shapeStr
return|;
block|}
comment|/** Create a {@link Shape} from the input string */
DECL|method|parseShape
specifier|public
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
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
name|BAD_REQUEST
argument_list|,
literal|"empty string shape"
argument_list|)
throw|;
name|Shape
name|shape
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|shapeReader
operator|!=
literal|null
condition|)
block|{
name|shape
operator|=
name|shapeReader
operator|.
name|readIfSupported
argument_list|(
name|str
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
comment|// Try all supported formats
name|shape
operator|=
name|ctx
operator|.
name|getFormats
argument_list|()
operator|.
name|read
argument_list|(
name|str
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
literal|"Unable to parse shape from: "
operator|+
name|str
argument_list|)
throw|;
block|}
return|return
name|shape
return|;
block|}
comment|/**    * Returns a String version of a shape to be used for the stored value.    *    * The format can be selected using the initParam<code>format={WKT|GeoJSON}</code>    */
DECL|method|shapeToString
specifier|public
name|String
name|shapeToString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|shapeWriter
operator|!=
literal|null
condition|)
block|{
return|return
name|shapeWriter
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
return|;
block|}
comment|// This will only happen if the context does not have any writers
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"ShapeWriter not configured"
argument_list|)
throw|;
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
comment|/**    * Implemented for compatibility with geofilt&amp; bbox query parsers:    * {@link SpatialQueryable}.    */
annotation|@
name|Override
DECL|method|createSpatialQuery
specifier|public
name|Query
name|createSpatialQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SpatialOptions
name|options
parameter_list|)
block|{
name|Point
name|pt
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|options
operator|.
name|pointStr
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|double
name|distDeg
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|options
operator|.
name|distance
argument_list|,
name|options
operator|.
name|radius
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|distDeg
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|bbox
condition|)
name|shape
operator|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
expr_stmt|;
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
name|shape
argument_list|)
decl_stmt|;
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|options
operator|.
name|field
argument_list|,
name|spatialArgs
argument_list|)
return|;
block|}
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
name|Point
name|p1
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|part1
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Point
name|p2
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|part2
argument_list|,
name|ctx
argument_list|)
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
comment|// the x& y pair of FieldCache value sources.
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
name|parseSpatialArgs
argument_list|(
name|parser
argument_list|,
name|externalVal
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseSpatialArgs
specifier|protected
name|SpatialArgs
name|parseSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
try|try
block|{
name|SpatialArgs
name|args
init|=
name|argsParser
operator|.
name|parse
argument_list|(
name|externalVal
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
comment|// Convert parsed args.distErr to degrees (using distanceUnits)
if|if
condition|(
name|args
operator|.
name|getDistErr
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|setDistErr
argument_list|(
name|args
operator|.
name|getDistErr
argument_list|()
operator|*
name|distanceUnits
operator|.
name|multiplierFromThisUnitToDegrees
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|args
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getQueryFromSpatialArgs
specifier|protected
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
comment|//See SOLR-2883 needScore
name|String
name|scoreParam
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
comment|//We get the valueSource for the score then the filter and combine them.
name|ValueSource
name|valueSource
init|=
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
decl_stmt|;
if|if
condition|(
name|valueSource
operator|==
literal|null
condition|)
block|{
return|return
name|strategy
operator|.
name|makeQuery
argument_list|(
name|spatialArgs
argument_list|)
return|;
comment|//assumed constant scoring
block|}
name|FunctionQuery
name|functionQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
operator|&&
operator|!
name|localParams
operator|.
name|getBool
argument_list|(
name|FILTER_PARAM
argument_list|,
literal|true
argument_list|)
condition|)
return|return
name|functionQuery
return|;
name|Query
name|filterQuery
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
name|spatialArgs
argument_list|)
decl_stmt|;
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
name|functionQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
comment|//matches everything and provides score
operator|.
name|add
argument_list|(
name|filterQuery
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
comment|//filters (score isn't used)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSphereRadius
specifier|public
name|double
name|getSphereRadius
parameter_list|()
block|{
return|return
name|distanceUnits
operator|.
name|getEarthRadius
argument_list|()
return|;
block|}
comment|/** The set of values supported for the score local-param. Not null. */
DECL|method|getSupportedScoreModes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedScoreModes
parameter_list|()
block|{
return|return
name|supportedScoreModes
return|;
block|}
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
name|score
parameter_list|,
name|T
name|strategy
parameter_list|)
block|{
if|if
condition|(
name|score
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|double
name|multiplier
decl_stmt|;
comment|// default multiplier for degrees
switch|switch
condition|(
name|score
condition|)
block|{
case|case
literal|""
case|:
case|case
name|NONE
case|:
return|return
literal|null
return|;
case|case
name|RECIP_DISTANCE
case|:
return|return
name|strategy
operator|.
name|makeRecipDistanceValueSource
argument_list|(
name|spatialArgs
operator|.
name|getShape
argument_list|()
argument_list|)
return|;
case|case
name|DISTANCE
case|:
name|multiplier
operator|=
name|distanceUnits
operator|.
name|multiplierFromDegreesToThisUnit
argument_list|()
expr_stmt|;
break|break;
default|default:
name|DistanceUnits
name|du
init|=
name|parseDistanceUnits
argument_list|(
name|score
argument_list|)
decl_stmt|;
if|if
condition|(
name|du
operator|!=
literal|null
condition|)
block|{
name|multiplier
operator|=
name|du
operator|.
name|multiplierFromDegreesToThisUnit
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
name|BAD_REQUEST
argument_list|,
literal|"'score' local-param must be one of "
operator|+
name|supportedScoreModes
operator|+
literal|", it was: "
operator|+
name|score
argument_list|)
throw|;
block|}
block|}
return|return
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
argument_list|,
name|multiplier
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
try|try
block|{
return|return
name|fieldStrategyCache
operator|.
name|get
argument_list|(
name|fieldName
argument_list|,
operator|new
name|Callable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|newSpatialStrategy
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return The Spatial Context for this field type    */
DECL|method|getSpatialContext
specifier|public
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|ctx
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
name|IndexableField
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
operator|+
literal|", instead try sorting by query."
argument_list|)
throw|;
block|}
DECL|method|getDistanceUnits
specifier|public
name|DistanceUnits
name|getDistanceUnits
parameter_list|()
block|{
return|return
name|this
operator|.
name|distanceUnits
return|;
block|}
block|}
end_class

end_unit

