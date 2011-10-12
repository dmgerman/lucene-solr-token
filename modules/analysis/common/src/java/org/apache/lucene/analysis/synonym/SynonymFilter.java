begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|analysis
operator|.
name|TokenFilter
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
name|TokenStream
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
name|CharTermAttribute
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
name|TypeAttribute
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
name|store
operator|.
name|ByteArrayDataInput
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
name|ArrayUtil
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
name|CharsRef
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
name|RamUsageEstimator
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
name|UnicodeUtil
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
name|fst
operator|.
name|FST
import|;
end_import

begin_comment
comment|/**  * Matches single or multi word synonyms in a token stream.  * This token stream cannot properly handle position  * increments != 1, ie, you should place this filter before  * filtering out stop words.  *   *<p>Note that with the current implementation, parsing is  * greedy, so whenever multiple parses would apply, the rule  * starting the earliest and parsing the most tokens wins.  * For example if you have these rules:  *        *<pre>  *   a -> x  *   a b -> y  *   b c d -> z  *</pre>  *  * Then input<code>a b c d e</code> parses to<code>y b c  * d</code>, ie the 2nd rule "wins" because it started  * earliest and matched the most input tokens of other rules  * starting at that point.</p>  *  *<p>A future improvement to this filter could allow  * non-greedy parsing, such that the 3rd rule would win, and  * also separately allow multiple parses, such that all 3  * rules would match, perhaps even on a rule by rule  * basis.</p>  *  *<p><b>NOTE</b>: when a match occurs, the output tokens  * associated with the matching rule are "stacked" on top of  * the input stream (if the rule had  *<code>keepOrig=true</code>) and also on top of another  * matched rule's output tokens.  This is not a correct  * solution, as really the output should be an arbitrary  * graph/lattice.  For example, with the above match, you  * would expect an exact<code>PhraseQuery</code><code>"y b  * c"</code> to match the parsed tokens, but it will fail to  * do so.  This limitation is necessary because Lucene's  * TokenStream (and index) cannot yet represent an arbitrary  * graph.</p>  *  *<p><b>NOTE</b>: If multiple incoming tokens arrive on the  * same position, only the first token at that position is  * used for parsing.  Subsequent tokens simply pass through  * and are not parsed.  A future improvement would be to  * allow these tokens to also be matched.</p>  */
end_comment

begin_comment
comment|// TODO: maybe we should resolve token -> wordID then run
end_comment

begin_comment
comment|// FST on wordIDs, for better perf?
end_comment

begin_comment
comment|// TODO: a more efficient approach would be Aho/Corasick's
end_comment

begin_comment
comment|// algorithm
end_comment

begin_comment
comment|// http://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_string_matching_algorithm
end_comment

begin_comment
comment|// It improves over the current approach here
end_comment

begin_comment
comment|// because it does not fully re-start matching at every
end_comment

begin_comment
comment|// token.  For example if one pattern is "a b c x"
end_comment

begin_comment
comment|// and another is "b c d" and the input is "a b c d", on
end_comment

begin_comment
comment|// trying to parse "a b c x" but failing when you got to x,
end_comment

begin_comment
comment|// rather than starting over again your really should
end_comment

begin_comment
comment|// immediately recognize that "b c d" matches at the next
end_comment

begin_comment
comment|// input.  I suspect this won't matter that much in
end_comment

begin_comment
comment|// practice, but it's possible on some set of synonyms it
end_comment

begin_comment
comment|// will.  We'd have to modify Aho/Corasick to enforce our
end_comment

begin_comment
comment|// conflict resolving (eg greedy matching) because that algo
end_comment

begin_comment
comment|// finds all matches.
end_comment

