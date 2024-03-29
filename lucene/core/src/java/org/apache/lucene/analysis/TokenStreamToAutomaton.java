begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|RollingBuffer
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
name|automaton
operator|.
name|Automaton
import|;
end_import

begin_comment
comment|// TODO: maybe also toFST?  then we can translate atts into FST outputs/weights
end_comment

begin_comment
comment|/** Consumes a TokenStream and creates an {@link Automaton}  *  where the transition labels are UTF8 bytes (or Unicode   *  code points if unicodeArcs is true) from the {@link  *  TermToBytesRefAttribute}.  Between tokens we insert  *  POS_SEP and for holes we insert HOLE.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|TokenStreamToAutomaton
specifier|public
class|class
name|TokenStreamToAutomaton
block|{
DECL|field|preservePositionIncrements
specifier|private
name|boolean
name|preservePositionIncrements
decl_stmt|;
DECL|field|finalOffsetGapAsHole
specifier|private
name|boolean
name|finalOffsetGapAsHole
decl_stmt|;
DECL|field|unicodeArcs
specifier|private
name|boolean
name|unicodeArcs
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|TokenStreamToAutomaton
specifier|public
name|TokenStreamToAutomaton
parameter_list|()
block|{
name|this
operator|.
name|preservePositionIncrements
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Whether to generate holes in the automaton for missing positions,<code>true</code> by default. */
DECL|method|setPreservePositionIncrements
specifier|public
name|void
name|setPreservePositionIncrements
parameter_list|(
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|preservePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
comment|/** If true, any final offset gaps will result in adding a position hole. */
DECL|method|setFinalOffsetGapAsHole
specifier|public
name|void
name|setFinalOffsetGapAsHole
parameter_list|(
name|boolean
name|finalOffsetGapAsHole
parameter_list|)
block|{
name|this
operator|.
name|finalOffsetGapAsHole
operator|=
name|finalOffsetGapAsHole
expr_stmt|;
block|}
comment|/** Whether to make transition labels Unicode code points instead of UTF8 bytes,     *<code>false</code> by default */
DECL|method|setUnicodeArcs
specifier|public
name|void
name|setUnicodeArcs
parameter_list|(
name|boolean
name|unicodeArcs
parameter_list|)
block|{
name|this
operator|.
name|unicodeArcs
operator|=
name|unicodeArcs
expr_stmt|;
block|}
DECL|class|Position
specifier|private
specifier|static
class|class
name|Position
implements|implements
name|RollingBuffer
operator|.
name|Resettable
block|{
comment|// Any tokens that ended at our position arrive to this state:
DECL|field|arriving
name|int
name|arriving
init|=
operator|-
literal|1
decl_stmt|;
comment|// Any tokens that start at our position leave from this state:
DECL|field|leaving
name|int
name|leaving
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
name|arriving
operator|=
operator|-
literal|1
expr_stmt|;
name|leaving
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|class|Positions
specifier|private
specifier|static
class|class
name|Positions
extends|extends
name|RollingBuffer
argument_list|<
name|Position
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newInstance
specifier|protected
name|Position
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|Position
argument_list|()
return|;
block|}
block|}
comment|/** Subclass and implement this if you need to change the    *  token (such as escaping certain bytes) before it's    *  turned into a graph. */
DECL|method|changeToken
specifier|protected
name|BytesRef
name|changeToken
parameter_list|(
name|BytesRef
name|in
parameter_list|)
block|{
return|return
name|in
return|;
block|}
comment|/** We create transition between two adjacent tokens. */
DECL|field|POS_SEP
specifier|public
specifier|static
specifier|final
name|int
name|POS_SEP
init|=
literal|0x001f
decl_stmt|;
comment|/** We add this arc to represent a hole. */
DECL|field|HOLE
specifier|public
specifier|static
specifier|final
name|int
name|HOLE
init|=
literal|0x001e
decl_stmt|;
comment|/** Pulls the graph (including {@link    *  PositionLengthAttribute}) from the provided {@link    *  TokenStream}, and creates the corresponding    *  automaton where arcs are bytes (or Unicode code points     *  if unicodeArcs = true) from each term. */
DECL|method|toAutomaton
specifier|public
name|Automaton
name|toAutomaton
parameter_list|(
name|TokenStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Automaton
operator|.
name|Builder
name|builder
init|=
operator|new
name|Automaton
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
specifier|final
name|TermToBytesRefAttribute
name|termBytesAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionLengthAttribute
name|posLengthAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Only temporarily holds states ahead of our current
comment|// position:
specifier|final
name|RollingBuffer
argument_list|<
name|Position
argument_list|>
name|positions
init|=
operator|new
name|Positions
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|freedPos
init|=
literal|0
decl_stmt|;
name|Position
name|posData
init|=
literal|null
decl_stmt|;
name|int
name|maxOffset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|posInc
init|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|preservePositionIncrements
operator|==
literal|false
operator|&&
name|posInc
operator|>
literal|1
condition|)
block|{
name|posInc
operator|=
literal|1
expr_stmt|;
block|}
assert|assert
name|pos
operator|>
operator|-
literal|1
operator|||
name|posInc
operator|>
literal|0
assert|;
if|if
condition|(
name|posInc
operator|>
literal|0
condition|)
block|{
comment|// New node:
name|pos
operator|+=
name|posInc
expr_stmt|;
name|posData
operator|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
expr_stmt|;
assert|assert
name|posData
operator|.
name|leaving
operator|==
operator|-
literal|1
assert|;
if|if
condition|(
name|posData
operator|.
name|arriving
operator|==
operator|-
literal|1
condition|)
block|{
comment|// No token ever arrived to this position
if|if
condition|(
name|pos
operator|==
literal|0
condition|)
block|{
comment|// OK: this is the first token
name|posData
operator|.
name|leaving
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// This means there's a hole (eg, StopFilter
comment|// does this):
name|posData
operator|.
name|leaving
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
name|addHoles
argument_list|(
name|builder
argument_list|,
name|positions
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|posData
operator|.
name|leaving
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addTransition
argument_list|(
name|posData
operator|.
name|arriving
argument_list|,
name|posData
operator|.
name|leaving
argument_list|,
name|POS_SEP
argument_list|)
expr_stmt|;
if|if
condition|(
name|posInc
operator|>
literal|1
condition|)
block|{
comment|// A token spanned over a hole; add holes
comment|// "under" it:
name|addHoles
argument_list|(
name|builder
argument_list|,
name|positions
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
name|freedPos
operator|<=
name|pos
condition|)
block|{
name|Position
name|freePosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|freedPos
argument_list|)
decl_stmt|;
comment|// don't free this position yet if we may still need to fill holes over it:
if|if
condition|(
name|freePosData
operator|.
name|arriving
operator|==
operator|-
literal|1
operator|||
name|freePosData
operator|.
name|leaving
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|positions
operator|.
name|freeBefore
argument_list|(
name|freedPos
argument_list|)
expr_stmt|;
name|freedPos
operator|++
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|endPos
init|=
name|pos
operator|+
name|posLengthAtt
operator|.
name|getPositionLength
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|termUTF8
init|=
name|changeToken
argument_list|(
name|termBytesAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
decl_stmt|;
name|int
index|[]
name|termUnicode
init|=
literal|null
decl_stmt|;
specifier|final
name|Position
name|endPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|endPos
argument_list|)
decl_stmt|;
if|if
condition|(
name|endPosData
operator|.
name|arriving
operator|==
operator|-
literal|1
condition|)
block|{
name|endPosData
operator|.
name|arriving
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
block|}
name|int
name|termLen
decl_stmt|;
if|if
condition|(
name|unicodeArcs
condition|)
block|{
specifier|final
name|String
name|utf16
init|=
name|termUTF8
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|termUnicode
operator|=
operator|new
name|int
index|[
name|utf16
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|utf16
operator|.
name|length
argument_list|()
argument_list|)
index|]
expr_stmt|;
name|termLen
operator|=
name|termUnicode
operator|.
name|length
expr_stmt|;
for|for
control|(
name|int
name|cp
init|,
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|utf16
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|cp
argument_list|)
control|)
block|{
name|termUnicode
index|[
name|j
operator|++
index|]
operator|=
name|cp
operator|=
name|utf16
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|termLen
operator|=
name|termUTF8
operator|.
name|length
expr_stmt|;
block|}
name|int
name|state
init|=
name|posData
operator|.
name|leaving
decl_stmt|;
for|for
control|(
name|int
name|byteIDX
init|=
literal|0
init|;
name|byteIDX
operator|<
name|termLen
condition|;
name|byteIDX
operator|++
control|)
block|{
specifier|final
name|int
name|nextState
init|=
name|byteIDX
operator|==
name|termLen
operator|-
literal|1
condition|?
name|endPosData
operator|.
name|arriving
else|:
name|builder
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|c
decl_stmt|;
if|if
condition|(
name|unicodeArcs
condition|)
block|{
name|c
operator|=
name|termUnicode
index|[
name|byteIDX
index|]
expr_stmt|;
block|}
else|else
block|{
name|c
operator|=
name|termUTF8
operator|.
name|bytes
index|[
name|termUTF8
operator|.
name|offset
operator|+
name|byteIDX
index|]
operator|&
literal|0xff
expr_stmt|;
block|}
name|builder
operator|.
name|addTransition
argument_list|(
name|state
argument_list|,
name|nextState
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|state
operator|=
name|nextState
expr_stmt|;
block|}
name|maxOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxOffset
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|end
argument_list|()
expr_stmt|;
name|int
name|endState
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|endPosInc
init|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|endPosInc
operator|==
literal|0
operator|&&
name|finalOffsetGapAsHole
operator|&&
name|offsetAtt
operator|.
name|endOffset
argument_list|()
operator|>
name|maxOffset
condition|)
block|{
name|endPosInc
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|endPosInc
operator|>
literal|0
condition|)
block|{
comment|// there were hole(s) after the last token
name|endState
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
comment|// add trailing holes now:
name|int
name|lastState
init|=
name|endState
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|state1
init|=
name|builder
operator|.
name|createState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addTransition
argument_list|(
name|lastState
argument_list|,
name|state1
argument_list|,
name|HOLE
argument_list|)
expr_stmt|;
name|endPosInc
operator|--
expr_stmt|;
if|if
condition|(
name|endPosInc
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|setAccept
argument_list|(
name|state1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
name|int
name|state2
init|=
name|builder
operator|.
name|createState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addTransition
argument_list|(
name|state1
argument_list|,
name|state2
argument_list|,
name|POS_SEP
argument_list|)
expr_stmt|;
name|lastState
operator|=
name|state2
expr_stmt|;
block|}
block|}
else|else
block|{
name|endState
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<=
name|positions
operator|.
name|getMaxPos
argument_list|()
condition|)
block|{
name|posData
operator|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|posData
operator|.
name|arriving
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|endState
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|addTransition
argument_list|(
name|posData
operator|.
name|arriving
argument_list|,
name|endState
argument_list|,
name|POS_SEP
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setAccept
argument_list|(
name|posData
operator|.
name|arriving
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|finish
argument_list|()
return|;
block|}
comment|// for debugging!
comment|/*   private static void toDot(Automaton a) throws IOException {     final String s = a.toDot();     Writer w = new OutputStreamWriter(new FileOutputStream("/tmp/out.dot"));     w.write(s);     w.close();     System.out.println("TEST: saved to /tmp/out.dot");   }   */
DECL|method|addHoles
specifier|private
specifier|static
name|void
name|addHoles
parameter_list|(
name|Automaton
operator|.
name|Builder
name|builder
parameter_list|,
name|RollingBuffer
argument_list|<
name|Position
argument_list|>
name|positions
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|Position
name|posData
init|=
name|positions
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|Position
name|prevPosData
init|=
name|positions
operator|.
name|get
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
decl_stmt|;
while|while
condition|(
name|posData
operator|.
name|arriving
operator|==
operator|-
literal|1
operator|||
name|prevPosData
operator|.
name|leaving
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|posData
operator|.
name|arriving
operator|==
operator|-
literal|1
condition|)
block|{
name|posData
operator|.
name|arriving
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
name|builder
operator|.
name|addTransition
argument_list|(
name|posData
operator|.
name|arriving
argument_list|,
name|posData
operator|.
name|leaving
argument_list|,
name|POS_SEP
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prevPosData
operator|.
name|leaving
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|pos
operator|==
literal|1
condition|)
block|{
name|prevPosData
operator|.
name|leaving
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|prevPosData
operator|.
name|leaving
operator|=
name|builder
operator|.
name|createState
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|prevPosData
operator|.
name|arriving
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|addTransition
argument_list|(
name|prevPosData
operator|.
name|arriving
argument_list|,
name|prevPosData
operator|.
name|leaving
argument_list|,
name|POS_SEP
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|addTransition
argument_list|(
name|prevPosData
operator|.
name|leaving
argument_list|,
name|posData
operator|.
name|arriving
argument_list|,
name|HOLE
argument_list|)
expr_stmt|;
name|pos
operator|--
expr_stmt|;
if|if
condition|(
name|pos
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|posData
operator|=
name|prevPosData
expr_stmt|;
name|prevPosData
operator|=
name|positions
operator|.
name|get
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

