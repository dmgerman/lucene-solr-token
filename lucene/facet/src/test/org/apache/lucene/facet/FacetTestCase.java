begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Comparator
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
name|facet
operator|.
name|taxonomy
operator|.
name|CachedOrdinalsReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|DocValuesOrdinalsReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|FastTaxonomyFacetCounts
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
name|facet
operator|.
name|taxonomy
operator|.
name|OrdinalsReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyFacetCounts
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|BytesRef
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|FacetTestCase
specifier|public
specifier|abstract
class|class
name|FacetTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|method|getTaxonomyFacetCounts
specifier|public
name|Facets
name|getTaxonomyFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|c
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|,
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
return|;
block|}
DECL|method|getTaxonomyFacetCounts
specifier|public
name|Facets
name|getTaxonomyFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|c
parameter_list|,
name|String
name|indexFieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|Facets
name|facets
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|facets
operator|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|indexFieldName
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|OrdinalsReader
name|ordsReader
init|=
operator|new
name|DocValuesOrdinalsReader
argument_list|(
name|indexFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|ordsReader
operator|=
operator|new
name|CachedOrdinalsReader
argument_list|(
name|ordsReader
argument_list|)
expr_stmt|;
block|}
name|facets
operator|=
operator|new
name|TaxonomyFacetCounts
argument_list|(
name|ordsReader
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|facets
return|;
block|}
DECL|method|getRandomTokens
specifier|protected
name|String
index|[]
name|getRandomTokens
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[
name|count
index|]
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
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tokens
index|[
name|i
index|]
operator|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|//tokens[i] = _TestUtil.randomSimpleString(random(), 1, 10);
block|}
return|return
name|tokens
return|;
block|}
DECL|method|pickToken
specifier|protected
name|String
name|pickToken
parameter_list|(
name|String
index|[]
name|tokens
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
name|tokens
index|[
name|i
index|]
return|;
block|}
block|}
comment|// Move long tail onto first token:
return|return
name|tokens
index|[
literal|0
index|]
return|;
block|}
DECL|class|TestDoc
specifier|protected
specifier|static
class|class
name|TestDoc
block|{
DECL|field|content
specifier|public
name|String
name|content
decl_stmt|;
DECL|field|dims
specifier|public
name|String
index|[]
name|dims
decl_stmt|;
DECL|field|value
specifier|public
name|float
name|value
decl_stmt|;
block|}
DECL|method|getRandomDocs
specifier|protected
name|List
argument_list|<
name|TestDoc
argument_list|>
name|getRandomDocs
parameter_list|(
name|String
index|[]
name|tokens
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|numDims
parameter_list|)
block|{
name|List
argument_list|<
name|TestDoc
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TestDoc
name|doc
init|=
operator|new
name|TestDoc
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|content
operator|=
name|pickToken
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|doc
operator|.
name|dims
operator|=
operator|new
name|String
index|[
name|numDims
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDims
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|dims
index|[
name|j
index|]
operator|=
name|pickToken
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|<
literal|3
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  doc "
operator|+
name|i
operator|+
literal|": content="
operator|+
name|doc
operator|.
name|content
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDims
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|dims
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    dim["
operator|+
name|j
operator|+
literal|"]="
operator|+
name|doc
operator|.
name|dims
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|docs
return|;
block|}
DECL|method|sortTies
specifier|protected
name|void
name|sortTies
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
parameter_list|)
block|{
for|for
control|(
name|FacetResult
name|result
range|:
name|results
control|)
block|{
name|sortTies
argument_list|(
name|result
operator|.
name|labelValues
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sortTies
specifier|protected
name|void
name|sortTies
parameter_list|(
name|LabelAndValue
index|[]
name|labelValues
parameter_list|)
block|{
name|double
name|lastValue
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|numInRow
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<=
name|labelValues
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|i
operator|<
name|labelValues
operator|.
name|length
operator|&&
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|==
name|lastValue
condition|)
block|{
name|numInRow
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|numInRow
operator|>
literal|1
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|labelValues
argument_list|,
name|i
operator|-
name|numInRow
argument_list|,
name|i
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LabelAndValue
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LabelAndValue
name|a
parameter_list|,
name|LabelAndValue
name|b
parameter_list|)
block|{
assert|assert
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|==
name|b
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
assert|;
return|return
operator|new
name|BytesRef
argument_list|(
name|a
operator|.
name|label
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|b
operator|.
name|label
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|numInRow
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|labelValues
operator|.
name|length
condition|)
block|{
name|lastValue
operator|=
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
DECL|method|sortLabelValues
specifier|protected
name|void
name|sortLabelValues
parameter_list|(
name|List
argument_list|<
name|LabelAndValue
argument_list|>
name|labelValues
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|labelValues
argument_list|,
operator|new
name|Comparator
argument_list|<
name|LabelAndValue
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LabelAndValue
name|a
parameter_list|,
name|LabelAndValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|>
name|b
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|<
name|b
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|a
operator|.
name|label
argument_list|)
operator|.
name|compareTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|b
operator|.
name|label
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|sortFacetResults
specifier|protected
name|void
name|sortFacetResults
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResult
name|a
parameter_list|,
name|FacetResult
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|>
name|b
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|>
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFloatValuesEquals
specifier|protected
name|void
name|assertFloatValuesEquals
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|a
parameter_list|,
name|List
argument_list|<
name|FacetResult
argument_list|>
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|lastValue
init|=
name|Float
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FacetResult
argument_list|>
name|aByDim
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|a
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|floatValue
argument_list|()
operator|<=
name|lastValue
argument_list|)
expr_stmt|;
name|lastValue
operator|=
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|floatValue
argument_list|()
expr_stmt|;
name|aByDim
operator|.
name|put
argument_list|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|dim
argument_list|,
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastValue
operator|=
name|Float
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FacetResult
argument_list|>
name|bByDim
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|b
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bByDim
operator|.
name|put
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|dim
argument_list|,
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|floatValue
argument_list|()
operator|<=
name|lastValue
argument_list|)
expr_stmt|;
name|lastValue
operator|=
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|dim
range|:
name|aByDim
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertFloatValuesEquals
argument_list|(
name|aByDim
operator|.
name|get
argument_list|(
name|dim
argument_list|)
argument_list|,
name|bByDim
operator|.
name|get
argument_list|(
name|dim
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertFloatValuesEquals
specifier|protected
name|void
name|assertFloatValuesEquals
parameter_list|(
name|FacetResult
name|a
parameter_list|,
name|FacetResult
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|dim
argument_list|,
name|b
operator|.
name|dim
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|a
operator|.
name|path
argument_list|,
name|b
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|childCount
argument_list|,
name|b
operator|.
name|childCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|value
operator|.
name|floatValue
argument_list|()
argument_list|,
name|b
operator|.
name|value
operator|.
name|floatValue
argument_list|()
argument_list|,
name|a
operator|.
name|value
operator|.
name|floatValue
argument_list|()
operator|/
literal|1e5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|labelValues
operator|.
name|length
argument_list|,
name|b
operator|.
name|labelValues
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|a
operator|.
name|labelValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|label
argument_list|,
name|b
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|label
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|floatValue
argument_list|()
argument_list|,
name|b
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|floatValue
argument_list|()
argument_list|,
name|a
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|value
operator|.
name|floatValue
argument_list|()
operator|/
literal|1e5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

