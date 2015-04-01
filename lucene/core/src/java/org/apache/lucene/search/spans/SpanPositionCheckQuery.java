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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
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
name|Term
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
name|TermContext
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
name|search
operator|.
name|Query
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
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Base class for filtering a SpanQuery based on the position of a match.  **/
end_comment

begin_class
DECL|class|SpanPositionCheckQuery
specifier|public
specifier|abstract
class|class
name|SpanPositionCheckQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|match
specifier|protected
name|SpanQuery
name|match
decl_stmt|;
DECL|method|SpanPositionCheckQuery
specifier|public
name|SpanPositionCheckQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the SpanQuery whose matches are filtered.    *    * */
DECL|method|getMatch
specifier|public
name|SpanQuery
name|getMatch
parameter_list|()
block|{
return|return
name|match
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|match
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|match
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return value for {@link SpanPositionCheckQuery#acceptPosition(Spans)}.    */
DECL|enum|AcceptStatus
specifier|protected
specifier|static
enum|enum
name|AcceptStatus
block|{
comment|/** Indicates the match should be accepted */
DECL|enum constant|YES
name|YES
block|,
comment|/** Indicates the match should be rejected */
DECL|enum constant|NO
name|NO
block|,
comment|/**      * Indicates the match should be rejected, and the enumeration may continue      * with the next document.      */
DECL|enum constant|NO_MORE_IN_CURRENT_DOC
name|NO_MORE_IN_CURRENT_DOC
block|}
empty_stmt|;
comment|/**    * Implementing classes are required to return whether the current position is a match for the passed in    * "match" {@link SpanQuery}.    *    * This is only called if the underlying last {@link Spans#nextStartPosition()} for the    * match indicated a valid start position.    *    *    * @param spans The {@link Spans} instance, positioned at the spot to check    *    * @return whether the match is accepted, rejected, or rejected and should move to the next doc.    *    * @see Spans#nextDoc()    *    */
DECL|method|acceptPosition
specifier|protected
specifier|abstract
name|AcceptStatus
name|acceptPosition
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
name|Spans
name|matchSpans
init|=
name|match
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
decl_stmt|;
return|return
operator|(
name|matchSpans
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|PositionCheckSpans
argument_list|(
name|matchSpans
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanPositionCheckQuery
name|clone
init|=
literal|null
decl_stmt|;
name|SpanQuery
name|rewritten
init|=
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|match
condition|)
block|{
name|clone
operator|=
operator|(
name|SpanPositionCheckQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|match
operator|=
name|rewritten
expr_stmt|;
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
DECL|class|PositionCheckSpans
specifier|protected
class|class
name|PositionCheckSpans
extends|extends
name|FilterSpans
block|{
DECL|field|atFirstInCurrentDoc
specifier|private
name|boolean
name|atFirstInCurrentDoc
init|=
literal|false
decl_stmt|;
DECL|field|startPos
specifier|private
name|int
name|startPos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|PositionCheckSpans
specifier|public
name|PositionCheckSpans
parameter_list|(
name|Spans
name|matchSpans
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|matchSpans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
return|return
name|toNextDocWithAllowedPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
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
if|if
condition|(
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
return|return
name|toNextDocWithAllowedPosition
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|toNextDocWithAllowedPosition
specifier|protected
name|int
name|toNextDocWithAllowedPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|startPos
operator|!=
name|NO_MORE_POSITIONS
assert|;
for|for
control|(
init|;
condition|;
control|)
block|{
switch|switch
condition|(
name|acceptPosition
argument_list|(
name|this
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
case|case
name|NO
case|:
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|startPos
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
break|break;
block|}
comment|// else fallthrough
case|case
name|NO_MORE_IN_CURRENT_DOC
case|:
if|if
condition|(
name|in
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|startPos
operator|=
operator|-
literal|1
expr_stmt|;
return|return
name|NO_MORE_DOCS
return|;
block|}
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|startPos
operator|!=
name|NO_MORE_POSITIONS
operator|:
literal|"no start position at doc="
operator|+
name|in
operator|.
name|docID
argument_list|()
assert|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|nextStartPosition
specifier|public
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|atFirstInCurrentDoc
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|false
expr_stmt|;
return|return
name|startPos
return|;
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|startPos
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
return|return
name|NO_MORE_POSITIONS
return|;
block|}
switch|switch
condition|(
name|acceptPosition
argument_list|(
name|this
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
return|return
name|startPos
return|;
case|case
name|NO
case|:
break|break;
case|case
name|NO_MORE_IN_CURRENT_DOC
case|:
return|return
name|startPos
operator|=
name|NO_MORE_POSITIONS
return|;
comment|// startPos ahead for the current doc.
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|startPos
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
operator|(
name|startPos
operator|!=
name|NO_MORE_POSITIONS
operator|)
condition|?
name|in
operator|.
name|endPosition
argument_list|()
else|:
name|NO_MORE_POSITIONS
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
literal|"spans("
operator|+
name|SpanPositionCheckQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanPositionCheckQuery
name|spcq
init|=
operator|(
name|SpanPositionCheckQuery
operator|)
name|o
decl_stmt|;
return|return
name|match
operator|.
name|equals
argument_list|(
name|spcq
operator|.
name|match
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
name|match
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

