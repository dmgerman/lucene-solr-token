begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.params
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|params
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
name|io
operator|.
name|Serializable
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
name|IndexReader
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
name|Term
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
name|CategoryListIterator
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
name|PayloadIntDecodingIterator
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
name|TotalFacetCounts
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
name|util
operator|.
name|PartitionsUtils
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
name|encoding
operator|.
name|DGapIntEncoder
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
name|encoding
operator|.
name|IntDecoder
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
name|encoding
operator|.
name|IntEncoder
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
name|encoding
operator|.
name|SortingIntEncoder
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
name|encoding
operator|.
name|UniqueValuesIntEncoder
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
name|encoding
operator|.
name|VInt8IntEncoder
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Contains parameters for a category list *  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryListParams
specifier|public
class|class
name|CategoryListParams
implements|implements
name|Serializable
block|{
comment|/** The default term used to store the facets information. */
DECL|field|DEFAULT_TERM
specifier|public
specifier|static
specifier|final
name|Term
name|DEFAULT_TERM
init|=
operator|new
name|Term
argument_list|(
literal|"$facets"
argument_list|,
literal|"$fulltree$"
argument_list|)
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
comment|/**    * Constructs a default category list parameters object, using    * {@link #DEFAULT_TERM}.    */
DECL|method|CategoryListParams
specifier|public
name|CategoryListParams
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_TERM
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a category list parameters object, using the given {@link Term}.    * @param term who's payload hold the category-list.    */
DECL|method|CategoryListParams
specifier|public
name|CategoryListParams
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
comment|// Pre-compute the hashCode because these objects are immutable.  Saves
comment|// some time on the comparisons later.
name|this
operator|.
name|hashCode
operator|=
name|term
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/**     * A {@link Term} who's payload holds the category-list.     */
DECL|method|getTerm
specifier|public
specifier|final
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
comment|/**    * Allows to override how categories are encoded and decoded. A matching    * {@link IntDecoder} is provided by the {@link IntEncoder}.    *<p>    * Default implementation creates a new Sorting(<b>Unique</b>(DGap)) encoder.    * Uniqueness in this regard means when the same category appears twice in a    * document, only one appearance would be encoded. This has effect on facet    * counting results.    *<p>    * Some possible considerations when overriding may be:    *<ul>    *<li>an application "knows" that all categories are unique. So no need to    * pass through the unique filter.</li>    *<li>Another application might wish to count multiple occurrences of the    * same category, or, use a faster encoding which will consume more space.</li>    *</ul>    * In any event when changing this value make sure you know what you are    * doing, and test the results - e.g. counts, if the application is about    * counting facets.    */
DECL|method|createEncoder
specifier|public
name|IntEncoder
name|createEncoder
parameter_list|()
block|{
return|return
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Equality is defined by the 'term' that defines this category list.      * Sub-classes should override this method if a more complex calculation    * is needed to ensure equality.     */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|CategoryListParams
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CategoryListParams
name|other
init|=
operator|(
name|CategoryListParams
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|hashCode
operator|!=
name|other
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// The above hashcodes might equal each other in the case of a collision,
comment|// so at this point only directly term equality testing will settle
comment|// the equality test.
return|return
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
comment|/**    * Hashcode is similar to {@link #equals(Object)}, in that it uses    * the term that defines this category list to derive the hashcode.    * Subclasses need to ensure that equality/hashcode is correctly defined,    * or there could be side-effects in the {@link TotalFacetCounts} caching     * mechanism (as the filename for a Total Facet Counts array cache     * is dependent on the hashCode, so it should consistently return the same    * hash for identity).    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|hashCode
return|;
block|}
comment|/**    * Create the category list iterator for the specified partition.    */
DECL|method|createCategoryListIterator
specifier|public
name|CategoryListIterator
name|createCategoryListIterator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|categoryListTermStr
init|=
name|PartitionsUtils
operator|.
name|partitionName
argument_list|(
name|this
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|Term
name|payloadTerm
init|=
operator|new
name|Term
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|categoryListTermStr
argument_list|)
decl_stmt|;
return|return
operator|new
name|PayloadIntDecodingIterator
argument_list|(
name|reader
argument_list|,
name|payloadTerm
argument_list|,
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

