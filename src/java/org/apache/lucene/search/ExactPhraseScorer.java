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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|*
import|;
end_import

begin_class
DECL|class|ExactPhraseScorer
specifier|final
class|class
name|ExactPhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|method|ExactPhraseScorer
name|ExactPhraseScorer
parameter_list|(
name|QueryWeight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|tps
argument_list|,
name|offsets
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
block|}
DECL|method|phraseFreq
specifier|protected
specifier|final
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// sort list with pq
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// build pq from list
block|}
name|pqToList
argument_list|()
expr_stmt|;
comment|// rebuild list from pq
comment|// for counting how many times the exact phrase is found in current document,
comment|// just count how many times all PhrasePosition's have exactly the same position.
name|int
name|freq
init|=
literal|0
decl_stmt|;
do|do
block|{
comment|// find position w/ all terms
while|while
condition|(
name|first
operator|.
name|position
operator|<
name|last
operator|.
name|position
condition|)
block|{
comment|// scan forward in first
do|do
block|{
if|if
condition|(
operator|!
name|first
operator|.
name|nextPosition
argument_list|()
condition|)
return|return
name|freq
return|;
block|}
do|while
condition|(
name|first
operator|.
name|position
operator|<
name|last
operator|.
name|position
condition|)
do|;
name|firstToLast
argument_list|()
expr_stmt|;
block|}
name|freq
operator|++
expr_stmt|;
comment|// all equal: a match
block|}
do|while
condition|(
name|last
operator|.
name|nextPosition
argument_list|()
condition|)
do|;
return|return
name|freq
return|;
block|}
block|}
end_class

end_unit

