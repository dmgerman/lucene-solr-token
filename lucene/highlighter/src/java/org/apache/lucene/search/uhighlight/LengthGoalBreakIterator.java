begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|CharacterIterator
import|;
end_import

begin_comment
comment|/**  * Wraps another {@link BreakIterator} to skip past breaks that would result in passages that are too  * short.  It's still possible to get a short passage but only at the very end of the input text.  *<p>  * Important: This is not a general purpose {@link BreakIterator}; it's only designed to work in a way  * compatible with the {@link UnifiedHighlighter}.  Some assumptions are checked with Java assertions.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|LengthGoalBreakIterator
specifier|public
class|class
name|LengthGoalBreakIterator
extends|extends
name|BreakIterator
block|{
DECL|field|baseIter
specifier|private
specifier|final
name|BreakIterator
name|baseIter
decl_stmt|;
DECL|field|lengthGoal
specifier|private
specifier|final
name|int
name|lengthGoal
decl_stmt|;
DECL|field|isMinimumLength
specifier|private
specifier|final
name|boolean
name|isMinimumLength
decl_stmt|;
comment|// if false then is "closest to" length
comment|/** Breaks will be at least {@code minLength} apart (to the extent possible). */
DECL|method|createMinLength
specifier|public
specifier|static
name|LengthGoalBreakIterator
name|createMinLength
parameter_list|(
name|BreakIterator
name|baseIter
parameter_list|,
name|int
name|minLength
parameter_list|)
block|{
return|return
operator|new
name|LengthGoalBreakIterator
argument_list|(
name|baseIter
argument_list|,
name|minLength
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Breaks will be on average {@code targetLength} apart; the closest break to this target (before or after)    * is chosen. */
DECL|method|createClosestToLength
specifier|public
specifier|static
name|LengthGoalBreakIterator
name|createClosestToLength
parameter_list|(
name|BreakIterator
name|baseIter
parameter_list|,
name|int
name|targetLength
parameter_list|)
block|{
return|return
operator|new
name|LengthGoalBreakIterator
argument_list|(
name|baseIter
argument_list|,
name|targetLength
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|LengthGoalBreakIterator
specifier|private
name|LengthGoalBreakIterator
parameter_list|(
name|BreakIterator
name|baseIter
parameter_list|,
name|int
name|lengthGoal
parameter_list|,
name|boolean
name|isMinimumLength
parameter_list|)
block|{
name|this
operator|.
name|baseIter
operator|=
name|baseIter
expr_stmt|;
name|this
operator|.
name|lengthGoal
operator|=
name|lengthGoal
expr_stmt|;
name|this
operator|.
name|isMinimumLength
operator|=
name|isMinimumLength
expr_stmt|;
block|}
comment|// note: the only methods that will get called are setText(txt), getText(),
comment|// getSummaryPassagesNoHighlight: current(), first(), next()
comment|// highlightOffsetsEnums: preceding(int), and following(int)
comment|//   Nonetheless we make some attempt to implement the rest; mostly delegating.
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|goalDesc
init|=
name|isMinimumLength
condition|?
literal|"minLen"
else|:
literal|"targetLen"
decl_stmt|;
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
operator|+
name|goalDesc
operator|+
literal|"="
operator|+
name|lengthGoal
operator|+
literal|", baseIter="
operator|+
name|baseIter
operator|+
literal|"}"
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|LengthGoalBreakIterator
argument_list|(
operator|(
name|BreakIterator
operator|)
name|baseIter
operator|.
name|clone
argument_list|()
argument_list|,
name|lengthGoal
argument_list|,
name|isMinimumLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getText
specifier|public
name|CharacterIterator
name|getText
parameter_list|()
block|{
return|return
name|baseIter
operator|.
name|getText
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|newText
parameter_list|)
block|{
name|baseIter
operator|.
name|setText
argument_list|(
name|newText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setText
specifier|public
name|void
name|setText
parameter_list|(
name|CharacterIterator
name|newText
parameter_list|)
block|{
name|baseIter
operator|.
name|setText
argument_list|(
name|newText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|current
specifier|public
name|int
name|current
parameter_list|()
block|{
return|return
name|baseIter
operator|.
name|current
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|first
specifier|public
name|int
name|first
parameter_list|()
block|{
return|return
name|baseIter
operator|.
name|first
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|last
specifier|public
name|int
name|last
parameter_list|()
block|{
return|return
name|baseIter
operator|.
name|last
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|(
name|int
name|n
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"Not supported"
assert|;
return|return
name|baseIter
operator|.
name|next
argument_list|(
name|n
argument_list|)
return|;
comment|// probably wrong
block|}
comment|// called by getSummaryPassagesNoHighlight to generate default summary.
annotation|@
name|Override
DECL|method|next
specifier|public
name|int
name|next
parameter_list|()
block|{
return|return
name|following
argument_list|(
name|current
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|previous
specifier|public
name|int
name|previous
parameter_list|()
block|{
assert|assert
literal|false
operator|:
literal|"Not supported"
assert|;
return|return
name|baseIter
operator|.
name|previous
argument_list|()
return|;
block|}
comment|// called while the current position is the start of a new passage; find end of passage
annotation|@
name|Override
DECL|method|following
specifier|public
name|int
name|following
parameter_list|(
name|int
name|followingIdx
parameter_list|)
block|{
specifier|final
name|int
name|startIdx
init|=
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|followingIdx
operator|<
name|startIdx
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"Not supported"
assert|;
return|return
name|baseIter
operator|.
name|following
argument_list|(
name|followingIdx
argument_list|)
return|;
block|}
specifier|final
name|int
name|targetIdx
init|=
name|startIdx
operator|+
name|lengthGoal
decl_stmt|;
comment|// When followingIdx>= targetIdx, we can simply delegate since it will be>= the target
if|if
condition|(
name|followingIdx
operator|>=
name|targetIdx
operator|-
literal|1
condition|)
block|{
return|return
name|baseIter
operator|.
name|following
argument_list|(
name|followingIdx
argument_list|)
return|;
block|}
comment|// If target exceeds the text length, return the last index.
if|if
condition|(
name|targetIdx
operator|>=
name|getText
argument_list|()
operator|.
name|getEndIndex
argument_list|()
condition|)
block|{
return|return
name|baseIter
operator|.
name|last
argument_list|()
return|;
block|}
comment|// Find closest break>= the target
specifier|final
name|int
name|afterIdx
init|=
name|baseIter
operator|.
name|following
argument_list|(
name|targetIdx
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|afterIdx
operator|==
name|DONE
condition|)
block|{
comment|// we're at the end; can this happen?
return|return
name|current
argument_list|()
return|;
block|}
if|if
condition|(
name|afterIdx
operator|==
name|targetIdx
condition|)
block|{
comment|// right on the money
return|return
name|afterIdx
return|;
block|}
if|if
condition|(
name|isMinimumLength
condition|)
block|{
comment|// thus never undershoot
return|return
name|afterIdx
return|;
block|}
comment|// note: it is a shame that we invoke preceding() *in addition to* following(); BI's are sometimes expensive.
comment|// Find closest break< target
specifier|final
name|int
name|beforeIdx
init|=
name|baseIter
operator|.
name|preceding
argument_list|(
name|targetIdx
argument_list|)
decl_stmt|;
comment|// or could do baseIter.previous() but we hope the BI implements preceding()
if|if
condition|(
name|beforeIdx
operator|<=
name|followingIdx
condition|)
block|{
comment|// too far back
return|return
name|moveToBreak
argument_list|(
name|afterIdx
argument_list|)
return|;
block|}
if|if
condition|(
name|targetIdx
operator|-
name|beforeIdx
operator|<=
name|afterIdx
operator|-
name|targetIdx
condition|)
block|{
return|return
name|beforeIdx
return|;
block|}
return|return
name|moveToBreak
argument_list|(
name|afterIdx
argument_list|)
return|;
block|}
DECL|method|moveToBreak
specifier|private
name|int
name|moveToBreak
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
comment|// precondition: idx is a known break
comment|// bi.isBoundary(idx) has side-effect of moving the position.  Not obvious!
comment|//boolean moved = baseIter.isBoundary(idx); // probably not particularly expensive
comment|//assert moved&& current() == idx;
comment|// TODO fix: Would prefer to do "- 1" instead of "- 2" but CustomSeparatorBreakIterator has a bug.
name|int
name|current
init|=
name|baseIter
operator|.
name|following
argument_list|(
name|idx
operator|-
literal|2
argument_list|)
decl_stmt|;
assert|assert
name|current
operator|==
name|idx
operator|:
literal|"following() didn't move us to the expected index."
assert|;
return|return
name|idx
return|;
block|}
comment|// called at start of new Passage given first word start offset
annotation|@
name|Override
DECL|method|preceding
specifier|public
name|int
name|preceding
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
return|return
name|baseIter
operator|.
name|preceding
argument_list|(
name|offset
argument_list|)
return|;
comment|// no change needed
block|}
annotation|@
name|Override
DECL|method|isBoundary
specifier|public
name|boolean
name|isBoundary
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"Not supported"
assert|;
return|return
name|baseIter
operator|.
name|isBoundary
argument_list|(
name|offset
argument_list|)
return|;
block|}
block|}
end_class

end_unit

