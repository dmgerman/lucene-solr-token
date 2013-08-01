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
name|ResultMode
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
name|CategoryPath
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Result of faceted search for a certain taxonomy node. This class serves as a  * bin of different attributes of the result node, such as its {@link #ordinal}  * as well as {@link #label}. You are not expected to modify those values.  *<p>  * This class implements {@link Comparable} for easy comparisons of result  * nodes, e.g. when sorting or computing top-K nodes.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|FacetResultNode
specifier|public
class|class
name|FacetResultNode
implements|implements
name|Comparable
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|field|EMPTY_SUB_RESULTS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|FacetResultNode
argument_list|>
name|EMPTY_SUB_RESULTS
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|/** The category ordinal of this node. */
DECL|field|ordinal
specifier|public
name|int
name|ordinal
decl_stmt|;
comment|/**    * The {@link CategoryPath label} of this result. May be {@code null} if not    * computed, in which case use {@link TaxonomyReader#getPath(int)} to label    * it.    *<p>    *<b>NOTE:</b> by default, all nodes are labeled. Only when    * {@link FacetRequest#getNumLabel()}&lt;    * {@link FacetRequest#numResults} there will be unlabeled nodes.    */
DECL|field|label
specifier|public
name|CategoryPath
name|label
decl_stmt|;
comment|/**    * The value of this result. Its actual type depends on the    * {@link FacetRequest} used (e.g. in case of {@link CountFacetRequest} it is    * {@code int}).    */
DECL|field|value
specifier|public
name|double
name|value
decl_stmt|;
comment|/**    * The sub-results of this result. If {@link FacetRequest#getResultMode()} is    * {@link ResultMode#PER_NODE_IN_TREE}, every sub result denotes an immediate    * child of this node. Otherwise, it is a descendant of any level.    *<p>    *<b>NOTE:</b> this member should not be {@code null}. To denote that a    * result does not have sub results, set it to {@link #EMPTY_SUB_RESULTS} (or    * don't modify it).    */
DECL|field|subResults
specifier|public
name|List
argument_list|<
name|FacetResultNode
argument_list|>
name|subResults
init|=
name|EMPTY_SUB_RESULTS
decl_stmt|;
DECL|method|FacetResultNode
specifier|public
name|FacetResultNode
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FacetResultNode
name|o
parameter_list|)
block|{
name|int
name|res
init|=
name|Double
operator|.
name|compare
argument_list|(
name|value
argument_list|,
name|o
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
name|ordinal
operator|-
name|o
operator|.
name|ordinal
expr_stmt|;
block|}
return|return
name|res
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
comment|/** Returns a String representation of this facet result node. */
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
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"not labeled (ordinal="
argument_list|)
operator|.
name|append
argument_list|(
name|ordinal
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|label
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|sub
range|:
name|subResults
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|sub
operator|.
name|toString
argument_list|(
name|prefix
operator|+
literal|"  "
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
block|}
end_class

end_unit

