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
name|params
operator|.
name|FacetIndexingParams
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
name|FacetLabel
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
name|CollectionUtil
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Result of faceted search.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|FacetResult
specifier|public
class|class
name|FacetResult
block|{
DECL|method|addIfNotExist
specifier|private
specifier|static
name|FacetResultNode
name|addIfNotExist
parameter_list|(
name|Map
argument_list|<
name|FacetLabel
argument_list|,
name|FacetResultNode
argument_list|>
name|nodes
parameter_list|,
name|FacetResultNode
name|node
parameter_list|)
block|{
name|FacetResultNode
name|n
init|=
name|nodes
operator|.
name|get
argument_list|(
name|node
operator|.
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|nodes
operator|.
name|put
argument_list|(
name|node
operator|.
name|label
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|n
operator|=
name|node
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
comment|/**    * A utility for merging multiple {@link FacetResult} of the same    * (hierarchical) dimension into a single {@link FacetResult}, to reconstruct    * the hierarchy. The results are merged according to the following rules:    *<ul>    *<li>If two results share the same dimension (first component in their    * {@link FacetLabel}), they are merged.    *<li>If a result is missing ancestors in the other results, e.g. A/B/C but    * no corresponding A or A/B, these nodes are 'filled' with their label,    * ordinal and value (obtained from the respective {@link FacetArrays}).    *<li>If a result does not share a dimension with other results, it is    * returned as is.    *</ul>    *<p>    *<b>NOTE:</b> the returned results are not guaranteed to be in the same    * order of the input ones.    *     * @param results    *          the results to merge    * @param taxoReader    *          the {@link TaxonomyReader} to use when creating missing ancestor    *          nodes    * @param dimArrays    *          a mapping from a dimension to the respective {@link FacetArrays}    *          from which to pull the nodes values    */
DECL|method|mergeHierarchies
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|mergeHierarchies
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FacetArrays
argument_list|>
name|dimArrays
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FacetResult
argument_list|>
argument_list|>
name|dims
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|FacetResult
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResult
name|fr
range|:
name|results
control|)
block|{
name|String
name|dim
init|=
name|fr
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|frs
init|=
name|dims
operator|.
name|get
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|frs
operator|==
literal|null
condition|)
block|{
name|frs
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
expr_stmt|;
name|dims
operator|.
name|put
argument_list|(
name|dim
argument_list|,
name|frs
argument_list|)
expr_stmt|;
block|}
name|frs
operator|.
name|add
argument_list|(
name|fr
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|frs
range|:
name|dims
operator|.
name|values
argument_list|()
control|)
block|{
name|FacetResult
name|mergedResult
init|=
name|frs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|frs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|frs
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
name|fr1
parameter_list|,
name|FacetResult
name|fr2
parameter_list|)
block|{
return|return
name|fr1
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
operator|.
name|compareTo
argument_list|(
name|fr2
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|FacetLabel
argument_list|,
name|FacetResultNode
argument_list|>
name|mergedNodes
init|=
operator|new
name|HashMap
argument_list|<
name|FacetLabel
argument_list|,
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
name|FacetArrays
name|arrays
init|=
name|dimArrays
operator|!=
literal|null
condition|?
name|dimArrays
operator|.
name|get
argument_list|(
name|frs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
else|:
literal|null
decl_stmt|;
for|for
control|(
name|FacetResult
name|fr
range|:
name|frs
control|)
block|{
name|FacetRequest
name|freq
init|=
name|fr
operator|.
name|getFacetRequest
argument_list|()
decl_stmt|;
name|OrdinalValueResolver
name|resolver
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|arrays
operator|!=
literal|null
condition|)
block|{
name|resolver
operator|=
name|freq
operator|.
name|createFacetsAggregator
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|)
operator|.
name|createOrdinalValueResolver
argument_list|(
name|freq
argument_list|,
name|arrays
argument_list|)
expr_stmt|;
block|}
name|FacetResultNode
name|frn
init|=
name|fr
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|FacetResultNode
name|merged
init|=
name|mergedNodes
operator|.
name|get
argument_list|(
name|frn
operator|.
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|merged
operator|==
literal|null
condition|)
block|{
name|FacetLabel
name|parent
init|=
name|frn
operator|.
name|label
operator|.
name|subpath
argument_list|(
name|frn
operator|.
name|label
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|FacetResultNode
name|childNode
init|=
name|frn
decl_stmt|;
name|FacetResultNode
name|parentNode
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|parent
operator|.
name|length
operator|>
literal|0
operator|&&
operator|(
name|parentNode
operator|=
name|mergedNodes
operator|.
name|get
argument_list|(
name|parent
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
name|int
name|parentOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|double
name|parentValue
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|arrays
operator|!=
literal|null
condition|)
block|{
name|parentValue
operator|=
name|resolver
operator|.
name|valueOf
argument_list|(
name|parentOrd
argument_list|)
expr_stmt|;
block|}
name|parentNode
operator|=
operator|new
name|FacetResultNode
argument_list|(
name|parentOrd
argument_list|,
name|parentValue
argument_list|)
expr_stmt|;
name|parentNode
operator|.
name|label
operator|=
name|parent
expr_stmt|;
name|parentNode
operator|.
name|subResults
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
expr_stmt|;
name|parentNode
operator|.
name|subResults
operator|.
name|add
argument_list|(
name|childNode
argument_list|)
expr_stmt|;
name|mergedNodes
operator|.
name|put
argument_list|(
name|parent
argument_list|,
name|parentNode
argument_list|)
expr_stmt|;
name|childNode
operator|=
name|parentNode
expr_stmt|;
name|parent
operator|=
name|parent
operator|.
name|subpath
argument_list|(
name|parent
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// at least one parent was added, so link the final (existing)
comment|// parent with the child
if|if
condition|(
name|parent
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|parentNode
operator|.
name|subResults
operator|instanceof
name|ArrayList
operator|)
condition|)
block|{
name|parentNode
operator|.
name|subResults
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|(
name|parentNode
operator|.
name|subResults
argument_list|)
expr_stmt|;
block|}
name|parentNode
operator|.
name|subResults
operator|.
name|add
argument_list|(
name|childNode
argument_list|)
expr_stmt|;
block|}
comment|// for missing FRNs, add new ones with label and value=-1
comment|// first time encountered this label, add it and all its children to
comment|// the map.
name|mergedNodes
operator|.
name|put
argument_list|(
name|frn
operator|.
name|label
argument_list|,
name|frn
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|child
range|:
name|frn
operator|.
name|subResults
control|)
block|{
name|addIfNotExist
argument_list|(
name|mergedNodes
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|merged
operator|.
name|subResults
operator|instanceof
name|ArrayList
operator|)
condition|)
block|{
name|merged
operator|.
name|subResults
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|(
name|merged
operator|.
name|subResults
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FacetResultNode
name|sub
range|:
name|frn
operator|.
name|subResults
control|)
block|{
comment|// make sure sub wasn't already added
name|sub
operator|=
name|addIfNotExist
argument_list|(
name|mergedNodes
argument_list|,
name|sub
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|merged
operator|.
name|subResults
operator|.
name|contains
argument_list|(
name|sub
argument_list|)
condition|)
block|{
name|merged
operator|.
name|subResults
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// find the 'first' node to put on the FacetResult root
name|FacetLabel
name|min
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FacetLabel
name|cp
range|:
name|mergedNodes
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|min
operator|==
literal|null
operator|||
name|cp
operator|.
name|compareTo
argument_list|(
name|min
argument_list|)
operator|<
literal|0
condition|)
block|{
name|min
operator|=
name|cp
expr_stmt|;
block|}
block|}
name|FacetRequest
name|dummy
init|=
operator|new
name|FacetRequest
argument_list|(
name|min
argument_list|,
name|frs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|numResults
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|FacetsAggregator
name|createFacetsAggregator
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not supported by this request"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|mergedResult
operator|=
operator|new
name|FacetResult
argument_list|(
name|dummy
argument_list|,
name|mergedNodes
operator|.
name|get
argument_list|(
name|min
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|add
argument_list|(
name|mergedResult
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|field|facetRequest
specifier|private
specifier|final
name|FacetRequest
name|facetRequest
decl_stmt|;
DECL|field|rootNode
specifier|private
specifier|final
name|FacetResultNode
name|rootNode
decl_stmt|;
DECL|field|numValidDescendants
specifier|private
specifier|final
name|int
name|numValidDescendants
decl_stmt|;
DECL|method|FacetResult
specifier|public
name|FacetResult
parameter_list|(
name|FacetRequest
name|facetRequest
parameter_list|,
name|FacetResultNode
name|rootNode
parameter_list|,
name|int
name|numValidDescendants
parameter_list|)
block|{
name|this
operator|.
name|facetRequest
operator|=
name|facetRequest
expr_stmt|;
name|this
operator|.
name|rootNode
operator|=
name|rootNode
expr_stmt|;
name|this
operator|.
name|numValidDescendants
operator|=
name|numValidDescendants
expr_stmt|;
block|}
comment|/**    * Facet result node matching the root of the {@link #getFacetRequest() facet request}.    * @see #getFacetRequest()    * @see FacetRequest#categoryPath    */
DECL|method|getFacetResultNode
specifier|public
specifier|final
name|FacetResultNode
name|getFacetResultNode
parameter_list|()
block|{
return|return
name|rootNode
return|;
block|}
comment|/**    * Number of descendants of {@link #getFacetResultNode() root facet result    * node}, up till the requested depth.    */
DECL|method|getNumValidDescendants
specifier|public
specifier|final
name|int
name|getNumValidDescendants
parameter_list|()
block|{
return|return
name|numValidDescendants
return|;
block|}
comment|/**    * Request for which this result was obtained.    */
DECL|method|getFacetRequest
specifier|public
specifier|final
name|FacetRequest
name|getFacetRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|facetRequest
return|;
block|}
comment|/**    * String representation of this facet result.    * Use with caution: might return a very long string.    * @param prefix prefix for each result line    * @see #toString()    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|nl
init|=
literal|""
decl_stmt|;
comment|// request
if|if
condition|(
name|this
operator|.
name|facetRequest
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"Request: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|facetRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|nl
operator|=
literal|"\n"
expr_stmt|;
block|}
comment|// total facets
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"Num valid Descendants (up to specified depth): "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|numValidDescendants
argument_list|)
expr_stmt|;
name|nl
operator|=
literal|"\n"
expr_stmt|;
comment|// result node
if|if
condition|(
name|this
operator|.
name|rootNode
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|rootNode
operator|.
name|toString
argument_list|(
name|prefix
operator|+
literal|"\t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

