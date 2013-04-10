begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|FacetRequest
operator|.
name|SortOrder
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
name|ParallelTaxonomyArrays
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link FacetResultsHandler} which counts the top-K facets at depth 1 only  * and always labels all result categories. The results are always sorted by  * value, in descending order. Sub-classes are responsible to pull the values  * from the corresponding {@link FacetArrays}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DepthOneFacetResultsHandler
specifier|public
specifier|abstract
class|class
name|DepthOneFacetResultsHandler
extends|extends
name|FacetResultsHandler
block|{
DECL|class|FacetResultNodeQueue
specifier|private
specifier|static
class|class
name|FacetResultNodeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|FacetResultNodeQueue
specifier|public
name|FacetResultNodeQueue
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|boolean
name|prepopulate
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
name|prepopulate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSentinelObject
specifier|protected
name|FacetResultNode
name|getSentinelObject
parameter_list|()
block|{
return|return
operator|new
name|FacetResultNode
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|a
parameter_list|,
name|FacetResultNode
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|a
operator|.
name|value
operator|>
name|b
operator|.
name|value
condition|)
return|return
literal|false
return|;
comment|// both have the same value, break tie by ordinal
return|return
name|a
operator|.
name|ordinal
operator|<
name|b
operator|.
name|ordinal
return|;
block|}
block|}
DECL|method|DepthOneFacetResultsHandler
specifier|public
name|DepthOneFacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetRequest
name|facetRequest
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|super
argument_list|(
name|taxonomyReader
argument_list|,
name|facetRequest
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
assert|assert
name|facetRequest
operator|.
name|getDepth
argument_list|()
operator|==
literal|1
operator|:
literal|"this handler only computes the top-K facets at depth 1"
assert|;
assert|assert
name|facetRequest
operator|.
name|numResults
operator|==
name|facetRequest
operator|.
name|getNumLabel
argument_list|()
operator|:
literal|"this handler always labels all top-K results"
assert|;
assert|assert
name|facetRequest
operator|.
name|getSortOrder
argument_list|()
operator|==
name|SortOrder
operator|.
name|DESCENDING
operator|:
literal|"this handler always sorts results in descending order"
assert|;
block|}
comment|/** Returnt the value of the requested ordinal. Called once for the result root. */
DECL|method|valueOf
specifier|protected
specifier|abstract
name|double
name|valueOf
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Add the siblings of {@code ordinal} to the given list. This is called    * whenever the number of results is too high (&gt; taxonomy size), instead of    * adding them to a {@link PriorityQueue}.    */
DECL|method|addSiblings
specifier|protected
specifier|abstract
name|void
name|addSiblings
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add the siblings of {@code ordinal} to the given {@link PriorityQueue}. The    * given {@link PriorityQueue} is already filled with sentinel objects, so    * implementations are encouraged to use {@link PriorityQueue#top()} and    * {@link PriorityQueue#updateTop()} for best performance.  Returns the total    * number of siblings.    */
DECL|method|addSiblings
specifier|protected
specifier|abstract
name|int
name|addSiblings
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
name|pq
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|compute
specifier|public
specifier|final
name|FacetResult
name|compute
parameter_list|()
throws|throws
name|IOException
block|{
name|ParallelTaxonomyArrays
name|arrays
init|=
name|taxonomyReader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|children
init|=
name|arrays
operator|.
name|children
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|siblings
init|=
name|arrays
operator|.
name|siblings
argument_list|()
decl_stmt|;
name|int
name|rootOrd
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
name|facetRequest
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
name|FacetResultNode
name|root
init|=
operator|new
name|FacetResultNode
argument_list|(
name|rootOrd
argument_list|,
name|valueOf
argument_list|(
name|rootOrd
argument_list|)
argument_list|)
decl_stmt|;
name|root
operator|.
name|label
operator|=
name|facetRequest
operator|.
name|categoryPath
expr_stmt|;
if|if
condition|(
name|facetRequest
operator|.
name|numResults
operator|>
name|taxonomyReader
operator|.
name|getSize
argument_list|()
condition|)
block|{
comment|// specialize this case, user is interested in all available results
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|child
init|=
name|children
index|[
name|rootOrd
index|]
decl_stmt|;
name|addSiblings
argument_list|(
name|child
argument_list|,
name|siblings
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResultNode
name|o1
parameter_list|,
name|FacetResultNode
name|o2
parameter_list|)
block|{
name|int
name|value
init|=
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|value
operator|-
name|o1
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|o2
operator|.
name|ordinal
operator|-
name|o1
operator|.
name|ordinal
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|root
operator|.
name|subResults
operator|=
name|nodes
expr_stmt|;
return|return
operator|new
name|FacetResult
argument_list|(
name|facetRequest
argument_list|,
name|root
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
comment|// since we use sentinel objects, we cannot reuse PQ. but that's ok because it's not big
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
name|pq
init|=
operator|new
name|FacetResultNodeQueue
argument_list|(
name|facetRequest
operator|.
name|numResults
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|numSiblings
init|=
name|addSiblings
argument_list|(
name|children
index|[
name|rootOrd
index|]
argument_list|,
name|siblings
argument_list|,
name|pq
argument_list|)
decl_stmt|;
comment|// pop() the least (sentinel) elements
name|int
name|pqsize
init|=
name|pq
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|numSiblings
operator|<
name|pqsize
condition|?
name|numSiblings
else|:
name|pqsize
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pqsize
operator|-
name|size
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|// create the FacetResultNodes.
name|FacetResultNode
index|[]
name|subResults
init|=
operator|new
name|FacetResultNode
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|FacetResultNode
name|node
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|node
operator|.
name|label
operator|=
name|taxonomyReader
operator|.
name|getPath
argument_list|(
name|node
operator|.
name|ordinal
argument_list|)
expr_stmt|;
name|subResults
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
name|root
operator|.
name|subResults
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|subResults
argument_list|)
expr_stmt|;
return|return
operator|new
name|FacetResult
argument_list|(
name|facetRequest
argument_list|,
name|root
argument_list|,
name|numSiblings
argument_list|)
return|;
block|}
block|}
end_class

end_unit

