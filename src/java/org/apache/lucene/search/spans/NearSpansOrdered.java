begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** A Spans that is formed from the ordered subspans of a SpanNearQuery  * where the subspans do not overlap and have a maximum slop between them.  *<p>  * The formed spans only contains minimum slop matches.<br>  * The matching slop is computed from the distance(s) between  * the non overlapping matching Spans.<br>  * Successive matches are always formed from the successive Spans  * of the SpanNearQuery.  *<p>  * The formed spans may contain overlaps when the slop is at least 1.  * For example, when querying using  *<pre>t1 t2 t3</pre>  * with slop at least 1, the fragment:  *<pre>t1 t2 t1 t3 t2 t3</pre>  * matches twice:  *<pre>t1 t2 .. t3</pre>  *<pre>      t1 .. t2 t3</pre>  */
end_comment

begin_class
DECL|class|NearSpansOrdered
class|class
name|NearSpansOrdered
implements|implements
name|Spans
block|{
DECL|field|allowedSlop
specifier|private
specifier|final
name|int
name|allowedSlop
decl_stmt|;
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
DECL|field|more
specifier|private
name|boolean
name|more
init|=
literal|false
decl_stmt|;
comment|/** The spans in the same order as the SpanNearQuery */
DECL|field|subSpans
specifier|private
specifier|final
name|Spans
index|[]
name|subSpans
decl_stmt|;
comment|/** Indicates that all subSpans have same doc() */
DECL|field|inSameDoc
specifier|private
name|boolean
name|inSameDoc
init|=
literal|false
decl_stmt|;
DECL|field|matchDoc
specifier|private
name|int
name|matchDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchStart
specifier|private
name|int
name|matchStart
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchEnd
specifier|private
name|int
name|matchEnd
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|matchPayload
specifier|private
name|List
comment|/*<byte[]>*/
name|matchPayload
decl_stmt|;
DECL|field|subSpansByDoc
specifier|private
specifier|final
name|Spans
index|[]
name|subSpansByDoc
decl_stmt|;
DECL|field|spanDocComparator
specifier|private
specifier|final
name|Comparator
name|spanDocComparator
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Spans
operator|)
name|o1
operator|)
operator|.
name|doc
argument_list|()
operator|-
operator|(
operator|(
name|Spans
operator|)
name|o2
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|query
specifier|private
name|SpanNearQuery
name|query
decl_stmt|;
DECL|field|collectPayloads
specifier|private
name|boolean
name|collectPayloads
init|=
literal|true
decl_stmt|;
DECL|method|NearSpansOrdered
specifier|public
name|NearSpansOrdered
parameter_list|(
name|SpanNearQuery
name|spanNearQuery
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|spanNearQuery
argument_list|,
name|reader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|NearSpansOrdered
specifier|public
name|NearSpansOrdered
parameter_list|(
name|SpanNearQuery
name|spanNearQuery
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|collectPayloads
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|spanNearQuery
operator|.
name|getClauses
argument_list|()
operator|.
name|length
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Less than 2 clauses: "
operator|+
name|spanNearQuery
argument_list|)
throw|;
block|}
name|this
operator|.
name|collectPayloads
operator|=
name|collectPayloads
expr_stmt|;
name|allowedSlop
operator|=
name|spanNearQuery
operator|.
name|getSlop
argument_list|()
expr_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
name|spanNearQuery
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|subSpans
operator|=
operator|new
name|Spans
index|[
name|clauses
operator|.
name|length
index|]
expr_stmt|;
name|matchPayload
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|subSpansByDoc
operator|=
operator|new
name|Spans
index|[
name|clauses
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subSpans
index|[
name|i
index|]
operator|=
name|clauses
index|[
name|i
index|]
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|subSpansByDoc
index|[
name|i
index|]
operator|=
name|subSpans
index|[
name|i
index|]
expr_stmt|;
comment|// used in toSameDoc()
block|}
name|query
operator|=
name|spanNearQuery
expr_stmt|;
comment|// kept for toString() only.
block|}
comment|// inherit javadocs
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|matchDoc
return|;
block|}
comment|// inherit javadocs
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|matchStart
return|;
block|}
comment|// inherit javadocs
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|matchEnd
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
comment|// TODO: Would be nice to be able to lazy load payloads
DECL|method|getPayload
specifier|public
name|Collection
comment|/*<byte[]>*/
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|matchPayload
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
name|matchPayload
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
return|;
block|}
comment|// inherit javadocs
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subSpans
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|subSpans
index|[
name|i
index|]
operator|.
name|next
argument_list|()
condition|)
block|{
name|more
operator|=
literal|false
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|more
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|collectPayloads
condition|)
block|{
name|matchPayload
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|advanceAfterOrdered
argument_list|()
return|;
block|}
comment|// inherit javadocs
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|firstTime
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subSpans
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|subSpans
index|[
name|i
index|]
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|more
operator|=
literal|false
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|more
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|more
operator|&&
operator|(
name|subSpans
index|[
literal|0
index|]
operator|.
name|doc
argument_list|()
operator|<
name|target
operator|)
condition|)
block|{
if|if
condition|(
name|subSpans
index|[
literal|0
index|]
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|inSameDoc
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|more
operator|=
literal|false
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|collectPayloads
condition|)
block|{
name|matchPayload
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|advanceAfterOrdered
argument_list|()
return|;
block|}
comment|/** Advances the subSpans to just after an ordered match with a minimum slop    * that is smaller than the slop allowed by the SpanNearQuery.    * @return true iff there is such a match.    */
DECL|method|advanceAfterOrdered
specifier|private
name|boolean
name|advanceAfterOrdered
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|more
operator|&&
operator|(
name|inSameDoc
operator|||
name|toSameDoc
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|stretchToOrder
argument_list|()
operator|&&
name|shrinkToAfterShortestMatch
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
comment|// no more matches
block|}
comment|/** Advance the subSpans to the same document */
DECL|method|toSameDoc
specifier|private
name|boolean
name|toSameDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|subSpansByDoc
argument_list|,
name|spanDocComparator
argument_list|)
expr_stmt|;
name|int
name|firstIndex
init|=
literal|0
decl_stmt|;
name|int
name|maxDoc
init|=
name|subSpansByDoc
index|[
name|subSpansByDoc
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|doc
argument_list|()
decl_stmt|;
while|while
condition|(
name|subSpansByDoc
index|[
name|firstIndex
index|]
operator|.
name|doc
argument_list|()
operator|!=
name|maxDoc
condition|)
block|{
if|if
condition|(
operator|!
name|subSpansByDoc
index|[
name|firstIndex
index|]
operator|.
name|skipTo
argument_list|(
name|maxDoc
argument_list|)
condition|)
block|{
name|more
operator|=
literal|false
expr_stmt|;
name|inSameDoc
operator|=
literal|false
expr_stmt|;
return|return
literal|false
return|;
block|}
name|maxDoc
operator|=
name|subSpansByDoc
index|[
name|firstIndex
index|]
operator|.
name|doc
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|firstIndex
operator|==
name|subSpansByDoc
operator|.
name|length
condition|)
block|{
name|firstIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subSpansByDoc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
operator|(
name|subSpansByDoc
index|[
name|i
index|]
operator|.
name|doc
argument_list|()
operator|==
name|maxDoc
operator|)
operator|:
literal|" NearSpansOrdered.toSameDoc() spans "
operator|+
name|subSpansByDoc
index|[
literal|0
index|]
operator|+
literal|"\n at doc "
operator|+
name|subSpansByDoc
index|[
name|i
index|]
operator|.
name|doc
argument_list|()
operator|+
literal|", but should be at "
operator|+
name|maxDoc
assert|;
block|}
name|inSameDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Check whether two Spans in the same document are ordered.    * @param spans1     * @param spans2     * @return true iff spans1 starts before spans2    *              or the spans start at the same position,    *              and spans1 ends before spans2.    */
DECL|method|docSpansOrdered
specifier|static
specifier|final
name|boolean
name|docSpansOrdered
parameter_list|(
name|Spans
name|spans1
parameter_list|,
name|Spans
name|spans2
parameter_list|)
block|{
assert|assert
name|spans1
operator|.
name|doc
argument_list|()
operator|==
name|spans2
operator|.
name|doc
argument_list|()
operator|:
literal|"doc1 "
operator|+
name|spans1
operator|.
name|doc
argument_list|()
operator|+
literal|" != doc2 "
operator|+
name|spans2
operator|.
name|doc
argument_list|()
assert|;
name|int
name|start1
init|=
name|spans1
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|start2
init|=
name|spans2
operator|.
name|start
argument_list|()
decl_stmt|;
comment|/* Do not call docSpansOrdered(int,int,int,int) to avoid invoking .end() : */
return|return
operator|(
name|start1
operator|==
name|start2
operator|)
condition|?
operator|(
name|spans1
operator|.
name|end
argument_list|()
operator|<
name|spans2
operator|.
name|end
argument_list|()
operator|)
else|:
operator|(
name|start1
operator|<
name|start2
operator|)
return|;
block|}
comment|/** Like {@link #docSpansOrdered(Spans,Spans)}, but use the spans    * starts and ends as parameters.    */
DECL|method|docSpansOrdered
specifier|private
specifier|static
specifier|final
name|boolean
name|docSpansOrdered
parameter_list|(
name|int
name|start1
parameter_list|,
name|int
name|end1
parameter_list|,
name|int
name|start2
parameter_list|,
name|int
name|end2
parameter_list|)
block|{
return|return
operator|(
name|start1
operator|==
name|start2
operator|)
condition|?
operator|(
name|end1
operator|<
name|end2
operator|)
else|:
operator|(
name|start1
operator|<
name|start2
operator|)
return|;
block|}
comment|/** Order the subSpans within the same document by advancing all later spans    * after the previous one.    */
DECL|method|stretchToOrder
specifier|private
name|boolean
name|stretchToOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|matchDoc
operator|=
name|subSpans
index|[
literal|0
index|]
operator|.
name|doc
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|inSameDoc
operator|&&
operator|(
name|i
operator|<
name|subSpans
operator|.
name|length
operator|)
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
operator|!
name|docSpansOrdered
argument_list|(
name|subSpans
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|subSpans
index|[
name|i
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|subSpans
index|[
name|i
index|]
operator|.
name|next
argument_list|()
condition|)
block|{
name|inSameDoc
operator|=
literal|false
expr_stmt|;
name|more
operator|=
literal|false
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|matchDoc
operator|!=
name|subSpans
index|[
name|i
index|]
operator|.
name|doc
argument_list|()
condition|)
block|{
name|inSameDoc
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|inSameDoc
return|;
block|}
comment|/** The subSpans are ordered in the same doc, so there is a possible match.    * Compute the slop while making the match as short as possible by advancing    * all subSpans except the last one in reverse order.    */
DECL|method|shrinkToAfterShortestMatch
specifier|private
name|boolean
name|shrinkToAfterShortestMatch
parameter_list|()
throws|throws
name|IOException
block|{
name|matchStart
operator|=
name|subSpans
index|[
name|subSpans
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|matchEnd
operator|=
name|subSpans
index|[
name|subSpans
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|end
argument_list|()
expr_stmt|;
name|Set
name|possibleMatchPayloads
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|subSpans
index|[
name|subSpans
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|possibleMatchPayloads
operator|.
name|addAll
argument_list|(
name|subSpans
index|[
name|subSpans
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
name|possiblePayload
init|=
literal|null
decl_stmt|;
name|int
name|matchSlop
init|=
literal|0
decl_stmt|;
name|int
name|lastStart
init|=
name|matchStart
decl_stmt|;
name|int
name|lastEnd
init|=
name|matchEnd
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|subSpans
operator|.
name|length
operator|-
literal|2
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Spans
name|prevSpans
init|=
name|subSpans
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|collectPayloads
operator|&&
name|prevSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|Collection
name|payload
init|=
name|prevSpans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|possiblePayload
operator|=
operator|new
name|ArrayList
argument_list|(
name|payload
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|possiblePayload
operator|.
name|addAll
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
name|int
name|prevStart
init|=
name|prevSpans
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|prevEnd
init|=
name|prevSpans
operator|.
name|end
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Advance prevSpans until after (lastStart, lastEnd)
if|if
condition|(
operator|!
name|prevSpans
operator|.
name|next
argument_list|()
condition|)
block|{
name|inSameDoc
operator|=
literal|false
expr_stmt|;
name|more
operator|=
literal|false
expr_stmt|;
break|break;
comment|// Check remaining subSpans for final match.
block|}
elseif|else
if|if
condition|(
name|matchDoc
operator|!=
name|prevSpans
operator|.
name|doc
argument_list|()
condition|)
block|{
name|inSameDoc
operator|=
literal|false
expr_stmt|;
comment|// The last subSpans is not advanced here.
break|break;
comment|// Check remaining subSpans for last match in this document.
block|}
else|else
block|{
name|int
name|ppStart
init|=
name|prevSpans
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|ppEnd
init|=
name|prevSpans
operator|.
name|end
argument_list|()
decl_stmt|;
comment|// Cannot avoid invoking .end()
if|if
condition|(
operator|!
name|docSpansOrdered
argument_list|(
name|ppStart
argument_list|,
name|ppEnd
argument_list|,
name|lastStart
argument_list|,
name|lastEnd
argument_list|)
condition|)
block|{
break|break;
comment|// Check remaining subSpans.
block|}
else|else
block|{
comment|// prevSpans still before (lastStart, lastEnd)
name|prevStart
operator|=
name|ppStart
expr_stmt|;
name|prevEnd
operator|=
name|ppEnd
expr_stmt|;
if|if
condition|(
name|collectPayloads
operator|&&
name|prevSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|Collection
name|payload
init|=
name|prevSpans
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|possiblePayload
operator|=
operator|new
name|ArrayList
argument_list|(
name|payload
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|possiblePayload
operator|.
name|addAll
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|collectPayloads
operator|&&
name|possiblePayload
operator|!=
literal|null
condition|)
block|{
name|possibleMatchPayloads
operator|.
name|addAll
argument_list|(
name|possiblePayload
argument_list|)
expr_stmt|;
block|}
assert|assert
name|prevStart
operator|<=
name|matchStart
assert|;
if|if
condition|(
name|matchStart
operator|>
name|prevEnd
condition|)
block|{
comment|// Only non overlapping spans add to slop.
name|matchSlop
operator|+=
operator|(
name|matchStart
operator|-
name|prevEnd
operator|)
expr_stmt|;
block|}
comment|/* Do not break on (matchSlop> allowedSlop) here to make sure        * that subSpans[0] is advanced after the match, if any.        */
name|matchStart
operator|=
name|prevStart
expr_stmt|;
name|lastStart
operator|=
name|prevStart
expr_stmt|;
name|lastEnd
operator|=
name|prevEnd
expr_stmt|;
block|}
name|boolean
name|match
init|=
name|matchSlop
operator|<=
name|allowedSlop
decl_stmt|;
if|if
condition|(
name|collectPayloads
operator|&&
name|match
operator|&&
name|possibleMatchPayloads
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|matchPayload
operator|.
name|addAll
argument_list|(
name|possibleMatchPayloads
argument_list|)
expr_stmt|;
block|}
return|return
name|match
return|;
comment|// ordered and allowed slop
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|")@"
operator|+
operator|(
name|firstTime
condition|?
literal|"START"
else|:
operator|(
name|more
condition|?
operator|(
name|doc
argument_list|()
operator|+
literal|":"
operator|+
name|start
argument_list|()
operator|+
literal|"-"
operator|+
name|end
argument_list|()
operator|)
else|:
literal|"END"
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

