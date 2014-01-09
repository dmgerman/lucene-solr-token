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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|MockTokenizer
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
name|TypeAttribute
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_class
DECL|class|TypeAsPayloadTokenFilterTest
specifier|public
class|class
name|TypeAsPayloadTokenFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|test
init|=
literal|"The quick red fox jumped over the lazy brown dogs"
decl_stmt|;
name|TypeAsPayloadTokenFilter
name|nptf
init|=
operator|new
name|TypeAsPayloadTokenFilter
argument_list|(
operator|new
name|WordTokenFilter
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|nptf
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
name|nptf
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
name|nptf
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|nptf
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|nptf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
operator|+
literal|" is not null and it should be"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"nextToken.getPayload() is null and it shouldn't be"
argument_list|,
name|payloadAtt
operator|.
name|getPayload
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|type
init|=
operator|new
name|String
argument_list|(
name|payloadAtt
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|type
operator|+
literal|" is not equal to "
operator|+
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|,
name|type
operator|.
name|equals
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|+
literal|" does not equal: "
operator|+
literal|10
argument_list|,
name|count
operator|==
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|class|WordTokenFilter
specifier|private
specifier|final
class|class
name|WordTokenFilter
extends|extends
name|TokenFilter
block|{
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
DECL|method|WordTokenFilter
specifier|private
name|WordTokenFilter
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
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

