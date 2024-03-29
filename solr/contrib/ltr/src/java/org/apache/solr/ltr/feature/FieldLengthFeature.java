begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.feature
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|feature
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
name|LinkedHashMap
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
name|index
operator|.
name|LeafReaderContext
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
name|NumericDocValues
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|util
operator|.
name|SmallFloat
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

begin_comment
comment|/**  * This feature returns the length of a field (in terms) for the current document.  * Example configuration:  *<pre>{   "name":  "titleLength",   "class": "org.apache.solr.ltr.feature.FieldLengthFeature",   "params": {       "field": "title"   } }</pre>  * Note: since this feature relies on norms values that are stored in a single byte  * the value of the feature could have a lightly different value.  * (see also {@link org.apache.lucene.search.similarities.ClassicSimilarity})  **/
end_comment

begin_class
DECL|class|FieldLengthFeature
specifier|public
class|class
name|FieldLengthFeature
extends|extends
name|Feature
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|paramsToMap
specifier|public
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|paramsToMap
parameter_list|()
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
literal|1
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"field"
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|protected
name|void
name|validate
parameter_list|()
throws|throws
name|FeatureException
block|{
if|if
condition|(
name|field
operator|==
literal|null
operator|||
name|field
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FeatureException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": field must be provided"
argument_list|)
throw|;
block|}
block|}
comment|/** Cache of decoded bytes. */
DECL|field|NORM_TABLE
specifier|private
specifier|static
specifier|final
name|float
index|[]
name|NORM_TABLE
init|=
operator|new
name|float
index|[
literal|256
index|]
decl_stmt|;
static|static
block|{
name|NORM_TABLE
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|NORM_TABLE
index|[
name|i
index|]
operator|=
name|SmallFloat
operator|.
name|byte4ToInt
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Decodes the norm value, assuming it is a single byte.    *    */
DECL|method|decodeNorm
specifier|private
specifier|final
name|float
name|decodeNorm
parameter_list|(
name|long
name|norm
parameter_list|)
block|{
return|return
name|NORM_TABLE
index|[
call|(
name|int
call|)
argument_list|(
name|norm
operator|&
literal|0xFF
argument_list|)
index|]
return|;
comment|//& 0xFF maps negative bytes to
comment|// positive above 127
block|}
DECL|method|FieldLengthFeature
specifier|public
name|FieldLengthFeature
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|FeatureWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|Query
name|originalQuery
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|efi
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldLengthFeatureWeight
argument_list|(
name|searcher
argument_list|,
name|request
argument_list|,
name|originalQuery
argument_list|,
name|efi
argument_list|)
return|;
block|}
DECL|class|FieldLengthFeatureWeight
specifier|public
class|class
name|FieldLengthFeatureWeight
extends|extends
name|FeatureWeight
block|{
DECL|method|FieldLengthFeatureWeight
specifier|public
name|FieldLengthFeatureWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|Query
name|originalQuery
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|efi
parameter_list|)
block|{
name|super
argument_list|(
name|FieldLengthFeature
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|request
argument_list|,
name|originalQuery
argument_list|,
name|efi
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|FeatureScorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|norms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|norms
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ValueFeatureScorer
argument_list|(
name|this
argument_list|,
literal|0f
argument_list|,
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|FieldLengthFeatureScorer
argument_list|(
name|this
argument_list|,
name|norms
argument_list|)
return|;
block|}
DECL|class|FieldLengthFeatureScorer
specifier|public
class|class
name|FieldLengthFeatureScorer
extends|extends
name|FeatureScorer
block|{
DECL|field|norms
name|NumericDocValues
name|norms
init|=
literal|null
decl_stmt|;
DECL|method|FieldLengthFeatureScorer
specifier|public
name|FieldLengthFeatureScorer
parameter_list|(
name|FeatureWeight
name|weight
parameter_list|,
name|NumericDocValues
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|norms
argument_list|)
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
comment|// In the constructor, docId is -1, so using 0 as default lookup
specifier|final
name|IndexableField
name|idxF
init|=
name|searcher
operator|.
name|doc
argument_list|(
literal|0
argument_list|)
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxF
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"FieldLengthFeatures can't be used if omitNorms is enabled (field="
operator|+
name|field
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|l
init|=
name|norms
operator|.
name|longValue
argument_list|()
decl_stmt|;
specifier|final
name|float
name|numTerms
init|=
name|decodeNorm
argument_list|(
name|l
argument_list|)
decl_stmt|;
return|return
name|numTerms
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

