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
name|DocValuesCategoryListIterator
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
name|DGapVInt8IntEncoder
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
block|{
comment|/** OrdinalPolicy defines which ordinals are encoded for every document. */
DECL|enum|OrdinalPolicy
specifier|public
specifier|static
enum|enum
name|OrdinalPolicy
block|{
comment|/**      * Encodes only the ordinal of leaf nodes. That is, the category A/B/C will      * not encode the ordinals of A and A/B.      *       *<p>      *<b>NOTE:</b> this {@link OrdinalPolicy} requires a special collector or      * accumulator, which will fix the parents' counts, unless you are not      * interested in the parents counts.      */
DECL|enum constant|NO_PARENTS
name|NO_PARENTS
block|,
comment|/**      * Encodes the ordinals of all path components. That is, the category A/B/C      * will encode the ordinals of A and A/B as well. This is the default      * {@link OrdinalPolicy}.      */
DECL|enum constant|ALL_PARENTS
name|ALL_PARENTS
block|}
comment|/** The default field used to store the facets information. */
DECL|field|DEFAULT_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FIELD
init|=
literal|"$facets"
decl_stmt|;
comment|/**    * The default {@link OrdinalPolicy} that's used when encoding a document's    * category ordinals.    */
DECL|field|DEFAULT_ORDINAL_POLICY
specifier|public
specifier|static
specifier|final
name|OrdinalPolicy
name|DEFAULT_ORDINAL_POLICY
init|=
name|OrdinalPolicy
operator|.
name|ALL_PARENTS
decl_stmt|;
DECL|field|field
specifier|public
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
comment|/** Constructs a default category list parameters object, using {@link #DEFAULT_FIELD}. */
DECL|method|CategoryListParams
specifier|public
name|CategoryListParams
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_FIELD
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs a category list parameters object, using the given field. */
DECL|method|CategoryListParams
specifier|public
name|CategoryListParams
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
comment|// Pre-compute the hashCode because these objects are immutable.  Saves
comment|// some time on the comparisons later.
name|this
operator|.
name|hashCode
operator|=
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
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
name|DGapVInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
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
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
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
comment|/** Create the {@link CategoryListIterator} for the specified partition. */
DECL|method|createCategoryListIterator
specifier|public
name|CategoryListIterator
name|createCategoryListIterator
parameter_list|(
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
name|partition
argument_list|)
decl_stmt|;
name|String
name|docValuesField
init|=
name|field
operator|+
name|categoryListTermStr
decl_stmt|;
return|return
operator|new
name|DocValuesCategoryListIterator
argument_list|(
name|docValuesField
argument_list|,
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the {@link OrdinalPolicy} to use for this {@link CategoryListParams}. */
DECL|method|getOrdinalPolicy
specifier|public
name|OrdinalPolicy
name|getOrdinalPolicy
parameter_list|()
block|{
return|return
name|DEFAULT_ORDINAL_POLICY
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
literal|"field="
operator|+
name|field
operator|+
literal|" encoder="
operator|+
name|createEncoder
argument_list|()
operator|+
literal|" ordinalPolicy="
operator|+
name|getOrdinalPolicy
argument_list|()
return|;
block|}
block|}
end_class

end_unit

