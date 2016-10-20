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
name|FixedBitSet
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_class
DECL|class|UniqueMultivaluedSlotAcc
class|class
name|UniqueMultivaluedSlotAcc
extends|extends
name|UniqueSlotAcc
implements|implements
name|UnInvertedField
operator|.
name|Callback
block|{
DECL|field|uif
specifier|private
name|UnInvertedField
name|uif
decl_stmt|;
DECL|field|docToTerm
specifier|private
name|UnInvertedField
operator|.
name|DocToTerm
name|docToTerm
decl_stmt|;
DECL|method|UniqueMultivaluedSlotAcc
specifier|public
name|UniqueMultivaluedSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|HLLAgg
operator|.
name|HLLFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|field
argument_list|,
name|numSlots
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|uif
operator|=
name|UnInvertedField
operator|.
name|getUnInvertedField
argument_list|(
name|field
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|docToTerm
operator|=
name|uif
operator|.
expr|new
name|DocToTerm
argument_list|()
expr_stmt|;
name|fcontext
operator|.
name|qcontext
operator|.
name|addCloseHook
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// TODO: find way to close accumulators instead of using close hook?
name|nTerms
operator|=
name|uif
operator|.
name|numTerms
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|protected
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docToTerm
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
comment|// bits for the current slot, only set for the callback
annotation|@
name|Override
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|int
name|termNum
parameter_list|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|termNum
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
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
name|bits
operator|=
name|arr
index|[
name|slotNum
index|]
expr_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|nTerms
argument_list|)
expr_stmt|;
name|arr
index|[
name|slotNum
index|]
operator|=
name|bits
expr_stmt|;
block|}
name|docToTerm
operator|.
name|getBigTerms
argument_list|(
name|doc
operator|+
name|currentDocBase
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// this will call back to our Callback.call(int termNum)
name|docToTerm
operator|.
name|getSmallTerms
argument_list|(
name|doc
operator|+
name|currentDocBase
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|docToTerm
operator|!=
literal|null
condition|)
block|{
name|docToTerm
operator|.
name|close
argument_list|()
expr_stmt|;
name|docToTerm
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

