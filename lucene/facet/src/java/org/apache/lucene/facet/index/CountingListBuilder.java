begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
operator|.
name|Entry
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
operator|.
name|OrdinalPolicy
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
name|index
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
name|TaxonomyWriter
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
name|IntsRef
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link CategoryListBuilder} which builds a counting list data by encoding  * the category ordinals into one or more {@link BytesRef}. Each  * {@link BytesRef} corresponds to a set of ordinals that belong to the same  * partition. When partitions are not enabled (i.e.  * {@link FacetIndexingParams#getPartitionSize()} returns  * {@link Integer#MAX_VALUE}), only one {@link BytesRef} is returned by this  * class.  *<p>  * Counting lists are used usually for computing the weight of categories by  * summing their number of occurrences (hence counting) in a result set.  */
end_comment

begin_class
DECL|class|CountingListBuilder
specifier|public
class|class
name|CountingListBuilder
implements|implements
name|CategoryListBuilder
block|{
comment|/** Specializes encoding ordinals when partitions are enabled/disabled. */
DECL|class|OrdinalsEncoder
specifier|private
specifier|static
specifier|abstract
class|class
name|OrdinalsEncoder
block|{
DECL|method|OrdinalsEncoder
name|OrdinalsEncoder
parameter_list|()
block|{}
DECL|method|encode
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|encode
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|)
function_decl|;
block|}
DECL|class|NoPartitionsOrdinalsEncoder
specifier|private
specifier|static
specifier|final
class|class
name|NoPartitionsOrdinalsEncoder
extends|extends
name|OrdinalsEncoder
block|{
DECL|field|encoder
specifier|private
specifier|final
name|IntEncoder
name|encoder
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
init|=
literal|""
decl_stmt|;
DECL|method|NoPartitionsOrdinalsEncoder
name|NoPartitionsOrdinalsEncoder
parameter_list|(
name|CategoryListParams
name|categoryListParams
parameter_list|)
block|{
name|encoder
operator|=
name|categoryListParams
operator|.
name|createEncoder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|encode
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|128
argument_list|)
decl_stmt|;
comment|// should be enough for most common applications
name|encoder
operator|.
name|encode
argument_list|(
name|ordinals
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|name
argument_list|,
name|bytes
argument_list|)
return|;
block|}
block|}
DECL|class|PerPartitionOrdinalsEncoder
specifier|private
specifier|static
specifier|final
class|class
name|PerPartitionOrdinalsEncoder
extends|extends
name|OrdinalsEncoder
block|{
DECL|field|indexingParams
specifier|private
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
DECL|field|categoryListParams
specifier|private
specifier|final
name|CategoryListParams
name|categoryListParams
decl_stmt|;
DECL|field|partitionSize
specifier|private
specifier|final
name|int
name|partitionSize
decl_stmt|;
DECL|field|partitionEncoder
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|IntEncoder
argument_list|>
name|partitionEncoder
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|IntEncoder
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|PerPartitionOrdinalsEncoder
name|PerPartitionOrdinalsEncoder
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|,
name|CategoryListParams
name|categoryListParams
parameter_list|)
block|{
name|this
operator|.
name|indexingParams
operator|=
name|indexingParams
expr_stmt|;
name|this
operator|.
name|categoryListParams
operator|=
name|categoryListParams
expr_stmt|;
name|this
operator|.
name|partitionSize
operator|=
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|encode
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|)
block|{
comment|// build the partitionOrdinals map
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|IntsRef
argument_list|>
name|partitionOrdinals
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|IntsRef
argument_list|>
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
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|PartitionsUtils
operator|.
name|partitionNameByOrdinal
argument_list|(
name|indexingParams
argument_list|,
name|ordinal
argument_list|)
decl_stmt|;
name|IntsRef
name|partitionOrds
init|=
name|partitionOrdinals
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|partitionOrds
operator|==
literal|null
condition|)
block|{
name|partitionOrds
operator|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
expr_stmt|;
name|partitionOrdinals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|partitionOrds
argument_list|)
expr_stmt|;
name|partitionEncoder
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|categoryListParams
operator|.
name|createEncoder
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|partitionOrds
operator|.
name|ints
index|[
name|partitionOrds
operator|.
name|length
operator|++
index|]
operator|=
name|ordinal
operator|%
name|partitionSize
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|partitionBytes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|IntsRef
argument_list|>
name|e
range|:
name|partitionOrdinals
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|IntEncoder
name|encoder
init|=
name|partitionEncoder
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|128
argument_list|)
decl_stmt|;
comment|// should be enough for most common applications
name|encoder
operator|.
name|encode
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|partitionBytes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|partitionBytes
return|;
block|}
block|}
DECL|field|ordinalsEncoder
specifier|private
specifier|final
name|OrdinalsEncoder
name|ordinalsEncoder
decl_stmt|;
DECL|field|taxoWriter
specifier|private
specifier|final
name|TaxonomyWriter
name|taxoWriter
decl_stmt|;
DECL|field|clp
specifier|private
specifier|final
name|CategoryListParams
name|clp
decl_stmt|;
DECL|method|CountingListBuilder
specifier|public
name|CountingListBuilder
parameter_list|(
name|CategoryListParams
name|categoryListParams
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|,
name|TaxonomyWriter
name|taxoWriter
parameter_list|)
block|{
name|this
operator|.
name|taxoWriter
operator|=
name|taxoWriter
expr_stmt|;
name|this
operator|.
name|clp
operator|=
name|categoryListParams
expr_stmt|;
if|if
condition|(
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|ordinalsEncoder
operator|=
operator|new
name|NoPartitionsOrdinalsEncoder
argument_list|(
name|categoryListParams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ordinalsEncoder
operator|=
operator|new
name|PerPartitionOrdinalsEncoder
argument_list|(
name|indexingParams
argument_list|,
name|categoryListParams
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Every returned {@link BytesRef} corresponds to a single partition (as    * defined by {@link FacetIndexingParams#getPartitionSize()}) and the key    * denotes the partition ID. When no partitions are defined, the returned map    * contains only one value.    *<p>    *<b>NOTE:</b> the {@code ordinals} array is modified by adding parent    * ordinals to it. Also, some encoders may sort the array and remove duplicate    * ordinals. Therefore you may want to invoke this method after you finished    * processing the array for other purposes.    */
annotation|@
name|Override
DECL|method|build
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|build
parameter_list|(
name|IntsRef
name|ordinals
parameter_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|upto
init|=
name|ordinals
operator|.
name|length
decl_stmt|;
comment|// since we may add ordinals to IntsRef, iterate upto original length
name|Iterator
argument_list|<
name|CategoryPath
argument_list|>
name|iter
init|=
name|categories
operator|.
name|iterator
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
name|upto
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|ordinals
operator|.
name|ints
index|[
name|i
index|]
decl_stmt|;
name|CategoryPath
name|cp
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|OrdinalPolicy
name|op
init|=
name|clp
operator|.
name|getOrdinalPolicy
argument_list|(
name|cp
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
name|OrdinalPolicy
operator|.
name|NO_PARENTS
condition|)
block|{
comment|// need to add parents too
name|int
name|parent
init|=
name|taxoWriter
operator|.
name|getParent
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
while|while
condition|(
name|parent
operator|>
literal|0
condition|)
block|{
name|ordinals
operator|.
name|ints
index|[
name|ordinals
operator|.
name|length
operator|++
index|]
operator|=
name|parent
expr_stmt|;
name|parent
operator|=
name|taxoWriter
operator|.
name|getParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|==
name|OrdinalPolicy
operator|.
name|ALL_BUT_DIMENSION
condition|)
block|{
comment|// discard the last added parent, which is the dimension
name|ordinals
operator|.
name|length
operator|--
expr_stmt|;
block|}
block|}
block|}
return|return
name|ordinalsEncoder
operator|.
name|encode
argument_list|(
name|ordinals
argument_list|)
return|;
block|}
block|}
end_class

end_unit

