begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|document
operator|.
name|Fieldable
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

begin_comment
comment|/**  * Holds state for inverting all occurrences of a single  * field in the document.  This class doesn't do anything  * itself; instead, it forwards the tokens produced by  * analysis to its own consumer  * (InvertedDocConsumerPerField).  It also interacts with an  * endConsumer (InvertedDocEndConsumerPerField).  */
end_comment

begin_class
DECL|class|DocInverterPerField
specifier|final
class|class
name|DocInverterPerField
extends|extends
name|DocFieldConsumerPerField
block|{
DECL|field|perThread
specifier|final
specifier|private
name|DocInverterPerThread
name|perThread
decl_stmt|;
DECL|field|fieldInfo
specifier|final
specifier|private
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|consumer
specifier|final
name|InvertedDocConsumerPerField
name|consumer
decl_stmt|;
DECL|field|endConsumer
specifier|final
name|InvertedDocEndConsumerPerField
name|endConsumer
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|method|DocInverterPerField
specifier|public
name|DocInverterPerField
parameter_list|(
name|DocInverterPerThread
name|perThread
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|perThread
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|perThread
operator|.
name|fieldState
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|perThread
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|endConsumer
operator|=
name|perThread
operator|.
name|endConsumer
operator|.
name|addField
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|consumer
operator|.
name|abort
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processFields
specifier|public
name|void
name|processFields
parameter_list|(
specifier|final
name|Fieldable
index|[]
name|fields
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldState
operator|.
name|reset
argument_list|(
name|docState
operator|.
name|doc
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxFieldLength
init|=
name|docState
operator|.
name|maxFieldLength
decl_stmt|;
specifier|final
name|boolean
name|doInvert
init|=
name|consumer
operator|.
name|start
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Fieldable
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
comment|// TODO FI: this should be "genericized" to querying
comment|// consumer if it wants to see this particular field
comment|// tokenized.
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
operator|&&
name|doInvert
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|fieldState
operator|.
name|position
operator|+=
name|docState
operator|.
name|analyzer
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
block|{
comment|// un-tokenized field
name|String
name|stringValue
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|valueLength
init|=
name|stringValue
operator|.
name|length
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|singleToken
operator|.
name|reinit
argument_list|(
name|stringValue
argument_list|,
literal|0
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
name|fieldState
operator|.
name|attributeSource
operator|=
name|perThread
operator|.
name|singleToken
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|add
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
name|docState
operator|.
name|docWriter
operator|.
name|setAborting
argument_list|()
expr_stmt|;
block|}
name|fieldState
operator|.
name|offset
operator|+=
name|valueLength
expr_stmt|;
name|fieldState
operator|.
name|length
operator|++
expr_stmt|;
name|fieldState
operator|.
name|position
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// tokenized field
specifier|final
name|TokenStream
name|stream
decl_stmt|;
specifier|final
name|TokenStream
name|streamValue
init|=
name|field
operator|.
name|tokenStreamValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamValue
operator|!=
literal|null
condition|)
name|stream
operator|=
name|streamValue
expr_stmt|;
else|else
block|{
comment|// the field does not have a TokenStream,
comment|// so we have to obtain one from the analyzer
specifier|final
name|Reader
name|reader
decl_stmt|;
comment|// find or make Reader
specifier|final
name|Reader
name|readerValue
init|=
name|field
operator|.
name|readerValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerValue
operator|!=
literal|null
condition|)
name|reader
operator|=
name|readerValue
expr_stmt|;
else|else
block|{
name|String
name|stringValue
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|stringValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must have either TokenStream, String or Reader value"
argument_list|)
throw|;
block|}
name|perThread
operator|.
name|stringReader
operator|.
name|init
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|reader
operator|=
name|perThread
operator|.
name|stringReader
expr_stmt|;
block|}
comment|// Tokenize field and add to postingTable
name|stream
operator|=
name|docState
operator|.
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
comment|// reset the TokenStream to the first token
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|boolean
name|hasMoreTokens
init|=
name|stream
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
name|fieldState
operator|.
name|attributeSource
operator|=
name|stream
expr_stmt|;
name|OffsetAttribute
name|offsetAttribute
init|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAttribute
init|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|start
argument_list|(
name|field
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// If we hit an exception in stream.next below
comment|// (which is fairly common, eg if analyzer
comment|// chokes on a given document), then it's
comment|// non-aborting and (above) this one document
comment|// will be marked as deleted, but still
comment|// consume a docID
if|if
condition|(
operator|!
name|hasMoreTokens
condition|)
break|break;
specifier|final
name|int
name|posIncr
init|=
name|posIncrAttribute
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
name|fieldState
operator|.
name|position
operator|+=
name|posIncr
expr_stmt|;
if|if
condition|(
name|fieldState
operator|.
name|position
operator|>
literal|0
condition|)
block|{
name|fieldState
operator|.
name|position
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|posIncr
operator|==
literal|0
condition|)
name|fieldState
operator|.
name|numOverlap
operator|++
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// If we hit an exception in here, we abort
comment|// all buffered documents since the last
comment|// flush, on the likelihood that the
comment|// internal state of the consumer is now
comment|// corrupt and should not be flushed to a
comment|// new segment:
name|consumer
operator|.
name|add
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
name|docState
operator|.
name|docWriter
operator|.
name|setAborting
argument_list|()
expr_stmt|;
block|}
name|fieldState
operator|.
name|position
operator|++
expr_stmt|;
if|if
condition|(
operator|++
name|fieldState
operator|.
name|length
operator|>=
name|maxFieldLength
condition|)
block|{
if|if
condition|(
name|docState
operator|.
name|infoStream
operator|!=
literal|null
condition|)
name|docState
operator|.
name|infoStream
operator|.
name|println
argument_list|(
literal|"maxFieldLength "
operator|+
name|maxFieldLength
operator|+
literal|" reached for field "
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|", ignoring following tokens"
argument_list|)
expr_stmt|;
break|break;
block|}
name|hasMoreTokens
operator|=
name|stream
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
comment|// trigger streams to perform end-of-stream operations
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
name|fieldState
operator|.
name|offset
operator|+=
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|fieldState
operator|.
name|offset
operator|+=
name|docState
operator|.
name|analyzer
operator|.
name|getOffsetGap
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|fieldState
operator|.
name|boost
operator|*=
name|field
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-2387: don't hang onto the field, so GC can
comment|// reclaim
name|fields
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|consumer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|endConsumer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

