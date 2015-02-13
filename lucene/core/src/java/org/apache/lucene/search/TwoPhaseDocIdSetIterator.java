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

begin_comment
comment|/**  * An approximation of a {@link DocIdSetIterator}. When the {@link #approximation()}'s  * {@link DocIdSetIterator#nextDoc()} or {@link DocIdSetIterator#advance(int)}  * return, {@link #matches()} needs to be checked in order to know whether the  * returned doc ID actually matches.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TwoPhaseDocIdSetIterator
specifier|public
specifier|abstract
class|class
name|TwoPhaseDocIdSetIterator
block|{
comment|/** Return a {@link DocIdSetIterator} view of the provided    *  {@link TwoPhaseDocIdSetIterator}. */
DECL|method|asDocIdSetIterator
specifier|public
specifier|static
name|DocIdSetIterator
name|asDocIdSetIterator
parameter_list|(
name|TwoPhaseDocIdSetIterator
name|twoPhaseIterator
parameter_list|)
block|{
specifier|final
name|DocIdSetIterator
name|approximation
init|=
name|twoPhaseIterator
operator|.
name|approximation
argument_list|()
decl_stmt|;
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|approximation
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doNext
argument_list|(
name|approximation
operator|.
name|nextDoc
argument_list|()
argument_list|)
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
throws|throws
name|IOException
block|{
return|return
name|doNext
argument_list|(
name|approximation
operator|.
name|advance
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|int
name|doNext
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
name|doc
operator|=
name|approximation
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
elseif|else
if|if
condition|(
name|twoPhaseIterator
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|approximation
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Return an approximation. The returned {@link DocIdSetIterator} is a    *  superset of the matching documents, and each match needs to be confirmed    *  with {@link #matches()} in order to know whether it matches or not. */
DECL|method|approximation
specifier|public
specifier|abstract
name|DocIdSetIterator
name|approximation
parameter_list|()
function_decl|;
comment|/** Return whether the current doc ID that the iterator is on matches. This    *  method should only be called when the iterator is positionned, ie. not    *  when {@link DocIdSetIterator#docID()} is {@code -1} or    *  {@link DocIdSetIterator#NO_MORE_DOCS}. */
DECL|method|matches
specifier|public
specifier|abstract
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

