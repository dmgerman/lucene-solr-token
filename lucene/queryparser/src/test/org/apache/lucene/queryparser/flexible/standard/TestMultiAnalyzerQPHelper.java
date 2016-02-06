begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
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
name|analysis
operator|.
name|*
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|Operator
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

begin_comment
comment|/**  * This test case is a copy of the core Lucene query parser test, it was adapted  * to use new QueryParserHelper instead of the old query parser.  *   * Test QueryParser's ability to deal with Analyzers that return more than one  * token per position or that return tokens with a position increment&gt; 1.  */
end_comment

begin_class
DECL|class|TestMultiAnalyzerQPHelper
specifier|public
class|class
name|TestMultiAnalyzerQPHelper
extends|extends
name|LuceneTestCase
block|{
DECL|field|multiToken
specifier|private
specifier|static
name|int
name|multiToken
init|=
literal|0
decl_stmt|;
DECL|method|testMultiAnalyzer
specifier|public
name|void
name|testMultiAnalyzer
parameter_list|()
throws|throws
name|QueryNodeException
block|{
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|()
decl_stmt|;
name|qp
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|MultiAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// trivial, no multiple tokens:
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo foobar"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo foobar"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo foobar\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo foobar\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo foobar blah\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo foobar blah\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// two tokens at the same position:
name|assertEquals
argument_list|(
literal|"(multi multi2) foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi foo"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo (multi multi2)"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo multi"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(multi multi2) (multi multi2)"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi multi"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(foo (multi multi2)) +(bar (multi multi2))"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"+(foo multi) +(bar multi)"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(foo (multi multi2)) field:\"bar (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"+(foo multi) field:\"bar multi\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrases:
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo multi\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"foo (multi multi2) foobar (multi multi2)\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"foo multi foobar multi\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// fields:
name|assertEquals
argument_list|(
literal|"(field:multi field:multi2) field:foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"field:multi field:foo"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field:\"(multi multi2) foo\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"field:\"multi foo\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// three tokens at one position:
name|assertEquals
argument_list|(
literal|"triplemulti multi3 multi2"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"triplemulti"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo (triplemulti multi3 multi2) foobar"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"foo triplemulti foobar"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase with non-default slop:
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\"~10"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\"~10"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase with non-default boost:
name|assertEquals
argument_list|(
literal|"(\"(multi multi2) foo\")^2.0"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\"^2"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phrase after changing default slop
name|qp
operator|.
name|setPhraseSlop
argument_list|(
literal|99
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\"~99 bar"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\" bar"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"(multi multi2) foo\"~99 \"foo bar\"~2"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"multi foo\" \"foo bar\"~2"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|qp
operator|.
name|setPhraseSlop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// non-default operator:
name|qp
operator|.
name|setDefaultOperator
argument_list|(
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(multi multi2) +foo"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"multi foo"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// public void testMultiAnalyzerWithSubclassOfQueryParser() throws
comment|// ParseException {
comment|// this test doesn't make sense when using the new QueryParser API
comment|// DumbQueryParser qp = new DumbQueryParser("", new MultiAnalyzer());
comment|// qp.setPhraseSlop(99); // modified default slop
comment|//
comment|// // direct call to (super's) getFieldQuery to demonstrate differnce
comment|// // between phrase and multiphrase with modified default slop
comment|// assertEquals("\"foo bar\"~99",
comment|// qp.getSuperFieldQuery("","foo bar").toString());
comment|// assertEquals("\"(multi multi2) bar\"~99",
comment|// qp.getSuperFieldQuery("","multi bar").toString());
comment|//
comment|//
comment|// // ask sublcass to parse phrase with modified default slop
comment|// assertEquals("\"(multi multi2) foo\"~99 bar",
comment|// qp.parse("\"multi foo\" bar").toString());
comment|//
comment|// }
DECL|method|testPosIncrementAnalyzer
specifier|public
name|void
name|testPosIncrementAnalyzer
parameter_list|()
throws|throws
name|QueryNodeException
block|{
name|StandardQueryParser
name|qp
init|=
operator|new
name|StandardQueryParser
argument_list|()
decl_stmt|;
name|qp
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|PosIncrementAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"quick brown"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"the quick brown"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"? quick brown\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"the quick brown\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"quick brown fox"
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"the quick brown fox"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"? quick brown fox\""
argument_list|,
name|qp
operator|.
name|parse
argument_list|(
literal|"\"the quick brown fox\""
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expands "multi" to "multi" and "multi2", both at the same position, and    * expands "triplemulti" to "triplemulti", "multi3", and "multi2".    */
DECL|class|MultiAnalyzer
specifier|private
class|class
name|MultiAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|TestFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TestFilter
specifier|private
specifier|final
class|class
name|TestFilter
extends|extends
name|TokenFilter
block|{
DECL|field|prevType
specifier|private
name|String
name|prevType
decl_stmt|;
DECL|field|prevStartOffset
specifier|private
name|int
name|prevStartOffset
decl_stmt|;
DECL|field|prevEndOffset
specifier|private
name|int
name|prevEndOffset
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
DECL|method|TestFilter
specifier|public
name|TestFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
if|if
condition|(
name|multiToken
operator|>
literal|0
condition|)
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"multi"
operator|+
operator|(
name|multiToken
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|prevStartOffset
argument_list|,
name|prevEndOffset
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|prevType
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|multiToken
operator|--
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|boolean
name|next
init|=
name|input
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|next
condition|)
block|{
return|return
literal|false
return|;
block|}
name|prevType
operator|=
name|typeAtt
operator|.
name|type
argument_list|()
expr_stmt|;
name|prevStartOffset
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|prevEndOffset
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|String
name|text
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
literal|"triplemulti"
argument_list|)
condition|)
block|{
name|multiToken
operator|=
literal|2
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|equals
argument_list|(
literal|"multi"
argument_list|)
condition|)
block|{
name|multiToken
operator|=
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
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
name|this
operator|.
name|prevType
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|prevStartOffset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|prevEndOffset
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/**    * Analyzes "the quick brown" as: quick(incr=2) brown(incr=1). Does not work    * correctly for input other than "the quick brown ...".    */
DECL|class|PosIncrementAnalyzer
specifier|private
class|class
name|PosIncrementAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|TestPosIncrementFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TestPosIncrementFilter
specifier|private
class|class
name|TestPosIncrementFilter
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
DECL|method|TestPosIncrementFilter
specifier|public
name|TestPosIncrementFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"the"
argument_list|)
condition|)
block|{
comment|// stopword, do nothing
block|}
elseif|else
if|if
condition|(
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"quick"
argument_list|)
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|2
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

