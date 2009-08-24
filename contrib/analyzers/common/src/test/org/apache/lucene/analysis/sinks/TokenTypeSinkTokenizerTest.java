begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StringReader
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
name|TeeSinkTokenFilter
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
name|TeeSinkTokenFilter
operator|.
name|SinkTokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TypeAttribute
import|;
end_import

begin_class
DECL|class|TokenTypeSinkTokenizerTest
specifier|public
class|class
name|TokenTypeSinkTokenizerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|TokenTypeSinkTokenizerTest
specifier|public
name|TokenTypeSinkTokenizerTest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenTypeSinkFilter
name|sinkFilter
init|=
operator|new
name|TokenTypeSinkFilter
argument_list|(
literal|"D"
argument_list|)
decl_stmt|;
name|String
name|test
init|=
literal|"The quick red fox jumped over the lazy brown dogs"
decl_stmt|;
name|TeeSinkTokenFilter
name|ttf
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|WordTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SinkTokenStream
name|sink
init|=
name|ttf
operator|.
name|newSinkTokenStream
argument_list|(
name|sinkFilter
argument_list|)
decl_stmt|;
name|boolean
name|seenDogs
init|=
literal|false
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|ttf
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
operator|(
name|TypeAttribute
operator|)
name|ttf
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ttf
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ttf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|termAtt
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dogs"
argument_list|)
condition|)
block|{
name|seenDogs
operator|=
literal|true
expr_stmt|;
name|assertTrue
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"D"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
literal|"D"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"word"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|seenDogs
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|seenDogs
operator|==
literal|true
argument_list|)
expr_stmt|;
name|int
name|sinkCount
init|=
literal|0
decl_stmt|;
name|sink
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|sink
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|sinkCount
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"sink Size: "
operator|+
name|sinkCount
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|sinkCount
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|class|WordTokenFilter
specifier|private
class|class
name|WordTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
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
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|termAtt
operator|.
name|term
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dogs"
argument_list|)
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"D"
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

