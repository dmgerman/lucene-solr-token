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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TermPositions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|SloppyPhraseScorer
specifier|final
class|class
name|SloppyPhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
DECL|method|SloppyPhraseScorer
name|SloppyPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|int
index|[]
name|positions
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|int
name|slop
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
name|positions
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
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
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|end
init|=
literal|0
decl_stmt|;
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
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
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
name|float
name|freq
init|=
literal|0.0f
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
do|do
block|{
name|PhrasePositions
name|pp
init|=
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|pp
operator|.
name|position
decl_stmt|;
name|int
name|next
init|=
operator|(
operator|(
name|PhrasePositions
operator|)
name|pq
operator|.
name|top
argument_list|()
operator|)
operator|.
name|position
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
name|start
init|;
name|pos
operator|<=
name|next
condition|;
name|pos
operator|=
name|pp
operator|.
name|position
control|)
block|{
name|start
operator|=
name|pos
expr_stmt|;
comment|// advance pp to min window
if|if
condition|(
operator|!
name|pp
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
comment|// ran out of a term -- done
break|break;
block|}
block|}
name|int
name|matchLength
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|matchLength
operator|<=
name|slop
condition|)
name|freq
operator|+=
name|getSimilarity
argument_list|()
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
comment|// score match
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|put
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// restore pq
block|}
do|while
condition|(
operator|!
name|done
condition|)
do|;
return|return
name|freq
return|;
block|}
block|}
end_class

end_unit