begin_class
DECL|class|SynonymFilter
specifier|public
specifier|final
class|class
name|SynonymFilter
extends|extends
name|TokenFilter
block|{
DECL|field|TYPE_SYNONYM
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_SYNONYM
init|=
literal|"SYNONYM"
decl_stmt|;
DECL|field|synonyms
specifier|private
specifier|final
name|SynonymMap
name|synonyms
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|rollBufferSize
specifier|private
specifier|final
name|int
name|rollBufferSize
decl_stmt|;
DECL|field|captureCount
specifier|private
name|int
name|captureCount
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
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
comment|// How many future input tokens have already been matched
comment|// to a synonym; because the matching is "greedy" we don't
comment|// try to do any more matching for such tokens:
DECL|field|inputSkipCount
specifier|private
name|int
name|inputSkipCount
decl_stmt|;
comment|// Hold all buffered (read ahead) stacked input tokens for
comment|// a future position.  When multiple tokens are at the
comment|// same position, we only store (and match against) the
comment|// term for the first token at the position, but capture
comment|// state for (and enumerate) all other tokens at this
comment|// position:
DECL|class|PendingInput
specifier|private
specifier|static
class|class
name|PendingInput
block|{
DECL|field|term
specifier|final
name|CharsRef
name|term
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
DECL|field|state
name|AttributeSource
operator|.
name|State
name|state
decl_stmt|;
DECL|field|keepOrig
name|boolean
name|keepOrig
decl_stmt|;
DECL|field|matched
name|boolean
name|matched
decl_stmt|;
DECL|field|consumed
name|boolean
name|consumed
init|=
literal|true
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|state
operator|=
literal|null
expr_stmt|;
name|consumed
operator|=
literal|true
expr_stmt|;
name|keepOrig
operator|=
literal|false
expr_stmt|;
name|matched
operator|=
literal|false
expr_stmt|;
block|}
block|}
empty_stmt|;
comment|// Rolling buffer, holding pending input tokens we had to
comment|// clone because we needed to look ahead, indexed by
comment|// position:
DECL|field|futureInputs
specifier|private
specifier|final
name|PendingInput
index|[]
name|futureInputs
decl_stmt|;
comment|// Holds pending output synonyms for one future position:
DECL|class|PendingOutputs
specifier|private
specifier|static
class|class
name|PendingOutputs
block|{
DECL|field|outputs
name|CharsRef
index|[]
name|outputs
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|posIncr
name|int
name|posIncr
init|=
literal|1
decl_stmt|;
DECL|method|PendingOutputs
specifier|public
name|PendingOutputs
parameter_list|()
block|{
name|outputs
operator|=
operator|new
name|CharsRef
index|[
literal|1
index|]
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|upto
operator|=
name|count
operator|=
literal|0
expr_stmt|;
name|posIncr
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|pullNext
specifier|public
name|CharsRef
name|pullNext
parameter_list|()
block|{
assert|assert
name|upto
operator|<
name|count
assert|;
specifier|final
name|CharsRef
name|result
init|=
name|outputs
index|[
name|upto
operator|++
index|]
decl_stmt|;
name|posIncr
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|count
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|char
index|[]
name|output
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|count
operator|==
name|outputs
operator|.
name|length
condition|)
block|{
specifier|final
name|CharsRef
index|[]
name|next
init|=
operator|new
name|CharsRef
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|count
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|outputs
argument_list|,
literal|0
argument_list|,
name|next
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|outputs
operator|=
name|next
expr_stmt|;
block|}
if|if
condition|(
name|outputs
index|[
name|count
index|]
operator|==
literal|null
condition|)
block|{
name|outputs
index|[
name|count
index|]
operator|=
operator|new
name|CharsRef
argument_list|()
expr_stmt|;
block|}
name|outputs
index|[
name|count
index|]
operator|.
name|copy
argument_list|(
name|output
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|field|bytesReader
specifier|private
specifier|final
name|ByteArrayDataInput
name|bytesReader
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
comment|// Rolling buffer, holding stack of pending synonym
comment|// outputs, indexed by position:
DECL|field|futureOutputs
specifier|private
specifier|final
name|PendingOutputs
index|[]
name|futureOutputs
decl_stmt|;
comment|// Where (in rolling buffers) to write next input saved state:
DECL|field|nextWrite
specifier|private
name|int
name|nextWrite
decl_stmt|;
comment|// Where (in rolling buffers) to read next input saved state:
DECL|field|nextRead
specifier|private
name|int
name|nextRead
decl_stmt|;
comment|// True once we've read last token
DECL|field|finished
specifier|private
name|boolean
name|finished
decl_stmt|;
DECL|field|scratchArc
specifier|private
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
name|scratchArc
decl_stmt|;
DECL|field|fst
specifier|private
specifier|final
name|FST
argument_list|<
name|BytesRef
argument_list|>
name|fst
decl_stmt|;
DECL|field|scratchBytes
specifier|private
specifier|final
name|BytesRef
name|scratchBytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|scratchChars
specifier|private
specifier|final
name|CharsRef
name|scratchChars
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
comment|/**    * @param input input tokenstream    * @param synonyms synonym map    * @param ignoreCase case-folds input for matching with {@link Character#toLowerCase(int)}.    *                   Note, if you set this to true, its your responsibility to lowercase    *                   the input entries when you create the {@link SynonymMap}    */
DECL|method|SynonymFilter
specifier|public
name|SynonymFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|SynonymMap
name|synonyms
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|synonyms
operator|=
name|synonyms
expr_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|this
operator|.
name|fst
operator|=
name|synonyms
operator|.
name|fst
expr_stmt|;
if|if
condition|(
name|fst
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fst must be non-null"
argument_list|)
throw|;
block|}
comment|// Must be 1+ so that when roll buffer is at full
comment|// lookahead we can distinguish this full buffer from
comment|// the empty buffer:
name|rollBufferSize
operator|=
literal|1
operator|+
name|synonyms
operator|.
name|maxHorizontalContext
expr_stmt|;
name|futureInputs
operator|=
operator|new
name|PendingInput
index|[
name|rollBufferSize
index|]
expr_stmt|;
name|futureOutputs
operator|=
operator|new
name|PendingOutputs
index|[
name|rollBufferSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|rollBufferSize
condition|;
name|pos
operator|++
control|)
block|{
name|futureInputs
index|[
name|pos
index|]
operator|=
operator|new
name|PendingInput
argument_list|()
expr_stmt|;
name|futureOutputs
index|[
name|pos
index|]
operator|=
operator|new
name|PendingOutputs
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("FSTFilt maxH=" + synonyms.maxHorizontalContext);
name|scratchArc
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|capture
specifier|private
name|void
name|capture
parameter_list|()
block|{
name|captureCount
operator|++
expr_stmt|;
comment|//System.out.println("  capture slot=" + nextWrite);
specifier|final
name|PendingInput
name|input
init|=
name|futureInputs
index|[
name|nextWrite
index|]
decl_stmt|;
name|input
operator|.
name|state
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|input
operator|.
name|consumed
operator|=
literal|false
expr_stmt|;
name|input
operator|.
name|term
operator|.
name|copy
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|nextWrite
operator|=
name|rollIncr
argument_list|(
name|nextWrite
argument_list|)
expr_stmt|;
comment|// Buffer head should never catch up to tail:
assert|assert
name|nextWrite
operator|!=
name|nextRead
assert|;
block|}
comment|/*    This is the core of this TokenFilter: it locates the    synonym matches and buffers up the results into    futureInputs/Outputs.     NOTE: this calls input.incrementToken and does not    capture the state if no further tokens were checked.  So    caller must then forward state to our caller, or capture:   */
DECL|method|parse
specifier|private
name|void
name|parse
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("\nS: parse");
assert|assert
name|inputSkipCount
operator|==
literal|0
assert|;
name|int
name|curNextRead
init|=
name|nextRead
decl_stmt|;
comment|// Holds the longest match we've seen so far:
name|BytesRef
name|matchOutput
init|=
literal|null
decl_stmt|;
name|int
name|matchInputLength
init|=
literal|0
decl_stmt|;
name|BytesRef
name|pendingOutput
init|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
decl_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|scratchArc
argument_list|)
expr_stmt|;
assert|assert
name|scratchArc
operator|.
name|output
operator|==
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
assert|;
name|int
name|tokenCount
init|=
literal|0
decl_stmt|;
name|byToken
label|:
while|while
condition|(
literal|true
condition|)
block|{
comment|// Pull next token's chars:
specifier|final
name|char
index|[]
name|buffer
decl_stmt|;
specifier|final
name|int
name|bufferLen
decl_stmt|;
comment|//System.out.println("  cycle nextRead=" + curNextRead + " nextWrite=" + nextWrite);
if|if
condition|(
name|curNextRead
operator|==
name|nextWrite
condition|)
block|{
comment|// We used up our lookahead buffer of input tokens
comment|// -- pull next real input token:
if|if
condition|(
name|finished
condition|)
block|{
break|break;
block|}
else|else
block|{
comment|//System.out.println("  input.incrToken");
assert|assert
name|futureInputs
index|[
name|nextWrite
index|]
operator|.
name|consumed
assert|;
comment|// Not correct: a syn match whose output is longer
comment|// than its input can set future inputs keepOrig
comment|// to true:
comment|//assert !futureInputs[nextWrite].keepOrig;
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|buffer
operator|=
name|termAtt
operator|.
name|buffer
argument_list|()
expr_stmt|;
name|bufferLen
operator|=
name|termAtt
operator|.
name|length
argument_list|()
expr_stmt|;
specifier|final
name|PendingInput
name|input
init|=
name|futureInputs
index|[
name|nextWrite
index|]
decl_stmt|;
name|input
operator|.
name|startOffset
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|input
operator|.
name|endOffset
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
comment|//System.out.println("  new token=" + new String(buffer, 0, bufferLen));
if|if
condition|(
name|nextRead
operator|!=
name|nextWrite
condition|)
block|{
name|capture
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|input
operator|.
name|consumed
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// No more input tokens
comment|//System.out.println("      set end");
name|finished
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
comment|// Still in our lookahead
name|buffer
operator|=
name|futureInputs
index|[
name|curNextRead
index|]
operator|.
name|term
operator|.
name|chars
expr_stmt|;
name|bufferLen
operator|=
name|futureInputs
index|[
name|curNextRead
index|]
operator|.
name|term
operator|.
name|length
expr_stmt|;
comment|//System.out.println("  old token=" + new String(buffer, 0, bufferLen));
block|}
name|tokenCount
operator|++
expr_stmt|;
comment|// Run each char in this token through the FST:
name|int
name|bufUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bufUpto
operator|<
name|bufferLen
condition|)
block|{
specifier|final
name|int
name|codePoint
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|buffer
argument_list|,
name|bufUpto
argument_list|,
name|bufferLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|ignoreCase
condition|?
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePoint
argument_list|)
else|:
name|codePoint
argument_list|,
name|scratchArc
argument_list|,
name|scratchArc
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("    stop");
break|break
name|byToken
break|;
block|}
comment|// Accum the output
name|pendingOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|pendingOutput
argument_list|,
name|scratchArc
operator|.
name|output
argument_list|)
expr_stmt|;
comment|//System.out.println("    char=" + buffer[bufUpto] + " output=" + pendingOutput + " arc.output=" + scratchArc.output);
name|bufUpto
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePoint
argument_list|)
expr_stmt|;
block|}
comment|// OK, entire token matched; now see if this is a final
comment|// state:
if|if
condition|(
name|scratchArc
operator|.
name|isFinal
argument_list|()
condition|)
block|{
name|matchOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|pendingOutput
argument_list|,
name|scratchArc
operator|.
name|nextFinalOutput
argument_list|)
expr_stmt|;
name|matchInputLength
operator|=
name|tokenCount
expr_stmt|;
comment|//System.out.println("  found matchLength=" + matchInputLength + " output=" + matchOutput);
block|}
comment|// See if the FST wants to continue matching (ie, needs to
comment|// see the next input token):
if|if
condition|(
name|fst
operator|.
name|findTargetArc
argument_list|(
name|SynonymMap
operator|.
name|WORD_SEPARATOR
argument_list|,
name|scratchArc
argument_list|,
name|scratchArc
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// No further rules can match here; we're done
comment|// searching for matching rules starting at the
comment|// current input position.
break|break;
block|}
else|else
block|{
comment|// More matching is possible -- accum the output (if
comment|// any) of the WORD_SEP arc:
name|pendingOutput
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|pendingOutput
argument_list|,
name|scratchArc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextRead
operator|==
name|nextWrite
condition|)
block|{
name|capture
argument_list|()
expr_stmt|;
block|}
block|}
name|curNextRead
operator|=
name|rollIncr
argument_list|(
name|curNextRead
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextRead
operator|==
name|nextWrite
operator|&&
operator|!
name|finished
condition|)
block|{
comment|//System.out.println("  skip write slot=" + nextWrite);
name|nextWrite
operator|=
name|rollIncr
argument_list|(
name|nextWrite
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|matchOutput
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("  add matchLength=" + matchInputLength + " output=" + matchOutput);
name|inputSkipCount
operator|=
name|matchInputLength
expr_stmt|;
name|addOutput
argument_list|(
name|matchOutput
argument_list|,
name|matchInputLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nextRead
operator|!=
name|nextWrite
condition|)
block|{
comment|// Even though we had no match here, we set to 1
comment|// because we need to skip current input token before
comment|// trying to match again:
name|inputSkipCount
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|finished
assert|;
block|}
comment|//System.out.println("  parse done inputSkipCount=" + inputSkipCount + " nextRead=" + nextRead + " nextWrite=" + nextWrite);
block|}
comment|// Interleaves all output tokens onto the futureOutputs:
DECL|method|addOutput
specifier|private
name|void
name|addOutput
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|matchInputLength
parameter_list|)
block|{
name|bytesReader
operator|.
name|reset
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|code
init|=
name|bytesReader
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|keepOrig
init|=
operator|(
name|code
operator|&
literal|0x1
operator|)
operator|==
literal|0
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
comment|//System.out.println("  addOutput count=" + count + " keepOrig=" + keepOrig);
for|for
control|(
name|int
name|outputIDX
init|=
literal|0
init|;
name|outputIDX
operator|<
name|count
condition|;
name|outputIDX
operator|++
control|)
block|{
name|synonyms
operator|.
name|words
operator|.
name|get
argument_list|(
name|bytesReader
operator|.
name|readVInt
argument_list|()
argument_list|,
name|scratchBytes
argument_list|)
expr_stmt|;
comment|//System.out.println("    outIDX=" + outputIDX + " bytes=" + scratchBytes.length);
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|scratchBytes
argument_list|,
name|scratchChars
argument_list|)
expr_stmt|;
name|int
name|lastStart
init|=
name|scratchChars
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|chEnd
init|=
name|lastStart
operator|+
name|scratchChars
operator|.
name|length
decl_stmt|;
name|int
name|outputUpto
init|=
name|nextRead
decl_stmt|;
for|for
control|(
name|int
name|chIDX
init|=
name|lastStart
init|;
name|chIDX
operator|<=
name|chEnd
condition|;
name|chIDX
operator|++
control|)
block|{
if|if
condition|(
name|chIDX
operator|==
name|chEnd
operator|||
name|scratchChars
operator|.
name|chars
index|[
name|chIDX
index|]
operator|==
name|SynonymMap
operator|.
name|WORD_SEPARATOR
condition|)
block|{
specifier|final
name|int
name|outputLen
init|=
name|chIDX
operator|-
name|lastStart
decl_stmt|;
comment|// Caller is not allowed to have empty string in
comment|// the output:
assert|assert
name|outputLen
operator|>
literal|0
operator|:
literal|"output contains empty string: "
operator|+
name|scratchChars
assert|;
name|futureOutputs
index|[
name|outputUpto
index|]
operator|.
name|add
argument_list|(
name|scratchChars
operator|.
name|chars
argument_list|,
name|lastStart
argument_list|,
name|outputLen
argument_list|)
expr_stmt|;
comment|//System.out.println("      " + new String(scratchChars.chars, lastStart, outputLen) + " outputUpto=" + outputUpto);
name|lastStart
operator|=
literal|1
operator|+
name|chIDX
expr_stmt|;
comment|//System.out.println("  slot=" + outputUpto + " keepOrig=" + keepOrig);
name|outputUpto
operator|=
name|rollIncr
argument_list|(
name|outputUpto
argument_list|)
expr_stmt|;
assert|assert
name|futureOutputs
index|[
name|outputUpto
index|]
operator|.
name|posIncr
operator|==
literal|1
operator|:
literal|"outputUpto="
operator|+
name|outputUpto
operator|+
literal|" vs nextWrite="
operator|+
name|nextWrite
assert|;
block|}
block|}
block|}
name|int
name|upto
init|=
name|nextRead
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|matchInputLength
condition|;
name|idx
operator|++
control|)
block|{
name|futureInputs
index|[
name|upto
index|]
operator|.
name|keepOrig
operator||=
name|keepOrig
expr_stmt|;
name|futureInputs
index|[
name|upto
index|]
operator|.
name|matched
operator|=
literal|true
expr_stmt|;
name|upto
operator|=
name|rollIncr
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ++ mod rollBufferSize
DECL|method|rollIncr
specifier|private
name|int
name|rollIncr
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|rollBufferSize
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|count
return|;
block|}
block|}
comment|// for testing
DECL|method|getCaptureCount
name|int
name|getCaptureCount
parameter_list|()
block|{
return|return
name|captureCount
return|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("\nS: incrToken inputSkipCount=" + inputSkipCount + " nextRead=" + nextRead + " nextWrite=" + nextWrite);
while|while
condition|(
literal|true
condition|)
block|{
comment|// First play back any buffered future inputs/outputs
comment|// w/o running parsing again:
while|while
condition|(
name|inputSkipCount
operator|!=
literal|0
condition|)
block|{
comment|// At each position, we first output the original
comment|// token
comment|// TODO: maybe just a PendingState class, holding
comment|// both input& outputs?
specifier|final
name|PendingInput
name|input
init|=
name|futureInputs
index|[
name|nextRead
index|]
decl_stmt|;
specifier|final
name|PendingOutputs
name|outputs
init|=
name|futureOutputs
index|[
name|nextRead
index|]
decl_stmt|;
comment|//System.out.println("  cycle nextRead=" + nextRead + " nextWrite=" + nextWrite + " inputSkipCount="+ inputSkipCount + " input.keepOrig=" + input.keepOrig + " input.consumed=" + input.consumed + " input.state=" + input.state);
if|if
condition|(
operator|!
name|input
operator|.
name|consumed
operator|&&
operator|(
name|input
operator|.
name|keepOrig
operator|||
operator|!
name|input
operator|.
name|matched
operator|)
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|state
operator|!=
literal|null
condition|)
block|{
comment|// Return a previously saved token (because we
comment|// had to lookahead):
name|restoreState
argument_list|(
name|input
operator|.
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Pass-through case: return token we just pulled
comment|// but didn't capture:
assert|assert
name|inputSkipCount
operator|==
literal|1
operator|:
literal|"inputSkipCount="
operator|+
name|inputSkipCount
operator|+
literal|" nextRead="
operator|+
name|nextRead
assert|;
block|}
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputs
operator|.
name|count
operator|>
literal|0
condition|)
block|{
name|outputs
operator|.
name|posIncr
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|nextRead
operator|=
name|rollIncr
argument_list|(
name|nextRead
argument_list|)
expr_stmt|;
name|inputSkipCount
operator|--
expr_stmt|;
block|}
comment|//System.out.println("  return token=" + termAtt.toString());
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|outputs
operator|.
name|upto
operator|<
name|outputs
operator|.
name|count
condition|)
block|{
comment|// Still have pending outputs to replay at this
comment|// position
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|int
name|posIncr
init|=
name|outputs
operator|.
name|posIncr
decl_stmt|;
specifier|final
name|CharsRef
name|output
init|=
name|outputs
operator|.
name|pullNext
argument_list|()
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|output
operator|.
name|chars
argument_list|,
name|output
operator|.
name|offset
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|TYPE_SYNONYM
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|input
operator|.
name|startOffset
argument_list|,
name|input
operator|.
name|endOffset
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputs
operator|.
name|count
operator|==
literal|0
condition|)
block|{
comment|// Done with the buffered input and all outputs at
comment|// this position
name|nextRead
operator|=
name|rollIncr
argument_list|(
name|nextRead
argument_list|)
expr_stmt|;
name|inputSkipCount
operator|--
expr_stmt|;
block|}
comment|//System.out.println("  return token=" + termAtt.toString());
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Done with the buffered input and all outputs at
comment|// this position
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
name|nextRead
operator|=
name|rollIncr
argument_list|(
name|nextRead
argument_list|)
expr_stmt|;
name|inputSkipCount
operator|--
expr_stmt|;
block|}
block|}
if|if
condition|(
name|finished
operator|&&
name|nextRead
operator|==
name|nextWrite
condition|)
block|{
comment|// End case: if any output syns went beyond end of
comment|// input stream, enumerate them now:
specifier|final
name|PendingOutputs
name|outputs
init|=
name|futureOutputs
index|[
name|nextRead
index|]
decl_stmt|;
if|if
condition|(
name|outputs
operator|.
name|upto
operator|<
name|outputs
operator|.
name|count
condition|)
block|{
specifier|final
name|int
name|posIncr
init|=
name|outputs
operator|.
name|posIncr
decl_stmt|;
specifier|final
name|CharsRef
name|output
init|=
name|outputs
operator|.
name|pullNext
argument_list|()
decl_stmt|;
name|futureInputs
index|[
name|nextRead
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|outputs
operator|.
name|count
operator|==
literal|0
condition|)
block|{
name|nextWrite
operator|=
name|nextRead
operator|=
name|rollIncr
argument_list|(
name|nextRead
argument_list|)
expr_stmt|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|output
operator|.
name|chars
argument_list|,
name|output
operator|.
name|offset
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|TYPE_SYNONYM
argument_list|)
expr_stmt|;
comment|//System.out.println("  set posIncr=" + outputs.posIncr + " outputs=" + outputs);
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
comment|//System.out.println("  return token=" + termAtt.toString());
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Find new synonym matches:
name|parse
argument_list|()
expr_stmt|;
block|}
block|}
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
name|captureCount
operator|=
literal|0
expr_stmt|;
name|finished
operator|=
literal|false
expr_stmt|;
comment|// In normal usage these resets would not be needed,
comment|// since they reset-as-they-are-consumed, but the app
comment|// may not consume all input tokens in which case we
comment|// have leftover state here:
for|for
control|(
name|PendingInput
name|input
range|:
name|futureInputs
control|)
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|PendingOutputs
name|output
range|:
name|futureOutputs
control|)
block|{
name|output
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

