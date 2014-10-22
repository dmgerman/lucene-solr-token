begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Bits
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

begin_comment
comment|/**  * Base class for DocIdSet to be used with DocValues. The implementation  * of its iterator is very stupid and slow if the implementation of the  * {@link #matchDoc} method is not optimized, as iterators simply increment  * the document id until {@code matchDoc(int)} returns true. Because of this  * {@code matchDoc(int)} must be as fast as possible and in no case do any  * I/O.  * @lucene.internal  */
end_comment

begin_class
DECL|class|DocValuesDocIdSet
specifier|public
specifier|abstract
class|class
name|DocValuesDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|acceptDocs
specifier|protected
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|method|DocValuesDocIdSet
specifier|public
name|DocValuesDocIdSet
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
block|}
comment|/**    * this method checks, if a doc is a hit    */
DECL|method|matchDoc
specifier|protected
specifier|abstract
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
specifier|final
name|Bits
name|bits
parameter_list|()
block|{
return|return
operator|(
name|acceptDocs
operator|==
literal|null
operator|)
condition|?
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
return|return
name|matchDoc
argument_list|(
name|docid
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
else|:
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|docid
parameter_list|)
block|{
return|return
name|matchDoc
argument_list|(
name|docid
argument_list|)
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|docid
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|final
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
condition|)
block|{
comment|// Specialization optimization disregard acceptDocs
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
do|do
block|{
name|doc
operator|++
expr_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
do|while
condition|(
operator|!
name|matchDoc
argument_list|(
name|doc
argument_list|)
condition|)
do|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
for|for
control|(
name|doc
operator|=
name|target
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|matchDoc
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|acceptDocs
operator|instanceof
name|FixedBitSet
condition|)
block|{
comment|// special case for FixedBitSet: use the iterator and filter it
comment|// (used e.g. when Filters are chained by FilteredQuery)
return|return
operator|new
name|FilteredDocIdSetIterator
argument_list|(
operator|(
operator|(
name|DocIdSet
operator|)
name|acceptDocs
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|DocValuesDocIdSet
operator|.
name|this
operator|.
name|matchDoc
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
else|else
block|{
comment|// Stupid consultation of acceptDocs and matchDoc()
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
do|do
block|{
name|doc
operator|++
expr_stmt|;
if|if
condition|(
name|doc
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
do|while
condition|(
operator|!
operator|(
name|matchDoc
argument_list|(
name|doc
argument_list|)
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
do|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
for|for
control|(
name|doc
operator|=
name|target
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
control|)
block|{
if|if
condition|(
name|matchDoc
argument_list|(
name|doc
argument_list|)
operator|&&
name|acceptDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

