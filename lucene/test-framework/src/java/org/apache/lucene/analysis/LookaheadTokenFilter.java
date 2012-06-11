begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|List
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
name|AttributeSource
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
name|RollingBuffer
import|;
end_import

begin_comment
comment|// TODO: cut SynFilter over to this
end_comment

begin_comment
comment|// TODO: somehow add "nuke this input token" capability...
end_comment

begin_comment
comment|/** An abstract TokenFilter to make it easier to build graph  *  token filters requiring some lookahead.  This class handles  *  the details of buffering up tokens, recording them by  *  position, restoring them, providing access to them, etc. */
end_comment

begin_class
DECL|class|LookaheadTokenFilter
specifier|public
specifier|abstract
class|class
name|LookaheadTokenFilter
parameter_list|<
name|T
extends|extends
name|LookaheadTokenFilter
operator|.
name|Position
parameter_list|>
extends|extends
name|TokenFilter
block|{
DECL|field|DEBUG
specifier|private
specifier|final
specifier|static
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|field|posIncAtt
specifier|protected
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLenAtt
specifier|protected
specifier|final
name|PositionLengthAttribute
name|posLenAtt
init|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|protected
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Position of last read input token:
DECL|field|inputPos
specifier|protected
name|int
name|inputPos
decl_stmt|;
comment|// Position of next possible output token to return:
DECL|field|outputPos
specifier|protected
name|int
name|outputPos
decl_stmt|;
comment|// True if we hit end from our input:
DECL|field|end
specifier|protected
name|boolean
name|end
decl_stmt|;
DECL|field|tokenPending
specifier|private
name|boolean
name|tokenPending
decl_stmt|;
DECL|field|insertPending
specifier|private
name|boolean
name|insertPending
decl_stmt|;
comment|/** Holds all state for a single position; subclass this    *  to record other state at each position. */
DECL|class|Position
specifier|protected
specifier|static
class|class
name|Position
implements|implements
name|RollingBuffer
operator|.
name|Resettable
block|{
comment|// Buffered input tokens at this position:
DECL|field|inputTokens
specifier|public
specifier|final
name|List
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|inputTokens
init|=
operator|new
name|ArrayList
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
argument_list|()
decl_stmt|;
comment|// Next buffered token to be returned to consumer:
DECL|field|nextRead
specifier|public
name|int
name|nextRead
decl_stmt|;
comment|// Any token leaving from this position should have this startOffset:
DECL|field|startOffset
specifier|public
name|int
name|startOffset
init|=
operator|-
literal|1
decl_stmt|;
comment|// Any token arriving to this position should have this endOffset:
DECL|field|endOffset
specifier|public
name|int
name|endOffset
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|inputTokens
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nextRead
operator|=
literal|0
expr_stmt|;
name|startOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|endOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|AttributeSource
operator|.
name|State
name|state
parameter_list|)
block|{
name|inputTokens
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|nextState
specifier|public
name|AttributeSource
operator|.
name|State
name|nextState
parameter_list|()
block|{
assert|assert
name|nextRead
operator|<
name|inputTokens
operator|.
name|size
argument_list|()
assert|;
return|return
name|inputTokens
operator|.
name|get
argument_list|(
name|nextRead
operator|++
argument_list|)
return|;
block|}
block|}
DECL|method|LookaheadTokenFilter
specifier|protected
name|LookaheadTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Call this only from within afterPosition, to insert a new    *  token.  After calling this you should set any    *  necessary token you need. */
DECL|method|insertToken
specifier|protected
name|void
name|insertToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokenPending
condition|)
block|{
name|positions
operator|.
name|get
argument_list|(
name|inputPos
argument_list|)
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
name|tokenPending
operator|=
literal|false
expr_stmt|;
block|}
assert|assert
operator|!
name|insertPending
assert|;
name|insertPending
operator|=
literal|true
expr_stmt|;
block|}
comment|/** This is called when all input tokens leaving a given    *  position have been returned.  Override this and    *  call createToken and then set whichever token's    *  attributes you want, if you want to inject    *  a token starting from this position. */
DECL|method|afterPosition
specifier|protected
name|void
name|afterPosition
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|newPosition
specifier|protected
specifier|abstract
name|T
name|newPosition
parameter_list|()
function_decl|;
DECL|field|positions
specifier|protected
specifier|final
name|RollingBuffer
argument_list|<
name|T
argument_list|>
name|positions
init|=
operator|new
name|RollingBuffer
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|T
name|newInstance
parameter_list|()
block|{
return|return
name|newPosition
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Returns true if there is a new token. */
DECL|method|peekToken
specifier|protected
name|boolean
name|peekToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"LTF.peekToken inputPos="
operator|+
name|inputPos
operator|+
literal|" outputPos="
operator|+
name|outputPos
operator|+
literal|" tokenPending="
operator|+
name|tokenPending
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|!
name|end
assert|;
assert|assert
name|inputPos
operator|==
operator|-
literal|1
operator|||
name|outputPos
operator|<=
name|inputPos
assert|;
if|if
condition|(
name|tokenPending
condition|)
block|{
name|positions
operator|.
name|get
argument_list|(
name|inputPos
argument_list|)
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
name|tokenPending
operator|=
literal|false
expr_stmt|;
block|}
specifier|final
name|boolean
name|gotToken
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  input.incrToken() returned "
operator|+
name|gotToken
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gotToken
condition|)
block|{
name|inputPos
operator|+=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
assert|assert
name|inputPos
operator|>=
literal|0
assert|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  now inputPos="
operator|+
name|inputPos
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Position
name|startPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|inputPos
argument_list|)
decl_stmt|;
specifier|final
name|Position
name|endPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|inputPos
operator|+
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|startOffset
init|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|startPosData
operator|.
name|startOffset
operator|==
operator|-
literal|1
condition|)
block|{
name|startPosData
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
block|}
else|else
block|{
comment|// Make sure our input isn't messing up offsets:
assert|assert
name|startPosData
operator|.
name|startOffset
operator|==
name|startOffset
operator|:
literal|"prev startOffset="
operator|+
name|startPosData
operator|.
name|startOffset
operator|+
literal|" vs new startOffset="
operator|+
name|startOffset
operator|+
literal|" inputPos="
operator|+
name|inputPos
assert|;
block|}
specifier|final
name|int
name|endOffset
init|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|endPosData
operator|.
name|endOffset
operator|==
operator|-
literal|1
condition|)
block|{
name|endPosData
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
else|else
block|{
comment|// Make sure our input isn't messing up offsets:
assert|assert
name|endPosData
operator|.
name|endOffset
operator|==
name|endOffset
operator|:
literal|"prev endOffset="
operator|+
name|endPosData
operator|.
name|endOffset
operator|+
literal|" vs new endOffset="
operator|+
name|endOffset
operator|+
literal|" inputPos="
operator|+
name|inputPos
assert|;
block|}
name|tokenPending
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|gotToken
return|;
block|}
comment|/** Call this when you are done looking ahead; it will set    *  the next token to return.  Return the boolean back to    *  the caller. */
DECL|method|nextToken
specifier|protected
name|boolean
name|nextToken
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("  nextToken: tokenPending=" + tokenPending);
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"LTF.nextToken inputPos="
operator|+
name|inputPos
operator|+
literal|" outputPos="
operator|+
name|outputPos
operator|+
literal|" tokenPending="
operator|+
name|tokenPending
argument_list|)
expr_stmt|;
block|}
name|Position
name|posData
init|=
name|positions
operator|.
name|get
argument_list|(
name|outputPos
argument_list|)
decl_stmt|;
comment|// While loop here in case we have to
comment|// skip over a hole from the input:
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("    check buffer @ outputPos=" +
comment|//outputPos + " inputPos=" + inputPos + " nextRead=" +
comment|//posData.nextRead + " vs size=" +
comment|//posData.inputTokens.size());
comment|// See if we have a previously buffered token to
comment|// return at the current position:
if|if
condition|(
name|posData
operator|.
name|nextRead
operator|<
name|posData
operator|.
name|inputTokens
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  return previously buffered token"
argument_list|)
expr_stmt|;
block|}
comment|// This position has buffered tokens to serve up:
if|if
condition|(
name|tokenPending
condition|)
block|{
name|positions
operator|.
name|get
argument_list|(
name|inputPos
argument_list|)
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
name|tokenPending
operator|=
literal|false
expr_stmt|;
block|}
name|restoreState
argument_list|(
name|positions
operator|.
name|get
argument_list|(
name|outputPos
argument_list|)
operator|.
name|nextState
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("      return!");
return|return
literal|true
return|;
block|}
if|if
condition|(
name|inputPos
operator|==
operator|-
literal|1
operator|||
name|outputPos
operator|==
name|inputPos
condition|)
block|{
comment|// No more buffered tokens:
comment|// We may still get input tokens at this position
comment|//System.out.println("    break buffer");
if|if
condition|(
name|tokenPending
condition|)
block|{
comment|// Fast path: just return token we had just incr'd,
comment|// without having captured/restored its state:
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  pass-through: return pending token"
argument_list|)
expr_stmt|;
block|}
name|tokenPending
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|end
operator|||
operator|!
name|peekToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  END"
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|posData
operator|.
name|startOffset
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// This position had at least one token leaving
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  call afterPosition"
argument_list|)
expr_stmt|;
block|}
name|afterPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|insertPending
condition|)
block|{
comment|// Subclass inserted a token at this same
comment|// position:
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  return inserted token"
argument_list|)
expr_stmt|;
block|}
assert|assert
name|insertedTokenConsistent
argument_list|()
assert|;
name|insertPending
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|// Done with this position; move on:
name|outputPos
operator|++
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  next position: outputPos="
operator|+
name|outputPos
argument_list|)
expr_stmt|;
block|}
name|positions
operator|.
name|freeBefore
argument_list|(
name|outputPos
argument_list|)
expr_stmt|;
name|posData
operator|=
name|positions
operator|.
name|get
argument_list|(
name|outputPos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// If subclass inserted a token, make sure it had in fact
comment|// looked ahead enough:
DECL|method|insertedTokenConsistent
specifier|private
name|boolean
name|insertedTokenConsistent
parameter_list|()
block|{
specifier|final
name|int
name|posLen
init|=
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
decl_stmt|;
specifier|final
name|Position
name|endPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|outputPos
operator|+
name|posLen
argument_list|)
decl_stmt|;
assert|assert
name|endPosData
operator|.
name|endOffset
operator|!=
operator|-
literal|1
assert|;
assert|assert
name|offsetAtt
operator|.
name|endOffset
argument_list|()
operator|==
name|endPosData
operator|.
name|endOffset
assert|;
return|return
literal|true
return|;
block|}
comment|// TODO: end()?
comment|// TODO: close()?
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|positions
operator|.
name|reset
argument_list|()
expr_stmt|;
name|inputPos
operator|=
operator|-
literal|1
expr_stmt|;
name|outputPos
operator|=
literal|0
expr_stmt|;
name|tokenPending
operator|=
literal|false
expr_stmt|;
name|end
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

