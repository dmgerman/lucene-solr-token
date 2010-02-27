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

begin_class
DECL|class|TokenRangeSinkTokenizerTest
specifier|public
class|class
name|TokenRangeSinkTokenizerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|TokenRangeSinkTokenizerTest
specifier|public
name|TokenRangeSinkTokenizerTest
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
name|TokenRangeSinkFilter
name|sinkFilter
init|=
operator|new
name|TokenRangeSinkFilter
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|String
name|test
init|=
literal|"The quick red fox jumped over the lazy brown dogs"
decl_stmt|;
name|TeeSinkTokenFilter
name|tee
init|=
operator|new
name|TeeSinkTokenFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|SinkTokenStream
name|rangeToks
init|=
name|tee
operator|.
name|newSinkTokenStream
argument_list|(
name|sinkFilter
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|tee
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tee
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|int
name|sinkCount
init|=
literal|0
decl_stmt|;
name|rangeToks
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|rangeToks
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
name|assertTrue
argument_list|(
literal|"rangeToks Size: "
operator|+
name|sinkCount
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|sinkCount
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

