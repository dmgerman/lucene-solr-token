begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

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
name|Token
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
name|core
operator|.
name|WhitespaceAnalyzer
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
name|FlagsAttribute
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
name|PayloadAttribute
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
name|util
operator|.
name|Version
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
name|HashSet
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

begin_comment
comment|/**  *  * @since solr 1.3  **/
end_comment

begin_class
DECL|class|SimpleQueryConverter
class|class
name|SimpleQueryConverter
extends|extends
name|SpellingQueryConverter
block|{
annotation|@
name|Override
DECL|method|convert
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|origQuery
parameter_list|)
block|{
try|try
block|{
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|WhitespaceAnalyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|origQuery
argument_list|)
decl_stmt|;
comment|// TODO: support custom attributes
name|CharTermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|FlagsAttribute
name|flagsAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|Token
name|tok
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|tok
operator|.
name|copyBuffer
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
name|tok
operator|.
name|setOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setFlags
argument_list|(
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setPayload
argument_list|(
name|payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setPositionIncrement
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setType
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

