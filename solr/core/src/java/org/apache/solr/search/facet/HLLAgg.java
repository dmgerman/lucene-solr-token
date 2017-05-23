begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
operator|.
name|HLL
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
name|hll
operator|.
name|HLLType
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
name|DocValues
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Hash
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
name|util
operator|.
name|SimpleOrderedMap
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
name|SchemaField
import|;
end_import

begin_class
DECL|class|HLLAgg
specifier|public
class|class
name|HLLAgg
extends|extends
name|StrAggValueSource
block|{
DECL|field|NO_VALUES
specifier|public
specifier|static
name|Integer
name|NO_VALUES
init|=
literal|0
decl_stmt|;
DECL|field|factory
specifier|protected
name|HLLFactory
name|factory
decl_stmt|;
DECL|method|HLLAgg
specifier|public
name|HLLAgg
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
literal|"hll"
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|HLLFactory
argument_list|()
expr_stmt|;
block|}
comment|// factory for the hyper-log-log algorithm.
comment|// TODO: make stats component HllOptions inherit from this?
DECL|class|HLLFactory
specifier|public
specifier|static
class|class
name|HLLFactory
block|{
DECL|field|log2m
name|int
name|log2m
init|=
literal|13
decl_stmt|;
DECL|field|regwidth
name|int
name|regwidth
init|=
literal|6
decl_stmt|;
DECL|method|getHLL
specifier|public
name|HLL
name|getHLL
parameter_list|()
block|{
return|return
operator|new
name|HLL
argument_list|(
name|log2m
argument_list|,
name|regwidth
argument_list|,
operator|-
literal|1
comment|/* auto explict threshold */
argument_list|,
literal|false
comment|/* no sparse representation */
argument_list|,
name|HLLType
operator|.
name|EMPTY
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createSlotAcc
specifier|public
name|SlotAcc
name|createSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|getArg
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
operator|||
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|multiValuedFieldCache
argument_list|()
condition|)
block|{
if|if
condition|(
name|sf
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
operator|new
name|UniqueMultiDvSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|sf
argument_list|,
name|numSlots
argument_list|,
name|fcontext
operator|.
name|isShard
argument_list|()
condition|?
name|factory
else|:
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|UniqueMultivaluedSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|sf
argument_list|,
name|numSlots
argument_list|,
name|fcontext
operator|.
name|isShard
argument_list|()
condition|?
name|factory
else|:
literal|null
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getNumberType
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// always use hll here since we don't know how many values there are?
return|return
operator|new
name|NumericAcc
argument_list|(
name|fcontext
argument_list|,
name|getArg
argument_list|()
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|UniqueSinglevaluedSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|sf
argument_list|,
name|numSlots
argument_list|,
name|fcontext
operator|.
name|isShard
argument_list|()
condition|?
name|factory
else|:
literal|null
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Merger
argument_list|()
return|;
block|}
DECL|class|Merger
specifier|private
specifier|static
class|class
name|Merger
extends|extends
name|FacetSortableMerger
block|{
DECL|field|aggregate
name|HLL
name|aggregate
init|=
literal|null
decl_stmt|;
DECL|field|answer
name|long
name|answer
init|=
operator|-
literal|1
decl_stmt|;
comment|// -1 means unset
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|,
name|Context
name|mcontext
parameter_list|)
block|{
if|if
condition|(
name|facetResult
operator|instanceof
name|Number
condition|)
block|{
assert|assert
name|NO_VALUES
operator|.
name|equals
argument_list|(
name|facetResult
argument_list|)
assert|;
return|return;
block|}
name|SimpleOrderedMap
name|map
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|facetResult
decl_stmt|;
name|byte
index|[]
name|serialized
init|=
operator|(
operator|(
name|byte
index|[]
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"hll"
argument_list|)
operator|)
decl_stmt|;
name|HLL
name|subHLL
init|=
name|HLL
operator|.
name|fromBytes
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregate
operator|==
literal|null
condition|)
block|{
name|aggregate
operator|=
name|subHLL
expr_stmt|;
block|}
else|else
block|{
name|aggregate
operator|.
name|union
argument_list|(
name|subHLL
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLong
specifier|private
name|long
name|getLong
parameter_list|()
block|{
if|if
condition|(
name|answer
operator|<
literal|0
condition|)
block|{
name|answer
operator|=
name|aggregate
operator|==
literal|null
condition|?
literal|0
else|:
name|aggregate
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
return|return
name|getLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FacetSortableMerger
name|other
parameter_list|,
name|FacetRequest
operator|.
name|SortDirection
name|direction
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|getLong
argument_list|()
argument_list|,
operator|(
operator|(
name|Merger
operator|)
name|other
operator|)
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// TODO: hybrid model for non-distrib numbers?
comment|// todo - better efficiency for sorting?
DECL|class|NumericAcc
class|class
name|NumericAcc
extends|extends
name|SlotAcc
block|{
DECL|field|sf
name|SchemaField
name|sf
decl_stmt|;
DECL|field|sets
name|HLL
index|[]
name|sets
decl_stmt|;
DECL|field|values
name|NumericDocValues
name|values
decl_stmt|;
DECL|method|NumericAcc
specifier|public
name|NumericAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|sf
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|sets
operator|=
operator|new
name|HLL
index|[
name|numSlots
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|sets
operator|=
operator|new
name|HLL
index|[
name|sets
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|sets
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|sets
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|valuesDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|valuesDocID
operator|<
name|doc
condition|)
block|{
name|valuesDocID
operator|=
name|values
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|valuesDocID
operator|>
name|doc
condition|)
block|{
return|return;
block|}
assert|assert
name|valuesDocID
operator|==
name|doc
assert|;
name|long
name|val
init|=
name|values
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|hash
init|=
name|Hash
operator|.
name|fmix64
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|HLL
name|hll
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|hll
operator|==
literal|null
condition|)
block|{
name|hll
operator|=
name|sets
index|[
name|slot
index|]
operator|=
name|factory
operator|.
name|getHLL
argument_list|()
expr_stmt|;
block|}
name|hll
operator|.
name|addRaw
argument_list|(
name|hash
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|)
block|{
return|return
name|getShardValue
argument_list|(
name|slot
argument_list|)
return|;
block|}
return|return
name|getCardinality
argument_list|(
name|slot
argument_list|)
return|;
block|}
DECL|method|getCardinality
specifier|private
name|int
name|getCardinality
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|HLL
name|set
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
return|return
name|set
operator|==
literal|null
condition|?
literal|0
else|:
operator|(
name|int
operator|)
name|set
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|getShardValue
specifier|public
name|Object
name|getShardValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|HLL
name|hll
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|hll
operator|==
literal|null
condition|)
return|return
name|NO_VALUES
return|;
name|SimpleOrderedMap
name|map
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"hll"
argument_list|,
name|hll
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// optionally use explicit values
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|getCardinality
argument_list|(
name|slotA
argument_list|)
operator|-
name|getCardinality
argument_list|(
name|slotB
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

