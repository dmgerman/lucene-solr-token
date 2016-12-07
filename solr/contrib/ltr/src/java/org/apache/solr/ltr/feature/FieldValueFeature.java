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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|Document
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
name|schema
operator|.
name|BoolField
import|;
end_import

begin_comment
comment|/**  * This feature returns the value of a field in the current document  * Example configuration:  *<pre>{   "name":  "rawHits",   "class": "org.apache.solr.ltr.feature.FieldValueFeature",   "params": {       "field": "hits"   } }</pre>  */
end_comment

begin_class
DECL|class|FieldValueFeature
specifier|public
class|class
name|FieldValueFeature
extends|extends
name|Feature
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|fieldAsSet
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|fieldAsSet
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
name|fieldAsSet
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|field
argument_list|)
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
DECL|method|FieldValueFeature
specifier|public
name|FieldValueFeature
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
name|FieldValueFeatureWeight
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
DECL|class|FieldValueFeatureWeight
specifier|public
class|class
name|FieldValueFeatureWeight
extends|extends
name|FeatureWeight
block|{
DECL|method|FieldValueFeatureWeight
specifier|public
name|FieldValueFeatureWeight
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
name|FieldValueFeature
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
return|return
operator|new
name|FieldValueFeatureScorer
argument_list|(
name|this
argument_list|,
name|context
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
DECL|class|FieldValueFeatureScorer
specifier|public
class|class
name|FieldValueFeatureScorer
extends|extends
name|FeatureScorer
block|{
DECL|field|context
name|LeafReaderContext
name|context
init|=
literal|null
decl_stmt|;
DECL|method|FieldValueFeatureScorer
specifier|public
name|FieldValueFeatureScorer
parameter_list|(
name|FeatureWeight
name|weight
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|,
name|DocIdSetIterator
name|itr
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|itr
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
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
try|try
block|{
specifier|final
name|Document
name|document
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|document
argument_list|(
name|itr
operator|.
name|docID
argument_list|()
argument_list|,
name|fieldAsSet
argument_list|)
decl_stmt|;
specifier|final
name|IndexableField
name|indexableField
init|=
name|document
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexableField
operator|==
literal|null
condition|)
block|{
return|return
name|getDefaultValue
argument_list|()
return|;
block|}
specifier|final
name|Number
name|number
init|=
name|indexableField
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|number
operator|!=
literal|null
condition|)
block|{
return|return
name|number
operator|.
name|floatValue
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|String
name|string
init|=
name|indexableField
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// boolean values in the index are encoded with the
comment|// a single char contained in TRUE_TOKEN or FALSE_TOKEN
comment|// (see BoolField)
if|if
condition|(
name|string
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|BoolField
operator|.
name|TRUE_TOKEN
index|[
literal|0
index|]
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|string
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|BoolField
operator|.
name|FALSE_TOKEN
index|[
literal|0
index|]
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FeatureException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
literal|"Unable to extract feature for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|getDefaultValue
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

