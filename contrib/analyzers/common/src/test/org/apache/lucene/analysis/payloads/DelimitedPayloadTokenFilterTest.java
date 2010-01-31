begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
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
name|WhitespaceTokenizer
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
name|TermAttribute
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
name|Payload
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
name|LuceneTestCase
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|DelimitedPayloadTokenFilterTest
specifier|public
class|class
name|DelimitedPayloadTokenFilterTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPayloads
specifier|public
name|void
name|testPayloads
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"The quick|JJ red|JJ fox|NN jumped|VB over the lazy|JJ brown|JJ dogs|NN"
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|filter
init|=
operator|new
name|DelimitedPayloadTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|DelimitedPayloadTokenFilter
operator|.
name|DEFAULT_DELIMITER
argument_list|,
operator|new
name|IdentityEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTermEquals
argument_list|(
literal|"The"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"quick"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"red"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fox"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"NN"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"jumped"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"VB"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"over"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"the"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"lazy"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"brown"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"dogs"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|"NN"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNext
specifier|public
name|void
name|testNext
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"The quick|JJ red|JJ fox|NN jumped|VB over the lazy|JJ brown|JJ dogs|NN"
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|filter
init|=
operator|new
name|DelimitedPayloadTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
name|DelimitedPayloadTokenFilter
operator|.
name|DEFAULT_DELIMITER
argument_list|,
operator|new
name|IdentityEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|assertTermEquals
argument_list|(
literal|"The"
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"quick"
argument_list|,
name|filter
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"red"
argument_list|,
name|filter
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fox"
argument_list|,
name|filter
argument_list|,
literal|"NN"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"jumped"
argument_list|,
name|filter
argument_list|,
literal|"VB"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"over"
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"the"
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"lazy"
argument_list|,
name|filter
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"brown"
argument_list|,
name|filter
argument_list|,
literal|"JJ"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"dogs"
argument_list|,
name|filter
argument_list|,
literal|"NN"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloatEncoding
specifier|public
name|void
name|testFloatEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"The quick|1.0 red|2.0 fox|3.5 jumped|0.5 over the lazy|5 brown|99.3 dogs|83.7"
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|filter
init|=
operator|new
name|DelimitedPayloadTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
literal|'|'
argument_list|,
operator|new
name|FloatEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTermEquals
argument_list|(
literal|"The"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"quick"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"red"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fox"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|3.5f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"jumped"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"over"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"the"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"lazy"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|5.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"brown"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|99.3f
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"dogs"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeFloat
argument_list|(
literal|83.7f
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIntEncoding
specifier|public
name|void
name|testIntEncoding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"The quick|1 red|2 fox|3 jumped over the lazy|5 brown|99 dogs|83"
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|filter
init|=
operator|new
name|DelimitedPayloadTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|,
literal|'|'
argument_list|,
operator|new
name|IntegerEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payAtt
init|=
name|filter
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTermEquals
argument_list|(
literal|"The"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"quick"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"red"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"fox"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"jumped"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"over"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"the"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"lazy"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"brown"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|99
argument_list|)
argument_list|)
expr_stmt|;
name|assertTermEquals
argument_list|(
literal|"dogs"
argument_list|,
name|filter
argument_list|,
name|termAtt
argument_list|,
name|payAtt
argument_list|,
name|PayloadHelper
operator|.
name|encodeInt
argument_list|(
literal|83
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTermEquals
name|void
name|assertTermEquals
parameter_list|(
name|String
name|expected
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|byte
index|[]
name|expectPay
parameter_list|)
throws|throws
name|Exception
block|{
name|TermAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|Payload
name|payload
init|=
name|payloadAtt
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|payload
operator|.
name|length
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|expectPay
operator|.
name|length
argument_list|,
name|payload
operator|.
name|length
argument_list|()
operator|==
name|expectPay
operator|.
name|length
argument_list|)
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
name|expectPay
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|expectPay
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
name|payload
operator|.
name|byteAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|expectPay
index|[
name|i
index|]
operator|==
name|payload
operator|.
name|byteAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"expectPay is not null and it should be"
argument_list|,
name|expectPay
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertTermEquals
name|void
name|assertTermEquals
parameter_list|(
name|String
name|expected
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|TermAttribute
name|termAtt
parameter_list|,
name|PayloadAttribute
name|payAtt
parameter_list|,
name|byte
index|[]
name|expectPay
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|termAtt
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|Payload
name|payload
init|=
name|payAtt
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|payload
operator|.
name|length
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|expectPay
operator|.
name|length
argument_list|,
name|payload
operator|.
name|length
argument_list|()
operator|==
name|expectPay
operator|.
name|length
argument_list|)
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
name|expectPay
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|expectPay
index|[
name|i
index|]
operator|+
literal|" does not equal: "
operator|+
name|payload
operator|.
name|byteAt
argument_list|(
name|i
argument_list|)
argument_list|,
name|expectPay
index|[
name|i
index|]
operator|==
name|payload
operator|.
name|byteAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"expectPay is not null and it should be"
argument_list|,
name|expectPay
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

